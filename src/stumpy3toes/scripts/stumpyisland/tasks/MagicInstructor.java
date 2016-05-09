package stumpy3toes.scripts.stumpyisland.tasks;

import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.api.script.wrappers.Npc;
import stumpy3toes.api.util.Utils;
import stumpy3toes.scripts.stumpyisland.AccountType;
import stumpy3toes.scripts.stumpyisland.StumpyIsland;
import stumpy3toes.scripts.stumpyisland.StumpyIslandTask;

import java.util.concurrent.Callable;

public class MagicInstructor extends StumpyIslandTask {
    public AccountType accountType;
    public boolean enterMainland;
    public boolean userEnterPin;
    public long startTime;

    private static final int MAGIC_INSTRUCTOR_NPC_ID = 3309;
    private static final Tile MAGIC_INSTRUCTOR_TILE = new Tile(3141, 3087);

    private static final int CHICKEN_NPC_ID = 3316;
    private static final Area CASTING_AREA = new Area(new Tile(3138, 3092), new Tile(3141, 3090));

    private static final int ADAM_NPC_ID = 311;
    private static final int ACCOUNT_TYPE_VARPBIT_ID = 499;
    private static final int STANDARD_ACCOUNT_VARPBIT_VALUE = 0x0;
    private static final int IRONMAN_ACCOUNT_VARPBIT_VALUE = 0x40000000;
    private static final int ULTIMATE_IRONMAN_ACCOUNT_VARPBIT_VALUE = 0x80000000;

    private static final int ADAM_CHAT_OPTIONS_COMPONENT_ID = 0;
    private static final int ADAM_CHAT_OPTIONS_OPEN_IRONMAN_SETTINGS_COMPONENT_ID = 2;

    private static final int IRONMAN_SETTINGS_WIDGET_ID = 215;
    private static final int IRONMAN_SETTINGS_MAIN_COMPONENT_ID = 2;
    private static final int IRONMAN_SETTINGS_MAIN_CLOSE_COMPONENT_ID = 11;
    private static final int IRONMAN_SETTINGS_NONE_COMPONENT_ID = 23;
    private static final int IRONMAN_SETTINGS_STANDARD_COMPONENT_ID = 15;
    private static final int IRONMAN_SETTINGS_ULTIMATE_COMPONENT_ID = 19;

    private boolean revertedRooftopSetting = false;

    public MagicInstructor(ClientContext ctx) {
        super(ctx, "Magic Instructor", 0x26C, 0x3E8, MAGIC_INSTRUCTOR_TILE);
    }

