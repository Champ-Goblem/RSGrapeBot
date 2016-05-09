package Leroux.NewbHonies.constants;

import org.powerbot.script.Area;
import org.powerbot.script.Tile;

public class Areas {
	
	private final Area bankArea  = new Area(new Tile(2717, 3498, 0), new Tile(2731, 3484, 0));
	private final Area honeyArea = new Area(new Tile(2751, 3452, 0), new Tile(2767, 3436, 0));

	private final Tile bankTile = new Tile(2725, 3492, 0);
	private final Tile honeyTile = new Tile(2758, 3444, 0);
	
	public Area getBankArea() {
		return bankArea;
	}
	
	public Area getHoneyArea() {
		return honeyArea;
	}
	
	public Tile getBankTile() {
		return bankTile;
	}
	
	public Tile getHoneyTile() {
		return honeyTile;
	}
}
