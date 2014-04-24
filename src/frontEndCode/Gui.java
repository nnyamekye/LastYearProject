package frontEndCode;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
	GroupLayout layout = new GroupLayout(getContentPane());
	
	magicFunctions mf = new magicFunctions();
	Dictionary<String, String> forStatement_dict = new Hashtable<String, String>();
	static Dictionary<String, String> var_dict = new Hashtable<String, String>();
	
	boolean instate;
	
	Pattern pattern;
	Matcher matcher;
	
	String sourceCode;
	String intStatement;
	String forStatement;
	String ifStatement;
	
	public static void main (String args[]){
		new Gui(); 
	}
	
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
				txtAreaC.setText("");
				btnToAsm.setEnabled(false);
			}
		});
		
		btnClearAsm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				txtAreaAsm.setText("");
				btnToC.setEnabled(false);
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

			@Override
			public void actionPerformed(ActionEvent e) {
				Enumeration<String> identifiers = var_dict.keys();
				Deque<String> stack = new ArrayDeque<String>();
				AsmTemplates at = new AsmTemplates();
				
				instate = false;
				
				sourceCode = "";
				intStatement = "";
				forStatement = "";
				ifStatement = "";
				
				int currentKeyHandler= 0;
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
						"\\s+",					// Whitespace
						"^\\s*int\\s+main.*\\{.*", 	// main
						"^\\s*for.*\\{.*",			// for
						"^\\s*if.*\\{.*",			// if
						"^\\s*.*\\}.*",				// }
						"^\\s*return.*\\;",			// return
						"\\s*+\\+{2}\\w+;|\\s*\\w+\\+{2}\\s*;\\s*", // increment
						"\\s*+\\-{2}\\w+;|\\s*\\w+\\-{2}\\s*;\\s*" // decrement
				};

				txtAreaAsm.setText("");	
				
				while (identifiers.hasMoreElements()){
					var_dict.remove(identifiers.nextElement().toString());
				}
				
				for (int i = 1 ; i <= 3; i++){
					switch(i){ 
						case 1:
							int index_count = 0;
							
							for (String strLine : txtAreaC.getText().split("\\n+")){	
								boolean matchFound = false;
								for (String regex_string : patterns_array){
									pattern = Pattern.compile(regex_string);
									matcher = pattern.matcher(strLine);
									if(!matchFound){
										if(matcher.matches()){
											switch(index_count){
											case 0: // #include
												sourceCode += strLine.replaceAll(regex_string, matcher.group()+"*remove*").trim()+"\n";
												matchFound = true;
												index_count = 0;
												break;
											case 1: // int decleration
												sourceCode += strLine.replaceAll(regex_string, matcher.group()+"<int>").trim()+"\n";
												matchFound = true;
												index_count = 0;
												break;
											case 2: // whitespace
												sourceCode += strLine.replaceAll(regex_string,"").trim();
												matchFound = true;
												index_count = 0;
												break;
											case 3: // main
												sourceCode += strLine.replaceFirst(regex_string, "<main<").trim() +"\n";
												stack.push("main");
												matchFound = true;
												index_count = 0;
												break;
											case 4: // for
												if(stack.contains("for")){
													sourceCode += strLine.replaceAll(regex_string, matcher.group()+"<nested_for<"+ nestedForIncrement++  + "<").trim()+"\n";
													nestedFor_counter = nestedForIncrement;
													open_forCount++;
												}
												else{
													sourceCode += strLine.replaceAll(regex_string, matcher.group()+"<for<"+ actual_forCount++  + "<").trim()+"\n";
												 	for_counter = actual_forCount;
												}
												stack.push("for");
												matchFound = true;
												index_count = 0;
												break;
											case 5: // if
												sourceCode += strLine.replaceAll(regex_string, matcher.group()+"<if<"+if_counter++  + "<").trim()+"\n";
												stack.push("if");
												matchFound = true;
												index_count = 0;
												break;
											case 6: // }
												if (stack.peek().equals("for")){
													if(open_forCount >= 1){
														sourceCode += strLine.replaceAll(regex_string, matcher.group().trim()+">nested_for>"+ --nestedFor_counter + ">").trim()+"\n";
														--open_forCount;
													}
													else{
														sourceCode += strLine.replaceAll(regex_string, matcher.group().trim()+">for>"+ --for_counter + ">").trim()+"\n";
													}
												}else if(stack.peek().equals("if")){
													sourceCode += strLine.replaceAll(regex_string, matcher.group().trim()+">if>"+ --if_counter + ">").trim()+"\n";
												}else if(stack.peek().equals("main")) {
													sourceCode += strLine.replaceAll(regex_string, matcher.group().trim()+">main>").trim()+"\n";
													for_counter = 1;
													if_counter = 1;
												}
												stack.poll();
												matchFound = true;
												index_count = 0;
												break;
												
											case 7: // return
												sourceCode += strLine.replaceAll(regex_string, matcher.group()+"*remove*").trim()+"\n";
												matchFound = true;
												index_count = 0;
												break;
											case 8: // return
												sourceCode += strLine.trim().replaceAll(regex_string, matcher.group()+"<increment>").trim()+"\n";
												matchFound = true;
												index_count = 0;
												break;
											case 9: // return
												sourceCode += strLine.trim().replaceAll(regex_string, matcher.group()+"<decrement>").trim()+"\n";
												matchFound = true;
												index_count = 0;
												break;
													}
										}else if(index_count == 9){
											sourceCode += strLine.trim() + "*unknown*\n";
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
							}
							sourceCode = sourceCode.replaceAll(".*\\*remove\\*[\n]", "");
							sourceCode = sourceCode.replaceAll(".*<main<[\n]", "");
							sourceCode = sourceCode.replaceAll(".*>main>[\n]", "");
							break;
							
						case 3:
							try {
								
								for(String lines: at.getTemplate_A()){
									txtAreaAsm.append(lines.trim()+"\n");
								}
								
								// Variables Declaration 
								int picMemoryLocation = 32;
								identifiers = var_dict.keys();
								while (identifiers.hasMoreElements()){
									String varName = identifiers.nextElement().toString();
									
									if (picMemoryLocation < 496){
										if (picMemoryLocation > 124 && picMemoryLocation < 240){
											if(picMemoryLocation == 125)
												picMemoryLocation = 160;
											at.storeVarInBAnk(varName, 1); 
										}	
										else if(picMemoryLocation > 239 && picMemoryLocation < 368){
											if(picMemoryLocation == 240)
												picMemoryLocation = 272;
											at.storeVarInBAnk(varName, 2);
										}
										else if(picMemoryLocation >= 368) {
											if(picMemoryLocation == 368)
												picMemoryLocation = 400;
											at.storeVarInBAnk(varName, 3); 
										}
										else
											at.storeVarInBAnk(varName, 0); 
											txtAreaAsm.append(varName + "\tEQU\t0x" + Integer.toHexString(picMemoryLocation++).toUpperCase()  +"\n");
									}
									else {
										JOptionPane.showMessageDialog(new Frame(), "The PIC only provide 400 General Purpose"
												+ "Registers!\n You have exeeded your limit by: " + (picMemoryLocation - 496)
													, "Message",JOptionPane.ERROR_MESSAGE);
										break;
									}
									
								}
							}catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}	
							
							// PArt B of Code
							try {
								for(String lines: at.getTemplate_B()){
									txtAreaAsm.append(lines.trim()+"\n");
								}

							// MAIN function Scan and translation 
 								for(String strLine: sourceCode.split("[\n]")){
 									if(!(strLine.matches("<main<"))){
 										if(strLine.trim().matches(".*<int>")){
 											strLine = strLine.replaceAll(";<int>", "").trim();
 											strLine = strLine.replaceAll("int", "").trim();
 											txtAreaAsm.append(mf.getAssignement(strLine));
 										}else if(strLine.matches(".*<for<\\d+<") || strLine.matches(".*<nested_for<\\d+<")){
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
 											boolean match = false;
 											while(vars.hasMoreElements()){
 											
 												String identifier = vars.nextElement();
 												
 												if(strLine.matches("\\s*[\\+{2}]*" + identifier + "\\s*[\\+{2}]*")){
 													strLine = "";
 													txtAreaAsm.append(at.writeIncOperation(true,identifier));
 													match = true;
 												}
 												else if(strLine.matches("\\s*[\\-{2}]*" + identifier + "\\s*[\\-{2}]*")){
 													strLine = "";
 													txtAreaAsm.append(at.writeIncOperation(false,identifier));
 													match = true;
 												}
 											}
 											if(!match){
 												txtAreaAsm.append("\nnop\t; \" " + strLine + " \" veriable is not declared\n");
 												match = false;
	 	 									}
 										}	
 										else if(strLine.matches(".*\\*unknown\\*")){
 											boolean match = false;
 											Enumeration<String> vars = var_dict.keys();
 											strLine = strLine.replaceAll("\\*unknown\\*", "");
 											while(vars.hasMoreElements()){
 											
 												String identifier = vars.nextElement();
 												if (strLine.trim().matches(identifier + "\\s*=\\s*\\d+;")){
 													txtAreaAsm.append(mf.getAssignement(strLine.trim()));
 													match=true;
 												}else if(!strLine.trim().matches(identifier + "\\s*=\\s*\\D+;")) {
 													txtAreaAsm.append("\nnop\t; \" " + strLine + " \" veriable is not declared\n");
 													match=true;
 												}
 												if(match)
 													break;
 													
 											}
 											if(!match){
 												strLine=strLine.replaceAll("\\*unknown\\*","");
 												txtAreaAsm.append("nop\t; \" " + strLine + " \" couldn't be resloved\n");
 											}
										}
 									}
 								}
 								txtAreaAsm.append("\n\t" + "END"+"\n\n");
 								
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							break;
						}
					}
				}

			public void keyword_int(String token) {
				intStatement += token;				
				
				if(token.matches(".*\\;.*")){
					
					String value="";
					String identifier="";
					String intVars[] ={}; 
							
					intStatement = intStatement.replaceAll("int","");
					intStatement = intStatement.replaceAll(";<>","");
					intStatement = intStatement.replaceAll(";","");
					
					if(intStatement.contains(",")){
						
						if (intStatement.contains("=")){
							intVars = intStatement.split("=");
							if(Integer.parseInt(intVars[1]) > 256){
								intVars[1] = "255";
							}
							value = intVars[1].trim();
							
							intVars = intVars[0].split(",");
							for(String var : intVars){
								var_dict.put(var, value);
							}
						}else {
							intVars = intStatement.split(",");
							for(String var : intVars){
								var_dict.put(var, "0");
							}
						}
					}
					else if (intStatement.contains("=")){
						intVars = intStatement.split("=");
						if(Integer.parseInt(intVars[1]) > 256){
							intVars[1] = "255";
						}
						var_dict.put(intVars[0], intVars[1]);	
					}
					else{
						var_dict.put(intStatement, "0");							
					}
					
					instate = false;
					intStatement = "";
				}
				
			}
			
			void keyword_for(String token) {
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
	
			void keyword_if(String token){
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
		
		txtAreaAsm.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				if(txtAreaAsm.getText().matches("\\s*"))
					btnToC.setEnabled(false);
				else 
					btnToC.setEnabled(true);
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				
			}
		});
		
		txtAreaC.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				if(txtAreaC.getText().matches("\\s*"))
					btnToAsm.setEnabled(false);
				else 
					btnToAsm.setEnabled(true);
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
			}
		});
		
	}

	private void openButtenEvent(String fileType) {
		//filter 
		fc.resetChoosableFileFilters();
		fc.setFileFilter(new FileNameExtensionFilter(fileType.toUpperCase() + " Program - (*." + fileType + ")" , fileType));
		
		//TODO For testing so remove after !!!//////////////////////////////////////////////
		fc.setCurrentDirectory( new File("/Users/norisnyamekye/Desktop/Uni Project/test"));
		////////////////////////////////////////////////////////////////////////////////////
		int respond = fc.showOpenDialog(null);
			
		
				
			if(respond == JFileChooser.APPROVE_OPTION){
				try {
					String[] strLines = mf.readFile(fc.getSelectedFile());
					
					if(fileType.equals(Constants.C))
						txtAreaC.setText("");
					else
						txtAreaAsm.setText("");
					
				for(String strLine : strLines){

					if(fileType.equals(Constants.C))
						txtAreaC.append(strLine + "\n");
					else
						txtAreaAsm.append(strLine + "\n");
				}
				
				if(fileType.equals(Constants.C)){
					if(txtAreaC.getText().matches("\\s*")){
						btnToAsm.setEnabled(false);
					}
					else
						btnToAsm.setEnabled(true);
				}else{
					if(txtAreaAsm.getText().matches("\\s*")){
						btnToC.setEnabled(false);
					}
					else
						btnToC.setEnabled(true);
				}
					
				
				}catch (Exception ex){//Catch exception if any
					JOptionPane.showMessageDialog(new Frame(), fc.getSelectedFile().getName()+" couldn't be open!"
							, "Message",JOptionPane.ERROR_MESSAGE);
				}
			}
			
		
	}
	
	private void saveButtonEvent(String fileType){
		//TODO Improve code simiar to open & save shouldnt only be posible if file is openend or txtbox is not empmty is not that simple think
		fc.resetChoosableFileFilters();
		fc.setFileFilter(new FileNameExtensionFilter(fileType.toUpperCase() + " Program - (*." + fileType + ")" , fileType));;
			
		int respond = fc.showSaveDialog(null);
		
		try	{
			
			if (respond == JFileChooser.APPROVE_OPTION) {
				OutputStream out = null;
				File file = new File(fc.getSelectedFile().getAbsoluteFile().toString() + "." + fileType);
				try{
					if(file.exists()){
						int response = JOptionPane.showConfirmDialog(new Frame(), "Would you like to overwrite the existing file", "Warning", JOptionPane.YES_NO_OPTION); 
						if(response == JOptionPane.YES_OPTION){
							out = new FileOutputStream(file);
							
							if (fileType.equals(Constants.C)){
								out.write(txtAreaC.getText().getBytes());
								JOptionPane.showMessageDialog(new Frame(), file.getName()+" has been saved Successfully"
										, "Message",JOptionPane.INFORMATION_MESSAGE);
							}
							else {
								out.write(txtAreaAsm.getText().getBytes());
								JOptionPane.showMessageDialog(new Frame(), file.getName()+" has been saved Successfully"
										, "Message",JOptionPane.INFORMATION_MESSAGE);
							}
						}else{
							JOptionPane.showMessageDialog(new Frame(), file.getName()+" wasn't saved"
									, "Message",JOptionPane.INFORMATION_MESSAGE);
							
						}
					}else {
						file.createNewFile();
						out = new FileOutputStream(file);
						out.write(txtAreaC.getText().getBytes());
					}
					out.close();
				}catch (Exception e) {
					// TODO: handle exception
				}
				
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
		
		btnToAsm.setEnabled(false);
		btnToC.setEnabled(false);

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
