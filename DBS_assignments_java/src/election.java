import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;
import net.sf.javaml.tools.data.FileHandler;

public class election {

	public static void main(String[] args) {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			System.exit(0);
		}
		
		try{
			
//			Einstellungen zur Verbindung
			
			String url = "jdbc:postgresql:election";
			Properties props = new Properties();
			props.setProperty("user","testuser");
			props.setProperty("password","testpass");
			props.setProperty("ssl","true");
			String[] vorhandeneHashtags = null;
			Connection conn = DriverManager.getConnection(url, props);
			System.out.println("Verbindung erfolgreich hergestellt.\nFolgende Möglichkeiten bestehen:\nbreak - Verbindung beenden\nplattmachen - DB leeren\nzeige - alle Tabellennamen anzeigen\nneu - Tabellen gemäß dem Schema neu anlegen\neinlesen - alle Tabellen generieren nach der .csv Datei (dauert etwas)\nEigene Abfragen gehen direkt in die DB.");
			
			while (true){
				
//				 dauerhafter Scanner, welcher Befehle erwartet und auswertet
				
				Scanner s = new Scanner(System.in);
				String input = s.nextLine();
				if(input.startsWith("break")){
					verbindungBeenden(conn);
					
//					 Möglichkeit, die Tabellen der DB zu leeren: (buggy)
				
					
				} else if (input.equals("test")){
					String string = "hallo";
//					System.out.println("hallo".charAt(0));
					String word = "";
					for(int i = 0; i < string.length(); i++){
						word = word + (int)(string.charAt(i)) + ",";
						System.out.println(string + string.charAt(i));
					}
					
					
					
					
					
				}else if(input.equals("plattmachen")){
					System.out.println("Wirklich alle Tabellen der DB unwiderruflich plattmachen? J/N");
					Scanner s1 = new Scanner(System.in);
					String inputSicherheit = s1.nextLine();
					if (inputSicherheit.equals("J")){
						tabellenLoeschen(conn);
					}
					
//					 Möglichkeit, die Tabellen der DB nach unserem Modell zu generieren:
					
				}else if(input.equals("neu")){
					PreparedStatement ps = conn.prepareStatement("CREATE TABLE hashtag (ID serial primary key, text VARCHAR(255) not null);"+
							"CREATE TABLE tweet (ID serial primary key, URL VARCHAR(255), text VARCHAR(255) not null, is_retweet boolean, favourite_count int, retweet_count int, zeit VARCHAR(255), original_verfasser VARCHAR(255), absender VARCHAR(255), antwort VARCHAR(255));"+
							"CREATE TABLE besitzt (tweet_ID int REFERENCES tweet(ID), hashtag_ID int REFERENCES hashtag(ID));"+
							"CREATE TABLE tritt_zusammen_auf (hashtagA int REFERENCES hashtag(ID), hashtagB int REFERENCES hashtag(ID));"
							);
					ps.executeUpdate();
					ps.close();
					
//					Möglichkeit, alle vorhandenen Tabellen auszugeben:
					
				}else if(input.equals("zeige")){
					DatabaseMetaData md = conn.getMetaData();
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery("SELECT relname from pg_stat_user_tables");
					while(rs.next()){
						System.out.println(rs.getString(1));
					}
					
//					Möglichkeit, die .csc Datei einzulesen und alle 
					
				}else if (input.equals("einlesen")){
					conn.setAutoCommit(false);
					String incsv = "";
					
					// liest file, generiert Liste<Tweets> und liefert diese zurück
					
					ArrayList<Tweet> alles = auslesen();
					
					ArrayList<String> hashtags = new ArrayList<String>();
					System.out.println("Datei eingelesen, Tweets werden nun generiert (dauert 'ne Weile)");
					for (Tweet tweet : alles){
						
//						zu jedem Tweet wird ein SQL-Insert-Befehl in die Tabelle "tweet" abgesetzt
						
						PreparedStatement pst = null;
						pst = conn.prepareStatement(tweet.insertString());
//						System.out.println(tweet.insertString());
						
//						wenn der Tweet hashtags besitzt, werden diese in die Liste "hashtags" hinzugefügt
						
						for (int i = 0; i < tweet.hashtags.size(); i++){
							hashtags.add(tweet.hashtags.get(i));
						}
						pst.executeUpdate();
						pst.close();
						conn.commit();
						
					}
					System.out.println("Tweets in DB geschrieben");
//					HashSet<String> count = new HashSet<String>();
//					count.addAll(hashtags);
					
					ArrayList<String> unique = new ArrayList<String>();
					
//					die Liste unique beinhaltet keine doppelten Strings, jeden hashtag genau einmal
					
					for (String element : hashtags){
						if (!unique.contains(element)){
							unique.add(element);
							incsv+= element + "\r\n";
						}												
					}
					
					
					
//					jeder hashtag wird in die DB geschrieben per SQL statement
					
					for (String element: unique){
						PreparedStatement pst = null;
						pst = conn.prepareStatement("INSERT INTO hashtag (text) VALUES ('"+element+"');");
//						System.out.println("hashtag" +element);
						pst.executeUpdate();
						pst.close();
						conn.commit();
					}
					
//					for (String element : unique) {
//						Instance tmp = new DenseInstance(1);
//						
//						
//						test.add(tmp);
//					}
					
					
					System.out.println("hashtags fertig");
					
					// TO-DO
//					Dataset test = new DefaultDataset();
					
					
					
//					
//					Clusterer km = new KMeans();
//					Dataset[] clusters = km.cluster(test);
//					
//				    
//					for (Dataset dataset : clusters) {
//						System.out.println(dataset);
//					}
//					
//					
					
					
					
					
					
					
					
					
					
					
					
					
					
					for (Tweet tweet : alles){
						
//						wenn ein Tweet mindestens einen hashtag besitzt, so wird die verknüpfung in der tabelle "besitzt" organisiert
						
						if (tweet.hashtags.size() > 0 ){
							for (String hashtag : tweet.hashtags){
								PreparedStatement pst = null;
								pst = conn.prepareStatement("INSERT INTO besitzt (hashtag_ID, tweet_id) select ht.ID,'"+tweet.ID+"' from hashtag ht where ht.text = '"+hashtag+"';");	
								pst.executeUpdate();
									pst.close();
									conn.commit();
							}
						}
						
//						wenn mehrere hashtags in einem Tweet auftauchen, werden diese in der Tabelle "tritt_zusammen_auf" persistiert
						
						if (tweet.hashtags.size() > 1){
							for (int i = 0; i < tweet.hashtags.size(); i++){
								for (int j = 0; j < tweet.hashtags.size(); j++){
									if (i != j){
										PreparedStatement pst = null;
										pst = conn.prepareStatement("INSERT INTO tritt_zusammen_auf (hashtagA, hashtagB) select ht1.ID, ht2.ID from "
												+ "hashtag ht1, hashtag ht2 where ht1.text = '" +tweet.hashtags.get(i)+
												"' and ht2.text = '"+tweet.hashtags.get(j)+"';");
										pst.executeUpdate();
										pst.close();
										conn.commit();
									}
								}
							}
						}
					}
					
					try(Writer writer = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream("/home/rob/Downloads/test3.csv"),"utf-8"))){
						String ObDuBehindertBist = "";
						ArrayList<String> alleHashs = new ArrayList<String>();
						
//						for (Tweet tweet : alles) {
//							ArrayList<String> hashtagsVonTweet = tweet.getHashtags();
//							alleHashs.addAll(hashtagsVonTweet);
//							
//						}
						for (String string : unique) {
							ObDuBehindertBist = ObDuBehindertBist + string +"," + string.length()+ "\r\n";
						}
						
						
						
//						for (String string : unique) {
////							ObDuBehindertBist = ObDuBehindertBist + string +"," + string.length()+ "\r\n";
//							String word = "";
//							
//								int counter = 0;
//								for (String element : hashtags) {
//									if(element == string){
//										counter++;
//									}
//								}
//								ObDuBehindertBist = ObDuBehindertBist + string +"," + string.length()+","+counter+ "\r\n";
//							
////							ObDuBehindertBist = ObDuBehindertBist + string +"," + word + "\r\n";
//						}
							writer.write(ObDuBehindertBist);
							System.out.println("hab geschrieben");
					}  catch(Exception e){
						System.err.println(e);
					}
					
					
					
					
					
					
					
					
					System.out.println("Alle Tabellen vollständig generiert.");
					
					
					conn.setAutoCommit(true);
					
//					normale SQL statements werden in die DB geleitet (ohne Validierung), select abfragen liefern ergebnisse aus der DB zurück
					
					
				}else{
					Statement stmt = conn.createStatement();
					if(input.toUpperCase().startsWith("SELECT")){
				        ResultSet rs = stmt.executeQuery(input);
				        ResultSetMetaData rsmd = rs.getMetaData();
				        int columnsNumber = rsmd.getColumnCount();
				        while (rs.next()) {
				        	for (int i = 1; i <= columnsNumber; i++) {
				        		System.out.print(rs.getRow());
				                if (i > 1) System.out.print(",  ");
				                String columnValue = rs.getString(i);
				                System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
				            }
				        	System.out.println("");
				        }
				        rs.close();
					}else{
						ResultSet rs = stmt.executeQuery(input);
						System.out.println("Abfrage liefert keine Ausgabe, weil sie kein SELECT ist. "
								+ "Ergebnisse wie eine Meldung über den erfolgreichen Eingang des Befehls gibts nicht.");
					}
				}
			}
		}catch(Exception e){
			System.out.println(e);
		}
	}
	public static void verbindungBeenden(Connection conn) throws SQLException{
		
//		beendet die Verbindung
		
		conn.close();
		System.out.println("Verbindung abgebrochen");
		System.exit(0);
	}
	
	public static void tabellenLoeschen(Connection conn) throws SQLException{
		boolean fertig = false;
		while(fertig = false){
			DatabaseMetaData md = conn.getMetaData();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT relname from pg_stat_user_tables");
			if (rs.wasNull()){
				fertig = true;
			}
			while(rs.next()){
				try{
					PreparedStatement ps = conn.prepareStatement("drop table besitzt; drop table tritt_zusammen_auf; drop table tweet; drop table hashtag;");
					ps.executeUpdate();
					ps.close();
					ps = conn.prepareStatement("drop table "+ rs.getString(1)+";");
					ps.executeUpdate();
					ps.close();
					System.out.println(rs.getString(1)+" platt gemacht.");
				}catch(org.postgresql.util.PSQLException e){
					System.err.println(e);
				}
			}
		}
	}
	
