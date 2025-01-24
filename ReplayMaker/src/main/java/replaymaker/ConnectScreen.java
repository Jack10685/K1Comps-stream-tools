package replaymaker;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ConnectScreen {
	public static JTextField ipenter;
	public static JPasswordField pwenter;
	public static JFrame frame;
	
	public static void run() {
		frame = new JFrame("K1 Virtual Drift Comps Replay Maker");
		frame.setSize(500,200);
		frame.setLayout(new GridBagLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.ipadx = 10;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		
		JLabel iplabel = new JLabel("Streamer ZeroTier IP:");
		frame.add(iplabel, c);
		
		c.gridx = 1;
		c.ipadx = 150;
		ipenter = new JTextField();
		ipenter.addActionListener(new Connect());
		frame.add(ipenter, c);
		
		c.gridx = 0;
		
		c.gridy = 1;
		c.ipadx = 10;
		JLabel pwlabel = new JLabel("Streamer OBS WebSocket Password:");
		
		frame.add(pwlabel, c);
		
		c.gridx = 1;
		c.ipadx = 150;
		pwenter = new JPasswordField();
		pwenter.addActionListener(new Connect());
		frame.add(pwenter, c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		
		JButton submit = new JButton("Connect");
		submit.addActionListener(new Connect());
		
		frame.add(submit, c);
		frame.setVisible(true);
	}
}
