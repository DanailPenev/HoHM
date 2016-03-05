package Controller;

import Model.Game;
import Model.Lobby;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


/**
 * Created by Danail on 3/5/2016.
 */

@WebSocket
public class SocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        System.out.println("Connected user, brat");
//        String username = "user" + HoHMSocket.playerCount++;
//        HoHMSocket.connectedPlayers.put(user, username);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        System.out.println("Disconnected user");
        HoHMSocket.connectedPlayers.remove(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        System.out.println("Message:" + message);
        String messageKey = message.substring(0,5);
        //handler for setting username
        if(messageKey.equals("NAME:")){
            HoHMSocket.connectedPlayers.put(user, message.substring(5));
        }
        //handler for creating a lobby
        else if(messageKey.equals("CRTL:")){
            String host = message.substring(5);
            Lobby newLobby = new Lobby(host);
            ServerController.availableLobbies.add(newLobby);
        }
        //handler for starting a game/lobby
        else if(messageKey.equals("STRT:")){
            String lobbyName = message.substring(5);
            if(!lobbyName.equals("")){
                Game.startGame(lobbyName);
            }
        }
        //handler for ending the game
        else if (messageKey.equals("ENDG:")){
            String lobbyName = message.substring(5);
            if(!lobbyName.equals("")){
                Game.endGame(lobbyName);
            }
        }
        //handler for connecting to a lobby
        else if(messageKey.equals("CONN:")){
            String lobbyToConnectName = message.substring(5);
            Lobby connectedTo = Lobby.tryToConnect(lobbyToConnectName);
            if (connectedTo != null) {
                connectedTo.addPlayer(user);
                String userName = HoHMSocket.connectedPlayers.get(user);
                String messageToSend = userName + " entered the lobby!";
                broadcastToAllInALobby(user, connectedTo.getId(), messageToSend);
            }
        }
        //handler for disconnecting from the lobby
        else if(messageKey.equals("DISC:")){
            String lobbyToDisconnectName = message.substring(5);
            Lobby disconnectFrom = Game.getLobby(lobbyToDisconnectName);
            if (disconnectFrom != null) {
                disconnectFrom.removePlayer(user);
                String userName = HoHMSocket.connectedPlayers.get(user);
                String messageToSend = userName + " disconnected from the lobby!";
                broadcastToAllInALobby(user, disconnectFrom.getId(), messageToSend);
            }
        }
        //handler for sending lines over to the opponent
        else if(messageKey.equals("WORD:")){
            String lobbyName = message.substring(5);
            sendCodeToOpponent(user, lobbyName);
        }
    }

    private void broadcastMessageTo(Session receiver, String message){
        try {
            receiver.getRemote().sendString(message);
        } catch (IOException e) {
            System.out.println("Gre6ka pri sendwane na liniika");
        }
    }

    private void broadcastToAllInALobby(Session sender, String lobbyID, String message){
        for (Session s : HoHMSocket.connectedPlayers.keySet()){
            try {
                if(!s.equals(sender)){
                    s.getRemote().sendString(message);
                }
            } catch (IOException e) {
                System.out.println("Problem pri pra6taneto na wsi4ki w lobito");
                e.printStackTrace();
            }
        }
    }

    private void sendCodeToOpponent(Session sender, String lobbyName){
        String lobbyID = Game.getLobby(lobbyName).getId();

        for (Session s : Game.lobby_participants.get(lobbyID)){
            Random rand = new Random();
            ArrayList<Session> otherPlayers = new ArrayList<Session>();
            if(!s.equals(sender)){
                otherPlayers.add(s);
            }
            int randomIndex = rand.nextInt(otherPlayers.size());
            Session receiver = otherPlayers.get(randomIndex);
            String toSend = rand.nextInt(ServerController.linesSize) + "";
            broadcastMessageTo(receiver, toSend);
        }
    }

}
