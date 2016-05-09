package stumpy3toes.api.script.wrappers;

import org.powerbot.script.Condition;
import org.powerbot.script.Nameable;
import org.powerbot.script.Tile;
import org.powerbot.script.Validatable;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.api.script.InteractableEntity;

import java.awt.*;

public class Actor extends InteractableEntity implements Nameable, Validatable {
    private final org.powerbot.script.rt4.Actor actor;

    public Actor(final ClientContext ctx, org.powerbot.script.rt4.Actor actor) {
        super(ctx);
        this.actor = actor;
    }

    @Override
    public Actor setReachableTile(Tile tile) {
        return (Actor)super.setReachableTile(tile);
    }

    @Override
    public Actor setBounds(int[] bounds) {
        return (Actor)super.setBounds(bounds);
    }

    public boolean isInteracting() {
        return interacting().valid();
    }

    public boolean animated() {
        return animation() != -1;
    }

    public boolean idle() {
        return !inMotion() && !inCombat() && !animated();
    }

    public boolean inCombat() {
        Actor enemy = interacting();
        return oldInCombat() || (enemy.interacting().equals(this) && enemy.oldInCombat());
    }

    public boolean oldInCombat() {
        return actor.inCombat();
    }

    public boolean waitForIdle() {
        return Condition.wait(ctx.checks.idle, 100, 10);
    }

    public boolean waitForAnimated() {
        return Condition.wait(ctx.checks.animated, 100, 10);
    }

    public boolean waitForAnimation(final int animationID) {
        return Condition.wait(ctx.checks.animated(animationID), 100, 10);
    }

    @Override
    public void bounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
        actor.bounds(x1, x2, y1, y2, z1, z2);
    }

    public String name() {
        return actor.name();
    }

    public int combatLevel() {
        return actor.combatLevel();
    }

    public int animation() {
        return actor.animation();
    }

    public int speed() {
        return actor.speed();
    }

    public int orientation() {
        return actor.orientation();
    }

    public String overheadMessage() {
        return actor.overheadMessage();
    }

    public boolean inMotion() {
        return actor.inMotion();
    }

    public int health() {
        return actor.health();
    }

    public int maxHealth() {
        return actor.maxHealth();
    }

    public Actor interacting() {
        return new Actor(ctx, actor.interacting());
    }

    public int relative() {
        return actor.relative();
    }

    @Override
    public Tile tile() {
        return actor.tile();
    }

    @Override
    public Point nextPoint() {
        return actor.nextPoint();
    }

    @Override
    public Point centerPoint() {
        return actor.centerPoint();
    }

    @Override
    public boolean contains(final Point point) {
        return actor.contains(point);
    }

    @Override
    public boolean valid() {
        return actor.valid();
    }

    @Override
    public boolean equals(final Object o) {
        return o != null && (o instanceof org.powerbot.script.rt4.Actor && actor.equals(o))
                || (o instanceof Actor && actor.equals(((Actor)o).actor));
    }

    @Override
    public int hashCode() {
        return actor.hashCode();
    }
}
