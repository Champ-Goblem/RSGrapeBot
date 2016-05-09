package sd8z.scripts.sbonfire.jobs;

import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.*;
import sd8z.core.script.Job;
import sd8z.scripts.sbonfire.SBonfire;

import java.util.concurrent.Callable;

public class Bonfire extends Job<SBonfire, ClientContext> {

    private final int id;
    private final Area area;
    private final Tile loc;
    private final Component widget;
    private final int[] bounds = {-184, 184, -312, -8, -184, 192};

    public Bonfire(SBonfire script, int id, Tile loc) {
        super(script);
        this.id = id;
        this.loc = loc;
        area = new Area(new Tile(loc.x() - 1, loc.y() - 1), new Tile(loc.x() + 2, loc.y() + 2));
        widget = ctx.widgets.component(1179, 34);
    }


    @Override
    public boolean activate() {
        Player p = ctx.players.local();
        return !ctx.backpack.select().id(id).isEmpty() && p.stance() != 16701 && p.animation() == -1
                || widget.visible();
    }

    @Override
    public void execute() {
        ctx.hud.open(Hud.Window.BACKPACK);
        GameObject fire = ctx.objects.select().name("Fire").within(area).shuffle().each(Interactive.doSetBounds(bounds)).poll();
        if (widget.visible()) {
            if (widget.interact("Select")) {
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.players.local().stance() == 16701;
                    }
                });
            }
        } else {
            if (fire.interact("Use", "Fire")) {
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return widget.visible();
                    }
                });
            }
        }
        if (fire.valid()) {
            if (!fire.inViewport()) {
                ctx.camera.turnTo(fire);
            }
        } else {
            TileMatrix tile = loc.matrix(ctx);
            Item log = ctx.backpack.poll();
            if (ctx.players.local().tile().equals(loc)) {
                if (log.interact("Light")) {
                    if (!tile.contains(ctx.input.getLocation()))
                        ctx.input.move(tile.nextPoint());
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.objects.select(5).name("Fire").within(area).poll().valid();
                        }
                    });
                }
            } else {
                if (ctx.backpack.itemSelected()) {
                    if (ctx.backpack.itemAt(ctx.backpack.selectedItemIndex()).click())
                        return;
                }
                if (tile.inViewport()) {
                    if (tile.interact("Walk here")) {
                        log.click(false);
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return ctx.players.local().tile().equals(loc);
                            }
                        });
                    }
                } else {
                    ctx.camera.turnTo(loc);
                }
            }
        }
    }
}
