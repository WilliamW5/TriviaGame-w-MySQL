import java.awt.Checkbox;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GenericGame {

	final String[] bgQuestions = new String[5];
	final String[] vgQuestions = new String[4];
	final String[] mmQuestions = new String[3];
	ArrayList<String> completedQ = new ArrayList<>();
	private TriviaGame main;
	private Participants user;
	
	int questionCount;
	
	String country; // board games
	int releaseDate; //movies and video games									// may have to change to string
	String title; // board game, video games, and movies
	String actor; // movies
	String publisher; // video games board games
	String genre; // video games
	String category; // board games
	int score = 0;
	
	int questionNum = 1;
	String question = null;
	String correctAnswer = null;
	String userAnswer = null;
	String[] incorrectAnswers = new String[3];
	ArrayList<String> answerOptions = new ArrayList<String>();
	
	TextField tf= new TextField();
	
	public GenericGame(Participants user) {
		// passed user class to save high score
		this.user = user;
	}
	// fix pane
	public void display(int gameType) {
		VBox vbox = new VBox();
		Stage popUpWindow = new Stage();
		popUpWindow.initModality(Modality.APPLICATION_MODAL);
		popUpWindow.setTitle("Questions!");
		answerOptions.clear();
		// resets our arrays, allows us to keep our question format
		bgQuestions[0] = "What game originated in "; // country to title
		bgQuestions[1] = "What category does the following game belong to: "; // game to category
		bgQuestions[2] = "What is the name of the game published by "; // publisher to title
		bgQuestions[3] = "In what country is the following game created: "; // title to country
		bgQuestions[4] = "In what country does the following publisher reside in: "; // country publisher
		
		vgQuestions[0] = "In what year was the following game released: "; // title to release date
		vgQuestions[1] = "What genre does the following game belong to: "; // title to genre
		vgQuestions[2] = "Who is the publisher of the following game: "; // title to publisher 
		vgQuestions[3] = "What is the name of the game, the following publisher created: "; // publisher to title
		
		mmQuestions[0] = "In what year did the following movie release:  "; // title to release date
		mmQuestions[1] = "Which movie did the following actor star in: "; // actor to title
		mmQuestions[2] = "Which lead actor starred in the following film: "; // title to actor
		country = null; // board games
		releaseDate = 0; //movies and video games
		title = null; // board game, video games, and movies
		actor = null; // movies
		publisher = null; // video games board games
		genre = null; // video games
		category = null; // board games
		tf.setEditable(false);
		// creates a game type for the associated category. These are consistent through-out the project
		try {
			if (gameType == 1) {
				//marvel movies
				populateMMQuestions(random(5));
				
			}else if (gameType == 2) {
				//board games
				populateBGQuestions(random(4));
				
			}else {
				//video games
				populateVGQuestions(random(3));
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
		// adds our incorrect/correct answers to an array list, and shuffles them
		answerOptions.add(incorrectAnswers[0]);
		answerOptions.add(incorrectAnswers[1]);
		answerOptions.add(incorrectAnswers[2]);
		answerOptions.add(correctAnswer);
		Collections.shuffle(answerOptions);
		
		Label q = new Label(question);
		Label aL = new Label("A. " + answerOptions.get(0));
		Label bL = new Label("B. " + answerOptions.get(1));
		Label cL = new Label("C. " + answerOptions.get(2));
		Label dL = new Label("D. " + answerOptions.get(3));
		Button a = new Button("A");
		Label qNum = new Label("Question number: " + Integer.toString(questionNum));
		a.setOnAction(e->{
			userAnswer = answerOptions.get(0);
		});
		Button b = new Button("B");
		b.setOnAction(e->{
			userAnswer = answerOptions.get(1);
		});
		Button c = new Button("C");
		c.setOnAction(e->{
			userAnswer = answerOptions.get(2);
		});
		Button d = new Button("D");
		d.setOnAction(e->{
			userAnswer = answerOptions.get(3);
		});
		
		//verify if the user's answer is correct
		Button submit = new Button("Submit");
		submit.setOnAction(e -> {
			if (userAnswer == correctAnswer) {
				score += 10;
				questionNum++;
				answerPrompt(true, popUpWindow, gameType);
			} else {
				questionNum++;
				answerPrompt(false, popUpWindow, gameType);
			}
		});
		
		vbox.getChildren().addAll(qNum, q, aL, bL, cL, dL, a, b, c, d, submit);
		Scene scene = new Scene(vbox, 600, 400);
		
		popUpWindow.setScene(scene);
		popUpWindow.showAndWait();
	}
	
	// prompt that appears when answering a question correct or not. Also adds the question number to the variable, or resets it if =10
	public void answerPrompt(Boolean answer, Stage popUpWindow, int gameType) {
		VBox vbox = new VBox();
		Stage tempPopUpWindow = new Stage();
		String prompt = null;
		
		Label displayScore = new Label("Score: " + score);
		
		if (questionNum <= 10) {
			if (answer == true) {
				prompt = "Correct!";
			} else {
				prompt = "Incorrect. Correct answer is " + correctAnswer;
			}
		} else {
			if (answer == true) {
				user.saveScore(score);
				prompt = "Correct! this was the final question.";
				score = 0;
				questionNum = 0;
			} else {
				user.saveScore(score);
				prompt = "Incorrect. Correct answer is " + correctAnswer + " This was the final question";
				score = 0;
				questionNum = 0;
			}
		}
		Label displayPrompt = new Label(prompt);
		
		Button nextQuestion = new Button("Next");
		nextQuestion.setOnAction(e-> {
			popUpWindow.close();
			display(gameType);
			tempPopUpWindow.close();
		});
		
		vbox.getChildren().addAll(displayPrompt, displayScore, nextQuestion);
		Scene tempScene = new Scene(vbox, 500, 200);
		tempPopUpWindow.setScene(tempScene);
		tempPopUpWindow.showAndWait();
	}
	// simple random function, used to randomize the questions
	public int random(int num) {
		int roll = 0;
		
		roll = (int)(Math.random()*num);
		System.out.println(roll);
		return roll;
	}
	
	
	// uses the rand() function to obtain a random number. That number is tied to an array of questions.
	// grabs the array associated with the random number, then populates the question based on the criteria needed to satisfy that question
	public void populateBGQuestions(int num) throws Exception {
		Connection con = getConnection();
		
		if (num == 0) {
			// country to title
			// selects a random country of origin
			PreparedStatement st = con.prepareStatement("SELECT countryOfOrigin FROM boardgames ORDER BY RAND() LIMIT 1");
			ResultSet rs = st.executeQuery();
			rs.next();
			country = rs.getString("countryOfOrigin");
			question = bgQuestions[0] + country;
			
			//selects the title to the answer
			st = con.prepareStatement("SELECT titlebg FROM boardgames WHERE countryOfOrigin = \"" + country + "\"");
			rs = st.executeQuery();
			rs.next();
			correctAnswer = rs.getString("titlebg");

			// creates 2 temp string to prevent repeats
			String temp1 = null;
			String temp2 = null;
			for ( int i = 0; i <= incorrectAnswers.length; i++) {
				// uses those temps and != statement to select 3 incorrect answer
				if ( i == 0) {
					st = con.prepareStatement("SELECT titlebg FROM boardgames WHERE countryOfOrigin != \"" + country + "\" "
							+ "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("titlebg");
					temp1 = incorrectAnswers[i];
				}else if( i == 1) {
					st = con.prepareStatement("SELECT titlebg FROM boardgames WHERE countryOfOrigin != \"" + country + "\" "
							+ "AND titlebg != \"" + temp1 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("titlebg");
					temp2 = incorrectAnswers[i];
				}else {
					st = con.prepareStatement("SELECT titlebg FROM boardgames WHERE countryOfOrigin != \"" + country + "\" "
							+ "AND titlebg != \"" + temp1 + "\"" + "AND titlebg != \"" + temp2 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("titlebg");
				}
			}
		}	
		else if (num == 1) {
			// title to category
			PreparedStatement st = con.prepareStatement("SELECT titlebg FROM boardgames ORDER BY RAND() LIMIT 1");
			ResultSet rs = st.executeQuery();
			rs.next();
			title = rs.getString("titlebg");
			question = bgQuestions[1] + title;
			
			//selects the title to the answer
			st = con.prepareStatement("SELECT category FROM boardgames WHERE titlebg = \"" + title + "\"");
			rs = st.executeQuery();
			rs.next();
			correctAnswer = rs.getString("category");

			// creates 2 temp string to prevent repeats
			String temp1 = null;
			String temp2 = null;
			for ( int i = 0; i <= incorrectAnswers.length; i++) {
				// uses those temps and != statement to select 3 incorrect answer
				if ( i == 0) {
					st = con.prepareStatement("SELECT category FROM boardgames WHERE titlebg != \"" + title + "\" "
							+ "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("category");
					temp1 = incorrectAnswers[i];
				}else if( i == 1) {
					st = con.prepareStatement("SELECT category FROM boardgames WHERE titlebg != \"" + title + "\" "
							+ "AND category != \"" + temp1 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("category");
					temp2 = incorrectAnswers[i];
				}else {
					st = con.prepareStatement("SELECT category FROM boardgames WHERE titlebg != \"" + title + "\" "
							+ "AND category != \"" + temp1 + "\"" + "AND category != \"" + temp2 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("category");
				}
			}
		}
		else if (num == 2) {
			// publisher to title
			PreparedStatement st = con.prepareStatement("SELECT publisherbg FROM boardgames ORDER BY RAND() LIMIT 1");
			ResultSet rs = st.executeQuery();
			rs.next();
			publisher = rs.getString("publisherbg");
			question = bgQuestions[2] + publisher;
			
			//selects the title to the answer
			st = con.prepareStatement("SELECT titlebg FROM boardgames WHERE publisherbg = \"" + publisher + "\"");
			rs = st.executeQuery();
			rs.next();
			correctAnswer = rs.getString("titlebg");

			// creates 2 temp string to prevent repeats
			String temp1 = null;
			String temp2 = null;
			for ( int i = 0; i <= incorrectAnswers.length; i++) {
				// uses those temps and != statement to select 3 incorrect answer
				if ( i == 0) {
					st = con.prepareStatement("SELECT titlebg FROM boardgames WHERE publisherbg != \"" + publisher + "\" "
							+ "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("titlebg");
					temp1 = incorrectAnswers[i];
				}else if( i == 1) {
					st = con.prepareStatement("SELECT titlebg FROM boardgames WHERE publisherbg != \"" + publisher + "\" "
							+ "AND titlebg != \"" + temp1 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("titlebg");
					temp2 = incorrectAnswers[i];
				}else {
					st = con.prepareStatement("SELECT titlebg FROM boardgames WHERE publisherbg != \"" + publisher + "\" "
							+ "AND titlebg != \"" + temp1 + "\"" + "AND titlebg != \"" + temp2 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("titlebg");
				}
			}
		}
		else if (num == 3) {
			// title to country
			PreparedStatement st = con.prepareStatement("SELECT titlebg FROM boardgames ORDER BY RAND() LIMIT 1");
			ResultSet rs = st.executeQuery();
			rs.next();
			title = rs.getString("titlebg");
			question = bgQuestions[3] + title;
			
			//selects the title to the answer
			st = con.prepareStatement("SELECT countryOfOrigin FROM boardgames WHERE titlebg = \"" + title + "\"");
			rs = st.executeQuery();
			rs.next();
			correctAnswer = rs.getString("countryOfOrigin");

			// creates 2 temp string to prevent repeats
			String temp1 = null;
			String temp2 = null;
			for ( int i = 0; i <= incorrectAnswers.length; i++) {
				// uses those temps and != statement to select 3 incorrect answer
				if ( i == 0) {
					st = con.prepareStatement("SELECT countryOfOrigin FROM boardgames WHERE titlebg != \"" + title + "\" "
							+ "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("countryOfOrigin");
					temp1 = incorrectAnswers[i];
				}else if( i == 1) {
					st = con.prepareStatement("SELECT countryOfOrigin FROM boardgames WHERE titlebg != \"" + title + "\" "
							+ "AND countryOfOrigin != \"" + temp1 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("countryOfOrigin");
					temp2 = incorrectAnswers[i];
				}else {
					st = con.prepareStatement("SELECT countryOfOrigin FROM boardgames WHERE titlebg != \"" + title + "\" "
							+ "AND countryOfOrigin != \"" + temp1 + "\"" + "AND countryOfOrigin != \"" + temp2 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("countryOfOrigin");
				}
			}
		}
		else {
			// country to publisher
			PreparedStatement st = con.prepareStatement("SELECT countryOfOrigin FROM boardgames ORDER BY RAND() LIMIT 1");
			ResultSet rs = st.executeQuery();
			rs.next();
			country = rs.getString("countryOfOrigin");
			question = bgQuestions[4] + country;
			
			//selects the title to the answer
			st = con.prepareStatement("SELECT publisherbg FROM boardgames WHERE countryOfOrigin = \"" + country + "\"");
			rs = st.executeQuery();
			rs.next();
			correctAnswer = rs.getString("publisherbg");

			// creates 2 temp string to prevent repeats
			String temp1 = null;
			String temp2 = null;
			for ( int i = 0; i <= incorrectAnswers.length; i++) {
				// uses those temps and != statement to select 3 incorrect answer
				if ( i == 0) {
					st = con.prepareStatement("SELECT publisherbg FROM boardgames WHERE countryOfOrigin != \"" + country + "\" "
							+ "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("publisherbg");
					temp1 = incorrectAnswers[i];
				}else if( i == 1) {
					st = con.prepareStatement("SELECT publisherbg FROM boardgames WHERE countryOfOrigin != \"" + country + "\" "
							+ "AND publisherbg != \"" + temp1 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("publisherbg");
					temp2 = incorrectAnswers[i];
				}else {
					st = con.prepareStatement("SELECT publisherbg FROM boardgames WHERE countryOfOrigin != \"" + country + "\" "
							+ "AND publisherbg != \"" + temp1 + "\"" + "AND publisherbg != \"" + temp2 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("publisherbg");
				}
			}
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

	public void populateVGQuestions(int num) throws Exception {
		Connection con = getConnection();
		if (num == 0) {
			// title to release date
			PreparedStatement st = con.prepareStatement("SELECT titlevg FROM videogames ORDER BY RAND() LIMIT 1");
			ResultSet rs = st.executeQuery();
			rs.next();
			title = rs.getString("titlevg");
			question = vgQuestions[0] + title; // title to release date
			
			st = con.prepareStatement("SELECT releasedatevg FROM videogames WHERE titlevg = \"" + title + "\"");
			rs = st.executeQuery();
			rs.next();
			correctAnswer = String.valueOf(rs.getInt("releasedatevg"));

			// creates 2 temp string to prevent repeats
			int temp1 = 0;
			int temp2 = 0;
			for ( int i = 0; i <= incorrectAnswers.length; i++) {
				// uses those temps and != statement to select 3 incorrect answer
				if ( i == 0) {
					st = con.prepareStatement("SELECT releasedatevg FROM videogames WHERE titlevg != \"" + title + "\" "
							+ "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = String.valueOf(rs.getInt("releasedatevg"));
					temp1 = Integer.parseInt(incorrectAnswers[i]);
				}else if( i == 1) {
					st = con.prepareStatement("SELECT releasedatevg FROM videogames WHERE titlevg != \"" + title + "\" "
							+ "AND releasedatevg != \"" + temp1 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = String.valueOf(rs.getInt("releasedatevg"));
					temp2 = Integer.parseInt(incorrectAnswers[i]);
				}else {
					st = con.prepareStatement("SELECT releasedatevg FROM videogames WHERE titlevg != \"" + title + "\" "
							+ "AND releasedatevg != \"" + temp1 + "\"" + "AND releasedatevg != \"" + temp2 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = String.valueOf(rs.getInt("releasedatevg"));
				}
			}
		}
		
		else if (num == 1) {
			// title to genre
			PreparedStatement st = con.prepareStatement("SELECT titlevg FROM videogames ORDER BY RAND() LIMIT 1");
			ResultSet rs = st.executeQuery();
			rs.next();
			title = rs.getString("titlevg");
			question = vgQuestions[1] + title; // title to genre
			
			st = con.prepareStatement("SELECT genrevg FROM videogames WHERE titlevg = \"" + title + "\"");
			rs = st.executeQuery();
			rs.next();
			correctAnswer = rs.getString("genrevg");
			
			// creates 2 temp string to prevent repeats
			String temp1 = null;
			String temp2 = null;
			for ( int i = 0; i <= incorrectAnswers.length; i++) {
				// uses those temps and != statement to select 3 incorrect answer
				if ( i == 0) {
					st = con.prepareStatement("SELECT genrevg FROM videogames WHERE titlevg != \"" + title + "\" "
							+ "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("genrevg");
					temp1 = incorrectAnswers[i];
				}else if( i == 1) {
					st = con.prepareStatement("SELECT genrevg FROM videogames WHERE titlevg != \"" + title + "\" "
							+ "AND genrevg != \"" + temp1 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("genrevg");
					temp2 = incorrectAnswers[i];
				}else {
					st = con.prepareStatement("SELECT genrevg FROM videogames WHERE titlevg != \"" + title + "\" "
							+ "AND genrevg != \"" + temp1 + "\"" + "AND genrevg != \"" + temp2 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("genrevg");
				}
			}
		}
		
		else if (num == 2) {
			// title to publisher 
			PreparedStatement st = con.prepareStatement("SELECT titlevg FROM videogames ORDER BY RAND() LIMIT 1");
			ResultSet rs = st.executeQuery();
			rs.next();
			title = rs.getString("titlevg");
			question = vgQuestions[2] + title; // title to publisher 
			
			st = con.prepareStatement("SELECT publishervg FROM videogames WHERE titlevg = \"" + title + "\"");
			rs = st.executeQuery();
			rs.next();
			correctAnswer = rs.getString("publishervg");
			
			// creates 2 temp string to prevent repeats
			String temp1 = null;
			String temp2 = null;
			for ( int i = 0; i <= incorrectAnswers.length; i++) {
				// uses those temps and != statement to select 3 incorrect answer
				if ( i == 0) {
					st = con.prepareStatement("SELECT publishervg FROM videogames WHERE titlevg != \"" + title + "\" "
							+ "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("publishervg");
					temp1 = incorrectAnswers[i];
				}else if( i == 1) {
					st = con.prepareStatement("SELECT publishervg FROM videogames WHERE titlevg != \"" + title + "\" "
							+ "AND publishervg != \"" + temp1 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("publishervg");
					temp2 = incorrectAnswers[i];
				}else {
					st = con.prepareStatement("SELECT publishervg FROM videogames WHERE titlevg != \"" + title + "\" "
							+ "AND publishervg != \"" + temp1 + "\"" + "AND publishervg != \"" + temp2 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("publishervg");
				}
			}
		}
		
		else {
			// publisher to title
			PreparedStatement st = con.prepareStatement("SELECT publishervg FROM videogames ORDER BY RAND() LIMIT 1");
			ResultSet rs = st.executeQuery();
			rs.next();
			publisher = rs.getString("publishervg");
			question = vgQuestions[3] + publisher; // publisher to title
			
			st = con.prepareStatement("SELECT titlevg FROM videogames WHERE publishervg = \"" + publisher + "\"");
			rs = st.executeQuery();
			rs.next();
			correctAnswer = rs.getString("titlevg");
			
			// creates 2 temp string to prevent repeats
			String temp1 = null;
			String temp2 = null;
			for ( int i = 0; i <= incorrectAnswers.length; i++) {
				// uses those temps and != statement to select 3 incorrect answer
				if ( i == 0) {
					st = con.prepareStatement("SELECT titlevg FROM videogames WHERE publishervg != \"" + publisher + "\" "
							+ "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("titlevg");
					temp1 = incorrectAnswers[i];
				}else if( i == 1) {
					st = con.prepareStatement("SELECT titlevg FROM videogames WHERE publishervg != \"" + publisher + "\" "
							+ "AND titlevg != \"" + temp1 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("titlevg");
					temp2 = incorrectAnswers[i];
				}else {
					st = con.prepareStatement("SELECT titlevg FROM videogames WHERE publishervg != \"" + publisher + "\" "
							+ "AND titlevg != \"" + temp1 + "\"" + "AND titlevg != \"" + temp2 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("titlevg");
				}
			}
		}
	}
	
	public void populateMMQuestions(int num) throws Exception {
		Connection con = getConnection();
		if (num == 0) {
			// title to release date
			PreparedStatement st = con.prepareStatement("SELECT titlem FROM marvelmovies ORDER BY RAND() LIMIT 1");
			ResultSet rs = st.executeQuery();
			rs.next();
			title = rs.getString("titlem");		
			question = mmQuestions[0] + title; // title to release date
			
			st = con.prepareStatement("SELECT releasedatem FROM marvelmovies WHERE titlem = \"" + title + "\"");
			rs = st.executeQuery();
			rs.next();
			correctAnswer = String.valueOf(rs.getInt("releasedatem"));
			
			// creates 2 temp string to prevent repeats
			int temp1 = 0;
			int temp2 = 0;
			for ( int i = 0; i <= incorrectAnswers.length; i++) {
				// uses those temps and != statement to select 3 incorrect answer
				if ( i == 0) {
					st = con.prepareStatement("SELECT releasedatem FROM marvelmovies WHERE titlem != \"" + title + "\" "
							+ "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = String.valueOf(rs.getInt("releasedatem"));
					temp1 = Integer.parseInt(incorrectAnswers[i]);
				}else if( i == 1) {
					st = con.prepareStatement("SELECT releasedatem FROM marvelmovies WHERE titlem != \"" + title + "\" "
							+ "AND releasedatem != \"" + temp1 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = String.valueOf(rs.getInt("releasedatem"));
					temp2 = Integer.parseInt(incorrectAnswers[i]);
				}else {
					st = con.prepareStatement("SELECT releasedatem FROM marvelmovies WHERE titlem != \"" + title + "\" "
							+ "AND releasedatem != \"" + temp1 + "\"" + "AND releasedatem != \"" + temp2 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = String.valueOf(rs.getInt("releasedatem"));
				}
			}
		}
		else if (num == 1) {
			// actor to title
			PreparedStatement st = con.prepareStatement("SELECT leadactor FROM marvelmovies ORDER BY RAND() LIMIT 1");
			ResultSet rs = st.executeQuery();
			rs.next();
			actor = rs.getString("leadactor");		
			question = mmQuestions[1] + actor; // actor to title
			
			st = con.prepareStatement("SELECT titlem FROM marvelmovies WHERE leadactor = \"" + actor + "\"");
			rs = st.executeQuery();
			rs.next();
			correctAnswer = rs.getString("titlem");
			
			// creates 2 temp string to prevent repeats
			String temp1 = null;
			String temp2 = null;
			for ( int i = 0; i <= incorrectAnswers.length; i++) {
				// uses those temps and != statement to select 3 incorrect answer
				if ( i == 0) {
					st = con.prepareStatement("SELECT titlem FROM marvelmovies WHERE leadactor != \"" + actor + "\" "
							+ "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("titlem");
					temp1 = incorrectAnswers[i];
				}else if( i == 1) {
					st = con.prepareStatement("SELECT titlem FROM marvelmovies WHERE leadactor != \"" + actor + "\" "
							+ "AND titlem != \"" + temp1 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("titlem");
					temp2 = incorrectAnswers[i];
				}else {
					st = con.prepareStatement("SELECT titlem FROM marvelmovies WHERE leadactor != \"" + actor + "\" "
							+ "AND titlem != \"" + temp1 + "\"" + "AND titlem != \"" + temp2 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("titlem");
				}
			}
		}
		else if (num == 2) {
			// title to actor
			PreparedStatement st = con.prepareStatement("SELECT titlem FROM marvelmovies ORDER BY RAND() LIMIT 1");
			ResultSet rs = st.executeQuery();
			rs.next();
			title = rs.getString("titlem");		
			question = mmQuestions[2] + title; // title to actor
			
			st = con.prepareStatement("SELECT leadactor FROM marvelmovies WHERE titlem = \"" + title + "\"");
			rs = st.executeQuery();
			rs.next();
			correctAnswer = rs.getString("leadactor");
			
			// creates 2 temp string to prevent repeats
			String temp1 = null;
			String temp2 = null;
			for ( int i = 0; i <= incorrectAnswers.length; i++) {
				// uses those temps and != statement to select 3 incorrect answer
				if ( i == 0) {
					st = con.prepareStatement("SELECT leadactor FROM marvelmovies WHERE titlem != \"" + title + "\" "
							+ "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("leadactor");
					temp1 = incorrectAnswers[i];
				}else if( i == 1) {
					st = con.prepareStatement("SELECT leadactor FROM marvelmovies WHERE titlem != \"" + title + "\" "
							+ "AND leadactor != \"" + temp1 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("leadactor");
					temp2 = incorrectAnswers[i];
				}else {
					st = con.prepareStatement("SELECT leadactor FROM marvelmovies WHERE titlem != \"" + title + "\" "
							+ "AND leadactor != \"" + temp1 + "\"" + "AND leadactor != \"" + temp2 + "\"" + "ORDER BY RAND() LIMIT 1");
					rs = st.executeQuery();
					rs.next();
					incorrectAnswers[i] = rs.getString("leadactor");
				}
			}
		}	

	}
	
	/*
	 * got this to works
	 * 		Connection con = getConnection();
		PreparedStatement st = con.prepareStatement("SELECT titlevg FROM boardgames WHERE releaseDateVG =\"" + "2008" + "\"");
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			System.out.print(rs.getString("titlevg"));
		}
		System.out.println();
		*/
}
