package chat;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;


public class Login extends JFrame {
	  JTextField useraccount = new JTextField(); 
	  JLabel useraccounttext = new JLabel();
	  JLabel userpwdtext = new JLabel();
	  JPasswordField userpwd = new JPasswordField();
	  JButton loginbtn = new JButton("Login");
	  JButton regbtn = new JButton("Register");
	  JButton exitbtn = new JButton("Quit");
	  JSeparator Line = new JSeparator();
	  
	  ImageIcon imageIcon;
	  
	  BufferedReader in;
	  PrintStream out;
	  Socket s;
	  String flag;
	  
	  public Login(String title) {
			// TODO Auto-generated constructor stub
			   super(title);
			    getContentPane().setLayout(null);
			    JLabel label = new JLabel();
			    
			    String picFile = "login.png"+File.separator;
			    imageIcon = new ImageIcon(new ImageIcon(picFile).getImage()
			    		.getScaledInstance(326, 166, Image.SCALE_DEFAULT));
			   label.setHorizontalAlignment(SwingConstants.CENTER);
			    
			   label.setIcon(imageIcon);
			   label.setHorizontalAlignment(SwingConstants.CENTER);
		       label.setBounds(0,0,326,85);
		       getContentPane().add(label);
		        
		        useraccounttext.setText("username：");
		        useraccounttext.setBounds(20,85,77,27);
		        getContentPane().add(useraccounttext);
		        
		        useraccount.setBounds(99, 85, 171, 22);
		        getContentPane().add(useraccount);
		        
		        userpwdtext.setText("password：");
		        userpwdtext.setBounds(20, 118, 77, 18);
		        getContentPane().add(userpwdtext);
		        
		        userpwd.setBounds(99,119,171,22);
		        getContentPane().add(userpwd);
		    
		    
		        loginbtn.setBounds(20, 150, 67, 22);
		        getContentPane().add(loginbtn);
		        
		        regbtn.setBounds(110, 150, 67, 22);
		        getContentPane().add(regbtn);
		        
		        exitbtn.setBounds(200, 150, 67, 22);
		        getContentPane().add(exitbtn);
		       
		      
			    setLocation(450, 230);
			    setSize(300, 220);
			    setResizable(false);
			    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			    setVisible(true);
			    exitbtn.addActionListener(new ActionListener(){
			    	public void actionPerformed(ActionEvent e){
			    		dispose();
			    	}
			  });
			   
			    regbtn.addActionListener(new ActionListener(){
			    	private Socket socket;
			    	public void actionPerformed(ActionEvent e){
			    		try{
			    			socket = new Socket("127.0.0.1",2000);
			    			in = new BufferedReader(new InputStreamReader(
								socket.getInputStream()));
			    			out = new PrintStream(socket.getOutputStream());
			    			Register rf = new Register(socket,"register") ;
			    		}catch(Exception e1)
			    		{
			    			e1.printStackTrace();
			    		}
			    	}
			  });
	      loginbtn.addActionListener(new LoginListener());
		}
	  
	  class LoginListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				String useracconutvalue = useraccount.getText();
				String userpwdvalue = userpwd.getText();
				
				
				if(e.getSource() == loginbtn){
					if(useracconutvalue.equals("")){
						JOptionPane.showMessageDialog(null, "Please include your account","",JOptionPane.ERROR_MESSAGE);
					    return;
					}
					
					if(userpwdvalue.equals("")){
						JOptionPane.showMessageDialog(null, "Please include your password","",JOptionPane.ERROR_MESSAGE);
					    return;
					}
					
					Socket socket = null;
					try{
						socket = new Socket("127.0.0.1",2000);
						in = new BufferedReader(new InputStreamReader(
								socket.getInputStream()));
						out = new PrintStream(socket.getOutputStream());
						out.println("0"+useracconutvalue+";"+userpwdvalue);
						new Thread(new LoginHelper(socket)).start();
					}catch(IOException e1){
						JOptionPane.showMessageDialog(null,"Server has not started.","",JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
			  class LoginHelper implements Runnable{
			    	private Socket socket;
					
					public LoginHelper(Socket socket){
						this.socket = socket;
					}
					public void run(){
						try{
							String login = in.readLine();
							System.out.println(login);
							while(login != null){
								String[] logintemp = login.split("#");
							    if(logintemp.length>2)
								{
									if(logintemp[0].equals("false"))
									{
										JOptionPane.showMessageDialog(null, "User is already online.","",JOptionPane.ERROR_MESSAGE);
									}
									else if(logintemp[1].equals("false")){
										JOptionPane.showMessageDialog(null, "Authorization failed. Please try again.","",JOptionPane.ERROR_MESSAGE);
									}
									else if(logintemp[0].equals("true")&&logintemp[1].equals("true")){
										ClientServer client = new ClientServer(socket,logintemp[2]);
										client.doConnect(socket);
										client.sendStartToServer();
										dispose();
										Thread.currentThread().stop();
										}
								}
								
								login = in.readLine();
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
	         }
	  }	
	  
		public static void main(String args[]){
		  Login lf = new Login("Login");
	  }
}