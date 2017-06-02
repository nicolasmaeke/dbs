import java.util.ArrayList;
import java.util.HashSet;

public class Tweet {
	int ID;
	String URL;
	String text;
	boolean is_retweet;
	int favourite_count;
	int retweet_count;
	String date;
	String originalVerfasser;
	String absender;
	String antwort;
	ArrayList<String> hashtags;
	
	public Tweet(String zeile, int ID) {
		super();
		this.ID = ID;
		zeile = zeile.replaceAll("'", "´");
		
		String[] parts = zeile.split(";");
		
		int max = parts.length-1;
		this.absender = parts[0];
		this.favourite_count = Integer.parseInt(parts[max-2]);
		this.retweet_count = Integer.parseInt(parts[max-3]);
		this.antwort = parts[max-5];
		this.date = parts[max-6];
		this.originalVerfasser = parts[max-7];
		this.URL = parts[max-1];
		this.is_retweet = Boolean.parseBoolean(parts[max-8]);
		String test = " ";
		for (int i = 1; i < max-8;i++){
			test += parts[i];
		}
		this.text = test;
		this.hashtags = getHashtags();
		
	}
	public String insertString(){
		
//		bildet den INSERT-INTO string, welcher das Objekt in die DB leiten kann
		
		return "INSERT INTO tweet (URL, text, is_retweet, favourite_count, retweet_count, zeit, original_verfasser, absender, antwort) VALUES "
				+ "('"+ this.URL + "', '"+ this.text+"', "+this.is_retweet+", "+this.favourite_count+", "+this.retweet_count+", '"+this.date+"', '"+this.originalVerfasser+"', '"+this.absender+"', '"+this.antwort+"');";
	}
	
	public ArrayList<String> getHashtags(){
		
//		findet im "text" heraus, ob und welche hashtags enthalten sind
		
		ArrayList<String> tags = new ArrayList<>();
		  String string = this.text;

		  HashSet<Integer> coolCharIndexes = new HashSet<>();
		  HashSet<Character> uncoolChars = new HashSet<>();
		  String coolCharString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789#";
		  for (char character : coolCharString.toCharArray()) {
		   coolCharIndexes.add(new Integer(character));
		  }
		  

		  //signs form NUL to ""
		  for (int i=0; i< 127 ; i++){
		   if(coolCharIndexes.contains(i) ==false){

		     uncoolChars.add((char)i);

		   }
		  }
		  
		  for(char uncoolChar : uncoolChars){
//		   System.out.println((int) uncoolChar);
		   
		   string = string.replace(uncoolChar, ' ');
		  }
		  
		  string.replaceAll("\t"," ");
		  string = string.replaceAll("\n"," ");

		  for(String word :string.trim().split(" ")){
		   if(word.contains("#") && word.startsWith("#")){
		    tags.add(word.toLowerCase());
		   }
		  }
//		  tags.forEach(tag -> System.out.println(tag));
		 
		  return tags;
	}
	
	public void ausgeben(){
		System.out.println("Absender: "+this.absender);
		System.out.println("Text: "+this.text);
		System.out.println("is_retweet: "+this.is_retweet);
		System.out.println("originalVerfasser: "+this.originalVerfasser);
		System.out.println("date: "+this.date);
		System.out.println("antwort: "+this.antwort);
		System.out.println("retweet_count: "+this.retweet_count);
		System.out.println("favourite_count: "+this.favourite_count);
		System.out.println("URL: "+this.URL);
	}
	
	
	
}
