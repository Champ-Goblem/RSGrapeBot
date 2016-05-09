package matulino.MPlanker.Tasks;

import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import matulino.MPlanker.Planker;
public class Energy extends Task<ClientContext> {
	
	Planker main;	

	 
	private final Component runEnergy = ctx.widgets.widget(160).component(23);
	private final Component run = ctx.widgets.widget(160).component(24);
	private Component plankInterface = ctx.widgets.widget(403).component(3);
	
	public Energy(ClientContext ctx, Planker main) {
		super(ctx);
		this.main = main;
	}

	@Override
	public boolean activate() {
		return !isRunning() 
				&& (Integer.parseInt(runEnergy.text()) > 10)
				&& !ctx.bank.opened()
				&& !plankInterface.valid();
	}

	@Override
	public void execute() {
		main.task = "Turning run on...";
		run.click();
		
	}
	
	public boolean isRunning() {
	    return ctx.varpbits.varpbit(173) == 0x1;
	    
	}

}
