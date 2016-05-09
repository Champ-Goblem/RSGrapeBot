package stumpy3toes.api.script;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.MenuCommand;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Interactive;

public abstract class InteractableEntity extends Interactive implements InteractiveEntity {
    public final ClientContext ctx;
    private Tile reachableTile = Tile.NIL;

    public InteractableEntity(ClientContext ctx) {
        super(ctx);
        this.ctx = ctx;
    }

    public InteractableEntity setReachableTile(Tile tile) {
        if (tile == null) {
            tile = Tile.NIL;
        }
        reachableTile = tile;
        return this;
    }

    public Tile getReachableTile() {
        if (!reachableTile.equals(Tile.NIL) && ctx.movement.reachable(reachableTile)) {
            return reachableTile;
        }
        return tile();
    }

    public InteractableEntity setBounds(int[] bounds) {
        bounds(bounds);
        return this;
    }

    @Override
    public boolean reachable() {
        return ctx.movement.reachable(this);
    }

    @Override
    public int distance() {
        return ctx.movement.distance(getReachableTile());
    }

    @Override
    public double tileDistance() {
        return ctx.movement.distanceToTile(tile());
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
}
