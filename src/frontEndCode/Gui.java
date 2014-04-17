package frontEndCode;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.filechooser.FileNameExtensionFilter;

import sharedResource.*;

public class Gui extends JFrame {
	
	private static final long serialVersionUID = 1L;
	JDialog dialogEdit = new JDialog();
	JScrollPane scrollPanelC = new JScrollPane();
	JScrollPane scrollPanelAsm = new JScrollPane();
	JScrollPane scrollPanelEdit = new JScrollPane();
	JTextArea txtAreaAsm = new JTextArea();
	JTextArea txtAreaC = new JTextArea();
	JTextArea txtAreaEdit = new JTextArea();
	JButton btnSaveC = new JButton("Save");
	JButton btnSaveAsm = new JButton("Save");
	JButton btnOpenC = new JButton("Open");
	JButton btnOpenAsm = new JButton("Open");		
	JButton btnClearC = new JButton("Clear");
	JButton btnClearAsm = new JButton("Clear"); 
	JButton btnToAsm = new JButton("-->");
	JButton btnToC = new JButton("<--");		
	JLabel lblC = new JLabel("C Code"); 
	JLabel lblAsm = new JLabel(Constants.ASM_full + " Code");
	JFileChooser fc = new JFileChooser();
	
	int currentKeyHandler= 0;
	
	boolean instate = false;
	boolean initialise = true;
	
	GroupLayout layout = new GroupLayout(getContentPane());
	
	magicFunctions mf = new magicFunctions();
	
	static Dictionary<String, String> var_dict = new Hashtable<String, String>();
	Dictionary<String, String> forStatement_dict = new Hashtable<String, String>();
	
	File temporally_file;
	
	Pattern pattern;
	Matcher matcher;
	
	String sourceCode = "";
	String intStatement = "";
	String forStatement = "";
	String ifStatement = "";
	
