package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class MyGuiController implements Initializable{
	@FXML	
	private		MenuBar menuBar;
	@FXML	
	private		Menu menuFile ;
	@FXML	
	private		MenuItem process ;
	@FXML	
	private		MenuItem open ;
	@FXML	
	private		MenuItem undo ;
	@FXML		
	private Button button_process;
	public Button getButton_process() {
		return button_process;
	}
	@FXML
	private LineChart<Number,Number> signalsChart;
	public LineChart getSignalsChart() {
		return signalsChart;
	}
	@FXML		
	private TextArea textarea_input;
	@FXML		
	private TextArea textarea_output;
	@FXML
	private AnchorPane rootLayout;
	

	public MenuBar getMenuBar() {
		return menuBar;
	}
	public Menu getMenuFile() {
		return menuFile;
	}
	public MenuItem getProcess() {
		return process;
	}
	public MenuItem getOpen() {
		return open;
	}
	public MenuItem getUndo() {
		return undo;
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
	public TextArea getTextarea_output() {
		return textarea_output;
	}
	public void setTextarea_output(TextArea textarea_output) {
		this.textarea_output = textarea_output;
	}
}
