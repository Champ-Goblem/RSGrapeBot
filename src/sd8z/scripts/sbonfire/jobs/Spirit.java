package sd8z.scripts.sbonfire.jobs;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Interactive;
import org.powerbot.script.rt6.Npc;
import sd8z.core.script.Job;
import sd8z.scripts.sbonfire.SBonfire;

import java.util.concurrent.Callable;

public class Spirit extends Job<SBonfire, ClientContext> {

    private final int[] bounds = {-116, 132, -420, -240, -60, 92};

    public Spirit(SBonfire script) {
        super(script);
    }

    @Override
    public boolean activate() {
        return !ctx.npcs.select().name("Fire spirit").isEmpty() && ctx.backpack.select().count() < Random.nextInt(10, 18);
    }

    @Override
    public void execute() {
        Condition.sleep(Random.nextInt(3000, 8000));
        final Npc spirit = ctx.npcs.each(Interactive.doSetBounds(bounds)).poll();
        if (spirit.inViewport()) {
            if (spirit.interact("Collect-reward")) {
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !spirit.valid();
                    }
                }, 300, 12);
            }
        } else {
            ctx.camera.turnTo(spirit);
        }
    }
}
