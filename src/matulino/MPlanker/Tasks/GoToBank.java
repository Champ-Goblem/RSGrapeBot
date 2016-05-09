package matulino.MPlanker.Tasks;


import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.TilePath;

import matulino.MPlanker.Planker;
	
public class GoToBank extends Task<ClientContext>{

	Planker main;
	
	public GoToBank(ClientContext ctx, Planker main) {
		super(ctx);
		this.main = main;
		
	}

	@Override
	public boolean activate() {
		//GameObject booth = ctx.objects.select().id(7409).nearest().poll();

		return ctx.inventory.select().id(main.plank.getLogId()).isEmpty()
				&& !ctx.npcs.select().id(2898).nearest().poll().valid();
		
	}

	@Override
	public void execute() {
		main.task = "Walking to bank...";
		TilePath path = main.pathToBank;
		path.traverse();
		
	}

}
