package Model;

/**
 * Created by Danail on 3/4/2016.
 */
public class Player {

    private String name = "Anonymous";

    public Player(String name){
        if (name != null){
            this.name = name;
        }
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}
