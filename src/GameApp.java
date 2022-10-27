import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;



interface Updatable {
    public void update();
}

class GameText {

}

abstract class GameObject extends Group {
    protected Translate myTranslate;
    protected Rotate myRotate;
    protected Scale myScale;
    void addT(Node node) {
        myTranslate = new Translate();
        myRotate = new Rotate();
        myScale = new Scale();
        this.getTransforms().addAll(myRotate, myTranslate,myScale);
    }
    public void rotate(double degrees) {
        myRotate.setAngle(myRotate.getAngle() + degrees);
    }

    public void update(double x) {
        myTranslate.setY(myTranslate.getY() + x);
    }

    public void translate(double x, double y) {
        myTranslate.setX(x);
        myTranslate.setY(y);
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
}


class Helicopter extends GameObject{

    public Helicopter(){
        Circle body = new Circle(20, Color.YELLOW);

        Rectangle point = new Rectangle(5,40,Color.YELLOW);
        point.setTranslateX(-2);

        point.setFill(Color.YELLOW);

        add(body);
        add(point);
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

            }
        };
        loop.start();
    }
}

public class GameApp extends Application {
    private static final int GAME_WIDTH = 400;
    private static final int GAME_HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) {

        Pane root = new Pane();
        root.setScaleY(-1);




        Helipad pad = new Helipad();
        root.getChildren().add(pad);
        Helicopter boom = new Helicopter();
        root.getChildren().add(boom);


        Scene scene = new Scene(root, GAME_WIDTH, GAME_HEIGHT, Color.BLACK);

        boom.addT(boom);
        boom.translate(200,50);

        primaryStage.setScene(scene);
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()){
                case A :
                    boom.rotate(15);
                    break;
                case D:
                    boom.rotate(-15);
                    break;
                case W:
                    boom.update(1);
                    break;
                case S:
                    boom.update(-1);
                    break;
            }
        });

        Game game = new Game(root,boom);
        game.play();
        primaryStage.setTitle("GAME_WINDOW_TITLE");
        // prevent window resizing by user
        primaryStage.setResizable(false);

        // display the Stage
        primaryStage.show();


    } // end of start()

    public static void main(String[] args) {
        Application.launch();
    }
}
