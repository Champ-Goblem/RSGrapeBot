package SirFancyGamer.scripts;

import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt6.*;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

//the rt6 package is for RS3. For OSRS scripts, you would use the rt4 package.

@Script.Manifest(
        name = "Chicken Slayer",
        description = "Kills chickens and collects feathers at NW Lumbridge spot",
        properties = "author=SirFancyGamer;topic=1298781;"
)
public class ChickenSlayer extends PollingScript<ClientContext> implements PaintListener{

    Font font = new Font("Verdana", Font.BOLD, 12);

    String displayKilled = "Chickens killed: 0";
    String displayMoney = "Money gained: 0";
    String displayFeathers = "Feathers collected: 0";
    String displayStatus = "Status: N/A";

    Color transGrey = new Color(119, 136, 153, 150);

    GroundItem feather = ctx.groundItems.select().name("Feather").nearest().poll(); //Selecting nearest feather on ground.
    Npc chicken = ctx.npcs.select().name("Chicken").nearest().poll(); // Targeting chicken to attack
    long currentTime = System.currentTimeMillis(); //Timer for attacking reset if needed.
    long tempTime = System.currentTimeMillis(); //Temp timer to receive elapsed time.
    int tempExp = ctx.skills.experience(2); //Temp strength experience

    GeItem featherGE = new GeItem(314); //Grand exchange item for feather
    long featherCount = ctx.backpack.select().name("Feather").count(true); //How many feathers are in inventory stack
    org.powerbot.script.Tile tile1 = new org.powerbot.script.Tile(3207, 3287); //Tile at chicken spot for door/gate distance restriction
    int playerExp = ctx.skills.experience(2); //Player strength exp
    int chickensKilled = 0; //Chickens killed counter

    long maxDistance = 10; //Max distance used for door/gate closing, feather collecting and chicken targeting
    Random rn = new Random();
    Camera camera = new Camera(ctx);

    org.powerbot.script.rt6.GameObject closedDoor = ctx.objects.select().id(45476).nearest().limit(1).poll(); //Selecting/identifying closed door
    org.powerbot.script.rt6.GameObject closedGate = ctx.objects.select().id(45208).nearest().limit(1).poll(); //Selecting/identifying closed gate

    @Override
    public void start() {
        System.out.println("Script Started!");
    }

    @Override
    public void poll() {
        closeGate();
        closeDoor();
        attackChicken();
        collectFeathers();
    }

    public void attackChicken() {
        currentTime = System.currentTimeMillis();
        chicken = ctx.npcs.select().name("Chicken").nearest().poll();
        do {
            tempTime = System.currentTimeMillis();
            if (chicken.valid() && chicken.tile().distanceTo(tile1) < maxDistance) {
                closeDoor();
                closeGate();
                if (camera.pitch() < 50) {
                    camera.pitch(55);
                }
                camera.turnTo(chicken);
                chicken.interact("Attack", chicken.name());
                displayStatus = "Status: Attacking";
                try {
                    TimeUnit.MILLISECONDS.sleep((long) (rn.nextDouble() + .45) * 1000);
                } catch (InterruptedException e) {
                    e.getMessage();
                }
            }

            if (tempTime - currentTime >= 5000) {
                break;
            }
            if (chicken.animation() == 3806) {
                break;
            }
        } while (chicken.animation() != 3806 || !chicken.valid());

        tempExp = ctx.skills.experience(2);
        if (tempExp - playerExp >= 8) {
            playerExp = ctx.skills.experience(2);
            chickensKilled++;
            displayKilled = "Chickens killed: " + chickensKilled;
        }
    }

    public void closeDoor() {
        closedDoor = ctx.objects.select().id(45476).nearest().limit(1).poll();
        if (closedDoor.valid() && closedDoor.tile().distanceTo(tile1) <= 2) {
            if (camera.pitch() < 50) {
                camera.pitch(55);
            }
            camera.turnTo(closedDoor);
            closedDoor.interact("Open");
            displayStatus = "Opening door..";
            try {
                TimeUnit.MILLISECONDS.sleep((long) (rn.nextDouble() + .95) * 1000);
            } catch (InterruptedException e) {
                e.getMessage();
            }
        }
    }

    public void closeGate() {
        closedGate = ctx.objects.select().id(45208).nearest().limit(1).poll();
        if (closedGate.valid() && closedGate.tile().distanceTo(tile1) < maxDistance) {
            if (camera.pitch() < 50) {
                camera.pitch(55);
            }
            camera.turnTo(closedGate);
            closedGate.interact("Open");
            displayStatus = "Opening gate...";
            try {
                TimeUnit.MILLISECONDS.sleep((long) (rn.nextDouble() + .85) * 1000);
            } catch (InterruptedException e) {
                e.getMessage();
            }
        }
    }

    public void collectFeathers() {
        feather = ctx.groundItems.select().name("Feather").nearest().poll();
        while (feather != ctx.groundItems.nil() && feather.valid() && feather.tile().distanceTo(tile1) < maxDistance) {
            closeDoor();
            closeGate();
            if (feather.valid()) {
                if (camera.pitch() < 50) {
                    camera.pitch(55);
                }
                camera.turnTo(feather);
                feather.interact("Take", "Feather");
                displayStatus = "Status: Collecting Feathers";
            }
            if (!feather.valid()) {
                long tempFeathers = ctx.backpack.select().name("Feather").count(true);
                long feathersGained = tempFeathers - featherCount;
                long moneyGained = featherGE.price * feathersGained;
                displayFeathers = "Feathers collected: " + feathersGained;
                displayMoney = "Money gained: " + moneyGained;
            }
            feather = ctx.groundItems.select().name("Feather").nearest().poll();
        }
    }

    @Override
    public void repaint(Graphics graphics) {
        graphics.setFont(font);
        graphics.setColor(transGrey);
        graphics.drawRect(0, 0, 200, 100);
        graphics.fillRect(0, 0, 200, 100);
        graphics.setColor(Color.black);
        graphics.getFont();
        graphics.drawString("Lumbridge Chicken Slayer!", 15, 15);
        graphics.drawString(displayStatus, 15, 35);
        graphics.drawString(displayKilled, 15, 55);
        graphics.drawString(displayFeathers, 15, 75);
        graphics.drawString(displayMoney, 15, 95);
    }

    @Override
    public void stop() {
        System.out.println("Script Stopped!");
    }
}