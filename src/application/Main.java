package application;
	
import java.beans.PropertyChangeListenerProxy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
	
	//final int SAMPLING_RATE = 48000;
	double TIME_X = 20.83; //1 секунда
	FXMLLoader loader;
	boolean isCorelation=false;
	MyGuiController controller;
	int[] delays;
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
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	int dataInputCounter=0;
	
	public static void runAndWait(Runnable action) {
	    if (action == null)
	        throw new NullPointerException("action");
	 
	    // run synchronously on JavaFX thread
	    if (Platform.isFxApplicationThread()) {
	        action.run();
	        return;
	    }
	 
	    // queue on JavaFX thread and wait for completion
	    final CountDownLatch doneLatch = new CountDownLatch(1);
	    Platform.runLater(() -> {
	        try {
	            action.run();
	        } finally {
	            doneLatch.countDown();
	        }
	    });
	 
	    try {
	        doneLatch.await();
	    } catch (InterruptedException e) {
	        // ignore exception
	    }
	}
	
	public void initButtons()
	{
		
		controller.getButton_interCorelation().setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				controller.getTextarea_debug().setText("");
				sm.Sn.get(0).setSignal(sm.Sn.get(0).processEmiterArr(TIME_X,sm.Sn.get(0).samplingRate,1000));
				int[][] SMn = sm.Sn.get(0).getSMnArr(sm.Mn);
				showCorelationDependencyOfN(SMn);
			}
			
		});
		
		controller.getButton_dataInput().setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				 controller.getSlidersVBox().getChildren().clear();
				String inputText = controller.getTextarea_input().getText();
				// Введенна похибка
				String str = controller.getTextfield_errorValue().getText();
				double inputedError=0;
				//currentViewArea=0;
				if (!str.isEmpty())
				inputedError = Double.parseDouble(str);
				sm = new SignalsManager(inputedError);			
				sm.addSoundEmiterFromString(inputText);
				sm.addMicrophoneFromString(inputText);
				dataInputCounter++;
				//Тест
				/*sm.Sn.get(0).setSignal(sm.Sn.get(0).processEmiterArr(TIME_X,SAMPLING_RATE,sm.Sn.get(0).F));
				int[] SMn = sm.Sn.get(0).signal;
				double t=TIME_X/SAMPLING_RATE;
				controller.getSignalsChart().getData().clear();
				XYChart.Series<Number, Number> serie= new XYChart.Series<Number, Number>();
					for (int j=0;j<SMn.length/500;j++)
					serie.getData().add(new XYChart.Data<Number,Number>(t*j,SMn[j]));
					controller.getSignalsChart().getData().add(serie);
				checkSettings();*/
				//Кінець тесту
				
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
			 sliderX.setValue(emiter.x);
	         sliderX.valueProperty().addListener(new ChangeListener<Number>() {
	            @Override
	            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
	                //changes the x position of the circle
	                emiter.x=(int) sliderX.getValue();
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
	                emiter.y=(int) sliderY.getValue();
	              //  processSMDiagram();
	               // updateSMDiagram();
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
	class CorelationAngle{
		volatile private Double angle=0.0;
		public void setAngle(Double newAngle){
			angle=newAngle;
		}
		public Double getAngle(){
			return angle;
		}
	}

	
	volatile long[][] shiftIndexesWithMaxSMvalue = null;

	 volatile int currentScreen=0;
	CorelationAngle corelationAngle = new CorelationAngle();
	public void showCorelationDependencyOfN(int[][] SMn)
	{
		checkSettings();
		//Графіка
		Runnable showingRunnable = new Runnable(){
			@Override
		    public void run(){
			controller.getSignalsChart().getData().clear();
			ArrayList<XYChart.Series<Number,Number>> series= new ArrayList<XYChart.Series<Number, Number>>(shiftIndexesWithMaxSMvalue.length);
			for (int i=0;i<shiftIndexesWithMaxSMvalue.length;i++)
			{
				series.add(new XYChart.Series<Number,Number>());
				for (int j=0;j<shiftIndexesWithMaxSMvalue[0].length;j++)
				series.get(i).getData().add(new XYChart.Data<Number,Number>(j,shiftIndexesWithMaxSMvalue[i][j]));
				controller.getSignalsChart().getData().add(series.get(i));
			}
			controller.getText_debugInfo().setText("Екран №:"+currentScreen);
			controller.getTextarea_debug().appendText("Екран:"+currentScreen+" Кут:"+corelationAngle.getAngle()+"\n");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//draw
			}};
			//Логіка
		new Thread(new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				System.out.println("Logic:"+SMn.length);
				for (int i=0;i<SMn.length/sm.BUFFER_CAPACITY;i++)
				{
				  if(i>0)sm.fillPreviousBuffer(SMn,(i-1)*sm.BUFFER_CAPACITY,sm.BUFFER_CAPACITY);
				  sm.fillBuffer(SMn,i*sm.BUFFER_CAPACITY,sm.BUFFER_CAPACITY);		 
				  shiftIndexesWithMaxSMvalue = sm.interCorelationFunc(sm.buffer,corelationAngle);
				  currentScreen=i;
				  runAndWait(showingRunnable);
				}
				  return null;
			}
		}).start();
		//marker1////////////////////////////////////////////////////////////////////
				
//////////////////////////////////
	}

	public static void main(String[] args) {
		launch(args);
	}
}
