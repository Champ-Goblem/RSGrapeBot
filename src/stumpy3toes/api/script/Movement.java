package stumpy3toes.api.script;

import org.powerbot.script.*;
import org.powerbot.script.InteractiveEntity;
import org.powerbot.script.rt4.Menu;

import java.util.concurrent.Callable;

public class Movement extends org.powerbot.script.rt4.Movement {
    public final ClientContext ctx;

    public Movement(ClientContext ctx) {
        super(ctx);
        this.ctx = ctx;
    }

    private Tile getReachableTile(Locatable locatable) {
        if (locatable instanceof InteractableEntity) {
            return ((InteractableEntity) locatable).getReachableTile();
        }
        return locatable.tile();
    }

    public boolean reachable(Locatable locatable) {
        return reachable(ctx.players.local(), locatable);
    }

    public double distanceToTile(Locatable locatable) {
        return ctx.players.local().tile().distanceTo(locatable);
    }

    public int distanceToDestination() {
        return distance(destination());
    }

    public int distanceFromDestination(Locatable locatable) {
        return distance(destination(), locatable);
    }

    public double distanceToDestinationTile() {
        return distanceToTile(destination());
    }

    public double distanceFromDestinationTile(Locatable locatable) {
        return destination().distanceTo(locatable);
    }

    public <T extends Locatable & Validatable> boolean walk(final T entity, boolean run) {
        if (!entity.valid() && getReachableTile(entity).equals(Tile.NIL)) {
            return false;
        }
        if (distanceToTile(entity) <= 2) {
            return true;
        }
        Tile tile = getReachableTile(entity);
        if (distanceFromDestinationTile(tile) > 2) {
            if (reachable(tile)) {
                return findPath(tile).traverse();
            }
            if (!ctx.movement.running() && ctx.movement.energyLevel() >= 20) {
                ctx.movement.running(true);
            }
            if (ctx.players.local().inMotion() && !Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return distanceToDestinationTile() < 5;
                }
            }, 100, 10)) {
                return false;
            }
            if (!step(tile)) {
                return false;
            }
        }
        return Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return distanceToTile(entity) <= 2;
            }
        }, 100, 10);
    }

    public <T extends Locatable & Validatable> boolean walk(final T entity) {
        return walk(entity, true);
    }

    public <T extends Locatable & Validatable & Viewable> boolean viewport(final T entity) {
        if (entity.inViewport()) {
            return true;
        }
        walk(entity);
        if (distanceToTile(entity) < 4 && !entity.inViewport()) {
            ctx.camera.turnTo(entity);
        }
        return entity.inViewport();
    }

    public boolean interact(InteractiveEntity entity, Filter<? super MenuCommand> f, final Condition.Check check) {
        if (viewport(entity) && entity.interact(f)) {
            int distance = distance(getReachableTile(entity));
            if (distance == -1) {
                distance = (int)Math.ceil(distanceToTile(entity));
            }
            InteractionCheck interactionCheck = new InteractionCheck(check, true);
            if (Condition.wait(interactionCheck, 100, 10)) {
                if (interactionCheck.conditionMet) {
                    return true;
                }
                interactionCheck.inMotion = false;
                return Condition.wait(interactionCheck, 100, Math.max(10, Math.min(5, distance) * (running() ? 7 : 10)))
                        && (interactionCheck.conditionMet || Condition.wait(check, 100, 5));
            }
        }
        return false;
    }

    public boolean interact(InteractiveEntity entity, String action, String option, Condition.Check check) {
        return interact(entity, Menu.filter(action, option), check);
    }

    public boolean interact(InteractiveEntity entity, String action, Condition.Check check) {
        return interact(entity, Menu.filter(action), check);
    }

    private class InteractionCheck extends Condition.Check {
        public boolean conditionMet = false;
        public boolean inMotion;

        private final Condition.Check check;

        public InteractionCheck(Condition.Check check, boolean inMotion) {
            this.check = check;
            this.inMotion = inMotion;
        }

        @Override
        public boolean poll() {
            if (check.poll()) {
                conditionMet = true;
            }
            return conditionMet || ctx.players.local().inMotion() == inMotion;
        }
    }
}