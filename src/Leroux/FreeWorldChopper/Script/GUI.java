package Leroux.FreeWorldChopper.Script;

import Leroux.FreeWorldChopper.Contants.Areas;
import Leroux.FreeWorldChopper.Contants.Paths;
import Leroux.FreeWorldChopper.Enums.Bankers;
import Leroux.FreeWorldChopper.Enums.Trees;
import Leroux.FreeWorldChopper.WoodCutting.Tasks.*;
import Leroux.FreeWorldChopper.WoodCutting.Wrapper.WoodCutting;
import org.powerbot.script.rt6.ClientContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;

public class GUI extends JFrame {

    private static final long serialVersionUID = -7756889463610098394L;

    private ClientContext ctx;

    private JPanel panel;
    public boolean guiDone = false, bank;
    private Areas area = new Areas(ctx);

    public GUI(ClientContext ctx) {

        this.ctx = ctx;
        setTitle("Leroux");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 350);
        panel = new JPanel();
        setContentPane(panel);
        panel.setLayout(null);

        final JLabel title = new JLabel("Welcome to Free Chopper");
        title.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        title.setBounds(153, 11, 154, 15);
        panel.add(title);

        final JLabel toTrain = new JLabel("What Tree?");
        toTrain.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        toTrain.setBounds(187, 37, 76, 14);
        panel.add(toTrain);

        final JComboBox<String> treeBox = new JComboBox<String>();
        treeBox.setModel(new DefaultComboBoxModel<String>(new String[]{"Trees", "Normal", "Oak", "Willow"}));
        treeBox.setBounds(175, 56, 100, 20);
        panel.add(treeBox);

        final JButton start = new JButton("Start");
        start.setEnabled(false);
        start.setBounds(180, 277, 90, 23);
        panel.add(start);

        final JLabel optionLabel1 = new JLabel("Options");
        optionLabel1.setVisible(false);
        optionLabel1.setBounds(187, 85, 100, 14);
        panel.add(optionLabel1);

        final JComboBox<String> optionBox1 = new JComboBox<String>();
        optionBox1.setVisible(false);
        optionBox1.setModel(new DefaultComboBoxModel<String>(new String[]{"Example", "Options"}));
        optionBox1.setBounds(175, 105, 89, 20);
        panel.add(optionBox1);

        final JLabel optionLabel3 = new JLabel("Options");
        optionLabel3.setVisible(false);
        optionLabel3.setBounds(275, 85, 100, 14);
        panel.add(optionLabel3);

        final JComboBox<String> optionBox3 = new JComboBox<String>();
        optionBox3.setVisible(false);
        optionBox3.setModel(new DefaultComboBoxModel<String>(new String[]{"Example", "Options"}));
        optionBox3.setBounds(275, 105, 89, 20);
        panel.add(optionBox3);

        final JCheckBox chkBox1 = new JCheckBox("Optional");
        chkBox1.setVisible(false);
        chkBox1.setBounds(25, 149, 97, 23);
        panel.add(chkBox1);

        final JCheckBox chkBox2 = new JCheckBox("Optional");
        chkBox2.setVisible(false);
        chkBox2.setBounds(150, 149, 97, 23);
        panel.add(chkBox2);

        final JCheckBox chkBox3 = new JCheckBox("Optional");
        chkBox3.setVisible(false);
        chkBox3.setBounds(275, 149, 97, 23);
        panel.add(chkBox3);

        final JCheckBox chkBox4 = new JCheckBox("Optional");
        chkBox4.setVisible(false);
        chkBox4.setBounds(25, 175, 97, 23);
        panel.add(chkBox4);

        final JCheckBox chkBox5 = new JCheckBox("Optional");
        chkBox5.setVisible(false);
        chkBox5.setBounds(150, 175, 97, 23);
        panel.add(chkBox5);

        final JCheckBox chkBox6 = new JCheckBox("Optional");
        chkBox6.setVisible(false);
        chkBox6.setBounds(275, 175, 97, 23);
        panel.add(chkBox6);

