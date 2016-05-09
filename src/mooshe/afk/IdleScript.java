package mooshe.afk;

import java.awt.*;
import java.util.*;

import javax.imageio.*;
import javax.swing.*;

import mooshe.afk.task.*;
import mooshe.afk.task.TimerTask;

import org.powerbot.script.*;
import org.powerbot.script.rt6.ClientContext;


@Script.Manifest(
		name="AFK Wars",
		description="Idles in Castle Wars to get tickets.",
		properties="author=Mooshe; topic=1296226; client=6;")
public class IdleScript extends PollingScript<ClientContext>
		implements PaintListener {

	public static final int
		// Objects
		PORTAL_GUTHIX = 83642,
		PORTAL_ZAMORAK = 83638,
		PORTAL_SARADOMIN = 83639,
		// Display offsets
		OFFSET_X = 24,
		OFFSET_Y = 60;

	public int win = 0, loss = 0, draw = 0,
		gold = 0, silver = 0, loseStreak = 0,
		line = 0;
	public Option option = null;
	private Team team = null, winner = null;
	private Image bg = null;
	private long start = 0, updateOffset = 0;
	
	private Queue<Task> tasks 
			= new LinkedList<Task>();
	private TimerTask timer = new TimerTask();
	private Task task = null;
	
	@Override
	public void start() {
		start = System.currentTimeMillis();
		log.info("Starting AFK Wars");
		bg = downloadImage("http://i.imgur.com/qRolF87.png");
		option = (Option) JOptionPane.showInputDialog(null,
				"Please select a team to join",
				"AFK Wars",
				JOptionPane.QUESTION_MESSAGE,
				null, Option.values(), Option.LOSING);
		log.info("Option: "+option);
		addTask(new LobbyTask());
		addTask(new GameTask());
	}
	
	@Override
	public void stop() {
		log.info("Stopping AFK Wars");
	}
	
	@Override
	public void poll() {
		if(option == null || task == null)
			return;
		if(!task.execute(ctx))
			task = tasks.poll();
	}
	
	public void addTask(Task task) {
		if(tasks.size() < 1)
			this.task = task;
		tasks.add(task);
	}

	@Override
	public void repaint(Graphics g) {
		line = 0;
		g.drawImage(bg, 0, 0, null);

		if(loseStreak > 3) {
			String msg = (System.currentTimeMillis() / 500) % 2 == 0 ?
					"" : "Clan Manipulation Detected!";
			g.setColor(Color.YELLOW);
			drawString(g, msg); 
		}

		drawString(g, "Time Elapsed: "+formatTime());
		drawString(g, String.format("Wins: %s  Losses: %s  Draws: %s",
				win, loss, draw));
		
		double ldr = loss + draw;
		double wlr = ldr == 0 ? win : win / ldr;
		drawString(g, String.format("Win Ratio: %1.2f", wlr));

		drawString(g,
				String.format("Tickets: %s Gold, %s Silver", gold, silver));
		
		g.setColor(Color.WHITE);
		g.drawString("By Mooshe", 170, 40);
	}
	
	private void drawString(Graphics g, String str) {
		int h = g.getFontMetrics().getHeight();
		g.drawString(str, OFFSET_X, OFFSET_Y + (5 * line) + (h * line));
		line++;
	}
	
	private String formatTime() {
		long delta = (System.currentTimeMillis() - start) / 1000,
			h = (delta / (60 * 60)) % 24,
			m = (delta / 60) % 60,
			s = delta % 60;
		return String.format("%02dh %02dm %02ds", h, m, s);
	}
	
	public void update(String text) {
		long cur = System.currentTimeMillis();
		if(updateOffset > cur)
			return;
		updateOffset = cur + 60000;
		
		silver++;
		if(text.contains("Lost")) {
			loseStreak++;
			loss++;
			winner = team == Team.SARADOMIN ?
					Team.ZAMORAK : Team.SARADOMIN;
		}
		else if(text.contains("Draw")) {
			loseStreak = 0;
			draw++;
			gold++;
			winner = null;
		}
		else {
			loseStreak = 0;
			win++;
			gold += 2;
			winner = team;
		}
	}
	
	public int getPortal() {
		switch(option) {
			case WINNING:
				if(winner == null)
					return PORTAL_GUTHIX;
				return winner == Team.SARADOMIN ?
						PORTAL_SARADOMIN : PORTAL_ZAMORAK;
		
			case LOSING:
				if(winner == null)
					return PORTAL_GUTHIX;
				return winner == Team.SARADOMIN ?
						PORTAL_ZAMORAK : PORTAL_SARADOMIN;
		
			case SARADOMIN:
				return PORTAL_SARADOMIN;
			
			case ZAMORAK:
				return PORTAL_ZAMORAK;
		
			case RANDOM:
				return Math.random() < 0.5 ?
						PORTAL_SARADOMIN : PORTAL_ZAMORAK;
			
			default:
				return PORTAL_GUTHIX;
		}
	}
	
	public void timer() {
		timer.execute(ctx);
	}
}
