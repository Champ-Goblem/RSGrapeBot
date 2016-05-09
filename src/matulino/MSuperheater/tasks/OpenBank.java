package matulino.MSuperheater.tasks;

import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;
import matulino.MSuperheater.Superheater;
import matulino.MSuperheater.utility.Constantss;

import org.powerbot.script.rt4.GameObject;;

public class OpenBank extends Task<ClientContext>{
	Superheater sh = null;

	
	public OpenBank(ClientContext ctx, Superheater sh) {
		super(ctx);
		this.sh = sh;
	}

	@Override	
	public boolean activate() {

			return  !ctx.bank.opened()
					&& (ctx.game.tab() == Game.Tab.INVENTORY)
					&& (ctx.inventory.select().id(sh.chosenOre).isEmpty() || ctx.inventory.select().id(Constantss.coalOreId).count() < sh.coalMin);
	}
	

	@Override
	public void execute() {
	
	sh.status = "Opening bank...";
	
       //final GameObject booth = ctx.objects.select().id(Constantss.bankBooth).nearest().poll();
      //  booth.interact("Bank");
		ctx.bank.open();
        Condition.wait(new Callable<Boolean>() {
		    @Override
		    public Boolean call() {
		        return ctx.bank.opened();
		    }
		}, 800, 2);
        	
        ctx.bank.depositAllExcept(Constantss.natureRune);
        	
	}	
}