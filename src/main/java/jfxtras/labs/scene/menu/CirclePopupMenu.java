package jfxtras.labs.scene.menu;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import jfxtras.labs.scene.layout.CircularPane;
import jfxtras.labs.scene.layout.CircularPane.AnimationInterpolation;
import jfxtras.labs.scene.layout.CircularPane.AnimationLayoutInfo;
import jfxtras.labs.util.Implements;
import jfxtras.util.NodeUtil;

/**
 * CirclePopupMenu is a menu is intended to pop up at any place in a scene.
 * It will show the provided menu items in a circle with the origin at the point where the mouse button was clicked.
 * It is possible to, and per default will, animate the menu items in and out of view.
 * 
 * CirclePopupMenu requires a StackPane to attach itself to. 
 *  
 * CirclePopupMenu uses CircularPane and this will leak through in the API. 
 * For example: it is possible to customize the animation, and the required interface to implement is the one from CircularPane.
 * 
 * @author Tom Eugelink
 *
 */
public class CirclePopupMenu {
	
	// ==================================================================================================================
	// CONSTRUCTOR

	/**
	 * 
	 * @param stackPane the stack pane to render upon
	 * @param mouseButton the mouse button on which the popup is shown (null means the coder will take care of showing and hiding)
	 */
	public CirclePopupMenu(StackPane stackPane, MouseButton mouseButton)
	{
		construct();
		addToStackPane(stackPane, mouseButton);
	}

	/*
	 * 
	 */
	private void construct()
	{
    	// listen to items and modify circular pane's children accordingly
		getItems().addListener( (ListChangeListener.Change<? extends MenuItem> change) -> {
			while (change.next())
			{
				for (MenuItem lMenuItem : change.getRemoved())
				{
					for (javafx.scene.Node lNode : new ArrayList<javafx.scene.Node>(circularPane.getChildren())) {
						if (lNode instanceof CirclePopupMenuNode) {
							CirclePopupMenuNode lCirclePopupMenuNode = (CirclePopupMenuNode)lNode;
							if (lCirclePopupMenuNode.menuItem == lMenuItem) {
								circularPane.remove(lCirclePopupMenuNode);
							}
						}
					}
				}
				for (MenuItem lMenuItem : change.getAddedSubList()) 
				{
					circularPane.add( new CirclePopupMenuNode(lMenuItem) );
				}
			}
		});	
		
		// hide when the mouse move out of the menu
		circularPane.setOnMouseExited( mouseEvent -> {
			hide();
		});
		
		// default status
		circularPane.setVisible(false);
		setShown(false);
	}
	

	// ==================================================================================================================
	// PROPERTIES
	
	/** items */
    private final ObservableList<MenuItem> items = FXCollections.observableArrayList(); 
    public final ObservableList<MenuItem> getItems() {
        return items;
    }
    
	/** shown */
	public final ReadOnlyBooleanProperty shownProperty() { return shown.getReadOnlyProperty(); }
    private void setShown(boolean value) { shown.set(value); }
    public final boolean isShown() { return shownProperty().get(); }
    private ReadOnlyBooleanWrapper shown = new ReadOnlyBooleanWrapper(this, "shown");
	
    // ----------------------
    // CircularPane API
    
	/** animationDuration */
	public ObjectProperty<Duration> animationDurationProperty() { return animationDurationObjectProperty; }
	final private ObjectProperty<Duration> animationDurationObjectProperty = new SimpleObjectProperty<Duration>(this, "animationDuration", Duration.millis(500));
	public Duration getAnimationDuration() { return animationDurationObjectProperty.getValue(); }
	public void setAnimationDuration(Duration value) { animationDurationObjectProperty.setValue(value); }
	public CirclePopupMenu withAnimationDuration(Duration value) { setAnimationDuration(value); return this; } 

	/** animationInterpolation: calculate the position of a node during the animation (default: move from origin), use node.relocate to position node (or manually apply layoutBounds.minX/Y) */
	public ObjectProperty<AnimationInterpolation> animationInterpolationProperty() { return animationInterpolationObjectProperty; }
	final private ObjectProperty<AnimationInterpolation> animationInterpolationObjectProperty = new SimpleObjectProperty<AnimationInterpolation>(this, "animationInterpolation", CirclePopupMenu::animateFromTheOrigin);
	public AnimationInterpolation getAnimationInterpolation() { return animationInterpolationObjectProperty.getValue(); }
	public void setAnimationInterpolation(AnimationInterpolation value) { animationInterpolationObjectProperty.setValue(value); }
	public CirclePopupMenu withAnimationInterpolation(AnimationInterpolation value) { setAnimationInterpolation(value); return this; } 



	// ==================================================================================================================
	// ACTION
	
