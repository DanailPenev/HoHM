import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.nio.file.Paths;

/**
 * Created by Ivaylo on 06/03/2016.
 */
public class Sound {


    public static void onWin() {
        new MediaPlayer(new Media(Paths.get("src\\main\\res\\win.mp3").toUri().toString())).play();

    }

    public static void onLose() {
        new MediaPlayer(new Media(Paths.get("src\\main\\res\\lose.mp3").toUri().toString())).play();

    }

    public static void onEnter() {
        new MediaPlayer(new Media(Paths.get("src\\main\\res\\lobby.mp3").toUri().toString())).play();
    }

}
