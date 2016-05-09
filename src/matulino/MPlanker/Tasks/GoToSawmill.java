package matulino.MPlanker.Tasks;

import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.TilePath;

import matulino.MPlanker.Planker;

public class GoToSawmill extends Task<ClientContext>{ 

	Planker main;
	
	public GoToSawmill(ClientContext ctx, Planker main) {
		super(ctx);
		this.main = main;
	}

	@Override
	public boolean activate() {
		return !ctx.npcs.select().id(5422).nearest().poll().valid() 
				&& !ctx.inventory.select().id(main.plank.getLogId()).isEmpty() ;
		
	}

	@Override
	public void execute() {
		main.task = "Walking to sawmill...";
		TilePath path = main.pathToSawMill;
		path.traverse();
	}

}
