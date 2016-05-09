package sd8z.scripts.sbonfire.jobs;

import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GroundItem;
import sd8z.core.script.Job;
import sd8z.scripts.sbonfire.SBonfire;

import java.util.concurrent.Callable;

public class Pick extends Job<SBonfire, ClientContext> {

    private final Filter<GroundItem> filter = new Filter<GroundItem>() {
        @Override
        public boolean accept(GroundItem g) {
            String name = g.name().toLowerCase();
            return name.contains("logs") || name.contains("ashes");
        }
    };
    private final Area area;

    public Pick(SBonfire script, Tile loc) {
        super(script);
        area = new Area(new Tile(loc.x() - 1, loc.y() - 1), new Tile(loc.x() + 2, loc.y() + 2));
    }

    @Override
    public boolean activate() {
        return !ctx.groundItems.select().select(filter).within(area).isEmpty() && ctx.backpack.select().count() < 28;
    }

    @Override
    public void execute() {
        final GroundItem item = ctx.groundItems.poll();
        if (item.inViewport()) {
            if (item.interact("Take", item.name())) {
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !item.valid();
                    }
                });
            }
        } else {
            ctx.camera.turnTo(item);
        }
    }
}
