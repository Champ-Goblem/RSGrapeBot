package matulino.MPlanker;



import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.*;

import matulino.MPlanker.Tasks.Task;
import matulino.MPlanker.Utility.Constants;
import matulino.MPlanker.Utility.Gui;
import matulino.MPlanker.Utility.Plank;

import org.powerbot.script.PaintListener;
import java.awt.*;
import java.text.NumberFormat;
import java.util.*;




@Script.Manifest(name="MPlanker", description = "Makes any plank at Varrock sawmill.", properties = "authors=Matulino; topic=1306732; client=4;")
public class Planker extends PollingScript<ClientContext> implements PaintListener  {
	
   
    
	private ArrayList<Task> taskList = new ArrayList<Task>();
	
	public TilePath pathToSawMill, pathToBank;
	public Plank plank = null;
	public int logPrice;
	public int plankPrice;
	public int profitPerPlank = 0;
	public int plankCount = 0;
	public int plankMode = 27;
	public boolean staminaPot = false;
	public boolean superEnergy = false;
	public String task;		
	

	
    public void start() {
    	task = "Waiting...";
    	pathToSawMill = ctx.movement.newTilePath(Constants.PATH_TO_SAWMILL);
    	pathToBank = ctx.movement.newTilePath(Constants.SAWMILL_TO_BANK);

    	try {
			new Gui(ctx,Planker.this, taskList);
			
    	} catch (Exception e) {
			e.printStackTrace();
    	}    		
    		
    }
	

    public void poll() {
    	
            for (Task task : taskList) {
                if (task.activate()) {
                    task.execute();
                    
                }
            }
       

    }
    
    
    private final Color color1 = new Color(0, 0, 0, 167);
	private final Color color2 = new Color(255, 255, 255);
	private final Color color3 = new Color(204, 0, 204);
	private final BasicStroke stroke1 = new BasicStroke(1);
	private final Font font1 = new Font("Perpetua", 1, 15);
	private final Font font2 = new Font("Consolas", 1, 14);
	
	@Override
	public void repaint(Graphics gr) {
		Graphics2D g = (Graphics2D) gr;
		g.setColor(color1);
		g.fillRect(0, 220, 220, 120);
		g.setColor(color2);
		g.setStroke(stroke1);
		g.drawRect(0, 220, 220, 120);
		g.drawLine(0, 240, 220, 240);
		g.setFont(font1);
		g.drawString("Time: " + formated(this.getRuntime()), 1, 252);
		g.drawString("Planks: " + NumberFormat.getInstance().format(plankCount)+"("+NumberFormat.getInstance().format(
				getPerHour(plankCount, getRuntime()))+" pH)",1, 264);
		g.drawString("Profit per hour: " + getPerHour(plankCount, getRuntime())*profitPerPlank, 1, 276);
		g.drawString("Status:"+ task,1,290);
		g.setFont(font2);
		g.setColor(color3);
		g.drawString("MPlanker, by Matulino", 4, 236);
	}
	
	String formated(long time) {
		final int sec = (int) (time / 1000),
				  h = sec / 3600,
				  m = sec / 60 % 60,
				  s = sec % 60;
		return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":"
				+ (s < 10 ? "0" + s : s);
	}
	
	public static int getPerHour(int in, long time) {
		return (int) ((in) * 3600000D / time);
	}
    
    
    
}