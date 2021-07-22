import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Participants {

	private String file = "src/highscore.txt";
	private TextArea ta = new TextArea();
	String name;
	String password;
	int admin;
	TextField tf = new TextField();
	
	public Participants() {
		
	}
	
	// displays highscores
	public void display() {
		Stage popUpWindow = new Stage();
		
		ta.clear();
		ta.setEditable(false);
		
		// creates a modal window which blocks events from any other application window
		popUpWindow.initModality(Modality.APPLICATION_MODAL);
		popUpWindow.setTitle("Highscores!");

		Button button = new Button("Close");
		
		button.setOnAction(e-> {
			popUpWindow.close();
		});
		
		VBox layout = new VBox();

		layout.getChildren().addAll(ta, button);
		
		Scene scene = new Scene(layout, 300, 600);
		populateHS(ta);
		
		popUpWindow.setScene(scene);
		popUpWindow.showAndWait();
	}
	
	// selects the high score and name from our participants
    private void populateHS(TextArea ta) {
		try {
			Connection con = getConnection();
			PreparedStatement st = con.prepareStatement("SELECT name,highScore FROM participants");
			ResultSet rs = st.executeQuery();
			
			ArrayList<String> data = new ArrayList<String>();
			
			while (rs.next()) {
				ta.appendText(rs.getString("name"));
				ta.appendText(" ");
				ta.appendText(Integer.toString(rs.getInt("highScore")));
				ta.appendText("\n");
			}
		}catch(Exception e){
			System.out.println(e);
		}
	}

	public Connection getConnection() throws Exception{
    	
    	try {
    		String driver = "com.mysql.cj.jdbc.Driver";
    		String url = "jdbc:mysql://127.0.0.1:3306/TriviaGame";
    		String username = "test";
    		String password = "mypass1!";
    		Class.forName(driver);
    		
    		Connection conn = DriverManager.getConnection(url, username, password);
    		System.out.println("Connected!");
    		
    		return conn;
    		
    	}catch(Exception e) {
    		System.out.println(e);
    	}
    	
    	return null;
    }
	
	// creates a login window, this can be skipped, but will allow no ability so save high score, or admin properties
	public void login() {
		Stage popUpWindow = new Stage();
		tf.setEditable(false);
		// creates a modal window which blocks events from any other application window
		popUpWindow.initModality(Modality.APPLICATION_MODAL);
		popUpWindow.setTitle("login");
		
		Label user = new Label("Enter a 10 letter name");
		TextField tfUser = new TextField("Enter name here");
		
		Label pass = new Label("Enter a 10 letter name");
		TextField tfPass = new TextField("Enter name here");
		
		Button display = new Button("View other highscores!");
		Button login = new Button("login");
		Button createLog = new Button("Create Account");
		
		Button close = new Button("Close");
		
		close.setOnAction(e-> {
			popUpWindow.close();
		});
		
		display.setOnAction(e-> {
			display();
		});
		
		
		login.setOnAction(e-> {
			if (tfUser.getLength() >= 20) {
				tfUser.setText("I said less than 20 letters...");
			}
			else {
			try {
				Connection con = getConnection();
				
				PreparedStatement st = con.prepareStatement("SELECT name,password,admin FROM participants WHERE name = ? AND password = ?" );
				st.setString(1, tfUser.getText());
				st.setString(2, tfPass.getText());
				
				ResultSet rs = st.executeQuery();
				//it will print true if a row exists for the given id and false if not. 
				if (rs.next()) {
					name = tfUser.getText();
					password = tfPass.getText();
					admin = rs.getInt("admin");
					System.out.println(name + password + admin);
					popUpWindow.close();
				} else {
					tf.setText("Login not found");
				}
				
			}catch (Exception s) {
				System.out.println(s);
				}
			}
		});
		
		createLog.setOnAction(e-> {
			if (tfUser.getLength() >= 20) {
				tfUser.setText("I said less than 20 letters...");
			}
			else {
				try {
					Connection con = getConnection();
					PreparedStatement st = con.prepareStatement("INSERT INTO `triviagame`.`participants` (`name`, `password`) "
							+ "VALUES ('" + tfUser.getText() + "', '" + tfPass.getText() + "');");
					st.executeUpdate();
					System.out.println(name + password);
					admin = 0;
					name = tfUser.getText();
					password = tfPass.getText();
					
					tf.setText("Account created, please Login");
			}catch (Exception s) {
				System.out.println(s);
			}
			// closes window and display all highscores
			}
		});
		
		VBox layout = new VBox();
		layout.getChildren().addAll(user, tfUser, pass, tfPass, login, createLog, close, tf);
		
		Scene scene = new Scene(layout, 300, 400);
		
		popUpWindow.setScene(scene);
		popUpWindow.showAndWait();	
	}
	
	public void saveScore(int score) {
		try {
			Connection con = getConnection();
			PreparedStatement st = con.prepareStatement("UPDATE `participants` SET `highScore` = '" + score + "' WHERE (`name` = '" + name + "') "
					+ "AND (`password` = '" + password + "');");
			st.executeUpdate();
			System.out.println(score + name + password);
			
			//UPDATE `triviagame`.`participants` SET `highScore` = '10' WHERE (`name` = 'Stansbury') and (`password` = '12345');
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
	
	public boolean adminStatus() {
		if (admin == 0) {
			System.out.println("0");
			return false;
		} else {
			System.out.println("1");
			return true;
		}
	}
}
