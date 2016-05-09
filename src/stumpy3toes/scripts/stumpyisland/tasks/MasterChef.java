package stumpy3toes.scripts.stumpyisland.tasks;

import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Game;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.scripts.stumpyisland.StumpyIslandTask;

import java.util.concurrent.Callable;

public class MasterChef extends StumpyIslandTask {
    private static final int MASTER_CHEF_NPC_ID = 3305;

    private static final int DOOR_OBJECT_ID = 9709;
    private static final int[] DOOR_BOUNDS = new int[]{ -4, 16, -228, 8, 4, 128 };
    private static final Tile DOOR_TILE = new Tile(3079, 3084);

    private static final int EXIT_DOOR_OBJECT_ID = 9710;
    private static final int[] EXIT_DOOR_BOUNDS = new int[]{ 100, 132, -228, 0, -4, 128 };
    private static final Tile EXIT_DOOR_TILE = new Tile(3073, 3090);

    private static final int POT_OF_FLOUR_ITEM_ID = 2516;
    private static final int BUCKET_OF_WATER_ITEM_ID = 1929;

    private static final int DOUGH_ITEM_ID = 2307;
    private static final int RANGE_OBJECT_ID = 9736;

    public MasterChef(ClientContext ctx) {
        super(ctx, "Master Chef", 0x82, 0xB4, DOOR_TILE, EXIT_DOOR_TILE);
    }

    @Override
    protected void execute(int progress) {
        switch (progress) {
            case 0x82:
                leaveArea();
                break;
            case 0x8C:
                setStatus("Talking to the Master Chef");
                ctx.npcs.npc(MASTER_CHEF_NPC_ID).talkTo();
                break;
            case 0x96:
                setStatus("Making dough");
                ctx.game.tab(Game.Tab.INVENTORY);
                if (ctx.inventory.item(POT_OF_FLOUR_ITEM_ID).use(ctx.inventory.item(BUCKET_OF_WATER_ITEM_ID))) {
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.inventory.item(DOUGH_ITEM_ID).valid();
                        }
                    }, 250, 3);
                }
                break;
            case 0xA0:
                setStatus("Cooking dough");
                ctx.game.tab(Game.Tab.INVENTORY);
                if (ctx.inventory.item(DOUGH_ITEM_ID).use(ctx.objects.object(RANGE_OBJECT_ID), ctx.checks.animated)) {
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return progress() == 0xAA;
                        }
                    }, 300, 10);
                }
                break;
            case 0xAA:
                setStatus("Opening music tab");
                ctx.game.tab(Game.Tab.MUSIC);
                break;
        }
    }

    @Override
    protected void leaveArea() {
        if (ctx.movement.reachable(EXIT_DOOR_TILE)) {
            setStatus("Opening exit door");
            ctx.objects.object(EXIT_DOOR_OBJECT_ID).setBounds(EXIT_DOOR_BOUNDS).setReachableTile(EXIT_DOOR_TILE).walkThroughDoor();
        } else {
            setStatus("Opening door");
            ctx.objects.object(DOOR_OBJECT_ID).setBounds(DOOR_BOUNDS).setReachableTile(DOOR_TILE).walkThroughDoor();
        }
    }
}