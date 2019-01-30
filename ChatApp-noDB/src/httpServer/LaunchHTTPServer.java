package httpServer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ListIterator;

import data.User;
import model.PacketUserList;
import view.ServerWindow;

// Each Client Connection will be managed in a dedicated Thread
public class LaunchHTTPServer  implements PropertyChangeListener{ 
	
	static ArrayList<User> OnlineUserList = new ArrayList<User>(); 	//list of online users
	static final int PORT = 8080;									// port to listen connection
	
	private ServerSocket serverConnect;
	private boolean running = true;
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("changementListeConnectés")) {	//if the connected user list has changed
			OnlineUserList = ((ArrayList<User>) evt.getNewValue());		//update the local one
			sendListtoUsers(OnlineUserList);
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
	
	private void sendListtoUsers(ArrayList<User> copyList) {
		PacketUserList packetList = new PacketUserList(copyList);
		try {
			byte[] serialized_msg = serialize(packetList);				//serializes it into bytes[]
			ListIterator<User> i= copyList.listIterator();	//go through list
			while(i.hasNext()) {
				User local=i.next();					
				Socket socket = new Socket (local.getAddr(),4446);
			    	
		    	OutputStream os = socket.getOutputStream();		//retrieves the output stream of the socket
		        os.write(serialized_msg,0,serialized_msg.length);		//writes the bytes into the stream
			    os.flush();												//flushes the stream
			    os.close();	        									//closes the stream
			    socket.close();	
			}
		} catch (IOException e) {
			System.out.println("LaucnchHTTPServer : "+e.toString());
		}
	}
	
	public boolean Disconnect() {
		try {
			this.serverConnect.close();
			return true;
		} catch (Exception e){
			System.out.println("HTTPServer: disconnect failed "+e.toString());
			return false;
		}
	}
	
	public LaunchHTTPServer() {
		ServerWindow window = new ServerWindow();
		window.setVisible(true);
		try {
			//connect to a socket in order to retrieve local address
			final DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);
			socket.connect(InetAddress.getByName("8.8.8.8"),10002);
			InetAddress localAddress = socket.getLocalAddress();
			socket.close();
			serverConnect = new ServerSocket(PORT,1,localAddress);
			
			window.addWindowListener(new WindowAdapter(){
	            public void windowClosing(WindowEvent e){
	            	if(Disconnect()) {
	            		e.getWindow().dispose();
		    		}
	            }
	        });    
			
			 //Send a file button listener
		    window.disconnectButton.addActionListener(new ActionListener() {
		    	public void actionPerformed(ActionEvent event) {
		    		if(Disconnect()) {
		    			window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
		    		}
				}
		    });
		    
		    window.TextArea.setText("Server is running on local address : "+localAddress.toString());

			// we listen until user halts server execution
			while (running) {
				ThreadClientConnection newThread = new ThreadClientConnection(serverConnect.accept(),OnlineUserList);
				Thread thread = new Thread(newThread);		// create dedicated thread to manage the client connection
				newThread.addPropertyChangeListener(this);
				thread.start();				
			}
			
		} catch (IOException e) {
			window.TextArea.setText("The server crashed, please restart");
			//System.err.println("Server Connection error : " + e.getMessage());
		}
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		LaunchHTTPServer HTTPServer = new LaunchHTTPServer();	

	}
}