package matulino.MPlanker.Tasks;

import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;

import matulino.MPlanker.Planker;
import matulino.MPlanker.Utility.Constants;

public class Deposit extends Task<ClientContext> {
	private Planker main;
	
	public Deposit(ClientContext ctx, Planker main) {
		super(ctx);
		this.main = main;
	}

	@Override
	public boolean activate() {
		return ctx.bank.opened()
				&& (!ctx.inventory.select().id(main.plank.getPlankId()).isEmpty()
				|| !ctx.inventory.select().id(Constants.VIAL_ID).isEmpty());
	}

	@Override
	public void execute() {
		main.task = "Depositing stuff...";
		ctx.bank.depositAllExcept(995,Constants.STAMINA_IDS[0],Constants.STAMINA_IDS[1],Constants.STAMINA_IDS[2],Constants.STAMINA_IDS[3]);
		/*if(!ctx.inventory.select().id(Constants.VIAL_ID).isEmpty()) 
			ctx.bank.deposit(Constants.VIAL_ID, Bank.Amount.ALL);
		else if(!ctx.inventory.select().id(main.plank.getPlankId()).isEmpty())
			ctx.bank.deposit(main.plank.getPlankId(), 0);	*/
		Condition.wait(new Callable<Boolean>() {
		     @Override
		     public Boolean call() {
		         return ctx.inventory.select().id(main.plank.getPlankId()).isEmpty();
		     }
		}, 350, 2);

	}

}
