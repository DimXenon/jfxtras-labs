package jfxtras.labs.scene.layout.test;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import jfxtras.labs.scene.layout.CircularPane;
import jfxtras.scene.layout.HBox;

public class CircularPaneTrail3 extends Application {

    public static void main(String[] args) {
        launch(args);       
    }

	@Override
	public void start(Stage stage) {

		VBox lVBox = new VBox();		
		HBox lHBox = new HBox(0);
		lVBox.getChildren().add(lHBox);

		for (int j = 1; j < 27; j++)
		{
			CircularPane lCircularPane = new CircularPane();
			lCircularPane.setStyle("-fx-border-color:black;");
			lCircularPane.setShowDebug(Color.GREEN);
			for (int i = 0; i < j; i++) {
				javafx.scene.shape.Rectangle c = new javafx.scene.shape.Rectangle(30,30);
				//c.setStroke(Color.RED);
				lCircularPane.add(c);
			}
			lHBox.getChildren().add(lCircularPane);
			if (lHBox.prefWidth(-1) > 1500) {
				lHBox = new HBox(0);
				lVBox.getChildren().add(lHBox);
			}
		}

        // setup scene
		Scene scene = new Scene(lVBox);
		scene.getStylesheets().add(this.getClass().getName().replace(".", "/") + ".css");
		
        // create stage
        stage.setTitle(this.getClass().getSimpleName());
        stage.setScene(scene);
        stage.show();	
	}
	
	

}

	