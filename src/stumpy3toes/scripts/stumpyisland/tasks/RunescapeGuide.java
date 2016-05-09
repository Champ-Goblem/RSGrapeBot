package stumpy3toes.scripts.stumpyisland.tasks;

import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Game;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.scripts.stumpyisland.StumpyIslandTask;

public class RunescapeGuide extends StumpyIslandTask {
    private static final int RUNESCAPE_GUIDE_NPC_ID = 3308;

    private static final int DOOR_OBJECT_ID = 9398;
    private static final int[] DOOR_BOUNDS = new int[]{ -16, 32, -224, 0, 12, 112 };
    private static final Tile DOOR_TILE = new Tile(3097, 3107);

    public RunescapeGuide(ClientContext ctx) {
        super(ctx, "RuneScape Guide", 0x0, 0xA, DOOR_TILE);
    }

    @Override
    public boolean checks() {
        return super.checks() && ctx.varpbits.varpbit(22) == 0x2000000;
    }

    @Override
    protected void execute(int progress) {
        switch (progress) {
            case 0x0:
            case 0x7:
                setStatus("Talking to RuneScape Guide");
                ctx.npcs.npc(RUNESCAPE_GUIDE_NPC_ID).talkTo();
                break;
            case 0x3:
                setStatus("Opening options tab");
                ctx.game.tab(Game.Tab.OPTIONS);
                break;
        }
    }

    @Override
    protected void leaveArea() {
        setStatus("Opening door");
        ctx.objects.object(DOOR_OBJECT_ID).setBounds(DOOR_BOUNDS).setReachableTile(DOOR_TILE).walkThroughDoor();
    }
}
