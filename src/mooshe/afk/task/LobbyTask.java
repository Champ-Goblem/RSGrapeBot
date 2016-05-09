package mooshe.afk.task;

import java.util.concurrent.Callable;

import mooshe.afk.IdleScript;

import org.powerbot.script.Condition;
import org.powerbot.script.rt6.*;


public class LobbyTask implements Task {

	public static final int
		// Widgets
		WIDGET_GAMEOVER = 985,
			COMPONENT_CLOSE = 86,
			COMPONENT_TITLE = 87,
		WIDGET_QUICKJOIN = 1188,
			COMPONENT_YES = 0,
			COMPONENT_NO  = 10,
		// NPCs
		NPC_LANTHUS = 1526;
	
	private int portal = -1;
	
	@Override
	public boolean execute(final ClientContext ctx) {
		IdleScript script = (IdleScript) ctx.controller.script();
		// Gameover
		final Widget w = ctx.widgets.widget(WIDGET_GAMEOVER);
		if(w.valid()) {
			String text = w.component(COMPONENT_TITLE).text();
			boolean b = Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() {
					return w.component(COMPONENT_CLOSE).click() && !w.valid();
				}
			}, 300, 8);
			if(b)
				script.update(text);
			return true;
		}
		
		// Outside
		if(!ctx.npcs.select().id(NPC_LANTHUS)
				.within(16.0).isEmpty()) {
			if(portal < 0)
				portal = script.getPortal();
			
			final GameObject p = ctx.objects.select().id(portal).peek();
			if(p.valid()) {
				if(!p.inViewport())
					ctx.camera.turnTo(p);
				p.click();
				if(!Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() {
						return !p.valid();
					}
				}, 100, 15))
					portal = IdleScript.PORTAL_GUTHIX;
			}
			return true;
		}
		
		// Quickjoin
		final Widget q = ctx.widgets.widget(WIDGET_QUICKJOIN);
		if(q.valid()) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() {
					return q.component(COMPONENT_NO).click() && !q.valid();
				}
			}, 300, 8);
			return true;
		}
		
		script.timer();
		return ctx.players.local().tile().floor() == 0;
	}
	
}
