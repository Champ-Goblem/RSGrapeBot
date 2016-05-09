package is0lates.GrandExchangeAlcher;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GeItem;

public class GrandExchangeAlcherForm extends JFrame {
    JPanel p = new JPanel();
    JButton startButton = new JButton("Start script");
    JEditorPane description = new JEditorPane();
    JCheckBox f2pItems = new JCheckBox();

    public boolean valid = false;

    private GrandExchangeAlcher grandExchangeAlcher = null;
    private ClientContext ctx = null;

    JComboBox sortBy = new JComboBox(new String[] {"Profit", "Max Profit"});
    JTextField minProfit = new JTextField(1);
    JTextField natureRunePrice = new JTextField(1);

    public GrandExchangeAlcherForm(final ClientContext ctx, GrandExchangeAlcher alcher){
        this.ctx = ctx;
        this.grandExchangeAlcher = alcher;

        setSize(200, 600);
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Grand Exchange Alcher");

        GridBagLayout l = new GridBagLayout();
        p.setLayout(l);
        GridBagConstraints c = new GridBagConstraints();

        description.setText("Caution!\n" +
                "1. This script interacts with the Grand Exchange. Cancel all open Buy or Sell orders.\n" +
                "2. This script alchs all items in you inventory. Bank any items you do not want alched.\n" +
                "\n" +
                "Before you start:\n" +
                "1. Equip a fire staff.\n" +
                "2. Place High Alchemy in the first slot of your action bar.\n" +
                "3. Have atleast 500k F2P or 1M P2P in your money pouch.\n" +
                "4. Stand next to a Grand Exchange clerk.");



        c.fill = GridBagConstraints.BOTH;
        c.ipady = 25;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
        p.add(description, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 10;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 1;
        p.add(new JLabel("Buy Items Sortation:"), c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        p.add(sortBy,c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        p.add(new JLabel("F2P items only:"),c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 2;
        p.add(f2pItems, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        p.add(new JLabel("Min Profit:"),c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 3;
        minProfit.setText("100");
        p.add(minProfit, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 4;
        p.add(new JLabel("Nature rune price:"), c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 4;
        GeItem natureRune = new GeItem(561);
        natureRunePrice.setText((natureRune.price + 5) + "");
        p.add(natureRunePrice,c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 2;
        p.add(startButton, c);

        add(p);

        startButton.addActionListener(new ActionListener() {
//
//            @Override
            public void actionPerformed(ActionEvent e) {
                grandExchangeAlcher.minProfit = Integer.parseInt(minProfit.getText());
                grandExchangeAlcher.buyNatureRunePrice = Integer.parseInt(natureRunePrice.getText());
                grandExchangeAlcher.sortBy = sortBy.getSelectedItem() + "";
                valid = true;
                dispose();
            }
        });


        f2pItems.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    grandExchangeAlcher.f2pItemsOnly = true;
                } else {
                    grandExchangeAlcher.f2pItemsOnly = false;
                };
            }
        });
    }
}