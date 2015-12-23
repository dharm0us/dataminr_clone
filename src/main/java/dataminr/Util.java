package dataminr;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.arabidopsis.ahocorasick.AhoCorasick;

import twitter4j.Status;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.UserMentionEntity;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class Util {
	
	public static AhoCorasick getCompanyNamesTree() throws FileNotFoundException, IOException {
		 AhoCorasick tree = new AhoCorasick();
		String file = "C:\\company_names.txt";
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String companyName;
		    while ((companyName = br.readLine()) != null) {
		    	companyName = companyName.trim().toLowerCase();
		    	if(companyName.length() > 5) {
			       tree.add(companyName.getBytes(), companyName);
		    	}
		    }
		}
	       tree.prepare();
	       return tree;
	       /*
	        * nike time sprint harris imation unit versar skyline 'under armour' graham
	        */
	}
	
	private static void processCompanyName(String companyName,	Map<String, Set<String>> partialNameToCompanyNames) {
			String[] tokens = splitOnWhitespace(companyName);
			String runningName = "";
			for(int i = 0; i< tokens.length; i++) {
				runningName = runningName + " "+tokens[i];
				runningName = runningName.trim();
				Set<String> companyNames = new HashSet<String>();
				if(partialNameToCompanyNames.containsKey(runningName)) {
					companyNames = partialNameToCompanyNames.get(runningName);
				}
				companyNames.add(companyName);
				partialNameToCompanyNames.put(runningName, companyNames);
			}
	}

	public static String cleanText(Status status) {
		String text = cleanText(status.getText());
		UserMentionEntity[] userMentionEntities = status.getUserMentionEntities();
		for (UserMentionEntity ume : userMentionEntities) {
			text = text.replaceAll("@"+ume.getScreenName().toLowerCase()," ");
		}
		return text;
	}
	
	public static String cleanText(String text) {
		return text.replaceAll("[^\\x00-\\x7F]", "").toLowerCase();// remove non ascii
	}
	
	public static String[] splitOnWhitespace(String inputString) {
		 return inputString.split("[\\p{Punct}\\s]+");
	}
	
	public static CRFClassifier<CoreLabel> getClassifier() throws ClassCastException, ClassNotFoundException, IOException {
		String serializedClassifier = "D:\\classifiers\\english.all.7class.distsim.crf.ser.gz";
		return CRFClassifier.getClassifier(serializedClassifier);
	}
	
	public static Map<String,String> getWordToEventType() {
		Map<String,String> wordToEventType = new HashMap<String,String>();
		wordToEventType = new HashMap<String, String>();
		wordToEventType.put("tornadoe", "tornadoe");
		wordToEventType.put("tornadoed", "tornadoe");
		wordToEventType.put("tornadoes", "tornadoe");
		wordToEventType.put("eruption", "eruption");
		wordToEventType.put("eruptions", "eruption");
		wordToEventType.put("erupting", "eruption");
		wordToEventType.put("erupted", "eruption");
		wordToEventType.put("blizzard", "blizzard");
		wordToEventType.put("blizzards", "blizzard");
		wordToEventType.put("cyclone", "cyclone");
		wordToEventType.put("cyclones", "cyclone");
		wordToEventType.put("hailstorm", "hailstorm");
		wordToEventType.put("hailstorms", "hailstorm");
		wordToEventType.put("earthquake", "earthquake");
		wordToEventType.put("earthquakes", "earthquake");
		wordToEventType.put("tsunami", "tsunami");
		wordToEventType.put("tsunamis", "tsunami");
		wordToEventType.put("explosion", "explosion");
		wordToEventType.put("explosions", "explosion");
		wordToEventType.put("exploding", "explosion");
		wordToEventType.put("exploded", "explosion");
		wordToEventType.put("explode", "explosion");
		wordToEventType.put("crash", "crash");
		wordToEventType.put("crashes", "crash");
		wordToEventType.put("crashed", "crash");
		wordToEventType.put("crashing", "crash");
		wordToEventType.put("derailment", "derailment");
		wordToEventType.put("derailed", "derailment");
		wordToEventType.put("derailing", "derailment");
		wordToEventType.put("capsize", "capsize");
		wordToEventType.put("capsized", "capsize");
		wordToEventType.put("capsizes", "capsize");
		wordToEventType.put("capsizing", "capsize");
		wordToEventType.put("flood", "flood");
		wordToEventType.put("floods", "flood");
		wordToEventType.put("flooding", "flood");
		wordToEventType.put("flooded", "flood");
		wordToEventType.put("fire", "fire");
		wordToEventType.put("sink", "sinking");
		wordToEventType.put("sank", "sinking");
		wordToEventType.put("sunk", "sinking");
		wordToEventType.put("sinking", "sinking");
		wordToEventType.put("radiation", "radiation");
		wordToEventType.put("radiotherapy", "radiotherapy");
		wordToEventType.put("smog", "smog");
		wordToEventType.put("riot", "riot");
		wordToEventType.put("rioting", "riot");
		wordToEventType.put("riots", "riot");
		wordToEventType.put("collapse", "collapse");
		wordToEventType.put("collapsing", "collapse");
		wordToEventType.put("collapses", "collapse");
		wordToEventType.put("collapsed", "collapse");
		//wordToEventType.put("crush", "crush");
		//wordToEventType.put("crushes", "crush");
		//wordToEventType.put("crushed", "crush");
		//wordToEventType.put("crushing", "crush");
		wordToEventType.put("stampede", "stampede");
		wordToEventType.put("stampedes", "stampede");
	//	wordToEventType.put("disaster", "disaster");
		//wordToEventType.put("disasters", "disaster");
		wordToEventType.put("storm", "storm");
		wordToEventType.put("storms", "storm");
		wordToEventType.put("stormed", "attack");
		wordToEventType.put("storming", "storm");
		wordToEventType.put("plunge", "plunge");
		wordToEventType.put("plunged", "plunge");
		wordToEventType.put("plunging", "plunge");
		wordToEventType.put("paniced", "panic");
		wordToEventType.put("panic", "panic");
		wordToEventType.put("train disaster", "train disaster");
		wordToEventType.put("rail disaster", "train disaster");

		/*wordToEventType.put("accident", "accident");
		wordToEventType.put("accidents", "accident");
		wordToEventType.put("shooting", "shooting");
		wordToEventType.put("shootout", "shooting");
		wordToEventType.put("kill", "killings");
		wordToEventType.put("killing", "killings");
		wordToEventType.put("kills", "killings");
		wordToEventType.put("killed", "killings");
		wordToEventType.put("killings", "killings");
		wordToEventType.put("death", "deaths");
		wordToEventType.put("deaths", "deaths");
		wordToEventType.put("dead", "deaths");
		wordToEventType.put("die", "deaths");
		wordToEventType.put("died", "deaths");*/
		
		return wordToEventType;
	}
}
