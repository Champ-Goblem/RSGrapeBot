package matulino.MPlanker.Tasks;

import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Item;

import matulino.MPlanker.Planker;
import matulino.MPlanker.Utility.Constants;

public class Stamina extends Task<ClientContext> {
	
	private Planker main;

	private final Component runEnergy = ctx.widgets.widget(160).component(23);
	private final Component plankInterface = ctx.widgets.widget(403).component(3);	
	
	public Stamina(ClientContext ctx, Planker main) {
		super(ctx);
		this.main = main;
	}

	@Override
	public boolean activate() {
		return Integer.parseInt(runEnergy.text()) < 80 
				&&  !isStaminaPotionActive()
				&& (ctx.inventory.select().id(Constants.STAMINA_IDS).count() > 0)
				&& !ctx.bank.opened()
				&& !plankInterface.valid();
				
 	}

	@Override
	public void execute() {
		main.task = "Drinking stamina potion...";
		//Drink a stamina potion
		
		final Item potion = ctx.inventory.id(Constants.STAMINA_IDS).poll();
		potion.interact("Drink");
    	

		
	}
	
	public boolean isStaminaPotionActive() {
	    return ctx.varpbits.varpbit(638) == 0x100000;
	    
	}

}
