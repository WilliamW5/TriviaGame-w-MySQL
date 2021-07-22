import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainMenu{
	
	
	private Participants user;
	private GenericGame game;
	ArrayList<String> loginInfo = new ArrayList<>();
	
	Pane pane = new Pane();
	
	public MainMenu() {
		user = new Participants();
	}
    
	// starts the game with the associated category
    public void createButtons(Pane menuPane) {
    	user.login();
    	game = new GenericGame(user);
    	adminOption(user.adminStatus());
    	MenuButton movieButton = new MenuButton("Marvel Movies");
    	MenuButton boardButton = new MenuButton("Board Games");
    	MenuButton videoButton = new MenuButton("Video Games");
    	MenuButton hsButton = new MenuButton("Highscores!");
    	MenuButton exitButton = new MenuButton("Exit Game");
    	
    	// Opens Marvel Movies
    	movieButton.relocate(125, 250);
    	movieButton.setOnAction(e ->{
    		// MarvelMovie game type = 1
    		game.display(1);
    		System.out.println(" MarvelGame ");
    		//game.startGame(1);
    	});
    	
    	// Opens Board Game
    	boardButton.relocate(500, 250);
    	boardButton.setOnMouseClicked(e -> {
    		// BoardGame game type = 2
    		game.display(2);
    		System.out.println(" BoardGame ");
    		//game.startGame(2);
    	});
    	
    	// Opens Video Game
    	videoButton.relocate(300, 350);
    	videoButton.setOnMouseClicked(e -> {
    		// VideoGame game type = 3
    		game.display(3);
    		System.out.println(" VideoGame ");
    		//game.startVideoGame();
    	});
    	
    	// opens our highscore scene
    	hsButton.relocate(125, 500);
    	hsButton.setOnMouseClicked(e -> {
    		user.display();
    	});
    	
    	// closes the stage
    	exitButton.relocate(500, 500);
    	exitButton.setOnMouseClicked(e -> {
    		Platform.exit();
    	});
    	
    	pane.getChildren().addAll(movieButton, boardButton, videoButton, hsButton, exitButton);
    	menuPane.getChildren().add(pane);
    }
    
    // allows the use of the "admin" button for only admins
    private void adminOption(boolean adminStatus) {
		Button admin = new Button("Admin");
		admin.setDisable(!adminStatus);
		admin.relocate(730, 0);
		admin.setOnAction(e->{
			setupAdminPane();
			
		});
		pane.getChildren().add(admin);
    }
	 
    // opens a new window which contains a functional way to update the table
    private void setupAdminPane() {
    	Pane pane = new Pane();
		Stage popUpWindow = new Stage();
		popUpWindow.initModality(Modality.APPLICATION_MODAL);
		popUpWindow.setTitle("Admin");
		
		Label bgL = new Label("title,countryOfOrigin,category,publisher");
		Label mmL = new Label("title,releaseDate,actor");
		Label vgL = new Label("title,releaseDate,genre,publisher");
		//select box
		Button mm = new Button("MarvelMovies");
		mm.relocate(25, 50);
		mm.setOnAction(e->{
			insertMethod(mmL, 1);
			popUpWindow.close();
		});
		Button vg = new Button("Video Games");
		vg.relocate(175, 50);
		vg.setOnAction(e->{
			insertMethod(vgL, 3);
			popUpWindow.close();
		});
		Button bg = new Button("Board Games");
		bg.relocate(325, 50);
		bg.setOnAction(e->{
			insertMethod(bgL, 2);
			popUpWindow.close();
		});
		
		Button close = new Button("Close");
		close.relocate(100, 250);
		close.setOnAction(e->{
			popUpWindow.close();
		});
		
		pane.getChildren().addAll(mm, vg, bg, close);
		Scene tempScene = new Scene(pane, 500, 300);
		popUpWindow.setScene(tempScene);
		popUpWindow.showAndWait();
    }
    // once the database is selected from the above code, we now open this to submit the additions to the database.
    public void insertMethod(Label format, int gameType) {
    	VBox vbox = new VBox();
		Stage popUpWindow = new Stage();
		popUpWindow.initModality(Modality.APPLICATION_MODAL);
		popUpWindow.setTitle("Please follow below format, no spaces");
		
		TextField tf = new TextField();
		Button insert = new Button("Insert");
		insert.setOnAction(e->{
			insertFunctionality(tf.getText(), gameType);
			popUpWindow.close();
		});
		
		vbox.getChildren().addAll(format, tf, insert);
		Scene tempScene = new Scene(vbox, 400, 300);
		popUpWindow.setScene(tempScene);
		popUpWindow.showAndWait();
		
    }
    // inserts into the associated table
    public void insertFunctionality(String temp, int gameType) {
    	try {
			Connection con = getConnection();
			//marvel movies
			if (gameType == 1) {
				String[] format = new String[3];
				format = temp.split(",");
				PreparedStatement st = con.prepareStatement("INSERT INTO `triviagame`.`marvelmovies` (`titleM`, `releaseDateM`, `leadActor`) "
						+ "VALUES (?, ?, ?)");
				st.setString(1, format[0]);
				st.setInt(2, Integer.valueOf(format[1]));
				st.setString(3, format[2]);
				st.executeUpdate();
				
				// board games
			}else if (gameType == 2) {
				String[] format = new String[4];
				format = temp.split(",");
				PreparedStatement st = con.prepareStatement("INSERT INTO `triviagame`.`boardgames` (`titleBG`, `countryOfOrigin`, `category`, `publisherBG`) "
						+ "VALUES (?, ?, ?, ?)");
				st.setString(1, format[0]);
				st.setString(2, format[1]);
				st.setString(3, format[2]);
				st.setString(4, format[3]);
				st.executeUpdate();
				
				// video games
			}else {
				String[] format = new String[4];
				format = temp.split(",");
				PreparedStatement st = con.prepareStatement("INSERT INTO `triviagame`.`videogames` (`titleVG`, `releaseDateVG`, `genreVG`, `publisherVG`) "
						+ "VALUES (?, ?, ?, ?)");
				st.setString(1, format[0]);
				st.setInt(2, Integer.valueOf(format[1]));
				st.setString(3, format[2]);
				st.setString(4, format[3]);
				st.executeUpdate();
								
			}
			
			
		} catch (Exception e) {
			System.out.println(e);
		}
    }
   public static Connection getConnection() throws Exception{
    	
    	try {
    		String driver = "com.mysql.cj.jdbc.Driver";
    		String url = "jdbc:mysql://127.0.0.1:3306/TriviaGame";
    		String username = "admin";
    		String password = "admin1!";
    		Class.forName(driver);
    		
    		Connection conn = DriverManager.getConnection(url, username, password);
    		System.out.println("Connected!");
    		
    		return conn;
    		
    	}catch(Exception e) {
    		System.out.println(e);
    	}
    	
    	return null;
    	
    }
    
    // sets our background
    public void createBackground(Pane menuPane) {
    	Image menuBackground = new Image("/resources/background.png",800, 600 , false, true);
    	BackgroundImage background = new BackgroundImage(menuBackground, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
    	
    	menuPane.setBackground(new Background(background));
    }    
    
    
    // creates a unique button for this class to use
    class MenuButton extends Button{
    	// paths for our images
    	private final String BUTTON_PATH = "-fx-background-color: transparent; -fx-background-image: url('/resources/button.png');";
    	private final String BUTTON_PRESSED_PATH = "-fx-background-color: transparent; -fx-background-image: url('/resources/button_pressed.png')";
    	
    	// creates our class constructor (simple menu buttons)
    	public MenuButton(String text) {
    		setText(text);
    		setPrefWidth(190);
    		setPrefHeight(50);
    		setStyle(BUTTON_PATH);
    		buttonListener();
    	}
    	
    	// changes our image based on being pressed
    	private void setButtonPressedImage() {
    		setStyle(BUTTON_PRESSED_PATH);
    		setPrefHeight(45);
    		setLayoutY(getLayoutY() + 5);
    	}
    	
    	// sets our image back to our default
    	private void setButtonReleasedImage() {
    		setStyle(BUTTON_PATH);
    		setPrefHeight(50);
    		setLayoutY(getLayoutY() - 5);
    	}
    	
    	// button listener which grabs setImage methods
    	private void buttonListener() {
    		setOnMousePressed(e -> {
    			if(e.getButton().equals(MouseButton.PRIMARY)) {
    				setButtonPressedImage();
    			}
    		});
    		
    		setOnMouseReleased(e -> {
    			if(e.getButton().equals(MouseButton.PRIMARY)) {
    				setButtonReleasedImage();
    			}
    		});
    		
    	}
    }
}
