package application;
	
import java.awt.Paint;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.web.WebView;

/**
 * Space Escape - A Program that finds the 
 * optimal escape route for a space ship in a solar
 * system with surrounding planets.
 * @author Tanvir
 *
 */
public class Main extends Application {
	
	private static ResultSet rs;
	private Connection con ;
    private Statement st ;
    private ResultSetMetaData rsMetaData;
    private sqlUtil SQL;
    
    final String url = "jdbc:mysql://localhost:3306/planets";
    final String user = "root";
    final String password = "root";
    final String query = "SELECT * FROM planetInfo";
    
    private Stage popup;
	private GridPane gPop;
	private Scene popScene;
	private Button selectDir;
	public static String directory="";
	private DirectoryChooser dChoose;
	
    private TextArea display;
    private Button btn;
    private ScrollPane scrollPane;
    private GridPane mainPane;
    private Label introMessage;
    
    private GridPane simBasePane;
    private Button runSim;
    private Button genPlanets;
    private Slider spacePopSlide;
    private Label selectPop;
    private Button writeFindings;
    private Label writeSuccess;
    private long spacePop;
    
    private Pane simPane;
    private Scene simScene;
    private Stage simWindow;
    public Circle[] planetSprites;
    public Label[] planetStats;
    public Label lblSpeed;
    private SpaceShip ship;
    private Button startRoute;
    private Button moveShip;
    private Button toggleConnects;
    private Slider speedControl;
    private TextArea shipStats;
    public ArrayList<Line> lineSprites;
    public Route path;
    private int move;
    private boolean shortest;
    private boolean showLines;
    private double speed;
    private String routeString;
    private String endMessage;
   
    private GridPane calPane;
    private WebView webView;
    
    private GridPane apiPane;
    private WebView apiView;
    
    private GridPane videoPane;
    private Media video;
    private MediaPlayer videoPlayer;
    private MediaView videoView;
    private Tab conclude;
    private Button togglePlay;
    private boolean playing;
    

