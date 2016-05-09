package matulino.MPlanker.Tasks;

import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;

import matulino.MPlanker.Planker;
import matulino.MPlanker.Utility.Constants;

public class WithdrawEnergy extends Task<ClientContext> {
	private Planker main;	
	
	private final Component runEnergy = ctx.widgets.widget(160).component(23);
	
	public WithdrawEnergy(ClientContext ctx, Planker main) {
		super(ctx);
		this.main = main;
	}

	@Override
	public boolean activate() {
		return main.superEnergy	
				&& ctx.bank.opened()
				&& ctx.inventory.select().count() <= 2 
				&&  (Integer.parseInt(runEnergy.text()) <= 10);
	}

	@Override
	public void execute() {
		main.task = "Withdraving energy potions";
		if (!ctx.bank.select().id(Constants.SUPER_ENERGY_ID).isEmpty()) {
			ctx.bank.withdraw(Constants.SUPER_ENERGY_ID, 1);
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() {
					return !ctx.inventory.select().id(Constants.SUPER_ENERGY_ID).isEmpty();
				}
			}, 1000, 2);
		} else {
			//out of super energies, stopping script
			ctx.controller.stop();
			

		}
	}

}