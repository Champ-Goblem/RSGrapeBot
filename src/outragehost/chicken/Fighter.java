package outragehost.chicken;

import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt6.ClientContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Script.Manifest(
        name = "Outrage Chicken Slayer", properties = "author=Outrage Host; topic=1304601; client=6;",
        description = "v1.0 - Fights chickens to level up your account and gain money!"
)

public class Fighter extends PollingScript<ClientContext> implements PaintListener {

    public static int killed;
    public static int totalKilled = 0;
    public static int currentFeathers = 0;

    Font font = new Font("Verdana", Font.BOLD, 12);
    static String chickensKilled = "Chickens killed: 0";
    static String displayMoney = "Money gained: 0";
    static String displayFeathers = "Feathers collected: 0";
    static String displayStatus = "Status: N/A";
    String timeRunning = ""+getRuntime();

    Color transGrey = new Color(119, 136, 153, 150);

    private List<Task> taskList = new ArrayList<Task>();
    @Override
    public void start() {
        taskList.addAll(Arrays.asList(new Gate(ctx), new Chicken(ctx), new Chicken_Feathers(ctx), new Antiban(ctx)));
    }

    @Override
    public void poll() {
        for (Task task : taskList) {
            if (task.activate()) {
                task.execute();
            }
        }
    }

    @Override
    public void repaint(Graphics graphics) {
            graphics.setFont(font);
            graphics.setColor(transGrey);
            graphics.drawRect(0, 0, 200, 120);
            graphics.fillRect(0, 0, 200, 120);
            graphics.setColor(Color.black);
            graphics.getFont();
            graphics.drawString("Outrage Chicken Slayer!", 15, 15);
            graphics.drawString(displayStatus, 15, 35);
            graphics.drawString(chickensKilled, 15, 55);
            graphics.drawString(displayFeathers, 15, 75);
            graphics.drawString(displayMoney, 15, 95);

            long hours = (getRuntime()/1000) / 3600;
            long minutes = ((getRuntime()/1000) % 3600) / 60;
            long seconds = (getRuntime()/1000) % 60;

            graphics.drawString("Time Running: "+String.format("%02d:%02d:%02d", hours, minutes, seconds), 15, 115);
    }
}
