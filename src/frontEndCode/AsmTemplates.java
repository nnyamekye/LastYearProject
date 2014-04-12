package frontEndCode;

import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;


public class AsmTemplates extends magicFunctions{
	
	String[] strLines = null;
	String path = "temp";
	String template ="";
	File temporally_file;
	
	static int forStartCount = 1;
	static int forEndCount = 1;
	static int ifStartCount = 1;
	static int ifEndCount = 1;
	static int currentBank = 0;
	
	static Dictionary<String, Integer> varInBank = new Hashtable<String, Integer>();
	
	public AsmTemplates() {
		
	}

	void storeVarInBAnk(String varName, int bank){
		varInBank.put(varName, bank);
	}

	String bankCheck(String varName){
		String returnStatement = "";
		int varInt = varInBank.get(varName);
		if(varInt != currentBank){
			currentBank = varInBank.get(varName);
		
			switch(currentBank){
			case 0:// Bank 0
				returnStatement="\nbcf\tSTATUS,RP1"
						+ "\nbcf\tSTATUS,RP0";
				break;
			case 1:// Bank 1
				returnStatement="\nbcf\tSTATUS,RP1"
						+ "\nbsf\tSTATUS,RP0";
				break;
			case 2:// Bank 2
				returnStatement="\nbsf\tSTATUS,RP1"
						+ "\nbcf\tSTATUS,RP0";
				break;
			case 3:// Bank 3
				returnStatement="\nbsf\tSTATUS,RP1"
						+ "\nbsf\tSTATUS,RP0";
				break;
			}
		}
		
		return returnStatement;
	}
	
	String[] getTemplate_A() throws IOException{
		File asmTemplate = new File("src/sharedResource/asmTemplate1");
		if(asmTemplate.exists())
			strLines = readFile(asmTemplate);
			for(String line: strLines)
				template = template.concat(line + "\n");
		return strLines;
	}
	
	String[] getTemplate_B() throws IOException{
		File asmTemplate = new File("src/sharedResource/asmTemplate2");
		if(asmTemplate.exists())
			strLines = readFile(asmTemplate);
			for(String line: strLines)
				template = template.concat(line + "\n");
		return strLines;
	}
	
	String intitialise_int(String identifier, String value){
		forEndCount = forStartCount = 1;
		ifEndCount = ifStartCount = 1;
		
		String statement = "";
		
		if (Integer.parseInt(value) == 0){
			statement = "\nclrf\t" + identifier + "\n"; 
		}else {
			statement = bankCheck(identifier);
			statement += "\nmovlw\td'"+value+"'\nmovwf\t"+ identifier + "\n" ;
		}
			
		return statement;
	}
	
