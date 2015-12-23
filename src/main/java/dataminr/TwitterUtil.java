package dataminr;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Dharmendra Tolani on 12/23/2015.
 */
public class TwitterUtil {
    public static Twitter createTwitterInstance() {
        return new TwitterFactory(getTwitterConfig()).getInstance();
    }

    static Configuration getTwitterConfig() {
        String custkey = "";
        String custsecret = "";

        String accesstoken = "";
        String accesssecret = "";

        ConfigurationBuilder config = new ConfigurationBuilder()
                .setOAuthConsumerKey(custkey)
                .setOAuthConsumerSecret(custsecret)
                .setOAuthAccessToken(accesstoken)
                .setOAuthAccessTokenSecret(accesssecret);
        return config.build();
    }

    public static TwitterStream createTwitterStream() {
        TwitterStreamFactory fact = new TwitterStreamFactory(getTwitterConfig());
        return fact.getInstance();
    }
}
