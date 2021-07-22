import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class TriviaGame extends Application{
	Pane mainPane = new Pane();
	Pane gamePane = new Pane();
	Scene mainScene = new Scene(mainPane,800, 600);
	private Stage stage = new Stage();
	
	// creates object of the MainMenu class as main
	private MainMenu main;

	public TriviaGame() {
		main = new MainMenu();
	}
	
	@Override
	public void start(Stage stage) {
		this.stage = stage;
	    setupMenuPane(mainPane);
	    
	    stage.setScene(mainScene);
	    stage.setTitle("TriviaGame!");
	    stage.show();
	    }
	
	// adds functionality to the Main menu
    private Pane setupMenuPane(Pane mainPane) {
		main.createButtons(mainPane);
		main.createBackground(mainPane);	
		return mainPane;
	}
    
    public void setupGamePane(Pane pane) {
    	mainScene = new Scene(pane, 800, 600);
    	stage.setScene(mainScene);
    }
    
	public static void main(String[] args){
		launch(args);
	}
	
}
