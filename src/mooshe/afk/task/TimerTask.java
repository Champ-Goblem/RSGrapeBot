package mooshe.afk.task;

import org.powerbot.script.*;
import org.powerbot.script.rt6.*;
import org.powerbot.script.rt6.ClientContext;

public class TimerTask implements Task {

	public static final int[] TRAPDOOR = {83826, 83827};
	
	private long timer = 0;
	
	@Override
	public boolean execute(ClientContext ctx) {
		long cur = System.currentTimeMillis();
		if(timer > cur)
			return false;
		timer = cur + 30000 + (long) getRand(60000);
		
		switch(getRand(4)) {
			case 0:
				Locatable b = Math.random() < 0.5 ?
						ctx.players.shuffle().poll() :
						ctx.objects.shuffle().poll();
				ctx.camera.turnTo(b, getRandomPct());
				break;
				
			case 1:
				ctx.camera.angle(getRandomDeg());
				break;

			case 2:
				if(Math.random() < 0.5) {
					ctx.camera.angle(getRandomDeg());
					ctx.camera.pitch(getRandomPct());
				} else {
					ctx.camera.pitch(getRandomPct());
					ctx.camera.angle(getRandomDeg());
				}
				break;
			
			default:
				if(ctx.players.local().tile().floor() == 2) {
					GameObject o = ctx.objects.select().id(TRAPDOOR).poll();
					Tile ta = new Tile(o.tile().x() - 2, o.tile().y() - 2),
						tb  = new Tile(o.tile().x() + 2, o.tile().y() + 2);
					Area a = new Area(ta, tb);
					ctx.movement.step(a.getRandomTile());
				}
				break;
		}
		return false;
	}
	
	public static int getRand(int mod) {

		return (int) (Math.random() * mod);
	}
	
	public static int getRandomDeg() {
		return getRand(360);
	}
	
	public static int getRandomPct() {
		return getRand(100);
	}

}
