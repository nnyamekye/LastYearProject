package frontEndCode;

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
	
	int currentKeyHandler=0;
	
	boolean instate = false;
	boolean initialise = true;
	
	GroupLayout layout = new GroupLayout(getContentPane());
	
	magicFunctions mf = new magicFunctions();
	
	Dictionary<String, String> intVar_dict = new Hashtable<String, String>();
	Dictionary<String, String> forStatement_dict = new Hashtable<String, String>();
	
	File temporally_file;
	
	Pattern pattern;
	Matcher matcher;
	
	String sourceCode = "";
	String intStatement = "";
	String forStatement = "";
	
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

			@Override
			public void actionPerformed(ActionEvent e) {
				Enumeration<String> identifiers = intVar_dict.keys();
				Deque<String> stack = new ArrayDeque<String>();

				
				AsmTemplates at = new AsmTemplates();
			
				int for_counter = 1;
				int if_counter = 1;
				txtAreaAsm.setText("");	
				sourceCode="";
				
				identifiers = intVar_dict.keys();
					while (identifiers.hasMoreElements()){
						intVar_dict.remove(identifiers.nextElement().toString());
					}
					
				String[] keywords = {"int","for","if"};
				
				for (int i = 1 ; i <= 3; i++){
					switch(i){ 
						case 1:
							for (String strline : txtAreaC.getText().split("\\n+")){	
								
								
								pattern = Pattern.compile("^\\s*int\\s+main.*\\{.*");
								matcher = pattern.matcher(strline);
								
								if(matcher.matches()){
									strline = strline.replaceFirst("int\\s+main.*\\{", "<main<");
									stack.push("main");
								}
								
								// For Statement
								pattern = Pattern.compile("^\\s*for.*\\{.*");
								matcher = pattern.matcher(strline);
								
								if(matcher.matches()){
									strline = strline.replaceAll("^\\s*for.*\\{.*", matcher.group()+"<for<"+for_counter++  + "<");
									stack.push("for");
								}
								// if Statement
								pattern = Pattern.compile("^\\s*if.*\\{.*");
								matcher = pattern.matcher(strline);
								
								if(matcher.matches()){
									strline = strline.replaceAll("^\\s*if.*\\{.*", matcher.group()+"<if<"+if_counter++  + "<");
									stack.push("if");
								}
								
								// End bracket
								pattern = Pattern.compile("^\\s*.*\\}.*");
								matcher = pattern.matcher(strline);
								
								if(matcher.matches()){
									String stack_entity = stack.pop();
									
									if (stack_entity.equals("for")){
										strline = strline.replaceAll("^\\s*.*\\}.*", matcher.group()+">for>"+ --for_counter + ">");
									}else if(stack_entity.equals("if")){
										strline = strline.replaceAll("^\\s*.*\\}.*", matcher.group()+">if>"+ --if_counter + ">");
									}else if(stack_entity.equals("main")) {
										strline = strline.replaceAll("^\\s*.*\\}.*", matcher.group()+">main>");
										for_counter = 1;
										if_counter = 1;
									}
									stack_entity="";
								}
								
								// int Keyword
								pattern = Pattern.compile("^\\s*int.*\\;");
								matcher = pattern.matcher(strline);
								
								if(matcher.matches()){
									strline = strline.replaceAll("^\\s*int.*\\;", matcher.group()+"*remove*");
								}
								
								// return 
								pattern = Pattern.compile("^\\s*return.*\\;");
								matcher = pattern.matcher(strline);
								
								if(matcher.matches()){
									strline = strline.replaceAll("^\\s*return.*\\;", matcher.group()+"*remove*");
								}
								
								// #include 
								if(!(strline.trim().isEmpty())){
									if(!(strline.contains("#include")))
										sourceCode = sourceCode.concat(strline+"\n");
								}
								
							}
							System.out.println(sourceCode);
							break;
					
						case 2:
							int count = 0;
							for (String strline : sourceCode.split("\\n+")){
								String[] tokens = strline.split("\\s+");
								for(String token : tokens){ 
									count = 0;
									for (String keyword : keywords) {
										if (token.equals(keyword)){
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
												currentKeyHandler = 0;
												break;
										}
									}
								}
							}
							sourceCode = sourceCode.replaceAll(".*\\*remove\\*[\n]", "");
							break;
							
						case 3:
							try {
								// Variables Declaration 
								for(String lines: at.getTemplate_A()){
									txtAreaAsm.append(lines.trim()+"\n");
								}
								
								//identifier = 
								int picMemorySpace = 32;
								identifiers = intVar_dict.keys();
								while (identifiers.hasMoreElements()){
									
									if (picMemorySpace < 496){
										txtAreaAsm.append(identifiers.nextElement().toString() + "\tEQU\t0x" + Integer.toHexString(picMemorySpace++).toUpperCase()  +"\n");
										if(picMemorySpace == 125)
											picMemorySpace = 160;
										if(picMemorySpace == 240)
											picMemorySpace = 272;
										if(picMemorySpace == 368)
											picMemorySpace = 400;
									}
									else {
										txtAreaAsm.append(identifiers.nextElement() + "\tEQU\t\t" + "; No space available for veriable declation \n");
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
							identifiers = intVar_dict.keys();
 							while (identifiers.hasMoreElements()){
 								String identifier = identifiers.nextElement();
 								String value = intVar_dict.get(identifier);
 								
 								txtAreaAsm.append(at.intitialise_int(identifier, value));
							}
 							// MAIN function Scan and translation 
 								for(String strLine: sourceCode.split("[\n]")){
 									if(!(strLine.matches("<main<"))){
 										if(strLine.matches(".*<for<\\d+<")){
 											String forNr = strLine.replaceAll(".*\\{", ""); 
 											txtAreaAsm.append(mf.getStatement(forNr,forStatement_dict.get(forNr)) + "\n");
 										}else if(strLine.matches(".*}>for>\\d+>")){
 	 										String forNr = strLine.replaceAll(".*}", "");
 	 										forNr = forNr.replaceAll(">", "<");
 	 										txtAreaAsm.append(mf.getEndStatement(forStatement_dict.get(forNr),forNr) + "\n");	
 										}else {
 											strLine = strLine.trim();
 											strLine = strLine.replaceAll("}>main>", "END");
											txtAreaAsm.append("\t" + strLine+"\n\n");
										}
 									}
 								}
 								
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
							intVar_dict.put(intVars[0], intVars[1]);	
					}
					else{
						if(intStatement.contains(",")){
							String intVars [] = intStatement.split(",");
							for(String var : intVars){
								intVar_dict.put(var, "0");
							}
						}else {
							intVar_dict.put(intStatement.trim(), "0");
						}
					}
					
					instate = false;
					intStatement = "";
				}
				
			}
			
			private void keyword_for(String token) {
				forStatement += token;
				
				if(token.matches(".*\\{<for<.+<")){
					forStatement = forStatement.replaceAll("for\\(", "");
					String forNr = forStatement.replaceAll(".*\\{","");
					
					forStatement = forStatement.replaceAll("\\).*<for<.*", "");
					
					String forCondition [] = forStatement.split(";");
					keyword_int(forCondition[0].trim().concat(";"));
					mf.storeOperationType(forNr,forCondition[1].trim());
					
					forStatement_dict.put(forNr, forCondition[2]);
					
					forStatement = "";
					instate = false;
				}
				
			}
		});
	
		txtAreaC.addMouseListener(new MouseListener()  {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
		/*		if(!(txtAreaC.getText().isEmpty())){
					dialogEdit.add(scrollPanelEdit);
					scrollPanelEdit.setViewportView(txtAreaEdit);
				
					txtAreaEdit.setText("");
					txtAreaEdit.setText(txtAreaC.getText());
					dialogEdit.setSize(txtAreaEdit.getPreferredSize());
				
					dialogEdit.setVisible(true);
				}*/
			}
		});	
	
		txtAreaAsm.addMouseListener(new MouseListener()  {
		
		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseClicked(MouseEvent arg0) {
			/*if(!(txtAreaAsm.getText().isEmpty())){
				dialogEdit.add(scrollPanelEdit);
				scrollPanelEdit.setViewportView(txtAreaEdit);
			
				txtAreaEdit.setText("");
				txtAreaEdit.setText(txtAreaAsm.getText());
				dialogEdit.setSize(txtAreaEdit.getPreferredSize());
			
				dialogEdit.setVisible(true);
			}*/
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
				// TODO Auto-generated method stub
				
				
			}
		});
	}
	
	// Methods
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
}
