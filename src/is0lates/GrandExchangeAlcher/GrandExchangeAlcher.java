package is0lates.GrandExchangeAlcher;

import is0lates.GrandExchangeAlcher.model.AlchItem;
import org.powerbot.script.*;
import org.powerbot.script.GeItem;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Hud;
import org.powerbot.script.rt6.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Script.Manifest(name = "Grand Exchange Alcher",description = "Buys and alchs items at the Grand Exchange for profit and Magic XP.", properties = "topic=1307713; author=Is0lates; ")
public class GrandExchangeAlcher extends PollingScript<ClientContext> implements MessageListener, PaintListener, Stoppable {

    private static boolean started = false;
    private static int NATURE_RUNE_ID = 561;
    private static int natureRunePrice;
    private static String status = "Starting";
    private static ArrayList<AlchItem> alchItemList = new ArrayList<AlchItem>();
    private static AlchItem currentAlchItem;
    private static int currentAlchItemIndex = 0;
    private static boolean isStopping = false;
    private static final long startTime = System.currentTimeMillis();
    private static int startXp = -1;
    private static int totalAlchs;
    private static int totalGp;
    private static int totalGuideGp;
    public static GrandExchangeAlcherForm form;
    public static int buyNatureRunePrice;
    public static int minProfit;
    public static boolean f2pItemsOnly = false;
    public static String sortBy = "Profit";
    public static int waitForOrders = 0;

    private static AlchItem lastAlchItem;

    private static GrandExchange grandExchange;

    @Override
    public boolean isStopping() {
        return isStopping;
    }

    private void fetchAlchItems() {
        status = "Fetching items...";
        final String content = downloadString("http://198.23.59.64/grandExchangeAlcher/");
        String[] rows = content.split("\n");
        for(String row : rows) {
            String[] item = row.split(",");
            AlchItem alchItem = new AlchItem();
            alchItem.id = Integer.parseInt(item[0]);
            alchItem.name = item[1];
            alchItem.price = Integer.parseInt(item[2]);
            alchItem.alchPrice = Integer.parseInt(item[3]);
            alchItem.profit = Integer.parseInt(item[4]);
            alchItem.maxProfit = Integer.parseInt(item[6]);
            alchItem.limit = Integer.parseInt(item[5]);
            alchItem.members = item[7].equals("1");
            if((f2pItemsOnly || !isMember()) && alchItem.members) {
                continue;
            }
            alchItemList.add(alchItem);
        }
        Collections.sort(alchItemList, new Comparator<AlchItem>() {
            @Override public int compare(AlchItem i1, AlchItem i2) {
                Integer x3 = i1.maxProfit;
                Integer x4 = i2.maxProfit;
                if(sortBy.equals("Profit")) {
                    x3 = i1.profit;
                    x4 = i2.profit;
                }
                return x4.compareTo(x3);
            }
        });
        started = true;
    }

    private AlchItem getAlchItemById(int id) {
        for(AlchItem alchItem : alchItemList) {
            if(alchItem.id == id || alchItem.id == (id-1)) {
                return alchItem;
            }
        }
        return null;
    }

    public void start() {
        form = new GrandExchangeAlcherForm(ctx, this);
        form.setLocationRelativeTo(null);
        form.setVisible(true);
        while(!form.valid) {
            Condition.sleep(500);
        }
        grandExchange = new GrandExchange(ctx);
        status = "Fetching items...";
        fetchAlchItems();
        GeItem natureRune = new org.powerbot.script.rt6.GeItem(NATURE_RUNE_ID);
        natureRunePrice = natureRune.price;
    }

    private int countOpenOrders() {
        int openOrders = 0;
        for (int s = 0; s < (isMember() ? 8 : 3); s++) {
            String orderType = ctx.widgets.component(grandExchange.WIDGET, GrandExchange.FIRST_SLOT_COMPONENT + (s)).component(1).text();
            if (orderType.equals("Buy") || orderType.equals("Sell")) {
                openOrders++;
            }
        }
        return openOrders;
    }

