
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

import java.io.*;
import java.net.*;
import java.util.*;

public class Main extends Application {

    private final String exitCommand = "exit";
    private final String singleCommand = "single";
    private final String multiCommand = "multi";

    Label single, multi, exit, host, join, chooseLanguage, chooseSpeed, startSingleGame;


    Pane gamePane;
    TextField tfInput;
    VBox mainLayout;

    WebSocketClient webSocketClient;
    ClientSocket clientSocket;

    String[] lines = {"qwe1", "asd2", "zxc3", "rty4", "fgh5", "vbn6", "uio7", "jkl8", "m,.9", "p[]0"};

    final int MAIN_MENU = 0, SINGLE_MENU = 1, SINGLE_PLAYER = 2, MULTI_MENU = 3, MULTI_WAITING = 4, MULTI_JOINING = 5,
            MULTI_PLAYING = 6;
    final int MIXED = 0, CPP = 1, PYTHON = 2, JAVA = 3;
    int GAME_MODE;
    int CHOSEN_LANGUAGE = MIXED;

    ArrayList<Label> labels;
    Label activeLabel;

    Timer spawning, dropping;

    int playerLives, playerLimit;

    String username, destUri, lobbyName = "";

    boolean isHost = false;

    int cccccOMBO = 0;

    private ArrayList<String> realSnipetts;