    /**
     * Initializes Popup Tab
     * @param primaryStage
     * @throws InterruptedException
     */
	public void initPopup(Stage primaryStage) throws InterruptedException {
		Label secondLabel = new Label("Please select the Directory of the 'Space Escape' folder you extracted");
		dChoose = new DirectoryChooser();
		selectDir = new Button("Select");
		gPop = new GridPane();
		gPop.setHgap(10);
		gPop.setVgap(10);
		gPop.add(secondLabel, 1, 0);
		gPop.add(selectDir, 1, 1);
		popScene = new Scene(gPop,500,100);
		popup = new Stage();
		popup.setTitle("Select Program Folder");
		popup.setScene(popScene);
		popup.initModality(Modality.WINDOW_MODAL);
		popup.initOwner(primaryStage);
		popup.show();
	}
    /**
     * Initializes Main Tab
     */
	public void mainSetup() {
		btn = new Button();
        btn.setText("Display data from MySQL DB");
        display = new TextArea();
        introMessage = new Label("Welcome to Space Escape!\n"
        		+ "This is a path simulator. Below are the automatically generated planets from a database"
        		+ "\nthat this simulator will use. To get started, go to the Simulation tab and click 'Generate'.");
        
    	display.setMinSize(100, 100);
    	display.setEditable(false);
    	scrollPane = new ScrollPane();
    	scrollPane.setContent(display);
    	mainPane = new GridPane();
    	mainPane.setPadding(new Insets(10,10,10,10));
    	mainPane.setHgap(10);
    	mainPane.setVgap(10);
    	SQL = new sqlUtil(rs,con,st,rsMetaData);
    	mainPane.add(introMessage, 1, 0);
    	mainPane.add(btn, 1, 1);
    	mainPane.add(scrollPane, 1, 2);
        
    	try {
          if (SQL.connect(url,user,password)) 
          	SQL.executeQuery(query);
          else 
          	display.setText("not able to connect!");
          
		} catch (SQLException e) {
			display.setText("cannot get data");
		}
    	display.setText("Click Button above to fetch Planet Data from DataBase");
	}
	/**
	 * Initializes Simulation Tab
	 */
	public void simBaseSetup() {
		simBasePane = new GridPane();
		simBasePane.setPadding(new Insets(10,10,10,10));
		runSim = new Button("Click to Run Simulation");
		genPlanets = new Button("Generate Planets and Map");
		spacePopSlide = new Slider(5,25,10);
		selectPop = new Label("Select Spaceship Population (Millions)");
		writeFindings = new Button("Write Results to File");
		writeSuccess = new Label("");
		
		runSim.setDisable(true);
		writeFindings.setDisable(true);
		spacePopSlide.setShowTickMarks(true); 
		spacePopSlide.setShowTickLabels(true); 
		spacePopSlide.setMajorTickUnit(5); 
		spacePopSlide.setMinorTickCount(5);
		simBasePane.setHgap(10);
		simBasePane.setVgap(10);
		simBasePane.add(genPlanets, 0, 0);
		simBasePane.add(runSim, 0, 1);
		simBasePane.add(selectPop, 0, 2);
		simBasePane.add(spacePopSlide, 0, 3);
		simBasePane.add(writeFindings, 0, 4);
		simBasePane.add(writeSuccess, 0, 5);
		
	}
	/**
	 * Initializes Simulation Window
	 * @throws FileNotFoundException
	 */
	public void simSetup() throws FileNotFoundException {
		simPane = new Pane();
		startRoute = new Button("Start Route");
		moveShip = new Button("Move Ship");
		toggleConnects = new Button("Toggle Connections");
		shipStats = new TextArea();
		speedControl = new Slider(0.5,4,1);
		lblSpeed = new Label("Adjust for Speed:");
		
		startRoute.setLayoutX(1050);
		startRoute.setLayoutY(30);
		moveShip.setLayoutX(1050);
		moveShip.setLayoutY(100);
		speedControl.setLayoutX(1025);
		speedControl.setLayoutY(200);
		speedControl.setShowTickMarks(true); 
		speedControl.setShowTickLabels(true); 
		speedControl.setMajorTickUnit(0.5); 
		lblSpeed.setLayoutX(1040);
		lblSpeed.setLayoutY(175);
		toggleConnects.setLayoutX(1025);
		toggleConnects.setLayoutY(275);
		shipStats.setLayoutX(1025);
		shipStats.setLayoutY(350);
		shipStats.setMaxSize(150, 400);
		shipStats.setEditable(false);
		simPane.setLayoutX(0);
		simPane.setLayoutY(0);
		showLines = true;
		speed =1000;
		simPane.getChildren().addAll(startRoute, moveShip,shipStats,toggleConnects,speedControl,lblSpeed);
		
		FileInputStream fInp = new FileInputStream(directory+"\\space bg.png");
		Image image = new Image(fInp);
		BackgroundImage backgroundimage = new BackgroundImage(image,  
										BackgroundRepeat.NO_REPEAT,  
										BackgroundRepeat.NO_REPEAT,  
										BackgroundPosition.DEFAULT,  
										BackgroundSize.DEFAULT); 
		Background bg = new Background(backgroundimage); 
		simPane.setBackground(bg);
		
		planetSprites = new Circle[Planet.allPlanets.size()];
		planetStats = new Label[Planet.allPlanets.size()];
		lineSprites = new ArrayList<Line>();
		for(int i=0;i <Planet.allPlanets.size();i++) {
			//for every planet, make a circle and display it in coords with correct radius
			Planet p = Planet.allPlanets.get(i);
			Circle c = Planet.genSprite(p);
			Label label = new Label(p.id+"");
			c.setLayoutX(p.x);
			c.setLayoutY(p.y);
			label.setLayoutX(p.x);
			label.setLayoutY(p.y);
			simPane.getChildren().addAll(c,label);
			label.toFront();
			Line ln;
			for(int j=0;j<p.connections.size();j++) {
				if(p.connections.get(j).id != p.id) {
					ln = simulation.createEdge(p, p.connections.get(j));
					lineSprites.add(ln);
					simPane.getChildren().add(ln);
					ln.toBack();
				}
			}
			
		}
	
		ship = new SpaceShip(spacePop,25000000);
		ship.sprite.setLayoutX(Planet.allPlanets.get(0).x);
		ship.sprite.setLayoutY(Planet.allPlanets.get(0).y);
		
		simPane.getChildren().add(ship.sprite);
		simScene = new Scene(simPane,1200,1000);
		simWindow = new Stage();
		simWindow.setScene(simScene);
		simWindow.setTitle("Space Simulation");
		simWindow.show();
			
		shipStats.setText("SpaceShip Info:\n"
				+"Passengers: "+ship.passengers
				+ "\nCapacity: "+ ship.capacity
				+ "\nShip Speed: " + speed
				+ "\nX-position: "+ Planet.allPlanets.get(0).x
				+ "\nY-position: "+ Planet.allPlanets.get(0).y);

		simulation sim = new simulation();
		endMessage="";
		startRoute.setOnMouseClicked((MouseEvent me)->{
			ArrayList<Planet> ap = sim.modifiedShortestPath(Planet.allPlanets.get(0), ship, Planet.allPlanets);
			path = new Route(ap);
			routeString ="";
			for(int i=0;i<ap.size();i++) {
				routeString+=ap.get(i).id+" -> ";
			}
		
			travel(Planet.allPlanets.get(0),path.route.get(0));
			ship.passengers-=(path.route.get(0).maxPopulation-path.route.get(0).population);
			System.out.println();
			startRoute.setDisable(true);
			shipStats.setText("SpaceShip Info:\n"
					+"Passengers: "+ship.passengers
					+ "\nCapacity: "+ ship.capacity
					+ "\nShip Speed: " + speed
					+ "\nX-position: "+ path.route.get(0).x
					+ "\nY-position: "+ path.route.get(0).y
					+ "\nRoute: "+ routeString);
			startRoute.setDisable(true);
		});
		
		move =0; shortest = false;
		moveShip.setOnAction((ActionEvent event)->{
				if(move<path.route.size()-1) {
					ship.sprite.setLayoutX(path.route.get(move).x);
					ship.sprite.setLayoutY(path.route.get(move).y);
					travel(path.route.get(move),path.route.get(move+1));
					if(!shortest)ship.passengers-=(path.route.get(move).maxPopulation-path.route.get(move).population);
					if(ship.passengers<0)ship.passengers=0;
					move++;
				}else {
					if(!shortest) {
						path.end = path.route.get(path.route.size()-1);
						ArrayList<Planet> temp = sim.shortestPath(path.route.get(path.route.size()-1),Planet.allPlanets);
						for(int i=0;i<temp.size();i++) {
							path.route.add(temp.get(i));
							routeString+=temp.get(i).id+" -> ";
						}
						routeString+="end";
						shortest = true;
					}
				}
				if(move>=path.route.size()-1 && shortest)endMessage = "Trip Complete!";
				shipStats.setText("SpaceShip Info:\n"
						+"Passengers: "+ship.passengers
						+ "\nCapacity: "+ ship.capacity
						+ "\nShip Speed: " + speed
						+ "\nX-position: "+ path.route.get(move).x
						+ "\nY-position: "+ path.route.get(move).y
						+ "\nRoute: "+ routeString
						+ "\n"+ endMessage);
		});
		
		toggleConnects.setOnAction((ActionEvent event)->{
			if(showLines) showLines = false;
			else showLines = true;
			
			for(int i=0;i<lineSprites.size();i++){
				if(showLines) {simPane.getChildren().add(lineSprites.get(i));
				lineSprites.get(i).toBack();
				}
				else simPane.getChildren().remove(lineSprites.get(i));
			}
		});
		
		speedControl.setOnMouseExited((MouseEvent d)->{
			speed = speedControl.getValue();
			speed*=(int)1000;
		});
	}
	/**
	 * Initializes Calendar Tab
	 */
	public void calendarSetup() {
		calPane = new GridPane();
		webView = new WebView();
        webView.getEngine().load("https://drive.google.com/file/d/1sgsg7h6LUUbT1xtz3RtmpOuG9f8tkjGN/view?usp=sharing");
        calPane.add(webView, 0, 0);
	}
	/**
	 * Initializes API Tab
	 */
	public void apiSetup() {
		apiPane = new GridPane();
		apiView = new WebView();
		apiView.getEngine().load("https://drive.google.com/file/d/1tUB-w05ao6rhhGKUrHqb_75stalzP9Ee/view?usp=sharing");
		apiPane.add(apiView, 0, 0);
	}
	
