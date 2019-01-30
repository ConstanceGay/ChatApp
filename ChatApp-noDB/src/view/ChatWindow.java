package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import data.MessageChat;
import data.Session;
import data.User;

import java.awt.*;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ListIterator;


public class ChatWindow extends Window {
	private static final long serialVersionUID = 1L;
	
    private JPanel chatWindowPanel; //global panel
    
    protected JList<String> OnlineUserList;	//Online users list
    protected JList<String> OfflineUserList;	//Offline users list
    protected JList<String> NotificationList;	//Notificationslist
    
    protected JButton chatFileButton;	//button to send file
    protected JButton ChangePseudoButton; //bouton pour changer de pseudo 
    
    protected JTextField changePseudoArea;	//field to enter new username
    protected JTextField chatTypeTextArea;	//field to enter message  
    protected JTextArea affichageHistorique; //field to show messages
    
    private JScrollPane HistoriqueScroll;	//Scrollpane for message list
    private JScrollPane OnlineUserScroll;	//Scrollpane for online users
    private JScrollPane OfflineUserScroll;	//Scrollpane for offline users
    private JScrollPane NotificationScroll; //Scrollpane for notifications
    
    public ChatWindow() {
        OnlineUserList.setModel(new DefaultListModel<String>());
        OfflineUserList.setModel(new DefaultListModel<String>());
        NotificationList.setModel(new DefaultListModel<String>());
        add(chatWindowPanel);
    }
    
    //adds a notification to the list
    public void UpdateNotificationList(String notif,String pseudo,String localpseudo) {
    	DefaultListModel<String> notifModel = (DefaultListModel<String>) NotificationList.getModel();
    	String selectedValue = OnlineUserList.getSelectedValue();
    	String lastline;
    	if(!notifModel.isEmpty()) {
    		lastline = notifModel.lastElement();
    	} else {
    		lastline ="";
    	}
    	
    	if(!(selectedValue==null)) {
	    	if (selectedValue.equals(localpseudo+" (Moi)")) { //checks if the person selected is the localuser
	    		selectedValue = localpseudo;
	    	}
	    	if(!selectedValue.equals(pseudo) && !notif.equals(lastline)){ //checks if the notification isn't from the person we're already chatting with
	    		notifModel.addElement(notif);		//adds the notification to the list
	        	NotificationList.setModel(notifModel);
	        	NotificationScroll.setViewportView(NotificationList);
	            JScrollBar vertical = NotificationScroll.getVerticalScrollBar();
	     		vertical.setValue( vertical.getMaximum());
	    	}
    	} else if (!notif.equals(lastline)){
    		notifModel.addElement(notif);		//adds the notification to the list
        	NotificationList.setModel(notifModel);
        	NotificationScroll.setViewportView(NotificationList);
            JScrollBar vertical = NotificationScroll.getVerticalScrollBar();
     		vertical.setValue( vertical.getMaximum());
    	}
    }
   
    //Updates the list views of online/offline users
    public void UpdateConnectedUsers (ArrayList<User> Onlinelist, String localpseudo,ArrayList<Session> listSession){
    	DefaultListModel<String> listModelOnline = new DefaultListModel<String>();
    	DefaultListModel<String> listModelOffline = new DefaultListModel<String>();
    	ArrayList<InetAddress> listOnlineAddr = new ArrayList<InetAddress>();
    	ListIterator<User> i= Onlinelist.listIterator();
 		while(i.hasNext()) {
 			User local=i.next();
 			listOnlineAddr.add(local.getAddr());
 			if(local.getUsername().equals(localpseudo)) {
 				listModelOnline.addElement(local.getUsername()+" (Moi)");
 			}else {
 				listModelOnline.addElement(local.getUsername());
 				
 			}
 		}
        OnlineUserList.setModel(listModelOnline);
        OnlineUserScroll.setViewportView(OnlineUserList);
        JScrollBar vertical = OnlineUserScroll.getVerticalScrollBar();
 		vertical.setValue( vertical.getMinimum());
 		
 		ListIterator<Session> j= listSession.listIterator();
 		while(j.hasNext()) {
 			Session local=j.next();
 			if(!listOnlineAddr.contains(local.getOtherUserAddress())) {
 				listModelOffline.addElement(local.getOtherUserAddress().toString()); 			
 			}
 		}
 		OfflineUserList.setModel(listModelOffline);
        OfflineUserScroll.setViewportView(OfflineUserList);
        JScrollBar vertical2 = OfflineUserScroll.getVerticalScrollBar();
  		vertical2.setValue(vertical2.getMinimum());
    }
    
