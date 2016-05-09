package matulino.MSuperheater.tasks;

import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Bank.Amount;

import matulino.MSuperheater.Superheater;
import matulino.MSuperheater.utility.Constantss;;

public class Withdraw extends Task<ClientContext>{
	Superheater sh = null;

	
	public Withdraw(ClientContext ctx, Superheater sh) {
		super(ctx);
		this.sh = sh;
	}

	@Override
	public boolean activate() {
		return ctx.bank.opened() 
				&& (ctx.inventory.select().count() == 1);
	}
	
	
	@Override
	public void execute() {
		sh.status = "Withdraving stuff...";
		if (!ctx.bank.select().id(sh.chosenOre).isEmpty() || ctx.bank.select().id(Constantss.coalOreId).count() < sh.coalMin) {
			if (sh.coalMin == 0)
				ctx.bank.withdraw(sh.chosenOre, Amount.ALL);
			else {
			ctx.bank.withdraw(Constantss.coalOreId, sh.oreAmount);
			ctx.bank.withdraw(sh.chosenOre, Amount.ALL);
			}
			ctx.bank.close();	
			
		}
		else {
			sh.status = "Not enough ores, stopping script...";
			ctx.controller.stop();
		}
	}

}