	public String writeForStatement(String val1, String val2, int operator){
		String code = "";
		switch (operator) {
		case 1:
			//> done
			code = 	"\nfor_start_"+ forStartCount 
					+ "\nbcf\tSTATUS,C";
					
			// is val2 the inc/dec value
			if (val2.trim().matches("\\d+")){
				code += "\nmovlw\td'"+ val2;
			}else{
				code += bankCheck(val2);
				code += "\nmovf\t"+ val2 + ",0";
			}
			// is val1 the inc/dec value					
			if (val1.trim().matches("\\d+")){
				code += "\nsublw\td'"+ val1 +"'";
			}else{
				code += bankCheck(val1);
				code += "'\nsubwf\t" + val1 + ",0";
			}
			code +=	"\nbtfss\tSTATUS,Z\t"  
					+ "\nbtfss\tSTATUS,C"
					+ "\ngoto\tfor_end_" + forStartCount++ 
					+ "\n";	
						
			break;
		case 2:
			//>= done
			code= 	"\nfor_start_"+ forStartCount
					+ "\nbcf\tSTATUS,C";
					
			if (val1.trim().matches("\\d+")){
				code += "\nmovlw\td'"+ val1 +"'";
			}else{
				code += bankCheck(val1);
				code += "\nmovf\t"+ val1 + ",0";
			}
			code +=	"\nsublw\td'255'";
					
			if (val2.trim().matches("\\d+")){
				code += "\naddlw\td'"+ val2 +"'";
			}else{
				code += bankCheck(val2);
				code += "\naddwf\t"+ val2 + ",0";
			}
			code +=	"\nbtfsc\tSTATUS,C"
					+ "\ngoto\tfor_end_" + forStartCount++ 
					+ "\n";			
			
			break;
		case 3:
			//< done - DONE 
			code= 	"\nfor_start_"+ forStartCount
					+"\nbcf\tSTATUS,C"; 
					
			if (val2.trim().matches("\\d+")){
				code += "\nmovlw\td'"+ val2 +"'";
			}else{
				code += bankCheck(val2);
				code += "\nmovf\t"+ val2 + ",0";
			}
					
			code +=	"\nsublw\td'255'\n"
					+ "addlw\td'1'\n"
					+ "\nbtfss\tSTATUS,Z\n";
			
			if (val1.trim().matches("\\d+")){
				code += "\naddlw\td'"+ val1 +"'";
			}else{
				code += bankCheck(val1);
				code += "\naddwf\t"+ val1 + ",0";
			}	
			
			code += "\nbtfsc\tSTATUS,C"
					+ "\ngoto\tfor_end_" + forStartCount++ 
					+ "\n";			
			break;
		case 4:
			//<= done
			code= 	"\nfor_start_"+ forStartCount;
			
			if (val2.trim().matches("\\d+")){
				code += "\nmovlw\td'"+ val2 +"'";
			}else{
				code += bankCheck(val2);
				code += "\nmovf\t"+ val2 + ",0";
			}
			code +=	"\nsublw\td'255'"
					+ "\nbcf\tSTATUS,C";
			
			if (val1.trim().matches("\\d+")){
				code += "\naddlw\td'"+ val1 +"'";
			}else{
				code += bankCheck(val1);
				code += "\naddwf\t"+ val1 + ",0";
			}	
			code += "\nbtfsc\tSTATUS,C"
					+ "\ngoto\tfor_end_" + forStartCount++ 
					+ "\n";			
			break;
		case 5:
			//!= done
			code= 	"\nfor_start_"+ forStartCount;
			
			if (val1.trim().matches("\\d+")){
				code += "\nmovlw\td'"+ val1 +"'";
			}else{
				code += bankCheck(val1);
				code += "\nmovf\t"+ val1 + ",0";
			}
			code +="\nbcf\tSTATUS,C";
					
			if (val2.trim().matches("\\d+")){
				code += "\nsublw\td'"+ val2 +"'";
			}else{
				code += bankCheck(val2);
				code += "\nsubwf\t" + val2 + ",0";
			}
			code += "\nbtfsc\tSTATUS,Z"
					+ "\ngoto\tfor_end_" + forStartCount++ 
					+ "\n";				
			break;
		case 6:
			//== done
			code= 	"\nfor_start_"+ forStartCount;
			if (val1.trim().matches("\\d+")){
				code += "\nmovlw\td'"+ val1 +"'";
			}else{
				code += bankCheck(val1);
				code += "\nmovf\t"+ val1 + ",0";
			}
			code += "\nbcf\tSTATUS,Z";
			
			if (val2.trim().matches("\\d+")){
				code += "\nsublw\td'"+ val2 +"'";
			}else{
				code += bankCheck(val2);
				code += "\nsubwf\t" + val2 + ",0";
			}		
			code += "\nbtfss\tSTATUS,Z"
					+ "\ngoto\tfor_end_" + forStartCount++ 
					+ "\n";			
			break;
		}
		return code;		
		
	}
	
	public String endforStatment(String inc_dec, String operation) {
		String code="";
		code += bankCheck(inc_dec.replaceAll("[^\\w]+", ""));
		
		if(inc_dec.contains("++")){
			code = "incf\t" + inc_dec.replaceAll("[^\\w]+", "") + ",1\n" ;
		}else if(inc_dec.contains("--")) {
			code = "decf\t" + inc_dec.replaceAll("[^\\w]+", "") + ",1\n" ;
		}

		if(operation.equals(">=")){
			code +="addlw\td'1'\n"
					+ "btfss\tSTATUS,Z\n";
		}
		
		return	code + "goto\tfor_start_"  + forEndCount +"\n"
				+ "for_end_"+ forEndCount++; 
	}
	
	public String endIfStatment() {
		return	"if_end_"+ ifEndCount++ + "\n"; 
	}

