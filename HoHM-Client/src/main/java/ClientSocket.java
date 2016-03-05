import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by Danail on 3/5/2016.
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class ClientSocket {
    private final CountDownLatch closeLatch;
    private String username;
    private Main main;
    @SuppressWarnings("unused")
    private Session session;

    public ClientSocket(String username, Main main) {
        this.main = main;
        this.username = username;
        this.closeLatch = new CountDownLatch(1);
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return this.closeLatch.await(duration, unit);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) throws IOException {
        System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
        this.session = null;
        this.closeLatch.countDown();
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.printf("Got connect: %s%n", session);
        this.session = session;
        try {
            Future<Void> fut;
            fut = session.getRemote().sendStringByFuture("NAME:"+username);
            fut.get(2, TimeUnit.SECONDS);
//            session.close(StatusCode.NORMAL, "I'm done");
        } catch (Throwable t) {
            System.out.println("CONNECT ERROR");
            t.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        if(msg.substring(0,5).equals("ENTR:")){
            String newPlayer = msg.substring(5);
            main.notifyHostClientEntered(newPlayer);
            return;
        }
        if(msg.substring(0,5).equals("LEFT:")){
            String otherPlayer = msg.substring(5);
            main.notifyHostClientLeft(otherPlayer);

        }
        if(msg.substring(0,5).equals("EXEC:")){
            main.notifyClientGameStart();
        }
        if(msg.substring(0,5).equals("LINE:")){
            String number = msg.substring(5);
            Integer lineNumber = Integer.parseInt(number);
            main.getInjected(lineNumber);
        }
//        if(msg.substring(0,5).equals("STRT:")){
//            main.notifyClient();
//        }



        System.out.printf("Got msg: %s%n", msg);
    }



    public void broadcastMessage(String message) throws IOException {
        session.getRemote().sendString(message);

    }

}

