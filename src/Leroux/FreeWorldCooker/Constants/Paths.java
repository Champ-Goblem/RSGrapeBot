package Leroux.FreeWorldCooker.Constants;

import org.powerbot.script.Tile;

public class Paths {

    private final Tile[] toRange = new Tile[] {
            new Tile(3273,3178,0),
            new Tile(3270,3183,0)
    };

    private final Tile[] toBank = new Tile[] {
            new Tile(3275,3177,0),
            new Tile(3270,3168,0)
    };

    public Tile[] getToRange() {return toRange;}
    public Tile[] getToBank() {return toBank;}
}
