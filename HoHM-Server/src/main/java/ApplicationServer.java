import Controller.ServerController;

import java.net.UnknownHostException;

import static Controller.HoHMSocket.startWebSocket;
import static spark.Spark.port;

/**
 * Created by Danail on 3/4/2016.
 */
public class ApplicationServer {
    public static void main(String[] args) throws Exception {
        port(420);
        new ServerController();
        startWebSocket();


    }
}
