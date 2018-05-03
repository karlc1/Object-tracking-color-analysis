import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Date;



public class Database {

	private Connection connection;
	private static final String DB_ADRESS = "jdbc:postgresql://localhost:5432/projectDB";
	private static final String DB_USER = "postgres";
	private static final String DB_PASSWORD = "pass";

	/**
	 * Constructor that also opens the database connection.
	 * @throws ClassNotFoundException 
	 */
	public Database() {
		try{
			java.lang.Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(DB_ADRESS, DB_USER, DB_PASSWORD);	
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}


	/**
	 * Push a data entry to DB
	 * @param t
	 * @param red
	 * @param green
	 * @param blue
	 */
	public void insertData(Timestamp t, int red, int green, int blue) {
		System.out.println("insertData");
		try {
			System.out.println("Insert try");
			String sql = "INSERT into colors values(?, ?, ?, ?)";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setTimestamp(4, t);
			ps.setInt(1, red);
			ps.setInt(2, green);
			ps.setInt(3, blue);
			System.out.println(ps.toString());
			ps.executeUpdate();
			


		} catch (SQLException e) {

		}
	}







}
