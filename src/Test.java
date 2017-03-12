import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Random;


public class Test{

	private String userName = "root";
	private String password = "";
	private String dbms = "mysql";
	private String serverName = "localhost";
	private String portNumber = "3306";
	private String dbName = "votingapp";

	public Test(){

	}
	public static void main(String args[]) throws SQLException, ClassNotFoundException{
		Test test = new Test();
		System.out.println(test.doesIDNumberExist("12345"));
		System.out.println(test.doesValidationNumberExist("24125532525235"));
		System.out.println(test.createValidationNumber(128));
		System.out.println(test.getNextId());
		test.addValidationNumberToTable(test.createValidationNumber(32), 2456432);
	}
	
	public int getNextId() throws ClassNotFoundException, SQLException{
		Connection conn = getConnection();
		Statement stmt = null;
		String query = "Select max(id) From votingapp.validation_numbers;";
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				return rs.getInt("max(id)")+1;
			}
		} catch (SQLException e ) {
		} finally {
			if (stmt != null) { stmt.close(); }
		}
		conn.close();
		return 123;
	}
	
	public boolean addValidationNumberToTable(String validationNum, int ssn) throws SQLException, ClassNotFoundException{
		Connection conn = getConnection();
		int id = getNextId();
		Statement stmt = null;
		String query = "insert into votingapp.validation_numbers (id, IDNUMBER, VALIDATIONNUMBER)"
				+ " values (?, ?, ?)";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		preparedStmt.setInt(1, id);
		preparedStmt.setInt(2, ssn);
		preparedStmt.setString(3, validationNum);
		System.out.println(query);
		try {
			preparedStmt.execute();
			
		} catch (SQLException e ) {
		} finally {
			if (stmt != null) { stmt.close(); }
		}
		conn.close();
		return true;
	}
	public String createValidationNumber(int length) throws ClassNotFoundException, SQLException{
		Random rnd = new Random();
		BigInteger testValidationNum = new BigInteger(length, rnd);
		if(doesValidationNumberExist(testValidationNum.toString())){
			return createValidationNumber(length);
		}
		else
			return testValidationNum.toString();
	}
	
	public boolean doesValidationNumberExist(String validationNumber) throws SQLException, ClassNotFoundException{
		Connection conn = getConnection();
		Statement stmt = null;
		String query = "SELECT VALIDATIONNUMBER FROM votingapp.validation_numbers where VALIDATIONNUMBER = " + validationNumber +";";
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				String identification = rs.getString("VALIDATIONNUMBER");
				if(identification.equals(validationNumber)){
					conn.close();
					return true;
				}
			}
		} catch (SQLException e ) {
		} finally {
			if (stmt != null) { stmt.close(); }
		}
		conn.close();
		return false;
	}
	
	public boolean doesIDNumberExist(String idNumber) throws ClassNotFoundException, SQLException{
		Connection conn = getConnection();
		Statement stmt = null;
		String query = "SELECT IDNUMBER FROM votingapp.validation_numbers where IDNUMBER = " + idNumber +";";
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				String identification = rs.getString("IDNUMBER");
				if(identification.equals(idNumber)){
					conn.close();
					return true;
				}
			}
		} catch (SQLException e ) {
		} finally {
			if (stmt != null) { stmt.close(); }
		}
		conn.close();
		return false;
	}

	/*
	 * Note large parts of this code are either directly taken from
	 * or are at least based off of code provided by java and sql tutorials
	 * on connecting to databases.
	 */
	public Connection getConnection() throws SQLException, ClassNotFoundException {
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", this.userName);
		connectionProps.put("password", this.password);

		if (this.dbms.equals("mysql")) {
			conn = DriverManager.getConnection(
					"jdbc:" + this.dbms + "://" +
							this.serverName +
							":" + this.portNumber + "/",
							connectionProps);
		} else if (this.dbms.equals("derby")) {
			conn = DriverManager.getConnection(
					"jdbc:" + this.dbms + ":" +
							this.dbName +
							";create=true",
							connectionProps);
		}
		System.out.println("Connected to database");
		return conn;
	}

}