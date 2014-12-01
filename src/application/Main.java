package application;
	
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;


public class Main extends Application {
	
	final int SAMPLING_RATE = 48000;
	final int VIEW_AREAS_COUNT = 12;
	double TIME_X = 1;
	double[][] SMn;
	FXMLLoader loader;
	MyGuiController controller;
	SignalsManager sm;
	ScatterChart<Number,Number> sc;
	@Override
	public void start(Stage primaryStage) {
		try {
			initRootLayout();
			
		controller = (MyGuiController)loader.getController();
		//Введена похибка
		String str = controller.getTextfield_errorValue().getText();
		double inputedError=0; 
		if (!str.isEmpty())
		inputedError = Double.parseDouble(str);
		//
		sm = new SignalsManager(inputedError);
	        //Заповнення серії інформацією
	       // series.getData().add(new XYChart.Data("2", 14)); 
		initButtons();
		initScatterChart();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void initButtons()
	{
		
		controller.getButton_addSoundEmiterControls().setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				addSoundEmiterControls(sm.Sn,50);
			}
			
		});
		controller.getButton_dataInput().setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				String inputText = controller.getTextarea_input().getText();
				// Введенна похибка
				String str = controller.getTextfield_errorValue().getText();
				double inputedError=0;
				if (!str.isEmpty())
				inputedError = Double.parseDouble(str);
				//
				sm = new SignalsManager(inputedError);			
				sm.addSoundEmiterFromString(inputText);
				sm.addMicrophoneFromString(inputText);
				sm.processTimings();
				SignalsManager.checkOneLinePosition(sm.Mn);
				
