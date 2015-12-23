package dataminr;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.arabidopsis.ahocorasick.AhoCorasick;
import org.arabidopsis.ahocorasick.SearchResult;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

// Class for listening on the tweet stream - for twitter4j
public class CustomTweetListener implements StatusListener {

	private int totalCount = 0;
	private Set<String> first20Set = new HashSet<String>();
	private Set<String> last20Set = new HashSet<String>();
	private CRFClassifier<CoreLabel> classifier;
	private JDBCHelper helper;
	Map<String, String> wordToEventType;
	AhoCorasick companyNamesTree;

	public CustomTweetListener(Map<String, String> wordToEventType)
			throws ClassCastException, ClassNotFoundException, IOException,
			SQLException {
		helper = new JDBCHelper();
		this.classifier = Util.getClassifier();
		this.wordToEventType = wordToEventType;
		this.companyNamesTree = Util.getCompanyNamesTree();
	}

	private void storeNonEmptyLocationAndEvent(Status status)
			throws ClassCastException, ClassNotFoundException, IOException {
		// add the tweet into the queue buffer
		String text = status.getText();
		text = Util.cleanText(text);
		String first20 = text.substring(0, Math.min(text.length(), 20));
		if (first20Set.contains(first20))
			return;
		first20Set.add(first20);

		String last20 = text.length() <= 20 ? text : text.substring(text
				.length() - 20);
		if (last20Set.contains(last20))
			return;
		last20Set.add(last20);

		String[] locationAndEvent = getLocationEventAndOrg(text);
		String location = locationAndEvent[0];
		String event = locationAndEvent[1];
		// String location = "dummy"; String event = "dummy";
		if (location != null && event != null) {
			++totalCount;
			System.out.println(totalCount + "-" + location + "-" + event + "-"
					+ text);
			try {
				helper.insertTweet(status.getId(), status.getCreatedAt(), text,
						location, event, status.getUser().getScreenName(),
						status.getLang(), status.getPlace().getFullName(),
						status.getGeoLocation().getLatitude(), status
								.getGeoLocation().getLongitude(), status
								.getRetweetCount(), status.getSource(), null);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * status.getUser(); status.getCreatedAt(); status.getGeoLocation();
			 * status.getLang(); status.getPlace(); status.getRetweetCount();
			 * status.getSource(); System.out.println(location + "-" +
			 * status.getText() + " User : " + status.getUser().getName() +
			 * " date  = " + status.getCreatedAt() + " - " + //
			 * status.getGeoLocation().getLatitude()+ //
			 * "- '"+status.getGeoLocation().getLongitude()+ " - " +
			 * status.getLang() + " - " + status.getPlace() + " - " +
			 * status.getRetweetCount() + status.getSource());
			 * System.out.println("--------------------------");
			 */

		}
	}

	private void storeForCompanies(Status status) throws ClassCastException,
			ClassNotFoundException, IOException {
		
		String text = Util.cleanText(status);
		String first20 = text.substring(0, Math.min(text.length(), 20));
		if (first20Set.contains(first20))
			return;
		first20Set.add(first20);

		String last20 = text.length() <= 20 ? text : text.substring(text
				.length() - 20);
		if (last20Set.contains(last20))
			return;
		last20Set.add(last20);
		String[] data = getLocationEventAndOrg(text);
		String location = data[0];
		String event = data[1];
		String org = data[2];
		++totalCount;
		System.out.println(totalCount + "-" + text);
		try {
			Double lat = 0d;
			Double lng = 0d;
			if (status.getGeoLocation() != null) {
				lat = status.getGeoLocation().getLatitude();
				lng = status.getGeoLocation().getLongitude();
			}
			helper.insertTweet(status.getId(), status.getCreatedAt(), text,
					location, event, status.getUser().getScreenName(), status
							.getLang(), status.getPlace() != null ? status
							.getPlace().getFullName() : null, lat, lng, status
							.getRetweetCount(), status.getSource(), org);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void storeAll(Status status) throws ClassCastException,
			ClassNotFoundException, IOException {
		String text = Util.cleanText(status);

		String[] locationAndEvent = getLocationEventAndOrg(text);
		String location = locationAndEvent[0];
		String event = locationAndEvent[1];
		++totalCount;
		System.out.println(totalCount + "-" + text);
		try {
			Double lat = 0d;
			Double lng = 0d;
			if (status.getGeoLocation() != null) {
				lat = status.getGeoLocation().getLatitude();
				lng = status.getGeoLocation().getLongitude();
			}
			helper.insertTweet(status.getId(), status.getCreatedAt(), text,
					location, event, status.getUser().getScreenName(), status
							.getLang(), status.getPlace() != null ? status
							.getPlace().getFullName() : null, lat, lng, status
							.getRetweetCount(), status.getSource(), null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Implement the callback function when a tweet arrives
	@Override
	public void onStatus(Status status) {
		try {
			// storeAll(status);
			// storeNonEmptyLocationAndEvent(status);
			storeForCompanies(status);
		} catch (ClassCastException | ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice sdn) {
	}

	@Override
	public void onTrackLimitationNotice(int i) {
	}

	@Override
	public void onScrubGeo(long l, long l1) {
	}

	@Override
	public void onStallWarning(StallWarning warning) {
		writeStall(warning);
	}

	@Override
	public void onException(Exception e) {
		e.printStackTrace();
	}

	private void writeStall(StallWarning warning) {
		FileWriter fstream;

		try {
			fstream = new FileWriter("C:\\stall.txt", true);
			BufferedWriter bufferedWriter = new BufferedWriter(fstream);
			String msg = "stalled " + warning.getMessage() + "- % full = "
					+ warning.getPercentFull();
			bufferedWriter.write(msg);
			bufferedWriter.close();
			fstream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getOrg(String inputString) {
//		get the location - either it should be zero or there should be a space preceding it. 
		inputString = inputString.toLowerCase();
		Iterator searcher = companyNamesTree.search(inputString.getBytes());
		 if (searcher.hasNext()) {
	           SearchResult result = (SearchResult) searcher.next();
	           String companyName = (String) result.getOutputs().iterator().next();
	           return companyName;
	          /* int index  = result.getLastIndex() - companyName.length();
	           if(index == 0 || inputString.charAt(index - 1) == ' ') {
	        	   return companyName;
	           }*/
	       }
		  return null;
	}
	
	private String[] getLocationEventAndOrg(String inputString) {
		// System.out.println("getting locations for "+inputString);
		String location = null;
		String output1 = classifier.classifyToString(inputString,
				"tabbedEntities", false);
		String[] split = output1.split("\n");
		for (String string : split) {
			String[] split1 = string.split("\t");
			if (split1.length == 3) {
				// System.out.println("oye 0 "+split1[0] + " 1 "+split1[1]+
				// " 2 "+split1[2]);
				if (split1[1].equals("LOCATION")) {
					location = split1[0];
					break;
				}
			}
		}

		
		String org = getOrg(inputString);

		String[] ret = new String[3];
		ret[0] = location;
		ret[1] = null;
		ret[2] = org;
		String[] tokens = Util.splitOnWhitespace(inputString);

		for (String token : tokens) {
			if (wordToEventType.containsKey(token)) {
				ret[1] = wordToEventType.get(token);
				break;
			}
		}
		return ret;
	}

}