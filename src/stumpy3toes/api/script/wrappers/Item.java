package stumpy3toes.api.script.wrappers;

import org.powerbot.script.*;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Menu;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.api.script.GenericItem;
import stumpy3toes.api.script.InteractableEntity;

import java.awt.*;
import java.util.concurrent.Callable;

public class Item extends GenericItem implements Identifiable, Nameable, Stackable, Actionable {
    private final org.powerbot.script.rt4.Item item;

    public Item(ClientContext ctx, org.powerbot.script.rt4.Item item) {
        super(ctx);
        this.item = item;
    }

    public boolean isSelected() {
        return ctx.inventory.selectedItem().equals(this);
    }

    public boolean select() {
        if (!valid()) {
            return false;
        }

        if (!isSelected()) {
            Item selectedItem = ctx.inventory.selectedItem();
            if (selectedItem.valid() && (!selectedItem.interact("Use") || !Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.inventory.selectedItem().valid();
                }
            }, 100, 5))) {
                return false;
            }
            return interact("Use") && Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return isSelected();
                }
            }, 100, 5);
        }
        return true;
    }

    private Filter<? super MenuCommand> useOnAction(Nameable entity) {
        return Menu.filter("Use", name() + " -> " + entity.name());
    }

    public <T extends InteractableEntity & Nameable> boolean use(T entity, Condition.Check completion) {
        return entity.valid() && entity.walkInViewport() && select()
                && entity.walkingInteraction(useOnAction(entity), completion);
    }

    public boolean use(Item item) {
        return item.valid() && select() && item.interact(useOnAction(item));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            if (obj instanceof org.powerbot.script.rt4.Item) {
                org.powerbot.script.rt4.Item otherItem = (org.powerbot.script.rt4.Item) obj;
                return otherItem.id() == id() && otherItem.stackSize() == stackSize();
            } else if (obj instanceof Item) {
                Item otherItem = (Item) obj;
                return otherItem.id() == id() && otherItem.stackSize() == stackSize();
            }
        }
        return false;
    }

    @Override
    public void bounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
        item.bounds(x1, x2, y1, y2, z1, z2);
    }

    @Override
    public int id() {
        return item.id();
    }

    @Override
    public Point centerPoint() {
        return item.centerPoint();
    }

    @Override
    public String name() {
        return item.name();
    }

    @Override
    public int stackSize() {
        return item.stackSize();
    }

    @Override
    public String[] actions() {
        return item.actions();
    }

    @Override
    public Point nextPoint() {
        return item.nextPoint();
    }

    @Override
    public boolean contains(final Point point) {
        return item.contains(point);
    }

    public Component component() {
        return item.component();
    }

    @Override
    public boolean valid() {
        return item.valid();
    }
}