				fillScatterChartData();
			}
			
		});
			controller.getButton_process().setOnAction(new EventHandler<ActionEvent>(){

				@Override
				public void handle(ActionEvent event) {
					//processSMDiagram();
					double SMnArr[][]=
						{
							{1,2,3},
							{21,2,1},
							{2,2,1},
							{21,2,1},
							{2,2,1}
							
						};
					//processSMDiagram(SMnArr, 5);
					processSMDiagramData();
				}
			});
			controller.getButton_showMaximums().setOnAction(new EventHandler<ActionEvent>(){

				@Override
				public void handle(ActionEvent event) {
					processSMDiagramMaxes();
				}
			});
			controller.getButton_delay().setOnAction(new EventHandler<ActionEvent>(){

				@Override
				public void handle(ActionEvent event) {
					String angleString = controller.getTextfield_rotationAngle().getText();
					Integer angle = 0;
					if (!angleString.isEmpty())angle=Integer.parseInt(angleString);
					//sm.processDelay(angle);
					//Зчитування затримок
					if(controller.getTextarea_output().getText().length()==0)return;
					String[] items = controller.getTextarea_output().getText().split(",");
					if (items.length==0) return;
					int[] delays = new int[items.length];
					
					for (int i = 0; i < items.length; i++) {
					    try {
					        delays[i] = Integer.parseInt(items[i]);
					    } catch (NumberFormatException nfe) {};
					}
					
					SMn=SignalsManager.shift(SMn,delays);
				}
			});
			controller.getButton_dataToChart().setOnAction(new EventHandler<ActionEvent>(){

				@Override
				public void handle(ActionEvent event) {
					updateSMDiagram();
				}
			});
			controller.getButton_updateChart().setOnAction(new EventHandler<ActionEvent>(){

				@Override
				public void handle(ActionEvent event) {
					checkSettings();
					//SignalsManager.rotateMicPane(sm.Mn, 10);
					fillScatterChartData();
					///ТЕСТ ЗСУВУ
					
					///
					//sm.processDelay(45);
				}
			});
			
			controller.getButton_rotate().setOnAction(new EventHandler<ActionEvent>(){

				@Override
				public void handle(ActionEvent event) {
					String angleString = controller.getTextfield_rotationAngle().getText();
					Integer angle = 0;
					if (!angleString.isEmpty())angle=Integer.parseInt(angleString);
					sm.rotateMicPane(sm.Mn, angle);
					fillScatterChartData();
				}
			});
	
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
	
	public void addSoundEmiterControls(ArrayList<SoundEmiter> Sn,int range){
		
	
		 controller.getSlidersVBox().getChildren().clear();
		 for (SoundEmiter emiter : Sn)
		 {
			
			 //AddSliderX
			 Slider sliderX = new Slider();
			 sliderX.setMin(-range/2);
			 sliderX.setMax(range/2);
			 sliderX.setValue(0);
			 sliderX.setShowTickLabels(true);
			 sliderX.setShowTickMarks(true);
			 sliderX.setMajorTickUnit(range/10);
			 sliderX.setMinorTickCount(0);
			 sliderX.setBlockIncrement(1);
	         sliderX.valueProperty().addListener(new ChangeListener<Number>() {
	            @Override
	            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
	                //changes the x position of the circle
	                emiter.x=sliderX.getValue();
	                sm.processTimings();
	                processSMDiagramData();
	                updateSMDiagram();
	            }
	        });
	         
	         Slider sliderY = new Slider();
			 sliderY.setMin(-range/2);
			 sliderY.setMax(range/2);
			 sliderY.setValue(0);
			 sliderY.setShowTickLabels(true);
			 sliderY.setShowTickMarks(true);
			 sliderY.setMajorTickUnit(range/10);
			 sliderY.setMinorTickCount(0);
			 sliderY.setBlockIncrement(1);
	         sliderY.valueProperty().addListener(new ChangeListener<Number>() {
	            @Override
	            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
	                //changes the x position of the circle
	                emiter.y=sliderY.getValue();
	                sm.processTimings();
	                processSMDiagramData();
	                updateSMDiagram();
	            }
	        });
	         
	         HBox hboxX = new HBox(0); // spacing = 8
	         HBox hboxY = new HBox(0); // spacing = 8
	         Label labelX = new Label("x:");
	         Label labelY = new Label("y:");
		     hboxX.getChildren().addAll(labelX, sliderX);
		     hboxX.setHgrow(sliderX, Priority.ALWAYS);
		     hboxY.getChildren().addAll(labelY, sliderY);
		     hboxY.setHgrow(sliderY, Priority.ALWAYS);
	        controller.getSlidersVBox().getChildren().add(hboxX);
	        controller.getSlidersVBox().getChildren().add(hboxY);
	   sliderX.valueProperty().addListener(new ChangeListener<Number>() {
	        @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue)
	        {
	        	labelX.setText("x:"+Math.round(newValue.intValue()) + "");
	        }
	        
	        });
	   
	   sliderY.valueProperty().addListener(new ChangeListener<Number>() {
	        @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue)
	        {
	        	labelY.setText("y:"+Math.round(newValue.intValue()) + "");
	        }
	        
	        });
	   
	
		 }
	}
	/**
	 * Ініціалізація карти розміщення мікрофонів та джерел звуку
	 */
	public void initScatterChart(){
	final NumberAxis xAxis = new NumberAxis(-20,20, 1);
    final NumberAxis yAxis = new NumberAxis(-20,20,1);        
  // xAxis.setAutoRanging(true);
   // yAxis.setAutoRanging(true);
    xAxis.setLabel("X");                
    yAxis.setLabel("Y");
    sc = new ScatterChart<Number,Number>(xAxis,yAxis);
    //sc.prefHeight(100);
   // sc.prefWidth(100);
    sc.setTitle("Карта");

    controller.getMapAnchorPane().getChildren().addAll(sc);
}
	/**
	 * Оновлення і вивід інформації про розміщення мікрофонів і джерел звуку
	 */
