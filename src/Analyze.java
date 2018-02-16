import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;
import java.util.Collections;
import java.lang.Integer;
import java.lang.Double;

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
		return isValid;
	}
	
	// Function to write to repeat_donors.txt
	public void writeToFile(ArrayList<String> results){
		try {
			BufferedWriter output = null;
			File repeat = new File("./output/repeat_donors.txt");
			output = new BufferedWriter(new FileWriter(repeat));
			for (int i = 0; i < results.size(); i++){
				output.write(results.get(i));
				if (i != (results.size()-1)) output.write("\n");
			}			
			output.close();
			System.out.println("File created.");
		} catch (Exception e){
			System.out.println("Error creating the file.");
		}
	}
	
	// Function to pull the percentile from percentile.txt
	public int numInFile(String fileName){		
		// Convert to the appropriate file path
		String dataPath = "./input/" + fileName;
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
	
	//Function to check dates between 2 records
	public static boolean checkDates(String newDate, String storedDate){
		boolean greater = true;
		String newYear = newDate.substring(newDate.length()-4);
		String storedYear = storedDate.substring(storedDate.length()-4);
		String newMonth = newDate.substring(0,2);
		String storedMonth = storedDate.substring(0,2);
		String newDay = newDate.substring(2,4);
		String storedDay = storedDate.substring(2,4);
		// Don't consider record if new record's year is lower than last updated donation year
		if (Integer.parseInt(newYear) < Integer.parseInt(storedYear)){
			greater = false;
		}
		// If both have same year, we check to see if new record's month 
		// is lower than last updated donation month 
		if (Integer.parseInt(newMonth) < Integer.parseInt(storedMonth)){
			greater = false;
		}
		// If both have same year & month, we check to see if new record's day 
		// is lower than last updated donation day 
		if (Integer.parseInt(newDay) < Integer.parseInt(storedDay)){
			greater = false;
		}
		return greater;
	}
	
	public boolean dateValid(String date){
		boolean isValid = true;
		// Feb, April, June, Sept, Nov has 30 days, rest have 31 days, Feb is special case		
		String rex = "^(01|03|05|07|08|10|12)[0-2][0-9][0-9]{4}|(01|03|05|07|08|10|12)[0-3][0-1][0-9]{4}|(04|06|09|11)[0-3][0][0-9]{4}|(04|06|09|11)[0-2][0-9][0-9]{4}|(02)[0-2][0-9][0-9]{4}$";
		Pattern patt = Pattern.compile(rex);
		Matcher match = patt.matcher(date);
		if (!match.matches()) isValid = false;		
		return isValid;
	}
	
	// Function to parse valid rows stored in list of Record objects
	public ArrayList<String> resultString (int perc, ArrayList<Record> records){
		ArrayList<String> results = new ArrayList <String>();
		// Keep track of # of times a person has donated
		HashMap<String, Integer>nameZipNum = new HashMap<>();
		// Keep track of most recent date a person has donated
		HashMap<String, String>nameZipDate = new HashMap<>();
		// Keep track of donations a candidate received in a certain year from repeat donors 
		HashMap<String, ArrayList<Integer>> candZipYr = new HashMap<>();
		// Keep track of unique repeat donors per unique candidate
		HashMap<String, String> uniqueDonors = new HashMap<>();
		// To be used with above to keep track of # of unique repeat donors per unique candidate
		HashMap<String, Integer> uniqueDonorCnt = new HashMap<>();
		// Boolean for checking date that is greater than previous date
		boolean updateDate = true;		;
		for (Record rec : records){
			String nameZip = rec.getName().replaceAll(" ","").trim()+rec.getZip().replaceAll(" ","").trim();				
			if (nameZipNum.get(nameZip) == null){
				if (dateValid(rec.getDate()) == true){
					//First time we see person of a particular zip has donated
                    nameZipNum.put(nameZip, 1);
                    // Update the most recent date a person last donated				
			        nameZipDate.put(nameZip, rec.getDate());	
				}								
			} else {
				// Check for donations that has a date that is later than previous stored date 
				// for that donor of a particular zip code
				updateDate = checkDates(rec.getDate(), nameZipDate.get(nameZip));
				if (updateDate == true && dateValid(rec.getDate()) == true) {
					// Increment donation count for this person by 1, is a repeat donar we check later
				    nameZipNum.put(nameZip, 1+nameZipNum.get(nameZip));
				}
				
			}
            if (updateDate == true && dateValid(rec.getDate()) == true){
				// Update donations candidate gets from donors in a certain year
			    String whenYr = rec.getDate().substring(rec.getDate().length() - 4);
			    String candZipWhen = rec.getCmte()+rec.getZip()+ whenYr;
			    if (candZipYr.get(candZipWhen) == null){
				   candZipYr.put(candZipWhen, new ArrayList<Integer>());
			    } 
				// Add index of repeat donor, even if they are the same repeat donors
				if (nameZipNum.get(nameZip) > 1){
					candZipYr.get(candZipWhen).add(records.indexOf(rec));
					if (uniqueDonors.containsValue(nameZip) == false){
						uniqueDonors.put(candZipWhen, nameZip);
						if (uniqueDonorCnt.get(candZipWhen) != null ){
							uniqueDonorCnt.put(candZipWhen, uniqueDonorCnt.get(candZipWhen)+1);
						} else {
							uniqueDonorCnt.put(candZipWhen, 1);
						}						
					} else {
						// Case where same donor donates to same person in multiple years
						boolean different = true;
						// Iterate through keys with the same donors & check candidate years
                        for (Map.Entry entry: uniqueDonors.entrySet()){
							if (nameZip.equals(entry.getValue())){
								int yr = Integer.parseInt(whenYr);
								String storedYr = (String)entry.getKey();
								int oldYr = Integer.parseInt(storedYr.substring(storedYr.length()-4));
								if (yr == oldYr) different = false;
							}
						}
                        if (different == true) uniqueDonorCnt.put(candZipWhen, 1); 						
					}
				}			    
			}	
			
			// Check if this record's donor is a repeat donor given the date is valid
			if ( dateValid(rec.getDate()) == true && nameZipNum.get(nameZip) > 1){
				String whenYr = rec.getDate().substring(rec.getDate().length() - 4);
				String candZipWhen = rec.getCmte()+rec.getZip()+ whenYr;
				// Get current number of repeat donors so far based on records that have already been parsed
				String contribut = String.valueOf(uniqueDonorCnt.get(candZipWhen));
				// Find total $$ for a candidate based on valid records recorded by their indexes
				ArrayList<Integer> amtList = candZipYr.get(candZipWhen);
				ArrayList<Double> listOfAmts = new ArrayList<>();
				double money = 0;
				for (int i : amtList){
					money += records.get(i).getAmt();
					listOfAmts.add(records.get(i).getAmt());
				}
				// Find percentile we need to calculate
				Collections.sort(listOfAmts);
				double index = (perc/100) * listOfAmts.size();				
				int roundIndex = (int)Math.round(index);
                double d = listOfAmts.get(roundIndex);	
                int percent = (int)d;  
                String row = rec.getCmte()+"|"+rec.getZip()+"|"+whenYr+"|";				
				row += String.valueOf(percent) + "|"+String.valueOf((int)money)+"|"+ contribut; 
				results.add(row);
			}
		}		
		return results;
	}
	
	// Function to parse data from the given itcont.txt
	public ArrayList<Record> dataToArray(String fileName){
		ArrayList<Record> theData = new ArrayList<Record>();
		// Convert to the appropriate file path
		String dataPath = "./input/" + fileName;		
		try{
			File inData = new File(dataPath);
			String readLine = "";
			BufferedReader d = new BufferedReader(new FileReader(inData));
			while ((readLine = d.readLine()) != null) {				
				// method to check valid rows through a Regex String
				boolean check = rowValid(readLine);				
				if (check == true){
					// Create a Record object using that file line and add it to the ArrayList
					Record lines = new Record(readLine);
					theData.add(lines);
				} 
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
        //String dataFile = "itcont.txt";
        //String percentFile = "percentile.txt";	
		// ArrayList to hold data from itcont.txt
		ArrayList <Record> inputs = event.dataToArray(dataFile);
		// Get the percentile from percentile.txt 
		int perc = event.numInFile(percentFile);
		// Leads to main method used to parse the valid records
		ArrayList <String> results = event.resultString(perc, inputs);         
		event.writeToFile(results);
	}
}