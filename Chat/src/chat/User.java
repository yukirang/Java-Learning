package chat;
public class User {
	private String useraccount;
	private String userpwd;

	
  	User(String useraccount,String userpwd){
  		this.useraccount = useraccount;
  		this.userpwd = userpwd;
  	}
	
	public String getUseraccount(){
		return useraccount;
	}
	
	public void setUseraccount(String useraccount){
		this.useraccount = useraccount;
	}
	
	public String getUserpwd(){
		return userpwd;
	}
	
	public void setUserpwd(String userpwd){
		this.userpwd = userpwd;
	}
	
}
