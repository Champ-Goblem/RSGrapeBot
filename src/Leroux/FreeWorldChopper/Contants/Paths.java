package Leroux.FreeWorldChopper.Contants;

import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;

public class Paths extends ClientAccessor {

    public Paths(ClientContext ctx) {
        super(ctx);
    }

    private static final Tile[] VWB_TO_OAK = {
            new Tile(3178, 3429, 0),
            new Tile(3168, 3418, 0)
    };

    private static final Tile[] OAK_TO_VWB = {
            new Tile(3169, 3429, 0),
            new Tile(3184, 3430, 0),
            new Tile(3184, 3438, 0)
    };

    private static final Tile[] VWB_TO_NORMAL = {
            new Tile(3175, 3428, 0),
            new Tile(3162, 3422, 0),
            new Tile(3149, 3424, 0),
            new Tile(3140, 3430, 0)
    };

    private static final Tile[] NORMAL_TO_VWB = {
            new Tile(3146,3424,0),
            new Tile(3159,3423,0),
            new Tile(3169,3428,0),
            new Tile(3181,3429,0),
            new Tile(3185,3436,0)
    };

    private static final Tile[] VEB_TO_OAK = {
            new Tile(3256, 3427, 0),
            new Tile(3264, 3428, 0),
            new Tile(3275, 3428, 0),
            new Tile(3282, 3422, 0)
    };

    private static final Tile[] OAK_TO_VEB = {
            new Tile(3277, 3429, 0),
            new Tile(3261, 3428, 0),
            new Tile(3253, 3425, 0),
            new Tile(3253, 3420, 0)
    };

    private static final Tile[] DRAYNOR_TO_WILLOW = {
            new Tile(3089, 3233, 0)
    };

    private static final Tile[] WILLOW_TO_DRAYNOR = {
            new Tile(3092, 3244, 0)
    };

    private static final Tile[] EDGE_TO_YEW = {
            new Tile(3093, 3484, 0),
            new Tile(3094, 3475, 0),
            new Tile(3087, 3473, 0)
    };

    private static final Tile[] YEW_TO_EDGE = {
            new Tile(3095, 3477, 0),
            new Tile(3090, 3486, 0),
            new Tile(3094, 3493, 0)
    };

    public static Tile[] getVwbToOak() { return VWB_TO_OAK; }
    public static Tile[] getOakToVwb() { return OAK_TO_VWB; }
    public static Tile[] getVebToOak() { return VEB_TO_OAK; }
    public static Tile[] getOakToVeb() { return OAK_TO_VEB; }
    public static Tile[] getVwbToNormal() { return VWB_TO_NORMAL; }
    public static Tile[] getNormalToVwb() { return NORMAL_TO_VWB; }
    public static Tile[] getDraynorToWillow() { return DRAYNOR_TO_WILLOW; }
    public static Tile[] getWillowToDraynor() { return WILLOW_TO_DRAYNOR; }
    public static Tile[] getEdgeToYew() { return EDGE_TO_YEW; }
    public static Tile[] getYewToEdge() { return YEW_TO_EDGE; }
}
