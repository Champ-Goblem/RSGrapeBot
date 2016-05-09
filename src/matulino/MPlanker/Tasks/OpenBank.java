package matulino.MPlanker.Tasks;

import org.powerbot.script.rt4.Npc;

import matulino.MPlanker.Planker;
import matulino.MPlanker.Utility.Constants;

import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;

public class OpenBank extends Task<ClientContext> {
	private Planker main;
	//final int[] boothBounds = {-32, 32, -100, 0, -32, 32};
	//bank booth, id = 7409, tile(3254,3419,0)
	
	public OpenBank(ClientContext ctx, Planker main) {
		super(ctx);
		this.main = main;
	}

	@Override
	public boolean activate() {
		//GameObject booth = ctx.objects.select().id(7409).nearest().poll()
	
		return !ctx.bank.opened()
				&& ctx.inventory.select().id(Constants.SUPER_ENERGY_IDS).isEmpty()
				&& ctx.inventory.select().id(main.plank.getLogId()).isEmpty()
				&& ctx.npcs.select().id(2898).nearest().poll().valid();
		
	}

	@Override
	public void execute() {

		main.task = "Openning bank...";
		//GameObject booth = ctx.objects.select().id(7409).nearest().poll();
		//booth.bounds(boothBounds);

		Npc banker = ctx.npcs.select().id(2898).nearest().poll();

		if (banker.inViewport()) {
			banker.interact("Bank");
			Condition.wait(new Callable<Boolean>() {
			     @Override
			     public Boolean call() {
			         return ctx.bank.opened();
			     }
			}, 3000, 2);
		} else {
			ctx.movement.step(banker);
			ctx.camera.turnTo(banker);			
		}

	
	}

}
