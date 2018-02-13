import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Analyze {
	public static void main (String [] args) throws IOException {
		// Get file arguments passed in
		String dataFile = args[0];
		String percentFile = args[1];		
		// Convert to the appropriate file path
		String dataPath = "../input/" + dataFile;
		String percentPath = "../input/" + percentFile;
		// ArrayList to hold data from itcont.txt
		ArrayList <String> inputs = new ArrayList <String>();
		try {
			File inData = new File(dataPath);
			File inPercent = new File(percentPath);
			String readLine = "";
			BufferedReader n = new BufferedReader(new FileReader(inPercent));
			BufferedReader d = new BufferedReader(new FileReader(inData));
			while ((readLine = n.readLine()) != null) {
                System.out.println(readLine);
            }
			while ((readLine = d.readLine()) != null) {
                inputs.add(readLine);
            }
			System.out.println(inputs.get(1));
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}