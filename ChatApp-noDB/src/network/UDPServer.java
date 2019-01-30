package network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.io.*;
import java.util.*;
import data.*;
import model.*;

public class UDPServer extends  Thread implements PropertyChangeListener {
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	private DatagramSocket socket;
	private byte[] buf = new byte[65535];
	private ModelData Data;
	
	public UDPServer(ModelData d,int portsrc) throws Exception{
			this.Data=d;
			this.socket = new DatagramSocket(portsrc);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	 }
	
	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("userList")) { 							//if the connected user list has changed
			this.Data.setConnectedUsers((ArrayList<User>) evt.getNewValue());
		}
	}

	//converts a java object to byte[] in order to send it
	private static byte[] serialize(PacketUserList ListUser) throws IOException {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream os = new ObjectOutputStream(out);
	    os.writeObject(ListUser);
	    byte [] data = out.toByteArray();
	    os.close();
	    out.close();
	    return data;
	}
	
	//returns true if the local user is the last of the list to connect
	private boolean isLastConnected () {
		Date localUserDate = Data.getLocalUser().getUser().getDate();
		ListIterator<User> i= Data.getConnectedUsers().listIterator();
		while(i.hasNext()) {
			User local=i.next();
			Date localDate = local.getDate();
			if(localUserDate.before(localDate)) {
				return false;
			}
		}
		return true;
	}
	
	public void run() {
		boolean running = true;
		while(running) {
			try {
				DatagramPacket incomingPacket = new DatagramPacket(buf,buf.length);
				this.socket.receive(incomingPacket);				//wait to receive a message
				
				InetAddress dstAddress = incomingPacket.getAddress();								//extract the address
				String msg = new String(incomingPacket.getData(), 0, incomingPacket.getLength());	//extract the message
				
				if(msg.equals("ListRQ") && isLastConnected() ) { //if the message is a list request and local user is the last connected
					int port= incomingPacket.getPort();		//retrieve the distant port
					
					PacketUserList PacketList = new PacketUserList(Data.getConnectedUsers()); //put list of connected users in packet
					byte[] data = serialize(PacketList);									  //serialize the packet
					DatagramPacket outgoingPacket = new DatagramPacket(data,data.length,dstAddress,port);	//put into datagram packet
					this.socket.send(outgoingPacket);														//send packet
				} else if(msg.equals("end")) {				//if the message is from local UDPClient to "end",
					running=false;							// end loop and Thread
				} else if (msg.equals("disconnect")){		//if the message is a user disconnection
					ListIterator<User> i= Data.getConnectedUsers().listIterator();	//find that user in the list
					boolean trouve = false;
					while(i.hasNext() && !trouve) {
						User local=i.next();
						if(local.getAddr().equals(dstAddress)) {
							ArrayList<User> oldlist = new ArrayList<User>(this.Data.getConnectedUsers()); 
							Data.removeConnectedUser(local);										//remove him from the list
							
							String notif = local.getUsername()+" has logged off";					//make a notification
							pcs.firePropertyChange("ConnectionStatus",new String() , notif);		//fire change
							pcs.firePropertyChange("userList", oldlist, Data.getConnectedUsers());	//fire change
							trouve=true;
						}
					}
				} else if(!msg.equals("ListRQ")){									//if the message is a new username
					ListIterator<User> i= Data.getConnectedUsers().listIterator();	//try to find the user in the list
					boolean trouve = false;
					User newUser = new User(msg,dstAddress);
					ArrayList<User> oldlist = new ArrayList<User>(this.Data.getConnectedUsers());
					while(i.hasNext() && !trouve) {
						User local = i.next();
						if(local.getAddr().equals(dstAddress)) {								//he's online already, so it's a renaming
							this.Data.removeConnectedUser(local);								//remove him from the list
							String notif = local.getUsername()+" changed their username to "+msg;	//make a notification
							pcs.firePropertyChange("Pseudo",new String() , notif);				//fire change
							newUser.setDate(local.getDate()); 									//set his date so it doesn't reset
							trouve=true;
						}
					}
					
					if(!trouve) {															//if he wasn't on the list, he logged on
						String notif2 = msg + " has logged on";								//make a notification
						pcs.firePropertyChange("ConnectionStatus",new String() , notif2);	//fire change
					}
					this.Data.addConnectedUser(newUser);									//add new user to list of connected users
					pcs.firePropertyChange("userList", oldlist, this.Data.getConnectedUsers()); //fire change
				}	
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		this.socket.close();
	}
}
