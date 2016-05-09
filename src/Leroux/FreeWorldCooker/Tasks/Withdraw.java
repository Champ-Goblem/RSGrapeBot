package Leroux.FreeWorldCooker.Tasks;

import Leroux.FreeWorldCooker.Constants.Areas;
import Leroux.FreeWorldCooker.Constants.Objects;
import Leroux.FreeWorldCooker.Script.FreeWorldCooker;
import Leroux.FreeWorldCooker.Script.Task;
import org.powerbot.script.Condition;
import org.powerbot.script.rt6.*;

import java.util.concurrent.Callable;

public class Withdraw extends Task<ClientContext> {
    public Withdraw(ClientContext ctx) {super(ctx);}

    private Areas area = new Areas();
    private Objects obj = new Objects();
    private Player myPlayer = ctx.players.local();
    private Backpack myBackpack = ctx.backpack;

    final int[] boothBounds = {-128, 128, -256, 0, -128, 128};

    public boolean activate() {
           return area.getBankArea().contains(myPlayer)
                && myBackpack.select().isEmpty()
                && !ctx.objects.select().id(obj.getBankBooth()).each(Interactive.doSetBounds(boothBounds)).isEmpty();
    }

    public void execute() {
        FreeWorldCooker.scriptStatus = "Withdrawing";

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
            if (!ctx.bank.select().contains(cookingItem)) {
                System.out.print("Out of stuff to cook.");
                ctx.controller.stop();
            }

            cookingItem.interact("Withdraw-All");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.bank.isEmpty();
                }
            });
            if (myBackpack.select().count() != 28 && cookingItem != null) {
                return;
            }
        }
    }
}
