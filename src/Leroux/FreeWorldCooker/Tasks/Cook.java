package Leroux.FreeWorldCooker.Tasks;

import Leroux.FreeWorldCooker.Constants.Areas;
import Leroux.FreeWorldCooker.Constants.Objects;
import Leroux.FreeWorldCooker.Script.FreeWorldCooker;
import Leroux.FreeWorldCooker.Script.Task;
import org.powerbot.script.Condition;
import org.powerbot.script.rt6.*;

import java.util.concurrent.Callable;

public class Cook extends Task<ClientContext> {
    public Cook(ClientContext ctx) {super(ctx);}
    private Areas area = new Areas();
    private Objects obj = new Objects();
    private Player myPlayer = ctx.players.local();
    private Backpack myBackpack = ctx.backpack;

    private final Component cookingWindow = ctx.widgets.widget(1371).component(0);
    private final Component cookingProgress = ctx.widgets.widget(1251).component(0);
    private final Component cookButton = ctx.widgets.widget(1371).component(5);

    private final int[] rangeBounds = {-128, 128, -256, 0, -128, 128};

    public boolean activate() {
        Item cookingItem = myBackpack.select().id(FreeWorldCooker.cookingIDs).poll();

        return area.getRangeArea().contains(myPlayer)
                && myBackpack.select().contains(cookingItem)
                && !ctx.objects.select().id(obj.getRange()).each(Interactive.doSetBounds(rangeBounds)).isEmpty();
    }

    public void execute() {
        FreeWorldCooker.scriptStatus = "Cooking";

        GameObject range = ctx.objects.nearest().poll();

        if (!cookingWindow.visible() && !cookingProgress.visible()) {
            range.interact("Cook at", "Range");

            Condition.wait(new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    return cookingWindow.visible();
                }
            });
        } else if (cookingWindow.visible() && !cookingProgress.visible()) {
            //cookButton.interact("Make");
            cookButton.click();
        } else {
            Condition.wait(new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    return !cookingProgress.visible();
                }
            });
        }




    }
}
