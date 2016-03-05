package Controller;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import java.util.HashMap;

/**
 * Created by Danail on 3/5/2016.
 */
public class HoHMSocket {

    public static int playerCount = 0;
    public static HashMap<Session, String> connectedPlayers = new HashMap<Session, String>();

    public static void startWebSocket() throws Exception{
        startWebSocket(421);
    }

    public static void startWebSocket(int port){
        Server server = new Server(port);
        WebSocketHandler wsHandler = new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.register(SocketHandler.class);
            }
        };

        server.setHandler(wsHandler);
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            System.out.println("socketa garmi!");
            e.printStackTrace();
        }
    }



}
