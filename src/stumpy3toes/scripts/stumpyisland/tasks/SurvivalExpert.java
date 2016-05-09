package stumpy3toes.scripts.stumpyisland.tasks;

import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Game;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.api.script.wrappers.GameObject;
import stumpy3toes.api.script.wrappers.Item;
import stumpy3toes.scripts.stumpyisland.StumpyIslandTask;

public class SurvivalExpert extends StumpyIslandTask {
    private static final int SURVIVAL_EXPERT_NPC_ID = 3306;
    private static final Tile SURVIVAL_EXPERT_TILE = new Tile(3103, 3096);

    private static final int GATE_OBJECT_ID = 9708;
    private static final int[] GATE_BOUNDS = { 104, 136, -120, 0, -96, 104 };
    private static final Tile GATE_TILE = new Tile(3090, 3092);

    private static final int TREE_OBJECT_ID = 9730;
    private static final int TINDERBOX_ITEM_ID = 590;
    private static final int LOG_ITEM_ID = 2511;
    private static final int FIRE_OBJECT_ID = 26185;

    private static final int FISHING_SPOT_NPC_ID = 3317;
    private static final int RAW_SHRIMPS_ITEM_ID = 2514;
    private static final int BURNT_SHRIMPS_ITEM_ID = 7954;
    private static final int[] SHRIMPS_ITEM_IDS = {
            RAW_SHRIMPS_ITEM_ID,
            BURNT_SHRIMPS_ITEM_ID
    };

    public SurvivalExpert(ClientContext ctx) {
        super(ctx, "Survival Expert", 0x14, 0x78, SURVIVAL_EXPERT_TILE);
    }

    @Override
    protected void execute(int progress) {
        switch (progress) {
            case 0x46:
            case 0x14:
                setStatus("Talking to Survival Expert");
                ctx.npcs.npc(SURVIVAL_EXPERT_NPC_ID).setReachableTile(SURVIVAL_EXPERT_TILE).talkTo();
                break;
            case 0x1E:
                setStatus("Opening inventory");
                ctx.game.tab(Game.Tab.INVENTORY);
                break;
            case 0x28:
                chopDownTree();
                break;
            case 0x32:
                createFire();
                break;
            case 0x3C:
                setStatus("Opening stats tab");
                ctx.game.tab(Game.Tab.STATS);
                break;
            case 0x50:
                catchShrimp();
                break;
            case 0x6E:
            case 0x64:
            case 0x5A:
                ctx.game.tab(Game.Tab.INVENTORY);
                if (ctx.inventory.select().id(SHRIMPS_ITEM_IDS).count() < 2) {
                    catchShrimp();
                } else if (!ctx.players.local().animated()) {
                    GameObject fire = ctx.objects.object(FIRE_OBJECT_ID);
                    if (!fire.valid()) {
                        createFire();
                    } else {
                        setStatus("Cooking shrimp");
                        ctx.inventory.item(RAW_SHRIMPS_ITEM_ID).use(fire, ctx.checks.animated);
                    }
                } else {
                    ctx.players.local().waitForIdle();
                }
                break;
        }
    }

    private void chopDownTree() {
        if (!ctx.players.local().animated()) {
            setStatus("Going to nearby tree");
            ctx.objects.object(TREE_OBJECT_ID).walkingInteraction("Chop down", ctx.checks.animated);
        } else {
            setStatus("Chopping tree");
            ctx.players.local().waitForIdle();
        }
    }

    private void createFire() {
        ctx.game.tab(Game.Tab.INVENTORY);
        if (ctx.players.local().idle()) {
            Item log = ctx.inventory.item(LOG_ITEM_ID);
            if (!log.valid()) {
                chopDownTree();
            } else {
                if (ctx.objects.select(0).id(FIRE_OBJECT_ID).size() == 1) {
                    setStatus("Moving to tile without fire");
                    loop:
                    for (int i = ctx.players.local().tile().x() - 2; i < ctx.players.local().tile().x() + 2; i++) {
                        for (int j = ctx.players.local().tile().y() - 2; j < ctx.players.local().tile().y() + 2; j++) {
                            Tile clearTile = new Tile(i, j);
                            if (ctx.objects.select(2).at(clearTile).id(FIRE_OBJECT_ID).isEmpty()
                                    && ctx.movement.reachable(clearTile) && ctx.movement.step(clearTile)) {
                                break loop;
                            }
                        }
                    }
                } else {
                    setStatus("Using logs on tinderbox");
                    if (log.use(ctx.inventory.item(TINDERBOX_ITEM_ID))) {
                        setStatus("Lighting a fire");
                        ctx.players.local().waitForAnimated();
                    }
                }
            }
        } else {
            ctx.players.local().waitForIdle();
        }
    }

    private void catchShrimp() {
        if (!ctx.players.local().animated()) {
            setStatus("Going to fishing spot");
            ctx.npcs.npc(FISHING_SPOT_NPC_ID).walkingInteraction("Net", ctx.checks.animated);
        } else {
            setStatus("Fishing");
            ctx.players.local().waitForIdle();
        }
    }

    @Override
    protected void leaveArea() {
        setStatus("Opening gate");
        ctx.objects.object(GATE_OBJECT_ID).setBounds(GATE_BOUNDS).setReachableTile(GATE_TILE).walkThroughDoor();
    }
}
