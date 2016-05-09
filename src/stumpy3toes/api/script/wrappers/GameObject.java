package stumpy3toes.api.script.wrappers;

import org.powerbot.script.*;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.api.script.InteractableEntity;

import java.awt.*;
import java.util.concurrent.Callable;

public class GameObject extends InteractableEntity implements Nameable, Identifiable, Validatable, Actionable {
    private final org.powerbot.script.rt4.GameObject object;

    public GameObject(ClientContext ctx, org.powerbot.script.rt4.GameObject object) {
        super(ctx);
        this.object = object;
    }

    @Override
    public GameObject setReachableTile(Tile tile) {
        return (GameObject)super.setReachableTile(tile);
    }

    @Override
    public GameObject setBounds(int[] bounds) {
        return (GameObject)super.setBounds(bounds);
    }

    public boolean use(Condition.Check check) {
        return walkingInteraction("use", check);
    }

    public boolean open() {
        return walkingInteraction("open", new Condition.Check() {
            @Override
            public boolean poll() {
                return !valid();
            }
        });
    }

    public boolean walkThroughDoor() {
        return open() && Condition.wait(ctx.checks.idle, 100, 15);
    }

    private boolean climb(String interact) {
        if (walkingInteraction(interact, ctx.checks.animated)) {
            final Tile location = ctx.players.local().tile();
            return Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.players.local().tile().equals(location);
                }
            }, 100, 10) && ctx.players.local().waitForIdle();
        }
        return false;
    }

    public boolean climb() {
        return climb("Climb");
    }

    public boolean climb(boolean upwards) {
        return climb(upwards ? "Climb-up" : "Climb-down");
    }

    @Override
    public void bounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
        object.bounds(x1, x2, y1, y2, z1, z2);
    }

    public int mainId() {
        return object.mainId();
    }

    @Override
    public int id() {
        return object.id();
    }

    @Override
    public String name() {
        return object.name();
    }

    public int[] colors1() {
        return object.colors1();
    }

    public int[] colors2() {
        return object.colors2();
    }

    public int width() {
        return object.width();
    }

    public int height() {
        return object.height();
    }

    public int[] meshIds() {
        return object.meshIds();
    }

    public String[] actions() {
        return object.actions();
    }

    public int orientation() {
        return object.orientation();
    }

    public org.powerbot.script.rt4.GameObject.Type type() {
		return object.type();
    }

    public int relative() {
        return object.relative();
    }

    @Override
    public boolean valid() {
        return object.valid();
    }

    @Override
    public Tile tile() {
        return object.tile();
    }

    @Override
    public Point centerPoint() {
        return object.centerPoint();
    }

    @Override
    public Point nextPoint() {
        return object.nextPoint();
    }

    @Override
    public boolean contains(final Point point) {
        return object.contains(point);
    }

    @Override
    public String toString() {
        return object.toString();
    }

    @Override
    public int hashCode() {
        return object.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        return o != null && (o instanceof org.powerbot.script.rt4.GameObject && object.equals(o))
                || (o instanceof GameObject && object.equals(((GameObject)o).object));
    }
}
