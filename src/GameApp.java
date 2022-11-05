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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import java.util.Random;

interface Updatable {
    void update();
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
class GameText extends GameObject {
    Text text;

    public GameText(String text){
        this.text = new Text(text);
        this.text.setScaleY(-1);
        this.text.setFont(Font.font(15));
        this.text.setFill(Color.LEMONCHIFFON);
        add(this.text);
    }
    public void setText(String text){
        this.text.setText(text);
    }
    public void setLoc(Helicopter x){
        this.myRotation = x.myRotation;
        this.myTranslate = x.myTranslate;

    }
}
class Pond extends GameObject{

    int fill;
    Random rand = new Random();
    Circle pond;
    GameText per;
    public Pond(){
        int random = rand.nextInt(25)+10;
        fill =random;
        per = new GameText(String.valueOf(fill));
        pond = new Circle(random,Color.BLUE);
        add(pond);
        this.translate(rand.nextInt(250)+100,rand.nextInt(350)+200);
        per = new GameText(String.valueOf(fill));
        add(per);
        per.setTranslateX(-10);
    }

}
class Cloud extends GameObject{

    Random rand = new Random();
    Circle cloud;
    int water;
    GameText per;
    public Cloud(){
        cloud = new Circle(rand.nextInt(80)+30,Color.WHITE);
        water = 0;
        add(cloud);
        this.translate(rand.nextInt(250)+100,rand.nextInt(350)+200);
        per = new GameText(String.valueOf(water));
        per.text.setFill(Color.BLUE);
        add(per);
        cloud.setOpacity(.8);
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
    double currSpeedY,currSpeedX, vel;
    boolean ignition;
    GameText tfuel;
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
        vel =0;
        fuel = 25000;
        water = 0;
        tfuel = new GameText(String.valueOf(fuel));
        tfuel.setTranslateY(-20);
        tfuel.setTranslateX(-20);
        add(tfuel);
        tfuel.setLoc(this);

    }
    public void UpDown(Boolean x){
        if(ignition) {
            if(x) {
                if(vel < 10)
                    vel += .1;
            }else{
                if(vel > -10)
                    vel += -.2;
            }
        }
    }
    public void Right(){
        myRotation.setAngle((getMyRotation() % 360));
        double rad = Math.toRadians(getMyRotation());
        currSpeedX = vel * Math.sin(rad) * -1;
        currSpeedY = vel * Math.cos(rad);

    }
    public void Left(){
        myRotation.setAngle(getMyRotation() % 360);
        double rad = Math.toRadians(getMyRotation());
        currSpeedX = vel * Math.sin(rad) * -1;
        currSpeedY = vel * Math.cos(rad) ;
    }
    @Override
    public void update() {

        if((int)getMyRotation() != 0) {
            myTranslate.setY(myTranslate.getY() + currSpeedY);
            myTranslate.setX(myTranslate.getX() + currSpeedX);
            tfuel.setLoc(this);

        }else{
            myTranslate.setY(myTranslate.getY() + vel);
            tfuel.setLoc(this);
        }
        if(ignition)
            fuel -= 5;
        tfuel.setText(String.valueOf(fuel));
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
                }
                if(count % 40 == 1) {

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

        init(root);

        //Helicopter temp = (Helicopter) root.getChildren().get(3);
        Scene scene = new Scene(root, GAME_WIDTH, GAME_HEIGHT, Color.BLACK);

        primaryStage.setScene(scene);

        scene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.W){
                ((Helicopter) root.getChildren().get(3)).UpDown(true);
            }
            if(e.getCode() == KeyCode.S){
                ((Helicopter) root.getChildren().get(3)).UpDown(false);
            }
            if(e.getCode() == KeyCode.A){
                ((Helicopter) root.getChildren().get(3)).rotate(((Helicopter) root.getChildren().get(3)).getMyRotation() + 15);
                ((Helicopter) root.getChildren().get(3)).Left();

            }
            if(e.getCode() == KeyCode.D){
                ((Helicopter) root.getChildren().get(3)).rotate(((Helicopter) root.getChildren().get(3)).getMyRotation() - 15);
                ((Helicopter) root.getChildren().get(3)).Right();

            }
            if(e.getCode() == KeyCode.I){
                ((Helicopter) root.getChildren().get(3)).ignition = true;
            }
            if(e.getCode() == KeyCode.R) {
                init(root);
                start(root,((Helicopter) root.getChildren().get(3)));
            }
        });


        start(root,((Helicopter) root.getChildren().get(3)));
        primaryStage.setTitle("Rain Maker");
        // prevent window resizing by user
        primaryStage.setResizable(false);

        // display the Stage
        primaryStage.show();


    } // end of start()

    public void init(Pane root){
        root.getChildren().clear();
        Helipad pad = new Helipad();
        Helicopter boom = new Helicopter();
        Pond x = new Pond();
        Cloud y = new Cloud();
        root.getChildren().addAll(pad,x,y,boom);

        boom.translate(200,50);
        boom.setPivot(200,50);

    }
    public void start(Pane parent, Helicopter heli){
        Game game = new Game(parent,heli);
        game.play();
    }
    public static void main(String[] args) {
        Application.launch();
    }
}
