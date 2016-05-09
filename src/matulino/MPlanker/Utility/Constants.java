package matulino.MPlanker.Utility;

import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientAccessor;
import org.powerbot.script.rt4.ClientContext;

public class Constants extends ClientAccessor {
	
	
	public Constants(ClientContext ctx) {
		super(ctx);
		
	}

	public static final int STAMINA_ID = 12625;
	public static final int VIAL_ID = 229;
	public static final int[] STAMINA_IDS = {12625, 12629, 12631, 12627};
	public static final int[] SUPER_ENERGY_IDS = {3016, 3018, 3020, 3022};	
	public static final int SUPER_ENERGY_ID = 3016;
		
	
	public static final Tile[] PATH_TO_SAWMILL = new Tile[] { new Tile(3253, 3422, 0), 
			new Tile(3254, 3424, 0), new Tile(3257, 3428, 0), 
			new Tile(3261, 3428, 0), new Tile(3265, 3428, 0), 
			new Tile(3269, 3428, 0), new Tile(3273, 3430, 0), 
			new Tile(3275, 3434, 0), new Tile(3275, 3438, 0), 
			new Tile(3279, 3440, 0), new Tile(3280, 3444, 0), 
			new Tile(3281, 3448, 0), new Tile(3282, 3452, 0), 
			new Tile(3285, 3455, 0), new Tile(3286, 3459, 0), 
			new Tile(3289, 3462, 0), new Tile(3292, 3465, 0), 
			new Tile(3293, 3469, 0), new Tile(3295, 3473, 0), 
			new Tile(3298, 3476, 0), new Tile(3299, 3480, 0), 
			new Tile(3299, 3484, 0), new Tile(3301, 3488, 0)};	
	
	public static final Tile[] SAWMILL_TO_BANK = {new Tile(3302, 3490, 0),
			new Tile(3302, 3486, 0), new Tile(3302, 3482, 0), 
			new Tile(3300, 3478, 0), new Tile(3297, 3475, 0),
			new Tile(3297, 3471, 0), new Tile(3294, 3467, 0),
			new Tile(3292, 3463, 0), new Tile(3291, 3459, 0),
			new Tile(3288, 3456, 0), new Tile(3286, 3452, 0),
			new Tile(3286, 3448, 0), new Tile(3283, 3445, 0),
			new Tile(3280, 3442, 0), new Tile(3277, 3439, 0),
			new Tile(3275, 3435, 0), new Tile(3274, 3431, 0), 
			new Tile(3270, 3430, 0), new Tile(3266, 3430, 0), 
			new Tile(3262, 3430, 0), new Tile(3258, 3430, 0), 
			new Tile(3255, 3427, 0), new Tile(3253, 3423, 0)};

}
