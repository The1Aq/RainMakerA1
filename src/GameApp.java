import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import java.util.Random;

interface Updatable {
    void update();
}

class GameText {

}

abstract class GameObject extends Group implements Updatable {
    protected Translate myTranslate;
    protected Rotate myRotation;
    protected Scale myScale;

    public GameObject() {
        this.setManaged(false);
        myTranslate = new Translate();
        myRotation = new Rotate();
        myScale = new Scale();
        this.getTransforms().addAll( myRotation, myTranslate,myScale);
    }

    public void rotate(double degrees) {
        myRotation.setAngle(degrees);


    }

    public void translate(double x, double y) {
        myTranslate.setX(myTranslate.getX() + x);
        myTranslate.setY(myTranslate.getY() + y);
    }

    public void scale(double x, double y) {
        myScale.setX(x);
        myScale.setY(y);
    }

    public double getMyRotation() {
        return myRotation.getAngle();
    }

    public void update() {
        for (Node n : getChildren()) {
            if (n instanceof Updatable)
                ((Updatable) n).update();
        }
    }

    void add(Node node) {
        this.getChildren().add(node);
    }
}

class Pond {
    Random rand = new Random();
    Circle pond;
    public Pond(){
        pond = new Circle(10,Color.BLUE);
        pond.setTranslateY(rand.nextInt(700)+100);
        pond.setTranslateX(rand.nextInt(400));
    }

}

class Cloud {
    Random rand = new Random();
    Circle cloud;
    int water = 0;
    public Cloud(){
        cloud = new Circle(rand.nextInt(80)+30,Color.WHITE);
        cloud.setTranslateY(rand.nextInt(700)+100);
        cloud.setTranslateX(rand.nextInt(400));
    }


}

class PondAndCloud {
    Cloud cloud;
    Pond pond;

    public PondAndCloud(Pane root){
        cloud = new Cloud();
        pond = new Pond();
        root.getChildren().addAll(cloud.cloud,pond.pond);
    }
}

class Helipad extends Group{
    public Helipad(){
        Rectangle base = new Rectangle(100 ,100);
        base.setStroke(Color.YELLOW);
        base.setTranslateY(10);
        base.setTranslateX(150);
        Circle baseIn = new Circle(45);
        baseIn.setTranslateX(200);
        baseIn.setTranslateY(60);
        baseIn.setStroke(Color.WHITE);
        this.getChildren().add(base);
        this.getChildren().add(baseIn);
    }
}


class Helicopter extends GameObject{
    int   fuel, water;
    double currSpeedY,currSpeedX;
    boolean ignition;
    public Helicopter(){
        super();
        Circle body = new Circle(20, Color.YELLOW);

        Rectangle point = new Rectangle(5,40,Color.YELLOW);
        point.setTranslateX(-2);

        point.setFill(Color.YELLOW);

        add(body);
        add(point);
        ignition = false;
        currSpeedX = 0;
        currSpeedY = 0;
        fuel = 0;
        water = 0;
    }
    public void move(double x,double y ){
        if(ignition) {
            if (currSpeedY < 10)
                currSpeedY += y;
            if (currSpeedX < 10)
                currSpeedX += x;
        }

    }
    @Override
    public void update() {
        myTranslate.setY(myTranslate.getY() + currSpeedY);
        myTranslate.setX(myTranslate.getX() + currSpeedX);
        setPivot(myTranslate.getX(),myTranslate.getY());
    }
    public void setPivot(double x,double y){
        myRotation.setPivotX(x);
        myRotation.setPivotY(y);
    }
}


class Game {
    Pane game;
    Helicopter heli;
    public Game(Pane parent, Helicopter heli) {
        game = parent;
        this.heli = heli;
    }
    public void play() {
        AnimationTimer loop = new AnimationTimer() {
            int count = 0;
            public void handle(long now) {
                count++;
                if(count % 5 == 1) {
                    heli.update();
                    heli.rotate(heli.getMyRotation());
                    System.out.println(heli.currSpeedY + " y speed");
                    System.out.println(heli.currSpeedX + " X speed");
                }
            }
        };
        loop.start();
    }
}

public class GameApp extends Application {
    private static final int GAME_WIDTH = 400;
    private static final int GAME_HEIGHT = 800;


    public void start(Stage primaryStage) {

        Pane root = new Pane();
        root.setScaleY(-1);
        int rotation = 0;
        init(root);
        init(root);
        Helicopter temp = (Helicopter) root.getChildren().get(3);
        Scene scene = new Scene(root, GAME_WIDTH, GAME_HEIGHT, Color.BLACK);

        primaryStage.setScene(scene);

        scene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.W){
                temp.move(0,.1);
            }
            if(e.getCode() == KeyCode.S){
                temp.move(0,-.1);
            }
            if(e.getCode() == KeyCode.A){
                if(temp.currSpeedY > 0.1)
                    temp.move(-.1,0);
                temp.rotate(temp.getMyRotation() + 15);
            }
            if(e.getCode() == KeyCode.D){
                if(temp.currSpeedY > 0.1)
                    temp.move(.1,0);
                temp.rotate(temp.getMyRotation() - 15);
            }
            if(e.getCode() == KeyCode.I){
                temp.ignition = true;
            }
            if(e.getCode() == KeyCode.R)
                init(root);
        });


        Game game = new Game(root,temp);
        game.play();
        primaryStage.setTitle("Rain Maker");
        // prevent window resizing by user
        primaryStage.setResizable(false);

        // display the Stage
        primaryStage.show();


    } // end of start()
    public static void calMove(int rot, Helicopter heli){
        if(rot == 0) {
            heli.currSpeedX = 0;
            heli.move(0, .1);
        } else if(rot > 0 && rot < 90){
            heli.currSpeedX = -heli.currSpeedY;
            heli.move(-.1,.1);
        } else if (rot == 90) {
            heli.currSpeedY = 0;
            heli.move(-.1,0);
        } else if (rot > 90 && rot < 180) {
            System.out.println("here");
            heli.currSpeedY = heli.currSpeedX;
            heli.move(-.1,-.1);
        } else if (rot == 180) {
            heli.currSpeedX = 0;
            heli.move(0,-.1);
        }else if(rot > 180 && rot < 270){
            heli.currSpeedX = -heli.currSpeedY;
            heli.move(.1,-.1);
        } else if (rot == 270) {
            heli.currSpeedY = 0;
            heli.move(.1,0);
        } else if (rot > 270 && rot < 360) {
            heli.currSpeedY = heli.currSpeedX;
            heli.move(.1,.1);
        }
    }
    public void init(Pane root){
        root.getChildren().clear();
        Helipad pad = new Helipad();
        Helicopter boom = new Helicopter();
        PondAndCloud com = new PondAndCloud(root);
        root.getChildren().addAll(pad,boom);

        boom.translate(200,50);
        boom.setPivot(200,50);

    }
    public static void main(String[] args) {
        Application.launch();
    }
}
