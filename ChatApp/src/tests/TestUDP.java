package tests;

import java.util.ArrayList;
import java.util.ListIterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import data.*;
import network.*;

public class TestUDP {
	NetworkController Cont1;
	NetworkController Cont2;
	NetworkController Cont3;
	
	public static void afficherList(ArrayList<User> list) {
		if(list.isEmpty() || list.equals(null)) {
			System.out.println("vide");
		}else {
			//on cherche si le pseudo est d�ja dans la liste
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
		//On cr�e un utilisateur
		Cont1 = new NetworkController();
		if (!Cont1.PerformConnectUDP("Bob",4445,4446,2000)) {
			System.out.println("La connection du premier Controller a rat�");
		} else {
			System.out.println("La connection du premier Controller, Bob, a reussi");
		}
	}
	
	@Test		
	public void test() {
		Cont2= new NetworkController();
		Cont3= new NetworkController();
		//TEST 1
		System.out.println("\nTest 1: On tente de se connecter avec un pseudo d�j� utilis�");
		boolean success= Cont2.PerformConnectUDP("Bob",4446,4445,2001);
		System.out.println("R�ponse attendu: false \nR�ponse obtenue: "+success);
		
		//TEST 2
		System.out.println("\nTest 2: On tente de se connecter avec un pseudo valide");
		success= Cont3.PerformConnectUDP("Claude",4446,4445,2001);
		System.out.println("R�ponse attendu: true \nR�ponse obtenue: "+success);
		System.out.print("La liste des utilisateurs connect�s de Cont1 est: ");
		afficherList(Cont1.getModelData().getConnectedUsers());
		System.out.print("La liste des utilisateurs connect�s de Cont3 est: ");
		afficherList(Cont3.getModelData().getConnectedUsers());
		
		//TEST 3
		System.out.println("\nTest 3: On tente de deconnecter Cont3");
		success= Cont3.PerformDisconnectUDP(4446,4445);
		System.out.println("R�ponse attendu: true \nR�ponse obtenue: "+success);
		System.out.println("La liste des utilisateurs connect�s de Cont1 est: ");
		afficherList(Cont1.getModelData().getConnectedUsers());
		System.out.println("La liste des utilisateurs connect�s de Cont3 est: ");
		afficherList(Cont3.getModelData().getConnectedUsers());
	}
	
	@After
	public void tearDown() {
		//on arr�te les deux threads
		UDPClient client = new UDPClient() ;
		client.sendPseudo(Cont2.getModelData().getConnectedUsers(),"end",4445);
		client.close();
	}

}
