package chat;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

//import chat.ClientServer1.MessageReader;
import chat.Login.LoginListener;
import chat.Login.LoginListener.LoginHelper;
public class Register extends JFrame {
	  JTextField useraccount = new JTextField(); 
	  JLabel useraccounttext = new JLabel();
	  JLabel userpwdtext = new JLabel();
	  JLabel userpwdconftext = new JLabel();
	  JPasswordField userpwd = new JPasswordField();
	  JPasswordField userpwdconf = new JPasswordField();
	  Socket socket;
	  JButton regbtn = new JButton("Register");
	  JButton exitbtn = new JButton("Quit");
	  ImageIcon imageIcon;
	  
	  BufferedReader in=null;
      PrintStream out=null;
	  String flag;
	  
	  class RegListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				String useraccountvalue = useraccount.getText();
				String userpwdvalue = userpwd.getText();
				String confirmvalue = userpwdconf.getText();
				
				
				if(e.getSource() == regbtn){
					if(useraccountvalue.equals("")){
						JOptionPane.showMessageDialog(null, "","",JOptionPane.ERROR_MESSAGE);
					    return;
					}
					
					if(userpwdvalue.equals("")){
						JOptionPane.showMessageDialog(null, "Please include your password.","",JOptionPane.ERROR_MESSAGE);
					    return;
					}
					
					if(confirmvalue.equals("")){
						JOptionPane.showMessageDialog(null, "Please confirm your password.","",JOptionPane.ERROR_MESSAGE);
					    return;
					}
					
					if(!(userpwdvalue.equals(confirmvalue))){
						JOptionPane.showMessageDialog(null, "Password not match.","",JOptionPane.ERROR_MESSAGE);
					    return;
					}
				
					Socket socket = null;
					try{
						socket = new Socket("127.0.0.1",2000);
						in = new BufferedReader(new InputStreamReader(
								socket.getInputStream()));
						out = new PrintStream(socket.getOutputStream());
						out.println("r"+useraccountvalue+";"+userpwdvalue);
						new Thread(new RegisterHelper(socket)).start();
					}catch(IOException e1){
						JOptionPane.showMessageDialog(null,"Server has not started.","",JOptionPane.INFORMATION_MESSAGE);
					}
				}	
			}
			  public void doConnect(Socket socket){
				  try{
					  in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					  out = new PrintStream(socket.getOutputStream());
					  new Thread(new RegisterHelper(socket)).start(); 
				  }catch(Exception e){
					  System.out.println("Cannot connect to the server！");
					  e.printStackTrace();
				  }
			  }
			  class RegisterHelper implements Runnable{
			    	private Socket socket;
			    	String useracconutvalue = useraccount.getText();
					String userpwdvalue = userpwd.getText();
					
					public RegisterHelper(Socket socket){
						this.socket = socket;
					}
					public void run(){
						try{
							String register = in.readLine();
							System.out.println(register);
							while(register != null){
								if(register.equals("true")){
									JOptionPane.showMessageDialog(null, "You have register a new account！","",JOptionPane.ERROR_MESSAGE);
									dispose();
									Thread.currentThread().stop();
								}
								else if(register.equals("false")){
									JOptionPane.showMessageDialog(null, "Username already exists！","",JOptionPane.ERROR_MESSAGE);
								}
								register = in.readLine();
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
			  }
			
		}
	  
	  public Register(Socket socket,String title) {
		    super(title);
		    getContentPane().setLayout(null);
		    JLabel label = new JLabel();
		    this.socket = socket;
		    String picFile = "login.png"+File.separator;
		    imageIcon = new ImageIcon(new ImageIcon(picFile).getImage()
		    		.getScaledInstance(326, 166, Image.SCALE_DEFAULT));
		   label.setHorizontalAlignment(SwingConstants.CENTER);
		    
		   label.setIcon(imageIcon);
		   label.setHorizontalAlignment(SwingConstants.CENTER);
	       label.setBounds(0,0,326,85);
	       getContentPane().add(label);
	        
	        useraccounttext.setText("username：");
	        useraccounttext.setBounds(35,82,74,32);
	        getContentPane().add(useraccounttext);
	        
	        useraccount.setBounds(116, 87, 160, 22);
	        getContentPane().add(useraccount);
	        
	        userpwdtext.setText("password：");
	        userpwdtext.setBounds(35, 118, 86, 22);
	        getContentPane().add(userpwdtext);
	        
	        userpwd.setBounds(116,118,160,22);
	        getContentPane().add(userpwd);
	        
	        userpwdconftext.setText("confirm：");
	        userpwdconftext.setBounds(35, 149, 74, 25);
	        getContentPane().add(userpwdconftext);
	        
	        userpwdconf.setBounds(116,150,160,22);
	        getContentPane().add(userpwdconf);
	    
	        regbtn.setBounds(60, 190, 86, 22);
	        getContentPane().add(regbtn);
	        
	        exitbtn.setBounds(169, 190, 86, 22);
	        getContentPane().add(exitbtn);
	       
	      
		    setLocation(450, 230);
		    setSize(326, 260);
		    setResizable(false);
		    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    setVisible(true);
		    
		    exitbtn.addActionListener(new ActionListener(){
		    	public void actionPerformed(ActionEvent e){
		    		dispose();
		    	}
		    });
		   regbtn.addActionListener(new RegListener());
		  }
}