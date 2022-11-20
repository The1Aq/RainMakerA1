import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.util.ArrayList;
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
        per = new GameText(fill + "%");
        add(per);
        per.setTranslateX(-10);
    }
    public void seeded(int x){
        if(x >= 1){
            if(fill < 100) {
                fill++;
                per.setText(fill+ "%");
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
        r = rand.nextInt(50)+30;
        cloud = new Circle(r,Color.WHITE);
        seed = 0;
        add(cloud);
        this.translate(rand.nextInt(250)+100,rand.nextInt(550)+200);
        per = new GameText(seed + "%");
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
            per.setText(seed + "%");
        }
        return seed;
    }
    public int getRadius(){
        return r;
    }

}
class HeloBody extends Group{
    public HeloBody(){
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
        this.getChildren().add(conec2);
        this.getChildren().add(conec1);
        this.getChildren().add(legL);
        this.getChildren().add(legR);
        this.getChildren().add(tail);
        this.getChildren().add(body);
        this.getChildren().add(center);
        this.getChildren().add(window);
    }
}
class Helipad extends Group{
    public Helipad(){
        Rectangle base = new Rectangle(100 ,100);
        base.setStroke(Color.YELLOW);
        base.setY(10);
        base.setX(150);
        Circle baseIn = new Circle(45);
        baseIn.setTranslateX(200);
        baseIn.setTranslateY(60);
        baseIn.setStroke(Color.WHITE);
        this.getChildren().add(base);
        this.getChildren().add(baseIn);
    }
}
class HeloBlade  extends GameObject{
    int bladeSpeed;
    boolean on;
    public HeloBlade (){

        Rectangle blade1 = new Rectangle(5,100,Color.GHOSTWHITE);
        add(blade1);
        myRotation.setPivotY(50+ myTranslate.getY());
        bladeSpeed = 0;
        on = false;

    }
    public void setLoc(Helicopter x){
        this.myTranslate = x.myTranslate;
    }