	public void videoSetup() {
		videoPane = new GridPane();
		togglePlay = new Button("Toggle Play/Pause");
		videoPane.setPadding(new Insets(20,20,20,20));
		videoPane.setHgap(10);
		videoPane.setVgap(10);
		video = new Media(new File(directory+"\\Video.mp4").toURI().toString());
		videoPlayer = new MediaPlayer(video);
		videoView = new MediaView(videoPlayer);
		videoPane.add(videoView, 0, 0);
		videoPane.add(togglePlay, 0, 1);
		videoView.setFitWidth(700);
		videoView.setFitHeight(400);
		togglePlay.setOnAction((ActionEvent event)->{
			if(playing)playing =false;
			else playing = true;
			
			if(playing)videoPlayer.play();
			else videoPlayer.pause();
		});
	}
	@Override
	public void start(Stage primaryStage) throws InterruptedException{
			TabPane tabPane = new TabPane();
			Tab mainTab = new Tab("Main");
			Tab simulation = new Tab("Simulate");
			Tab calendar = new Tab("Calendar");
			Tab apiDoc = new Tab("API");
			conclude = new Tab("Conclusions");
		
			mainSetup();
			mainTab.setContent(mainPane);
			mainTab.setClosable(false);
			
			simBaseSetup();
			simulation.setContent(simBasePane);
			simulation.setClosable(false);
			
			calendarSetup();
			calendar.setContent(calPane);
			calendar.setClosable(false);
			
			apiSetup();
			apiDoc.setContent(apiPane);
			apiDoc.setClosable(false);
			
			tabPane.getTabs().addAll(mainTab,simulation,calendar,apiDoc,conclude);
			BorderPane root = new BorderPane(tabPane);
			Scene scene = new Scene(root,800,500);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Spaceship Escape");
			primaryStage.show();
			
			initPopup(primaryStage);
			actions(primaryStage);
			
	}
	/**
	 * Handles all ActionEvents (Buttons,Sliders, etc)
	 * @param stage
	 */

