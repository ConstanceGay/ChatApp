package tests;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

//import com.sun.xml.internal.ws.api.message.Message;

import data.*;
import model.*;

public class TestBDD {
	
	@Test
	public void testBDD() {		
		//Connection conn;
	
	try {
		System.out.println("Begin");
		
		//Chargement du driver
		database.loadDriver();
		System.out.println("Driver OK");
		
		
		//TestAddSession(User)
		System.out.println("Test addSession(user)");
		InetAddress addrBatman=InetAddress.getLoopbackAddress();
		User batman=new User("Batman",addrBatman);
		database.addSession(batman);
		System.out.println("Insertion du user réussie");
		
		System.out.println();
		
		//TestAddSession(addrIP,nickname)
		System.out.println("Test addSession(addrIP,nickname)");
		database.addSession(InetAddress.getLocalHost(),"Superman");
		System.out.println("Insertion du user réussie");
		
		System.out.println();
		
		//TestAffichage
		System.out.println("Dans la BDD il y a:");
		ArrayList<String> addrIP=database.getAllUserIP();
		ArrayList<String> usernames=database.getAllUserName();
		
		int i;
		for(i=0;i<addrIP.size();i++) {
			System.out.println(usernames.get(i) + " : " + addrIP.get(i));
		}
		
		System.out.println();
		
		//Test getNickname
		System.out.println("Test getNickname(IP)");
		System.out.println(database.getNickname(addrBatman.toString()) + " : Batman?");
		
		System.out.println();
		
		//Test updateNickName
		System.out.println("Test UpdateNickname");
		database.updateNickname(addrBatman, "I am");
		System.out.println("update effectuée");
		System.out.println(database.getNickname(addrBatman.toString()) + " Batman"); //cette ligne là est problématique, why?
		System.out.println();
		
		//Test ajout doublon user
		System.out.println("On tente de réajouter Batman");
		database.addSession(addrBatman, "Le Joker");
		System.out.println(database.getNickname(addrBatman.toString()) + " vs Batman");
		
		System.out.println();
		
		//Test addMessage
		System.out.println("Ajout de deux messages venant de Batman..., enfin Le Joker");
		MessageChat msg=new MessageChat("Le Joker",new Date(),"je ne porte que du noir et des caractères spéciaux \\ '  ");
		database.addMessage(msg, addrBatman);
		System.out.println("Message1 reçu");
		
		database.addMessage(InetAddress.getLocalHost(), new Date(), "ou du gris très très foncé", "Superman");
		System.out.println("Message2 reçu");
		
		System.out.println();
		
		//Test ajoutMessage IP non existante
		/*
		System.out.println("Test ajout message d'une IP inexistante");
		database.addMessage(InetAddress.getByName("euh"), new Date(), "null", "personne");
		System.out.println("Si on est là ce n'est point normal");
		
		System.out.println();
		*/
		
		//Test getHistoric
		System.out.println("Les messages reçus sont : ");
		ArrayList<MessageChat> msglist = database.getHistoric(addrBatman);
		for(i=0;i<msglist.size();i++) {
			System.out.println(msglist.get(i).getAuthor() + " : " + msglist.get(i).getContent() + " reçu à " + msglist.get(i).getDate().toString());
		}
		
		System.out.println();
		
		//Test Update nickname
		System.out.println("Renommons Batman Batman");
		database.updateNickname(addrBatman, "Batman");
		System.out.println(database.getNickname(addrBatman.toString()));
		msglist = database.getHistoric(addrBatman);
		for(i=0;i<msglist.size();i++) {
			System.out.println(msglist.get(i).getAuthor() + " : " + msglist.get(i).getContent() + " reçu à " + msglist.get(i).getDate().toString());
		}
		
		System.out.println();
		
		//Test Update nickname in MessageChat
		System.out.println("Test de l'update sur les auteurs des messages");
		database.updateNickname(addrBatman, "Robin");
		ArrayList<MessageChat> messages=database.getHistoric(addrBatman);
		for(i=0;i<messages.size();i++) {
			System.out.println(messages.get(i).getAuthor() + " a dit: "  +messages.get(i).getContent());
		}
		
		System.out.println();
		
		//Test updateMyNickname 
		System.out.println("Test de updateMyNickname");
		database.addMessage(addrBatman, new Date(), "Ah bon, moi j aime bien la couleur", "Moi");
		database.addMessage(addrBatman, new Date(), "c est zoli la couleur", "Moi");
		messages=database.getHistoric(addrBatman);
		for(i=0;i<messages.size();i++) {
			System.out.println(messages.get(i).getAuthor() + " a dit: "  +messages.get(i).getContent());
		}
		database.updateMyNickname("Groot");
		messages=database.getHistoric(addrBatman);
		for(i=0;i<messages.size();i++) {
			System.out.println(messages.get(i).getAuthor() + " a dit: "  +messages.get(i).getContent());
		}
		
		System.out.println();
		
		//Test de ISINDATABASE
		System.out.println("Test de IsInDatabase");
		boolean a=database.isInDatabase(addrBatman);
		System.out.println("La réponse est " + a + " : true");
		a=database.isInDatabase(InetAddress.getByName(null));
		System.out.println("La réponse est " + a + " : false");
		
		System.out.println();
		
		//Fin des tests
		System.out.println("Fins des tests");
		System.out.println();
		
		
		}catch(Exception e){
		System.out.println(e);
		//e.printStackTrace();	
	}
	}
}