    @Override
    protected void execute(int progress) {
        if(userEnterPin && !revertedRooftopSetting) {
            revertSetting();
        } else if (!isCorrectAccountType()) {
            if (!ironmanComponent().visible()) {
                if (!openIronmanInterfaceChatComponent().visible()) {
                    setStatus("Talking to Adam");
                    ctx.npcs.npc(ADAM_NPC_ID).setReachableTile(MAGIC_INSTRUCTOR_TILE).talkTo();
                    return;
                }
                setStatus("Opening ironman settings interface");
                if (!openIronmanInterfaceChatComponent().click() || !Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ironmanComponent().visible();
                    }
                }, 200, 5)) {
                    return;
                }
            }
            setStatus("Selecting desired account type");
            if (ironmanComponent().click() && Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return isCorrectAccountType();
                }
            }, 100, 5)) {
                if (userEnterPin) {
                    completed();
                } else {
                    setStatus("Closing ironman settings interface");
                }
            }
        } else {
            if (ironmanComponent().visible() && (!ironmanSettingsWidget().component(IRONMAN_SETTINGS_MAIN_COMPONENT_ID)
                    .component(IRONMAN_SETTINGS_MAIN_CLOSE_COMPONENT_ID).click()
                    || !Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ironmanComponent().visible();
                }
            }, 200, 5))) {
                return;
            }
            switch (progress) {
                case 0x26C:
                    talkToInstructor();
                    break;
                case 0x276:
                    setStatus("Opening magic tab");
                    ctx.game.tab(Game.Tab.MAGIC);
                    break;
                case 0x28A:
                    if (CASTING_AREA.contains(ctx.players.local())) {
                        Npc chicken = ctx.npcs.select().id(CHICKEN_NPC_ID).select(new Filter<Npc>() {
                            @Override
                            public boolean accept(Npc npc) {
                                return !npc.inCombat();
                            }
                        }).nearest().peek();
                        if (chicken.valid()) {
                            setStatus("Casting wind strike on chicken");
                            if (chicken.walkInViewport() && ctx.magic.cast(Magic.Spell.WIND_STRIKE)
                                    && chicken.interact("Cast")) {
                                Condition.wait(new Callable<Boolean>() {
                                    @Override
                                    public Boolean call() throws Exception {
                                        return progress() == 0x29E;
                                    }
                                }, 250, 4);
                            }
                        } else {
                            setStatus("Waiting for chicken to spawn");
                            Condition.sleep(100);
                        }
                    } else if (!ctx.players.local().inMotion()) {
                        setStatus("Walking to casting area");
                        ctx.movement.step(CASTING_AREA.getRandomTile());
                    } else {
                        ctx.players.local().waitForIdle();
                    }
                    break;
                case 0x29E:
                    leaveArea();
                    break;
            }
        }
    }

    private boolean isCorrectAccountType() {
        int accountVarpbit = ctx.varpbits.varpbit(ACCOUNT_TYPE_VARPBIT_ID);
        switch (accountType) {
            case NORMAL:
                return accountVarpbit == STANDARD_ACCOUNT_VARPBIT_VALUE;
            case IRONMAN:
                return accountVarpbit == IRONMAN_ACCOUNT_VARPBIT_VALUE;
            case ULTIMATE_IRONMAN:
                return accountVarpbit == ULTIMATE_IRONMAN_ACCOUNT_VARPBIT_VALUE;
        }
        return true;
    }

    private Component openIronmanInterfaceChatComponent() {
        return ctx.widgets.component(Constants.CHAT_WIDGET, ADAM_CHAT_OPTIONS_COMPONENT_ID)
                .component(ADAM_CHAT_OPTIONS_OPEN_IRONMAN_SETTINGS_COMPONENT_ID);
    }

    private Widget ironmanSettingsWidget() {
        return ctx.widgets.widget(IRONMAN_SETTINGS_WIDGET_ID);
    }

    private Component ironmanComponent() {
        int componentID = -1;
        switch (accountType) {
            case NORMAL:
                componentID = IRONMAN_SETTINGS_NONE_COMPONENT_ID;
                break;
            case IRONMAN:
                componentID = IRONMAN_SETTINGS_STANDARD_COMPONENT_ID;
                break;
            case ULTIMATE_IRONMAN:
                componentID = IRONMAN_SETTINGS_ULTIMATE_COMPONENT_ID;
                break;
        }
        return ironmanSettingsWidget().component(componentID);
    }

    private void talkToInstructor() {
        setStatus("Talking to Magic Instructor");
        ctx.npcs.npc(MAGIC_INSTRUCTOR_NPC_ID).setReachableTile(MAGIC_INSTRUCTOR_TILE).talkTo();
    }

    private void revertSetting() {
        ((StumpyIsland) ctx.controller.script()).roofSetting.pause(false);
        revertedRooftopSetting = true;
    }

    private void completed() {
        if (revertedRooftopSetting) {
            if (userEnterPin) {
                System.out.println("Stopping because user wanted to set a pin for their ironman account");
            }
            System.out.println("Script complete in " + Utils.formatTime(System.currentTimeMillis() - startTime));
            ctx.controller.stop();
        } else {
            revertSetting();
        }
    }

    @Override
    protected void leaveArea() {
        if (!userEnterPin && enterMainland && progress() == 0x29E) {
            ChatOption yes = ctx.chat.select().text("Yes.").peek();
            if (yes.valid()) {
                setStatus("Clicking yes to enter mainland");
                if (yes.select(true)) {
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.chat.canContinue();
                        }
                    }, 250, 5);
                }
            } else {
                talkToInstructor();
            }
        } else {
            completed();
        }
    }
}