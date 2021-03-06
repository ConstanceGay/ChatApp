package tests;


import java.util.ArrayList;
import java.util.ListIterator;
import org.junit.Test;
import java.util.Scanner;
import data.*;
import network.*;

public class TestNetworkController {
	NetworkController Cont;
	
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
	
	public static void afficherList(ArrayList<User> list,String pseudo) {
		if(list.isEmpty() || list.equals(null)) {
			System.out.println("UserList vide");
		}else {
			ListIterator<User> i= list.listIterator();
			while(i.hasNext()) {
				User local=i.next();
				if(!local.getUsername().equals(pseudo)) {
					if(i.hasNext()) {
						System.out.print(local.getUsername()+" , ");
					} else {
						System.out.print(local.getUsername()+"\n");
					}
				}
			}
		}
		System.out.println();
	}
	
	public static boolean isinList(ArrayList<User> list,String pseudo,String localpseudo) {
		if(list.isEmpty() || list.equals(null) || pseudo.equals(localpseudo)) {
			return false;
		}else {
			ListIterator<User> i= list.listIterator();
			while(i.hasNext()) {
				User local=i.next();
				if(local.getUsername().equals(pseudo)) {
					return true;
				}
			}
			return false;
		}
	}
	
	@Test		
	public void test() {
		Cont = new NetworkController();
		String pseudo ="";
		boolean pseudo_ok = false;
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Entrez un pseudo ");

		//loop to choose a valid username
		while(!pseudo_ok) {
			pseudo = keyboard.nextLine();
			pseudo_ok= Cont.PerformConnectUDP(pseudo, 4445, 4445, 2000);
		}
		
		System.out.println("Bienvenue "+pseudo);
		afficherList(Cont.getModelData().getConnectedUsers(),pseudo);
		
		
		String other_pseudo = "";
		boolean other_pseudo_ok = false;
		boolean run = true;
		
		//loop to choose who to chat with
		while(!other_pseudo_ok && run) {
			System.out.println("Entrez un pseudo de personne � qui envoyer un message (DISCONNECT pour finir)");
			other_pseudo = keyboard.nextLine();
			other_pseudo_ok = isinList(Cont.getModelData().getConnectedUsers(),other_pseudo,pseudo);
			if(other_pseudo.equals("DISCONNECT")) {
				run = false;
			}
		}
		
		String message;
		while (run) {	
			System.out.println("Entrez un message (DISCONNECT pour finir la session) : ");
			message = keyboard.nextLine();
			if(message.equals("DISCONNECT")) {
				run = false;
			} else {
				Cont.sendMessage(other_pseudo, message, 2000);
			}
		}
		
		System.out.println("Appuyez sur entrer pour finir ");
		message = keyboard.nextLine();
		keyboard.close();
		
		//disconnect
		afficherSession(Cont.getModelData().getSession(other_pseudo));
		Cont.PerformDisconnectUDP(4445, 4445);
	}
	
}