	Gui()
	{
		initialise();
		
		btnOpenC.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openButtenEvent(Constants.C);
			}
		});
		
		btnOpenAsm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openButtenEvent(Constants.ASM);
			}
		});

		btnClearC.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				txtAreaC.setText(null);
			}
		});
		
		btnClearAsm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				txtAreaAsm.setText(null);
			}
		});
		
		btnSaveC.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveButtonEvent(Constants.C);
			}
		
		});
		
		btnSaveAsm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveButtonEvent(Constants.ASM);
			}
		});
		
		btnToAsm.addActionListener(new ActionListener() {

		//	@SuppressWarnings("static-access")
			@Override
			public void actionPerformed(ActionEvent e) {
				Enumeration<String> identifiers0 = var_dict.keys();
				Deque<String> stack = new ArrayDeque<String>();
				AsmTemplates at = new AsmTemplates();
				
				int actual_forCount = 1;
				int open_forCount = 0;
				int nestedForIncrement=1;
				int for_counter = 1;
				int nestedFor_counter = 1;
				
				int actual_ifCount = 1; 
				int open_IfCount = 0;
				int nestedIfIncrement = 1; 
				int if_counter = 1;
				int ifNested_Count = 1;
				
				String[] keywords = {"int","for","if"};
				String[] patterns_array = {
						".*#include.*",				// #include
						"^\\s*int.*\\;",			// int decleration
						"\\s+",						// Whitespace
						"^\\s*int\\s+main.*\\{.*", 	// main
						"^\\s*for.*\\{.*",			// for
						"^\\s*if.*\\{.*",			// if
						"^\\s*.*\\}.*",				// }
						"^\\s*return.*\\;",			// return
						"\\s*[\\+{2}]*\\w+\\s*[\\+{2}]*;",
						"\\s*[-{2}]*\\w+\\s*[-{2}]*;"
				};

				txtAreaAsm.setText("");	
				sourceCode="";
				
				while (identifiers0.hasMoreElements()){
					var_dict.remove(identifiers0.nextElement().toString());
				}
				
				for (int i = 1 ; i <= 3; i++){
					switch(i){ 
						case 1:
							int index_count = 0;
							
							for (String strline : txtAreaC.getText().split("\\n+")){	
								boolean matchFound = false;
								for (String regex_string : patterns_array){
									pattern = Pattern.compile(regex_string);
									matcher = pattern.matcher(strline);
									if(!matchFound){
										if(matcher.matches()){
											switch(index_count){
											case 0: // #include
												sourceCode += strline.replaceAll(regex_string, matcher.group()+"*remove*").trim()+"\n";
												matchFound = true;
												index_count = 0;
												break;
											case 1: // int decleration
												sourceCode += strline.replaceAll(regex_string, matcher.group()+"*remove*").trim()+"\n";
												matchFound = true;
												index_count = 0;
												break;
											case 2: // whitespace
												sourceCode += strline.replaceAll(regex_string,"").trim();
												matchFound = true;
												index_count = 0;
												break;
											case 3: // main
												sourceCode += strline.replaceFirst(regex_string, "<main<").trim() +"\n";
												stack.push("main");
												matchFound = true;
												index_count = 0;
												break;
											case 4: // for
												if(stack.contains("for")){
													sourceCode += strline.replaceAll(regex_string, matcher.group()+"<nested_for<"+ nestedForIncrement++  + "<").trim()+"\n";
													nestedFor_counter = nestedForIncrement;
													open_forCount++;
												}
												else{
													sourceCode += strline.replaceAll(regex_string, matcher.group()+"<for<"+ actual_forCount++  + "<").trim()+"\n";
												 	for_counter = actual_forCount;
												}
												stack.push("for");
												matchFound = true;
												index_count = 0;
												break;
											case 5: // if
												sourceCode += strline.replaceAll(regex_string, matcher.group()+"<if<"+if_counter++  + "<").trim()+"\n";
												stack.push("if");
												matchFound = true;
												index_count = 0;
												break;
											case 6: // }
												if (stack.peek().equals("for")){
													if(open_forCount >= 1){
														sourceCode += strline.replaceAll(regex_string, matcher.group()+">nested_for>"+ --nestedFor_counter + ">").trim()+"\n";
														--open_forCount;
													}
													else{
														sourceCode += strline.replaceAll(regex_string, matcher.group()+">for>"+ --for_counter + ">").trim()+"\n";
													}
												}else if(stack.peek().equals("if")){
													sourceCode += strline.replaceAll(regex_string, matcher.group()+">if>"+ --if_counter + ">").trim()+"\n";
												}else if(stack.peek().equals("main")) {
													sourceCode += strline.replaceAll(regex_string, matcher.group()+">main>").trim()+"\n";
													for_counter = 1;
													if_counter = 1;
												}
												stack.poll();
												matchFound = true;
												index_count = 0;
												break;
												
											case 7: // return
												sourceCode += strline.replaceAll(regex_string, matcher.group()+"*remove*").trim()+"\n";
												matchFound = true;
												index_count = 0;
												break;
											case 8: // return
												sourceCode += strline.replaceAll(regex_string, matcher.group()+"<increment>").trim()+"\n";
												matchFound = true;
												index_count = 0;
												break;
											case 9: // return
												sourceCode += strline.replaceAll(regex_string, matcher.group()+"<decrement>").trim()+"\n";
												matchFound = true;
												index_count = 0;
												break;
													}
										}else if(index_count == 9){
											sourceCode += strline.trim() + "*unknown*\n";
											index_count = 0;
										}
										else{
											index_count++;
										}
									}else{
										break;
									}
								}
							}
							break;
						case 2:
							for (String strline : sourceCode.split("\\n+")){
								String[] tokens = strline.split("\\s+");
								for(String token : tokens){ 
									int count = 0;
									for (String keyword : keywords) {
										
										pattern = Pattern.compile(".*" + keyword + "\\(*");
										matcher = pattern.matcher(token);
										if (matcher.matches()){
											instate = true;
											currentKeyHandler = count;
											break;
										}else{
											count++;
										}
									}
									if(instate){
										switch(currentKeyHandler){
											case 0:
												keyword_int(token);
												break;
											case 1:
												keyword_for(token);
												break;
											case 2:
												keyword_if(token);
												break;
										}
									}
								}
								
								Enumeration<String> vars = var_dict.keys();
								while(vars.hasMoreElements()){
								
									String identifier = vars.nextElement();
									
									//Variable initialization 
									pattern = Pattern.compile(".*" + identifier + ".*=.*;");
									matcher = pattern.matcher(strline);
									if(matcher.matches()){
										String[] variables = strline.split("\\s+");
										for(String var : variables){
											keyword_int(var);
										}
										sourceCode = sourceCode.replace(matcher.group(), "");
									}
								}
							}
							sourceCode = sourceCode.replaceAll(".*\\*remove\\*[\n]", "");
							sourceCode = sourceCode.replaceAll(".*<main<[\n]", "");
							sourceCode = sourceCode.replaceAll(".*>main>[\n]", "");
							break;
							
						case 3:
							try {
								// Variables Declaration 
								for(String lines: at.getTemplate_A()){
									txtAreaAsm.append(lines.trim()+"\n");
								}
								
								//identifier = 
								int picMemorySpace = 32;
								identifiers0 = var_dict.keys();
								while (identifiers0.hasMoreElements()){
									String varName = identifiers0.nextElement().toString();
									
									if (picMemorySpace < 496){
										if (picMemorySpace > 124 && picMemorySpace < 240){
											if(picMemorySpace == 125)
												picMemorySpace = 160;
											at.storeVarInBAnk(varName, 1); 
										}	
										else if(picMemorySpace > 239 && picMemorySpace < 368){
											if(picMemorySpace == 240)
												picMemorySpace = 272;
											at.storeVarInBAnk(varName, 2);
										}
										else if(picMemorySpace >= 368) {
											if(picMemorySpace == 368)
												picMemorySpace = 400;
											at.storeVarInBAnk(varName, 3); 
										}
										else
											at.storeVarInBAnk(varName, 0); 
											txtAreaAsm.append(varName + "\tEQU\t0x" + Integer.toHexString(picMemorySpace++).toUpperCase()  +"\n");
									}
									else {
										txtAreaAsm.append(identifiers0.nextElement() + "\tEQU\t\t" + "; No space available for veriable declation \n");
									}
									
								}
							}catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}	
							
							// Main Template And initialization of variables
							try {
								for(String lines: at.getTemplate_B()){
									txtAreaAsm.append(lines.trim()+"\n");
								}
							identifiers0 = var_dict.keys();
 							while (identifiers0.hasMoreElements()){
 								String identifier = identifiers0.nextElement();
 								String value = var_dict.get(identifier);
 								
 								txtAreaAsm.append(at.intitialise_int(identifier, value));
							}
 							// MAIN function Scan and translation 
 								for(String strLine: sourceCode.split("[\n]")){
 									if(!(strLine.matches("<main<"))){
 										if(strLine.matches(".*<for<\\d+<") || strLine.matches(".*<nested_for<\\d+<")){
 											String forNr = strLine.replaceAll(".*\\{", ""); 
 											txtAreaAsm.append(mf.getStatement(forNr,forStatement_dict.get(forNr)) + "\n");
 										}else if(strLine.matches(".*}>for>\\d+>") || strLine.matches(".*}>nested_for>\\d+>")){
 	 										String forNr = strLine.replaceAll(".*}", "");
 	 										forNr = forNr.replaceAll(">", "<");
 	 										txtAreaAsm.append(mf.getEndForStatement(forStatement_dict.get(forNr),forNr) + "\n");
 										}else if(strLine.matches(".*<if<\\d+<")){
 											String ifNr = strLine.replaceAll(".*\\{", ""); 
 											txtAreaAsm.append(mf.getStatement(ifNr));
 										}else if(strLine.matches(".*}>if>\\d+>")){
 											txtAreaAsm.append(mf.getEndIStatement());
 										}
 										else if(strLine.matches(".*\\<increment\\>") || strLine.matches(".*\\<decrement\\>")){
 											strLine = strLine.replaceAll("\\<increment\\>", "");
 											strLine = strLine.replaceAll("\\<decrement\\>", "");
 											strLine = strLine.replaceAll(";", "");
 											Enumeration<String> vars = var_dict.keys();
 											while(vars.hasMoreElements()){
 											
 												String identifier = vars.nextElement();
 												
 												if(strLine.matches("\\s*[\\+{2}]*" + identifier + "\\s*[\\+{2}]*")){
 													strLine = "";
 													txtAreaAsm.append(at.writeIncOperation(true,identifier));
 												}
 												else if(strLine.matches("\\s*[\\-{2}]*" + identifier + "\\s*[\\-{2}]*")){
 													strLine = "";
 													txtAreaAsm.append(at.writeIncOperation(true,identifier));
 												}
 											}
 										}
 										else if(strLine.matches(".*\\*unknown\\*")){
											txtAreaAsm.append("nop\t;This C code couldn't be resloved \""+strLine+"\"");
										}
 										
 									}
 								}
 								txtAreaAsm.append("\t" + "END"+"\n\n");
 								
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							break;
						}
					}
				}

			private void keyword_int(String token) {
				intStatement += token;				
				
				if(token.matches(".*\\;.*")){
					
					intStatement = intStatement.replaceAll("int","");
					intStatement = intStatement.replaceAll(";\\*remove\\*","");
					intStatement = intStatement.replaceAll(";","");
					
					if (intStatement.contains("=")){
							String intVars [] = intStatement.split("=");
							if(Integer.parseInt(intVars[1]) > 256)
								intVars[1] = "255";
							var_dict.put(intVars[0], intVars[1]);	
					}
					else{
						if(intStatement.contains(",")){
							String intVars [] = intStatement.split(",");
							for(String var : intVars){
								var_dict.put(var, "0");
							}
						}else {
							var_dict.put(intStatement.trim(), "0");
						}
					}
					
					instate = false;
					intStatement = "";
				}
				
			}
			
			private void keyword_for(String token) {
				forStatement += token;
				
				if(token.matches(".*\\{<for<.+<") || token.matches(".*\\{<nested_for<.+<")){
					forStatement = forStatement.replaceAll("for\\(", "");
					String forNr = forStatement.replaceAll(".*\\{","");
					
					forStatement = forStatement.replaceAll("\\).*<for<.*", "");
					forStatement = forStatement.replaceAll("\\).*<nested_for<.*", "");
					
					String forCondition [] = forStatement.split(";");
					keyword_int(forCondition[0].trim().concat(";"));
					mf.storeOperationType(forNr,forCondition[1].trim());
					
					forStatement_dict.put(forNr, forCondition[2]);
					
					forStatement = "";
					instate = false;
				}
				
			}
	
			private void keyword_if(String token){
				ifStatement += token;
				
				if(token.matches(".*\\{<if<.+<")){
					ifStatement = ifStatement.replaceAll("if\\(", "");
					String ifNr = ifStatement.replaceAll(".*\\{","");					
					
					ifStatement = ifStatement.replaceAll("\\).*<if<.*", "");
					mf.storeOperationType(ifNr, ifStatement);
					
					ifStatement ="";
					instate = false;
				}
			}
			
		});

		btnToAsm.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				if (txtAreaC.getText().isEmpty())
					btnToAsm.setEnabled(false);
				else
					btnToAsm.setEnabled(true);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	
		btnToC.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				if (txtAreaAsm.getText().isEmpty())
					btnToC.setEnabled(false);
				else
					btnToC.setEnabled(true);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
		});
	}

	public static void main (String args[]){
		new Gui(); 
	}
	
	
	private void openButtenEvent(String fileType) {
		//TODO Clean up this method
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter(fileType.toUpperCase() + " Program - (*." + fileType + ")" , fileType, Constants.ASM);
		fc.setFileFilter(filter);
		//TODO For testing so remove after !!!//////////////////////////////////////////////
		fc.setCurrentDirectory( new File("/Users/norisnyamekye/Desktop/Uni Project/test"));
		////////////////////////////////////////////////////////////////////////////////////
		fc.showOpenDialog(null);
			
		try {
				
			if(!(fc.getSelectedFile()==null)){
				if (fileType == Constants.C){
					txtAreaC.setText("");
					btnToC.setEnabled(false);
					btnToAsm.setEnabled(true);
					btnOpenAsm.setEnabled(false);
				}
				else{
					txtAreaAsm.setText("");
					btnToAsm.setEnabled(false);
					btnToC.setEnabled(true);
					btnOpenC.setEnabled(false);
				}
				
				String[] strLines = mf.readFile(fc.getSelectedFile());
				
				for(String strLine : strLines){

					if(fileType.equals(Constants.C))
						txtAreaC.append(strLine + "\n");
					else
						txtAreaAsm.append(strLine + "\n");
				}
				
			}
			
		}catch (Exception ex){//Catch exception if any
				ex.printStackTrace();
		}
	}
	
	private void saveButtonEvent(String fileType){
		// Improve // Replace a existen file and etc.
			
		FileNameExtensionFilter filter = new FileNameExtensionFilter(Constants.ASM_full  + " Program - (*." + fileType + ")" , fileType, Constants.ASM);
		FileNameExtensionFilter filter2 = new FileNameExtensionFilter(fileType.toUpperCase() + " Program - (*." + fileType + ")" , fileType, Constants.ASM);
			
		if(fileType.equals(Constants.ASM))
			fc.setFileFilter(filter);
		else{
			fc.setFileFilter(filter2);
		}
		int status = fc.showSaveDialog(null);
		
		try	{
			File new_file = null;
			if(!fc.getSelectedFile().getAbsoluteFile().toString().toLowerCase().endsWith("."+ fileType))
			{
			    new_file = new File(fc.getSelectedFile().getAbsoluteFile().toString() + "." + fileType);
			}
			
			if (status == JFileChooser.APPROVE_OPTION) {
				OutputStream out = new FileOutputStream(new_file);
				if (fileType.equals(Constants.C)){
					out.write(txtAreaC.getText().getBytes());	
				}
				else {
					out.write(txtAreaAsm.getText().getBytes());
				}
				out.close();
				
			} 
				
		}catch (Exception ex){//Catch exception if any
			ex.printStackTrace();
		}
	}

	void initialise()
	{	
		setTitle("C Translator");
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		
		// Intialisation of the GUI
		
		txtAreaAsm.setColumns(20);
		txtAreaAsm.setRows(5);
		scrollPanelAsm.setViewportView(txtAreaAsm);
		
		txtAreaC.setColumns(20);
		txtAreaC.setRows(5);
		scrollPanelC.setViewportView(txtAreaC);

		// Component Positioning <-- This code was Generated by Netbeans GUI Drag & Drop -->
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
						.addGap(161, 161, 161)
		                .addComponent(lblC)
		                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                .addComponent(lblAsm)
		                .addGap(130, 130, 130))
		                
		        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
		        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        				.addGroup(layout.createSequentialGroup()
		        						.addGap(25, 25, 25)
		    	                        .addComponent(scrollPanelC, GroupLayout.PREFERRED_SIZE, 325, GroupLayout.PREFERRED_SIZE)
		    	                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		    	                        		.addGroup(layout.createSequentialGroup()
		    	    	                                .addGap(20, 20, 20)
		    	    	                                .addComponent(btnToAsm, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
		    	    	                                .addGap(18, 19, Short.MAX_VALUE))
		    	    	                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
		    	    	                        		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		    	    	                                .addComponent(btnToC, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
		    	    	                                .addGap(18, 18, 18))))
		        				.addGroup(layout.createSequentialGroup()
		        						.addGap(43, 43, 43)
		    	                        .addComponent(btnOpenC, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
		    	                        .addGap(18, 18, 18)
		    	                        .addComponent(btnSaveC, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
		    	                        .addGap(18, 18, 18)
		    	                        .addComponent(btnClearC, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
		    	                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
		    	                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		    	                    .addGroup(layout.createSequentialGroup()
		    	                        .addComponent(scrollPanelAsm, GroupLayout.PREFERRED_SIZE, 325, GroupLayout.PREFERRED_SIZE)
		    	                        .addGap(25, 25, 25))
		    	                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
		    	                        .addComponent(btnOpenAsm, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
		    	                        .addGap(18, 18, 18)
		    	                        .addComponent(btnSaveAsm, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
		    	                        .addGap(18, 18, 18)
		    	                        .addComponent(btnClearAsm, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
		    	                        .addGap(50, 50, 50))))
		        				
		        				);
		
	        layout.setVerticalGroup(
	        		layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        		.addGroup(layout.createSequentialGroup()
	        				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        						.addGroup(layout.createSequentialGroup()
	        								.addGap(206, 206, 206)
	        								.addComponent(btnToAsm)
	        								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	        								.addComponent(btnToC))
	        						.addGroup(layout.createSequentialGroup()
	        								.addGap(20, 20, 20)
	        								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
	        										.addComponent(scrollPanelAsm, GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
	        										.addComponent(scrollPanelC))))
	        				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	        				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
	        						.addComponent(lblAsm, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	        						.addComponent(lblC, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	        				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	        						.addComponent(btnOpenAsm)
	        						.addComponent(btnSaveAsm)
	        						.addComponent(btnClearAsm)
	        						.addComponent(btnOpenC)
	        						.addComponent(btnSaveC)
	        						.addComponent(btnClearC))
	        				.addContainerGap(23, Short.MAX_VALUE))
	        		);
	        pack();
	        setVisible(true);
	}
	
	void showError(String err) {
		JOptionPane.showMessageDialog(new Frame(),
			    err,
			    "Exception Error",
			    JOptionPane.ERROR_MESSAGE);
	}
}
