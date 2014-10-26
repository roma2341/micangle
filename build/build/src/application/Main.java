package application;
	
import java.io.IOException;
import java.util.BitSet;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {

	FXMLLoader loader;
	@Override
	public void start(Stage primaryStage) {
		try {
			initRootLayout();
			MyGuiController controller = (MyGuiController)loader.getController();
			
			controller.getButton_process().setOnAction(new EventHandler<ActionEvent>(){

				@Override
				public void handle(ActionEvent event) {
		
					controller.getTextarea_input().setText("");	
				//	double SM1=A[0][0]*Math.sin(W*t+2*dP)+A[1][0]*Math.sin(W*t+dP)+A[0][1]*Math.sin(W*t+dP*Math.sqrt(5))+A[1][1]*Math.sin(W*t+Math.sqrt(2));
					//controller.getTextarea_input().appendText("SM1:"+SM1 + " t:"+t+"\n");
					//System.out.println("SM1:"+SM1 + " t:"+t);
					SignalsManager sm = new SignalsManager();
					
				}
				
			});
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void initRootLayout() {
        try {
            // Load root layout from FXML file.
        	loader = new FXMLLoader(getClass().getResource("VisualConfig.fxml"));
        	AnchorPane rootLayout = (AnchorPane) loader.load();
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            Stage primaryStage = new Stage();
           primaryStage.setScene(scene);
           scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	//Works
	
	
	
	public static void main(String[] args) {
		launch(args);
	}
}
