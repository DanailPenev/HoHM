package Model;

import Controller.ServerController;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Danail on 3/4/2016.
 */
public class Game {

    public static Map<String, ArrayList<Session>> lobby_participants = new HashMap<String, ArrayList<Session>>();

    public static void startGame(String lobbyName){
        Lobby lobby = getLobby(lobbyName);

        if(lobby != null){
            ServerController.availableLobbies.remove(lobby);
            lobby_participants.put(lobby.getId(),lobby.getPlayers() );
        } else {
            System.out.println("Gre6ka s imeto na lobyto pri startGame");
        }
    }

    public static void endGame(String lobbyName){
        Lobby lobby = getLobby(lobbyName);

        if(lobby!=null){
            lobby_participants.remove(lobby);
        } else {
            System.out.println("Gre6ka s imeto na lobyto pri endGame");
        }
    }

    public static Lobby getLobby(String lobbyName){
        Lobby lobby = null;
        for(Lobby l : ServerController.availableLobbies){
            if (l.getLobbyName().equals(lobbyName)){
                lobby = l;
            }
        }
        return lobby;
    }

}
