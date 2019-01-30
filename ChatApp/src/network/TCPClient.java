package network;

import java.net.*;
import model.PacketFile;
import model.PacketMessage;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class TCPClient {
	    private Socket socket;
	    
	    public TCPClient(InetAddress serverAddr, int serverPort) throws Exception {
	        this.socket = new Socket(serverAddr, serverPort);				//opens the socket
	    }
	    
	  	//Converts a java object to bytes[] in order to send it
		public static byte[] serialize(Object pack) throws IOException {
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ObjectOutputStream os = new ObjectOutputStream(out);
		    os.writeObject(pack);
		    byte [] data = out.toByteArray();
		    return data;
		}
	    
		//sends message
	    public void sendTxt(String message) throws IOException {
	    	PacketMessage packet = new PacketMessage(message);		//puts the message in a serializable packet
	    	byte[] serialized_msg = serialize(packet);				//serializes it into bytes[]
	    	
	    	OutputStream os = this.socket.getOutputStream();		//retrieves the output stream of the socket
	        os.write(serialized_msg,0,serialized_msg.length);		//writes the bytes into the stream
	        os.flush();												//flushes the stream
	        os.close();	        									//closes the stream
	        this.socket.close();									//closes the socket
	    }
	    
	    //sends the file corresponding to that path
	    public void sendFile(String path) throws IOException {
	        File myFile = new File (path);							//gets the file corresponding to path
	        String name = myFile.getName();							//retrieves the file name
	        byte [] byte_file  = new byte [(int)myFile.length()];	//makes a byte that will contain the file's data
	        FileInputStream fis = new FileInputStream(myFile);		//opens the input stream of the file
	        BufferedInputStream bis = new BufferedInputStream(fis);	//links it to the buffered input stream
	        bis.read(byte_file,0,byte_file.length);					//buffered input stream writes into byte_file
	        bis.close();											//close buffered output stream
	        
	        PacketFile packet = new PacketFile(byte_file,name);		//puts the byte_file + file name into serializable packet
	        byte[] serialized_file = serialize(packet);				//serializes it into bytes[]
	        
	        OutputStream os = this.socket.getOutputStream();		//retrieves the output stream of the socket
	        os.write(serialized_file,0,serialized_file.length);		//writes the bytes into the stream
	        os.flush();												//flushes the stream
	        os.close();	        									//closes the stream
	        this.socket.close();									//closes the socket
	    }
}