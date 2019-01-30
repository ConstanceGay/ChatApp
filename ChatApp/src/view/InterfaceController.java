package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.PacketFile;
import model.database;
import network.NetworkController;

public class InterfaceController implements PropertyChangeListener{

	static String SelectedContact;
	static NetworkController NetworkController = new NetworkController();
	static LoginWindow loginWindow;
    static ChatWindow chatWindow;
    static IPWindow IPWindow;
    static boolean UDPConnection;
	
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
    
    public InterfaceController() {
    	try {
			database.loadDriver();
			//System.out.println("Database Driver OK");
		} catch (Exception e){
			System.out.println("Database Driver OK");
		}
    	SelectedContact="";
    	loginWindow= new LoginWindow();
    	chatWindow = new ChatWindow();
    	IPWindow= new IPWindow();
    	NetworkController.addPropertyChangeListener(this);
	    loginWindow.setVisible(true);
	    chatWindow.setVisible(false);
	    IPWindow.setVisible(false);
	    
	    /*------------------------LoginWindow listeners--------------------------*/
	    
	 	//Listener when the login button is pressed
	    loginWindow.loginButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent event) {
	            String login = loginWindow.loginTextField.getText();
	            if (login.length() < 1) {
	                JOptionPane.showMessageDialog(null, "You forgot to enter a username", "Error", JOptionPane.ERROR_MESSAGE);
	            } else if (login.length() > 15) {
	                JOptionPane.showMessageDialog(null, "Your username can't contain more than 15 characters", "Error", JOptionPane.ERROR_MESSAGE);
	            } else if (login.contains(" ")==true){
	            	JOptionPane.showMessageDialog(null, "Your username can't contain spaces", "Error", JOptionPane.ERROR_MESSAGE);
	            }else if (login.equals("ListRQ") || login.equals("end") || login.equals("disconnect")){
	            	JOptionPane.showMessageDialog(null, "The Usernames : ListRQ, end, disconnect are forbidden", "Error", JOptionPane.ERROR_MESSAGE);
	            } else if(!loginWindow.lanRadioButton.isSelected() && !loginWindow.wanRadioButton.isSelected()) {
	            	JOptionPane.showMessageDialog(null, "Select a connexion mode", "Error", JOptionPane.ERROR_MESSAGE);
	            }
	            else {  
	            	UDPConnection = loginWindow.lanRadioButton.isSelected();
	            	if(UDPConnection) {
		            	if(NetworkController.PerformConnectUDP(login, 4445, 4445, 2000)) {
		            		try {
		            			chatWindow.UpdateConnectedUsers(NetworkController.getModelData().getConnectedUsers(),NetworkController.getModelData().getLocalUser().getUser().getUsername(),database.getAllUserIP());
		            		}catch (Exception e){
		            			System.out.println("Interface controller : "+e.toString());
		            		}
		            		loginWindow.setVisible(false);
			            	chatWindow.setVisible(true);
		            	} else {
		            		JOptionPane.showMessageDialog(null, "This username is unavailable", "Error", JOptionPane.ERROR_MESSAGE);
		            	}
	            	} else {
	            		IPWindow.setVisible(true);
	            		loginWindow.setVisible(false);
	            	}
	            }
	        }
	    });
	    
	    /*------------------------IPWindow listeners--------------------------*/
	    //Send a file button listener
	    IPWindow.connectButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent event) {
	    		String IP = IPWindow.IPField.getText();
	    		String login = loginWindow.loginTextField.getText();
	    		try {
	    			InetAddress serveraddr = InetAddress.getByName(IP);
		    		if(NetworkController.PerformConnectHTTP(login,serveraddr)) {
	        			try {
	        				chatWindow.UpdateConnectedUsers(NetworkController.getModelData().getConnectedUsers(),NetworkController.getModelData().getLocalUser().getUser().getUsername(),database.getAllUserIP());
	        			}catch (Exception e){
	        				System.out.println("Interface controller : "+e.toString());
	        			}
		            	chatWindow.setVisible(true);
	            	} else {
	            		JOptionPane.showMessageDialog(null, "This username is unavailable/this server is unavailable", "Error", JOptionPane.ERROR_MESSAGE);
	            		IPWindow.IPField.setText(null);
	            		IPWindow.setVisible(false);
	            		loginWindow.setVisible(true);
	            	}
	    		}catch(Exception e) {
	    			JOptionPane.showMessageDialog(null, "That is not an IP address", "Error", JOptionPane.ERROR_MESSAGE);
	    			IPWindow.IPField.setText(null);
	    		}
			}
	    
	    });
	    /*------------------------ChatWindow listeners--------------------------*/
	    
	    //User disconnects when the window is closed
	    chatWindow.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
            	if(UDPConnection) {
            		NetworkController.PerformDisconnectUDP(4445, 4445);
            	}else {
            		NetworkController.PerformDisconnectHTTP();
            	}
                e.getWindow().dispose();
            }
        });
	    
	    //Listener when a new contact is selected on the online user list
	    chatWindow.OnlineUserList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				chatWindow.OfflineUserList.clearSelection(); //clears the selection on the offline users list
	    		SelectedContact = chatWindow.OnlineUserList.getSelectedValue(); //sets the value SelectedContact
	    		if (SelectedContact==null) {
	    			SelectedContact="";
	    		}else if(SelectedContact.equals(NetworkController.getModelData().getLocalUser().getUser().getUsername()+" (Moi)")){
	    			SelectedContact =NetworkController.getModelData().getLocalUser().getUser().getUsername();
	    		}
	    		//chatWindow.UpdateHistorique(NetworkController.getModelData().getSession((SelectedContact))); //Updates the messages window
	    		try {
	    			chatWindow.UpdateHistorique(database.getHistoric(NetworkController.getModelData().getAddressFromPseudo(SelectedContact)));
	    		} catch (Exception e){
	    			//System.out.println("InterfaceController (l.144) : "+e.toString());	
	    		}
			}
	    });
	    
	    //Listener when a new contact is selected on the offline user list
	    chatWindow.OfflineUserList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				try {
					chatWindow.OnlineUserList.clearSelection(); //clears the selection on the online list
		    		String address = chatWindow.OfflineUserList.getSelectedValue();
		    		SelectedContact="";
		    		int i =address.lastIndexOf("/");
		    		address = address.substring(i+1);
		    		//address = address.substring(1); //takes the / away from the InetAddress
		    		//chatWindow.UpdateHistorique(NetworkController.getModelData().getSessionFromAddress((InetAddress.getByName(address))));
		    		chatWindow.UpdateHistorique(database.getHistoric(InetAddress.getByName(address)));
				} catch (Exception e) {
					//System.out.println("InterfaceController : "+e.toString());
				}
			}
	    });
	        
	    //Send a file button listener
	    chatWindow.chatFileButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent event) {
	    		if(SelectedContact.equals("")){
	    			JOptionPane.showMessageDialog(null, "Please select a contact", "Error", JOptionPane.ERROR_MESSAGE);
	    		} else {
		    		JFileChooser fileChooser = new JFileChooser();
		    	    int result = fileChooser.showOpenDialog(chatWindow); //opens a file selector
		    	    if (result == JFileChooser.APPROVE_OPTION) {	//if a value was chosen
		    	        File selectedFile = fileChooser.getSelectedFile();
		    	        if(!(selectedFile.exists() && selectedFile.isFile() )){
		    	        	JOptionPane.showMessageDialog(null, "That is not a file", "Error", JOptionPane.ERROR_MESSAGE);
		   				}else if(!NetworkController.sendFile(SelectedContact, selectedFile.getAbsolutePath(), 2000)){
			    			JOptionPane.showMessageDialog(null, "You need to select an online contact", "Error", JOptionPane.ERROR_MESSAGE);
			    		}
		    	    }
	    		}
			}
	    
	    });
	    
	    //When the Enter key is pressed after writing a message
	    chatWindow.chatTypeTextArea.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent event) {
	    		String message = chatWindow.chatTypeTextArea.getText();	//retrieve the message
	    		chatWindow.chatTypeTextArea.setText(null);
	    		if(SelectedContact.equals("")){
	    			JOptionPane.showMessageDialog(null, "Please select a contact", "Error", JOptionPane.ERROR_MESSAGE);
	    		}else if(message.length()>1023) {
	    			JOptionPane.showMessageDialog(null, "This message is too long", "Error", JOptionPane.ERROR_MESSAGE);
	    		} else if(message.length()<1){
	    			JOptionPane.showMessageDialog(null, "You need to write a message", "Error", JOptionPane.ERROR_MESSAGE);
	    		}else if(!NetworkController.sendMessage(SelectedContact, message, 2000)){ //send it
	    			JOptionPane.showMessageDialog(null, "You need to select an online contact", "Error", JOptionPane.ERROR_MESSAGE);
	    		} 
			}
	    });
	    
	    //Change username button listener
	    chatWindow.ChangePseudoButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent event) {
	    	    String new_pseudo = chatWindow.changePseudoArea.getText();
	    	    chatWindow.changePseudoArea.setText(null);
	    	    if (new_pseudo.length() < 1) {
	                JOptionPane.showMessageDialog(null, "You forgot to enter a username", "Error", JOptionPane.ERROR_MESSAGE);
	            } else if (new_pseudo.length() > 15) {
	                JOptionPane.showMessageDialog(null, "Your username can't contain more than 15 characters", "Error", JOptionPane.ERROR_MESSAGE);
	            } else if (new_pseudo.contains(" ")==true){
	            	JOptionPane.showMessageDialog(null, "Your username can't contain spaces", "Error", JOptionPane.ERROR_MESSAGE);
	            }else if (new_pseudo.equals("ListRQ") || new_pseudo.equals("end") || new_pseudo.equals("disconnect")){
	            	JOptionPane.showMessageDialog(null, "The Usernames : ListRQ, end, disconnect are forbidden", "Error", JOptionPane.ERROR_MESSAGE);
	            } else {
		    	    if(UDPConnection) {
			            if(!NetworkController.ChangePseudoUDP(new_pseudo)) {
			            	JOptionPane.showMessageDialog(null, "This username is unavailable", "Error", JOptionPane.ERROR_MESSAGE);
			            }	   
		    	    }else {
		    	    	if(!NetworkController.ChangePseudoHTTP(new_pseudo)) {
			            	JOptionPane.showMessageDialog(null, "This username is unavailable", "Error", JOptionPane.ERROR_MESSAGE);
			            }
		    	    }
	            }
	    	}
	    });	 
	    
	  	//same listener but when the Enter key is pressed
	    chatWindow.changePseudoArea.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent event) {
	    	    String new_pseudo = chatWindow.changePseudoArea.getText();
	    	    chatWindow.changePseudoArea.setText(null);
	    	    if (new_pseudo.length() < 1) {
	                JOptionPane.showMessageDialog(null, "You forgot to enter a username", "Error", JOptionPane.ERROR_MESSAGE);
	            } else if (new_pseudo.length() > 15) {
	                JOptionPane.showMessageDialog(null, "Your username can't contain more than 15 characters", "Error", JOptionPane.ERROR_MESSAGE);
	            } else if (new_pseudo.contains(" ")==true){
	            	JOptionPane.showMessageDialog(null, "Your username can't contain spaces", "Error", JOptionPane.ERROR_MESSAGE);
	            }else if (new_pseudo.equals("ListRQ") || new_pseudo.equals("end") || new_pseudo.equals("disconnect")){
	            	JOptionPane.showMessageDialog(null, "The Usernames : ListRQ, end, disconnect are forbidden", "Error", JOptionPane.ERROR_MESSAGE);
	            }else {
		    	    if(UDPConnection) {
			            if(!NetworkController.ChangePseudoUDP(new_pseudo)) {
			            	JOptionPane.showMessageDialog(null, "This username is unavailable", "Error", JOptionPane.ERROR_MESSAGE);
			            }	   
		    	    }else {
		    	    	if(!NetworkController.ChangePseudoHTTP(new_pseudo)) {
			            	JOptionPane.showMessageDialog(null, "This username is unavailable", "Error", JOptionPane.ERROR_MESSAGE);
			            }
		    	    }
	            }
	    	}
	    });
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("userList")) {
			//Update the online users list view
			try {
				chatWindow.UpdateConnectedUsers(NetworkController.getModelData().getConnectedUsers(),NetworkController.getModelData().getLocalUser().getUser().getUsername(),database.getAllUserIP());
			}catch (Exception e) {
				System.out.print("InterfaceController pb update connected user list: "+e.toString());
			}
		} else if(evt.getPropertyName().equals("sessionList")) {
			//Update the message list view
		//	chatWindow.UpdateHistorique(NetworkController.getModelData().getSession((SelectedContact)));
			try {
			if(!chatWindow.OnlineUserList.isSelectionEmpty()) {
				chatWindow.UpdateHistorique(database.getHistoric(NetworkController.getModelData().getAddressFromPseudo(SelectedContact)));	
			}
			}catch (Exception e) {
				System.out.print("InterfaceController : "+e.toString());
			}
		} else if(evt.getPropertyName().equals("NewMessageFrom")) {
			//Update the notification panel view with a new notification : new message
			String notif = "New Message from "+(String) evt.getNewValue();
			chatWindow.UpdateNotificationList(notif,(String) evt.getNewValue(),NetworkController.getModelData().getLocalUser().getUser().getUsername());
		} else if(evt.getPropertyName().equals("NewFileFrom")) {
			//Update the notification panel view with a new notification : new file
			String notif = "New File from "+(String) evt.getNewValue();
			chatWindow.UpdateNotificationList(notif,(String) evt.getNewValue(),NetworkController.getModelData().getLocalUser().getUser().getUsername());
		} else if(evt.getPropertyName().equals("NewFile")) {
			//Update the notification panel view with a new notification : new file
			String choosertitle = "Select directory to save file";
			PacketFile packet = (PacketFile) evt.getNewValue();
			byte[] byte_file =  packet.getBytes();				//retrieve file bytes from packet
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle(choosertitle);
		    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    int result = fileChooser.showOpenDialog(chatWindow); //opens a file selector
		    if (result == JFileChooser.APPROVE_OPTION) {	//if a value was chosen
		    	try {
	    	        File selectedFile = fileChooser.getSelectedFile();
	    	      //write bytes into the file
	    	        FileOutputStream fos = new FileOutputStream(selectedFile.getAbsolutePath()+"\\"+packet.getName());	//get file output stream
	    	        BufferedOutputStream bos = new BufferedOutputStream(fos);
	    	        bos.write(byte_file, 0 , byte_file.length);
	    	        bos.flush();
	    	        fos.close();
	    	        bos.close();
		    	} catch (Exception e) {
		    		System.out.println("InterfaceController : "+e.toString());
		    	}
		    }
		}else if(evt.getPropertyName().equals("Pseudo") || evt.getPropertyName().equals("ConnectionStatus")) {
			//Update the notification panel view with a new notification
			chatWindow.UpdateNotificationList( (String) evt.getNewValue(),"",NetworkController.getModelData().getLocalUser().getUser().getUsername());
		} 
	}

}
