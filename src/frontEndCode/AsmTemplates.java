package frontEndCode;

import java.io.File;
import java.io.IOException;

public class AsmTemplates extends magicFunctions{
	
	String[] strLines = null;
	String path = "temp";
	String template ="";
	File temporally_file;
	
	static int forStartCount = 1;
	static int ForEndCount = 1;
	
	public AsmTemplates() {
		
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
		ForEndCount = forStartCount = 1;
		
		String statement = "";
		
		if (Integer.parseInt(value) == 0){
			statement = "\nclrf\t" + identifier + "\n"; 
		}else {
			statement = "\nmovlw\td'"+value+"'\nmovwf\t"+ identifier + "\n" ;
		}
			
		return statement;
	}
	
	public String writeForStatement(String i, String k, int operator){
		String code = "";
		switch (operator) {
		case 1:
			//> done
			code = 	"\nfor_start_"+ forStartCount 
					+ "\nbcf\tSTATUS,C"
					+ "\nmovlw\td'"+ k 
					+ "'\nsubwf\t" + i 
					+ ",0\nbtfss\tSTATUS,Z\t"  
					+ "\nbtfss\tSTATUS,C"
					+ "\ngoto\tfor_end_" + forStartCount++ 
					+ "\n";	
						
			break;
		case 2:
			//>= done
			code= 	"\nfor_start_"+ forStartCount
					+ "\nbcf\tSTATUS,C"
					+ "\nmovf\t"+ i 
					+ ",0\nsublw\td'255'"
					+ "\naddlw\td'" + k 
					+ "'\nbtfsc\tSTATUS,C"
					+ "\ngoto\tfor_end_" + forStartCount++ 
					+ "\n";			
			break;
		case 3:
			//< done
			code= 	"\nfor_start_"+ forStartCount
					+"\nbcf\tSTATUS,C" 
					+"\nmovlw\td'"+ k 
					+ "\nsublw\td'255'\n"
					+ "addlw\td'1'\n"
					+ "addwf\t" + i + ",0"
					+ "\nbtfsc\tSTATUS,C"
					+ "\ngoto\tfor_end_" + forStartCount++ 
					+ "\n";			
			break;
		case 4:
			//<= done
			code= 	"\nfor_start_"+ forStartCount 
					+ "\nmovlw\td'"+ k 
					+ "'\nsublw\td'255'"
					+ "\nbcf\tSTATUS,C"
					+ "\naddwf\t" + i + ",0"
					+ "\nbtfsc\tSTATUS,C"
					+ "\ngoto\tfor_end_" + forStartCount++ 
					+ "\n";			
			break;
		case 5:
			//!= done
			code= 	"\nfor_start_"+ forStartCount
					+ "\nmovf\t"+ i 
					+ ",0\nbcf\tSTATUS,C"
					+ "\nsublw\td'" + k
					+ "'\nbtfsc\tSTATUS,Z"
					+ "\ngoto\tfor_end_" + forStartCount++ 
					+ "\n";				
			break;
		case 6:
			//== done
			code= 	"\nfor_start_"+ forStartCount
					+ "\nmovf\t"+ i 
					+ "\nbcf\tSTATUS,Z"
					+ "\nsublw\td'" + k
					+ "'\nbtfss\tSTATUS,Z"
					+ "\ngoto\tfor_end_" + forStartCount++ 
					+ "\n";			
			break;
		}
		return code;		
		
	}
	
	public String endStatment(String inc_dec, String operation) {
		String code="";
	
		if(inc_dec.contains("++")){
			code = "incf\t" + inc_dec.replaceAll("[^\\w]+", "") + ",1\n" ;
		}else if(inc_dec.contains("--")) {
			code = "decf\t" + inc_dec.replaceAll("[^\\w]+", "") + ",1\n" ;
		}
		if(operation.equals("<=")){
			code +="addlw\td'1'\n"
					+ "btfss\tSTATUS,C\n";
		}
		if(operation.equals(">=")){
			code +="addlw\td'1'\n"
					+ "btfss\tSTATUS,Z\n";
		}
		
		
		return	code + "goto\tfor_start_"  + ForEndCount +"\n"
				+ "for_end_"+ ForEndCount++; 
	
	}
}
