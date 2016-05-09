package stumpy3toes.scripts.stumpyisland.tasks;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Game;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.api.script.wrappers.Item;
import stumpy3toes.api.script.wrappers.Npc;
import stumpy3toes.scripts.stumpyisland.StumpyIslandTask;

import java.util.concurrent.Callable;

public class CombatInstructor extends StumpyIslandTask {
    private static final int COMBAT_INSTRUCTOR_NPC_ID = 3307;

    private static final int EQUIPMENT_STATS_BUTTON_COMPONENT_ID = 18;

    private static final int EQUIPMENT_STATS_INTERFACE_WIDGET_ID = 84;
    private static final int EQUIPMENT_STATS_INTERFACE_CLOSE_BUTTON_COMPONENT_ID = 4;
    private static final int EQUIPMENT_STATS_INTERFACE_INVENTORY_WIDGET_ID = 85;
    private static final int EQUIPMENT_STATS_INTERFACE_INVENTORY_COMPONENT_ID = 0;

    private static final int BRONZE_DAGGER_ITEM_ID = 1205;
    private static final int BRONZE_SWORD_ITEM_ID = 1277;
    private static final int WOODEN_SHIELD_ITEM_ID = 1171;

    private static final int GATE_OBJECT_ID = 9720;
    private static final int[] GATE_BOUNDS = { -24, 24, -188, 0, -100, 92 };
    private static final Tile GATE_TILE = new Tile(3110, 9519);

    private static final int RAT_NPC_ID = 3313;

    private static final int SHORTBOW_ITEM_ID = 841;
    private static final int BRONZE_ARROWS_ITEM_ID = 882;

    private static final int LADDER_OBJECT_ID = 9727;
    private static final int[] LADDER_BOUNDS = { -36, 36, -188, 0, -24, 24 };
    private static final Tile LADDER_TILE = new Tile(3111, 9525);

    public CombatInstructor(ClientContext ctx) {
        super(ctx, "Combat Instructor", 0x172, 0x1F4, LADDER_TILE);
    }

    @Override
    protected void execute(int progress) {
        switch (progress) {
            case 0x1D6:
                if (ctx.movement.reachable(GATE_TILE)) {
                    setStatus("Exiting rat pen");
                    if (ctx.objects.object(GATE_OBJECT_ID).setBounds(GATE_BOUNDS).setReachableTile(GATE_TILE)
                            .walkThroughDoor()) {
                        ctx.players.local().waitForIdle();
                    }
                    return;
                }
            case 0x19A:
                if (equipmentInterfaceOpen()) {
                    setStatus("Closing equipment stats interface");
                    if (equipmentInterfaceCloseButton().click()) {
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return !equipmentInterfaceOpen();
                            }
                        }, 100, 10);
                    }
                    return;
                }
            case 0x172:
                setStatus("Talking to Combat Instructor");
                ctx.npcs.npc(COMBAT_INSTRUCTOR_NPC_ID).talkTo();
                break;
            case 0x186:
                setStatus("Opening equipment tab");
                ctx.game.tab(Game.Tab.EQUIPMENT);
                break;
            case 0x195:
            case 0x190:
                if (!equipmentInterfaceOpen()) {
                    setStatus("Opening equipment stats interface");
                    if (ctx.widgets.component(Constants.EQUIPMENT_WIDGET,
                            EQUIPMENT_STATS_BUTTON_COMPONENT_ID).click()) {
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return equipmentInterfaceOpen();
                            }
                        }, 100, 10);
                    }
                } else {
                    setStatus("Equipping bronze dagger");
                    final Component dagger = equipmentInterfaceDagger();
                    if (dagger.interact("Equip")) {
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return !dagger.valid();
                            }
                        }, 100, 10);
                    }
                }
                break;
            case 0x1A4:
                wieldItem(BRONZE_SWORD_ITEM_ID);
                wieldItem(WOODEN_SHIELD_ITEM_ID);
                break;
            case 0x1AE:
                setStatus("Opening attack tab");
                ctx.game.tab(Game.Tab.ATTACK);
                break;
            case 0x1B8:
            case 0x1C2:
            case 0x1CC:
                if (!ctx.movement.reachable(GATE_TILE)) {
                    setStatus("Entering rat pen");
                    ctx.objects.object(GATE_OBJECT_ID).setBounds(GATE_BOUNDS).walkThroughDoor();
                } else {
                    attackRat();
                }
                break;
            case 0x1E0:
            case 0x1EA:
                if (wieldItem(SHORTBOW_ITEM_ID) && wieldItem(BRONZE_ARROWS_ITEM_ID)) {
                    attackRat();
                }
                break;
        }
    }

    private Component equipmentInterfaceCloseButton() {
        return ctx.widgets.component(EQUIPMENT_STATS_INTERFACE_WIDGET_ID,
                EQUIPMENT_STATS_INTERFACE_CLOSE_BUTTON_COMPONENT_ID);
    }

    private boolean equipmentInterfaceOpen() {
        return equipmentInterfaceCloseButton().visible();
    }

    private Component equipmentInterfaceDagger() {
        for (Component component : ctx.widgets.component(EQUIPMENT_STATS_INTERFACE_INVENTORY_WIDGET_ID,
                EQUIPMENT_STATS_INTERFACE_INVENTORY_COMPONENT_ID).components()) {
            if (component.itemId() == BRONZE_DAGGER_ITEM_ID) {
                return component;
            }
        }
        return ctx.widgets.component(-1, -1);
    }

    private boolean wieldItem(int itemID) {
        ctx.game.tab(Game.Tab.INVENTORY);
        final Item item = ctx.inventory.item(itemID);
        if (item.valid()) {
            setStatus("Wielding " + item.name());
            return item.interact("Wield") && Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !item.valid();
                }
            }, 100, 10);
        }
        return true;
    }

    private void attackRat() {
        if (ctx.players.local().inCombat()) {
            setStatus("Attacking rat");
            ctx.players.local().waitForIdle();
        } else {
            setStatus("Targeting rat");
            ctx.npcs.select().id(RAT_NPC_ID).select(new Filter<Npc>() {
                @Override
                public boolean accept(Npc npc) {
                    return !npc.inCombat();
                }
            }).nearest().peek().attack();
        }
    }

    @Override
    protected void leaveArea() {
        setStatus("Climbing ladder");
        ctx.objects.object(LADDER_OBJECT_ID).setBounds(LADDER_BOUNDS).setReachableTile(LADDER_TILE).climb();
    }
}