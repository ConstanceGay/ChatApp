package data;

import java.net.InetAddress;
import java.util.*;

//One session per User you chat with
public class Session {
	private InetAddress otherUserAddress;
	private ArrayList<MessageChat> ConvList;
	
	public Session(InetAddress OUA) {
		this.otherUserAddress=OUA;
		this.ConvList=new ArrayList<MessageChat>();
	}
	
	public ArrayList<MessageChat> getMessageChat(){
		return this.ConvList;
	}
	
	public InetAddress getOtherUserAddress() {
		return this.otherUserAddress;
	}
	
	public void addMessage(MessageChat message) {
		ConvList.add(message);
	}
	
}
