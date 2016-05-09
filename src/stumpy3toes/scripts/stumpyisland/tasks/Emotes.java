package stumpy3toes.scripts.stumpyisland.tasks;

import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Game;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.scripts.stumpyisland.StumpyIslandTask;

public class Emotes extends StumpyIslandTask {
    private static final int EMOTES_WIDGET_ID = 216;
    private static final int EMOTE_COMPONENTS_PARENT_ID = 1;

    private static final int DOOR_OBJECT_ID = 9716;
    private static final int[] DOOR_BOUNDS = new int[]{ 12, 120, -220, 0, -16, 24 };
    private static final Tile DOOR_TILE = new Tile(3086, 3126);

    public Emotes(ClientContext ctx) {
        super(ctx, "Emotes", 0xB7, 0xD2, DOOR_TILE);
    }

    @Override
    protected void execute(int progress) {
        switch (progress) {
            case 0xB7:
                setStatus("Opening emotes tab");
                ctx.game.tab(Game.Tab.EMOTES);
                break;
            case 0xBB:
                setStatus("Doing random emote");
                if (ctx.players.local().animated()) {
                    ctx.players.local().waitForIdle();
                } else if (ctx.widgets.component(EMOTES_WIDGET_ID, EMOTE_COMPONENTS_PARENT_ID)
                        .component(Random.nextInt(0, 16)).click()) {
                    ctx.players.local().waitForAnimated();
                }
                break;
            case 0xBE:
                setStatus("Opening options tab");
                ctx.game.tab(Game.Tab.OPTIONS);
                break;
            case 0xC8:
                setStatus("Turning on run");
                ctx.movement.running(true);
                break;
        }
    }

    @Override
    protected void leaveArea() {
        setStatus("Opening door");
        ctx.objects.object(DOOR_OBJECT_ID).setBounds(DOOR_BOUNDS).setReachableTile(DOOR_TILE).walkThroughDoor();
    }
}