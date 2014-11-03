package application;
	
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	 final double TIME_X = 100;
	FXMLLoader loader;
	@Override
	public void start(Stage primaryStage) {
		try {
			initRootLayout();
			MyGuiController controller = (MyGuiController)loader.getController();
			
	        //populating the series with data
	       // series.getData().add(new XYChart.Data("2", 14));
	     
	        
			controller.getButton_process().setOnAction(new EventHandler<ActionEvent>(){

				@Override
				public void handle(ActionEvent event) {
				//	controller.getTextarea_input().setText("");	
					
					SignalsManager sm = new SignalsManager();
					String inputText = controller.getTextarea_input().getText();
					System.out.println("str:"+controller.getTextarea_input().getText());
					sm.addSoundEmiterFromString(inputText);
					sm.addMicrophoneFromString(inputText);
					System.out.println("series count:"+sm.Mn.size()*sm.Mn.size());
					Comber comb = new Comber(sm.Mn.size());
					BitSet[] coombs = comb.getCoombs();
					if(controller.getCheckbox_chartAutoranging().isSelected())controller.getSignalsChartXAxis().setAutoRanging(true);
					else {
						controller.getSignalsChartXAxis().setAutoRanging(false);
						String maxXStr = controller.getTextfield_maxChartX().getText(); 
						String minXStr = controller.getTextfield_minChartX().getText(); 
						if (!(maxXStr.length()<1 || minXStr.length()<1)) {
						double maxX = Double.parseDouble(maxXStr);
						double minX = Double.parseDouble(minXStr);
						controller.getSignalsChartXAxis().setUpperBound(maxX);
						controller.getSignalsChartXAxis().setLowerBound(minX);
						}
					}
					ArrayList<XYChart.Series<Number,Number>> series = new ArrayList<XYChart.Series<Number, Number>>(sm.Mn.size()*sm.Mn.size());
					for (int i=0;i<coombs.length;i++)
					{
						series.add(new XYChart.Series<Number,Number>());
					}
					sm.SMn = new double[sm.Mn.size()];
					//Звук(X:-1;y:1.0;A:1.0)
				
					int i=0;
					controller.getSignalsChart().getData().clear();
				
					//ДАти серіям імя
					for (int k=0;k<coombs.length;k++)
					{
						series.get(k).setName(comb.getSmStatusFromBitSet(coombs[k]));
					}
					for (int j=0;j<TIME_X;j++)
					{
					
						
						for (int k=0;k<coombs.length;k++)
						{
							for (int l=0;l<sm.Mn.size();l++)
							if(coombs[k].get(l))sm.Mn.get(l).delay=1;
							else sm.Mn.get(l).delay=0;
						
							double value = sm.getSm(j*60);
							controller.getTextarea_output().appendText("SM:"+value+"\n");	
							 series.get(i).getData().add(new XYChart.Data(j, value));
						       i++;	
						}
					i=0;
					}
					for (	Series<Number, Number> serie : series)
					 controller.getSignalsChart().getData().add(serie);
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
