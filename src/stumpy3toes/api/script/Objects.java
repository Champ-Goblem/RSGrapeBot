package stumpy3toes.api.script;

import org.powerbot.script.Locatable;
import org.powerbot.script.rt4.BasicQuery;
import stumpy3toes.api.script.wrappers.GameObject;

import java.util.ArrayList;
import java.util.List;

public class Objects extends BasicQuery<GameObject> {
    public final ClientContext ctx;

    public Objects(final ClientContext ctx) {
        super(ctx);
        this.ctx = ctx;
    }

    private GameObject wrap(org.powerbot.script.rt4.GameObject object) {
        return new GameObject(ctx, object);
    }

    public BasicQuery<GameObject> select(final int radius) {
        return select(get(radius));
    }

    public BasicQuery<GameObject> select(final Locatable l, final int radius) {
        return select(get(l, radius));
    }

    @Override
    public List<GameObject> get() {
        return get(Integer.MAX_VALUE);
    }

    public List<GameObject> get(final int radius) {
        return get(ctx.players.local(), radius);
    }

    public List<GameObject> get(final Locatable l, int radius) {
        final List<GameObject> objects = new ArrayList<GameObject>();
        for (org.powerbot.script.rt4.GameObject object : ctx.pbObjects.get(l, radius)) {
            objects.add(wrap(object));
        }
        return objects;
    }

    public GameObject object(int id, int radius) {
        return select(radius).id(id).nearest().peek();
    }

    public GameObject object(int id) {
        return object(id, 30);
    }

    @Override
    public GameObject nil() {
        return wrap(ctx.pbObjects.nil());
    }
}