    @Override
    public void start(Stage primaryStage) throws Exception {
        realSnipetts = new ArrayList<String>();
        populateArrayOfSnippets();
        TextInputDialog dialog = new TextInputDialog("walter");
        dialog.setTitle("Text Input Dialog");
        dialog.setHeaderText("Set up your profile");
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
        tfInput.setMinSize(300, 35);
        tfInput.setAlignment(Pos.BOTTOM_LEFT);
        tfInput.setStyle("-fx-background-color:#000000; -fx-text-fill:#00FF00; -fx-font-family:monospace;");

        inputHandlers();


        mainLayout = new VBox(gamePane, tfInput);

        GAME_MODE = MAIN_MENU;

        Scene scene = new Scene(mainLayout, 300, 590);
//        scene.getStylesheets().add(getClass().getResource("font.css").toExternalForm());


        checkMode();


        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent t) {

                Platform.exit();
                System.exit(0);
            }
        });


    }

    private void populateArrayOfSnippets() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                realSnipetts = new ArrayList<String>();
                try {
                    File f = new File("src\\main\\java\\res\\codeSnippets.txt");
                    FileReader ff = new FileReader(f);
                    BufferedReader br = new BufferedReader(ff);
                    String currentLine;
                    while ((currentLine = br.readLine()) != null) {
                        realSnipetts.add(currentLine);
                    }
                    System.out.println("Buffer Done");
                    br.close();
                } catch (IOException e) {
                    System.out.println("Buffer issues");
                }
            }
        });
        t.start();
    }

    private void inputHandlers() {
        tfInput.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (GAME_MODE == MAIN_MENU) {
                    if (tfInput.getText().equals(singleCommand)) {
                        single.setStyle("-fx-text-fill: #00FF00;");
                        if (event.getCode().equals(KeyCode.ENTER)) {
                            GAME_MODE = SINGLE_MENU;
                            tfInput.setText("");
                            gamePane.getChildren().clear();
                            checkMode();
                        }
                    } else if (tfInput.getText().equals(multiCommand)) {
                        multi.setStyle("-fx-text-fill: #00FF00;");
                        if (event.getCode().equals(KeyCode.ENTER)) {
                            GAME_MODE = MULTI_MENU;
                            tfInput.setText("");
                            gamePane.getChildren().clear();
                            checkMode();
                        }
                    } else if (tfInput.getText().equals(exitCommand)) {
                        exit.setStyle("-fx-text-fill: #00FF00;");
                        if (event.getCode().equals(KeyCode.ENTER)) {
                            System.exit(0);
                        }
                    } else {
                        single.setStyle("-fx-text-fill: #FF0000;");
                        multi.setStyle("-fx-text-fill: #FF0000;");
                        exit.setStyle("-fx-text-fill: #FF0000;");
                    }
                }

                if (GAME_MODE == SINGLE_MENU) {
                    if (tfInput.getText().equals("back();")) {
                        tfInput.setText("");
                        GAME_MODE = MAIN_MENU;
                        checkMode();
                    }
                    else if(tfInput.getText().equals("language(\"MIXED\")")){
                        chooseLanguage.setStyle("-fx-text-fill:#00FF00");
                        if (event.getCode().equals(KeyCode.ENTER)){
                            CHOSEN_LANGUAGE = MIXED;
                            chooseLanguage.setText("language(\"MIXED\")");
                        }
                    }else if(tfInput.getText().equals("language(\"CPP\")")){
                        chooseLanguage.setStyle("-fx-text-fill:#00FF00");
                        if (event.getCode().equals(KeyCode.ENTER)){
                            CHOSEN_LANGUAGE = CPP;
                            chooseLanguage.setText("C++");
                        }
                    }else if(tfInput.getText().equals("language(\"PYTHON\")")){
                        chooseLanguage.setStyle("-fx-text-fill:#00FF00");
                        if (event.getCode().equals(KeyCode.ENTER)){
                            CHOSEN_LANGUAGE = PYTHON;
                            chooseLanguage.setText("language(\"PYTHON\")");

                        }
                    }else if(tfInput.getText().equals("language(\"JAVA\")")){
                        chooseLanguage.setStyle("-fx-text-fill:#00FF00");
                        if (event.getCode().equals(KeyCode.ENTER)){
                            CHOSEN_LANGUAGE = JAVA;
                            chooseLanguage.setText("language(\"JAVA\")");
                        }
                    } else if (tfInput.getText().equals(startSingleGame.getText())) {
                        startSingleGame.setStyle("-fx-text-fill:#00FF00");
                        if (event.getCode().equals(KeyCode.ENTER)) {
                            tfInput.setText("");
                            GAME_MODE = SINGLE_PLAYER;
                            checkMode();
                        }
                    } else {
                        startSingleGame.setStyle("-fx-text-fill:#FF0000;");
                        chooseSpeed.setStyle("-fx-text-fill:#FF0000;");
                        chooseLanguage.setStyle("-fx-text-fill:#FF0000;");
                    }
                }

                if (GAME_MODE == SINGLE_PLAYER) {
                    activeLabel = null;
                    try {
                        activeLabel = labels.get(0);
                    } catch (Exception e) {
                        System.out.println("LOL");
                    }
                    if (activeLabel == null) {
                        return;
                    }
                    if (tfInput.getText().equals(activeLabel.getText())) {
                        activeLabel.setStyle("-fx-text-fill:#00FF00");
                        if (event.getCode().equals(KeyCode.ENTER)) {
                            synchronized (labels) {
                                labels.remove(activeLabel);
                            }
                            gamePane.getChildren().remove(activeLabel);
                            tfInput.setText("");

                        }
                    } else {
                        activeLabel.setStyle("-fx-text-fill:#FF0000");
                    }
                }

                if (GAME_MODE == MULTI_MENU) {
                    if (tfInput.getText().equals("back();")) {
                        tfInput.setText("");
                        GAME_MODE = MAIN_MENU;
                        checkMode();
                    }
                    if (tfInput.getText().equals(host.getText())) {
                        host.setStyle("-fx-text-fill:#00FF00;");
                        if (event.getCode().equals(KeyCode.ENTER)) {
                            tfInput.setText("");
                            GAME_MODE = MULTI_WAITING;
                            checkMode();
                            host();
                            isHost = true;
                        }
                    } else if (tfInput.getText().equals(join.getText())) {
                        join.setStyle("-fx-text-fill:#00FF00;");
                        if (event.getCode().equals(KeyCode.ENTER)) {
                            tfInput.setText("");
                            GAME_MODE = MULTI_JOINING;
                            checkMode();
                            try {
                                connectAndSeeLobbies();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    } else {
                        host.setStyle("-fx-text-fill:#FF0000;");
                        join.setStyle("-fx-text-fill:#FF0000;");
                    }
                }
                if (GAME_MODE == MULTI_JOINING) {
                    if (tfInput.getText().equals("back();")) {
                        tfInput.setText("");
                        GAME_MODE = MAIN_MENU;
                        checkMode();
                    }
                    ArrayList<Label> labelsArray = new ArrayList<Label>();
                    for (Node n : gamePane.getChildren()) {
                        labelsArray.add((Label) n);
                    }
                    for (Label l : labelsArray) {
                        if (tfInput.getText().equals(l.getText())) {
                            l.setStyle("-fx-text-fill:#00FF00;");
                            if (event.getCode().equals(KeyCode.ENTER)) {
                                // JOIN GAME

                                try {
                                    joinLobby(l.getText());
                                    lobbyName = l.getText();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                tfInput.setText("");
                                GAME_MODE = MULTI_WAITING;
                                checkMode();
                            }
                        } else {
                            l.setStyle("-fx-text-fill:#FF0000;");
                        }
                    }
                }
                if (GAME_MODE == MULTI_WAITING) {
                    if (isHost) {
                        Label start = (Label) gamePane.getChildren().get(0);
                        if (tfInput.getText().equals(start.getText())) {
                            start.setStyle("-fx-text-fill:#00FF00;");
                            if (event.getCode().equals(KeyCode.ENTER)) {
                                try {
                                    clientSocket.broadcastMessage("STRT:" + lobbyName);
                                } catch (IOException e) {
                                    System.out.println("STARTING MULTI ERROR");
                                    e.printStackTrace();
                                }
                                tfInput.setText("");
                                GAME_MODE = MULTI_PLAYING;
                                checkMode();
                            }
                        } else {
                            start.setStyle("-fx-text-fill:#FF0000;");
                        }
                    }

                }
                if (GAME_MODE == MULTI_PLAYING) {
                    activeLabel = null;
                    try {
                        activeLabel = labels.get(0);
                    } catch (Exception e) {
                        System.out.println("LOL");
                    }
                    if (activeLabel == null) {
                        return;
                    }
                    if (tfInput.getText().equals(activeLabel.getText())) {
                        activeLabel.setStyle("-fx-text-fill:#00FF00");
                        if (event.getCode().equals(KeyCode.ENTER)) {
                            synchronized (labels) {
                                labels.remove(activeLabel);
                            }
                            gamePane.getChildren().remove(activeLabel);
                            tfInput.setText("");
                            cccccOMBO++;
                            if (cccccOMBO == 3) {
                                try {
                                    injectOpponents();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                cccccOMBO = 0;
                            }

                        }
                    } else {
                        activeLabel.setStyle("-fx-text-fill:#FF0000");
                    }
                }
            }
        });
    }

    private void injectOpponents() throws IOException {
//        clientSocket.broadcastMessage("WORD:");
        clientSocket.broadcastMessage("WORD:" + lobbyName);
    }

    private void joinLobby(String lobbyName) throws IOException {
        clientSocket.broadcastMessage("CONN:" + lobbyName);
    }

    private void connectAndSeeLobbies() throws IOException {
        destUri = "ws://25.82.76.49:421";
        webSocketClient = new WebSocketClient();
        clientSocket = new ClientSocket(username, this);
        try {
            webSocketClient.start();
            URI uri = new URI(destUri);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            webSocketClient.connect(clientSocket, uri, request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        URL obj = new URL("http://25.82.76.49:420/getLobbies");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + destUri);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine + "\n");
        }
        in.close();

        String[] servers = response.toString().split("\n");

        createMultiJoiningLayout(servers);


    }

    private void createMultiJoiningLayout(String[] servers) {
        gamePane.getChildren().clear();
        labels.clear();
        for (int i = 0; i < servers.length; i++) {
            String server = servers[i];
            System.out.println(server);
            Label label = new Label(server);
            label.setStyle("-fx-text-fill:#FF0000;");
            createMainMenuItem(label, i);
        }
        System.out.println(servers.length);
    }

    private void host() {
        destUri = "ws://25.82.76.49:421";
        webSocketClient = new WebSocketClient();
        clientSocket = new ClientSocket(username, this);
        try {
            webSocketClient.start();
            URI uri = new URI(destUri);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            webSocketClient.connect(clientSocket, uri, request);
            System.out.println("Connecting to , uri");
//            clientSocket.awaitClose(5, TimeUnit.SECONDS);

            TextInputDialog dialog = new TextInputDialog("walter");
            dialog.setTitle("Text Input Dialog");
            dialog.setHeaderText("Set up your lobby!");
            dialog.setContentText("Enter your lobby name:");

            lobbyName = dialog.showAndWait().get();

            clientSocket.broadcastMessage("CRTL:" + lobbyName);

        } catch (Exception e) {
            System.out.println("GARMI");
        }

    }

    private void checkMode() {
        if (GAME_MODE == MAIN_MENU) {
            createMainMenuLayout();
        } else if (GAME_MODE == SINGLE_MENU) {
            createSingleMenuLayout();
        } else if (GAME_MODE == SINGLE_PLAYER) {
            createSinglePlayerGameLayout();
        } else if (GAME_MODE == MULTI_MENU) {
            createMultiMenuLayout();
        } else if (GAME_MODE == MULTI_WAITING) {
            createMultiWaitingLayout();
        } else if (GAME_MODE == MULTI_JOINING) {
            createMultiJoiningLayout();
        } else if (GAME_MODE == MULTI_PLAYING) {
            createMultiPlayingLayout();
        }

    }

    private void createMultiPlayingLayout() {
        Platform.runLater(new Runnable() {
            public void run() {
                gamePane.getChildren().clear();
            }
        });
        labels.clear();

        startMultiPlayerGame();
    }

    private void startMultiPlayerGame() {
        playerLimit = 540;
        playerLives = 5;

        spawning = new Timer();
        spawning.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                String s;
                if (realSnipetts.size() > 1) {
                    s = realSnipetts.get(new Random().nextInt(realSnipetts.size()));
                }
                else {
                    s = lines[new Random().nextInt(lines.length)];
                }
                final Label label = new Label(s);
                label.setLayoutX(0);
                label.setLayoutY(0);
                label.setStyle("-fx-text-fill:#FF0000;");
                labels.add(label);
                Platform.runLater(new Runnable() {
                    public void run() {
                        gamePane.getChildren().add(label);
                        for (int i = 0; i < gamePane.getChildren().size(); i++) {
                            if (gamePane.getChildren().size() == 1) {
                                break;
                            }
                            if (gamePane.getChildren().get(i).getLayoutY() == gamePane.getChildren().get(i + 1).getLayoutY()) {
                                gamePane.getChildren().get(i + 1).setLayoutY(gamePane.getChildren().get(i + 1).getLayoutY() + 20);
                            }
                            if (i == gamePane.getChildren().size() - 2) {
                                break;
                            }
                        }

                    }
                });
            }
        }, 0, 3500);

        dropping = new Timer();
        dropping.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Label labelToRemove = null;

                synchronized (labels) {
                    for (Label label : labels) {
                        if (label.getLayoutY() >= playerLimit) {
                            labelToRemove = label;
                            playerLimit -= 20;
                            playerLives--;
                        } else {
                            label.setLayoutY(label.getLayoutY() + 20);
                        }
                    }
                    if (labelToRemove != null) {
                        labels.remove(labelToRemove);
                    }
                    labelToRemove.setStyle("-fx-text-fill:#660000;");
                }

                if (playerLives <= 0) {
                    try {
                        multiPlayerGameOver();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, 1000);


    }

    private void multiPlayerGameOver() throws IOException {
        spawning.cancel();
        dropping.cancel();
        Sound.onLose();
        clientSocket.broadcastMessage("ENDG:"+lobbyName);
        final Label label = new Label("game over :(");
        label.setStyle("-fx-text-fill: #FF0000; -fx-font-size: 24");
        label.setLayoutX(50);
        label.setLayoutY(100);
        Platform.runLater(new Runnable() {
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
                if (counter == 3) {
                    GAME_MODE = MAIN_MENU;
                    Platform.runLater(new Runnable() {
                        public void run() {
                            checkMode();
                            tfInput.setText("");
                        }
                    });
                    this.cancel();
                }
            }
        }, 0, 2500);

        clientSocket.broadcastMessage("ENDG:" + lobbyName);
    }


    private void createMultiJoiningLayout() {
        gamePane.getChildren().clear();
        labels.clear();
    }

    private void createMultiWaitingLayout() {
        gamePane.getChildren().clear();
        labels.clear();

        Label waiting = new Label("waiting");
        createMainMenuItem(waiting, 0);

    }

    private void createMultiMenuLayout() {
        labels.clear();
        gamePane.getChildren().clear();

        host = new Label("host");
        join = new Label("join");
        createMainMenuItem(host, 0);
        createMainMenuItem(join, 1);
    }

    private void createMainMenuLayout() {
        labels.clear();
        gamePane.getChildren().clear();

        single = new Label(singleCommand);
        multi = new Label(multiCommand);
        exit = new Label(exitCommand);

        createMainMenuItem(single, 0);
        createMainMenuItem(multi, 1);
        createMainMenuItem(exit, 2);
    }

    private void createMainMenuItem(final Label node, int position) {

        node.setLayoutX(50);
        node.setLayoutY(100 + position * 20);

        if(node.getStyle().equals("")){
            node.setStyle("-fx-text-fill: #FF0000;");
        }

        Platform.runLater(new Runnable() {
            public void run() {
                gamePane.getChildren().add(node);
            }
        });
    }

    private void createSingleMenuLayout() {
        gamePane.getChildren().clear();
        labels.clear();
        //c++
        //python
        //java
        Label langTip  = new Label("//MIXED, C++, PYTHON, JAVA");
        langTip.setStyle("-fx-text-fill:#666666;");
        chooseLanguage = new Label("language(\"MIXED\")");

        Label speedTip  = new Label("//SLOW, MEDIUM, FAST");
        speedTip.setStyle("-fx-text-fill:#666666;");
        chooseSpeed = new Label("speed(\"MEDIUM\")");

        startSingleGame = new Label("start()");

        createMainMenuItem(langTip, 0);
        createMainMenuItem(chooseLanguage, 1);
        createMainMenuItem(speedTip, 3);
        createMainMenuItem(chooseSpeed, 4);
        createMainMenuItem(startSingleGame, 6);
    }

    private void createSinglePlayerGameLayout() {
        gamePane.getChildren().clear();
        labels.clear();

        startSinglePlayer();

    }

    private void startSinglePlayer() {
        playerLimit = 540;
        playerLives = 5;
        spawning = new Timer();
        spawning.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                String s;
                if (realSnipetts.size() > 1) {
                    int limit = realSnipetts.size();
                    if(CHOSEN_LANGUAGE != MIXED){
                        limit = 10;
                    }
                    int randomNumber = new Random().nextInt(limit);
                    if(CHOSEN_LANGUAGE != MIXED){
                        s = realSnipetts.get(randomNumber + 10*(CHOSEN_LANGUAGE-1));
                    } else{
                        s = realSnipetts.get(randomNumber);
                    }
                } else {
                    s = lines[new Random().nextInt(lines.length)];
                }
                final Label label = new Label(s);
                label.setMaxHeight(20);
                label.setMinHeight(20);
                label.setLayoutX(0);
                label.setLayoutY(0);
                label.setStyle("-fx-text-fill:#FF0000;");
                labels.add(label);
                Platform.runLater(new Runnable() {
                    public void run() {
                        gamePane.getChildren().add(label);
                        for (int i = 0; i < gamePane.getChildren().size(); i++) {
                            if (gamePane.getChildren().size() == 1) {
                                break;
                            }
                            if (gamePane.getChildren().get(i).getLayoutY() == gamePane.getChildren().get(i + 1).getLayoutY()) {
                                gamePane.getChildren().get(i + 1).setLayoutY(gamePane.getChildren().get(i + 1).getLayoutY() + 20);
                            }
                            if (i == gamePane.getChildren().size() - 2) {
                                break;
                            }
                        }

                    }
                });
            }
        }, 0, 3500);

        dropping = new Timer();
        dropping.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Label labelToRemove = null;
                synchronized (labels) {
                    for (Label label : labels) {
                        if (label.getLayoutY() >= playerLimit) {
                            labelToRemove = label;
                            playerLimit -= 20;
                            playerLives--;
                            tfInput.setText("");
                        } else {
                            label.setLayoutY(label.getLayoutY() + 20);
                        }
                    }
                    if (labelToRemove != null) {
                        labels.remove(labelToRemove);
                        labelToRemove.setStyle("-fx-text-fill:#660000;");
                    }
                }

                if (playerLives <= 0) {
                    singlePlayerGameOver();
                }
            }
        }, 0, 4000);


    }

    private void singlePlayerGameOver() {
        spawning.cancel();
        dropping.cancel();
        Sound.onLose();
        final Label label = new Label("game over :(");
        label.setStyle("-fx-text-fill: #FF0000; -fx-font-size: 24");
        label.setLayoutX(50);
        label.setLayoutY(100);
        Platform.runLater(new Runnable() {
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
                if (counter == 3) {
                    GAME_MODE = MAIN_MENU;
                    Platform.runLater(new Runnable() {
                        public void run() {
                            checkMode();
                            tfInput.setText("");
                        }
                    });
                    this.cancel();
                }
            }
        }, 0, 2500);
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void notifyHostClientEntered(final String newPlayer) {
        if (GAME_MODE == MULTI_WAITING) {
            final Label waitingLabel = (Label) gamePane.getChildren().get(0);
            Platform.runLater(new Runnable() {
                public void run() {
                    Sound.onEnter();
                    waitingLabel.setText("start();");
                    Label player2 = new Label(newPlayer + " joined the lobby");
                    player2.setStyle("-fx-text-fill:#00FF00;");
                    createMainMenuItem(player2, gamePane.getChildren().size());
                }
            });
        }
    }

    public void notifyClient() {

    }

    public void notifyHostClientLeft(String otherPlayer) {
        if (GAME_MODE == MULTI_WAITING) {
            Label waitingLabel = (Label) gamePane.getChildren().get(0);
            if (gamePane.getChildren().size() == 1) {
                waitingLabel.setText("waiting");
            }
            Label toRemove = null;
            for (Node n : gamePane.getChildren()) {
                Label l = (Label) n;
                if (l.getText().contains(otherPlayer)) {
                    toRemove = l;
                }
            }
            if (toRemove != null) {
                final Label finalToRemove = toRemove;
                Platform.runLater(new Runnable() {
                    public void run() {
                        gamePane.getChildren().remove(finalToRemove);

                    }
                });
            }
        }
    }

    public void notifyClientGameStart() {
        GAME_MODE = MULTI_PLAYING;
        checkMode();
    }

    public void getInjected(Integer lineNumber) {
        final Label injectedLabel = new Label(lines[lineNumber]);
        injectedLabel.setStyle("-fx-text-fill:#FF0000;");

        Platform.runLater(new Runnable() {
            public void run() {
                gamePane.getChildren().add(injectedLabel);
                for (int i = 0; i < gamePane.getChildren().size(); i++) {
                    if (gamePane.getChildren().size() == 1) {
                        break;
                    }
                    if (gamePane.getChildren().get(i).getLayoutY() == gamePane.getChildren().get(i + 1).getLayoutY()) {
                        gamePane.getChildren().get(i + 1).setLayoutY(gamePane.getChildren().get(i + 1).getLayoutY() + 20);
                    }
                    if (i == gamePane.getChildren().size() - 2) {
                        break;
                    }
                }

            }
        });
        labels.add(injectedLabel);
    }

    public void win() {
        spawning.cancel();
        dropping.cancel();
        Sound.onWin();
        final Label label = new Label("VICTORIOUS :)");
        label.setStyle("-fx-text-fill: #00FF00; -fx-font-size: 24");
        label.setLayoutX(50);
        label.setLayoutY(100);
        Platform.runLater(new Runnable() {
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
                if (counter == 3) {
                    GAME_MODE = MAIN_MENU;
                    Platform.runLater(new Runnable() {
                        public void run() {
                            checkMode();
                            tfInput.setText("");
                        }
                    });
                    this.cancel();
                }
            }
        }, 0, 2500);
    }
}