package is0lates.ChocolateDuster;

import org.powerbot.script.*;
import org.powerbot.script.rt6.GeItem;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Hud;

import java.awt.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Script.Manifest(name = "Chocolate Duster",description = "Converts chocolate bars into chocolate dust.", properties = "topic=1309466; author=Is0lates; ")
public class ChocolateDuster extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    private static final long startTime = System.currentTimeMillis();
    private static int chocolateBarId = 1973;
    private static int chocolateDustId = 1975;
    private static int totalDusts = 0;
    private static GeItem chocolateBar = new GeItem(chocolateBarId);
    private static GeItem chocolateDust = new GeItem(chocolateDustId);
    private static String status = "Starting";

    private void escape() {
        ctx.input.send("{VK_ESCAPE down}");
        Condition.sleep(50);
        ctx.input.send("{VK_ESCAPE up}");
        Condition.sleep(1000);
    }

    public void poll() {
        if(!ctx.hud.opened(Hud.Window.BACKPACK)) {
            status = "Opening Backpack";
            ctx.hud.open(Hud.Window.BACKPACK);
        }
        if(ctx.widgets.component(1433, 0).visible()) {
            escape();
        }
        if(ctx.backpack.select().id(chocolateBarId).count() == 0) {
            if(!ctx.bank.opened()) {
                status = "Opening bank";
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        return ctx.bank.open();
                    }
                });
                return;
            } else {
                status = "Depositing inventory";
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        return ctx.bank.depositInventory();
                    }
                });
                if(ctx.bank.select().id(chocolateBarId).count() == 0) {
                    ctx.controller.stop();
                    return;
                }
                status = "Withdrawing chocolate bars";
                ctx.bank.withdraw(chocolateBarId, 28);
                return;
            }
        } else {
            if(ctx.bank.opened()) {
                status = "Closing bank";
                escape();
                return;
            } else {
                status = "Powdering chocolate bar";
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        return ctx.backpack.select().id(chocolateBarId).poll().interact("Powder");
                    }
                });
                status = "Grinding chocolate";
                if (Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        return ctx.widgets.component(1370, 20).visible();
                    }
                }, 10, 300)) {
                    ctx.input.send(" ");
                    Condition.sleep(1000);
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() {
                            return !ctx.widgets.component(1251, 0).visible();
                        }
                    }, 1, 16000);
                }
            }
        }
    }

    public void messaged(MessageEvent messageEvent) {
        if(messageEvent.text().contains("You grind the ingredient with your pestle and mortar.") && messageEvent.source().equals("")) {
            totalDusts++;
        }
    }

    private String timeFormat(long duration) {
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
        if (days == 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
    }

    Font font = new Font("Arial", Font.PLAIN, 10);
    Color background = new Color(0, 0, 0, 150);
    public void repaint(Graphics graphics) {
        graphics.setFont(font);
        graphics.setColor(background);
        graphics.drawRect(0, 0, 200, 100);
        graphics.fillRect(0, 0, 200, 100);
        graphics.setColor(Color.WHITE);
        graphics.getFont();
        graphics.drawString("Chocolate Duster", 5, 15);
        int runtime = Integer.parseInt("" + (System.currentTimeMillis()- startTime));
        graphics.drawString("Run time: " + timeFormat(runtime), 105, 15);
        graphics.drawString("Status: " + status, 5, 40);
        graphics.drawString("Total grinds: " + totalDusts, 5, 53);
        graphics.drawString("Grinds/h: " + (int)((3600000D*totalDusts) / (System.currentTimeMillis() - startTime)), 5, 68);
        graphics.drawString("Profit/dust: " + (chocolateDust.price - chocolateBar.price), 105, 53);
        graphics.drawString("Total profit: " + (chocolateDust.price - chocolateBar.price)*totalDusts, 105, 68);
        graphics.drawString("Profit/h: " + (int)((3600000D*((chocolateDust.price - chocolateBar.price)*totalDusts)) / (System.currentTimeMillis() - startTime)), 105, 83);
    }
}
