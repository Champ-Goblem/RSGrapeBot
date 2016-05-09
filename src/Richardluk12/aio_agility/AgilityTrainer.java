package Richardluk12.aio_agility;

/**
 * Created by Rich on 4/19/2016.
 */

import org.powerbot.script.*;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.*;
import org.powerbot.script.rt6.Component;

import java.awt.*;
import java.util.concurrent.Callable;

import static org.powerbot.script.rt6.Constants.SKILLS_AGILITY;

@Script.Manifest(name="AIOAgility",
        description="Trains Agility at any location",
        properties="author=Richardluk12;" +
                "topic=1309370;" +
                "version=2.01")

public class AgilityTrainer extends PollingScript<ClientContext> implements MessageListener,PaintListener {

    public static final Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);

    public int startXP;
    public int laps;

    public static final int[] LOG_BALANCE_BOUNDS = {-128, 128, -256, 0, -128, 128};
    public static final int[] TREE_BRANCH_BOUNDS = {-50, 50, -128, -256, -128, 128};
    public static final int[] OBSTACLE_NET_BOUNDS = {-128, 128, -256, -256, -128, 128};
    public static final int[] BALANCING_ROPE_BOUNDS = {0, 0, 0, 0, 0, 0};
    public static final int[] OBSTACLE_PIPE_BOUNDS = {-128, 128, -256, 0, -128, 128};
    public static final int[] BASE_BOUNDS = {-128, 128, -256, 0, -128, 128};
    public static final int[] ROPE_SWING_BOUNDS = {0, 0, -256, 0, 0, 0};

    public static final Tile[] WIDERNESS_PIT = {
            new Tile(2998, 10345, 0), new Tile(3003, 10345, 0),
            new Tile(3003, 10350, 0), new Tile(3003, 10355, 0),
            new Tile(3005, 10360, 0), new Tile(3003, 10362, 0)
    };

    private static final int[] FOOD_ID = {379, 365, 329, 373, 385, 473, 7946, 15272};


    public Course[] Courses = {
            new Course( "Gnome Tree",
                new Tile(2408, 3352, 0), 1, 3000,
                new Obstacle("Walk-across", "Log balance", 69526, LOG_BALANCE_BOUNDS, new FAIL(false), new Tile(2474, 3429, 0)),
                new Obstacle("Climb-over", "Obstacle net", 69383, OBSTACLE_NET_BOUNDS , new FAIL(false),
                        new Tile(2476, 3423, 1),new Tile(2475, 3423, 1),
                        new Tile(2474, 3423, 1),new Tile(2473, 3423, 1),
                        new Tile(2472, 3423, 1),new Tile(2471, 3423, 1)),
                new Obstacle("Climb", "Tree branch", 69508, TREE_BRANCH_BOUNDS, new FAIL(false), new Tile(2473, 3420, 2)),
                new Obstacle("Walk-on", "Balancing rope",2312, BALANCING_ROPE_BOUNDS, new FAIL(false), new Tile(2483, 3420, 2)),
                new Obstacle("Climb-down", "Tree branch", 69163,TREE_BRANCH_BOUNDS, new FAIL(false), new Tile(2487, 3417, 0)),
                new Obstacle("Climb-over", "Obstacle net", 69384, OBSTACLE_NET_BOUNDS, new FAIL(false),
                        new Tile(2483, 3427, 0), new Tile(2484, 3427, 0),
                        new Tile(2485, 3427, 0), new Tile(2486, 3427, 0),
                        new Tile(2487, 3427, 0), new Tile(2488, 3427, 0)),
                new Obstacle("Squeeze-through", "Obstacle pipe", 69378, OBSTACLE_PIPE_BOUNDS, new FAIL(false), new Tile(2483, 3437, 0))
            ),
            new Course("Watch Tower",
                    new Tile(2496, 3064, 0), 18, 2000,
                    new Obstacle("Climb-up", "Trellis", 20056, BASE_BOUNDS, new FAIL(false), new Tile(2548, 3117, 1)),
                    new Obstacle("Climb-down", "Ladder", 17122, BASE_BOUNDS, new FAIL(false), new Tile(2544, 3112, 0))
            ),
            new Course("Barbarian Outpost",
                    new Tile(2496, 3496, 0), 35, 5000,
                    new Obstacle("Swing-on", "Rope swing", 43526, ROPE_SWING_BOUNDS,
                            new FAIL(true,
                                    "NONE",
                                    "you skillfully swing across",
                                    "you slip and fall to the pit below",
                                    new Obstacle("Climb-up", "Ladder", 32015, BASE_BOUNDS, new FAIL(false), new Tile(2548, 3551, 0))),
                            new Tile(2551, 3549, 0), new Tile(2552, 3549, 0)),
                    new Obstacle("Walk-across", "Log balance", 43595, LOG_BALANCE_BOUNDS,
                            new FAIL(true,
                                    "you walk carefully across the slippery log",
                                    "you lose your footing and fall into the water",
                                    "and make it safely to the other side",
                                    new Obstacle("Walk-across", "Log balance", 43595, LOG_BALANCE_BOUNDS, new FAIL(false), new Tile(2548, 3551, 0))),
                            new Tile(2539, 3545, 0), new Tile(2539, 3546, 0), new Tile(2539, 3547, 0),
                            new Tile(2540, 3545, 0), new Tile(2540, 3546, 0), new Tile(2540, 3547, 0),
                            new Tile(2541, 3545, 0), new Tile(2541, 3546, 0), new Tile(2541, 3547, 0)),
                    new Obstacle("Climb over", "Obstacle net", 20211, OBSTACLE_NET_BOUNDS,
                            new FAIL(false),
                            new Tile(2537, 3546, 1)),
                    new Obstacle("Walk-across", "Balancing ledge", 2302, BASE_BOUNDS,
                            new FAIL(true,
                                    "you put your foot on the ledge and try to edge across",
                                    "you slip and fall onto the spikes below",
                                    "you skilfully edge across the gap",
                                    new Obstacle("Climb over", "Obstacle net", 20211, OBSTACLE_NET_BOUNDS, new FAIL(false), new Tile(2537, 3546, 1))),
                            new Tile(2532, 3546, 1)),
                    new Obstacle("Climb-down", "Ladder", 3205, BASE_BOUNDS,
                            new FAIL(false),
                            new Tile(2532, 3546, 0)),
                    new Obstacle("Climb-over", "Crumbling wall", 1948, BASE_BOUNDS,
                            new FAIL(false),
                            new Tile(2538, 3553, 0)),
                    new Obstacle("Climb-over", "Crumbling wall", 1948, BASE_BOUNDS,
                            new FAIL(false),
                            new Tile(2543, 3553, 0))
            ),
            new Course("Wilderness",
                    new Tile(2968, 3864, 0), 52, 10000,
                    new Obstacle("Squeeze-through", "Obstacle pipe", 65362, OBSTACLE_PIPE_BOUNDS,
                            new FAIL(false),
                            new Tile(3004, 3950, 0)),
                    new Obstacle("Swing-on", "Ropeswing", 64696, BASE_BOUNDS,
                            new FAIL(true,
                                    "NONE",
                                    "you slip and fall to the pit below",
                                    "you skilfully swing across",
                                    new Obstacle("Climb-up", "Ladder", 32015, BASE_BOUNDS, new FAIL(false),
                                            new Tile(3005, 3962, 0))),
                            new Tile(3005, 3958, 0)),
                    new Obstacle("Cross", "Stepping stone", 64699, BASE_BOUNDS,
                            new FAIL(true,
                                    "you carefully start crossing the stepping stones",
                                    "but you lose your footing and fall into the lava",
                                    "and reach the other side safely",
                                    new Obstacle("Cross", "Stepping stone", 64699, BASE_BOUNDS, new FAIL(false),
                                            new Tile(2996, 3960, 0))),
                            new Tile(2996, 3960, 0)),
                    new Obstacle("Walk-across", "Balancing ledge", 64698, BASE_BOUNDS,
                            new FAIL(true,
                                    "you walk carefully across the slippery log",
                                    "you slip and fall onto the spikes below",
                                    "you skilfully edge across the gap",
                                    new Obstacle("Climb-up", "Ladder", 32015, BASE_BOUNDS, new FAIL(false),
                                            new Tile(3005, 3962, 0))),
                            new Tile(2994, 3945, 0)),
                    new Obstacle("Climb", "Cliffside", 65734, OBSTACLE_PIPE_BOUNDS,
                            new FAIL(false),
                            new Tile(2993, 3935, 0))


                    ),
            new Course("Advanced Barbarian Outpost",
                    new Tile(2496, 3496, 0), 90, 5000)
    };

    public Course current_course;

    public Game gameStats = new Game(ctx);
    public Skills myStats = new Skills(ctx);
    public int next_course_level, current_xp;
    public Timer t = new Timer(10000);
    public static boolean condition;
    public static final Area failed_bo = new Area(new Tile(2533, 3548, 0), new Tile(2538, 3544, 0));
    public TilePath path2ladder;

    public static final int ENERGY_COMPONENT = 1465;
    public static final int REST_ANIMATION = 17301;
    public static final int REST_COMPONENT = 51;
    public AgilityTrainer(){

    }

    @Override
    public void start(){
        current_course = getCourse();
        current_course.getAction(ctx.players.local().tile());
        startXP = myStats.experience(SKILLS_AGILITY);
        laps = 0;
        next_course_level = getBestCourse();
        path2ladder = new TilePath(ctx, WIDERNESS_PIT);
        condition = false;
        current_xp = myStats.experience(SKILLS_AGILITY);
    }


    @Override
    public void messaged(MessageEvent e) {
        final String msg = e.text().toLowerCase();
        if(msg.contains("can't") || msg.contains("cannot")){
            current_course.current_action++;
            if (current_course.current_action >= current_course.actions.length) {
                current_course.current_action = 0;
            }
            current_course.actions[current_course.current_action].doing = false;
        }
        if(!current_course.actions[current_course.current_action].canFail.failable){
            return;
        }

        if(e.source().isEmpty()){
            if(msg.contains(current_course.actions[current_course.current_action].canFail.successMessage)){
                current_course.actions[current_course.current_action].completed = true;
                current_course.actions[current_course.current_action].doing = false;
            } else if(msg.contains(current_course.actions[current_course.current_action].canFail.failMessage)){
                current_course.actions[current_course.current_action].failed = true;
                current_course.actions[current_course.current_action].doing = false;
            } else if(msg.contains(current_course.actions[current_course.current_action].canFail.startMessage)){
                current_course.actions[current_course.current_action].doing = true;
            }
        }
    }

    @Override
    public void repaint(Graphics graphics) {

        final Graphics2D g = (Graphics2D) graphics;
        g.setFont(TAHOMA);

        g.setColor(Color.WHITE);

        if(current_course != null) {
            if(current_course.current_action < 0) current_course.current_action = 0;
        }
        if(!t.isRunning()){
            current_course.actions[current_course.current_action].doing = false;
        }

        if(myStats.experience(SKILLS_AGILITY) - current_xp != 0){
            if(current_course != null) {
                current_course.actions[current_course.current_action].doing = false;
                current_course.actions[current_course.current_action].failed = false;
                current_course.current_action++;
                if (current_course.current_action >= current_course.actions.length) {
                    current_course.current_action = 0;
                }
                current_xp = myStats.experience(SKILLS_AGILITY);
            }
        }
        final int totalXP = myStats.experience(SKILLS_AGILITY) - startXP;
        final int XPHr = (int) ((totalXP * 3600000D) / getTotalRuntime());
        final int lapsHr = (int) ((laps * 3600000D) / getTotalRuntime());
        long time_til_next = 0;
        final int xp_left = (myStats.experienceAt(next_course_level) - myStats.experience(SKILLS_AGILITY));
        if(XPHr > 0) {
             time_til_next = (long) ( ((double) xp_left / (double) XPHr) * 3600000D);
        }
        if(current_course != null) {
            g.drawString("AIOAgility 2.01", 10, 20);
            g.drawString(String.format("Course: %s", current_course.name), 10, 40);
            g.drawString(String.format("Action: %s", current_course.actions[current_course.current_action].oname), 10, 60);
            g.drawString(String.format("Experience(%,d): %,d (%,d xp/hr)", myStats.level(SKILLS_AGILITY), totalXP, XPHr), 10, 80);
            if(xp_left > 0) {
                g.drawString(String.format("TTL(%,d): %,d | %s", next_course_level, xp_left, t.format(time_til_next)), 10, 100);
            } else {
                g.drawString("You are ready for " + getNextCourse(next_course_level), 10, 100);
            }
            g.drawString(String.format("Laps: %,d (%,d laps/hr)", laps, lapsHr), 10, 120);
        } else {
            g.drawString(String.format("Current: %s", "Course is Null, please start at ground floor."), 10, 20);
        }
    }


    @Override
    public void poll() {
        if(ctx.players.local().speed() > 0 || ctx.players.local().inMotion() || (ctx.players.local().animation() != REST_ANIMATION && ctx.players.local().animation() != -1) || current_course == null){
            return;
        }
        if(ctx.players.local().healthPercent() < 30 || (ctx.players.local().animation() == REST_ANIMATION && ctx.players.local().healthPercent() < 80)
                && !current_course.actions[current_course.current_action].oname.equals("Cliffside")){

            if(!ctx.hud.opened(Hud.Window.BACKPACK)){
                ctx.hud.open(Hud.Window.BACKPACK);
            }
            Item food = ctx.backpack.select().id(FOOD_ID).first().poll();
            if (food.valid()) {
                food.click();
            } else {
                if(ctx.players.local().animation() != REST_ANIMATION) {
                    Component energy = ctx.widgets.component(ENERGY_COMPONENT, REST_COMPONENT);
                    if (energy.valid()) {
                        energy.interact("Rest");
                    }
                }
            }
            return;

        }
        if(ctx.players.local().tile().equals(new Tile(2532, 3546, 0)) && current_course.actions[current_course.current_action].oname.equals("Ladder")) {
            current_course.actions[current_course.current_action].doing = false;
            current_course.current_action++;
        }

        if(current_course.actions[current_course.current_action].doing){
            return;
        }

        current_course.current_action = current_course.checkAction(current_course.current_action, ctx.players.local().tile());


        if(failed_bo.contains(ctx.players.local().tile())) {
            ctx.movement.step(new Tile(2541, 3546, 0));
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.players.local().inMotion() || ctx.players.local().animation() == -1;
                }
            });
        } else if(ctx.players.local().tile().equals(new Tile(3005, 3962, 0))){
            if(ctx.movement.destination() == Tile.NIL){
                if(current_course.actions[current_course.current_action].oname.equalsIgnoreCase("Ropeswing")) {
                    ctx.movement.step(new Tile(3005, 3953, 0));
                } else if(current_course.actions[current_course.current_action].oname.equalsIgnoreCase("Log Balance")){
                    ctx.movement.step(new Tile(3000, 3951, 0));
                }
            }
            current_course.actions[current_course.current_action].failed = false;
            current_course.actions[current_course.current_action].doing = false;
        } else if(ctx.players.local().tile().equals(new Tile(2998, 10345, 0))) {
            ctx.movement.step(new Tile(3005, 10362, 0));
            current_course.actions[current_course.current_action].failed = true;
        } else if(ctx.players.local().tile().distanceTo(new Tile(2952, 10312, 0)) <= 100){
            ctx.movement.step(new Tile(3005, 10362, 0));

            Obstacle Ladder = new Obstacle("Climb-up", "Ladder", 32015, BASE_BOUNDS, new FAIL(false),
                    new Tile(3005, 3962, 0));
            GameObject wilderness_ladder = ctx.objects.select().id(Ladder.oid).nearest().first().poll();

            if(!wilderness_ladder.valid()){
                return;
            }
            if (!wilderness_ladder.inViewport()) {
                ctx.camera.turnTo(wilderness_ladder, Random.nextInt(-45, 45));
            }
            wilderness_ladder.bounds(Ladder.bounds);
            wilderness_ladder.interact(Ladder.action, wilderness_ladder.name());
            return;

        }


        if(current_course.actions[current_course.current_action].canFail.failable){
            if(current_course.actions[current_course.current_action].completed){
                current_course.actions[current_course.current_action].completed = false;
                current_course.actions[current_course.current_action].failed = false;
            } else if(current_course.actions[current_course.current_action].failed){
                GameObject obstacle = ctx.objects.select().id(current_course.actions[current_course.current_action].canFail.failObstacle.oid).nearest().first().poll();

                if(!obstacle.valid()){
                    return;
                }
                ctx.camera.turnTo(obstacle, Random.nextInt(-45,45));
                obstacle.bounds(current_course.actions[current_course.current_action].canFail.failObstacle.bounds);
                obstacle.interact(current_course.actions[current_course.current_action].canFail.failObstacle.action, obstacle.name());

                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.players.local().speed() == 0 && current_course.actions[current_course.current_action].compare(ctx.players.local().tile()) ;
                    }
                }, 250, 10);
                current_course.actions[current_course.current_action].failed = false;
                t.setEndIn(current_course.reset);
            }
            if(!current_course.actions[current_course.current_action].doing){
                boolean sameObs = false;
                if(current_course.current_action >= 1){
                    sameObs = current_course.actions[current_course.current_action-1].oid ==current_course.actions[current_course.current_action].oid;
                }
                GameObject obstacle = ctx.objects.select().id(current_course.actions[current_course.current_action].oid).nearest().first().poll();
                if(sameObs){
                    obstacle = ctx.objects.select().id(current_course.actions[current_course.current_action].oid).nearest().reverse().first().poll();
                }

                if(!obstacle.valid()){
                    return;
                }
                ctx.camera.turnTo(obstacle, Random.nextInt(-45,45));
                if(obstacle.name().equalsIgnoreCase("log balance") && current_course.name.equalsIgnoreCase("Wilderness")){
                    ctx.camera.pitch(Random.nextInt(30, 40));
                }

                obstacle.bounds(current_course.actions[current_course.current_action].bounds);
                obstacle.interact(current_course.actions[current_course.current_action].action, obstacle.name());

                current_course.actions[current_course.current_action].doing = true;
                t.setEndIn(current_course.reset);
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.players.local().speed() == 0 && current_course.actions[current_course.current_action].compare(ctx.players.local().tile()) ;
                    }
                }, 250, 10);
            }
        }else {
            boolean sameObs = false;
            if(current_course.current_action >= 1){
                sameObs = current_course.actions[current_course.current_action-1].oid ==current_course.actions[current_course.current_action].oid;
            }
            GameObject obstacle = ctx.objects.select().id(current_course.actions[current_course.current_action].oid).nearest().first().poll();
            if(sameObs){
                obstacle = ctx.objects.select().id(current_course.actions[current_course.current_action].oid).nearest().reverse().first().poll();
            }

            if(!obstacle.valid()){
                return;
            }
            ctx.camera.turnTo(obstacle, Random.nextInt(-45,45));
            obstacle.bounds(current_course.actions[current_course.current_action].bounds);
            obstacle.interact(current_course.actions[current_course.current_action].action, obstacle.name());


            current_course.actions[current_course.current_action].doing = true;
            t.setEndIn(current_course.reset);

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.players.local().speed() == 0;
                }
            }, 250, 10);
        }

        if(current_course.current_action == current_course.actions.length-1){
            laps++;
        }

    }

    private Course getCourse(){
        for(Course course : Courses){
            if(course.mapBase.tile().distanceTo(gameStats.mapOffset().tile()) <= 100){
                return course;
            }
        }
        return null;
    }

    private int getBestCourse(){
        final int current_level = myStats.level(SKILLS_AGILITY);
        for(Course course : Courses){
            if(course.level >= current_level){
                return course.level;
            }
        }
        return 99;
    }

    private String getNextCourse(int level){
        for(Course course : Courses){
            if(current_course.level <= course.level && course.level <= level){
                return course.name;
            }
        }
        return "NONE";
    }

}