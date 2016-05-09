package Leroux.FreeWorldChopper.Methods;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;

import java.util.concurrent.Callable;

public class Walker extends ClientAccessor {
	
	public Walker(ClientContext ctx) {
		super(ctx);
	}

	public void followPath(Tile[] path, int min, int max, Tile tile) {			
		for(int i = 0; i < path.length; i++) {			
			if(ctx.movement.energyLevel() > 50 && !ctx.movement.running()) {
				ctx.movement.running(true);				
			}
			
			final Tile curTile = path[i];
			
			if(curTile != null) {				
				if(i == path.length) {
					ctx.movement.step(tile);
				} else {
					ctx.movement.step(curTile.derive(Random.nextInt(min, max), Random.nextInt(min, max)));
				}
						
				Condition.wait(new Callable<Boolean>() {
					public Boolean call() throws Exception {
						return ctx.movement.distance(ctx.players.local().tile(), curTile) <= Random.nextInt(2, 4);
					}
				}, 100, 10);
			} else {
				break;
			}			
		}			
	}
	
	public void followPath(Tile[] path, int min, int max) {
		for(int i = 0; i < path.length; i++) {
			if(ctx.movement.energyLevel() > 50 && !ctx.movement.running()) {
				ctx.movement.running(true);					
			}
				
			final Tile curTile = path[i];
				
			if(curTile != null) {					
				ctx.movement.step(curTile.derive(Random.nextInt(min, max), Random.nextInt(min, max)));							
				Condition.wait(new Callable<Boolean>() {
					public Boolean call() throws Exception {
						return ctx.movement.distance(ctx.players.local().tile(), curTile) <= Random.nextInt(2, 4);
					}
				}, 100, 10);
			} else {
				break;
			}				
		}			
	}
	
	public void followPath (Tile[] path) {
		for(int i = 0; i < path.length; i++) {
			if(ctx.movement.energyLevel() > 50 && !ctx.movement.running()) {
				ctx.movement.running(true);				
			}
			
			final Tile curTile = path[i];
			
			if(curTile != null) {				
				ctx.movement.step(curTile);		
				Condition.wait(new Callable<Boolean>() {
					public Boolean call() throws Exception {
						return ctx.movement.distance(ctx.players.local().tile(), curTile) <= Random.nextInt(2, 4);
					}
				}, 100, 10);
			} else {
				break;
			}			
		}			
	}	
}