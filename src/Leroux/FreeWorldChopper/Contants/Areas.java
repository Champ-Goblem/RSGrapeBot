package Leroux.FreeWorldChopper.Contants;

import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;

public class Areas extends ClientAccessor {

    public Areas(ClientContext ctx) {
        super(ctx);
    }

    private final Area varrockWestBankArea = new Area(new Tile(3179,3446,0), new Tile(3194,3432,0));
    private final Area varrockEastBankArea = new Area(new Tile(3250,3424,0), new Tile(3257,3418,0));
    private final Area varrockWestNormalArea = new Area(new Tile(3127,3444,0), new Tile(3148,3418,0));
    private final Area varrockEastOakArea = new Area(new Tile(3275,3445,0), new Tile(3286,3411,0));
    private final Area varrockWestOakArea = new Area(new Tile(3160,3426,0), new Tile(3172,3410,0));

    private final Area draynorBankArea = new Area(new Tile(3087,3248,0), new Tile(3098,3240,0));
    private final Area draynorWillowArea = new Area(new Tile(3079,3241,0), new Tile(3092,3225,0));
    private final Area edgeVilleBankArea = new Area(new Tile(3089,3499,0), new Tile(3099,3487,0));
    private final Area edgeVilleYewArea = new Area(new Tile(3084,3483,0), new Tile(3091,3467,0));

    public Area getVWNormalArea() { return varrockWestNormalArea; }
    public Area getVWBArea() { return varrockWestBankArea; }
    public Area getVEBArea() { return varrockEastBankArea; }
    public Area getVEOakArea() { return varrockEastOakArea; }
    public Area getVWOakArea() { return varrockWestOakArea; }

    public Area getDraynorBankArea() { return draynorBankArea; }
    public Area getDraynorWillowArea() { return draynorWillowArea; }
    public Area getEdgeVilleBankArea() { return edgeVilleBankArea; }
    public Area getEdgeVilleYewArea() { return edgeVilleYewArea; }
}