    public void update(){
        if(bladeSpeed <= 30 && on)
            bladeSpeed++;
        else if(bladeSpeed > 0 && !on)
            bladeSpeed--;
        this.myRotation.setAngle(getMyRotation() + bladeSpeed);
    }
    public boolean maxMin(){
        return bladeSpeed == 30 || bladeSpeed == 0;
    }
}
class Helicopter extends GameObject{
    int   fuel, water;
    double currSpeedY,currSpeedX, vel;
    boolean ignition,off, starting, stopping,ready;
    GameText tfuel;
    boolean ontop1,ontop2,ontop3;
    HeloBlade blade;
    public Helicopter(){
        super();

        add(new HeloBody());
        off = true;
        starting = false;
        stopping = false;
        ready = false;
        ignition = false;
        ontop1 = false;
        ontop2 = false;
        ontop3 = false;
        currSpeedX = 0;
        currSpeedY = 0;
        vel =0;
        fuel = 25000;
        water = 0;
        blade = new HeloBlade ();
        blade.setTranslateY(-50);
        blade.setTranslateX(-1);

        add(blade);
        tfuel = new GameText("F:" + fuel);
        tfuel.setTranslateY(-30);
        tfuel.setTranslateX(-20);
        add(tfuel);

        blade.setLoc(this);
        tfuel.setLoc(this);

    }
    public void UpDown(Boolean x){
        if(ignition) {
            if(x) {
                if(vel < 10)
                    vel += .1;
            }else{
                if(vel > -10)
                    vel -= .2;
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
        if(inHelipad() && off && ignition){
            off = false;
            starting = true;
        }else if(starting && ignition){
            blade.on = ignition;

            if(blade.maxMin()) {
                ready = true;
                starting = false;
            }
        }else if((ready || starting) && !ignition && inHelipad()){
            ready = false;
            starting = false;
            stopping = true;
            blade.on = ignition;
            blade.setLoc(this);
        }else if (stopping && !ignition) {
            off = true;
        }else if(ready && ignition ){
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
            if((int)getMyRotation() != 0) {
                double rad = Math.toRadians(getMyRotation());
                currSpeedX = vel * Math.sin(rad) * -1;
                currSpeedY = vel * Math.cos(rad) ;
                myTranslate.setY(myTranslate.getY() + currSpeedY);
                myTranslate.setX(myTranslate.getX() + currSpeedX);
            }else{
                myTranslate.setY(myTranslate.getY() + vel);
            }
            blade.on = ignition;
            blade.setLoc(this);
        }
        blade.update();
        blade.setLoc(this);
        tfuel.setLoc(this);
        tfuel.setText("F:" + fuel);
        setPivot(myTranslate.getX(),myTranslate.getY());
    }
    public void setPivot(double x,double y){
        myRotation.setPivotX(x);
        myRotation.setPivotY(y);
    }
    public boolean inHelipad(){
        return this.myTranslate.getX() > (150) && this.myTranslate.getX() < (250)
                && this.myTranslate.getY() < (110) && this.myTranslate.getY() > 10;
    }

}
class Game extends Pane{

    Helicopter heli;
    ArrayList<Cloud> clouds = new ArrayList<>();
    ArrayList<Pond> ponds = new ArrayList<>();
    public Game(Pane parent) {
       heli = (Helicopter) parent.getChildren().get(4);
       populate(parent);
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
                    ponds.get(0).seeded(clouds.get(0).deseed());
                    ponds.get(1).seeded(clouds.get(1).deseed());
                    ponds.get(2).seeded(clouds.get(2).deseed());
                }
                if(isTop(heli,clouds.get(0))){
                    heli.ontop1 = true;
                }else{
                    heli.ontop1 = false;
                }
                if(isTop(heli,clouds.get(1))){
                    heli.ontop2 = true;
                }else{
                    heli.ontop2 = false;
                }
                if(isTop(heli,clouds.get(2))){
                    heli.ontop3 = true;
                }else{
                    heli.ontop3 = false;
                }
            }
        };
        loop.start();
    }
    public boolean isTop(Helicopter heli, Cloud cloud){
        return (heli.myTranslate.getX() > (cloud.myTranslate.getX() -
                cloud.getRadius()) && heli.myTranslate.getX()
                < (cloud.myTranslate.getX() + cloud.getRadius()))
                && (heli.myTranslate.getY() < cloud.myTranslate.getY()
                + cloud.getRadius() && heli.myTranslate.getY()
                > cloud.myTranslate.getY() - cloud.getRadius());
    }
    public void populate(Pane parent){

        ponds.add(((Pond)((Pane)parent.getChildren().get(2))
                .getChildren().get(0)));
        ponds.add(((Pond)((Pane)parent.getChildren().get(2))
                .getChildren().get(1)));
        ponds.add(((Pond)((Pane)parent.getChildren().get(2))
                .getChildren().get(2)));

        clouds.add(((Cloud)((Pane)parent.getChildren().get(3))
                .getChildren().get(0)));
        clouds.add(((Cloud)((Pane)parent.getChildren().get(3))
                .getChildren().get(1)));
        clouds.add(((Cloud)((Pane)parent.getChildren().get(3))
                .getChildren().get(2)));
    }
}
class backGround extends Pane{
    Image backGround;
    public backGround(){
        backGround = new Image("land.jpg");
        ImageView back = new ImageView(backGround);
        back.setFitHeight(800);
        back.setFitWidth(400);
        this.getChildren().add(back);
    }
}
public class GameApp extends Application {
    private static final int GAME_WIDTH = 400;
    private static final int GAME_HEIGHT = 800;


    public void start(Stage primaryStage) {

        Pane root = new Pane();
        root.setScaleY(-1);


        init(root);


        Scene scene = new Scene(root, GAME_WIDTH, GAME_HEIGHT);


        primaryStage.setScene(scene);
        Helicopter heli = ((Helicopter) root.getChildren().get(4));
        scene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.W){
                heli.UpDown(true);
            }
            if(e.getCode() == KeyCode.S){
                heli.UpDown(false);
            }
            if(e.getCode() == KeyCode.A){
                heli.rotate(heli.getMyRotation() + 15);
                heli.Left();
            }
            if(e.getCode() == KeyCode.D){
                heli.rotate(heli.getMyRotation() - 15);
                heli.Right();
            }
            if(e.getCode() == KeyCode.I){
                if(heli.inHelipad())
                    heli.ignition = !heli.ignition;
            }
            if(e.getCode() == KeyCode.R) {
                init(root);
                start(root);
            }
            if(e.getCode() == KeyCode.SPACE){
                if(heli.ontop1){
                    ((Cloud)((Pane)root.getChildren().get(3)).getChildren().get(0)).seed();
                }
                if(heli.ontop2){
                    ((Cloud)((Pane)root.getChildren().get(3)).getChildren().get(1)).seed();
                }
                if(heli.ontop3){
                    ((Cloud)((Pane)root.getChildren().get(3)).getChildren().get(2)).seed();
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
        backGround back = new backGround();
        root.getChildren().addAll(back,pad,new Pane(new Pond(),new Pond(),new Pond()),
                new Pane(new Cloud(),new Cloud(),new Cloud()),boom);

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
