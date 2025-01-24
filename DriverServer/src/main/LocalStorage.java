package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LocalStorage {
	public static void updateNames() {
		try {
			File f = new File("C:\\K1Comps\\driver1.txt");
			FileWriter fw = new FileWriter(f);
			BufferedWriter driver = new BufferedWriter(fw);
			System.out.println("Setting Driver 1 to "+Challonge.getDriverName(Challonge.driver1));
			driver.write(Challonge.getDriverName(Challonge.driver1));
			driver.flush();
			driver.close();
			
			f = new File("C:\\K1Comps\\driver2.txt");
			fw = new FileWriter(f);
			driver = new BufferedWriter(fw);
			System.out.println("Setting Driver 2 to "+Challonge.getDriverName(Challonge.driver2));
			driver.write(Challonge.getDriverName(Challonge.driver2));
			driver.flush();
			driver.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}
}
