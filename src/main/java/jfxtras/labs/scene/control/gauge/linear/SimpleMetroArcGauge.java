package jfxtras.labs.scene.control.gauge.linear;

import javafx.collections.ObservableList;
import javafx.scene.control.Skin;
import jfxtras.labs.internal.scene.control.skin.gauge.linear.SimpleMetroArcGaugeSkin;
import jfxtras.scene.control.ListSpinner;

/**
 * = SimpleMetroArcGauge
 * 
 * This gauge is a simple flat possibly colorful (Microsoft Metro) arc shaped gauge.
 * The needle ranges from about 7 o'clock (min) via 12 o'clock to 5 o'clock (max).
 * 
 * == CSS properties
 * The gauge supports the following CSS styleable properties:
 * 
 * - -fxx-animated: indicate if the gauge is animated with YES or NO.
 * - -fxx-value-format: a DecimalFormat pattern for rendering the label inside the needle
 * 
 * === Example 
 * [source,css]
 * --
 *    #myGauge {
 *        -fxx-animated: YES; 
 *        -fxx-value-format:' ##0.0W'; 
 *     }
 * --
 * 
 * == Segments
 * This gauge supports segments:
 * 
 * - Segment colors can be set using CSS classes like "segment0", "segment1", ... , the numeric part is the index of the segment in the segments list.
 * - Another option is to specify an segment ID, which can then be used to style the segment in CSS.
 * - The SimpleMetroArcGauge.css per default supports segment classes segment0 - segment9.
 * - The CSS also contains a number of color schemes, like "colorscheme-green-to-red-10" (for 10 segments) which can be activated by assigning the color scheme class to the gauge.
 * - If no segments are specified a single segment will automatically be drawn.
 * - If segments are specified, the user is fully responsible for convering the whole range (or not, gaps are allowed).
 *
 * === Example
 * [source,java]
 * --
 *     final SimpleMetroArcGauge lSimpleMetroArcGauge = new SimpleMetroArcGauge();
 *     lSimpleMetroArcGauge.getStyleClass().add("colorscheme-green-to-red-10");
 *     for (int i = 0; i < 10; i++) {
 *         Segment lSegment = new PercentSegment(lSimpleMetroArcGauge, i * 10.0, (i+1) * 10.0);
 *         lSimpleMetroArcGauge.segments().add(lSegment);
 *     }
 * --
 * 
 * == Indicators
 * This gauge has two indicators: warning and error, they can be made visible through CSS:
 * [source,css]
 * --
 *     -fxx-warning-indicator-visibility: visible; 
 *     -fxx-error-indicator-visibility: visible;
 * --
 *
 * It is possible to have these indicators become visible based on the needle's value, by means of the segments.
 * Suppose the needle is over segment1, then a CSS class named "segment1-active" is added to the node.
 * Using this CSS class an indicator can be made visible, for example:
 * [source,css]
 * --
 *    .segment1-active {
 *        -fxx-warning-indicator-visibility: visible; 
 *     }
 *    .segment2-active {
 *        -fxx-error-indicator-visibility: visible; 
 *     }
 * --
 * Segments may overlap and can be transparent, there even is a special "colorscheme-first-grey-rest-transparent-10" colorscheme.
 * Segments could be setup solely to show indicators, for example segment1 could run from 50% to 100% and segment2 from 75% to 100%.
 * If the needle is over segment2, both the warning and error indicator will be visible.
 *
 * === Example (using the CSS above)
 * [source,java]
 * --
 *     final SimpleMetroArcGauge lSimpleMetroArcGauge = new SimpleMetroArcGauge();
 *     lSimpleMetroArcGauge.getStyleClass().add("colorscheme-first-grey-rest-transparent-10");
 *     lSimpleMetroArcGauge.segments().add(new CompleteSegment(lSimpleMetroArcGauge));
 *     lSimpleMetroArcGauge.segments().add(new PercentSegment(lSimpleMetroArcGauge, 50.0, 100.0, "warningSegment"));
 *     lSimpleMetroArcGauge.segments().add(new PercentSegment(lSimpleMetroArcGauge, 75.0, 100.0, "errorSegment"));
 * --
 * 
 * Given that the segements have id's, you can also use active classes based on that: 
 * [source,css]
 * --
 *    .segment-warningSegment-active {
 *        -warning-indicator-visibility: visible; 
 *     }
 *    .segment-errorSegment-active {
 *        -error-indicator-visibility: visible; 
 *     }
 * --
 * 
 * This should not be confused with the CSS rules based on the id of the segments (in the example: #warningSegment and #errorSegment).
 *  
 * == Disclaimer
 * This is a blatant but approved visual copy of Gerrit Grunwald's Enzo SimpleGauge (https://bitbucket.org/hansolo/enzo/src).
 * Gerrit describes the Enzo library in his blog like this: _all controls in that library are made for my personal demos and are not production ready._
 * This control is supposed to be production ready. 
 */
public class SimpleMetroArcGauge extends LinearGauge<SimpleMetroArcGauge> {
	
	// ==================================================================================================================
	// AbstractLinearGauge

	/**
	 * Return the path to the CSS file so things are setup right
	 */
	@Override public String getUserAgentStylesheet() {
		return ListSpinner.class.getResource("/jfxtras/labs/internal/scene/control/gauge/linear/" + SimpleMetroArcGauge.class.getSimpleName() + ".css").toExternalForm();
	}

	@Override public Skin<?> createDefaultSkin() {
		return new SimpleMetroArcGaugeSkin(this); 
	}

	// ==================================================================================================================
	// PROPERTIES

	/** segments:
	 * If you do not provide any segments, a default segment will be drawn.
	 * But if you provide at least one segment, you are responsible for making sure the segments cover the range of the gauge. 
	 */
	public ObservableList<Segment> segments() { return segments; }
	final private ObservableList<Segment> segments =  javafx.collections.FXCollections.observableArrayList();
}
