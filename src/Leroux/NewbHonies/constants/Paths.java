package Leroux.NewbHonies.constants;

import org.powerbot.script.Tile;

public class Paths {
	
	private final Tile[] toField = new Tile[] {
			new Tile(2726, 3478, 0),
			new Tile(2727, 3462, 0),
			new Tile(2734, 3448, 0),
			new Tile(2746, 3444, 0),
			new Tile(2761, 3444, 0)
	};
	
	private final Tile[] toBank = new Tile[] {
			new Tile(2747, 3441, 0),
			new Tile(2734, 3451, 0),
			new Tile(2725, 3465, 0),
			new Tile(2725, 3481, 0),
			new Tile(2725, 3492, 0)
	};

	public Tile[] getToField() {
		return toField;
	}

	public Tile[] getToBank() {
		return toBank;
	}
	
}
