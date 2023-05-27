/* 
CIS 217
Tom Williams
04/06/23
Week 12
Lab - Final Project Covid Database

This code will take data from a csv file and create a database of over 10,000 covid data records. This will allow the user to determine statistics related to covid-19.
*/ 

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CovidDatabase {
	
	private ArrayList<CovidEntry> covidData;
	private static final int SAFE = 5;
	int numEnt;
	
	public CovidDatabase() {
		covidData = new ArrayList<CovidEntry>();		
		numEnt = 0;
	}
	
	private void transferCovidData(String filename) {		
		String DataBase = "jdbc:sqlite:covid.db";
		String csvFilePath = "covid_data.csv";
		
		int batchSize = 20;
		
		Connection connection = null;
		
		try {
			connection = DriverManager.getConnection(DataBase);
			connection.setAutoCommit(false);
			
			String sql = "CREATE TABLE IF NOT EXISTS ENTRY (\n"
					+ " id integer PRIMARY KEY,\n"
					+ "state text NOT NULL,\n" 
					+ "month integer NOT NULL,\n"
					+ "day integer NOT NULL,\n"
					+ "daily_infections integer NOT NULL,\n"
					+ "daily_deaths integer NOT NULL,\n"
					+ "total_infections integer NOT NULL,\n "
					+ "total_deaths integer NOT NULL\n"
					+ ");";
			
			Statement statement = connection.createStatement();
			statement.execute(sql);
			
			String stmt_sql = "INSERT INTO ENTRY(state, month, day, daily_infections, daily_deaths, total_infections, total_deaths) VALUES(?,?,?,?,?,?,?)";
			
			PreparedStatement pstmt = connection.prepareStatement(stmt_sql);
			
			BufferedReader lineReader = new BufferedReader(new FileReader(csvFilePath));
			String lineText = null;
			
			int count = 0;
			
			lineReader.readLine();
			
			while ((lineText = lineReader.readLine()) != null) {
				String[] data = lineText.split(",");
				String state = data[0];
				int month = Integer.parseInt(data[1]);
				int day = Integer.parseInt(data[2]);
				int daily_infections = Integer.parseInt(data[3]);
				int daily_deaths = Integer.parseInt(data[4]);
				int total_infections = Integer.parseInt(data[5]);
				int total_deaths = Integer.parseInt(data[6]);
							
				pstmt.setString(1, state);
				pstmt.setInt(2, month);
				pstmt.setInt(3, day);
				pstmt.setInt(4, daily_infections);
				pstmt.setInt(5, daily_deaths);
				pstmt.setInt(6, total_infections);
				pstmt.setInt(7, total_deaths);
				
				pstmt.executeUpdate();
				
				if (count % batchSize == 0) {
					pstmt.executeBatch();
				}
			}
			
			lineReader.close();
			
			pstmt.executeBatch();
			
			connection.commit();			
			
		}
		catch (IOException ex) {
			System.err.println(ex);
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		String read_sql = "SELECT * FROM ENTRY";
		
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(read_sql);
			
			while(rs.next()) {
				System.out.println(rs.getInt("id") + ", " 
						+ rs.getString("state") + ", " 
						+ rs.getInt("month") + ", " 
						+ rs.getInt("day") + ", " 
						+ rs.getInt("daily_infections") + ", "
						+ rs.getInt("daily_deaths") + ", "
						+ rs.getInt("total_infections") + ", "
						+ rs.getInt("total_deaths") + ", "
						);
			}
		}
		catch (SQLException e){
			System.out.println(e.getMessage());
		}
		try {
			if (connection != null)
				connection.close();
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
	}	
	
	public void covidDataStart() {
		transferCovidData("covid_data.csv");
	}
	
	public void readCovidData(String filename) {
		String DataBase = "jdbc:sqlite:covid.db";
		
		Connection connection = null;
		
		try {
			//opens database file
			connection = DriverManager.getConnection(DataBase);
			connection.setAutoCommit(false);
			
			String sql = "SELECT * FROM ENTRY";
					
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				//pulls the first line and splits it into variables
				String st = rs.getString("state");
				int m = rs.getInt("month");
				int d = rs.getInt("day");
				int di = rs.getInt("daily_infections");
				int dd = rs.getInt("daily_deaths");
				int ti = rs.getInt("total_infections");
				int td = rs.getInt("total_deaths");
				
				//creates CovidEntry object
				CovidEntry cData = new CovidEntry(st, m, d, di, dd, ti, td);
				
				//adds object to database
				covidData.add(cData);
			}
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	//returns a count of how many Covid Entries have been recorded
	public int countRecords() {
		return numEnt = covidData.size();
	}
	
	//returns a count of total daily deaths within the database
	public int getTotalDeaths() {
		int tdeath = 0;
		int ddeath = 0;
		
		for (int i = 0; i < covidData.size(); ++i) {
			ddeath = covidData.get(i).getDailyDeaths();
			tdeath = tdeath + ddeath;
		}
		return tdeath;
	}	
	
	//returns a count of total daily infections within the database
	public int getTotalInfections() {
		int tInf = 0;
		int dInf = 0;
		
		for (int i = 0; i < covidData.size(); ++i) {
			dInf = covidData.get(i).getDailyInfections();
			tInf = tInf + dInf;
		}
		return tInf;
	}
	
	//returns a count of total daily deaths within the database from a specific date
	public int countTotalDeaths(int m, int d) {
		int dDeath = 0;
		int tDeath = 0;
		
		for (int i = 0; i < covidData.size(); ++i) {
			if (covidData.get(i).getMonth() == m && covidData.get(i).getDay() == d) {
				dDeath = covidData.get(i).getDailyDeaths();
				tDeath = tDeath + dDeath;
			}
		}
		
		return tDeath;
	}
	
	//returns a count of total daily infections within the database
	public int countTotalInfections(int m, int d) {
		int tInf = 0;
		int dInf = 0;
		
		for (int i = 0; i < covidData.size(); ++i) {
			if (covidData.get(i).getMonth() == m && covidData.get(i).getDay() == d) {
				dInf = covidData.get(i).getDailyInfections();
				tInf = tInf + dInf;
			}
		}
		return tInf;
	}
	
	//returns the entry with the highest daily death for the requested state
	public CovidEntry peakDailyDeaths(String st) {
		CovidEntry peakDDeath = null;
		int dDeath = 0;
		
		for (int i = 0; i < covidData.size(); ++i) {
			if ((covidData.get(i).getState()).equalsIgnoreCase(st)) {
				if (covidData.get(i).getDailyDeaths() > dDeath) {
					dDeath = covidData.get(i).getDailyDeaths();
					peakDDeath = covidData.get(i);
				}
			}
		}
		return peakDDeath;
	}
	
	//returns the entry with the highest daily deaths based on month and day
	public CovidEntry peakDailyDeaths(int m, int d) {
		CovidEntry peakDDeath = null;
		int dDeath = 0;
		
		for (int i = 0; i < covidData.size(); ++i) {
			if ((covidData.get(i).getMonth()) == m && (covidData.get(i).getDay()) == d ) {
				if (covidData.get(i).getDailyDeaths() > dDeath) {
					dDeath = covidData.get(i).getDailyDeaths();
					peakDDeath = covidData.get(i);
				}
			}
		}		
		return peakDDeath;
	}
	
	//returns the entry with the highest total deaths
	public CovidEntry mostTotalDeaths() {
		CovidEntry tDeath = null;
		int tD = 0;
		
		for (int i = 0; i < covidData.size(); ++i) {
			if (covidData.get(i).getTotalDeaths() > tD) {
				tD = covidData.get(i).getTotalDeaths();
				tDeath = covidData.get(i);
			}
		}		
		return tDeath;
	}
	
	//creates an arraylist to track daily deaths for a specific month and day. 
	//Will return null if there is no entry.
	public ArrayList <CovidEntry> getDailyDeaths(int m, int d) {	
		ArrayList<CovidEntry> dDeaths  = new ArrayList<CovidEntry>();
		
		for (int i = 0; i < covidData.size(); ++i) {
			if ((covidData.get(i).getMonth()) == m && (covidData.get(i).getDay()) == d) {
				CovidEntry vars = covidData.get(i);
				dDeaths.add(vars);
			}
		}
		return dDeaths;
	}
	
	//creates an arraylist to track daily infections for a specific month and day with a minimum infection rate. 
	//Will return null if there is no entry.
	public ArrayList <CovidEntry> listMinimumDailyInfections (int m, int d, int min) {
		ArrayList<CovidEntry> minInfs = new ArrayList<CovidEntry>();
		
		for (int i = 0; i < covidData.size(); ++i) {
			if ((covidData.get(i).getMonth()) == m && 
					(covidData.get(i).getDay()) == d &&
					(covidData.get(i).getDailyInfections() >= min)) {
				CovidEntry var = covidData.get(i);
				minInfs.add(var);
			}
		}		
		return minInfs;
	}
	
	//creates an arraylist to pull all entries from a state and then another arraylist to search for 5 consecutive days of decreasing infections
	//will return null if there are no entries
	public ArrayList <CovidEntry> safeToOpen (String st) {
		ArrayList<CovidEntry> stateList = new ArrayList<CovidEntry>();
		ArrayList<CovidEntry> safeOpen = new ArrayList<CovidEntry>();
		CovidEntry var;
		CovidEntry var1;
		CovidEntry var2;
		CovidEntry var3;
		CovidEntry var4;
		CovidEntry var5;		
		
		for (int i = 0; i < covidData.size(); ++i) {
			if ((covidData.get(i).getState().equalsIgnoreCase(st))) {
				var = (covidData.get(i));
				stateList.add(var);
			}
		}
		
		if (stateList.size() == 0) {
			safeOpen = null;
			return safeOpen;
		}
		
		for (int i = 0; i < stateList.size(); ++i) {
			if (i == stateList.size()-4) {
				break;
			}
			if ((stateList.get(i).getDailyInfections()) > (stateList.get(i + 1).getDailyInfections()) 
					&& (stateList.get(i + 1).getDailyInfections()) > (stateList.get(i + 2).getDailyInfections()) 
					&& (stateList.get(i + 2).getDailyInfections()) > (stateList.get(i + 3).getDailyInfections()) 
					&& (stateList.get(i + 3).getDailyInfections()) > (stateList.get(i + 4).getDailyInfections())) { 
						
				var1 = stateList.get(i);
				var2 = stateList.get(i + 1);
				var3 = stateList.get(i + 2);
				var4 = stateList.get(i + 3);
				var5 = stateList.get(i + 4);
				
				safeOpen.add(var1);
				safeOpen.add(var2);
				safeOpen.add(var3);
				safeOpen.add(var4);
				safeOpen.add(var5);
				
				break;
			}
		}
		return safeOpen;
	}
	
	//creates an array list to house all of the daily deaths on a given month and date. It will sort these by daily deaths
	//it will then transfer the top ten into it's own list and return that arraylist
	public ArrayList <CovidEntry> topTenDeaths (int m, int d) {
		ArrayList<CovidEntry> topTen = new ArrayList<CovidEntry>(); 
		ArrayList<CovidEntry> list = new ArrayList<CovidEntry>();
		
		list = getDailyDeaths(m, d);
		
		if (list.size() == 0) {
			return topTen;
		}
		
		Collections.sort(list);
		
		for (int i = 0; i < 10; ++i) {
			CovidEntry var = list.get(i);
			topTen.add(var);
		}
		
		return topTen;
	}
	
	public static void main(String[] args) {
		
	}
}
