package matulino.MPlanker.Tasks;

import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import matulino.MPlanker.Planker;
import matulino.MPlanker.Utility.Constants;

public class CloseBank extends Task<ClientContext> {

	private Planker main;
	
	public CloseBank(ClientContext ctx, Planker main) {
		super(ctx);
		this.main = main;
	}

	@Override
	public boolean activate() {
		return ctx.bank.opened()
				&& !ctx.inventory.select().id(Constants.SUPER_ENERGY_IDS).isEmpty();
	}

	@Override
	public void execute() {
		main.task = "Closing bank...";
		ctx.bank.close();
		Condition.wait(new Callable<Boolean>() {
		     @Override
		     public Boolean call() {
		         return !ctx.bank.opened();
		     }
		}, 500, 2);
		
	}

}