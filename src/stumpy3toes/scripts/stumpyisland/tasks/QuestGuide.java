package stumpy3toes.scripts.stumpyisland.tasks;

import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Game;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.scripts.stumpyisland.StumpyIslandTask;

public class QuestGuide extends StumpyIslandTask {
    private static final int QUEST_GUIDE_NPC_ID = 3312;

    private static final int LADDER_CAMERA_MIN_YAW = 80;
    private static final int LADDER_CAMERA_MAX_YAW = 250;

    private static final int LADDER_OBJECT_ID = 9726;
    private static final int[] LADDER_BOUNDS = { -48, 36, -112, 0, -56, 4 };
    private static final Tile LADDER_TILE = new Tile(3088, 3120);

    public QuestGuide(ClientContext ctx) {
        super(ctx, "Quest Guide", 0xDC, 0xFA, LADDER_TILE);
    }

    @Override
    protected void execute(int progress) {
        switch (progress) {
            case 0xF0:
            case 0xDC:
                setStatus("Talking to Quest Guide");
                ctx.npcs.npc(QUEST_GUIDE_NPC_ID).talkTo();
                break;
            case 0xE6:
                setStatus("Opening quests tab");
                ctx.game.tab(Game.Tab.QUESTS);
                break;
        }
    }

    @Override
    protected void leaveArea() {
        if (ctx.camera.yaw() < LADDER_CAMERA_MIN_YAW || ctx.camera.yaw() >= LADDER_CAMERA_MAX_YAW) {
            setStatus("Turning the camera towards the ladder");
            if (!ctx.camera.angle(Random.nextInt(LADDER_CAMERA_MIN_YAW, LADDER_CAMERA_MAX_YAW))) {
                return;
            }
        }
        setStatus("Climbing down ladder");
        ctx.objects.object(LADDER_OBJECT_ID).setBounds(LADDER_BOUNDS).setReachableTile(LADDER_TILE).climb();
    }
}