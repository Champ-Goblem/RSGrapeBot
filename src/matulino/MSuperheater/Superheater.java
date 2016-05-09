package matulino.MSuperheater;


import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.*;
import matulino.MSuperheater.tasks.Task;
import matulino.MSuperheater.utility.Constantss;
import matulino.MSuperheater.utility.GUI;

import org.powerbot.script.PaintListener;

import java.awt.*;
import java.text.NumberFormat;
import java.util.*;




@Script.Manifest(name="MSuperheater", description = "Superheats ore for magic and smithing xp!", properties = "authors = Matulino;  client=4; topic=1306736")
public class Superheater extends PollingScript<ClientContext> implements PaintListener  {
	
   
    public int bar;
    public int chosenOre;
    public int oreAmount ;
    public int coalMin;
    public String status;
    public int numOfCasts = 0;
    
    public GUI gui;
    public ArrayList<Task> taskList = new ArrayList<Task>();
    
    private int magicXpPh, smithXpPh;
    private int gainedMagicXp, gainedSmithXp, startMagicXp, startSmithXp;
   
    


    public void start() {
    	
    	startMagicXp = ctx.skills.experience(6);
    	startSmithXp = ctx.skills.experience(13);
    	status = "Waiting...";
    
    	try {
					gui = new GUI(ctx,this);
					gui.setVisible(true);
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
	private final Color color3 = new Color(51, 51, 255);
	private final BasicStroke stroke1 = new BasicStroke(1);
	private final Font font1 = new Font("Perpetua", 1, 15);
	private final Font font2 = new Font("Consolas", 1, 14);
    
    public void repaint(Graphics gr) {
    	
    	gainedMagicXp = (ctx.skills.experience(6) - startMagicXp);
        gainedSmithXp = (ctx.skills.experience(13) - startSmithXp);
        magicXpPh = getPerHour(gainedMagicXp,this.getRuntime());
        smithXpPh = getPerHour(gainedSmithXp,this.getRuntime());

		Graphics2D g = (Graphics2D) gr;
		g.setColor(color1);
		g.fillRect(0, 220, 240, 120);
		g.setColor(color2);
		g.setStroke(stroke1);
		g.drawRect(0, 220, 240, 120);
		g.drawLine(0, 240, 240, 240);
		g.setFont(font1);
		g.drawString("Time: " + formated(this.getRuntime()), 3, 252);
		g.drawString("Casts: " + NumberFormat.getInstance().format(numOfCasts)+"("+NumberFormat.getInstance().format(
				getPerHour(numOfCasts, getRuntime()))+" pH)",3, 266);
		g.drawString("Magic xp gained: " + gainedMagicXp + "("+(magicXpPh)+"/pH)", 3, 280);
        g.drawString("Smithing xp gained: "+ gainedSmithXp + "("+(smithXpPh)+"/pH)", 3, 294);
        g.drawString("XP till lvl: "+ getExperienceToNext(),3,308);
		g.drawString("Status: "+ status,2,326);
		g.setFont(font2);
		g.setColor(color3);
		g.drawString("SuperHeater, by Matulino", 6, 236);
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
	
	public final int getExperienceToNext() {	
        int level = ctx.skills.level(Constants.SKILLS_MAGIC);
        return Constantss.XP[level] - ctx.skills.experience(Constants.SKILLS_MAGIC);
    }
    
    public enum oreChoices{
    	Iron(27,0,Constantss.ironOreId,Constantss.ironBarId), 
    	Steel(18,2,Constantss.ironOreId,Constantss.steelBarId), 
    	Gold(27,0,Constantss.goldOreId,Constantss.goldBarId),
    	Mithril(20,4,Constantss.mithrilOreId,Constantss.mithrilBarId),
    	Adamant(18,6,Constantss.addyOreId,Constantss.addyBarId),
    	Rune(21,7,Constantss.runeOreId,Constantss.runeBarId);
    	
    	private int oreAmount, coalMin, chosenOre, bar;
    	
    	private oreChoices(int oreAmount, int coalMin, int chosenOre, int bar){
    		this.oreAmount = oreAmount;
    		this.coalMin = coalMin;
    		this.chosenOre = chosenOre;
    		this.bar = bar;
    	}
    	
    	public int getOreAmount() {
			return oreAmount;
		}
    	
    	public int getCoalMin() {
			return coalMin;
		}
    	
    	public int getChosenOre() {
			return chosenOre;
		}
    	
    	public int getBar() {
			return bar;
		}
  
    } 
    
    
    
}