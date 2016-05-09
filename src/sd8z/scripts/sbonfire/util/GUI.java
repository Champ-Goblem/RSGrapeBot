package sd8z.scripts.sbonfire.util;

import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Constants;
import sd8z.scripts.sbonfire.SBonfire;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {

    private JPanel contentPane;
    private JTextField txtX;
    private JTextField txtY;
    private JTextField txtZ;
    private JCheckBox chckbxPickAshes;

    public GUI(final SBonfire script, final ClientContext ctx) {
        Tile loc = ctx.players.local().tile();
        setTitle("sBonfire");
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 223, 184);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("sBonfire");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setBounds(10, 11, 197, 14);
        contentPane.add(lblNewLabel);

        final JComboBox<Log> cmbLog = new JComboBox<Log>(Log.values());
        cmbLog.setBounds(76, 36, 131, 20);
        contentPane.add(cmbLog);

        JButton btnStart = new JButton("Start");
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int x = Integer.valueOf(txtX.getText());
                int y = Integer.valueOf(txtY.getText());
                int z = Integer.valueOf(txtZ.getText());
                if (ctx.game.clientState() == Constants.GAME_MAP_LOADED) {
                    script.submit((Log) cmbLog.getSelectedItem(), new Tile(x, y, z), chckbxPickAshes.isSelected());
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Please start logged in.", "", JOptionPane.WARNING_MESSAGE);
                    ctx.controller.stop();
                    dispose();
                }
            }
        });
        btnStart.setBounds(96, 121, 111, 23);
        contentPane.add(btnStart);

        JLabel lblLogType = new JLabel("Log type:");
        lblLogType.setBounds(20, 39, 46, 14);
        contentPane.add(lblLogType);

        txtX = new JTextField();
        txtX.setHorizontalAlignment(SwingConstants.CENTER);
        txtX.setText("0");
        txtX.setBounds(30, 90, 46, 20);
        contentPane.add(txtX);
        txtX.setColumns(10);
        txtX.setText(String.valueOf(loc.x()));

        txtY = new JTextField();
        txtY.setText("0");
        txtY.setHorizontalAlignment(SwingConstants.CENTER);
        txtY.setColumns(10);
        txtY.setBounds(86, 90, 46, 20);
        contentPane.add(txtY);
        txtY.setText(String.valueOf(loc.y()));

        txtZ = new JTextField();
        txtZ.setText("0");
        txtZ.setHorizontalAlignment(SwingConstants.CENTER);
        txtZ.setColumns(10);
        txtZ.setBounds(142, 90, 46, 20);
        contentPane.add(txtZ);
        txtZ.setText(String.valueOf(loc.floor()));

        JLabel lblNewLabel_1 = new JLabel("Tile:");
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_1.setBounds(10, 67, 197, 14);
        contentPane.add(lblNewLabel_1);

        chckbxPickAshes = new JCheckBox("Pick ashes");
        chckbxPickAshes.setBounds(10, 121, 80, 23);
        contentPane.add(chckbxPickAshes);
    }
}
