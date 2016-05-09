package Leroux.FreeWorldChopper.WoodCutting.Tasks;

import Leroux.FreeWorldChopper.Script.FreeChopper;
import Leroux.FreeWorldChopper.Script.Task;
import Leroux.FreeWorldChopper.WoodCutting.Wrapper.WoodCutting;
import org.powerbot.script.Condition;
import org.powerbot.script.rt6.*;

import java.util.concurrent.Callable;

public class Deposit extends Task<ClientContext> {

    public Deposit(ClientContext ctx) {
        super(ctx);
    }

    private Player myPlayer = ctx.players.local();
    private Backpack myBackpack = ctx.backpack;

    public boolean activate() {
        return WoodCutting.getBankArea().contains(myPlayer)
                && myBackpack.select().count() == 28
                && !ctx.objects.select().id(WoodCutting.getBoothIDs()).each(Interactive.doSetBounds(WoodCutting.getBankBounds())).isEmpty();
    }

    public void execute() {
        FreeChopper.scriptStatus = "Banking";
        GameObject booth = ctx.objects.nearest().poll();

        if(!ctx.bank.opened()) {
            if (WoodCutting.getTreeName() != "Willow" && WoodCutting.getTreeName() != "Yew") {
                booth.interact("Bank", "Bank booth");
            } else {
                booth.interact("Bank", "Counter");
            }

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.bank.opened();
                }
            });
        } else {
            ctx.bank.depositInventory();
            ctx.bank.close();
        }
    }
}
