package coma.claysoftener;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.MessageListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.Item;

import java.util.concurrent.Callable;

@Script.Manifest(name = "Auto Clay Softener", properties = "author=Coma; topic=1286161; client=6;",
        description = "Softens clay for no-requirements money making.")
public class ClaySoftener extends PollingScript<ClientContext> implements MessageListener {
    //widget and component indices for the "Soften" button
    public static final int WIDGET = 1370, COMPONENT = 38;
    //item ids
    public static final int CLAY = 434, SOFT_CLAY = 1761;
    //fountain object id
    public static final int FOUNTAIN = 47150;
    //tiles to walk to
    public static final Tile BANK_TILE = new Tile(3151, 3479, 0);
    public static final Tile FOUNTAIN_TILE = new Tile(3164, 3489, 0);

    private int claysSoftened = 0;
    private long last = 0;

    @Override
    public void poll() {
        final State state = getState();
        if (state == null) {
            return;
        }
        switch (state) {
            case BANK:
                if (!ctx.bank.inViewport()) {
                    ctx.movement.step(BANK_TILE);
                    ctx.camera.turnTo(BANK_TILE);
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return BANK_TILE.distanceTo(ctx.players.local()) < 5;
                        }
                    }, 250, 10);
                } else if (ctx.bank.open()) {
                    if (ctx.bank.select().id(CLAY).isEmpty()) {
                        ctx.controller.stop();
                    } else if (ctx.bank.presetGear(1, true)) {
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return !ctx.bank.opened();
                            }
                        }, 250, 10);
                    }
                }
                break;
            case SOFTEN_CLAY:
                if (ctx.players.local().animation() != -1) {
                    last = System.currentTimeMillis();
                }

                //checks if last clay softened was less than 3 seconds ago (prevents spam clicking)
                if (System.currentTimeMillis() - last < 3000) {
                    break;
                }

                ctx.objects.select(15).id(FOUNTAIN).select(new Filter<GameObject>() {
                    @Override
                    public boolean accept(GameObject gameObject) {
                        return gameObject.inViewport();
                    }
                });
                if (ctx.objects.isEmpty()) {
                    ctx.movement.step(FOUNTAIN_TILE);
                    ctx.camera.turnTo(FOUNTAIN_TILE);
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return FOUNTAIN_TILE.distanceTo(ctx.players.local()) < 5;
                        }
                    }, 250, 10);
                    break;
                }

                final Item clay = ctx.backpack.select().id(CLAY).poll();
                final GameObject fountain = ctx.objects.nearest().poll();
                final Component soften = ctx.widgets.component(WIDGET, COMPONENT);

                if (!soften.visible() && clay.interact("Use", clay.name()) && fountain.interact("Use", fountain.name())) {
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return soften.visible();
                        }
                    }, 250, 10);
                } else if (soften.visible()) {
                    soften.click();
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return !soften.visible() && ctx.players.local().animation() != -1;
                        }
                    }, 250, 10);
                }

                break;
        }
    }

    private enum State {
        BANK, SOFTEN_CLAY
    }

    private State getState() {
        return ctx.backpack.select().id(CLAY).isEmpty()
                ? State.BANK : State.SOFTEN_CLAY;
    }

    @Override
    public void messaged(MessageEvent e) {
        final String msg = e.text().toLowerCase();
        if (e.source().isEmpty() && msg.contains("you mix the clay and water")) {
            claysSoftened++;
        }
    }
}
