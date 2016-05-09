package matulino.MSuperheater.utility;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.WindowConstants;

import org.powerbot.script.rt4.ClientContext;

import matulino.MSuperheater.Superheater;
import matulino.MSuperheater.Superheater.oreChoices;
import matulino.MSuperheater.tasks.Cast;
import matulino.MSuperheater.tasks.OpenBank;
import matulino.MSuperheater.tasks.Withdraw;

import javax.swing.JPanel;
import javax.swing.JComboBox;

public class GUI extends JFrame{
	JPanel p = new JPanel();
	JButton startButton = new JButton("Start script");
	JButton closeButton = new JButton("Close script");
	
	private Superheater sh = null;
	private ClientContext ctx = null;

	JComboBox<oreChoices> oreToHeat = new JComboBox<oreChoices>(oreChoices.values());
	
	public GUI(final ClientContext ctx, Superheater sh){
		this.ctx = ctx;
		this.sh = sh;
		initiate();
	}
	

	


	private void initiate(){
		setSize(350, 200);
		setResizable(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("SuperheaterGUI");
		p.setLayout(new GridLayout(2,2));
		p.add(new JLabel("Ore:"));
		p.add(oreToHeat);
		p.add(startButton);
		p.add(closeButton);
		add(p);
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				oreChoices toSmith = (oreChoices) oreToHeat.getSelectedItem();
				
				for (oreChoices choice : oreChoices.values()) {
						if(choice.equals(toSmith)){
							sh.chosenOre = choice.getChosenOre();
							sh.oreAmount = choice.getOreAmount();
							sh.coalMin = choice.getCoalMin();
							sh.bar = choice.getBar();
							break;
						}
					}
				sh.taskList.add(new Cast(ctx, sh));
		    	sh.taskList.add(new OpenBank(ctx,sh));
		    	sh.taskList.add(new Withdraw(ctx,sh));
				dispose();
			}
		});
		
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctx.controller.stop();
				dispose();
			}
		});  

	}
}