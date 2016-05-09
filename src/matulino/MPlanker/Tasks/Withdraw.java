package matulino.MPlanker.Tasks;

import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Bank;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;

import matulino.MPlanker.Planker;
import matulino.MPlanker.Utility.Constants;

public class Withdraw extends Task<ClientContext> {
	private Planker main;
	
	private final Component runEnergy = ctx.widgets.widget(160).component(23);
	
	public Withdraw(ClientContext ctx, Planker main) {
		super(ctx);
		this.main = main;
	}

	@Override
	public boolean activate() {
		return ctx.bank.opened() 
				&&  (Integer.parseInt(runEnergy.text()) > 10 || !main.superEnergy)
				&& ctx.inventory.select().count() <= 2;
				
	}

	@Override
	public void execute() {
		main.task = "Withdraving stuff...";
		if (!ctx.bank.select().id(main.plank.getLogId()).isEmpty()) {
			if (!main.staminaPot) {
				ctx.bank.withdraw(main.plank.getLogId(), Bank.Amount.ALL);
			}
			else if(!ctx.bank.select().id(Constants.STAMINA_ID).isEmpty() && (ctx.inventory.select().id(Constants.STAMINA_IDS).count() < 1)){
				ctx.bank.withdraw(Constants.STAMINA_ID, 1);
				ctx.bank.withdraw(main.plank.getLogId(), Bank.Amount.ALL);
			}
			else {
				ctx.bank.withdraw(main.plank.getLogId(), Bank.Amount.ALL);
			}
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() {
					return !ctx.inventory.select().id(main.plank.getLogId()).isEmpty();
				}
			}, 1000, 2);
		} else {
			ctx.controller.stop();
			

		}
	}

}