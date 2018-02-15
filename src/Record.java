import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.*;

public class Record {
	
	private String cmte = null;
	private String name = null;
	private String zip = null;
	private String date = null;
	private String amt = null;
	private String other = null;
	
	public Record(String readLine){
		// Split valid rows based on "|" using a Regex String 
		String [] checks = readLine.split("[|]");
		this.cmte = checks[0];
		this.name = checks[7];
		this.zip = checks[10];
		this.date = checks[13];
		this.amt = checks[14];
		this.other = checks[15];
	}
	
	// Methods to return the values we're interested in 
	public String getName() {
		return name;
	}
	
	public String getZip() {
		return zip;
	}
	
	public String getDate() {
		return date;
	}
	
	public String getAmt() {
		return amt;
	}
	
	public String getOther() {
		return other;
	}
}