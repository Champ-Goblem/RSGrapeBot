package xxb.ge;

import java.util.HashMap;

import org.powerbot.script.rt4.GeItem;
import org.powerbot.script.rt4.GroundItem;

/**
 *
 * @author Seth Davis <sethdavis321@gmail.com>
 * @Skype Sethrem
 *
 */
public class SProfitTracker {

    private long startTime;
    private HashMap<Integer, Integer> earnedItems = new HashMap<Integer, Integer>();
    private HashMap<Integer, GeItem> cachedItems = new HashMap<Integer, GeItem>();

    public SProfitTracker() {
        this.startTime = System.currentTimeMillis();
    }

    /**
     *
     * @return Total profit gained per hour
     */
    public int getTotalEarnedPerHour() {
        return (int) ((getTotalEarnedCoins() * 3600000D) / (System.currentTimeMillis() - startTime));
    }

    /**
     *
     * @return Total gained profit
     */
    public int getTotalEarnedCoins() {
        int totalCoints = 0;
        for (Integer itemId : earnedItems.keySet()) {
            totalCoints += earnedItems.get(itemId) * getItemPrice(itemId);
        }
        return totalCoints;
    }

    /**
     * @return formatted string of total earnings
     */
    @Override
    public String toString() {
        return getTotalEarnedCoins() + "(" + getTotalEarnedPerHour() + ")";
    }

    /** Grand Exchange Methods **/

    /**
     *
     * @param itemId - id of item picked up (Call this method when you confirned the script has picked up an item)
     * @param amount - stacksize of the item that has been picked up(Will default to 1 if left blank #Python like)
     */
    public void addEarnedItem(int itemId, int... amount) {
        int itemAmount = (amount == null ? 1 : amount[0]);
        cacheItem(itemId);
        earnedItems.put(itemId, (earnedItems.containsKey(itemId) ? earnedItems.get(itemId) + itemAmount : itemAmount));
    }

    /**
     *
     * @param item - item object of the picked up item (Call this method when you confirned the script has picked up an item)
     */
    public void addEarnedItem(GroundItem item) {
        cacheItem(item.id());
        earnedItems.put(item.id(), (earnedItems.containsKey(item.id()) ? earnedItems.get(item.id()) + item.stackSize() : item.stackSize()));
    }

    /**
     *
     * @param itemId Caches an items GE value so the script doesn't constantly call connections to the rs website
     */
    public void cacheItem(int itemId) {
        if (!cachedItems.containsKey(itemId)) {
            cachedItems.put(itemId, new GeItem(itemId));
        }
    }

    /**
     *
     * @param itemId
     * @return GE value of an item by item id
     */
    public int getItemPrice(int itemId) {
        return getItemData(itemId).price;
    }

    /**
     *
     * @param itemId
     * @return GEItem object of specified item by item id
     */
    public GeItem getItemData(int itemId) {
        cacheItem(itemId);
        return cachedItems.get(itemId);
    }

    /** Getters & Setters **/

    /**
     *
     * @return Time in milliseconds of when this instance was created
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     *
     * @return Earned Items Hashmap
     */
    public HashMap<Integer, Integer> getEarnedItems() {
        return earnedItems;
    }

    /**
     *
     * @return Cached Items Hashmap
     */
    public HashMap<Integer, GeItem> getCachedItems() {
        return cachedItems;
    }

}