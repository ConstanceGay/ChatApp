package httpServer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ListIterator;
import java.io.OutputStream;

import data.User;
import model.PacketUserList;

public class ThreadClientConnection implements Runnable,PropertyChangeListener{
	
	private ArrayList<User> OnlineUserList ; 	//list of online users
	private Socket connect;				// Client Connection via Socket Class
	private InetAddress clientAddr;		//client's address
	private Socket clientSocket;
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	 }
	
	public void propertyChange(PropertyChangeEvent evt) {
	}
	
	public ThreadClientConnection(Socket c,ArrayList<User> ulist) {
		this.connect = c;
		this.clientAddr = c.getInetAddress();
		this.OnlineUserList = ulist;
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
	
	public void run() {
		try {
			// we read characters from the client via input stream on the socket
			BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			// get binary output stream to client (for requested data)
			ArrayList<User> copyList = new ArrayList<User>(this.OnlineUserList);
			
			// get first line of the request from the client
			String input = in.readLine();
			if (input.equals("ListRQ")) {	//if the person is asking for the list of online users, he sends it
				this.clientSocket = new Socket(this.clientAddr,4446);
				OutputStream dataOut = this.clientSocket.getOutputStream();
				PacketUserList packetlist = new PacketUserList(copyList);				//make a packet containing the list
				byte[] fileData = serialize(packetlist);								//serialize the packet					
				dataOut.write(fileData, 0, fileData.length);							//write packet into stream
				dataOut.flush();
				dataOut.close();
				this.clientSocket.close();
				connect.close(); // we close socket connection
			} else if (input.equals("disconnect")){	//someone is signing off
				ListIterator<User> i= copyList.listIterator();	//find that user in the list
				boolean trouve = false;
				while(i.hasNext() && !trouve) {
					User local=i.next();
					if(local.getAddr().equals(clientAddr)) {
						this.OnlineUserList.remove(local);										//remove him from the list	
						System.out.println(local.getUsername()+" has logged off");
						trouve=true;
					}
				}
				connect.close(); // we close socket connection
				pcs.firePropertyChange("changementListeConnectés", new ArrayList<User>(), this.OnlineUserList );
			} else {	//it's a new connection OR username change
				User newUser = new User(input,this.clientAddr);
				ListIterator<User> i= copyList.listIterator();	//find that user in the list
				boolean trouve = false;
				while(i.hasNext() && !trouve) {
					User local=i.next();
					if(local.getAddr().equals(clientAddr)) {
						this.OnlineUserList.remove(local);	//remove him from the list
						newUser.setDate(local.getDate());
						System.out.println(local.getUsername()+" has changed their username to "+input);		
						trouve=true;
					}
				}	
				if(!trouve) {
					System.out.println(input+" has logged on");
				}
				
				this.OnlineUserList.add(newUser);
				connect.close(); // we close socket connection
				pcs.firePropertyChange("changementListeConnectés", new ArrayList<User>(), this.OnlineUserList );
			}
			
		} catch (IOException ioe) {
			System.err.println("Server error : " + ioe);
		} 
	}
}
