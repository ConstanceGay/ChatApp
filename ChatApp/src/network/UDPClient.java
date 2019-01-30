package network;

import java.net.*;
import java.io.*;
import model.*;
import data.*;
import java.util.*;

public class UDPClient{

	private DatagramSocket socket;
	private byte[] buf = new byte[65535];
	private InetAddress localAddr;
	public static InetAddress broadcastAddr;
	
	public UDPClient () {
		//connect to a socket in order to retrieve local address
		try (final DatagramSocket socket = new DatagramSocket()){
			socket.setBroadcast(true);
			socket.connect(InetAddress.getByName("8.8.8.8"),10002);
			this.localAddr = socket.getLocalAddress();
			socket.close();
			this.socket= new DatagramSocket();
			//on recupere l'adresse de broadcast
			NetworkInterface ni=NetworkInterface.getByInetAddress(localAddr);
			for (int i =0; i<ni.getInterfaceAddresses().size();i++) {
				if(ni.getInterfaceAddresses().get(i).getBroadcast() != null) {
					broadcastAddr=ni.getInterfaceAddresses().get(i).getBroadcast();
				}
			}
		}catch (UnknownHostException| SocketException e) {
			System.out.println("No Internet");
		}
	}
	
	//extracts a PacketUserList from bytes
	private PacketUserList deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is = new ObjectInputStream(in);
		return (PacketUserList) is.readObject();
	}
	    
	//sends the a list request in broadcast
	public ArrayList<User> sendBroadcastListRequest(int portsrc, int portdist) {
		buf= "ListRQ".getBytes();
		try {
			DatagramPacket packet = new DatagramPacket(buf,buf.length,broadcastAddr,portdist); 	//make UDP packet
			this.socket.send(packet);															//send packet
			
			byte[] data = new byte [65535];
			DatagramPacket incomingPacket = new DatagramPacket(data,data.length);	//make packet for reception
			incomingPacket.setPort(portdist);										//connect packet to distant port
			this.socket.setSoTimeout(3000);											//socket times out after 3 sec
			this.socket.receive(incomingPacket);									//socket waits to receive packet
				
			PacketUserList Packetlist=deserialize(data);							//deserialize packet list
			ArrayList<User> list = Packetlist.getUserList();						//retrieve list from packet
			return list;															//return the list
		} catch (SocketTimeoutException e) {	//if the socket times out he assumes he is alone and returns an empty list
			return new ArrayList<User>();
		}catch (IOException e) {
			System.out.println("UDPClient: "+e.toString());
		} catch	(ClassNotFoundException e) {
			System.out.println("UDPClient: "+e.toString());
		}
		return null;
	}
	
	//sends new username to all online users
	public boolean sendPseudo(ArrayList<User> list, String pseudo, int portdist) {
		buf= pseudo.getBytes();
		try {
			DatagramPacket packet = new DatagramPacket(buf,buf.length);	//make the UDP packet to send
			packet.setPort(portdist);									//give him the distant port
			
			ListIterator<User> i= list.listIterator();					//goes through the list and sends to all users
			while(i.hasNext()) {
				User local=i.next();
				if(!local.getAddr().equals(localAddr)) {
					packet.setAddress(local.getAddr());					//set the address
					this.socket.send(packet);							//send packet
				}
			}	
			return true;
		}catch (IOException e) {
			System.out.println("ClientUDP: "+e.toString());
			return false;
		} 
	}

	//sends a disconnect message to all online users
	public boolean sendDisconnect(ArrayList<User> list, int portsrc,int portdst) {
		buf= "disconnect".getBytes();
		try {
			DatagramPacket packet = new DatagramPacket(buf,buf.length);	//make the UDP packet to send
			packet.setPort(portdst);									//give him the distant port
			
			ListIterator<User> i= list.listIterator();					//goes through the list and sends to all users
			while(i.hasNext()) {
				User local=i.next();
				if(!local.getAddr().equals(localAddr)) {
					packet.setAddress(local.getAddr());					//set the address
					this.socket.send(packet);							//send packet
				}
			}		
			
			buf= "end".getBytes();										
			DatagramPacket packetServer = new DatagramPacket(buf,buf.length,localAddr,portsrc);	//send packet to localuser
			this.socket.send(packetServer);			//send packet ordering its UDPServer to end
			return true;
		}catch (IOException e) {
				return false;
		} 
	}
	
	public void close(){
		this.socket.close();	//closes socket
	}
}