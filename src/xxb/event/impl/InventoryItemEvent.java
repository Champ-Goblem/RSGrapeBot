package xxb.event.impl;

import java.util.EventObject;

public class InventoryItemEvent extends EventObject {

    private final int id;
    private final int diff;
    private final int slot;

    public InventoryItemEvent(int slot, int id, int diff) {
        super(id);
        this.id = id;
        this.diff = diff;
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    public int getDiff() {
        return diff;
    }

    public int getID() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("invevent[:id %d :diff %d :slot %d]", getID(), getDiff(), getSlot());
    }

}
