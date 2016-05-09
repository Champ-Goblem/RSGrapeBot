package xxb.scripts;

import org.powerbot.script.*;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Actor;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.GameObject;
import xxb.event.EventSource;
import xxb.event.impl.ExperienceEventSource;
import xxb.event.impl.ExperienceListener;
import xxb.event.impl.InventoryItemEventSource;
import xxb.event.impl.InventoryItemListener;
import xxb.ge.SProfitTracker;

import java.awt.*;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Script.Manifest(
        name="Taverly Chaos Druids",
        description="Kills Chaos Druids in Taverly dungeon. Teleports to Falardor and banks herbs and other things.",
        properties = "author=xxb; topic=1310645; client=4;"
)

public class TaverlyChaosDruids extends PollingScript<ClientContext> implements PaintListener {

    private final boolean debug = true;

    private ScriptState state;
    private Map<ScriptState, Runnable> actions;

    private SProfitTracker tracker = new SProfitTracker();

    private final int[] xpgains = new int[23];
    private final Color paintBG = new Color(0, 0, 0, 128);

    private List<EventSource> eventSources = new ArrayList<EventSource>(){{
       add(new InventoryItemEventSource(ctx));
       add(new ExperienceEventSource(ctx));
    }};

    private final int[] pickupids = {207, 563, 215, 213, 209, 211, 556, 2485, 9142};
    private final int[] dropids = { 526, 995, 227, 1291 };

    private final int air = 556, water = 555, law = 563;


    private enum ScriptState {
        WALK_WALL,
        WALK_LADDER,
        WALK_DRUIDS,
        KILL_DRUIDS,
        TELE_FALADOR,
        WALK_BANK,
        BANK_ITEMS,
        WITHDRAW_ITEMS
    }