	public String writeIfStatement(String val1, String val2, int operator) {
		String code = "";
		switch (operator) {
		case 1:
			//> done
			code = 	"\nif_start_"+ ifStartCount 
					+ "\nbcf\tSTATUS,C";
					
			// is val2 the inc/dec value
			if (val2.trim().matches("\\d+")){
				code += "\nmovlw\td'"+ val2;
			}else{
				code += bankCheck(val2);
				code += "\nmovf\t"+ val2 + ",0";
			}
			// is val1 the inc/dec value					
			if (val1.trim().matches("\\d+")){
				code += "\nsublw\td'"+ val1 +"'";
			}else{
				code += bankCheck(val1);
				code += "\nsubwf\t" + val1 + ",0";
			}
			code +=	"\nbtfss\tSTATUS,Z\t"  
					+ "\nbtfss\tSTATUS,C"
					+ "\ngoto\tif_end_" + ifStartCount++ 
					+ "\n";	
						
			break;
		case 2:
			//>= done
			code= 	"\nif_start_"+ ifStartCount
					+ "\nbcf\tSTATUS,C";
					
			if (val1.trim().matches("\\d+")){
				code += "\nmovlw\td'"+ val1 +"'";
			}else{
				code += bankCheck(val1);
				code += "\nmovf\t"+ val1 + ",0";
			}
			code +=	"\nsublw\td'255'";
					
			if (val2.trim().matches("\\d+")){
				code += "\naddlw\td'"+ val2 +"'";
			}else{
				code += bankCheck(val2);
				code += "\naddwf\t"+ val2 + ",0";
			}
			code +=	"\nbtfsc\tSTATUS,C"
					+ "\ngoto\tif_end_" + ifStartCount++ 
					+ "\n";			
			
			break;
		case 3:
			//< done - DONE 
			code= 	"\nif_start_"+ ifStartCount
					+"\nbcf\tSTATUS,C"; 
					
			if (val2.trim().matches("\\d+")){
				code += "\nmovlw\td'"+ val2 +"'";
			}else{
				code += bankCheck(val2);
				code += "\nmovf\t"+ val2 + ",0";
			}
			code +=	"\nsublw\td'255'\n"
					+ "addlw\td'1'\n"
					+ "\nbtfss\tSTATUS,Z\n";
			
			if (val1.trim().matches("\\d+")){
				code += "\naddlw\td'"+ val1 +"'";
			}else{
				code += bankCheck(val1);
				code += "\naddwf\t"+ val1 + ",0";
			}	
			
			code += "\nbtfsc\tSTATUS,C"
					+ "\ngoto\tif_end_" + ifStartCount++ 
					+ "\n";			
			break;
		case 4:
			//<= done
			code= 	"\nif_start_"+ ifStartCount;
			
			if (val2.trim().matches("\\d+")){
				code += "\nmovlw\td'"+ val2 +"'";
			}else{
				code += bankCheck(val2);
				code += "\nmovf\t"+ val2 + ",0";
			}
			code +=	"\nsublw\td'255'"
					+ "\nbcf\tSTATUS,C";
			
			if (val1.trim().matches("\\d+")){
				code += "\naddlw\td'"+ val1 +"'";
			}else{
				code += bankCheck(val1);
				code += "\naddwf\t"+ val1 + ",0";
			}	
			code += "\nbtfsc\tSTATUS,C"
					+ "\ngoto\tif_end_" + ifStartCount++ 
					+ "\n";			
			break;
		case 5:
			//!= done
			code= 	"\nif_start_"+ ifStartCount;
			
			if (val1.trim().matches("\\d+")){
				code += "\nmovlw\td'"+ val1 +"'";
			}else{
				code += bankCheck(val1);
				code += "\nmovf\t"+ val1 + ",0";
			}
			code +="\nbcf\tSTATUS,C";
					
			if (val2.trim().matches("\\d+")){
				code += "\nsublw\td'"+ val2 +"'";
			}else{
				code += bankCheck(val2);
				code += "\nsubwf\t" + val2 + ",0";
			}
			code += "\nbtfsc\tSTATUS,Z"
					+ "\ngoto\tif_end_" + ifStartCount++ 
					+ "\n";				
			break;
		case 6:
			//== done
			code= 	"\nif_start_"+ ifStartCount;
			if (val1.trim().matches("\\d+")){
				code += "\nmovlw\td'"+ val1 +"'";
			}else{
				code += bankCheck(val1);
				code += "\nmovf\t"+ val1 + ",0";
			}
			code += "\nbcf\tSTATUS,Z";
			
			if (val2.trim().matches("\\d+")){
				code += "\nsublw\td'"+ val2 +"'";
			}else{
				code += bankCheck(val2);
				code += "\nsubwf\t" + val2 + ",0";
			}		
			code += "\nbtfss\tSTATUS,Z"
					+ "\ngoto\tif_end_" + ifStartCount++ 
					+ "\n";			
			break;
		}
		return code;		
		
	}
}
