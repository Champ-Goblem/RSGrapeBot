package stumpy3toes.api.script.wrappers;

import org.powerbot.script.Actionable;
import org.powerbot.script.Condition;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Tile;
import stumpy3toes.api.script.ClientContext;

public class Npc extends Actor implements Identifiable, Actionable {
    private final org.powerbot.script.rt4.Npc npc;

    public Npc(ClientContext ctx, org.powerbot.script.rt4.Npc npc) {
        super(ctx, npc);
        this.npc = npc;
    }

    @Override
    public Npc setReachableTile(Tile tile) {
        return (Npc)super.setReachableTile(tile);
    }

    @Override
    public Npc setBounds(int[] bounds) {
        return (Npc)super.setBounds(bounds);
    }

    public boolean talkTo() {
        return walkingInteraction("Talk-to", ctx.checks.interacting(this))
                && Condition.wait(ctx.checks.chatting, 100, 10);
    }

    public boolean attack() {
        return walkingInteraction("Attack", ctx.checks.inCombat);
    }

    @Override
    public int id() {
        return npc.id();
    }

    @Override
    public String[] actions() {
        return npc.actions();
    }

    @Override
    public boolean valid() {
        return npc.valid();
    }
}
