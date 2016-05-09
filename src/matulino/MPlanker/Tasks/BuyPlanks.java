package matulino.MPlanker.Tasks;


import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import matulino.MPlanker.Planker;

public class BuyPlanks extends Task<ClientContext>{

	Planker main;
	
	public BuyPlanks(ClientContext ctx, Planker main) {
		super(ctx);
		this.main = main;
	}

	@Override
	public boolean activate() {
		
		Component plankInterface = ctx.widgets.widget(403).component(3);
		return plankInterface.valid();
	}

	@Override
	public void execute() {
		main.task = "Buying planks...";
		final Component plankInterface = ctx.widgets.widget(403).component(main.plank.getChild());
		plankInterface.interact("Buy All");
		main.plankCount += main.plankMode;
		Condition.wait(new Callable<Boolean>() {
		     @Override
		     public Boolean call() {
		         return !plankInterface.valid();
		     }
		}, 450, 2);

	}

}
