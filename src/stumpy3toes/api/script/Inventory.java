package stumpy3toes.api.script;

import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.ItemQuery;
import stumpy3toes.api.script.wrappers.Item;

import java.util.ArrayList;
import java.util.List;

public class Inventory extends ItemQuery<Item> {
    public final ClientContext ctx;

    public Inventory(ClientContext ctx) {
        super(ctx);
        this.ctx = ctx;
    }

    private Item wrap(org.powerbot.script.rt4.Item item) {
        return new Item(ctx, item);
    }

    @Override
    protected List<Item> get() {
        final List<Item> items = new ArrayList<Item>();
        for (org.powerbot.script.rt4.Item item : ctx.pbInventory.items()) {
            if (item != null && item.valid()) {
                items.add(wrap(item));
            }
        }
        return items;
    }

    public Item[] items() {
        return get().toArray(new Item[28]);
    }

    public Item itemAt(final int index) {
        return wrap(ctx.pbInventory.itemAt(index));
    }

    public int selectionType() {
        return ctx.pbInventory.selectionType();
    }

    public int selectedItemIndex() {
        return ctx.pbInventory.selectedItemIndex();
    }

    public Item selectedItem() {
        return wrap(ctx.pbInventory.selectedItem());
    }

    public Component component() {
        return ctx.pbInventory.component();
    }

    public Item item(int id, boolean shuffle) {
        select().id(id);
        if (shuffle) {
            shuffle();
        }
        return peek();
    }

    public Item item(int id) {
        return item(id, false);
    }

    @Override
    public Item nil() {
        return wrap(ctx.pbInventory.nil());
    }
}