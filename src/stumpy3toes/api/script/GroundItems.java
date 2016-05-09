package stumpy3toes.api.script;

import org.powerbot.script.rt4.BasicQuery;
import stumpy3toes.api.script.wrappers.GroundItem;

import java.util.ArrayList;
import java.util.List;

public class GroundItems extends BasicQuery<GroundItem> {
    public final ClientContext ctx;

    public GroundItems(ClientContext ctx) {
        super(ctx);
        this.ctx = ctx;
    }

    private GroundItem wrap(org.powerbot.script.rt4.GroundItem groundItem) {
        return new GroundItem(ctx, groundItem);
    }

    public BasicQuery<GroundItem> select(final int radius) {
        return select(get(radius));
    }

    @Override
    public List<GroundItem> get() {
        return get(104);
    }

    private List<GroundItem> get(int radius) {
        final List<GroundItem> groundItems = new ArrayList<GroundItem>();
        for (org.powerbot.script.rt4.GroundItem groundItem : ctx.pbGroundItems.get()) {
            groundItems.add(wrap(groundItem));
        }
        return groundItems;
    }

    @Override
    public GroundItem nil() {
        return wrap(ctx.pbGroundItems.nil());
    }
}
