package data;

import java.net.InetAddress;
import java.net.UnknownHostException;

//Class the keeps the localUser
public class LocalUser {
	private User Client;
	
	public LocalUser(String pseudo) {
		//finds the local IPaddress
		try {
			InetAddress address = InetAddress.getLocalHost();
			this.Client=new User(pseudo,address);
		}catch (UnknownHostException e) {
			System.out.println("No Internet");
		}
	}
	
	public User getUser() {
		return this.Client;
	}
		
	public void setUser(User user) {
		this.Client = user;
	}
}
	
