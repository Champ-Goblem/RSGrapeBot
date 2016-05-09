package mooshe.afk.task;

import java.util.concurrent.Callable;

import mooshe.afk.IdleScript;

import org.powerbot.script.Condition;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;


public class GameTask implements Task {
	
	public static final int[] LADDER = {83511, 83622};
	
	@Override
	public boolean execute(final ClientContext ctx) {
		IdleScript script = (IdleScript) ctx.controller.script();
		switch(ctx.players.local().tile().floor()) {
			case 1:
				final GameObject o = ctx.objects.select()
						.id(LADDER).nearest().poll();
				if(o.valid()) {
					if(!o.inViewport())
						ctx.camera.turnTo(o);
					o.interact("Climb up");
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() {
							return ctx.players.local().tile().floor() == 2;
						}
					}, 300, 6);
				}
				return true;
			case 2:
				script.timer();
				return true;
			
			default:
				script.addTask(new LobbyTask());
				script.addTask(new GameTask());
				return false;
		}
	}

	
}
