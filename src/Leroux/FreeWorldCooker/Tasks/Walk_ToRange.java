package Leroux.FreeWorldCooker.Tasks;

import Leroux.FreeWorldChopper.Methods.Walker;
import Leroux.FreeWorldCooker.Constants.Areas;
import Leroux.FreeWorldCooker.Constants.Paths;
import Leroux.FreeWorldCooker.Script.FreeWorldCooker;
import Leroux.FreeWorldCooker.Script.Task;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.Backpack;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Item;
import org.powerbot.script.rt6.Player;

import java.util.concurrent.Callable;

public class Walk_ToRange extends Task<ClientContext> {
    public Walk_ToRange(ClientContext ctx) {super(ctx);}

    private Areas area = new Areas();
    private Paths path = new Paths();
    private Walker walk = new Walker(ctx);
    private Player myPlayer = ctx.players.local();
    private Backpack myBackpack = ctx.backpack;

    public boolean activate() {
        Item cookingItem = myBackpack.select().id(FreeWorldCooker.cookingIDs).poll();

        return !area.getRangeArea().contains(myPlayer)
                && myBackpack.select().contains(cookingItem);
    }

    public void execute() {
        FreeWorldCooker.scriptStatus = "Walking to range";

        walk.followPath(path.getToRange(), -1, 1);

        Condition.wait(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return area.getRangeArea().contains(myPlayer);
            }
        });
    }
}
