package application;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * Class that holds various methods to deal with MySQL Databases
 * @author Roxy
 *
 */
public class sqlUtil {
	private static ResultSet rs;
	private Connection con ;
    private Statement st ;
    private ResultSetMetaData rsMetaData;
    
    /**
     * Constructor for the class
     * @param reSet
     * @param conn
     * @param state
     * @param rsMD
     */
    public sqlUtil(ResultSet reSet, Connection conn, Statement state, ResultSetMetaData rsMD) {
    	rs = reSet;
    	this.con = conn;
    	this.st = state;
    	this.rsMetaData = rsMD;
    }
	/**
	 * Connects Java Program to mySQL DB. Parameters are the 
	 * credentials and the localHost port
	 * @param url - Link for port
	 * @param user - User name
	 * @param password - Password
	 * @return - True if connection was successful
	 */
	public boolean connect(String url, String user, String password){
		try {
			con = DriverManager.getConnection(url, user, password);
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}
	/**
	 * Executes a query in the DB and gathers the information in a string.
	 * @param query - The query to execute
	 * @return - Returns what the query returns
	 * @throws SQLException
	 */
	public String executeQuery(String query) throws SQLException {
		String result = "";
		st = con.createStatement();
		rs = st.executeQuery(query);
		rsMetaData = rs.getMetaData();
		int cols = rsMetaData.getColumnCount();
		if(cols>1) {
			while(rs.next()) {
				//iterate through columns
				for(int i = 0 ; i < cols; i++) {
					if(i+1==cols) {
						result+=rs.getString(i+1)+"\n";
					}else {
						result+=rs.getString(i+1)+", ";
					}
				}
			}
		}
		return result;
	}
	/**
	 * Executes a MySQL command. 
	 * @param command - Command to execute
	 * @return - If the execution was successful
	 * @throws SQLException
	 */
	public int executeCommand(String command) throws SQLException {
		return st.executeUpdate(command);
	}
	/**
	 * Writes the results of the simulation to file
	 * @param ship - SpaceShip object
	 * @param initPassengers - No of initial passengers
	 * @param speed - Speed of spaceship
	 * @param route - Optimal route
	 * @param rt - String representation of route
	 * @param dir - Directory to write file to
	 * @return - Whether the write was successful
	 */
	public static boolean writeResults(SpaceShip ship,long initPassengers,double speed, Route route, String rt,String dir){
		route.calcTotalDist();
		String res="Spaceship Stats:"
				+"\nPassengers -> "+ initPassengers*1000000
				+"\nCapacity -> "+ ship.capacity
				+"\nSpeed -> "+ speed +"km/s"
				+"\nParent Planet -> Main Planet (Earth) id:0"
				+"\n\nRoute Stats:"
				+"\nStarting Planet -> "+route.start.name +" id -> "+route.start.id
				+"\nDestination Planet -> "+route.end.name +" id -> "+route.end.id
				+"\nTotal Distance -> "+route.distance +" km"
				+"\nTravel Time -> "+(route.distance/speed)
				+"\nRoute -> 0 ->" + rt;
		
		BufferedWriter bf;
		try {
			bf = new BufferedWriter(new FileWriter(dir+"\\Results.txt"));
		    bf.append(res);
		    bf.close();
		} catch (IOException e) {
			return false;
		}
		return true;

	}
}
