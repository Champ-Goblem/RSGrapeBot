package Leroux.FreeWorldCooker.Constants;


import org.powerbot.script.Area;
import org.powerbot.script.Tile;

public class Areas {

    private final Area bankArea = new Area(new Tile(3267,3170,0), new Tile(3273,3164,0));
    private final Area rangeArea = new Area(new Tile(3267,3185,0), new Tile(3273,3180,0));

    public Area getBankArea() {return bankArea;}
    public Area getRangeArea() {return rangeArea;}

}
