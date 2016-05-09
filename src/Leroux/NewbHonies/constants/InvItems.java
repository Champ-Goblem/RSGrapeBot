package Leroux.NewbHonies.constants;

import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Item;

public class InvItems extends ClientAccessor {
	public InvItems(ClientContext ctx) {
		super(ctx);
	}
	
	private final int honey = 12156;
	private final int repel = 28;

	public int getHoney() {
		return honey;
	}
	public int getRepel() {
		return repel;
	}
}
