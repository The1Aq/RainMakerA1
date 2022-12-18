import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.io.File;
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
    static ArrayList<Point2D> Loc = new ArrayList<>();
    int fill;
    Random rand = new Random();
    Circle pond;
    int x,y;
    int random;
    GameText per;
    public Pond(){
        x = rand.nextInt(650)+100;
        y = rand.nextInt(350)+200;
        random = rand.nextInt(25)+25;
        while(OntopOfEachOther()){
            x = rand.nextInt(650)+100;
            y = rand.nextInt(350)+200;
        }
        Loc.add(new Point2D(x,y));
        fill =50;
        per = new GameText(String.valueOf(fill));
        pond = new Circle(random,Color.BLUE);
        add(pond);
        this.translate(x,y);
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
    public boolean OntopOfEachOther(){
        if(Loc.size() == 0){
            return false;
        }else if(Loc.size() == 1){
            return (!(x > Loc.get(0).getY() + 80) && !(x < Loc.get(0).getX() - 80)) ||
                    (!(y > Loc.get(0).getY() + 80) && !(y < Loc.get(0).getY() - 80));
        }else if(Loc.size() == 2){
            if((x > Loc.get(0).getY() + 80 || x < Loc.get(0).getX() - 80) &&
                    (y > Loc.get(0).getY() + 80 || y < Loc.get(0).getY() - 80)){
                return (!(x > Loc.get(1).getY() + 80) && !(x < Loc.get(1).getX() - 80)) ||
                        (!(y > Loc.get(1).getY() + 80) && !(y < Loc.get(1).getY() - 80));
            }else{
                return true;
            }
        }else
            return true;
    }
}
class BezierOval  extends Path {
    Ellipse x;
    public BezierOval (Ellipse x){
        this.x = x;
        QuadCurveTo  curve1 = new QuadCurveTo ();
        QuadCurveTo curve2 = new QuadCurveTo();
        QuadCurveTo curve3 = new QuadCurveTo();
        QuadCurveTo curve4 = new QuadCurveTo();
        MoveTo start = new MoveTo(0,x.getRadiusY());

        curve1.setX(x.getRadiusX());
        curve1.setY(0);
        curve1.setControlX(x.getRadiusX()+20 * Math.cos(45));
        curve1.setControlY(x.getRadiusY()+20 * Math.sin(45));

        curve2.setX(0);
        curve2.setY(-x.getRadiusY());
        curve2.setControlX(x.getRadiusX()+20 * Math.cos(-45));
        curve2.setControlY(-x.getRadiusY()+20 * Math.sin(-45));

        curve3.setX(-x.getRadiusX());
        curve3.setY(0);
        curve3.setControlX(-x.getRadiusX()+20 * Math.cos(225));
        curve3.setControlY(-x.getRadiusY()+20 * Math.sin(225));

        curve4.setX(0);
        curve4.setY(x.getRadiusY());
        curve4.setControlX(-x.getRadiusX()+20 * Math.cos(135));
        curve4.setControlY(x.getRadiusY()+20 * Math.sin(135));

        this.getElements().add(start);
        this.getElements().add(curve1);
        this.getElements().add(curve2);
        this.getElements().add(curve3);
        this.getElements().add(curve4);
        this.setStroke(Color.BLACK);

    }
}
class Cloud extends GameObject{
    Random rand = new Random();
    boolean oddFive;
    //Circle cloud;
    Ellipse cloud;
    BezierOval  x;
    int seed;
    GameText per;
    int r;
    boolean outOfB;
    double windSpeed;
    public Cloud(){
        cloud = new Ellipse(rand.nextInt(50)+30,rand.nextInt(50)+10);
        cloud.setFill(Color.WHITE);
        x = new BezierOval (cloud);

        x.setFill(Color.WHITE);
        oddFive = false;
        r = rand.nextInt(50)+30;
        ///cloud = new Circle(r,Color.WHITE);
        seed = 0;
        add(cloud);
        add(x);
        this.translate(rand.nextInt(750)+100,rand.nextInt(550)+200);
        per = new GameText(seed + "%");
        per.text.setFill(Color.BLUE);
        add(per);
        cloud.setOpacity(.8);
        windSpeed = 2;
        outOfB = false;
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
    @Override
    public void update() {
        this.myTranslate.setX(this.myTranslate.getX()+windSpeed);
        if(this.myTranslate.getX() - r > 800)
            outOfB = true;
    }
    public void restart(){
        outOfB = false;
        seed = 0;
        cloud.setOpacity(.8);
        //cloud.setRadius(rand.nextInt(50)+30);
        if(oddFive){
            this.myTranslate.setX((rand.nextInt(300)+50)*(-1));
            this.myTranslate.setY(rand.nextInt(550)+200);
        }else {
            this.myTranslate.setX(-r);
            this.myTranslate.setY(rand.nextInt(550) + 200);
        }

        windSpeed = rand.nextDouble(2)+0.5;


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
class Helipad extends Pane{
    Image pad;
    public Helipad(){
        pad = new Image("pad.png");
        ImageView pads = new ImageView(pad);
        pads.setFitWidth(100);
        pads.setFitHeight(100);
        this.getChildren().add(pads);
        pads.setTranslateX(350);
        pads.setTranslateY(10);

    }
}
class HeloBlade  extends GameObject{
    int bladeSpeed;
    boolean on;
    public HeloBlade (){

        Rectangle blade1 = new Rectangle(5,100,Color.GHOSTWHITE);
        Rectangle blade2 = new Rectangle(5,100,Color.GHOSTWHITE);
        add(blade2);
        add(blade1);
        blade2.setRotate(90);
        myRotation.setPivotY(50 + myTranslate.getY());
        myRotation.setPivotX(2.5 + myTranslate.getX());
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
        return bladeSpeed == 30;
    }
}
class Helicopter extends GameObject{
    int   fuel, water;
    double currSpeedY,currSpeedX, vel;
    boolean ignition,off, starting, stopping,ready;
    GameText tfuel;
    boolean ontop1,ontop2,ontop3,ontop4,ontop5;
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
        ontop4 = false;
        ontop5 = false;
        currSpeedX = 0;
        currSpeedY = 0;
        vel =0;
        fuel = 1500;
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
            if(vel < 1)
                fuel -= 5;
            else if (vel < 2) {
                fuel -= 25;
            }else if(vel < 3){
                fuel -= 100;
            }else{
                fuel -= 150;
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

        return this.myTranslate.getX() > (350) && this.myTranslate.getX() < (450)
                && this.myTranslate.getY() < (110) && this.myTranslate.getY() > 10;
    }


}
class Game {
    music mus = new music();
    Helicopter heli;
    ArrayList<Cloud> clouds = new ArrayList<>();
    ArrayList<Pond> ponds = new ArrayList<>();
    ArrayList<Lines> lines = new ArrayList<>();
    popUp pop;

    public Game(Pane parent, popUp pop) {
       heli = (Helicopter) parent.getChildren().get(4);
       populate(parent);
       this.pop = pop;
       mus.playBack();
    }
    public void play(){
        clouds.get(3).oddFive = true;
        clouds.get(4).oddFive = true;
        AnimationTimer loop = new AnimationTimer() {
            int count = 0;

            public void handle(long now) {
                count++;
                if(count % 5 == 1) {
                    heli.update();
                    heli.rotate(heli.getMyRotation());
                }
                if(heli.fuel < 0)
                    mus.fail();


                if(heli.fuel < 1000){
                    mus.runningLow();
                }else{
                    mus.playBack();
                }

                if(count % 10 == 1 ){
                    for (Cloud cloud : clouds) {
                        cloud.update();
                        if(cloud.outOfB)
                            cloud.restart();
                    }
                    for(Lines line : lines){
                        line.update();
                    }

                }
                if(count % 60 == 1) {
                    deseed();
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
                if(isTop(heli,clouds.get(3))){
                    heli.ontop4 = true;
                }else{
                    heli.ontop4 = false;
                }
                if(isTop(heli,clouds.get(4))){
                    heli.ontop5 = true;
                }else{
                    heli.ontop5 = false;
                }
                pop.winOrLose();


            }
            public void deseed(){
                if(lines.get(0).Lac0)
                    ponds.get(0).seeded(clouds.get(0).deseed());
                if(lines.get(0).Lac1)
                    ponds.get(1).seeded(clouds.get(0).deseed());
                if(lines.get(0).Lac2)
                    ponds.get(2).seeded(clouds.get(0).deseed());
                if(lines.get(1).Lac0)
                    ponds.get(0).seeded(clouds.get(1).deseed());
                if(lines.get(1).Lac1)
                    ponds.get(1).seeded(clouds.get(1).deseed());
                if(lines.get(1).Lac2)
                    ponds.get(2).seeded(clouds.get(1).deseed());
                if(lines.get(2).Lac0)
                    ponds.get(0).seeded(clouds.get(2).deseed());
                if(lines.get(2).Lac1)
                    ponds.get(1).seeded(clouds.get(2).deseed());
                if(lines.get(2).Lac2)
                    ponds.get(2).seeded(clouds.get(2).deseed());

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
        clouds.add(((Cloud)((Pane)parent.getChildren().get(3))
                .getChildren().get(3)));
        clouds.add(((Cloud)((Pane)parent.getChildren().get(3))
                .getChildren().get(4)));
        lines.add(new Lines(ponds,clouds.get(0)));
        lines.add(new Lines(ponds,clouds.get(1)));
        lines.add(new Lines(ponds,clouds.get(2)));
        lines.add(new Lines(ponds,clouds.get(3)));
        lines.add(new Lines(ponds,clouds.get(4)));
        parent.getChildren().addAll(lines.get(0),lines.get(1),lines.get(2),
                lines.get(3),lines.get(4));

    }
    public void inVis(){
        lines.get(0).Visible();
        lines.get(1).Visible();
        lines.get(2).Visible();
        lines.get(3).Visible();
        lines.get(4).Visible();
    }
    public int pondAvg(){
        int total = 0;
        for (Pond pond : ponds) {
            total += pond.fill;
        }
        return total/ponds.size();
    }
}
class Lines extends Pane{

    ArrayList<Pond> ponds;
    Cloud c;
    Line line0;
    Line line1;
    Line line2;
    boolean Lac0, Lac1, Lac2;
    public Lines(ArrayList<Pond> p, Cloud c){
        this.ponds = p;
        this.c = c;
        line0 = new Line(this.ponds.get(0).myTranslate.getX(),this.ponds.get(0).myTranslate.getY(),this.c.myTranslate.getX(),this.c.myTranslate.getY());
        this.getChildren().add(line0);
        line1 = new Line(this.ponds.get(1).myTranslate.getX(),this.ponds.get(1).myTranslate.getY(),this.c.myTranslate.getX(),this.c.myTranslate.getY());
        this.getChildren().add(line1);
        line2 = new Line(this.ponds.get(2).myTranslate.getX(),this.ponds.get(2).myTranslate.getY(),this.c.myTranslate.getX(),this.c.myTranslate.getY());
        this.getChildren().add(line2);
        Lac0 = false;
        Lac1 = false;
        Lac2 = false;
    }
    public void update(){
        line0.setEndX(this.c.myTranslate.getX());
        line0.setEndY(this.c.myTranslate.getY());
        line1.setEndX(this.c.myTranslate.getX());
        line1.setEndY(this.c.myTranslate.getY());
        line2.setEndX(this.c.myTranslate.getX());
        line2.setEndY(this.c.myTranslate.getY());
        if(pInRange(this.ponds.get(0), line0)) {
            line0.setStroke(Color.WHITE);
            Lac0 = true;
        }
        else {
            line0.setStroke(Color.TRANSPARENT);
            Lac0 = false;
        }
        if(pInRange(this.ponds.get(1), line1)) {
            line1.setStroke(Color.WHITE);
            Lac1 = true;
        }
        else {
            line1.setStroke(Color.TRANSPARENT);
            Lac1 = false;
        }
        if(pInRange(this.ponds.get(2), line2)) {
            line2.setStroke(Color.WHITE);
            Lac2 = true;
        }
        else {
            line2.setStroke(Color.TRANSPARENT);
            Lac2 = false;
        }


    }
    public void Visible(){
        line0.setVisible(!line0.isVisible());
        line1.setVisible(!line1.isVisible());
        line2.setVisible(!line2.isVisible());
    }
    public boolean pInRange(Pond p,Line line){
        double dis = Math.sqrt(Math.pow(line.getEndX()-line.getStartX(),2)
                +Math.pow(line.getEndY()-line.getStartY(),2));
        return dis < p.pond.getRadius() * 8;
    }

}
class backGround extends Pane{
    Image backGround;
    public backGround(){
        backGround = new Image("land.jpg");
        ImageView back = new ImageView(backGround);
        back.setFitHeight(800);
        back.setFitWidth(800);
        this.getChildren().add(back);
    }
}
class music{
    String mus1 = "BackGround.mp3";
    String mus2 = "Running.mp3";
    String fall = "falling.mp3";
    Media background = new Media(new File(mus1).toURI().toString());
    Media running = new Media(new File(mus2).toURI().toString());
    Media falling = new Media(new File(fall).toURI().toString());
    MediaPlayer player;
    MediaPlayer player2;
    MediaPlayer player3;
    public music(){
        player = new MediaPlayer(background);
        player2 = new MediaPlayer(running);
        player3 = new MediaPlayer(falling);
    }
    public void playBack(){
        player.play();
    }
    public void runningLow(){
        player.setMute(true);
        player2.play();
    }
    public void fail(){
        player.setMute(true);
        player2.setMute(true);
        player3.play();
    }
}
class popUp {
    Alert pop = new Alert(Alert.AlertType.CONFIRMATION, "kanye was right",
            ButtonType.YES, ButtonType.NO);
    GameApp x;

    public popUp(GameApp x){
       this.x = x;
    }
    public void winOrLose(){

        if(x.heli.fuel < 0) {
            pop.setContentText("Better luck next time!!!!, try again?");
            pop.show();
            pop.setOnHidden(evt -> {

                if (pop.getResult() == ButtonType.YES) {
                    x.init(x.root);
                    x.start(x.root);
                    pop.close();
                }
                if(pop.getResult() == ButtonType.NO) {
                    System.exit(0);
                }

            });
        }else if(x.game.pondAvg() >= 80 && x.heli.inHelipad() && !x.heli.ignition){
            pop.setContentText("Great job!!!!, try again?");
            pop.show();
            pop.setOnHidden(evt -> {
                if (pop.getResult() == ButtonType.YES) {
                    x.init(x.root);
                    x.start(x.root);
                    pop.close();
                }
                if(pop.getResult() == ButtonType.NO) {
                    System.exit(0);
                }
            });
        }
    }
}
public class GameApp extends Application {
    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 800;
    Game game;
    Helicopter heli;
    Pane root;
    popUp pop;
    public void start(Stage primaryStage) {

        root = new Pane();
        root.setScaleY(-1);


        init(root);
        pop = new popUp(this);
        Scene scene = new Scene(root, GAME_WIDTH, GAME_HEIGHT);


        primaryStage.setScene(scene);
        heli = ((Helicopter) root.getChildren().get(4));

        scene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.UP){
                heli.UpDown(true);
            }
            if(e.getCode() == KeyCode.DOWN){
                heli.UpDown(false);
            }
            if(e.getCode() == KeyCode.LEFT){
                heli.rotate(heli.getMyRotation() + 15);
                heli.Left();
            }
            if(e.getCode() == KeyCode.RIGHT){
                heli.rotate(heli.getMyRotation() - 15);
                heli.Right();
            }
            if(e.getCode() == KeyCode.I){
                if(heli.inHelipad())
                    heli.ignition = !heli.ignition;
            }
            if(e.getCode() == KeyCode.R ) {
                Pond.Loc.clear();
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
                if(heli.ontop4){
                    ((Cloud)((Pane)root.getChildren().get(3)).getChildren().get(3)).seed();
                }
                if(heli.ontop5){
                    ((Cloud)((Pane)root.getChildren().get(3)).getChildren().get(4)).seed();
                }
            }
            if(e.getCode() == KeyCode.D){
                game.inVis();
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
                new Pane(new Cloud(),new Cloud(),new Cloud(),new Cloud(),new Cloud()),boom);

        boom.translate(400,50);
        boom.setPivot(400,50);
        heli = boom;
    }
    public void start(Pane parent){
        game = null;
        game = new Game(parent, pop);
        game.play();
        game.inVis();
    }
    public static void main(String[] args) {
        Application.launch();
    }
}
