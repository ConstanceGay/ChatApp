package model;

import java.io.Serializable;

//Serializable packet in order to send files
public class PacketFile implements Serializable{
	
private static final long serialVersionUID = 1L;
	
	private byte[] bytes;
	private String name;
	
	public PacketFile(byte[] b, String n) {
		this.bytes=b;
		this.name=n;
	}
	
	public byte[] getBytes(){
		return this.bytes;
	}	
	
	public String getName(){
		return this.name;
	}
}
