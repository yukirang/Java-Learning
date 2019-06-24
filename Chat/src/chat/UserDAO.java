package chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import javax.swing.JOptionPane;

public class UserDAO {
	
	private ArrayList<User> userlist = new ArrayList<User>();
    public UserDAO()
    {
		String usersFile = "user.txt"+File.separator;
		try{
			BufferedReader reader =  new BufferedReader(new FileReader(new File(usersFile)));
			String line = reader.readLine();
			while(true){
				if(line == null)
					break;
				String[] temp = line.split("#");
				if(temp.length>1)
				{
					userlist.add(new User(temp[0],temp[1]));
				}
				line = reader.readLine(); 
			}
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public User getUser(){
	    return this.userlist.get(userlist.size());
	}
	
	public boolean login(String useraccount,String userpwd)
	{
		boolean flag = false;
		for(User u:userlist){
			if(u.getUseraccount().equals(useraccount)&&u.getUserpwd().equals(userpwd)){
				flag = true;
				return flag;
			}
		}
		return flag;
	}
	
	public boolean register(String useraccount,String userpwd)
	{
		boolean flag = true;
		String file = "user.txt";
		String str = useraccount+"#" + userpwd;
		String in = null;
		try {
				BufferedReader br = new BufferedReader(new FileReader(new File(file)));
				for(in = br.readLine(); in!=null; in = br.readLine())
				{
					String []temp = in.split("#");
					if(temp[0].equals(useraccount))
					{
						flag = false;
						break;
					}
				}
				if(flag == true)
				{
					FileWriter fos = new FileWriter(new File(file),true);
					BufferedWriter bw = new BufferedWriter(fos);
					bw.write(str);
					bw.newLine();
					bw.close();
				}
				br.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		return flag;
	}
}
