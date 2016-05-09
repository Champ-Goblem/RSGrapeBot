package stumpy3toes.api.script;

import org.powerbot.script.rt4.BasicQuery;
import stumpy3toes.api.script.wrappers.Npc;

import java.util.ArrayList;
import java.util.List;

public class Npcs extends BasicQuery<Npc> {
    public final ClientContext ctx;

    public Npcs(final ClientContext ctx) {
        super(ctx);
        this.ctx = ctx;
    }

    private Npc wrap(org.powerbot.script.rt4.Npc npc) {
        return new Npc(ctx, npc);
    }

    @Override
    public List<Npc> get() {
        final List<Npc> npcs = new ArrayList<Npc>();
        for (org.powerbot.script.rt4.Npc npc : ctx.pbNpcs.get()) {
            npcs.add(wrap(npc));
        }
        return npcs;
    }

    public Npc npc(int id) {
        return select().id(id).nearest().peek();
    }

    @Override
    public Npc nil() {
        return wrap(ctx.pbNpcs.nil());
    }
}