    private static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%02d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }

    @Override
    public void repaint(Graphics graphics) {
        final Graphics2D g = (Graphics2D) graphics;

        g.setFont(TAHOMA);

        int y = ctx.game.dimensions().height - 400;
        int dy = 15;
        g.setColor(paintBG);
        g.fillRect(3, y, 152, 50);

        g.setColor(Color.WHITE);

        long dt = System.currentTimeMillis() - tracker.getStartTime();

        g.drawString("Running: " + formatDuration(Duration.ofMillis(dt)), 10, y+dy);

        dy += 15;
        int xpgained = 0;
        // generate xp gains dynamically
        /*for(int i = 0; i < xpgains.length; i++) {
            int xp = xpgains[i];
            if(xp > 0) {
                g.drawString(String.format("%s xp: %d", skillName(i), xp), 10, y+dy);
                dy += 15;
            }
        }*/
        for(int xp : xpgains) { xpgained += xp; }

        g.drawString("XP: " + xpgained, 10, y+dy);
        dy += 15;
        g.drawString("Profit: " + tracker.toString(), 10, y+dy);
    }

    //private static String skillName(int idx) { return null; }

    private static final Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);

    @Override
    public void start() {
        state = ScriptState.BANK_ITEMS;

        // add certain event listeners (mainly used for paint)
        ctx.dispatcher.add((ExperienceListener) evt -> {
            if(debug) {
                System.out.println(evt.toString());
            }
            xpgains[evt.getSkillIndex()] += evt.getExperienceChange();
        });

        ctx.dispatcher.add((InventoryItemListener) evt -> {
            if(debug) {
                System.out.println(evt.toString());
            }
            if(evt.getDiff() > 0) {
                // we only care about tracking pickups anyway
                if(tracker.getCachedItems().get(evt.getID()) != null) {
                    tracker.addEarnedItem(evt.getID(), evt.getDiff());
                }
            }
        });

        // cache pickup item prices
        for(int pickupid : pickupids) {
            tracker.cacheItem(pickupid);
        }

        actions = new ConcurrentHashMap<ScriptState,Runnable>(){{
            put(ScriptState.WALK_WALL, () -> {
                double d = ctx.movement.distance(new Tile(2936, 3355));
                if(ctx.players.local().tile().x() <= 2934) {
                    state = ScriptState.WALK_LADDER;
                } else if(d < 5) {
                    GameObject q = ctx.objects.select().id(24222).poll();
                    if(!q.inViewport()) {
                        ctx.camera.turnTo(q);
                        return;
                    }
                    q.interact("Climb-over");
                } else {
                    ctx.movement.findPath(new Tile(2936, 3355)).traverse();
                }
            });

            put(ScriptState.WALK_LADDER, () -> {
                // it clicked too fast and we walked over twice
                if(ctx.players.local().tile().x() > 2934) {
                    state = ScriptState.WALK_WALL;
                    return;
                }
                double d = ctx.movement.distance(new Tile(2884, 3396));
                int y = ctx.players.local().tile().y();
                System.out.println(d + "," + y);
                if((d == -1 && y < 9000) || d > 5) {
                    ladderPath.traverse();
                } else if(y < 9000) {
                    GameObject q = ctx.objects.select().id(16680).poll();
                    q.interact("Climb-down");
                } else {
                    state = ScriptState.WALK_DRUIDS;
                }
            });


            put(ScriptState.WALK_DRUIDS, () -> {
                if(ctx.game.tab() != Game.Tab.INVENTORY) {
                    ctx.game.tab(Game.Tab.INVENTORY);
                } else {
                    double d = ctx.movement.distance(new Tile(2931, 9847));
                    if (d == -1 || d > 5) {
                        pathToDruids.traverse();
                    } else {
                        state = ScriptState.KILL_DRUIDS;
                    }
                }
            });

            put(ScriptState.KILL_DRUIDS, () -> {
                org.powerbot.script.rt4.Player player = ctx.players.local();

                // Set back to run mode if energy > 40
                if(ctx.movement.energyLevel() >= 40 && !ctx.movement.running()) {
                    ctx.movement.running(true);
                    return;
                }

                // Not sure f i'm doing this correctly, but it seems to work.
                Actor interacting = player.interacting();
                if(interacting != null && interacting.valid()) {
                    // likely we attacked, accidently ran away and we're waiting for autoattack to start.
                    // we can just attack now so we don't rely on autoattack.
                    if(!interacting.inCombat() && player.inCombat()) {
                        interacting.interact("Attack");
                    }
                    return;
                }

                // CHeck for items to drop (ie. bones that were accidently pcked up)
                Item item2 = ctx.inventory.select().id(dropids).poll();

                if(item2 != null && item2.valid()) {
                    item2.interact("Drop", item2.name());
                    return;
                }

                if(ctx.inventory.select().count() == 28) {
                    state = ScriptState.TELE_FALADOR;
                }

                if(player.inMotion()) return;

                BasicQuery<GroundItem> itemQuery = ctx.groundItems.select().id(pickupids).within(10).nearest();
                // check for items to pick up
                GroundItem item = itemQuery.poll();

                if(item != null && item.valid()) {

                    if(!item.inViewport()) {
                        if(ctx.movement.distance(item.tile()) > 8) {
                            ctx.movement.step(item.tile());
                            return;
                        }
                        ctx.camera.turnTo(item);
                        ctx.camera.pitch(0);
                        //ctx.camera.angleTo(ctx.game.tileHeight(item.tile().x(), item.tile().y()));
                    }
                    item.interact("Take", item.name());
                    return;
                }

                // Check if any nearby druids are interacting with us
                Npc idruid = ctx.npcs.select().id(2878).select((n) -> n.interacting().equals(ctx.players.local())).poll();
                if(idruid != null && idruid.valid()) {
                    idruid.interact((cmd) -> cmd.action.equals("Attack"));
                    return;
                }

                // Kill nearby druids
                // TODO: make it change camera angle and not just rotation to search for druids
                Npc druid = ctx.npcs.select().id(2878).select((n) -> !n.inCombat()).nearest().poll();
                if(!druid.inViewport()) {

                    if(ctx.movement.distance(druid.tile()) > 8) {
                        ctx.movement.step(druid.tile());
                        return;
                    }

                    ctx.camera.turnTo(druid);
                }
                if(druid.valid() && !druid.inCombat()) {
                    druid.interact((cmd) -> cmd.action.equals("Attack"));
                    return;
                }
            });

            put(ScriptState.TELE_FALADOR, () -> {
                if(ctx.players.local().tile().y() > 9000) {
                    if(ctx.game.tab() != Game.Tab.MAGIC) {
                        ctx.game.tab(Game.Tab.MAGIC);
                    } else {
                        ctx.magic.cast(Magic.Spell.FALADOR_TELEPORT);
                    }
                } else {
                    state = ScriptState.WALK_BANK;
                }
            });

            put(ScriptState.WALK_BANK, () -> {
                double d = ctx.movement.distance(new Tile(2946,3369));
                if(d == -1 || d > 5) {
                    pathToBank.traverse();
                } else {
                    state = ScriptState.BANK_ITEMS;
                }
            });

            put(ScriptState.BANK_ITEMS, () -> {
                if(!ctx.bank.opened()) {
                    ctx.bank.open();
                } else {
                    if(!ctx.inventory.select().isEmpty()) {
                        ctx.bank.depositInventory();
                    } else {
                        state = ScriptState.WITHDRAW_ITEMS;
                    }
                }
            });

            // TODO: make this faster/better
            put(ScriptState.WITHDRAW_ITEMS, () -> {
                ItemQuery<Item> q =  ctx.inventory.select().id(air, water, law);
                int ac = q.poll().stackSize(), wc = q.poll().stackSize(), lc = q.poll().stackSize();

                if(ac > 3) {
                    ctx.bank.deposit(air, ac - 3);
                } else if(ac < 3) {
                    ctx.bank.withdraw(air, 3);
                } else if(wc < 1) {
                    ctx.bank.withdraw(water, 1);
                } else if(wc > 1) {
                    ctx.bank.deposit(water, wc - 1);
                } else if(lc > 1) {
                    ctx.bank.deposit(law, lc - 1);
                } else if(lc < 1) {
                    ctx.bank.withdraw(law, 1);
                } else {
                    state = ScriptState.WALK_WALL;
                }
            });

        }};
    }

    // we sometimes set the pitch to 0 to help locate items, so this
    // eventually resets the camera to 100% pitch. it acts as a sort of antiban (ie. pitch isn't only at 0)
    // and may help get the bot unstuck in case of an edge case!
    private void tendToMaxPitch() {
        int pitch = ctx.camera.pitch();
        if(pitch == 100) {
            return;
        } else {
            if(Math.random() > 0.5) {
                ctx.camera.pitch(pitch + (int)(Math.random()*3));
            }
        }
    }

    @Override
    public void poll() {
        tendToMaxPitch();

        if(state == ScriptState.KILL_DRUIDS) {
            for (EventSource r : eventSources)
                r.process(ctx);
        }

        Runnable action = actions.get(state);
        action.run();
    }

    /* Paths */

    private final Path ladderPath = ctx.movement.newTilePath(
            new Tile(2927, 3361),
            new Tile(2919, 3365),
            new Tile(2916, 3368),
            new Tile(2914, 3370),
            new Tile(2909, 3374),
            new Tile(2904, 3376),
            new Tile(2902, 3380),
            new Tile(2899, 3384),
            new Tile(2895, 3387),
            new Tile(2890, 3392),
            new Tile(2884, 3396)
    );

    private final Path pathToDruids = ctx.movement.newTilePath(
            new Tile(2885, 9796),
            new Tile(2885, 9797),
            new Tile(2885, 9798),
            new Tile(2884, 9799),
            new Tile(2884, 9800),
            new Tile(2884, 9806),
            new Tile(2884, 9812),
            new Tile(2884, 9818),
            new Tile(2884, 9824),
            new Tile(2885, 9832),
            new Tile(2885, 9837),
            new Tile(2884, 9842),
            new Tile(2889, 9846),
            new Tile(2894, 9848),
            new Tile(2904, 9848),
            new Tile(2910, 9848),
            new Tile(2916, 9849),
            new Tile(2923, 9847),
            new Tile(2926, 9843),
            new Tile(2926, 9843),
            new Tile(2931, 9847)
    );

    private final Path pathToBank = ctx.movement.newTilePath(
            new Tile(2964,3379),
            new Tile(2960,3379),
            new Tile(2956,3379),
            new Tile(2952,3376),
            new Tile(2948,3374),
            new Tile(2946,3372),
            new Tile(2946,3372),
            new Tile(2946,3369)
    );
}