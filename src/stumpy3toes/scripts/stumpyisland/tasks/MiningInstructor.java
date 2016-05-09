package stumpy3toes.scripts.stumpyisland.tasks;

import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Game;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.scripts.stumpyisland.StumpyIslandTask;

import java.util.concurrent.Callable;

public class MiningInstructor extends StumpyIslandTask {
    private static final int MINING_INSTRUCTOR_NPC_ID = 3311;
    private static final Tile MINING_INSTRUCTOR_TILE = new Tile(3081, 9505);

    private static final int TUTORIAL_CHAT_WIDGET_ID = 372;
    private static final int TUTORIAL_CHAT_TITLE_COMPONENT_ID = 0;

    private static final int TIN_ORE_ROCK_OBJECT_ID = 10080;
    private static final int COPPER_ORE_ROCK_OBJECT_ID = 10079;

    private static final int TIN_ORE_ITEM_ID = 438;
    private static final int FURNACE_OBJECT_ID = 10082;

    private static final int ANVIL_OBJECT_ID = 2097;

    private static final int SMITHING_WIDGET_ID = 312;
    private static final int SMITHING_DAGGER_COMPONENT_ID = 2;

    private static final int GATE_OBJECT_ID = 9717;
    private static final int[] GATE_BOUNDS = { 104, 140, -288, 0, 24, 236 };

    public MiningInstructor(ClientContext ctx) {
        super(ctx, "Mining Instructor", 0x104, 0x168, MINING_INSTRUCTOR_TILE);
    }

    @Override
    protected void execute(int progress) {
        switch (progress) {
            case 0x14A:
            case 0x122:
            case 0x104:
                setStatus("Talking to the Mining Instructor");
                ctx.npcs.npc(MINING_INSTRUCTOR_NPC_ID).setReachableTile(MINING_INSTRUCTOR_TILE).talkTo();
                break;
            case 0x10E:
                prospectRock("Tin", TIN_ORE_ROCK_OBJECT_ID);
                break;
            case 0x118:
                prospectRock("Copper", COPPER_ORE_ROCK_OBJECT_ID);
                break;
            case 0x12C:
                mineRock("Tin", TIN_ORE_ROCK_OBJECT_ID);
                break;
            case 0x136:
                mineRock("Copper", COPPER_ORE_ROCK_OBJECT_ID);
                break;
            case 0x140:
                setStatus("Smelting bronze bar");
                ctx.game.tab(Game.Tab.INVENTORY);
                if (ctx.inventory.item(TIN_ORE_ITEM_ID).use(ctx.objects.object(FURNACE_OBJECT_ID), ctx.checks.animated)) {
                    ctx.players.local().waitForIdle();
                }
                break;
            case 0x15E:
            case 0x154:
                ctx.game.tab(Game.Tab.INVENTORY);
                if (smithDaggerComponent().click()) {
                    ctx.players.local().waitForAnimated();
                } else if (ctx.players.local().animated()) {
                    setStatus("Smithing bronze dagger");
                    ctx.players.local().waitForIdle();
                } else {
                    setStatus("Using anvil");
                    ctx.objects.object(ANVIL_OBJECT_ID).walkingInteraction("Smith", new Condition.Check() {
                        @Override
                        public boolean poll() {
                            return smithDaggerComponent().visible();
                        }
                    });
                }
                break;
        }
    }

    private Component smithDaggerComponent() {
        return ctx.widgets.component(SMITHING_WIDGET_ID, SMITHING_DAGGER_COMPONENT_ID);
    }

    private boolean pleaseWait() {
        return ctx.widgets.component(TUTORIAL_CHAT_WIDGET_ID, TUTORIAL_CHAT_TITLE_COMPONENT_ID).text()
                .equals("Please wait.");
    }

    private void prospectRock(String name, int rockID) {
        setStatus("Prospecting " + name + " rock");
        if (ctx.objects.object(rockID).walkingInteraction("Prospect", new Condition.Check() {
            @Override
            public boolean poll() {
                return pleaseWait();
            }
        })) {
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !pleaseWait();
                }
            }, 500, 6);
        }
    }

    private void mineRock(String name, int rockID) {
        if (!ctx.players.local().animated()) {
            setStatus("Walking to " + name + " rock");
            ctx.objects.object(rockID).walkingInteraction("Mine", ctx.checks.animated);
        } else {
            setStatus("Mining " + name + " rock");
            ctx.players.local().waitForIdle();
        }
    }

    @Override
    protected void leaveArea() {
        setStatus("Opening gate");
        ctx.objects.object(GATE_OBJECT_ID).setBounds(GATE_BOUNDS).walkThroughDoor();
    }
}