package stumpy3toes.scripts.stumpyisland.tasks;

import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Widget;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.api.script.wrappers.Npc;
import stumpy3toes.scripts.stumpyisland.StumpyIslandTask;

import java.util.concurrent.Callable;

public class FinancialAdvisor extends StumpyIslandTask {
    private static final int BANKER_NPC_ID = 3318;
    private static final Tile BANKER_TILE = new Tile(3120, 3123);

    private static final int POLL_BOOTH_OBJECT_ID = 26801;
    private static final int POLL_BOOTH_HISTORY_INTERFACE_WIDGET_ID = 310;
    private static final int POLL_BOOTH_INTERFACE_WIDGET_ID = 345;
    private static final int POLL_BOOTH_INTERFACE_MAIN_COMPONENT_ID = 1;
    private static final int POLL_BOOTH_CLOSE_BUTTON_COMPONENT_ID = 11;

    private static final int DOOR_OBJECT_ID = 9721;
    private static final int[] DOOR_BOUNDS = { -8, 28, -200, 0, 16, 120 };
    private static final Tile DOOR_TILE = new Tile(3124, 3124);

    private static final int FINANCIAL_ADVISOR_NPC_ID = 3310;

    private static final int EXIT_DOOR_OBJECT_ID = 9722;
    private static final int[] EXIT_DOOR_BOUNDS = { -12, 24, -216, 0, 16, 116 };
    private static final Tile EXIT_DOOR_TILE = new Tile(3129, 3124);

    public FinancialAdvisor(ClientContext ctx) {
        super(ctx, "Financial Advisor", 0x1FE, 0x21C, BANKER_TILE, EXIT_DOOR_TILE);
    }

    @Override
    protected void execute(int progress) {
        switch (progress) {
            case 0x1FE:
                if (!ctx.widgets.component(Constants.CHAT_WIDGET, 0).visible()) {
                    setStatus("Walking to banker");
                    ctx.npcs.npc(BANKER_NPC_ID).setReachableTile(BANKER_TILE).talkTo();
                } else {
                    setStatus("Talking to banker");
                    if (ctx.chat.continueChat(true, "Yes.")) {
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return progress() == 0x208;
                            }
                        }, 100, 10);
                    }
                }
                break;
            case 0x208:
                if (ctx.bank.opened()) {
                    setStatus("Closing bank");
                    ctx.bank.close();
                } else if (!pollBoothOpen()) {
                    setStatus("Viewing poll booth");
                    if (ctx.objects.object(POLL_BOOTH_OBJECT_ID).walkingInteraction("Use", ctx.checks.idle)) {
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return ctx.chat.canContinue();
                            }
                        }, 250, 6);
                    }
                }
                break;
            case 0x20D:
                if (pollBoothOpen()) {
                    setStatus("Closing poll booth");
                    if (pollBoothCloseButton().click()) {
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return !pollBoothOpen();
                            }
                        }, 250, 4);
                    }
                } else {
                    leaveArea();
                }
                break;
            case 0x212:
                Npc financialAdvisor = ctx.npcs.npc(FINANCIAL_ADVISOR_NPC_ID);
                if (financialAdvisor.reachable()) {
                    setStatus("Talking to Financial Advisor");
                    financialAdvisor.talkTo();
                } else {
                    leaveArea();
                }
                break;
        }
    }

    private Component pollBoothCloseButton() {
        Widget widget = ctx.widgets.widget(POLL_BOOTH_INTERFACE_WIDGET_ID);
        if (!widget.valid()) {
            widget = ctx.widgets.widget(POLL_BOOTH_HISTORY_INTERFACE_WIDGET_ID);
        }
        return widget.component(POLL_BOOTH_INTERFACE_MAIN_COMPONENT_ID).component(POLL_BOOTH_CLOSE_BUTTON_COMPONENT_ID);
    }

    private boolean pollBoothOpen() {
        return pollBoothCloseButton().visible();
    }

    @Override
    protected void leaveArea() {
        if (ctx.movement.reachable(BANKER_TILE)) {
            setStatus("Opening door");
            ctx.objects.object(DOOR_OBJECT_ID).setBounds(DOOR_BOUNDS).setReachableTile(DOOR_TILE).walkThroughDoor();
        } else {
            setStatus("Exiting bank");
            ctx.objects.object(EXIT_DOOR_OBJECT_ID).setBounds(EXIT_DOOR_BOUNDS).setReachableTile(EXIT_DOOR_TILE).walkThroughDoor();
        }
    }
}