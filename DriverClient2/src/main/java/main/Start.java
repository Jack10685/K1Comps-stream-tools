package main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Start {
	static JTextField ipenter, passenter;
	static JLabel error;
	static Socket sock;
	static BufferedReader in;
	static BufferedWriter out;
	static JFrame frame, codeScreen;
	
	public static void main(String[] args) {
		frame = new JFrame("Bracket Manager");
		frame.setSize(300,300);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		error = new JLabel();
		frame.add(error, BorderLayout.NORTH);
		
		JPanel login = new JPanel();
		login.setLayout(new GridLayout(2,1));
		
		JLabel iplabel = new JLabel("Enter Host IP:");
		login.add(iplabel);
		
		ipenter = new JTextField();
		login.add(ipenter);
		
		frame.add(login, BorderLayout.CENTER);
		
		JButton submit = new JButton("Connect");
		submit.addActionListener(new Connect());
		frame.add(submit, BorderLayout.SOUTH);
		
		frame.setVisible(true);
	}
	
	public static class Connect implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				sock = new Socket(ipenter.getText(), 4555);
				if (sock.isConnected()) {
					in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
					out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
					
					codeScreen = new JFrame();
					codeScreen.setSize(300,300);
					codeScreen.setLocationRelativeTo(frame);
					codeScreen.setLayout(new BorderLayout());
					codeScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					
					//error = new JLabel();
					//codeScreen.add(error, BorderLayout.NORTH);
					
					JPanel login = new JPanel();
					login.setLayout(new GridLayout(2,1));
					
					JLabel passlabel = new JLabel("Enter Security Key:");
					login.add(passlabel);
					
					passenter = new JTextField();
					login.add(passenter);
					
					codeScreen.add(login, BorderLayout.CENTER);
					
					JButton submit = new JButton("Enter");
					submit.addActionListener(new Security());
					codeScreen.add(submit, BorderLayout.SOUTH);
					
					codeScreen.setVisible(true);
					
				} else {
					error.setText("<html><span style='color:red'>Could not connect</span></html>");
				}
				
			} catch (UnknownHostException e1) {
				error.setText("<html><span style='color:red'>No server running on address</span></html>");
				e1.printStackTrace();
			} catch (IOException e1) {
				error.setText("<html><span style='color:red'>Error communicating with server</span></html>");
				e1.printStackTrace();
			}
		}
		
	}
	
	public static class Security implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				out.write(passenter.getText()+"\r\n");
				out.flush();
				if (in.readLine().equals("ACCEPTED")) {
					frame.setVisible(false);
					codeScreen.setVisible(false);
					BracketScreen.open();
				} else {
					codeScreen.setVisible(false);
					error.setText("<html><span style='color:red'>Incorrect security key</span></html>");
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}
}
