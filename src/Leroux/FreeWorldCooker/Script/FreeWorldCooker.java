package Leroux.FreeWorldCooker.Script;

import org.powerbot.script.*;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Callable;

@Script.Manifest(name = "FreeWorld Cooker", description = "Cooks things in Al Kharid.", properties = "client=6;topic=1297540;author=Leroux;")
public class FreeWorldCooker extends PollingScript<ClientContext> implements PaintListener, MessageListener {
    public static ArrayList<Task> taskList = new ArrayList<Task>();
    private GUI gui = new GUI(ctx);

    private final Color mouseColor = new Color(82, 181, 209), boxColor = new Color(82,181,209,125), textColor = new Color(0,0,0);
    private final Font font = new Font(("Arial"), Font.BOLD, 15);

    private long initialTime;
    private int initCookingExp, initCookingLv, stuffCooked, expGained, lvsGained, hours, minutes, seconds;
    private double runTime, expPerHour;

    public static String scriptStatus;
    public static int[] cookingIDs;

    public void start() {
        scriptStatus = "Waiting for GUI...";

        if(!ctx.game.loggedIn()) {
            System.out.print("Please Log In.");
            ctx.controller.stop();
        }

        gui.setVisible(true);

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return gui.guiDone;
            }
        }, 500, 500);

        initialTime = System.currentTimeMillis();
        initCookingExp = ctx.skills.experience(Constants.SKILLS_COOKING);
        initCookingLv = ctx.skills.level(Constants.SKILLS_COOKING);
    }

    public void poll() {
        for(Task<?> task : taskList) {
            if(task.activate()) {
                task.execute();
            }
        }
    }

    public void repaint(Graphics g1) {

        Graphics2D g = (Graphics2D)g1;
        int x = (int) ctx.input.getLocation().getX();
        int y = (int) ctx.input.getLocation().getY();

        g.setColor(mouseColor);
        g.drawLine(x, y - 10, x, y + 10);
        g.drawLine(x - 10, y, x + 10, y);

        hours = (int) ((System.currentTimeMillis() - initialTime) / 3600000);
        minutes = (int) ((System.currentTimeMillis() - initialTime) / 60000 % 60);
        seconds = (int) ((System.currentTimeMillis() - initialTime) / 1000) % 60;
        runTime = (double) (System.currentTimeMillis() - initialTime) / 3600000;

        g.setColor(boxColor);
        g.fillRoundRect(8, 346, 300, 126, 5, 5);
        g.setColor(textColor);
        g.setFont(font);

        expGained = ctx.skills.experience(Constants.SKILLS_COOKING) - initCookingExp;
        lvsGained = ctx.skills.level(Constants.SKILLS_COOKING) - initCookingLv;
        expPerHour = expGained / runTime;

        g.drawString("Free World Cooker", 90, 364);
        g.drawString("1.00v ", 250, 364);
        g.drawString("Cooked: " + stuffCooked, 22, 385);
        g.drawString("Experience Gained: " + expGained, 22, 400);
        g.drawString("Levels Gained: " + lvsGained, 22, 415);
        g.drawString("Time Running: " + hours +":" + minutes + ":" + seconds, 22, 430);
        g.drawString("Experience/Hour : "  + (int)expPerHour, 22, 445);
        g.drawString("Script Status: " + scriptStatus, 22, 460);
    }

    public void messaged(MessageEvent m) {
        if(m.text().startsWith("You successfully")) {
            stuffCooked++;
        }
    }
}
