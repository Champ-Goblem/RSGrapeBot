package sd8z.scripts.sbonfire;

import org.powerbot.script.MessageEvent;
import org.powerbot.script.MessageListener;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Constants;
import sd8z.core.painter.Detail;
import sd8z.core.painter.Painter;
import sd8z.core.script.Job;
import sd8z.core.script.JobContainer;
import sd8z.core.script.Script;
import sd8z.scripts.sbonfire.jobs.Bank;
import sd8z.scripts.sbonfire.jobs.Bonfire;
import sd8z.scripts.sbonfire.jobs.Pick;
import sd8z.scripts.sbonfire.jobs.Spirit;
import sd8z.scripts.sbonfire.util.GUI;
import sd8z.scripts.sbonfire.util.Log;

import java.awt.*;

@Script.Manifest(name = "sBonfire", description = "Makes bonfires for firemaking xp.", properties = "version=0.80;topic=1296382;author=SD8Z;")
public class SBonfire extends Script<ClientContext> implements PaintListener, MessageListener {

    private JobContainer container = new JobContainer();
    private int logsBurned = 0;
    private int startXp = 0;
    private int spirits = 0;
    private int xp = 0;
    private final Painter paint = new Painter(this);
    private final Detail logs = new Detail("Logs", true);
    private final Detail spiritCount = new Detail("Spirits", true);
    private final Detail xpCount = new Detail("XP", true);
    private final Detail type = new Detail("Type", false);
    private String logType = "";

    @Override
    public void start() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUI gui = new GUI(SBonfire.this, ctx);
                gui.setVisible(true);
            }
        });
    }

    @Override
    public void poll() {
        if (startXp <= 0)
            startXp = ctx.skills.experience(Constants.SKILLS_FIREMAKING);
        Job j = container.get();
        if (j != null) {
            j.execute();
        }
    }

    @Override
    public void repaint(Graphics g) {
        paint.draw(g, type.setObject(logType), xpCount.setObject(xp), logs.setObject(logsBurned),
                spiritCount.setObject(spirits));
    }

    public void submit(Log log, Tile loc, boolean pickAshes) {
        startXp = ctx.skills.experience(Constants.SKILLS_FIREMAKING);
        logType = log.toString();
        container = new JobContainer(new Spirit(this), new Bank(this, log.id()), new Bonfire(this, log.id(), loc));
        if (pickAshes)
            container.submit(new Pick(this, loc));
        ctx.controller.offer(new Thread(new Runnable() {
            @Override
            public void run() {
                xp = ctx.skills.experience(Constants.SKILLS_FIREMAKING) - startXp;
                ctx.controller.offer(new Thread(this));
            }
        }));
    }

    @Override
    public void messaged(MessageEvent m) {
        String msg = m.text();
        if (msg.equals("You add a log to the fire.") || msg.equals("The fire catches and the logs begin to burn."))
            logsBurned++;
        else if (msg.equals("The fire spirit gives you a reward to say thank you for freeing it, before disappearing."))
            spirits++;
    }
}
