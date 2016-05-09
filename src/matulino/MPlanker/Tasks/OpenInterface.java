package matulino.MPlanker.Tasks;

import java.util.concurrent.Callable;

import javax.swing.JOptionPane;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt4.Npc;

import matulino.MPlanker.Planker;

public class OpenInterface extends Task<ClientContext> {

	Planker main;
	
	public OpenInterface(ClientContext ctx, Planker main) {
		super(ctx);
		this.main = main;
	}
	
	@Override
	public boolean activate() {
		Npc sawmillGuy = ctx.npcs.select().id(5422).nearest().poll();
		Component plankInterface = ctx.widgets.widget(403).component(3);
		return sawmillGuy.valid()
				&& !ctx.inventory.select().id(main.plank.getLogId()).isEmpty() 
				&& !plankInterface.valid();
	}

	@Override
	public void execute() {
		main.task = "Opening sawmill interface...";
		
		Npc sawmillGuy = ctx.npcs.select().id(5422).nearest().poll();
		final Component plankInterface = ctx.widgets.widget(403).component(main.plank.getChild());
		
		Item coins = ctx.inventory.select().id(995).peek();
		if(!coins.valid()) {
			ctx.controller.stop();
			JOptionPane.showMessageDialog(null, "Out of Coins!");
		}
		
		if (sawmillGuy.inViewport()) {
			sawmillGuy.interact("Buy-plank");
			Condition.wait(new Callable<Boolean>() {
			     @Override
			     public Boolean call() {
			         return plankInterface.valid();
			     }
			}, 500, 2);
		} else {
			ctx.camera.turnTo(sawmillGuy);
			ctx.movement.step(sawmillGuy);
		}
	

	}
}


