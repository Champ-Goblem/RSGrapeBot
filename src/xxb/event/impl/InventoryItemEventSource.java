package xxb.event.impl;

import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;
import xxb.event.EventSource;

import java.util.EventListener;

public class InventoryItemEventSource implements EventSource<InventoryItemEvent> {

    private final int[] itemIDCache;
    private final int[] itemCountCache;

    public InventoryItemEventSource(ClientContext ctx) {
        this.itemIDCache = new int[28];
        this.itemCountCache = new int[28];

        for (int i = 0; i < 28; i++) {
            Item item = ctx.inventory.itemAt(i);
            if(item != null && item.valid()) {
                itemIDCache[i] = item.id();
                itemCountCache[i] = item.stackSize();
            } else {
                itemIDCache[i] = -1;
                itemCountCache[i] = 0;
            }
        }
    }

    @Override
    public void process(ClientContext ctx) {
        for (int i = 0; i < 28; i++) {
            Item item = ctx.inventory.itemAt(i);
            if(item == null || !item.valid()) {
                itemCountCache[i] = 0;
                itemIDCache[i] = -1;
            } else {
                int countBefore = itemCountCache[i];
                int countAfter = item.stackSize();
                if (countBefore != countAfter) {
                    int id = item.id();
                    dispatch(ctx, new InventoryItemEvent(i, id, countAfter - countBefore));
                    itemCountCache[i] = countAfter;
                    itemIDCache[i] = id;
                }
            }
        }
    }

    @Override
    public void dispatch(ClientContext ctx, InventoryItemEvent o) {
        for (EventListener l : ctx.dispatcher) {
            if (l instanceof InventoryItemListener)
                ((InventoryItemListener) l).onInventoryItemChanged(o);
        }
    }

}
