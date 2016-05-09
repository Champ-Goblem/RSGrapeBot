package Leroux.FreeWorldChopper.WoodCutting.Tasks;

import Leroux.FreeWorldChopper.Script.FreeChopper;
import Leroux.FreeWorldChopper.Script.Task;
import Leroux.FreeWorldChopper.WoodCutting.Wrapper.WoodCutting;
import org.powerbot.script.Condition;
import org.powerbot.script.rt6.*;

import java.util.concurrent.Callable;

public class PowerChop extends Task<ClientContext> {

    public PowerChop(ClientContext ctx) {
        super(ctx);
    }

    private Player myPlayer = ctx.players.local();
    private Backpack myBackpack = ctx.backpack;

    public boolean activate() {
        return myBackpack.select().count() < 28
                && myPlayer.animation() == -1
                && !ctx.objects.select().id(WoodCutting.getTreeIDs()).each(Interactive.doSetBounds(WoodCutting.getTreeBounds())).isEmpty();
    }

    public void execute() {
        FreeChopper.scriptStatus = "Chopping Trees.";
        GameObject tree = ctx.objects.nearest().peek();

        if (tree.inViewport() && !myPlayer.inMotion()) {

            tree.interact("Chop down");

            Condition.wait(new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    return ctx.players.local().animation() != -1;
                }
            });
        } else {
            ctx.movement.step(tree);
            ctx.camera.turnTo(tree);
        }
    }
}