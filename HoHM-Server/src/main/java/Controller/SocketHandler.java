package Controller;

import Model.Game;
import Model.Lobby;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import sun.plugin2.message.GetAppletMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


/**
 * Created by Danail on 3/5/2016.
 */

@WebSocket
public class SocketHandler {

    private final String defaultUserName = "Anonymous";
    private int counter = 0;
    private String sendEnteredKeyword = "ENTR:", sendDCedKeyword="DISC:";

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        System.out.println("Connected user, brat");
//        String username = "user" + HoHMSocket.playerCount++;
//        HoHMSocket.connectedPlayers.put(user, username);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        System.out.println("Disconnected ");
        for (Lobby l : ServerController.availableLobbies){
            if (l.getPlayers().remove(user)){
//                l.removePlayer(user);
                System.out.println(l.getPlayers().size());
                broadcastToAllInALobby(l.getId(),"LEFT:"+HoHMSocket.connectedPlayers.get(user));
                if (l.getPlayers().size()<1){
                    ServerController.availableLobbies.remove(l);
                }
            }
        }
        HoHMSocket.connectedPlayers.remove(user);

    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        System.out.println("Message:" + message);
        String messageKey = message.substring(0,5);
        //handler for setting username
        if(messageKey.equals("NAME:")){
            String userName = message.substring(5);
            if (userName.equals("")) {
                userName = defaultUserName + ++counter;
            }
            HoHMSocket.connectedPlayers.put(user, userName);
        }
        //handler for creating a lobby
        else if(messageKey.equals("CRTL:")){
            String host = message.substring(5);
            Lobby newLobby = new Lobby(host);
            newLobby.addPlayer(user);
            ServerController.availableLobbies.add(newLobby);
        }
        //handler for starting a game/lobby
        else if(messageKey.equals("STRT:")){
            String lobbyName = message.substring(5);
            if(!lobbyName.equals("")){
                Game.startGame(lobbyName);
                broadcastToAllInALobby(user, Lobby.findLobbyByName(lobbyName).getId(), "EXEC:");
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
                String messageToSend = sendEnteredKeyword+userName;
                broadcastToAllInALobby(user, connectedTo.getId(), messageToSend);
            }
        }
        //handler for disconnecting from the lobby
//        else if(messageKey.equals("DISC:")){
//            String lobbyToDisconnectName = message.substring(5);
//            Lobby disconnectFrom = Game.getLobby(lobbyToDisconnectName);
//            if (disconnectFrom != null) {
//                disconnectFrom.removePlayer(user);
//                String userName = HoHMSocket.connectedPlayers.get(user);
//                String messageToSend = sendDCedKeyword+userName;
//                if(disconnectFrom.getPlayers().size() > 0){
//                    broadcastToAllInALobby(user, disconnectFrom.getId(), messageToSend);
//                } else {
//                    ServerController.availableLobbies.remove(disconnectFrom);
//                }
//            }
//        }
        //handler for sending lines over to the opponent
        else if(messageKey.equals("WORD:")){
            String lobbyName = message.substring(5);
            sendCodeToOpponent(user, lobbyName);
        }
    }

    //broadcast to a specific user
    private void broadcastMessageTo(Session receiver, String message){
        try {
            receiver.getRemote().sendString(message);
        } catch (IOException e) {
            System.out.println("Gre6ka pri sendwane na liniika");
        }
    }

    //broadcast to everyone in the lobby except for the sender
    private void broadcastToAllInALobby(Session sender, String lobbyID, String message){
        Lobby broadcastTo = Lobby.findLobbyById(lobbyID);
        if(broadcastTo != null){
            for (Session s : broadcastTo.getPlayers()) {
                try {
                    if (!s.equals(sender)) {
                        s.getRemote().sendString(message);
                    }
                } catch (IOException e) {
                    System.out.println("Problem pri pra6taneto na wsi4ki w lobito");
                    e.printStackTrace();
                }
            }
        }
    }

    private void broadcastToAllInALobby(String lobbyID, String message){
        Lobby broadcastTo = Lobby.findLobbyById(lobbyID);
        if(broadcastTo != null){
            for (Session s : broadcastTo.getPlayers()) {
                try {
                    s.getRemote().sendString(message);
                } catch (IOException e) {
                    System.out.println("Problem pri pra6taneto na wsi4ki w lobito");
                    e.printStackTrace();
                }
            }
        }
    }

    //send an integer to someone random in the lobby. Can't be the sender
    private void sendCodeToOpponent(Session sender, String lobbyName){
        String lobbyID = Lobby.findLobbyByName(lobbyName).getId();

        for (Session s : Game.lobby_participants.get(lobbyID)){
            Random rand = new Random();
            ArrayList<Session> otherPlayers = new ArrayList<Session>();
            if(!s.equals(sender)){
                otherPlayers.add(s);
            }
            int randomIndex = rand.nextInt(otherPlayers.size());
            Session receiver = otherPlayers.get(randomIndex);
            String toSend = "LINE:" + rand.nextInt(ServerController.linesSize);
            broadcastMessageTo(receiver, toSend);
        }
    }

}
