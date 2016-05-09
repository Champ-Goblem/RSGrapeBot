package Leroux.NewbHonies.script;

import Leroux.NewbHonies.constants.InvItems;
import Leroux.NewbHonies.tasks.Deposit;
import Leroux.NewbHonies.tasks.GrabHonies;
import Leroux.NewbHonies.tasks.Move_ToBank;
import Leroux.NewbHonies.tasks.Move_ToField;
import org.powerbot.script.*;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GeItem;
import org.powerbot.script.rt6.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Script.Manifest(name = "Newb Honies", description = "Collects HoneyComb in Seers Village.", properties = "client=6;topic=1265445;author=Leroux;")

public class NewbHonies extends PollingScript<ClientContext> implements PaintListener, MessageListener {

	private List<Task> taskList = new ArrayList<Task>();
	private InvItems items = new InvItems(ctx);

	private final Color mouseColor = new Color(82, 181, 209);
	private final Color boxColor = new Color(82, 181, 209, 125);

	private final Color textColor = new Color(0,0,0);
	private final Font font = new Font("Arial",Font.BOLD, 15);

	private long initialTime;
	private int profit, honeyPrice, honiesGrabbed, hours, minutes, seconds;
	private double runTime, honiesPerHour, profitPerHour;

	public  static String scriptStatus;

	@Override
	public void start() {
		Item repellent = ctx.backpack.select().id(items.getRepel()).poll();

		if(!ctx.backpack.select().contains(repellent)){
			System.out.print(" Insect repellent is needed! ");
			ctx.controller.stop();
		} else {
			taskList.addAll(Arrays.asList(new Deposit(ctx), new Move_ToField(ctx), new GrabHonies(ctx), new Move_ToBank(ctx)));
			System.out.print(" Tasks Added ");
		}

		honeyPrice = GeItem.price(items.getHoney());
		initialTime = System.currentTimeMillis();
		System.out.print(" " + honeyPrice);

	}

	 @Override
	    public void poll() {
	    	for (Task task : taskList) {
	    		if(task.activate()) {
	            	task.execute();
	            	System.out.print(" " + task + " ");
	    		}
	    	}
	    }

	@Override
	public void messaged(MessageEvent m) {
		if(m.text().startsWith("You take")) {
    		honiesGrabbed++;
    		profit += honeyPrice;
    	}
	}

	@Override
	public void repaint(Graphics g1) {

    	Graphics2D g = (Graphics2D)g1;
    	int x = (int) ctx.input.getLocation().getX();
    	int y = (int) ctx.input.getLocation().getY();

    	g.setColor(mouseColor);
    	g.drawLine(x, y - 10, x, y + 10);
    	g.drawLine(x - 10, y, x + 10, y);

    	hours = (int) ((System.currentTimeMillis() - initialTime) / 3600000);
		minutes = (int) ((System.currentTimeMillis() - initialTime) / 60000 % 60);
		seconds = (int) ((System.currentTimeMillis() - initialTime) / 1000) % 60;
		runTime = (double) (System.currentTimeMillis() - initialTime) / 3600000;
		honiesPerHour = honiesGrabbed / runTime;
		profitPerHour = profit / runTime;

		g.setColor(boxColor);
		g.fillRoundRect(8, 346, 504, 126, 5, 5);
		g.setColor(textColor);
		g.setFont(font);

		g.drawString("Newb Honies v1.02", 162, 364);
		g.drawString("Grabbed: " + honiesGrabbed + "(" + (int)honiesPerHour + "/hour)", 22, 385);
		g.drawString("Profit: " + profit + "(" + (int)profitPerHour + "/hour)", 22, 400);
		g.drawString("Time Running: " + hours +":" + minutes + ":" + seconds, 22, 415);
		g.drawString("Script Status: " + scriptStatus, 22, 430);
	}

}