    private int countAvailableOrders() {
        return (isMember() ? 8 : 3) - countOpenOrders();
    }

    private boolean abortOrder() {
        return Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                int s = (int) (Math.random() * (isMember() ? 8 : 3));
                return ctx.widgets.component(GrandExchange.WIDGET, GrandExchange.FIRST_SLOT_COMPONENT + s).click();
            }
        },1000,5) &&
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return ctx.widgets.component(GrandExchange.WIDGET, GrandExchange.ABORT_OFFER_COMPONENT).visible() &&
                        ctx.widgets.component(GrandExchange.WIDGET, GrandExchange.ABORT_OFFER_COMPONENT).click();
            }
        },1000,5) &&
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return grandExchange.collectToInventory();
            }
        },1000,5);
    }

    private int countOrdersHaveProgress() {
        int count = 0;
        for(int i = 1; i <= (isMember() ? 8 : 3); i++) {
            if(grandExchange.getProgress(i) > 0) {
                count++;
            }
        }
        return count;
    }

    private boolean isMember(){
        return !ctx.widgets.component(1484, 5).visible();
    }

    private void escape() {
        ctx.input.send("{VK_ESCAPE down}");
        Condition.sleep(50);
        ctx.input.send("{VK_ESCAPE up}");
        Condition.sleep(1000);
    }

    int loop = 0;
    public void poll() {
        if(started) {
            if (loop == 0) {
                if (startXp == -1) {
                    startXp = ctx.skills.experience(6);
                }
            }
            loop++;

            if(!ctx.hud.opened(Hud.Window.BACKPACK)) {
                status = "Opening Backpack.";
                ctx.hud.open(Hud.Window.BACKPACK);
            }

            if (currentAlchItem == null) {
                currentAlchItem = alchItemList.get(currentAlchItemIndex);
            }

            if (ctx.widgets.component(grandExchange.WIDGET, 15).visible()) {
                Condition.wait(new Callable<Boolean>() {
                    public Boolean call() {
                        return ctx.widgets.component(grandExchange.WIDGET, 15).click();
                    }
                }, 1000, 5);
                Condition.sleep(1000);
                return;
            }
            if (ctx.widgets.component(grandExchange.WIDGET, GrandExchange.CONFIRM_COMPONENT).visible() ||
                    ctx.widgets.component(grandExchange.WIDGET, 70).visible() ||
                    ctx.widgets.component(1433, 0).visible() ||
                    ctx.bank.opened()) {
                status = "Stuck on screen. Escaping.";
                escape();
                return;
            }

            if (ctx.backpack.select().id(561).count() == 0) {
                status = "No nature runes, buying nature runes.";
                if (!grandExchange.opened()) {
                    Condition.wait(new Callable<Boolean>() {
                        public Boolean call() {
                            return grandExchange.open();
                        }
                    }, 1000, 5);
                    return;
                } else {
                    if (countAvailableOrders() == 0) {
                        abortOrder();
                        Condition.sleep(1000);
                        return;
                    }
                    if (Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() {
                            int coins = ctx.backpack.moneyPouchCount();
                            int divider = isMember() ? 8 : 3;
                            int amount;
                            if (divider == 0) {
                                amount = 0;
                            } else {
                                amount = Integer.parseInt(Double.toString(Math.floor((coins < 1 ? 1 : coins) / (buyNatureRunePrice < 1 ? 1 : buyNatureRunePrice) / divider)).replace(".0", ""));
                            }
                            return grandExchange.buy(NATURE_RUNE_ID, amount, buyNatureRunePrice);
                        }
                    })) {
                        Condition.sleep(2000);
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() {
                                return grandExchange.collectToInventory();
                            }
                        }, 100, 30);
                        Condition.sleep(1000);
                        return;
                    }
                }
            } else if (ctx.backpack.select().count() == 1) {
                status = "No items to alch, buying items.";
                if (!grandExchange.opened()) {
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() {
                            return grandExchange.open();
                        }
                    }, 100, 30);
                    return;
                } else {
                    if (countAvailableOrders() == 0) {
                        waitForOrders++;
                        if (countOrdersHaveProgress() > 0) {
                            Condition.wait(new Callable<Boolean>() {
                                @Override
                                public Boolean call() {
                                    return grandExchange.collectToInventory();
                                }
                            }, 100, 30);
                        }
                        Condition.sleep(1000);
                        if (waitForOrders > 4) {
                            status = "Aborting order.";
                            if (Condition.wait(new Callable<Boolean>() {
                                @Override
                                public Boolean call() {
                                    return abortOrder();
                                }
                            })) {
                                Condition.sleep(1000);
                                waitForOrders = 0;
                            }
                        }
                        return;
                    } else {
                        status = "Buying item " + currentAlchItem.name + ".";
                        if (Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() {
                                int divider = countAvailableOrders() < 1 ? 1 : countAvailableOrders();
                                int coins = ctx.backpack.moneyPouchCount();
                                int price = ((currentAlchItem.alchPrice - minProfit) - buyNatureRunePrice < 1 ? 1 : (currentAlchItem.alchPrice - minProfit) - buyNatureRunePrice);
                                int canByAmount = (coins < 1 ? 1 : coins) / price / divider;
                                int amount = Integer.parseInt(Double.toString(Math.floor(canByAmount > currentAlchItem.limit ? currentAlchItem.limit : canByAmount)).replace(".0", ""));
                                return grandExchange.buy(currentAlchItem.id, amount, price);
                            }
                        })) {
                            currentAlchItemIndex++;
                            if (currentAlchItemIndex >= alchItemList.size()) {
                                currentAlchItemIndex = 0;
                            }
                            currentAlchItem = alchItemList.get(currentAlchItemIndex);
                            waitForOrders = 0;
                            Condition.sleep(2000);
                            return;
                        }
                    }
                }
            } else {
                waitForOrders = 0;
                if (grandExchange.opened()) {
                    escape();
                }
                status = "Alching.";
                for(Item item : ctx.backpack.items()) {
                    if(item.id() > 0 && item.id() != NATURE_RUNE_ID) {
                        ctx.input.send("1");
                        item.click();
                        lastAlchItem = getAlchItemById(item.id());
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() {
                                return !(ctx.players.local().animation() == 24458 || ctx.players.local().animation() == 17099);
                            }
                        }, 1, 2000);
                        break;
                    }
                }
            }
        }
    }

    public void messaged(MessageEvent messageEvent) {
        if(messageEvent.text().contains("coins have been added to your money pouch.") && messageEvent.source().equals("")) {
            if(status.equals("Alching.")) {
                totalAlchs++;
                int alchGp = Integer.parseInt(messageEvent.text().replace("coins have been added to your money pouch.","").replace(",","").trim());
                int alchPrice = (lastAlchItem.alchPrice - minProfit);
                int alchProfit = alchGp-alchPrice;
                totalGp += alchProfit;
                totalGuideGp += lastAlchItem.profit;
            }
        }
    }

    private String timeFormat(long duration) {
        String res = "";
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
        if (days == 0) {
            res = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            res = String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
        }
        return res;
    }

    Font font = new Font("Arial", Font.PLAIN, 10);
    Color background = new Color(0, 0, 0, 150);
    public void repaint(Graphics graphics) {
        graphics.setFont(font);
        graphics.setColor(background);
        graphics.drawRect(0, 0, 400, 150);
        graphics.fillRect(0, 0, 400, 150);
        graphics.setColor(Color.WHITE);
        graphics.getFont();
        graphics.drawString("Grand Exchange Alcher", 5, 15);
        int runtime = Integer.parseInt("" + (System.currentTimeMillis()- startTime));
        graphics.drawString("Run time: " + timeFormat(runtime), 300, 15);
        graphics.drawString("Status: " + status, 5, 40);
        graphics.drawString("Total Alchs: " + totalAlchs, 5, 53);
        graphics.drawString("Alchs/h: " + (int)((3600000D*totalAlchs) / (System.currentTimeMillis() - startTime)), 5, 68);

        graphics.drawString("Profit: " + totalGp + " ~ " +totalGuideGp, 105, 53);
        graphics.drawString("Profit/h: " + (int)((3600000D*totalGp) / (System.currentTimeMillis() - startTime)) + " ~ " + (int)((3600000D*totalGuideGp) / (System.currentTimeMillis() - startTime)), 105, 68);

        if(started) {
            if (startXp != -1) {
                graphics.drawString("XP gained: " + (ctx.skills.experience(6) - startXp), 266, 53);
                graphics.drawString("XP/h: " + (int) ((3600000D * (ctx.skills.experience(6) - startXp)) / (System.currentTimeMillis() - startTime)), 266, 68);
            }

            if (currentAlchItem != null && !status.equals("Alching.")) {
                graphics.drawString("Next buy item:", 5, 90);
                graphics.drawString("(#" + currentAlchItem.id + ") " + currentAlchItem.name, 70, 90);

                graphics.drawString("Alch price:", 5, 105);
                graphics.drawString("" + currentAlchItem.alchPrice, 65, 105);

                graphics.drawString("Limit:", 5, 120);
                graphics.drawString("" + currentAlchItem.limit, 65, 120);

                graphics.drawString("Buy price:", 125, 105);
                graphics.drawString("" + ((currentAlchItem.alchPrice - minProfit) - buyNatureRunePrice), 190, 105);

                graphics.drawString("Nat price:", 125, 120);
                graphics.drawString("" + buyNatureRunePrice, 190, 120);

                graphics.drawString("Calc Profit:", 125, 135);
                graphics.drawString("" + (currentAlchItem.alchPrice - ((currentAlchItem.alchPrice - minProfit))), 190, 135);

                graphics.drawString("Guide price:", 255, 105);
                graphics.drawString("" + (currentAlchItem.price), 320, 105);

                graphics.drawString("Guide Profit:", 255, 120);
                graphics.drawString("" + currentAlchItem.profit, 320, 120);

                graphics.drawString("Guide Max:", 255, 135);
                graphics.drawString("" + currentAlchItem.maxProfit, 320, 135);

            } else if (lastAlchItem != null && status.equals("Alching.")) {
                graphics.drawString("Alching item:", 5, 90);
                graphics.drawString("(#" + lastAlchItem.id + ") " + lastAlchItem.name, 70, 90);

                graphics.drawString("Alch price:", 5, 105);
                graphics.drawString("" + lastAlchItem.alchPrice, 65, 105);

                graphics.drawString("Limit:", 5, 120);
                graphics.drawString("" + lastAlchItem.limit, 65, 120);

                graphics.drawString("Buy price:", 125, 105);
                graphics.drawString("" + ((lastAlchItem.alchPrice - minProfit) - buyNatureRunePrice), 190, 105);

                graphics.drawString("Nat price:", 125, 120);
                graphics.drawString("" + buyNatureRunePrice, 190, 120);

                graphics.drawString("Calc Profit:", 125, 135);
                graphics.drawString("" + (lastAlchItem.alchPrice - ((lastAlchItem.alchPrice - minProfit))), 190, 135);

                graphics.drawString("Guide price:", 255, 105);
                graphics.drawString("" + (lastAlchItem.price), 320, 105);

                graphics.drawString("Guide Profit:", 255, 120);
                graphics.drawString("" + lastAlchItem.profit, 320, 120);

                graphics.drawString("Guide Max:", 255, 135);
                graphics.drawString("" + lastAlchItem.maxProfit, 320, 135);
            }
        }
    }
}
