package Leroux.FreeWorldCooker.Tasks;

import Leroux.FreeWorldCooker.Constants.Areas;
import Leroux.FreeWorldCooker.Constants.Objects;
import Leroux.FreeWorldCooker.Script.FreeWorldCooker;
import Leroux.FreeWorldCooker.Script.Task;
import org.powerbot.script.Condition;
import org.powerbot.script.rt6.*;

import java.util.concurrent.Callable;

public class Deposit extends Task<ClientContext> {
    public Deposit(ClientContext ctx) {super(ctx);}

    private Areas area = new Areas();
    private Objects obj = new Objects();
    private Player myPlayer = ctx.players.local();
    private Backpack myBackpack = ctx.backpack;

    final int[] boothBounds = {-128, 128, -256, 0, -128, 128};

    public boolean activate() {
        Item cookingItem = myBackpack.select().id(FreeWorldCooker.cookingIDs).poll();

        return area.getBankArea().contains(myPlayer)
                && !myBackpack.select().isEmpty()
                && !myBackpack.select().contains(cookingItem)
                && !ctx.objects.select().id(obj.getBankBooth()).each(Interactive.doSetBounds(boothBounds)).isEmpty();
    }

    public void execute() {
        FreeWorldCooker.scriptStatus = "Depositing";

        GameObject booth = ctx.objects.nearest().poll();
        Item cookingItem = ctx.bank.select().id(FreeWorldCooker.cookingIDs).poll();

        if (!ctx.bank.opened()) {
            booth.interact("Bank", "Bank booth");

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.bank.opened();
                }
            });
        } else {
            ctx.bank.depositInventory();
        }
    }
}