public void fillScatterChartData(){
	sc.getData().clear();
	 XYChart.Series series1 = new XYChart.Series();
	    series1.setName("Мікрофони");
	    for (Microphone microphone : sm.Mn)
	    series1.getData().add(new XYChart.Data(microphone.x, microphone.y));
	    XYChart.Series series2 = new XYChart.Series();
	    series2.setName("Джерела звуку");
	    for (SoundEmiter emiter : sm.Sn)
	    series2.getData().add(new XYChart.Data(emiter.x, emiter.y));
	    //
	    //sm.processCenter();//Центр платформи з мікрофонами
		//
	    XYChart.Series series3 = new XYChart.Series();
	    series3.setName("Центр");
	    series3.getData().add(new XYChart.Data(sm.getCenterOfMicPane()[0], sm.getCenterOfMicPane()[1]));
	    sc.getData().addAll(series1, series2,series3);
	  //  sc.autosize();
}

	public void checkSettings()
	{
		if(controller.getCheckbox_chartAutorangingX().isSelected())controller.getSignalsChartXAxis().setAutoRanging(true);
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
		if(controller.getCheckbox_chartAutorangingY().isSelected())controller.getSignalsChartYAxis().setAutoRanging(true);
		else {
			controller.getSignalsChartYAxis().setAutoRanging(false);
			String maxYStr = controller.getTextfield_maxChartY().getText(); 
			String minYStr = controller.getTextfield_minChartY().getText(); 
			if (!(maxYStr.length()<1 || minYStr.length()<1)) {
			double maxY = Double.parseDouble(maxYStr);
			double minY = Double.parseDouble(minYStr);
			controller.getSignalsChartYAxis().setUpperBound(maxY);
			controller.getSignalsChartYAxis().setLowerBound(minY);
			}
		}
	}
	public void WORKING_processSMDiagram_Deprecated(){
		final int TICK_COUNT = SAMPLING_RATE;
		 checkSettings();
			XYChart.Series<Number,Number> serie= new XYChart.Series<Number, Number>();
			//sm.SMn = new double[sm.Mn.size()];
			//Звук(X:-1;y:1.0;A:1.0)
		
			//int i=0;
			controller.getSignalsChart().getData().clear();
		
			//ДАти серіям імя
				//serie.setName(String.valueOf(k));
			double value = 0;
			double t=0;
			
			double[] SM = new double[TICK_COUNT];
			int i=0;
			
			while (t<=TIME_X)
			{
					value = sm.getSm(t);
					controller.getTextarea_output().appendText("SM:"+value+"\n");	
					 serie.getData().add(new XYChart.Data(t*1000, value));
			t+=TIME_X/TICK_COUNT;
			System.out.println("i:"+i++);
			}
			
			 controller.getSignalsChart().getData().add(serie);
		}
	public void processSMDiagramData(){
			checkSettings();
			
			//sm.SMn = new double[sm.Mn.size()];
			//Звук(X:-1;y:1.0;A:1.0)
			//int i=0;
			
			//ДАти серіям імя
				//serie.setName(String.valueOf(k));
			double value = 0;
			double t=0;
			
			int i=0;
			SMn = new double[SAMPLING_RATE+1][sm.Mn.size()];
			double step = TIME_X/SAMPLING_RATE;
			while (t<=TIME_X)
			{
				SMn[i]=sm.getSMn(t);
				t+= step;
				i++;
			}
			
		}
	public void updateSMDiagram()
	{
		controller.getSignalsChart().getData().clear();
		XYChart.Series<Number,Number> serie= new XYChart.Series<Number, Number>();
		double[] SM = SignalsManager.toSM(SMn);
		double step = TIME_X/SAMPLING_RATE;
		for (int l =0; l< SM.length;l++){
		 serie.getData().add(new XYChart.Data(step*l, SM[l]));
		 l+=10; //Прискорюєм обчислення, бо біда з продуктивністю:(
		}
		 controller.getSignalsChart().getData().add(serie);
	}
	
	
	public void processSMDiagramMaxes(){
			if(controller.getCheckbox_chartAutorangingX().isSelected())controller.getSignalsChartXAxis().setAutoRanging(true);
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
			if(controller.getCheckbox_chartAutorangingY().isSelected())controller.getSignalsChartYAxis().setAutoRanging(true);
			else {
				controller.getSignalsChartYAxis().setAutoRanging(false);
				String maxYStr = controller.getTextfield_maxChartY().getText(); 
				String minYStr = controller.getTextfield_minChartY().getText(); 
				if (!(maxYStr.length()<1 || minYStr.length()<1)) {
				double maxY = Double.parseDouble(maxYStr);
				double minY = Double.parseDouble(minYStr);
				controller.getSignalsChartYAxis().setUpperBound(maxY);
				controller.getSignalsChartYAxis().setLowerBound(minY);
				}
			}
		
		
		XYChart.Series<Number,Number> serie =new XYChart.Series<Number,Number>();
			//sm.SMn = new double[sm.Mn.size()];
			controller.getSignalsChart().getData().clear();
			//series.get(k).setName(String.valueOf(k));
			double value = 0;
			double t=0;
			
			double step = TIME_X/SAMPLING_RATE;
			System.out.println("step:"+step);
			for (int i=-50;i<=50;i++)
			{
				sm.Sn.get(0).x=i;
				t=0;
				double[][] SMn = new double[SAMPLING_RATE+1][sm.Mn.size()];
				int j=0;
				
			while (t<TIME_X)
			{	
				//sm.Sn.get(0).y+=1;
				sm.processTimings();
					SMn[j++] = sm.getSMn(t);
					 t+=step;

			}
			double[] SM = sm.toSM(SMn);
			double maxSM = 0;
			for (int l =0; l< SM.length;l++)
				if (SM[l]>maxSM)maxSM=SM[l];
				
			 serie.getData().add(new XYChart.Data(i, maxSM));
		System.out.println("i:"+i);
			 maxSM=0;
				
			}
			
			 controller.getSignalsChart().getData().add(serie);
		}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}