    //Updates the conversation with the new messages
    public void UpdateHistorique(ArrayList<MessageChat> messageList){
    	affichageHistorique.setText("");
    	JTextArea n = new JTextArea();
        TitledBorder titleblanc = BorderFactory.createTitledBorder("Messages");
        titleblanc.setTitleColor(Color.WHITE);
        n.setBorder(titleblanc);
        n.setLineWrap(true);
        n.setWrapStyleWord(true);
        n.setEditable(false);
        n.setBackground(new Color(-12236470));
        n.setSelectedTextColor(Color.white);
        n.setForeground(Color.white);
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    	ListIterator<MessageChat> i= messageList.listIterator();
 		while(i.hasNext()) {
 			MessageChat local=i.next();
 			n.append(local.getAuthor()+" ("+sdf.format(local.getDate())+") : "+local.getContent()+"\n");
 		}
 		HistoriqueScroll.add(n);
 		HistoriqueScroll.setViewportView(n);
 		JScrollBar vertical = HistoriqueScroll.getVerticalScrollBar();
 		vertical.setValue( vertical.getMaximum() );
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    private void $$$setupUI$$$() {
   
    	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    	int width = (int) (gd.getDisplayMode().getWidth() * 0.6);
    	//int height = (int) (gd.getDisplayMode().getHeight() * 0.2);
    	
    	//int width = 700;
    	int height =(int)(width * 0.7);
    	setSize(width,height);
    	setLocationRelativeTo(null);
    	
        chatWindowPanel = new JPanel();
        chatWindowPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
             
        //PANEL EN HAUT A GAUCHE
        TitledBorder title = BorderFactory.createTitledBorder("Online Users");
        title.setTitleColor(Color.GREEN);
        OnlineUserList = new JList<String>();
        OnlineUserList.setBorder(title);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.WEST;
        OnlineUserScroll = new JScrollPane(OnlineUserList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        OnlineUserScroll.setPreferredSize(new Dimension((int)(width*0.4),(int)((height*0.57)/3)));
        chatWindowPanel.add(OnlineUserScroll, gbc);
        
      //PANEL EN HAUT A GAUCHE
        TitledBorder title2 = BorderFactory.createTitledBorder("Offline Users");
        title2.setTitleColor(Color.RED);
        OfflineUserList = new JList<String>();
        OfflineUserList.setBorder(title2);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        OfflineUserScroll = new JScrollPane(OfflineUserList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        OfflineUserScroll.setPreferredSize(new Dimension((int)(width*0.4),(int)((height*0.57)/3)));
        chatWindowPanel.add(OfflineUserScroll, gbc);
        
        TitledBorder title3 = BorderFactory.createTitledBorder("Notification");
        title3.setTitleColor(Color.blue);
        NotificationList = new JList<String>();
        NotificationList.setBorder(title3);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        NotificationScroll = new JScrollPane(NotificationList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        NotificationScroll.setPreferredSize(new Dimension((int)(width*0.4),(int)((height*0.57)/3)));
        chatWindowPanel.add(NotificationScroll, gbc);
        
        
        //PANEL EN BAS A GAUCHE
        changePseudoArea = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.ipadx =(int)(width*0.187);
        gbc.anchor = GridBagConstraints.BELOW_BASELINE_LEADING;
        chatWindowPanel.add(changePseudoArea, gbc);
        
        ChangePseudoButton = new JButton();
        ChangePseudoButton.setText("Change Login");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.BELOW_BASELINE_LEADING;
        chatWindowPanel.add(ChangePseudoButton, gbc);
        
        //PANEL EN HAUT A DROITE       
        affichageHistorique = new JTextArea();
        TitledBorder titleblanc = BorderFactory.createTitledBorder("Messages");
        titleblanc.setTitleColor(Color.WHITE);
        affichageHistorique.setBorder(titleblanc);
        affichageHistorique.setLineWrap(true);
        affichageHistorique.setWrapStyleWord(true);
        affichageHistorique.setEditable(false);
        affichageHistorique.setBackground(new Color(-12236470));
        affichageHistorique.setSelectedTextColor(Color.white);
        affichageHistorique.setForeground(Color.white);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        HistoriqueScroll = new JScrollPane(affichageHistorique,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        HistoriqueScroll.setPreferredSize(new Dimension((int)(width*0.4),(int)(height*0.57)));
        chatWindowPanel.add(HistoriqueScroll, gbc);
        
        chatTypeTextArea = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        chatWindowPanel.add(chatTypeTextArea, gbc);
        
        chatFileButton = new JButton();
        chatFileButton.setText("File");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        chatWindowPanel.add(chatFileButton, gbc);    
        
    }

    public JComponent $$$getRootComponent$$$() {
        return chatWindowPanel;
    }

}
