package data;

import java.util.Date;

//Class with the format of messages
public class MessageChat {
	private String author;
	private Date date;
	private String content;
	
	public MessageChat(String author,Date date, String content) {
		this.author=author;
		this.date=date;
		this.content=content;
	}
	
	public String getAuthor() {
		return this.author;
	}
	
	public Date getDate() {
		return this.date;
	}
	
	public String getContent() {
		return this.content;
	}
}
