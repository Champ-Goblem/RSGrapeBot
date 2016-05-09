package Leroux.NewbHonies.tasks;

import Leroux.NewbHonies.constants.Areas;
import Leroux.NewbHonies.constants.InvItems;
import Leroux.NewbHonies.constants.Paths;
import Leroux.NewbHonies.methods.Walker;
import Leroux.NewbHonies.script.NewbHonies;
import Leroux.NewbHonies.script.Task;
import org.powerbot.script.rt6.ClientContext;

public class Move_ToField extends Task {

	public Move_ToField(ClientContext ctx) {
		super(ctx);
	}
	
	private Walker walk = new Walker(ctx);
	private Paths path = new Paths();
	private Areas area = new Areas();

	@Override
	public boolean activate() {
        return !area.getHoneyArea().contains(ctx.players.local())
                && ctx.backpack.select().count() == 1;
	}

	@Override
	public void execute() {
        NewbHonies.scriptStatus = "Walking to Field";
		
		walk.followPath(path.getToField(), -2, 2);
		
	}
}