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
	Dictionary<String, String> conditionStatement_dict = new Hashtable<String, String>();
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
			
		} catch (FileNotFoundException e) {
			// TODO: File couldn't be found
		}
		return stLines;
	}
	
	void storeOperationType(String operationType, String condition){
		conditionStatement_dict.put(operationType, condition);
		
	}
	
	String getStatement(String conditionStatement, String Inc_dec){
		AsmTemplates at = new AsmTemplates();
		
		
		String condition = conditionStatement_dict.get(conditionStatement);
		conditions_dict.put(conditionStatement,condition.replaceAll("\\w+", ""));
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
		//TODO<=
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
		System.out.println(Inc_dec.replaceAll("[^\\w]+", ""));
		if (elements[0].equals(Inc_dec.replaceAll("[^\\w]+", ""))){
			a = elements[0]; 
			b = elements[1];
		}
		else if(elements[1].equals(Inc_dec.replaceAll("[^\\w]+", ""))){
			a = elements[1]; 
			b = elements[0];
		}
		
	return at.writeForStatement(a, b, operator);
	}

	String getEndStatement(String Inc_dec, String operation){
		
		return new AsmTemplates().endStatment(Inc_dec, conditions_dict.get(operation));
	}
	
	String getStatement(String operationType){
		String condition = conditionStatement_dict.get(operationType);
		String code ="";
		
		//>
		if (condition.matches(".*\\b>\\b.*")){
			elements = condition.split("\\b>\\b");	
		}
		//>=
		if (condition.matches(".*\\b>=\\b.*")){
			elements = condition.split("\\b>=\\b");
		}
		//<
		if (condition.matches(".*\\b<\\b.*")){
			elements = condition.split("\\b<\\b");
		}
		//<=
		if (condition.matches(".*\\b<=\\b.*")){
			elements = condition.split("\\b<=\\b");
		}
		//!=
		if (condition.matches(".*\\b!=\\b.*")){
			elements = condition.split("\\b!=\\b");
		}
		//==
		if (condition.matches(".*\\b==\\b.*")){
			elements = condition.split("\\b>\\b");
		}
		
		
	return code;
	}
}
	
	
