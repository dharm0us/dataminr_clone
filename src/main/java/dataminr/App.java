package dataminr;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.arabidopsis.ahocorasick.AhoCorasick;
import org.arabidopsis.ahocorasick.SearchResult;

import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;

/**
 * Hello world!
 * 
 */
public class App {
	Map<String, String> wordToEventType;
	CRFClassifier<CoreLabel> classifier;
	// Twitter4j - twitter stream to get tweets
	private TwitterStream twitterStream;

	private void run1() throws ClassCastException, ClassNotFoundException,
			IOException {
		String serializedClassifier = "D:\\classifiers\\english.all.7class.distsim.prop";
		CRFClassifier<CoreLabel> classifier = CRFClassifier
				.getClassifier(serializedClassifier);
		String[] example = { " earthquake at 22nd century group" };

		for (String str : example) {
			System.out.println(classifier.classifyToString(str));
		}
		System.out.println("---");

		for (String str : example) {
			// This one puts in spaces and newlines between tokens, so just
			// print not println.
			System.out.print(classifier.classifyToString(str, "slashTags",
					false));
		}
		System.out.println("---");

		for (String str : example) {
			// This one is best for dealing with the output as a TSV
			// (tab-separated column) file.
			// The first column gives entities, the second their classes, and
			// the third the remaining text in a document
			String output1 = classifier.classifyToString(str, "tabbedEntities",
					false);
			String[] split = output1.split("\n");
			for (String str1 : split) {
				String[] split1 = str1.split("\t");
				System.out.println("length is " + split1.length);
				for (int i = 0; i < split1.length; i++) {
					System.out.println(i + "-" + split1[i]);
				}
				System.out.println("kkk");
			}
		}
		System.out.println("---");

		for (String str : example) {
			System.out.println(classifier.classifyWithInlineXML(str));
		}
		System.out.println("---");

		for (String str : example) {
			System.out.println(classifier.classifyToString(str, "xml", true));
		}
		System.out.println("---");

		for (String str : example) {
			System.out.print(classifier.classifyToString(str, "tsv", false));
		}
		System.out.println("---");

		// This gets out entities with character offsets
		int j = 0;
		for (String str : example) {
			j++;
			List<Triple<String, Integer, Integer>> triples = classifier
					.classifyToCharacterOffsets(str);
			for (Triple<String, Integer, Integer> trip : triples) {
				System.out.printf(
						"%s over character offsets [%d, %d) in sentence %d.%n",
						trip.first(), trip.second(), trip.third, j);
			}
		}
		System.out.println("---");

		// This prints out all the details of what is stored for each token

		System.out.println("---");
	}

	private void run() throws ClassCastException, ClassNotFoundException,
			IOException, SQLException {
	
		setTwitterStream();
		
		//trackStream();
		trackKeywords();
	}
	
	private void trackStream() {
		twitterStream.sample();
	}
	
	private void setTwitterStream() throws ClassCastException, ClassNotFoundException, IOException, SQLException {
		
		// provide the handler for twitter stream
		this.wordToEventType = Util.getWordToEventType();
		twitterStream = TwitterUtil.createTwitterStream();
		twitterStream.addListener(new CustomTweetListener(wordToEventType));

	}
	
	private void trackKeywords() {
		Set<String> keys = wordToEventType.keySet();
		String[] keywordsArray = keys.toArray(new String[keys.size()]);
		// String[] keywordsArray = {"london"};
		FilterQuery filter = new FilterQuery();
		filter.track(keywordsArray);
		twitterStream.filter(filter);
	}
	
	private static void test() throws SQLException, ClassNotFoundException {
		String text = "There's no evidence games cause violence or misogyny. No evidence that gaming has a special problem with sexism. It's a moral panic.";
		text = Util.cleanText(text);
		System.out.println(text);
		JDBCHelper helper = new JDBCHelper();
		//helper.insertTweet(1, new Date(), text, "dummy", "dummy");
	}
	
	private static void getTweetById(long id) throws TwitterException {
		Twitter twitter = TwitterUtil.createTwitterInstance();
		Status status = twitter.showStatus(id);
		String text = Util.cleanText(status);
		System.out.println(text);
	}
	
	public static void testAho() {
		 AhoCorasick tree = new AhoCorasick();
	       tree.add("hello".getBytes(), "hello");
	       tree.add("world".getBytes(), "world");
	       tree.prepare();

	       Iterator searcher = tree.search("hello world".getBytes());
	       while (searcher.hasNext()) {
	           SearchResult result = (SearchResult) searcher.next();
	           System.out.println(result.getOutputs());
	           System.out.println("Found at index: " + result.getLastIndex());
	       }
	}
	
	public static void main(String[] args) throws ClassCastException,
			ClassNotFoundException, IOException, SQLException, TwitterException {
		//test();
		//testAho();

	/*	CustomTweetListener customTweetListener = new CustomTweetListener(Util.getWordToEventType());
		String inputString = "a3M company fire";
		System.out.println(customTweetListener.getOrg(inputString));*/
		//getTweetById(588091477831581696l);
					App app = new App();
				app.run();
//		Util.getPartialNameToCompanyNames();
//		app.writeStall();
		// app.run1();
		/*
		 * String serializedClassifier =
		 * "D:\\classifiers\\english.all.3class.distsim.crf.ser.gz";
		 * app.classifier = CRFClassifier.getClassifier(serializedClassifier);
		 * app.getLocations(
		 * "RT @JackAllTimeLow: To promote our new album #FutureHearts I will be donating use of my body on the streets of London tonight"
		 * );
		 */
	}
}
