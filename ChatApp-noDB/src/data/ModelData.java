package data;


import java.util.*;
import java.net.InetAddress;

//Class that handles all the local data
public class ModelData{

	private LocalUser user;
	private ArrayList<User> ConnectedUsers;  //list of connected users
	private ArrayList<Session> sessionList;	//list of Sessions (conversations with other users)
	
	public ModelData (LocalUser user,ArrayList<User> l) {
		this.user=user;
		this.ConnectedUsers=l;
		this.sessionList=new ArrayList<Session>();
	}
	
	//Adds a new message from a user
	public void addMessage(MessageChat message,String OtherUser){
		//finds the corresponding Session
		ListIterator<Session> i= this.sessionList.listIterator();
		try {
			InetAddress address = getAddressFromPseudo(OtherUser);	//gets the address of the otheruser
			boolean trouve=false;
			while(i.hasNext() && !trouve) {
				Session local=i.next();
				if(local.getOtherUserAddress().equals(address)) {
					local.addMessage(message);
					trouve=true;
				}
			}
			if(!trouve) { //of you don't find a session, a new one is created
				Session local=new Session(address);
				local.addMessage(message);
				this.sessionList.add(local);
			}
		} catch (Exception e) {
			System.out.println("Cet utilisateur n'est pas connecté");
		}
	}
	
	public void addConnectedUser(User U) {
		this.ConnectedUsers.add(U);
	}

	public void removeConnectedUser(User U) {
		this.ConnectedUsers.remove(U);
	}
	
	public void setConnectedUsers(ArrayList<User> l) {
		this.ConnectedUsers = l;
	}
	
	public void setSessionList(ArrayList<Session> l) {
		this.sessionList = l;
	}
	
	public ArrayList<MessageChat> getSession(String Username){
		ListIterator<Session> i= this.sessionList.listIterator();
		ArrayList<MessageChat> result=new ArrayList<MessageChat>();
		try {
			InetAddress address = getAddressFromPseudo(Username);
			while(i.hasNext()) {
				Session local=i.next();
				if(local.getOtherUserAddress().equals(address)) {
					result=local.getMessageChat();
				}
			}		
			return result;
		}catch (Exception e){
			return result;
		}
	}
	
	public ArrayList<MessageChat> getSessionFromAddress(InetAddress address){
		ListIterator<Session> i= this.sessionList.listIterator();
		ArrayList<MessageChat> result=new ArrayList<MessageChat>();
		while(i.hasNext()) {
			Session local=i.next();
			if(local.getOtherUserAddress().equals(address)) {
				result=local.getMessageChat();
			}
		}		
		return result;
	}
	
	public ArrayList<Session> getSessionlist() {
		return this.sessionList;
	}
	
	public ArrayList<User> getConnectedUsers() {
		return this.ConnectedUsers;
	}
	
	public LocalUser getLocalUser() {
		return this.user;
	}	
	
	//retrieves a the username of an online user, using their address
	public String getPseudoFromAddress(InetAddress addr) throws Exception {
		if(!this.ConnectedUsers.isEmpty() && !this.ConnectedUsers.equals(null)) {
			//on cherche si le pseudo est déja dans la liste
			ListIterator<User> i= this.ConnectedUsers.listIterator();
			while(i.hasNext()) {
				User local=i.next();
				if (local.getAddr().equals(addr)){
					return local.getUsername();
				}
			}
		}
		throw new NullPointerException("La liste est vide OU on ne connait pas cet utilisateur");
	}
	
	//retrieves the address of an online user, using their username
	public InetAddress getAddressFromPseudo(String pseudo) throws Exception {
		if(!this.ConnectedUsers.isEmpty() && !this.ConnectedUsers.equals(null)) {
			//on cherche si le pseudo est déja dans la liste
			ListIterator<User> i= this.ConnectedUsers.listIterator();
			while(i.hasNext()) {
				User local=i.next();
				if (local.getUsername().equals(pseudo)){
					return local.getAddr();
				}
			}
		}
		throw new NullPointerException("La liste est vide OU on ne connait pas cet utilisateur");		
	}
	
}
