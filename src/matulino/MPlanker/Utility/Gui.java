package matulino.MPlanker.Utility;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.powerbot.script.rt4.ClientAccessor;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GeItem;

import matulino.MPlanker.Planker;
import matulino.MPlanker.Tasks.BuyPlanks;
import matulino.MPlanker.Tasks.CloseBank;
import matulino.MPlanker.Tasks.Deposit;
import matulino.MPlanker.Tasks.Energy;
import matulino.MPlanker.Tasks.GoToBank;
import matulino.MPlanker.Tasks.GoToSawmill;
import matulino.MPlanker.Tasks.OpenBank;
import matulino.MPlanker.Tasks.OpenInterface;
import matulino.MPlanker.Tasks.Stamina;
import matulino.MPlanker.Tasks.SuperEnergy;
import matulino.MPlanker.Tasks.Task;
import matulino.MPlanker.Tasks.Withdraw;
import matulino.MPlanker.Tasks.WithdrawEnergy;

public class Gui extends ClientAccessor {
	private JFrame frame;
	private JPanel contentPane = new JPanel();
	private JComboBox<Plank> cbPlankType;
	private JLabel lblPlankType;
	private JButton btnStart, btnCancel;
	private JCheckBox staminaPot;
	private JCheckBox superEnergy;
	private ArrayList<Task> taskList;
	
	Planker main;
	
	
		
		public Gui(ClientContext ctx, Planker main, ArrayList<Task> taskList) {
			super(ctx);
			this.main = main;
			this.taskList = taskList;
			initialize();
		}
		
		
		private void initialize(){
		
			frame = new JFrame("MPlanker");
			frame.setResizable(false);
			//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setBounds(100, 100, 450, 300);
			contentPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
			frame.setContentPane(contentPane);
			contentPane.setLayout(null);
			
			cbPlankType = new JComboBox<Plank>(Plank.values());
			cbPlankType.setToolTipText("Select plank type you wish to make.");
			cbPlankType.setBounds(127, 42, 82, 20);
			staminaPot = new JCheckBox("Use Stamina potion");
			staminaPot.setBounds(130, 100, 150, 40);
			superEnergy = new JCheckBox("Use Super energy potion");
			superEnergy.setBounds(130, 150, 200, 40);
			lblPlankType = new JLabel("Plank type:");
			lblPlankType.setBounds(65, 45, 60, 14);
			btnCancel = new JButton("Cancel");
			btnCancel.setBounds(212, 199, 89, 23);
			btnStart = new JButton("Start");
			btnStart.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					Plank plank = (Plank) cbPlankType.getSelectedItem();
					main.plank = plank;
					int logPrice = new GeItem(plank.getLogId()).price;
		    		int plankPrice = new GeItem(plank.getPlankId()).price;
		    		main.profitPerPlank = plankPrice - logPrice - plank.getFee();
		    		
					taskList.add(new BuyPlanks(ctx, main));
					taskList.add(new OpenBank(ctx, main));
					taskList.add(new Withdraw(ctx, main));
					taskList.add(new Deposit(ctx, main));
					taskList.add(new GoToBank(ctx, main));
					taskList.add(new GoToSawmill(ctx, main));
					taskList.add(new OpenInterface(ctx, main));
					taskList.add(new Energy(ctx, main));
					if(staminaPot.isSelected()) {
						main.staminaPot = true;
						main.plankMode = 26;
						taskList.add(new Stamina(ctx, main));
					}
					
					if(superEnergy.isSelected()) {
						main.superEnergy = true;
						taskList.add(new SuperEnergy(ctx, main));
						taskList.add(new WithdrawEnergy(ctx,main));
						taskList.add(new CloseBank(ctx, main));
					}
					
					frame.dispose();
				}
			});
			btnStart.setBounds(83, 199, 89, 23);
			
			contentPane.add(superEnergy);
			contentPane.add(staminaPot);
			contentPane.add(cbPlankType);			
			contentPane.add(btnStart);
			contentPane.add(lblPlankType);
			contentPane.add(btnCancel);
			frame.setVisible(true);
		}
}
