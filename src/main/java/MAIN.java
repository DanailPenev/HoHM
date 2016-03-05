package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main extends Application {

    private final String exitCommand = "exit";
    private final String singleCommand = "single";
    private final String multiCommand = "multi";

    Label single, multi, exit, host, join;


    Pane gamePane;
    TextField tfInput;
    VBox mainLayout;

    WebSocketClient webSocketClient;
    ClientSocket clientSocket;

    String[] lines = {"qwe1","asd2","zxc3"};

    final int MAIN_MENU = 0, SINGLE_PLAYER = 1, MULTI_MENU = 2, MULTI_WAITING = 3, MULTI_PLAYING = 4;
    int GAME_MODE;

    ArrayList<Label> labels;
    Label activeLabel;

    Timer spawning, dropping;

    int singlePlayerLives, singlePlayerLimit;

    String username, destUri;


    @Override
    public void start(Stage primaryStage) throws Exception{
        TextInputDialog dialog = new TextInputDialog("walter");
        dialog.setTitle("Text Input Dialog");
        dialog.setHeaderText("Look, a Text Input Dialog");
        dialog.setContentText("Please enter your name:");

// Traditional way to get the response value.
        username = dialog.showAndWait().get();
        System.out.println(username);

        labels = new ArrayList<Label>();

        gamePane = new Pane();
        gamePane.setStyle("-fx-background-color:#000000;");
        gamePane.setMinHeight(565);
        gamePane.setMinWidth(300);

        tfInput = new TextField();
        tfInput.setMinSize(300,35);
        tfInput.setAlignment(Pos.BOTTOM_LEFT);
        tfInput.setStyle("-fx-background-color:#000000; -fx-text-fill:#00FF00; -fx-font-family:monospace;");

        inputHandlers();


        mainLayout = new VBox(gamePane,tfInput);

        GAME_MODE = MAIN_MENU;

        Scene scene = new Scene(mainLayout,300,590);
        scene.getStylesheets().add(getClass().getResource("font.css").toExternalForm());
        
        
        checkMode();



        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });


    }

    private void inputHandlers() {
        tfInput.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(GAME_MODE == MAIN_MENU){
                    if(tfInput.getText().equals(singleCommand)){
                        single.setStyle("-fx-text-fill: #00FF00;");
                        if(event.getCode().equals(KeyCode.ENTER)){
                            GAME_MODE = SINGLE_PLAYER;
                            tfInput.setText("");
                            gamePane.getChildren().clear();
                            checkMode();
                        }
                    }
                    else if(tfInput.getText().equals(multiCommand)){
                        multi.setStyle("-fx-text-fill: #00FF00;");
                        if(event.getCode().equals(KeyCode.ENTER)){
                            GAME_MODE = MULTI_MENU;
                            tfInput.setText("");
                            gamePane.getChildren().clear();
                            checkMode();
                        }
                    } else if(tfInput.getText().equals(exitCommand)){
                        exit.setStyle("-fx-text-fill: #00FF00;");
                        if(event.getCode().equals(KeyCode.ENTER)){
                            System.exit(0);
                        }
                    }
                    else{
                        single.setStyle("-fx-text-fill: #FF0000;");
                        multi.setStyle("-fx-text-fill: #FF0000;");
                        exit.setStyle("-fx-text-fill: #FF0000;");
                    }
                }

                if(GAME_MODE == SINGLE_PLAYER){
                    activeLabel = null;
                    for(Label label : labels){
                        activeLabel = label;
                        break;
                    }
                    if(activeLabel == null){
                        return;
                    }
                    if(tfInput.getText().equals(activeLabel.getText())){
                        activeLabel.setStyle("-fx-text-fill:#00FF00");
                        if(event.getCode().equals(KeyCode.ENTER)){
                            labels.remove(activeLabel);
                            gamePane.getChildren().remove(activeLabel);
                            tfInput.setText("");

                        }
                    }
                    else{
                        activeLabel.setStyle("-fx-text-fill:#FF0000");
                    }
                }

                if(GAME_MODE == MULTI_MENU){
                    if(tfInput.getText().equals(host.getText())){
                        host.setStyle("-fx-text-fill:#00FF00;");
                        if(event.getCode().equals(KeyCode.ENTER)){
                            GAME_MODE = MULTI_WAITING;
                            checkMode();
                            connect();
                        }
                    }
                    else if(tfInput.getText().equals(join.getText())){
                        join.setStyle("-fx-text-fill:#00FF00;");
                    } else{
                        host.setStyle("-fx-text-fill:#FF0000;");
                        join.setStyle("-fx-text-fill:#FF0000;");
                    }
                }

            }
        });



    }

    private void connect() {
        destUri = "ws://localhost:421";
        webSocketClient = new WebSocketClient();
        clientSocket = new ClientSocket(username);
        try{
            webSocketClient.start();
            URI uri = new URI(destUri);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            webSocketClient.connect(clientSocket,uri,request);
            System.out.println("Connecting to , uri");
            clientSocket.awaitClose(5, TimeUnit.SECONDS);

            TextInputDialog dialog = new TextInputDialog("walter");
            dialog.setTitle("Text Input Dialog");
            dialog.setHeaderText("Look, a Text Input Dialog");
            dialog.setContentText("Please enter your name:");

            String lobbyName = dialog.showAndWait().get();

            clientSocket.broadcastMessage("CRTL:"+lobbyName);

        }catch (Exception e){
            System.out.println("GARMI");
        }

    }

    private void checkMode() {
        if(GAME_MODE == MAIN_MENU){
            createMainMenuLayout();
        }
        else if(GAME_MODE == SINGLE_PLAYER){
            createSinglePlayerLayout();
        }
        else if(GAME_MODE == MULTI_MENU){
            createMultiMenuLayout();
        }
        else if(GAME_MODE == MULTI_WAITING){
            createMultiWaitingLayout();
        }
//        els if(GAME_MODE == MULTI_)

    }

    private void createMultiWaitingLayout() {
        gamePane.getChildren().clear();
        labels.clear();

        Label waiting = new Label("waiting");
        createMainMenuItem(waiting,0);

    }

    private void createMultiMenuLayout() {
        labels.clear();
        gamePane.getChildren().clear();

        host = new Label("host");
        join = new Label("join");
        createMainMenuItem(host, 0);
        createMainMenuItem(join, 1);
    }

    private void createMainMenuLayout(){
        labels.clear();
        gamePane.getChildren().clear();

        single = new Label(singleCommand);
        multi = new Label(multiCommand);
        exit = new Label(exitCommand);

        createMainMenuItem(single, 0);
        createMainMenuItem(multi, 1);
        createMainMenuItem(exit, 2);
    }

    private void createMainMenuItem(Label node, int position){

        node.setLayoutX(50);
        node.setLayoutY(100 + position*20);

        node.setStyle("-fx-text-fill: #FF0000;");

        gamePane.getChildren().add(node);
    }

    private void createSinglePlayerLayout(){
        gamePane.getChildren().clear();

        startSinglePlayer();

    }

    private void startSinglePlayer() {
        singlePlayerLimit = 540;
        singlePlayerLives = 5;
        spawning = new Timer();
        spawning.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String s = lines[new Random().nextInt(lines.length)];
                Label label = new Label("");
                label.setLayoutX(0);
                label.setLayoutY(0);
                label.setStyle("-fx-text-fill:#FF0000;");
                labels.add(label);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        gamePane.getChildren().add(label);

                    }
                });
            }
        },2000,4000);

        dropping = new Timer();
        dropping.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Label labelToRemove = null;
                for(Label label : labels){
                    if(label.getLayoutY()>= singlePlayerLimit){
                        labelToRemove = label;
                        singlePlayerLimit -= 20;
                        singlePlayerLives--;
                    }
                    else {
                        label.setLayoutY(label.getLayoutY() + 20);
                    }
                }
                if(labelToRemove != null){
                    labels.remove(labelToRemove);
                    labelToRemove.setStyle("-fx-text-fill:#660000;");
                }

                if(singlePlayerLives<=0){
                    singlePlayerGameOver();
                }
            }
        },0,250);


    }

    private void singlePlayerGameOver() {
        spawning.cancel();
        dropping.cancel();
        Label label = new Label("game over :(");
        label.setStyle("-fx-text-fill: #FF0000; -fx-font-size: 24");
        label.setLayoutX(50);
        label.setLayoutY(100);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gamePane.getChildren().add(label);
            }
        });
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            int counter = 0;
            @Override
            public void run() {
                counter++;
                if(counter==3){
                    GAME_MODE = MAIN_MENU;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            checkMode();
                            tfInput.setText("");
                        }
                    });
                    this.cancel();
                }
            }
        },0,2500);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
