package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class MyGuiController implements Initializable{
	@FXML
	private AnchorPane mapAnchorPane;
	@FXML
	private NumberAxis signalsChartXAxis;
	@FXML
	private NumberAxis signalsChartYAxis;
	@FXML
	private Button button_updateChart;
	@FXML
	private Button button_interCorelation;
	@FXML
	Button button_dataInput;
	@FXML
	private TextField textfield_maxChartX;
	@FXML
	private TextField textfield_maxChartY;
	@FXML
	private CheckBox checkbox_chartAutorangingX;
	@FXML
	private CheckBox checkbox_chartAutorangingY;
	@FXML
	private VBox mainVBox;
	@FXML
	private LineChart<Number,Number> signalsChart;
	@FXML
	private VBox slidersVBox;
	@FXML		
	private TextArea textarea_input;
	@FXML		
	private TextArea textarea_delays;
	@FXML
	private TextField textfield_rotationAngle;
	@FXML
	private CheckBox checkbox_isCorelation;
	@FXML
	private AnchorPane rootLayout;
	@FXML
	private TextField textfield_minChartX;
	@FXML
	private TextField textfield_minChartY;
	@FXML
	private TextField textfield_errorValue;
	
	public Button getButton_interCorelation() {
		return button_interCorelation;
	}
	
	public AnchorPane getMapAnchorPane() {
		return mapAnchorPane;
	}	

	public Button getButton_updateChart() {
		return button_updateChart;
	}

	public NumberAxis getSignalsChartXAxis() {
		return signalsChartXAxis;
	}
	public NumberAxis getSignalsChartYAxis() {
		return signalsChartYAxis;
	}

	public Button getButton_dataInput() {
		return button_dataInput;
	}

	public LineChart getSignalsChart() {
		return signalsChart;
	}

	public VBox getMainVBox() {
		return mainVBox;
	}
	
	
	public VBox getSlidersVBox() {
		return slidersVBox;
	}
	
	public TextField getTextfield_rotationAngle() {
		return textfield_rotationAngle;
	}
	
	public CheckBox getCheckbox_isCorelation() {
		return checkbox_isCorelation;
	}
	
	public TextField getTextfield_errorValue() {
		return textfield_errorValue;
	}
	
	public TextField getTextfield_minChartY() {
		return textfield_minChartY;
	}
	public TextField getTextfield_maxChartY() {
		return textfield_maxChartY;
	}
	
	public TextField getTextfield_minChartX() {
		return textfield_minChartX;
	}
	public TextField getTextfield_maxChartX() {
		return textfield_maxChartX;
	}
	public CheckBox getCheckbox_chartAutorangingX() {
		return checkbox_chartAutorangingX;
	}
	public CheckBox getCheckbox_chartAutorangingY() {
		return checkbox_chartAutorangingY;
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	public TextArea getTextarea_input() {
		return textarea_input;
	}
	public void setTextarea_input(TextArea textarea_input) {
		this.textarea_input = textarea_input;
	}
	public TextArea getTextarea_delays() {
		return textarea_delays;
	}

}
