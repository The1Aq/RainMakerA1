import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
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
        myScale.setX(myScale.getX() + x);
        myScale.setY(myScale.getY() + y);
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
        per = new GameText(String.valueOf(fill) + "%");
        add(per);
        per.setTranslateX(-10);
    }
    public void seeded(int x){
        if(x > 1){
            if(fill < 100) {
                fill++;
                per.setText(String.valueOf(fill)+ "%");
                scale(.02, .02);
            }
        }

    }
}
class Cloud extends GameObject{
    Random rand = new Random();
    Circle cloud;
    int seed;
    GameText per;
    int r;
    public Cloud(){
        r = rand.nextInt(70)+30;
        cloud = new Circle(r,Color.WHITE);
        seed = 0;
        add(cloud);
        this.translate(rand.nextInt(250)+100,rand.nextInt(350)+200);
        per = new GameText(String.valueOf(seed)+ "%");
        per.text.setFill(Color.BLUE);
        add(per);
        cloud.setOpacity(.8);
    }
    public void seed(){
        if(seed <= 100)
            seed +=1;
        per.setText(String.valueOf(seed));
        if(seed >= 30){
            if(cloud.getOpacity() > .3)
                cloud.setOpacity(cloud.getOpacity()-.005);
        }
    }
    public int  deseed(){
        if(seed >= 1){
            seed --;
            per.setText(String.valueOf(seed)+ "%");
        }
        return seed;
    }
    public int getRadius(){
        return r;
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
class Blades extends GameObject{
    public Blades(){
        Rectangle blade1 = new Rectangle(5,100,Color.GHOSTWHITE);
        add(blade1);
        myRotation.setPivotY(50+ myTranslate.getY());

    }
    public void setLoc(Helicopter x){
        this.myTranslate = x.myTranslate;


    }
    public void OnSpin(){
        this.myRotation.setAngle(getMyRotation() + 15);
    }
}
class Helicopter extends GameObject{
    int   fuel, water;
    double currSpeedY,currSpeedX, vel;
    boolean ignition;
    GameText tfuel;
    boolean ontop;
    Blades blade1;
    public Helicopter(){
        super();


        Rectangle center = new Rectangle(20,20,Color.YELLOW);
        center.setTranslateX(-10);
        center.setTranslateY(-10);
        Ellipse body = new Ellipse(15,25);
        body.setTranslateY(10);
        body.setFill(Color.YELLOW);
        Rectangle tail = new Rectangle(5,20,Color.YELLOW);
        tail.setTranslateY(-30);
        tail.setTranslateX(-2.5);
        Rectangle legR = new Rectangle(5,40,Color.YELLOW);
        legR.setTranslateX(20);
        legR.setTranslateY(-10);
        Rectangle legL = new Rectangle(5,40,Color.YELLOW);
        legL.setTranslateX(-25);
        legL.setTranslateY(-10);
        Rectangle conec1 = new Rectangle(40,2,Color.YELLOW);
        conec1.setTranslateX(-20);
        Rectangle conec2 = new Rectangle(40,2,Color.YELLOW);
        conec2.setTranslateX(-20);
        conec2.setTranslateY(20);
        Ellipse window = new Ellipse(10,5);
        window.setFill(Color.BLUE);
        window.setTranslateY(25);
        add(conec2);
        add(conec1);
        add(legL);
        add(legR);
        add(tail);
        add(body);
        add(center);
        add(window);
        //add(bound);


        ignition = false;
        ontop = false;
        currSpeedX = 0;
        currSpeedY = 0;
        vel =0;
        fuel = 25000;
        water = 0;
        blade1 = new Blades();
        blade1.setTranslateY(-50);
        blade1.setTranslateX(-1);

        add(blade1);
        tfuel = new GameText("F:"+String.valueOf(fuel));
        tfuel.setTranslateY(-30);
        tfuel.setTranslateX(-20);
        add(tfuel);

        blade1.setLoc(this);
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
    }
    public void Left(){
        myRotation.setAngle(getMyRotation() % 360);
    }
    @Override
    public void update() {

        if((int)getMyRotation() != 0) {
            double rad = Math.toRadians(getMyRotation());
            currSpeedX = vel * Math.sin(rad) * -1;
            currSpeedY = vel * Math.cos(rad) ;
            myTranslate.setY(myTranslate.getY() + currSpeedY);
            myTranslate.setX(myTranslate.getX() + currSpeedX);
        }else{
            myTranslate.setY(myTranslate.getY() + vel);
        }
        tfuel.setLoc(this);
        if(ignition) {
            if(vel < 1)
                fuel -= 5;
            else if (vel < 2) {
                fuel -= 25;
            }else if(vel < 3){
                fuel -= 100;
            }else{
                fuel -= 150;
            }
        }
        if(ignition){
            blade1.OnSpin();
        }
        blade1.setLoc(this);

        tfuel.setText("F:"+String.valueOf(fuel));
        setPivot(myTranslate.getX(),myTranslate.getY());

    }
    public void setPivot(double x,double y){
        myRotation.setPivotX(x);
        myRotation.setPivotY(y);
    }

}
class Game extends Pane{

    Helicopter heli;
    Cloud cloud;
    Pond pond;
    public Game(Pane parent) {
       heli = (Helicopter) parent.getChildren().get(3);
       cloud = (Cloud) parent.getChildren().get(2);
       pond = (Pond) parent.getChildren().get(1);
    }
    public void play(){
        AnimationTimer loop = new AnimationTimer() {
            int count = 0;

            public void handle(long now) {
                count++;
                if(count % 5 == 1) {
                    heli.update();
                    heli.rotate(heli.getMyRotation());
                }
                if(count % 60 == 1) {
                    pond.seeded(cloud.deseed());
                }
                if(isTop(heli,cloud)){
                    heli.ontop = true;
                }else{
                    heli.ontop = false;
                }
            }
        };
        loop.start();
    }
    public boolean isTop(Helicopter heli, Cloud cloud){
        return (heli.myTranslate.getX() > (cloud.myTranslate.getX() - cloud.getRadius()) && heli.myTranslate.getX() < (cloud.myTranslate.getX() + cloud.getRadius()))
                && (heli.myTranslate.getY() < cloud.myTranslate.getY() + cloud.getRadius() && heli.myTranslate.getY() > cloud.myTranslate.getY() - cloud.getRadius());
    }
}
public class GameApp extends Application {
    private static final int GAME_WIDTH = 400;
    private static final int GAME_HEIGHT = 800;


    public void start(Stage primaryStage) {

        Pane root = new Pane();
        root.setScaleY(-1);

        init(root);


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
                ((Helicopter) root.getChildren().get(3)).rotate(
                        ((Helicopter) root.getChildren().get(3)).
                                getMyRotation() + 15);
                ((Helicopter) root.getChildren().get(3)).Left();

            }
            if(e.getCode() == KeyCode.D){
                ((Helicopter) root.getChildren().get(3)).rotate(
                        ((Helicopter) root.getChildren().get(3)).
                                getMyRotation() - 15);
                ((Helicopter) root.getChildren().get(3)).Right();

            }
            if(e.getCode() == KeyCode.I){
                ((Helicopter) root.getChildren().get(3)).ignition =
                        !((Helicopter) root.getChildren().get(3)).ignition;
            }
            if(e.getCode() == KeyCode.R) {
                init(root);
                start(root);
            }
            if(e.getCode() == KeyCode.SPACE){
                if(((Helicopter) root.getChildren().get(3)).ontop == true){
                    ((Cloud)root.getChildren().get(2)).seed();
                }
            }
        });


        start(root);
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
    public void start(Pane parent){
        Game game = new Game(parent);
        game.play();
    }
    public static void main(String[] args) {
        Application.launch();
    }
}
