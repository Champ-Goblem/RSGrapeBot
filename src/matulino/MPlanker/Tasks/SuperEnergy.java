package matulino.MPlanker.Tasks;

import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;
import matulino.MPlanker.Planker;
import matulino.MPlanker.Utility.Constants;
public class SuperEnergy extends Task<ClientContext> {
	
	private Planker main;
	
	public SuperEnergy(ClientContext ctx, Planker main) {
		super(ctx);
		this.main = main;
	}

	@Override
	public boolean activate() {
		
		return 	(ctx.inventory.select().id(Constants.SUPER_ENERGY_IDS).count() > 0)
				&& !ctx.bank.opened();
			
				
 	}

	@Override
	public void execute() {
		main.task = "Drinking super energies...";
		
		
		final Item potion = ctx.inventory.id(Constants.SUPER_ENERGY_IDS).poll();
		potion.interact("Drink");
    	

		
	}
}