	/**
	 * 
	 * @param mouseEvent
	 */
    public void show(MouseEvent mouseEvent) {
    	show(mouseEvent.getScreenX() - NodeUtil.screenX(canvasPane), mouseEvent.getScreenY() - NodeUtil.screenY(canvasPane));
    }
    
    /**
     * 
     * @param x origin of the circle
     * @param y origin of the circle
     */
    public void show(double x, double y) {
    	circularPane.setLayoutX(x - (circularPane.getWidth() / 2));
    	circularPane.setLayoutY(y - (circularPane.getHeight() / 2));
		setShown(true);
		circularPane.setVisible(true);
		circularPane.animateIn();
	}
    
    public void hide() {
		setShown(false);
		circularPane.animateOut();
		// if no animation, call the event directly
		if (circularPane.getAnimationInterpolation() == null) {
			circularPane.getOnAnimateOutFinished().handle(null);
		}
    }

	// ==================================================================================================================
	// RENDERING
	
    final private CirclePopupMenuCanvas canvasPane = new CirclePopupMenuCanvas();
    final private CircularPane circularPane = new CircularPane();

    /**
     * 
     */
	public void removeFromStackPane() {
		stackPane.getChildren().remove(canvasPane);
	}
	
    /**
     * Install this CirclePopupMenu in a new the top pane
     */
    private void addToStackPane(StackPane stackPane, MouseButton mouseButton) {

    	// react to the right mouse button
    	// TODO: this should be done on the canvasPane, but pickOnBounds has to be true then
    	stackPane.setOnMouseClicked( (mouseEvent) -> {
    		if (mouseButton != null && mouseButton.equals(mouseEvent.getButton())) {
    			if (isShown()) { 
    				hide();
    			}
    			else {
    				show(mouseEvent);
    			}
    		}
    	});

    	// positon
    	setupCircularPane();
    	
    	// circularPane in pane
      	canvasPane.getChildren().add(circularPane);
    	
    	// pane in stackpane
    	this.stackPane = stackPane;
    	stackPane.getChildren().add(canvasPane);
    }
    private StackPane stackPane = null;
    
    /*
     * 
     */
    private void setupCircularPane() {
    	// bind it uup
    	circularPane.animationDurationProperty().bind(this.animationDurationObjectProperty);
    	circularPane.animationInterpolationProperty().bind(this.animationInterpolationObjectProperty);
		// circularPane.setShowDebug(javafx.scene.paint.Color.GREEN);
    	
		// setup the animation
		circularPane.setOnAnimateOutFinished( (actionEvent) -> {
			circularPane.setVisible(false);
		});
    }

    /*
     * This is the canvas for positioning the circularPane in the correct corner
     */
    private class CirclePopupMenuCanvas extends Pane {
    	{ // anonymous constructor
    		setPickOnBounds(false);
    	}
    
    }
    
	/* 
	 * This class renders a MenuItem in CircularPane
	 */
	private class CirclePopupMenuNode extends Pane {
		CirclePopupMenuNode (MenuItem menuItem) {
			this.menuItem = menuItem;
			setId(this.getClass().getSimpleName() + "#" + menuNodeIdAtomicLong.incrementAndGet());
			
			// show the graphical part
			if (menuItem.getGraphic() == null) {
				throw new NullPointerException("MenuItems in CirclePopupMenu require a graphical part, text is optional");
			}
			getChildren().add(menuItem.getGraphic());

			// show the text as a tooltip
			if (menuItem.getText() != null && menuItem.getText().length() > 0) {
				Tooltip t = new Tooltip(menuItem.getText());
				Tooltip.install(this, t);
			}
			
			// react on a mouse click to perform the menu action
			setOnMouseClicked( (eventHandler) -> {
				hide();
				if (menuItem.getOnAction() != null) {
					menuItem.getOnAction().handle(null);
				}
			});
		}
		final private MenuItem menuItem;
	}
	private final AtomicLong menuNodeIdAtomicLong = new AtomicLong();

	
	// ==================================================================================================================
	// ANIMATION
	
	/**
	 * 
	 * @param progress
	 * @param animationLayoutInfo
	 */
    @Implements(interfaces=CircularPane.AnimationInterpolation.class)
    static public void animateFromTheOrigin(double progress, AnimationLayoutInfo animationLayoutInfo) {
    	// do the calculation
    	CornerMenu.animateFromTheOrigin(progress, animationLayoutInfo);
    }
	
    /**
     * 
     * @param progress
     * @param animationLayoutInfo
     */
    @Implements(interfaces=CircularPane.AnimationInterpolation.class)
    static public void animateOverTheArc(double progress, AnimationLayoutInfo animationLayoutInfo) {
    	// do the calculation
    	CornerMenu.animateOverTheArc(progress, animationLayoutInfo);
    }
}