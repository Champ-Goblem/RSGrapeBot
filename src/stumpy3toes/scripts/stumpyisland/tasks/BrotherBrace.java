package stumpy3toes.scripts.stumpyisland.tasks;

import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Game;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.scripts.stumpyisland.StumpyIslandTask;

public class BrotherBrace extends StumpyIslandTask {
    private static final int CHAPEL_DOOR_CLOSED_OBJECT_ID = 1524;
    private static final int[] CHAPEL_DOOR_CLOSED_BOUNDS = { -4, 32, -212, 0, -104, 108 };
    private static final Tile CHAPEL_DOOR_CLOSED_TILE = new Tile(3129, 3107);

    private static final int BROTHER_BRACE_NPC_ID = 3319;
    private static final Tile BROTHER_BRACE_TILE = new Tile(3124, 3106);

    private static final int CHAPEL_EXIT_DOOR_OBJECT_ID = 9723;
    private static final int[] CHAPEL_EXIT_DOOR_BOUNDS = { 12, 116, -224, 0, 96, 140 };
    private static final Tile CHAPEL_EXIT_DOOR_TILE = new Tile(3122, 3103);

    public BrotherBrace(ClientContext ctx) {
        super(ctx, "Brother Brace", 0x226, 0x262, CHAPEL_DOOR_CLOSED_TILE, CHAPEL_EXIT_DOOR_TILE);
    }

    @Override
    protected void execute(int progress) {
        switch (progress) {
            case 0x258:
            case 0x23A:
            case 0x226:
                if (ctx.movement.reachable(BROTHER_BRACE_TILE)) {
                    setStatus("Talking to Brother Brace");
                    ctx.npcs.npc(BROTHER_BRACE_NPC_ID).setReachableTile(BROTHER_BRACE_TILE).talkTo();
                } else {
                    enterChapel();
                }
                break;
            case 0x230:
                setStatus("Opening prayer tab");
                ctx.game.tab(Game.Tab.PRAYER);
                break;
            case 0x244:
                setStatus("Opening friends list tab");
                ctx.game.tab(Game.Tab.FRIENDS_LIST);
                break;
            case 0x24E:
                setStatus("Opening ignore list tab");
                ctx.game.tab(Game.Tab.IGNORED_LIST);
                break;
        }
    }

    private void enterChapel() {
        if (ctx.objects.object(CHAPEL_DOOR_CLOSED_OBJECT_ID).valid()) {
            setStatus("Opening chapel doors");
            ctx.objects.object(CHAPEL_DOOR_CLOSED_OBJECT_ID).setBounds(CHAPEL_DOOR_CLOSED_BOUNDS)
                    .setReachableTile(CHAPEL_DOOR_CLOSED_TILE).open();
        }
    }

    @Override
    protected void leaveArea() {
        if (ctx.movement.reachable(CHAPEL_EXIT_DOOR_TILE)) {
            setStatus("Exiting chapel");
            ctx.objects.object(CHAPEL_EXIT_DOOR_OBJECT_ID).setBounds(CHAPEL_EXIT_DOOR_BOUNDS)
                    .setReachableTile(CHAPEL_EXIT_DOOR_TILE).walkThroughDoor();
        } else {
            enterChapel();
        }
    }
}