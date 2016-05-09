package stumpy3toes.api.script.wrappers;

import org.powerbot.script.*;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.api.script.InteractiveEntity;
import stumpy3toes.api.script.GenericItem;

import java.awt.*;

public class GroundItem extends GenericItem implements Nameable, InteractiveEntity, Identifiable, Validatable, Actionable {
    private final org.powerbot.script.rt4.GroundItem groundItem;

    public GroundItem(ClientContext ctx, org.powerbot.script.rt4.GroundItem groundItem) {
        super(ctx);
        this.groundItem = groundItem;
    }

    @Override
    public boolean reachable() {
        return ctx.movement.reachable(this);
    }

    @Override
    public int distance() {
        return ctx.movement.distance(this);
    }

    @Override
    public double tileDistance() {
        return ctx.movement.distanceToTile(this);
    }

    @Override
    public boolean walk() {
        return ctx.movement.walk(this);
    }

    @Override
    public boolean walkInViewport() {
        return ctx.movement.viewport(this);
    }

    @Override
    public boolean walkingInteraction(String action, Condition.Check check) {
        return ctx.movement.interact(this, action, check);
    }

    @Override
    public boolean walkingInteraction(String action, String option, Condition.Check check) {
        return ctx.movement.interact(this, action, option, check);
    }

    @Override
    public boolean walkingInteraction(Filter<? super MenuCommand> f, Condition.Check check) {
        return ctx.movement.interact(this, f, check);
    }

    @Override
    public void bounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
        groundItem.bounds(x1, x2, y1, y2, z1, z2);
    }

    @Override
    public int id() {
        return groundItem.id();
    }

    public int stackSize() {
        return groundItem.stackSize();
    }

    @Override
    public Point centerPoint() {
        return groundItem.centerPoint();
    }

    @Override
    public Point nextPoint() {
        return groundItem.nextPoint();
    }

    @Override
    public boolean contains(final Point point) {
        return groundItem.contains(point);
    }

    @Override
    public Tile tile() {
        return groundItem.tile();
    }

    @Override
    public int hashCode() {
        return groundItem.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        return o != null && (o instanceof org.powerbot.script.rt4.GroundItem && groundItem.equals(o))
                || (o instanceof GroundItem && groundItem.equals(((GroundItem)o).groundItem));
    }

    @Override
    public String toString() {
        return groundItem.toString();
    }

    @Override
    public String[] actions() {
        return groundItem.actions();
    }

    @Override
    public boolean valid() {
        return groundItem.valid();
    }
}
