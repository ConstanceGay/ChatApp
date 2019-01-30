package network;

import model.*;
import data.*;
import java.net.*;
import java.util.Date;
import java.util.ArrayList;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class TCPServer extends Thread implements PropertyChangeListener{
		
	private ServerSocket socket;
	private ModelData Data;
	private boolean running;
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	public TCPServer(ModelData Data,int port) throws Exception{
		this.Data=Data;
		this.socket = new ServerSocket(port,1,this.Data.getLocalUser().getUser().getAddr());
		this.running = true;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	 }
	
	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("userList")) {								//if the connected user list has changed
			this.Data.setConnectedUsers((ArrayList<User>) evt.getNewValue());		//update the local one
		}/* else if(evt.getPropertyName().equals("sessionList")) {					//if the session list has changed
			this.Data.setSessionList((ArrayList<Session>) evt.getNewValue());		//update the local one
		} */
	}
	
	//called when the TCP server is closed
	public void stopServer() {
		this.running=false;							//the loop of the thread fails
		try {
			this.socket.close();					//closes the socket
		}catch (Exception e) {
			System.out.println("TCPServer : Socket non fermé");
		}
	}
	
	//Deserializes an Object from byte[]
	private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is = new ObjectInputStream(in);
		return (Object) is.readObject();
	}
	
	public void run() {
		while(this.running) {
			try {
		        this.socket.setSoTimeout(1000);									//set the socket to timeout after 1 second
		        Socket client = this.socket.accept();							//accept connections to the socket
		        InetAddress clientAddr = client.getInetAddress();				//retrieve the distant user's address
		        String pseudo = this.Data.getPseudoFromAddress((clientAddr)); 	//retrieve the distant user's username
		        															
		        byte [] byte_data = new byte [6022386];					//new byte to store data
		        InputStream is = client.getInputStream();				//retrieve the input stream of the distant socket
		        int bytesRead = is.read(byte_data,0,byte_data.length);	
		        int current = bytesRead;
		        do {
		           bytesRead = is.read(byte_data, current, (byte_data.length-current));
		           if(bytesRead >= 0) current += bytesRead;
		        } while(bytesRead > -1);

		        Object data = deserialize(byte_data);	//deserialize the data
		        Class<? extends Object> c = data.getClass();				//retrieve the class of the data
		        
		        //if it's a message
		        if (c.getCanonicalName().equals(PacketMessage.class.getCanonicalName())) {
		        	PacketMessage packet_msg = (PacketMessage) data;
		        	String msg = packet_msg.getMessage();							//retrieve message from the packet
		        	
			        MessageChat message = new MessageChat(pseudo, new Date(),msg);	//create new message
			        this.Data.addMessage(message,pseudo);							//add message to session

			        pcs.firePropertyChange("NewMessageFrom", new String(), pseudo);	//fire changes
			        pcs.firePropertyChange("sessionList", "", "a"); //fire changes
		        } else if(c.getCanonicalName().equals(PacketFile.class.getCanonicalName())) {
		        	PacketFile packet_file = (PacketFile) data;				
		        	String name = packet_file.getName();				//retrieve file name from packet
			        
			        //create and add message
			        MessageChat message = new MessageChat(pseudo, new Date(),"Envoi du fichier "+name+" (Clavardage/file_reception)");
			        this.Data.addMessage(message,pseudo);
			        
			        //fire changes
			        pcs.firePropertyChange("NewFile", null, packet_file);
			        pcs.firePropertyChange("NewFileFrom", new String(), pseudo);
			        pcs.firePropertyChange("sessionList", "", "a");
		        }
	        }catch (Exception e) {
	        	
	        }
		}
	}
	
    public InetAddress getSocketAddress() {
        return this.socket.getInetAddress();
    }
    
    public int getPort() {
        return this.socket.getLocalPort();
    }
}
