package Model;

import Controller.ServerController;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Danail on 3/4/2016.
 */
public class Lobby {

    //maybe later implement
    private final int lobbyLimit = 4;

    public static int idCounter = 1;


    private String idLobby;
    private ArrayList<Session> players;
    private String lobbyName;

    //Might initialize with the host session
    private Session host;

    public Lobby(String lobbyName){
        players = new ArrayList<Session>();
        this.lobbyName = lobbyName;
        idLobby = "" + idCounter;
        ++idCounter;
    }

    public void addPlayer(Session sender){
        players.add(sender);
    }

    public ArrayList<Session> getPlayers(){
        return players;
    }

    public String getLobbyName(){
        return lobbyName;
    }

    public String getId() {
        return idLobby;
    }

    public static Lobby tryToConnect(String lobbyName){
        Lobby connectedTo = null;
        for (Lobby l : ServerController.availableLobbies){
            if (l.getLobbyName().equals(lobbyName)){
                connectedTo = l;
            }
        }
        return connectedTo;
    }


}


