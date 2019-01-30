package network;

import model.*;
import data.*;
import java.net.*;
import java.util.ArrayList;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;

public class HTTPServerThread extends Thread implements PropertyChangeListener{
		
	private ServerSocket Serversocket;
	private boolean running;
	private InetAddress localAddress;
	private Socket ClientSocket;
	static InetAddress SERVERADDRESS;
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	 }
	
	public void propertyChange(PropertyChangeEvent evt) {};
	
	public HTTPServerThread(int port,InetAddress addr) throws Exception{
		//connect to a socket in order to retrieve local address
		try {
			this.localAddress = InetAddress.getLocalHost();
		}catch (UnknownHostException e) {
			System.out.println("HTTPServerThread : No Internet");
		}
		SERVERADDRESS = addr;
		this.Serversocket = new ServerSocket(port,1,this.localAddress);
		this.running = true;
	}
	
	public ArrayList<User> sendListRequest() {
		try {				
			InetAddress serverAddr = SERVERADDRESS;
			this.ClientSocket = new Socket(serverAddr,8080);
			byte[] byte_rq = "ListRQ".getBytes();
			OutputStream os = this.ClientSocket.getOutputStream();	//retrieves the output stream of the socket
	        os.write(byte_rq,0,byte_rq.length);						//writes the bytes into the stream
	        os.flush();												//flushes the stream
	        os.close();	        									//closes the stream
	        this.ClientSocket.close();								//close the socket
	        
	        this.Serversocket.setSoTimeout(3000);					//set the socket to timeout after 3 second
	        Socket client = this.Serversocket.accept();				//accept connections to the socket
	        															
	        byte [] byte_data = new byte [6022386];					//new byte to store data
	        InputStream is = client.getInputStream();				//retrieve the input stream of the distant socket
	        int bytesRead = is.read(byte_data,0,byte_data.length);	
	        int current = bytesRead;
	        do {
	           bytesRead = is.read(byte_data, current, (byte_data.length-current));
	           if(bytesRead >= 0) current += bytesRead;
	        } while(bytesRead > -1);

	        Object data = deserialize(byte_data);			//deserialize the data
	        Class<? extends Object> c = data.getClass();	//retrieve the class of the data
	        
	        //if it's a list
	        if (c.getCanonicalName().equals(PacketUserList.class.getCanonicalName())) {
	        	PacketUserList packet_list = (PacketUserList) data;
	        	return	packet_list.getUserList();	
	        } else{
	        	return new ArrayList<User>();
	        }
		} catch (Exception e) {
			//System.out.println("HTTPServerThread : problem retrieving list "+e.toString());
			return null;
		}
	}
	
	public boolean sendPseudo(String username) {
		try {
			InetAddress serverAddr = SERVERADDRESS;
			this.ClientSocket = new Socket(serverAddr,8080);
			byte[] byte_rq = username.getBytes();
			OutputStream os = this.ClientSocket.getOutputStream();	//retrieves the output stream of the socket
	        os.write(byte_rq,0,byte_rq.length);						//writes the bytes into the stream
	        os.flush();												//flushes the stream
	        os.close();	        									//closes the stream
	        this.ClientSocket.close();								//close the socket
	        return true;
		} catch (Exception e) {
			System.out.println("HTTPServerThread : problem sending username "+e.toString());
			return false;
		}
	}
	
	//called when the TCP server is closed
	public void stopServer() {
		this.running=false;							//the loop of the thread fails
		try {
			this.Serversocket.close();				//closes the socket
		}catch (Exception e) {
			System.out.println("HTTPServerThread : Socket non fermé "+e.toString());
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
		        this.Serversocket.setSoTimeout(1000);					//set the socket to timeout after 1 second
		        Socket client = this.Serversocket.accept();				//accept connections to the socket
		        															
		        byte [] byte_data = new byte [6022386];					//new byte to store data
		        InputStream is = client.getInputStream();				//retrieve the input stream of the distant socket
		        is.read(byte_data,0,byte_data.length);	
		        
		        Object data = deserialize(byte_data);					//deserialize the data
		        Class<? extends Object> c = data.getClass();			//retrieve the class of the data
		        
		        //if it's a list
		        if (c.getCanonicalName().equals(PacketUserList.class.getCanonicalName())) {
		        	PacketUserList packet_list = (PacketUserList) data;
		        	ArrayList<User> list = packet_list.getUserList();	
		        	
			        pcs.firePropertyChange("OnlineUserListHTTP", new ArrayList<User>(), list); //fire change
		        } 
	        }catch (Exception e) {}
		}
	}
}
