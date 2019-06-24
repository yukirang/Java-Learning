package chat;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;


public class Server extends JFrame{
	private  ArrayList<ClientConnection> connectionsList = 
			new ArrayList<ClientConnection>();
	private  ArrayList<String> onList = 
			new ArrayList<String>();
	  JTextArea output = new JTextArea("message\n"); 
	  JScrollPane pane3 = new JScrollPane(output); // upper panel
	  DefaultListModel dlmName;
	  JList online ;
	  JPanel jpWest = new JPanel();
	  ImageIcon imageIcon;
	  
	  public Server(String title) {
		    super(title);
		    
		    output.setFont(new Font("Serif", Font.PLAIN , 16));
		    output.setEditable(false);
		    dlmName = new DefaultListModel();
		    dlmName.addElement("online users");
		    online = new JList(dlmName);
		    add(pane3, BorderLayout.CENTER);
		    add(jpWest,BorderLayout.WEST);
		    
		    output.setFont(new Font("Serif", Font.PLAIN , 18));
		    output.setAutoscrolls(true); 
		   pane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		   pane3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		   JLabel label = new JLabel();
		    String picFile = "server.jpg"+File.separator;
		    imageIcon = new ImageIcon(new ImageIcon(picFile).getImage()
		    		.getScaledInstance(85, 450, Image.SCALE_DEFAULT));
		   label.setHorizontalAlignment(SwingConstants.CENTER);
		    
		   label.setIcon(imageIcon);
	       label.setBounds(0,0,85,450);
	       getContentPane().add(label,BorderLayout.EAST);
		   jpWest.add(online);
		    
		    setLocation(50, 150);
		    setSize(550, 450);
		    setResizable(false);
		    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    setVisible(true);
		  }  
	  
	  
	public void startServer(){
		ServerSocket serverSocket = null;
		Socket socket;
	    
	
		try{
			serverSocket = new ServerSocket(2000);
		}catch(IOException e){
			e.printStackTrace();
			System.exit(-1);
		}
		String input;
		while(true){
			try{
				socket = serverSocket.accept();
		    	hadleClientConnection(socket);
			}catch(Exception e){
				e.printStackTrace();
			}	
		}
		
    }
	public void hadleClientConnection(Socket socket){
		
		ClientConnection con = new ClientConnection(socket);
	    connectionsList.add(con);
		Thread t = new Thread(con);
		t.start();
	}
	public void sendToAllClients(String message){
		if(message.startsWith("4"))
		{
			output.append("\n"+getTime()+"\n"+message.substring(1)+" is online"); 
			String on = message;
			for (String s : onList)
			{
				on += ("&"+s);
			}
			for(ClientConnection con :connectionsList){
				con.sendMessageToClient(on);
			}
		}
		else if(message.startsWith("3"))
		{
			output.append("\n"+getTime()+"\n"+message.substring(1)+" is offline");
			for(ClientConnection con :connectionsList){
				con.sendMessageToClient(message);
				}
		}
		else if(message.startsWith("1")){
			for(ClientConnection con:connectionsList){
				con.sendMessageToClient(message);
			}
			output.append("\n"+getTime()+"\n"+message.substring(1));
		}
		else if(message.startsWith("2")){
			String[] temp = message.split("#");
			output.append("\n"+getTime());
			output.append("\n"+temp[2]+" to "+temp[1]+":");
			output.append(temp[3]);
			for(ClientConnection con:connectionsList){
				con.sendMessageToClient(message);
			}
		}
	}
	class ClientConnection implements Runnable{
		Socket socket = null;
		String useraccount = null;
		String userpwd = null;
		public PrintStream out = null;
		public BufferedReader in = null;
		public void run(){
			OutputStream socketOutput = null;
			InputStream socketInput = null;
			try{
				socketOutput = socket.getOutputStream();
				out = new PrintStream(socketOutput);
				socketInput = socket.getInputStream();
				in = new BufferedReader(new InputStreamReader(socketInput));
				String input = in.readLine();
				while(true){
					if(input!=null && input.startsWith("0")){
						String[] temp = input.split(";");
						useraccount = temp[0].substring(1);
						userpwd = temp[1];
						UserDAO dao1 = new UserDAO();
						boolean flag = dao1.login(useraccount, userpwd);
						boolean notonflag = true;
						for(String s:onList)
						{
							if(s.equals(useraccount))
							{
								notonflag = false;
								break;
							}
						}
						boolean f = (flag&&notonflag);
						if(f == true )
						{
							dlmName.addElement(useraccount);
							onList.add(useraccount);
							sendToAllClients("0"+useraccount);
							out.print(new Boolean(notonflag).toString()+"#");
							out.print(new Boolean(flag)+"#");
							out.println(useraccount);
							out.flush();
						}
						else{
							out.print(new Boolean(notonflag).toString()+"#");
							out.print(new Boolean(flag).toString()+"#");
							out.println(useraccount);
							out.flush();
						}
					}
					if(input != null && input.startsWith("1")){
						sendToAllClients(input);
					}
					else if(input != null && input.startsWith("2")){
						sendToAllClients(input);
					}
					else if(input != null&&input.startsWith("3"))
					{
						String[] temp = input.split("#");
						useraccount = temp[1];
						dlmName.removeElement(useraccount);
						onList.remove(useraccount);
						connectionsList.remove(this);
						sendToAllClients("3"+useraccount);
					}
					else if(input != null&&input.startsWith("4"))
					{
						sendToAllClients(input);
					}
					else if(input != null&&input.startsWith("r"))
					{
						String[] temp = input.split(";");
						useraccount = temp[0].substring(1);
						userpwd = temp[1];
						UserDAO dao = new UserDAO();
						boolean regflag = dao.register(useraccount, userpwd);
						if(regflag == true)
						{
							output.append("\n"+getTime()+"\n user "+useraccount+"has registered。");
						}
						out.println(new Boolean(regflag).toString());
						out.flush();
					}
					input=in.readLine();
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					if(in != null)in.close();
					if(out != null)out.close();
					socket.close();
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
		}
		public ClientConnection(Socket s)
		{ 
			socket = s; 
		}
	    public void sendMessageToClient(String on){
		   try{
		    	out.println(on);
		    	out.flush();
		   	}catch(Exception e){
		    	e.printStackTrace();
		    }
		}
	}
	public String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat  dat = new SimpleDateFormat("yyyy/MM/dd");
		GregorianCalendar gc = new GregorianCalendar();
		return dat.format(new Date())+" "+sdf.format(gc.getTime());
		}

	public static void main(String[] args){
		new Server("server").startServer();
	}
}