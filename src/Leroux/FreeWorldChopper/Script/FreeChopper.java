package Leroux.FreeWorldChopper.Script;

import Leroux.FreeWorldChopper.WoodCutting.Wrapper.WoodCutting;
import org.powerbot.script.*;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Callable;


@Script.Manifest(name = "FreeWorld Chopper", description = "Chops things in various F2P locations.",properties = "client=6;topic=1297151;author=Leroux;")
public class FreeChopper extends PollingScript<ClientContext> implements PaintListener, MessageListener{

    public static ArrayList<Task> taskList = new ArrayList<Task>();
    private GUI gui = new GUI(ctx);

    private final Color mouseColor = new Color(82, 181, 209), boxColor = new Color(82,181,209,125), textColor = new Color(0,0,0);
    private final Font font = new Font(("Arial"), Font.BOLD, 15);

    private long initialTime;
    private int initWoodcuttingExp, initWoodcuttingLv, logsChopped, expGained, lvsGained, hours, minutes, seconds;
    private double runTime, expPerHour;

    public static String scriptStatus;

    public void start() {
        scriptStatus = "Waiting for GUI...";

        if(!ctx.game.loggedIn()){
            System.out.print(" Please log in. ");
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
        initWoodcuttingExp = ctx.skills.experience(Constants.SKILLS_WOODCUTTING);
        initWoodcuttingLv = ctx.skills.level(Constants.SKILLS_WOODCUTTING);
        if (initWoodcuttingLv < WoodCutting.getMinLevel()) {
            System.out.print("Your Woodcutting Level is not quite high enough to chop to trees you selected.");
            taskList.removeAll(taskList);
            ctx.controller.stop();
        }
    }

    public void poll() {
        for(Task<?> task : taskList) {
            if(task.activate()) {
                task.execute();
                System.out.print(task);
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
        g.fillRoundRect(8, 346, 300, 126, 5,  5);
        g.setColor(textColor);
        g.setFont(font);
        g.drawString("Script Status: " + scriptStatus, 22, 460);

        expGained = ctx.skills.experience(Constants.SKILLS_WOODCUTTING) - initWoodcuttingExp;
        lvsGained = ctx.skills.level(Constants.SKILLS_WOODCUTTING) - initWoodcuttingLv;
        expPerHour = expGained / runTime;

        g.drawString("Free World Chopper", 90, 364);
        g.drawString("1.00v ", 250, 364);
        g.drawString("Logs Chopped: " + logsChopped, 22, 385);
        g.drawString("Chopping: " + WoodCutting.getTreeName(), 175, 385);
        g.drawString("Experience Gained: " + expGained, 22, 400);
        g.drawString("Levels Gained: " + lvsGained, 22, 415);
        g.drawString("Time Running: " + hours +":" + minutes + ":" + seconds, 22, 430);
        g.drawString("Experience/Hour : "  + (int)expPerHour, 22, 445);
    }

    public void messaged(MessageEvent m) {
        if(m.text().startsWith("You get some")) {
            logsChopped++;
        }
    }
}
