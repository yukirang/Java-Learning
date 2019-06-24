package chat;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import chat.Server;
import chat.Login.LoginListener.LoginHelper;

import javax.swing.*;


public class ClientServer extends JFrame {	
  private BufferedReader in = null;
  private PrintStream out = null;
  private Socket socket;
  private String useraccount;	
  private String destination;
  private boolean flag = true;
  String first;
  JTextArea output = new JTextArea("");
  JTextArea output2 = new JTextArea("\t\t"); 
  JScrollPane pane1 = new JScrollPane(output);
  JScrollPane pane3 = new JScrollPane(output2);
  JPanel jpBottom = new JPanel(); // Lower half panel
  JTextArea input = new JTextArea("");
  JScrollPane pane2 = new JScrollPane(input);
  JComboBox jcb1 = new JComboBox(new String[] {"choose emoji", ":)", ":(", "^_^","-_-","^o^","$_$","O_O",">_<","T_T","W_W","@_@"});
  JComboBox jcb2 = new JComboBox(new String[] {"normal words", "Hello！", "Hi everyone！", "Bye.", "Thank you!", "What happened？", "OK.", "Talk to you later.","Sorry."});
  JButton sendbtn = new JButton("send");
  JPanel jpBottomRight = new JPanel(); // right-down panel
  JLabel onlabel = new JLabel();
  DefaultListModel dlmName;
  JList online ;
  JPanel jpNorth = new JPanel();
  private final JTextField txtPrivateChat = new JTextField();
  private final JTextField txtPublicChat = new JTextField();
 
  public ClientServer(Socket socket,String useraccount) {
    super("chat "+ useraccount);
    txtPrivateChat.setHorizontalAlignment(SwingConstants.CENTER);
    txtPrivateChat.setText("private chat");
    txtPrivateChat.setColumns(10);
    this.socket = socket;
    this.useraccount = useraccount;
    output.setFont(new Font("serif", Font.PLAIN , 14));
    output.setEditable(false);
    output2.setEditable(true);
    dlmName = new DefaultListModel();
    dlmName.addElement("  online user             ");
    online = new JList(dlmName);
    getContentPane().add(pane1, BorderLayout.CENTER);
    txtPublicChat.setText("public chat");
    txtPublicChat.setHorizontalAlignment(SwingConstants.CENTER);
    txtPublicChat.setColumns(10);
    
    pane1.setColumnHeaderView(txtPublicChat);
    getContentPane().add(jpBottom, BorderLayout.SOUTH);
    getContentPane().add(jpNorth,BorderLayout.EAST);
    getContentPane().add(pane3,BorderLayout.WEST);
    
    pane3.setColumnHeaderView(txtPrivateChat);
    input.setFont(new Font("Serif", Font.PLAIN , 18));
    input.setAutoscrolls(true); // auto-scroll with new input
    pane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    pane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    jpBottom.setLayout(new BorderLayout());
    jpBottom.add(pane2, BorderLayout.CENTER);
    jpBottom.add(jpBottomRight, BorderLayout.EAST);
    

    jpBottomRight.setLayout(new GridLayout(3, 1));
    jpBottomRight.add(jcb1);
    jpBottomRight.add(jcb2);
    jpBottomRight.add(sendbtn);
    
    jpNorth.add(online);
    
    jcb1.addItemListener(
      new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
        	if(e.getStateChange() == ItemEvent.SELECTED)
           input.append(jcb1.getSelectedItem().toString());
        }
      }
    );
    
    jcb2.addItemListener(
      new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          Font old = input.getFont(); // original font
          if(e.getStateChange() == ItemEvent.SELECTED)
              input.append(jcb2.getSelectedItem().toString());
        }
      }
    );
    
    MouseListener mouseListener = new MouseAdapter(){
    	public void mouseClicked(MouseEvent me)
    	{
    		JList list =(JList)me.getSource();
    		if(me.getClickCount() == 1){
    			 int index = list.locationToIndex(me.getPoint());
    			 if(index >=1)
    			 {
    				 Object o = list.getModel().getElementAt(index);
					 destination = o.toString();
					 flag = false;
    			 }
    			 else if(index <1)
    			 {
    				 flag = true;
    			 }
    		}
    	}
    };
    
    online.addMouseListener(mouseListener);
    
    
    setLocation(350, 150);
    setSize(600, 550);
    setResizable(false);
    setVisible(true);
    
    
    addWindowListener(new WindowAdapter(){
    	public void windowClosing(WindowEvent e){
    		super.windowClosing(e);
    	    sendExitToServer();
    		dispose();
			Thread.currentThread().stop();
    	    System.exit(0);
    	}
    	
    });
    
    sendbtn.addActionListener(new SendHandler());
  }
  
  public void sendStartToServer(){
	  out.println("4"+useraccount);
  }
  
  private void sendExitToServer(){
	  out.println("3#"+useraccount);
  }
  
  private void sendMessageToServer(){
	  String text = input.getText();
	  System.out.println(flag);
	  if(flag == false)
	  {
		  out.println("2#"+destination+"#"+useraccount+"#"+text);
	  }
	  else
	  {
		  out.println("1"+useraccount+":"+text);
	  }
	  input.setText("");
  }
  
  private class SendHandler implements ActionListener{
	  public void actionPerformed(ActionEvent e){
		  sendMessageToServer();
	  }
  }
  public String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat  dat = new SimpleDateFormat("yyyy/MM/dd");
		GregorianCalendar gc = new GregorianCalendar();
		return dat.format(new Date())+" "+sdf.format(gc.getTime());
}
  
  public void doConnect(Socket socket){
	  try{
		  in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		  out = new PrintStream(socket.getOutputStream());
		  new Thread(new MessageReader()).start(); 
	  }catch(Exception e){
		  System.out.println("Cannot connect to the server!");
		  e.printStackTrace();
	  }
  }
  private class MessageReader implements Runnable{
    
	  private boolean keepListening = true;
	  @Override
	  public void run() {
		// TODO Auto-generated method stub
		  while(keepListening == true){
			  try{
				  String next = in.readLine();
				  if(next != null &&next.startsWith("4"))
				  {
					  String[] nameList = next.split("&");
					  dlmName.clear();
					  dlmName.addElement("  online user      ");
					  for(int i=1;i<nameList.length;i++)
					  {
						  dlmName.addElement(nameList[i]);
					  }
					  if(!(nameList[0].substring(1).equals(useraccount)))
					  {
						  output.append(getTime()+"\n");
						  output.append(nameList[0].substring(1)+" is online. \n");
					  }
				  }
				  else if(next!= null &&next.startsWith("3"))
				  {
					  output.append(getTime()+"\n");
					  output.append(next.substring(1)+"is offline. \n");
					  dlmName.removeElement(next.substring(1));
				  }
				  else if(next!= null && next.startsWith("1")) 
				  {
					  output.append(getTime()+"\n");
					  output.append(next.substring(1) + "\n");
				  }
				  else if(next != null && next.startsWith("2"))
				  {
					  String[] ds = next.split("#");
					  if(ds[1].equals(useraccount))
					  {
						  output2.append("\n"+getTime());
						  output2.append("\n"+ds[2]+"(to you):\n");
						  output2.append(ds[3]);
					  }
					  else if(ds[2].equals(useraccount))
					  {
						  output2.append("\n"+getTime());
						  output2.append("\n"+" to "+ds[1]+":\n");
						  output2.append(ds[3]);
					  }
				  }
			  }catch(Exception e){
				  keepListening = false;
				  e.printStackTrace();
			  }
		  }		
	}
	  
  }
}