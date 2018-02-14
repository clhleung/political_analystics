import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.*;

public class Analyze {
	
	// Function to see if we ignore a row or not
	public boolean rowValid(String line){
		boolean isValid = true;
		// Deletes all spaces and whitespaces from donation row
		String test = line.replaceAll(" ","").trim();
		String rex = "^[a-zA-Z0-9]{9}[|][NAT]{0,1}[|][!-{}~]{0,3}[|][!-{}~]{0,5}[|][!-{}~]{0,18}[|][!-{}~]{0,3}[|][!-{}~]{0,3}[|][!-{}~]{1,200}[|][!-{}~]{0,30}[|][!-{}~]{0,2}[|][!-{}~]{5,9}[|][!-{}~]{0,38}[|][!-{}~]{0,38}[|][0-9]{8}[|][0-9]{1,14}(([.][0-9]{0,1}[0-9]{0,1})?)[|][|].{0,}$";
		Pattern patt = Pattern.compile(rex);
		Matcher match = patt.matcher(test);
		if (!match.matches()) isValid = false;
		System.out.println(line);
		System.out.println();
		System.out.println();
		System.out.println(isValid);
		System.out.println();
		System.out.println();
		return isValid;
	}
	
	// Function to write to repeat_donors.txt
	public void writeToFile(){
		try {
			BufferedWriter output = null;
			File repeat = new File("../output/repeat_donors.txt");
			output = new BufferedWriter(new FileWriter(repeat));
			//output.write("45");
			output.write("\n");
			//output.write("56");
			output.close();
			System.out.println("File created.");
		} catch (Exception e){
			System.out.println("Error creating the file.");
		}
	}
	
	// Function to pull the percentile from percentile.txt
	public int numInFile(String fileName){		
		// Convert to the appropriate file path
		String dataPath = "../input/" + fileName;
		int data = 0;
		try{
			File inData = new File(dataPath);
			String readLine = "";
			BufferedReader d = new BufferedReader(new FileReader(inData));
			while ((readLine = d.readLine()) != null) {
				// Get the percentile we need to calculate
                data = Integer.parseInt(readLine);
            }
		} catch (IOException e){
			e.printStackTrace();
		}
		return data;
	}
	
	// Function to parse data from the given itcont.txt
	public ArrayList<String> dataToArray(String fileName){
		ArrayList<String> theData = new ArrayList<String>();
		// Convert to the appropriate file path
		String dataPath = "../input/" + fileName;
		try{
			File inData = new File(dataPath);
			String readLine = "";
			BufferedReader d = new BufferedReader(new FileReader(inData));
			while ((readLine = d.readLine()) != null) {
				// Add data to array
				// Some way to parse through invalid rows
				boolean check = rowValid(readLine);
				if (check == true) theData.add(readLine);
            }
		} catch (IOException e){
			e.printStackTrace();
		}
		return theData;
	}
	
	// Main function, where itcont.txt & percentile.txt are passed as args
	public static void main (String [] args) throws IOException {
		Analyze event = new Analyze();
		// Get file arguments passed in
		String dataFile = args[0];
		String percentFile = args[1];		
		// ArrayList to hold data from itcont.txt
		ArrayList <String> inputs = event.dataToArray(dataFile);
		// Percentile to calculate using the nearest-rank method
		int perc = event.numInFile(percentFile);
        System.out.println(perc);	
		System.out.println(inputs.get(1));
		event.writeToFile();
	}
}