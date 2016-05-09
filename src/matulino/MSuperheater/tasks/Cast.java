package matulino.MSuperheater.tasks;

import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Magic;

import matulino.MSuperheater.Superheater;
import matulino.MSuperheater.utility.Constantss;




public class Cast extends Task<ClientContext> {
	
		Superheater sh = null;
		Boolean free = true;;

    public Cast(final ClientContext ctx, Superheater sh) {
        super(ctx);
        this.sh = sh;
        
    }

    @Override
    public boolean activate() {
    
    	return 	!ctx.bank.opened()
    			&& ctx.game.tab() == Game.Tab.INVENTORY 
    			&& ctx.inventory.select().id(sh.chosenOre).count() > 0
    			&& (ctx.inventory.select().id(Constantss.coalOreId).count() >= sh.coalMin);
    			
   	 
    	
    }
    

    @Override
    public void execute() {
    	sh.status = "Casting superheat...";
    	if (ctx.inventory.select().id(Constantss.natureRune).count() < 1){   		
    		ctx.controller.stop();
    	}
        ctx.inventory.select().id(sh.chosenOre);
    	  while(ctx.inventory.size() > 0 ) {
    	
		        if (ctx.game.tab() == Game.Tab.MAGIC){
		        	ctx.magic.cast(Magic.Spell.SUPERHEAT_ITEM);
		        }
		        else{
		        	ctx.game.tab(Game.Tab.MAGIC);
		        	ctx.magic.cast(Magic.Spell.SUPERHEAT_ITEM);
		        }
		        ctx.input.click(Random.nextInt(690, 715), Random.nextInt(434, 454), 1);
    		  	ctx.inventory.select().id(sh.chosenOre).poll();

		        sh.numOfCasts++;
		        Condition.wait(new Callable<Boolean>() {
				    @Override
				    public Boolean call() {
				        return ctx.game.tab() == Game.Tab.MAGIC;
				    }
				}, 450, 4);
		        if ( ctx.controller.isStopping()) break;
	    	
	    }
	      ctx.game.tab(Game.Tab.INVENTORY);
    }
}