        final JTextArea greetingMessage = new JTextArea();
        greetingMessage.setEditable(false);
        greetingMessage.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        greetingMessage.setLineWrap(true);
        greetingMessage.setWrapStyleWord(true);
        greetingMessage.setRows(10);
        greetingMessage.setText("Chop stuff! \r\n\r\nFor Free!");
        greetingMessage.setBounds(75, 100, 300, 100);
        panel.add(greetingMessage);

        final JTextArea infoMessage = new JTextArea();
        infoMessage.setEditable(false);
        infoMessage.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        infoMessage.setLineWrap(true);
        infoMessage.setWrapStyleWord(true);
        infoMessage.setRows(10);
        infoMessage.setText("");
        infoMessage.setBounds(75, 150, 300, 100);
        panel.add(infoMessage);

        treeBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if (treeBox.getSelectedItem() == "Trees") {
                    greetingMessage.setVisible(true);
                    infoMessage.setVisible(false);

                    optionLabel1.setVisible(false);
                    optionLabel3.setVisible(false);
                    optionBox1.setVisible(false);
                    optionBox3.setVisible(false);

                    chkBox1.setVisible(false);
                    chkBox2.setVisible(false);
                    chkBox3.setVisible(false);
                    chkBox4.setVisible(false);
                    chkBox5.setVisible(false);
                    chkBox6.setVisible(false);
                    start.setEnabled(false);
                } else {
                    greetingMessage.setVisible(false);
                    infoMessage.setVisible(true);

                    optionLabel1.setVisible(true);
                    optionLabel1.setText("Method");
                    optionBox1.setVisible(true);
                    optionBox1.setModel(new DefaultComboBoxModel<String>(new String[]{"Bank", "Drop"}));


                    chkBox1.setVisible(false);
                    chkBox2.setVisible(false);
                    chkBox3.setVisible(false);
                    chkBox4.setVisible(false);
                    chkBox5.setVisible(false);
                    chkBox6.setVisible(false);
                    start.setEnabled(true);
                }
                if (treeBox.getSelectedItem() == "Normal") {
                    if (optionBox1.getSelectedItem() == "Bank") {
                        infoMessage.setText("Chopping west of Varrock, Banking at Varrock West Bank.");
                    } else {
                        infoMessage.setText("Start Near Some Normal Trees.");
                    }
                } else if (treeBox.getSelectedItem() == "Oak") {
                    if (optionBox1.getSelectedItem() == "Bank") {
                        optionLabel3.setVisible(true);
                        optionLabel3.setText("Location");
                        optionBox3.setVisible(true);
                        optionBox3.setModel(new DefaultComboBoxModel<String>(new String[]{"Varrock East", "Varrock West"}));
                        infoMessage.setText("Cutting Oak Trees Outside Varrock, Banking at Varrock West Bank.");
                    } else {
                        infoMessage.setText("Start Near Oak Trees");
                        optionBox3.setVisible(false);
                    }
                } else {
                    if (optionBox1.getSelectedItem() == "Bank") {
                        infoMessage.setText("Cutting Willow Trees Draynor Village, Banking at Draynor Bank.");
                    } else {
                        infoMessage.setText("Start Near Willow Trees.");
                    }
                }

            }

        });

        optionBox1.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if (optionBox1.getSelectedItem() == "Bank") {
                    if (treeBox.getSelectedItem() == "Normal") {
                        infoMessage.setText("Chopping west of Varrock, Banking at Varrock West Bank.");
                    } else if (treeBox.getSelectedItem() == "Oak") {
                        infoMessage.setText("Cutting Oak Trees Outside Varrock, Banking at Varrock West Bank.");
                        optionLabel3.setVisible(true);
                        optionBox3.setVisible(true);
                    } else if (treeBox.getSelectedItem() == "Willow") {
                        infoMessage.setText("Cutting Willow Trees Draynor Village, Banking at Draynor Bank.");
                    }
                } else {
                    if (treeBox.getSelectedItem() == "Normal") {
                        infoMessage.setText("Start Near Some Normal Trees.");
                    } else if (treeBox.getSelectedItem() == "Oak") {
                        infoMessage.setText("Start Near Oak Trees.");
                        optionLabel3.setVisible(false);
                        optionBox3.setVisible(false);
                    } else if (treeBox.getSelectedItem() == "Willow") {
                        infoMessage.setText("Start Near Willow Trees.");
                    }
                }
            }
        });

        start.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent a) {
                                        if (treeBox.getSelectedItem() == "Normal") {
                                            WoodCutting.setTreeIDs(Trees.NORMAL.getTreeIDs());
                                            WoodCutting.setLogInvID(Trees.NORMAL.getLogInvID());
                                            WoodCutting.setChopArea(area.getVWNormalArea());
                                            WoodCutting.setBankArea(area.getVWBArea());
                                            WoodCutting.setPathToSpot(Paths.getVwbToNormal());
                                            WoodCutting.setPathToBank(Paths.getNormalToVwb());
                                            WoodCutting.setTreeBounds(Trees.NORMAL.getTreeBounds());
                                            WoodCutting.setMinLevel(Trees.NORMAL.getMinLV());
                                            WoodCutting.setTreeName("Normal");
                                        } else if (treeBox.getSelectedItem() == "Oak") {
                                            if (optionBox3.getSelectedItem() == "Varrock West") {
                                                WoodCutting.setChopArea(area.getVWOakArea());
                                                WoodCutting.setBankArea(area.getVWBArea());
                                                WoodCutting.setPathToSpot(Paths.getVwbToOak());
                                                WoodCutting.setPathToBank(Paths.getOakToVwb());
                                            } else if (optionBox3.getSelectedItem() == "Varrock East") {
                                                WoodCutting.setChopArea(area.getVEOakArea());
                                                WoodCutting.setBankArea(area.getVEBArea());
                                                WoodCutting.setPathToSpot(Paths.getVebToOak());
                                                WoodCutting.setPathToBank(Paths.getOakToVeb());
                                            }
                                            WoodCutting.setTreeIDs(Trees.OAK.getTreeIDs());
                                            WoodCutting.setLogInvID(Trees.OAK.getLogInvID());
                                            WoodCutting.setTreeBounds(Trees.OAK.getTreeBounds());
                                            WoodCutting.setMinLevel(Trees.OAK.getMinLV());
                                            WoodCutting.setTreeName("Oak");
                                        } else if (treeBox.getSelectedItem() == "Willow") {
                                            WoodCutting.setChopArea(area.getDraynorWillowArea());
                                            WoodCutting.setBankArea(area.getDraynorBankArea());
                                            WoodCutting.setPathToSpot(Paths.getDraynorToWillow());
                                            WoodCutting.setPathToBank(Paths.getWillowToDraynor());
                                            WoodCutting.setTreeIDs(Trees.WILLOW.getTreeIDs());
                                            WoodCutting.setLogInvID(Trees.WILLOW.getLogInvID());
                                            WoodCutting.setTreeBounds(Trees.WILLOW.getTreeBounds());
                                            WoodCutting.setMinLevel(Trees.WILLOW.getMinLV());
                                            WoodCutting.setTreeName("Willow");
                                        }

                                        if (optionBox1.getSelectedItem() == "Bank") {
                                            bank = true;
                                        } else {
                                            bank = false;
                                        }

                                        addTasks(bank);

                                        setVisible(false);
                                        guiDone = true;
                                    }}
        );
    }

    public void addTasks(boolean bank) {
        if (bank) {
            WoodCutting.setBoothIDs(Bankers.BOOTHS.getBoothIDs());
            FreeChopper.taskList.addAll(Arrays.asList(new Chop(ctx), new Deposit(ctx), new WalkToBank(ctx), new WalkToSpot(ctx)));
        } else {
            FreeChopper.taskList.addAll(Arrays.asList(new Drop(ctx), new PowerChop(ctx)));
        }
    }

}

