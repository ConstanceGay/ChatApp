package tests;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;

import org.junit.Test;

import data.User;
import network.NetworkController;

public class TestFileTCP {
	
	NetworkController Cont;

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
		String pseudo="";
		boolean pseudo_ok = false;
		Scanner keyboard = new Scanner(System.in);
		
		//loop to choose valid username
		while(!pseudo_ok) {
			System.out.println("Entrez un pseudo ");
			pseudo = keyboard.nextLine();
			pseudo_ok=Cont.PerformConnectUDP(pseudo, 4445, 4445, 2000);
		}
		
		System.out.println("Bienvenue "+pseudo);
		afficherList(Cont.getModelData().getConnectedUsers(),pseudo);
		
		String other_pseudo="";
		boolean other_pseudo_ok = false;
		
		//loop to choose who to chat with
		while(!other_pseudo_ok) {
			System.out.println("Entrez un pseudo de personne à qui envoyer un message ");
			other_pseudo = keyboard.nextLine();
			other_pseudo_ok = isinList(Cont.getModelData().getConnectedUsers(),other_pseudo,pseudo);
		}
		
		String path;
		if(!other_pseudo.equals("DISCONNECT")) {
			System.out.println("Entrez un chemin ");
			path = keyboard.nextLine();
			Cont.sendFile(other_pseudo, path, 2000);
		}
		
		//disconnect
		keyboard.close();
		Cont.PerformDisconnectUDP(4445, 4445);
	}
	
}