	public void actions(Stage stage) {
		btn.setOnAction((ActionEvent event)->{
			try {
		        if (SQL.connect(url,user,password)) 
		          	display.setText(SQL.executeQuery(query));
		          else 
		          	display.setText("not able to connect!");
		          
				} catch (SQLException e) {
					display.setText("cannot get data");
				}
		});
		genPlanets.setOnAction((ActionEvent event) ->{
			Planet temp = new Planet();
			ArrayList<String> cmds = temp.genPlanet(21);
			try {
				SQL.executeCommand("DELETE FROM planetInfo;");
				for(int i=0;i<cmds.size();i++) {
				SQL.executeCommand(cmds.get(i));
				}
				Planet.allPlanets.clear();
				System.out.println(Planet.allPlanets.size());
				String planetQuery = SQL.executeQuery(query);
				temp.readPlanets(planetQuery);
			} catch (SQLException e) {
				System.out.println("Didnt work");
			}
			genPlanets.setDisable(true);
		});
		runSim.setOnAction((ActionEvent event) ->{
			try {
				simSetup();
				writeFindings.setDisable(false);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
		spacePopSlide.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				spacePop = (long)spacePopSlide.getValue()*1000000;
				runSim.setDisable(false);
			}
		});
		selectDir.setOnAction((ActionEvent event)->{
			directory = dChoose.showDialog(stage).getAbsolutePath();
			System.out.println(directory);
			if(directory.length()>0) {
				System.out.println("Done");
				videoSetup();
				conclude.setContent(videoPane);
				conclude.setClosable(false);
				}
			popup.close();
		});
		writeFindings.setOnAction((ActionEvent event)->{
			if(sqlUtil.writeResults(ship, (long)spacePopSlide.getValue(), speed, path, routeString, directory)) {
				writeSuccess.setText("Write to File Successful!");
			}else writeSuccess.setText("Write Failed");
			
		});

	}
	/**
	 * Moves the space ship sprite from one planet to another.
	 * Uses animations to move the sprite.
	 * @param st - Starting Planet
	 * @param end - Destination Planet
	 */
	private void travel(Planet st, Planet end){
		double delX = end.x-st.x;
		double delY = end.y-st.y;
		TranslateTransition tt = new TranslateTransition();
		tt.setDuration(Duration.millis(speed)); 
		tt.setFromX(0); tt.setFromY(0);
		tt.setToX(delX); tt.setToY(delY);
		tt.setNode(ship.sprite);
		tt.setCycleCount(1);
		tt.setAutoReverse(false); 
		tt.play();
		RotateTransition rt = new RotateTransition();
		rt.setNode(ship.sprite);
		rt.setAutoReverse(false); 
		rt.setToAngle(calcRot(st,end));
		rt.play();
		
	}

	/**
	 * Calculates the degree of rotation that the spaceship should
	 * rotate when traveling from one planet to another.
	 * @param st - Starting Planet
	 * @param end - Destination Planet
	 * @return - Degree of Rotation
	 */
	private double calcRot(Planet st, Planet end) {
		double x1=st.x,y1=st.y,x2=end.x,y2=end.y;
		double theta =0;
		if(y2<y1) {
			if(x2>x1) {theta =45;}
			else {theta = 135;}
		}else {
			if(x2>x1) { theta= 315;}
			else {theta = 225;}
		}
		return theta;
	}
	/**
	 * Main Method.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
