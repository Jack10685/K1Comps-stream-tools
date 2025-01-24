package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

public class DriverServer {
	public static void main(String args[]) {
		while (true) {
			start();
		}
	}
	
	public static void start() {
		try {
			ServerSocket serv = new ServerSocket(4555);
			
			System.out.println("Waiting for client...");
			Socket client = serv.accept();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			char[] pass = new char[6];
			for (int i = 0; i < 6; i++) {
				pass[i] = (char) ('0' + (char) (Math.random()*10));
			}
			System.out.println("Connection received from: "+client.getInetAddress());
			System.out.println("Security code: "+(new String(pass)));
			
			String in = br.readLine();
			System.out.println(in);
			if (Arrays.equals(pass, in.toCharArray())) {
				System.out.println("Authenticated client:"+client.getInetAddress());
				
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
				bw.write("ACCEPTED\r\n");
				bw.flush();
				
				System.out.println("Waiting for input...");
				String line = br.readLine();
				while (line != null) {
					System.out.println("Received input: "+line);
					if (line.startsWith("driver1")) {
						System.out.println("Updating Driver1");
						File f = new File("C:\\K1Comps\\driver1.txt");
						FileWriter fw = new FileWriter(f);
						BufferedWriter bw2 = new BufferedWriter(fw);
						bw2.write(line.substring(8));
						bw2.close();
						bw.write("DONE\r\n");
						bw.flush();
					} else if (line.startsWith("driver2")) {
						System.out.println("Updating Driver2");
						File f = new File("C:\\K1Comps\\driver2.txt");
						FileWriter fw = new FileWriter(f);
						BufferedWriter bw2 = new BufferedWriter(fw);
						bw2.write(line.substring(8));
						bw2.close();
						bw.write("DONE\r\n");
						bw.flush();
					} else {
						bw.write("INVALID\r\n");
						bw.flush();
					}
					line = br.readLine();
				}
				System.out.println("Client disconnect");
			} else {
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
				bw.write("DENIED");
				bw.close();
				br.close();
				client.close();
			}
		} catch(SocketException e) {
			System.out.println("Client disconnect");
		} catch (IOException e) {
			System.out.println("Communication Error");
		}
	}
	
	
}
