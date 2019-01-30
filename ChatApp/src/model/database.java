package model;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.Date;

import data.*;

public class database {
	private static String driverName = "org.postgresql.Driver";
	private static String url = "jdbc:postgresql://localhost:5432/Clavardage";
	private static String user = "ClavardageUser";
	private static String password = "SECRET";
	
	
	//à appeler à l'initialisation
	public static void loadDriver() throws ClassNotFoundException {
		Class.forName(driverName);
	}
	
	//crée une connection à la base de données définies par ses attributs
	private static Connection newConnection() throws SQLException {
		Connection conn = DriverManager.getConnection(url,user,password);
		return conn;
	}

	//GETTER
	//Renvoie les IP contenues dans la BDD selon le formalisme de InetAddress.toString
	public static ArrayList<String> getAllUserIP() throws SQLException{
		Connection conn=null;
		ArrayList<String> result = new ArrayList<String>();;
		try {
			conn = newConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT \"IP\" FROM public.\"Session\"; ");
			
			while (rs.next()) {
				result.add( rs.getString("IP"));
			}
			rs.close();
			st.close();
			return result;
		}finally {
			if (conn!=null) conn.close();
		}
	}
	
	//Renvoie tous les Username des personnes contenues dans la BDD
	public static ArrayList<String> getAllUserName() throws SQLException{
		Connection conn=null;
		ArrayList<String> result = new ArrayList<String>();;
		try {
			conn = newConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT nickname FROM public.\"Session\";");
			
			while (rs.next()) {
				result.add( rs.getString("nickname"));
			}
			rs.close();
			st.close();
			return result;
		}finally {
			if (conn!=null) conn.close();
		}
	}
	
	//Permet d'obtenir tous les messages chats échangés (dans les deux sens de la communication) avec le user d'adresse IP addrIP
	//Throws Unknown host exception si addrIP inconnue à la base
	public static ArrayList<MessageChat> getHistoric(InetAddress addrIP) throws SQLException{
		Connection conn=null;
		ArrayList<MessageChat> result = new ArrayList<MessageChat>();;
		try {
			conn = newConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT author,date,content FROM public.\"MessageChat\" WHERE \"IP\"='" + addrIP.toString()+"' ORDER BY id;");

			while (rs.next()) {
				result.add(new MessageChat(  rs.getString("author") ,new Date(rs.getTimestamp("date").getTime()) , rs.getString("content") ));
			}
			rs.close();
			st.close();
			return result;
		}finally {
			if (conn!=null) conn.close();
		}
	}
	
	//Permet d'obtenir le nickname associé à une addresse IP
	// /!\ le format doit respecter le formalisme de InetAddress.tostring()
	public static String getNickname(String addrIP) throws SQLException{
		Connection conn=null;
		try {
			conn= newConnection();
			
			Statement st = conn.createStatement();			
			ResultSet rs = st.executeQuery("SELECT nickname FROM public.\"Session\" WHERE \"IP\" ='" + addrIP.replace("'", "''") + "';");
			String result="";
			while(rs.next()) 
				result =  rs.getString("nickname");
			
			rs.close();
			st.close();
			return result;
		}finally {
			if (conn!= null) conn.close();
		}
	}
	
	//ADDER
	
	//Ajoute une session dans la BDD
	public static void addSession (User user) throws SQLException{
		Connection conn=null;
		try {
			conn = newConnection();
			Statement st = conn.createStatement();
			
			ResultSet rs = st.executeQuery("SELECT Count(*) FROM public.\"Session\" WHERE \"IP\"='" + user.getAddr().toString() + "';");
			rs.next();
			
			if(rs.getInt("count") == 0) {
				st.executeUpdate(
						"INSERT INTO public.\"Session\"(\"IP\",nickname) " +
						"VALUES ('" + user.getAddr().toString() + "', '" + user.getUsername().replace("'", "''") + "');"
						);
			}
			else {
				updateNickname(user.getAddr(),user.getUsername());
			}
			rs.close();
			st.close();
		}finally {
			if (conn!=null) conn.close();
		}		
	}
	
	//Ajoute une session dans la BDD
	public static void addSession (InetAddress addrIP,String name) throws SQLException{
		Connection conn=null;
		try {
			conn = newConnection();
			Statement st = conn.createStatement();
			
			ResultSet rs = st.executeQuery("SELECT Count(*) FROM public.\"Session\" WHERE \"IP\"='" + addrIP.toString() + "';");
			rs.next();
			
			if(rs.getInt("count") == 0) {
				st.executeUpdate(
						"INSERT INTO public.\"Session\"(\"IP\",nickname) " +
						"VALUES ('" + addrIP.toString() + "', '" + name.replace("'", "''") + "')"
						);
			}
			else {
				updateNickname(addrIP,name);
			}
			rs.close();
			st.close();
		}finally {
			if (conn!=null) conn.close();
		}		
	}
	
	//Ajoute un message à la BDD. L'IP doit correspondre à la personne distante, pas l'auteur du message.
	//Throws exception si addrIP pas déjà présente dans la BDD
	public static void addMessage(InetAddress addrIP,Date date,String content,String author) throws SQLException{
		Connection conn=null;
		try {
			conn = newConnection();
     		Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT Count(*) FROM public.\"MessageChat\";");
			rs.next();
			int id=rs.getInt("count")+1;
			st.executeUpdate(
					"INSERT INTO public.\"MessageChat\"(id,date,content,author,\"IP\") " +
					"VALUES (" + id + ",'" + new Timestamp(date.getTime()) + "','"+ content.replace("'", "''") + "','"+ author.replace("'", "''") +"','" + addrIP.toString() + "');"
					);
			rs.close();
			st.close();
			if (conn!=null) conn.close();
		}catch (Exception e) {
			System.out.println(e);
		}
	}
	
	//Ajoute un message à la BDD. L'IP doit correspondre à la personne distante, pas l'auteur du message.
	//Throws exception si addrIP pas déjà présente dans la BDD
	public static void addMessage(MessageChat msg,InetAddress addrIP) throws SQLException{
		Connection conn=null;
		Statement st=null;
		ResultSet rs=null;
		try {
			conn = newConnection();
			st = conn.createStatement();
			rs = st.executeQuery("SELECT Count(*) FROM public.\"MessageChat\"");
			rs.next();
			int id=rs.getInt("count") +1;
			st.executeUpdate(
					"INSERT INTO public.\"MessageChat\"(id,date,content,author,\"IP\") " +
					"VALUES (" + id + ",'" + new Timestamp(msg.getDate().getTime()) + "','"+ msg.getContent().replace("'", "''") + "','" + msg.getAuthor().replace("'", "''") + "','"+ addrIP.toString() + "');"
					);
			rs.close();
			st.close();
			if (conn!=null) conn.close();
		}catch(Exception e) {
				System.out.println(e);
		}		finally {
			rs.close();
			st.close();
		}
	}
	
	//SETTER
	
	public static void updateNickname(InetAddress addrIP, String newName) throws SQLException{
		Connection conn=null;
		try {
			conn = newConnection();
			Statement st = conn.createStatement();
			st.executeUpdate(
					"UPDATE public.\"MessageChat\""+
					"SET author='" + newName.replace("'", "''") +"'" +
					"WHERE \"IP\"='"+ addrIP.toString() +"' and author='" + database.getNickname(addrIP.toString()) + "';" 
					);
			
			st.executeUpdate(
					"UPDATE public.\"Session\"" +
					"SET nickname = '" + newName.replace("'", "''") + "'" +
					"WHERE \"IP\" ='" + addrIP.toString() +"';"
					);
			st.close();
		}finally {
			if (conn!=null) conn.close();
		}
	}
	
	public static void updateMyNickname(String newName) throws SQLException{
		Connection conn=null;
		try {
			conn = newConnection();
			Statement st = conn.createStatement();
			st.executeUpdate(
					"UPDATE public.\"MessageChat\" as mc "+
					"SET author='" + newName.replace("'", "''") +"' " +
					"FROM public.\"Session\" AS ss " +
					"WHERE ss.\"IP\"=mc.\"IP\" and author != ss.nickname;" 
					);
			st.close();
		}finally {
			if (conn!=null) conn.close();
		}
	}
	
	//OTHER
	public static boolean isInDatabase(InetAddress IP) throws SQLException{
		Connection conn=null;
		try {
			conn= newConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(
					"Select Count(*) " +
					"FROM public.\"Session\" as ss " +
					"WHERE ss.\"IP\" ='" + IP.toString() +"';"
					);
			int a=0;
			while(rs.next()) {
				a=rs.getInt(1);
			}
			rs.close();
			st.close();
			return(a!=0);
		}finally {
			if (conn!=null) conn.close();
		}
	}
}
