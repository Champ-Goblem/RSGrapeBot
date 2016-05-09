package Leroux.FreeWorldChopper.WoodCutting.Tasks;

import Leroux.FreeWorldChopper.Script.FreeChopper;
import Leroux.FreeWorldChopper.Script.Task;
import Leroux.FreeWorldChopper.WoodCutting.Wrapper.WoodCutting;
import org.powerbot.script.rt6.Backpack;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Item;
import org.powerbot.script.rt6.Player;

public class Drop extends Task<ClientContext> {

    public Drop(ClientContext ctx) {
        super(ctx);
    }

    private Backpack myBackpack = ctx.backpack;
    private Player myPlayer = ctx.players.local();

    public boolean activate() {
        return myPlayer.animation() == -1
                && myBackpack.select().count() == 28;
    }

    public void execute() {
        FreeChopper.scriptStatus = "Dropping Logs.";

        for (Item i : myBackpack.select().id(WoodCutting.getLogInvID())) {
            i.interact("Drop");
        }
    }
}
