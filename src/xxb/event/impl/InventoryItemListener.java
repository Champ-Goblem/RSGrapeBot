package xxb.event.impl;

import java.util.EventListener;

public interface InventoryItemListener extends EventListener {
    void onInventoryItemChanged(InventoryItemEvent evt);
}
