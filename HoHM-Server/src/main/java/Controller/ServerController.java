package Controller;

import Model.Game;
import Model.Lobby;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.Random;

import static spark.Spark.*;

/**
 * Created by Danail on 3/4/2016.
 */
public class ServerController {

    public static int linesSize = 10;
    public static ArrayList<Lobby> availableLobbies = new ArrayList<Lobby>();

    public ServerController(){
        hello();
//        newLobby();
        getLobbies();
//        tryConnection();
    }

    public void hello(){
        get("/helloWorld", new Route(){
            public Object handle(Request request, Response response) throws Exception {
                return "Zdrawei!";
            }
        });
    }

//    public void newLobby(){
//        post("/newLobby/:name", new Route(){
//            public Object handle(Request request, Response response) throws Exception {
//                Player player = new Player(request.params(":name"));
//                Lobby l = new Lobby(player);
//                lobbies.add(l);
//                return new Integer(l.getId()) + "";
//            }
//        });
//
//    }

    public void getLobbies(){
        get("/getLobbies", new Route() {
            public Object handle(Request request, Response response) throws Exception {
                StringBuffer sb = new StringBuffer();
                for (Lobby l : availableLobbies){
                    sb.append(l.getLobbyName());
                    sb.append("\n");
                }
                return sb.toString();
            }
        });
    }

//    public void tryConnection(){
//        post("/tryConnection/:id_name", new Route() {
//            public Object handle(Request request, Response response) throws Exception {
//                String s = request.params(":id_name");
//                int USIndex = s.indexOf("_");
//                String id = s.substring(0, USIndex);
//                String username = s.substring(USIndex + 1);
//                System.out.println(s);
//                System.out.println(id);
//                System.out.println(username);
//                return id;
//            }
//        });
//    }

}