//	
//	public Dataset[] cluster(Dataset data){
//		
//		return null;
//	}
	
	
	
	
	
	public static ArrayList<Tweet> auslesen() throws Exception{
		
//		liest file ein, bildet einen langen String mit dem kompletten Inhalt
		
		File file = new File("/home/rob/Downloads/american-election-tweets(1).csv");
		ArrayList<Tweet> alleTweets = new ArrayList<Tweet>();
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fis.read(data);
		fis.close();

		String str = new String(data, "UTF-8");
		ArrayList<String> bullshit = new ArrayList<String>();
		
//		splittet den String an den Zeilenumbrüchen
		
		String[] test = str.split("\r\n");
		for(int i = 1; i < test.length; i++){
			bullshit.add(test[i]);
			
//			generiert neues Objekt des Datentypes Tweet
			
			alleTweets.add(new Tweet(test[i],i));
		}
		String ausgabe = "";
		for (Tweet sql : alleTweets){
			ausgabe += sql.insertString();
		}
		return alleTweets;
	}		
	
	public static void inDBwerfen(Connection conn, Tweet tweet) throws Exception{
		PreparedStatement pst = null;
		pst = conn.prepareStatement(tweet.insertString());
		pst.executeUpdate();
	}
	
	public static double diceCoefficientOptimized(String s, String t)
	{
		// Verifying the input:
		if (s == null || t == null)
			return 0;
		// Quick check to catch identical objects:
		if (s == t)
			return 1;
	        // avoid exception for single character searches
	        if (s.length() < 2 || t.length() < 2)
	            return 0;

		// Create the bigrams for string s:
		final int n = s.length()-1;
		final int[] sPairs = new int[n];
		for (int i = 0; i <= n; i++)
			if (i == 0)
				sPairs[i] = s.charAt(i) << 16;
			else if (i == n)
				sPairs[i-1] |= s.charAt(i);
			else
				sPairs[i] = (sPairs[i-1] |= s.charAt(i)) << 16;

		// Create the bigrams for string t:
		final int m = t.length()-1;
		final int[] tPairs = new int[m];
		for (int i = 0; i <= m; i++)
			if (i == 0)
				tPairs[i] = t.charAt(i) << 16;
			else if (i == m)
				tPairs[i-1] |= t.charAt(i);
			else
				tPairs[i] = (tPairs[i-1] |= t.charAt(i)) << 16;

		// Sort the bigram lists:
		Arrays.sort(sPairs);
		Arrays.sort(tPairs);

		// Count the matches:
		int matches = 0, i = 0, j = 0;
		while (i < n && j < m)
		{
			if (sPairs[i] == tPairs[j])
			{
				matches += 2;
				i++;
				j++;
			}
			else if (sPairs[i] < tPairs[j])
				i++;
			else
				j++;
		}
		return (double)matches/(n+m);
	}
}
