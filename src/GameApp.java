import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.Light;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

interface Updatable {
    public void update();
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

}

class Cloud {

}

class PondAndCloud {

}

class Helipad extends GameObject{
    public Helipad(){
        Rectangle base = new Rectangle(100 ,100);
        base.setStroke(Color.YELLOW);
        base.setTranslateY(10);
        base.setTranslateX(150);
        Circle baseIn = new Circle(45);
        baseIn.setTranslateX(200);
        baseIn.setTranslateY(60);
        baseIn.setStroke(Color.WHITE);
        add(base);
        add(baseIn);
    }

    @Override
    public void update() {

    }
}


class Helicopter extends GameObject{
    int   fuel, water;
    double currSpeed;
    public Helicopter(){
        super();
        Circle body = new Circle(20, Color.YELLOW);

        Rectangle point = new Rectangle(5,40,Color.YELLOW);
        point.setTranslateX(-2);

        point.setFill(Color.YELLOW);

        add(body);
        add(point);

        currSpeed = 0;
        fuel = 0;
        water = 0;
    }
    public void move(double x ){
        if(currSpeed <= 10)
            currSpeed += x;

    }
    public double getSpeed(){
        return  currSpeed;
    }
    @Override
    public void update() {

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

            public void handle(long now) {
                heli.translate(0,heli.getSpeed());
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




        Helipad pad = new Helipad();
        root.getChildren().add(pad);
        Helicopter boom = new Helicopter();
        root.getChildren().add(boom);


        Scene scene = new Scene(root, GAME_WIDTH, GAME_HEIGHT, Color.BLACK);


        boom.translate(200,50);

        primaryStage.setScene(scene);
        scene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.W){
                boom.move(.1);
                boom.translate(0,boom.getSpeed());
            }
            if(e.getCode() == KeyCode.S){
                boom.move(-.1);
                boom.translate(0,boom.getSpeed());
            }
            if(e.getCode() == KeyCode.A){
                boom.rotate(15);
            }
            if(e.getCode() == KeyCode.D){
                boom.rotate(-15);
            }
        });


        Game game = new Game(root,boom);
        game.play();
        primaryStage.setTitle("Rain Maker");
        // prevent window resizing by user
        primaryStage.setResizable(false);

        // display the Stage
        primaryStage.show();


    } // end of start()

    public static void main(String[] args) {
        Application.launch();
    }
}
