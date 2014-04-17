package frontEndCode;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Dictionary;
import java.util.Hashtable;

public class magicFunctions {

	String[] elements;
	Dictionary<String, String> conditionStatement_dict = new Hashtable<String, String>(); // holds conditions e.g x<y
	Dictionary<String, String> conditions_dict = new Hashtable<String, String>();
	
	public magicFunctions() {}
	
	
	String[] readFile(File filePath) throws IOException{
		
		String strLine, fileContent= "";
		String [] stLines = null;
		
		try {
			
			FileInputStream fileInStream = new FileInputStream(filePath);
			DataInputStream input = new DataInputStream(fileInStream);
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
		
			while ((strLine = br.readLine()) != null)  {
				fileContent = fileContent.concat(strLine + "\n");
			}
			
			stLines = fileContent.split("\\n");
			br.close();
		} catch (FileNotFoundException e) {
			// TODO: File couldn't be found
		}
		return stLines;
	}
	
	void storeOperationType(String operationType, String condition){
		conditionStatement_dict.put(operationType, condition);
	}
	
	String getStatement(String operationType, String Inc_dec){
		AsmTemplates at = new AsmTemplates();
		
		
		String condition = conditionStatement_dict.get(operationType);
		conditions_dict.put(operationType,condition.replaceAll("\\w+", ""));
		String a = ""; 
		String b = "";
		int operator = 0;
		//>
		if (condition.matches(".*\\b>\\b.*")){
			elements = condition.split("\\b>\\b");
			operator = 1;
		}
		//>=
		else if (condition.matches(".*\\b>=\\b.*")){
			elements = condition.split("\\b>=\\b");
			operator = 2;
		}
		//<
		else if (condition.matches(".*\\b<\\b.*")){
			elements = condition.split("\\b<\\b");
			operator = 3;
		}
		//<=
		else if (condition.matches(".*\\b<=\\b.*")){
			elements = condition.split("\\b<=\\b");
			operator = 4;
		}
		//!=
		else if (condition.matches(".*\\b!=\\b.*")){
			elements = condition.split("\\b!=\\b");
			operator = 5;
		}
		//==
		else if (condition.matches(".*\\b==\\b.*")){
			elements = condition.split("\\b==\\b");
			operator = 6;
		}
		a = elements[0]; 
		b = elements[1];
		
//		if(elements[0].trim().matches("\\d+")){
//			a = elements[1];
//			b = elements[0];
//		}
//		else{
//			a = elements[0];
//			b = elements[1];
//		}
		
		if(a.trim().matches("\\d+")){
			if(Integer.parseInt(a.trim()) > 255)
				a = "255";
		}
		else if(b.trim().matches("\\d+")){
			if(Integer.parseInt(b.trim()) > 255)
				b = "255";
		}
		
		String loopName;
		loopName = operationType.replaceAll("<", "");
		
	return at.writeForStatement(a, b, operator, loopName);
	}

	String getEndForStatement(String Inc_dec, String operation){
		int forNr = Integer.parseInt(operation.replaceAll("[^\\d]+",""));
		if(operation.contains("nested")){
			return new AsmTemplates().endforStatment(Inc_dec, operation, forNr, true);
		}else{
			return new AsmTemplates().endforStatment(Inc_dec, operation, forNr, false);
		}
		
	}
	String getEndIStatement(){
		return new AsmTemplates().endIfStatment();
	}
	
	String getStatement(String operationType){
		AsmTemplates at = new AsmTemplates();
		
		String condition = conditionStatement_dict.get(operationType);
		String a = ""; 
		String b = "";
		int operator = 0;
		//>
		if (condition.matches(".*\\b>\\b.*")){
			elements = condition.split("\\b>\\b");
			operator = 1;
		}
		//>=
		else if (condition.matches(".*\\b>=\\b.*")){
			elements = condition.split("\\b>=\\b");
			operator = 2;
		}
		//<
		else if (condition.matches(".*\\b<\\b.*")){
			elements = condition.split("\\b<\\b");
			operator = 3;
		}
		//<=
		else if (condition.matches(".*\\b<=\\b.*")){
			elements = condition.split("\\b<=\\b");
			operator = 4;
		}
		//!=
		else if (condition.matches(".*\\b!=\\b.*")){
			elements = condition.split("\\b!=\\b");
			operator = 5;
		}
		//==
		else if (condition.matches(".*\\b==\\b.*")){
			elements = condition.split("\\b==\\b");
			operator = 6;
		}

		a = elements[0]; 
		b = elements[1];
	
		return at.writeIfStatement(a, b, operator);
	}
		
}
	
	
