package tests;

import java.util.ArrayList;
import java.util.ListIterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import data.*;
import network.*;

public class TestTCP {
	NetworkController Cont1;
	NetworkController Cont3;
	
	public static void afficherSession(ArrayList<MessageChat> list) {
		if(list.isEmpty() || list.equals(null)) {
			System.out.println("Historique vide");
		}else {
			System.out.println("Il y a "+list.size()+" message(s)");
			ListIterator<MessageChat> j= list.listIterator();
			while(j.hasNext()) {
				MessageChat localm = j.next();
				System.out.print("Author: "+localm.getAuthor());
				System.out.print(" | Date: "+localm.getDate());
				System.out.print(" | Content: "+localm.getContent()+"\n");
			}
		}
	}
	
	public static void afficherList(ArrayList<User> list) {
		if(list.isEmpty() || list.equals(null)) {
			System.out.println("UserList vide");
		}else {
			//on cherche si le pseudo est déja dans la liste
			ListIterator<User> i= list.listIterator();
			while(i.hasNext()) {
				User local=i.next();
				if(i.hasNext()) {
					System.out.print(local.getUsername()+" , ");
				} else {
					System.out.print(local.getUsername()+"\n");
				}
			}
		}
	}
	
	@Before
	public void setup() {
		//On crée un utilisateur
		Cont1 = new NetworkController();
		if (!Cont1.PerformConnectUDP("Bob",4445,4446,2000)) {
			System.out.println("La connection du premier Controller a raté");
		} else {
			System.out.println("La connection du premier Controller, Bob, a reussi");
		}
	}
	
	@Test		
	public void test() {
	Cont3 = new NetworkController();
	boolean success= Cont3.PerformConnectUDP("Claude",4446,4445,2001);
	System.out.println("Claude a pu se connecter: "+success);
	User User1 = Cont1.getModelData().getLocalUser().getUser();
	User User3 = Cont3.getModelData().getLocalUser().getUser();
	System.out.println("On a donc: "+ User1.getUsername()+"    Adresse: "+User1.getAddr().toString());
	System.out.println("           "+ User3.getUsername()+" Adresse: "+User3.getAddr().toString());
	
	System.out.println("-------------------------------------------------------------");
		//Bob envoit un message à Claude
	Cont1.sendMessage("Claude", "Hey", 2001);

	System.out.println("Historique de Bob avec Claude");
	afficherSession(Cont1.getModelData().getSession("Claude"));
		
	System.out.println("\nHistorique de Claude avec Bob");
	afficherSession(Cont3.getModelData().getSession("Bob"));
	System.out.println("--------------------------------------------------------------");
	//Claude envoi un message à Bob
	Cont3.sendMessage("Bob", "Hello Back", 2000);
	
	System.out.println("Historique de Bob avec Claude");
	afficherSession(Cont1.getModelData().getSession("Claude"));
		
	System.out.println("\nHistorique de Claude avec Bob");
	afficherSession(Cont3.getModelData().getSession("Bob"));
	
	}
	
	@After
	public void tearDown() {
		//on arrête les deux threads ServerUDP
		UDPClient client = new UDPClient() ;
		client.sendPseudo(Cont1.getModelData().getConnectedUsers(),"end",4445);
		client.sendPseudo(Cont3.getModelData().getConnectedUsers(),"end",4446);
		client.close();
	}

}
