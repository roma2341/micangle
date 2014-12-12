package application;
	
import java.beans.PropertyChangeListenerProxy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;

import com.sun.javafx.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
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
	int currentViewArea = 0;
	double TIME_X = 1;
	double[][] SMn;
	int[] delays;
	FXMLLoader loader;
	boolean isCorelation=false;
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
	int dataInputCounter=0;
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
				 controller.getSlidersVBox().getChildren().clear();
				String inputText = controller.getTextarea_input().getText();
				// Введенна похибка
				String str = controller.getTextfield_errorValue().getText();
				String curViewAreaStr=controller.getTextfield_viewArea().getText();
				if (!curViewAreaStr.isEmpty())currentViewArea = Integer.parseInt(curViewAreaStr);
				double inputedError=0;
				//currentViewArea=0;
				if (!str.isEmpty())
				inputedError = Double.parseDouble(str);
				//
				sm = new SignalsManager(inputedError);			
				sm.addSoundEmiterFromString(inputText);
				sm.addMicrophoneFromString(inputText);
				sm.processTimings();
			//	SignalsManager.checkOneLinePosition(sm.Mn);
				dataInputCounter++;
				fillScatterChartData();
				System.out.println("inputedError:"+inputedError+" currentViewArea:"+currentViewArea);
			}
			
		});
			controller.getButton_process().setOnAction(new EventHandler<ActionEvent>(){

				@Override
				public void handle(ActionEvent event) {
					processSMDiagram();
					updateSMDiagram();
				}
			});
			controller.getButton_showMaximums().setOnAction(new EventHandler<ActionEvent>(){

				@Override
				public void handle(ActionEvent event) {
					processSMDiagramMaxes();
				}
			});
	
		
			controller.getButton_updateChart().setOnAction(new EventHandler<ActionEvent>(){

				@Override
				public void handle(ActionEvent event) {
					checkSettings();
					//SignalsManager.rotateMicPane(sm.Mn, 10);
					fillScatterChartData();
				}
			});
			
			/*controller.getButton_rotate().setOnAction(new EventHandler<ActionEvent>(){

				@Override
				public void handle(ActionEvent event) {
					String angleString = controller.getTextfield_rotationAngle().getText();
					Integer angle = 0;
					if (!angleString.isEmpty())angle=Integer.parseInt(angleString);
					sm.rotateMicPane(sm.Mn, angle);
					fillScatterChartData();
				}
			});*/
	
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
			 sliderX.setValue(emiter.x);
	         sliderX.valueProperty().addListener(new ChangeListener<Number>() {
	            @Override
	            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
	                //changes the x position of the circle
	                emiter.x=sliderX.getValue();
	                sm.processTimings();
	                processSMDiagram();
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
			 sliderY.setValue(emiter.y);
	         sliderY.valueProperty().addListener(new ChangeListener<Number>() {
	            @Override
	            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
	                //changes the x position of the circle
	                emiter.y=sliderY.getValue();
	                sm.processTimings();
	                processSMDiagram();
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
	final NumberAxis xAxis = new NumberAxis(-5,5, 1);
    final NumberAxis yAxis = new NumberAxis(-5,5,1);        
   //xAxis.setAutoRanging(true);
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
	    double minCoord=100000;
	    double maxCoord=-100000;
	    for (Microphone microphone : sm.Mn)
	    {
	    	if (microphone.x>maxCoord) maxCoord=microphone.x;
	    	else if (microphone.x<minCoord)minCoord=microphone.x;
	    	if (microphone.y>maxCoord) maxCoord=microphone.y;
	    	else if (microphone.y<minCoord)minCoord=microphone.y;
	    series1.getData().add(new XYChart.Data(microphone.x, microphone.y));
	    }
	    XYChart.Series series2 = new XYChart.Series();
	    series2.setName("Джерела звуку");
	    for (SoundEmiter emiter : sm.Sn)
	    {
	    	if (emiter.x>maxCoord) maxCoord=emiter.x;
	    	else if (emiter.x<minCoord)minCoord=emiter.x;
	    	if (emiter.y>maxCoord) maxCoord=emiter.y;
	    	else if (emiter.y<minCoord)minCoord=emiter.y;
	    series2.getData().add(new XYChart.Data(emiter.x, emiter.y));
	    }
	    NumberAxis x=(NumberAxis) sc.getXAxis();
	    NumberAxis y=(NumberAxis) sc.getYAxis();
	    //upper 
	    x.setUpperBound(maxCoord);
	    y.setUpperBound(maxCoord);
	    //down
	    x.setLowerBound(minCoord);
	    y.setLowerBound(minCoord);
	    //sm.processCenter();//Центр платформи з мікрофонами
		//
	    XYChart.Series series3 = new XYChart.Series();
	    series3.setName("Центр мікрофонів");
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
		if(controller.getCheckbox_isCorelation().isSelected())isCorelation=true;
		else isCorelation=false;
		
		if(controller.getTextarea_delays().getText().length()==0){
			delays=null;
			return;
		}
		String[] items = controller.getTextarea_delays().getText().split(",");
		if (items.length==0) return;
		delays = new int[items.length];
		
		for (int i = 0; i < items.length; i++) {
		    try {
		        delays[i] = Integer.parseInt(items[i]);
		    } catch (NumberFormatException nfe) {};
		}
		
		
	
	}
	public void WORKING_processSMDiagram_Deprecated(){
		final int TICK_COUNT = SAMPLING_RATE;
		 checkSettings();
			XYChart.Series<Number,Number> serie= new XYChart.Series<Number, Number>();
			controller.getSignalsChart().getData().clear();
			//serie.setName(String.valueOf(k));
			double value = 0;
			double t=0;
			
			double[] SM = new double[TICK_COUNT];
			int i=0;
			
			while (t<=TIME_X)
			{
					value = sm.getSm(t);
					 serie.getData().add(new XYChart.Data(t*1000, value));
			t+=TIME_X/TICK_COUNT;
			System.out.println("i:"+i++);
			}
			
			 controller.getSignalsChart().getData().add(serie);
		}
	public void processSMDiagram(){
			checkSettings();
			double value = 0;
			int i=0;
			SMn = new double[SAMPLING_RATE][sm.Mn.size()];
			double step = TIME_X/SAMPLING_RATE;
			double t=step*currentViewArea*SAMPLING_RATE/VIEW_AREAS_COUNT;
			for (int l=0;l<SAMPLING_RATE;l++)
			{
				SMn[i++]=sm.getSMn(t);
				t+= step;
			}
			/*for (int l=currentViewArea*SAMPLING_RATE/VIEW_AREAS_COUNT;l<(currentViewArea+1)*SAMPLING_RATE/VIEW_AREAS_COUNT;l++)
			{
				SMn[i++]=sm.getSMn(t);
				t+= step;
			}*/
			SMn=SignalsManager.shift(SMn,delays);
		}
	public void updateSMDiagram()
	{
		if (SMn==null)return;
		controller.getSignalsChart().getData().clear();
		XYChart.Series<Number,Number> serie= new XYChart.Series<Number, Number>();
		double[] SM = SignalsManager.toSM(SMn,isCorelation);
		double step = TIME_X/SAMPLING_RATE;
		ArrayList<XYChart.Data<Number,Number>> data= new ArrayList<XYChart.Data<Number,Number>>(SAMPLING_RATE);
		double areaBegin = TIME_X/VIEW_AREAS_COUNT*currentViewArea;
		for (int l =currentViewArea*SAMPLING_RATE/VIEW_AREAS_COUNT; l<(currentViewArea+1)*SAMPLING_RATE/VIEW_AREAS_COUNT;l++){
			data.add(new XYChart.Data<Number,Number>(areaBegin+step*l,SM[l]));
		 //l+=10; //Прискорюєм обчислення, бо біда з продуктивністю:(
		}
		 serie.getData().addAll(data);
		 controller.getSignalsChart().getData().add(serie);
		 controller.getSignalsChartXAxis().setLowerBound(areaBegin);
		 controller.getSignalsChartXAxis().setUpperBound(areaBegin+1/TIME_X/VIEW_AREAS_COUNT);
	}
	
	
	public void processSMDiagramMaxes(){
		if (sm.Sn.size()<1 || sm.Mn.size()<1)return;
		checkSettings();
		XYChart.Series<Number,Number> serie =new XYChart.Series<Number,Number>();
			//sm.SMn = new double[sm.Mn.size()];
			controller.getSignalsChart().getData().clear();
			//series.get(k).setName(String.valueOf(k));
			double value = 0;
			double t=0;
			
			double step = TIME_X/SAMPLING_RATE;
			System.out.println("step:"+step);
			double[][] SMn = new double[SAMPLING_RATE+1][sm.Mn.size()];
			int min=-50;
			int max=50;
			//int delta=max-min;
			//progressbar
			//1000 - 100(delta)
			//i - x
			//x=delta*i/1000
			for (int i=min;i<=max;i++)
			{
				//double progress = delta * i / 1000;
				sm.Sn.get(0).x=i;
				t=0;
				int j=0;
				sm.processTimings();
			while (t<TIME_X)
			{	
				//sm.Sn.get(0).y+=1;
					SMn[j++] = sm.getSMn(t);
					 t+=step;

			}

			SMn=SignalsManager.shift(SMn,delays);
			double[] SM = sm.toSM(SMn,isCorelation);
			double SMsum = 0;
			double maxSM=0;
			
			for (int l =0; l< SM.length;l++)
			{
				 SMsum+=SM[l];
			if (SM[l]>maxSM)maxSM=SM[l];
			}
			if (isCorelation)
			 serie.getData().add(new XYChart.Data(i, SMsum));
			else serie.getData().add(new XYChart.Data(i, maxSM));
		System.out.println("i:"+i);
			 SMsum=0;
			}
			
			 controller.getSignalsChart().getData().add(serie);
			
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}
