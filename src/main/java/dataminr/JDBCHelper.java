package dataminr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class JDBCHelper {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/twitter";

	// Database credentials
	static final String USER = "twitter";
	static final String PASS = "twitter";

	private Connection conn;
	private PreparedStatement insertTweetStatement;
	

	public JDBCHelper() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		System.out.println("Creating statement...");
		String sql;
		sql = "insert into tweets (tweetId,createdAt,text,location,event,user,lang,place,lat,lng,rtCount,source,org) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
		insertTweetStatement = conn.prepareStatement(sql);
	}
	public void insertTweet(long tweetId, Date createdAt,String text, String location,
			String event, String user, String lang, String place, double lat, double lng, int rtCount, String source, String org) throws SQLException {
		
		insertTweetStatement.setLong(1, tweetId);
		insertTweetStatement.setTimestamp(2,  new java.sql.Timestamp(createdAt.getTime()));
		insertTweetStatement.setString(3, text);
		insertTweetStatement.setString(4, location);
		insertTweetStatement.setString(5, event);

		insertTweetStatement.setString(6, user);
		insertTweetStatement.setString(7, lang);
		insertTweetStatement.setString(8, place);
		insertTweetStatement.setDouble(9, lat);
		insertTweetStatement.setDouble(10, lng);
		insertTweetStatement.setInt(11, rtCount);
		insertTweetStatement.setString(12, source);
		insertTweetStatement.setString(13, org);

		//insertTweetStatement.executeUpdate();
	}

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException {

		String text = "bhai bhai asdfasdf";
		String location = "Jodhpur";
		String event = "earthquake";

		JDBCHelper helper = new JDBCHelper();
		for (int i = 0; i < 100; i++) {
		//	helper.insertTweet(i, new Date(),text, location, event);
		}

	}// end main
}
