import java.text.DecimalFormat;
import java.util.ArrayList;

/* 
CIS 217
Tom Williams
04/06/23
Week 12
Lab - Final Project Covid Database

This code will take data from a csv file and create a database of over 10,000 covid data records. This will allow the user to determine statistics related to covid-19.
*/ 

public class CovidDatabaseManager {

	public static void main(String[] args) {
		
		DecimalFormat decForm = new DecimalFormat("#,###");
		CovidEntry var;
		
		System.out.println ("Testing starts");
		System.out.println();
		CovidDatabase db = new CovidDatabase() ;
		db.readCovidData("covid.db");
		int err = 0;
		
		// check number of records, total infections, and total deaths
		System.out.println("Number of records: " + decForm.format(db.countRecords()));		
		if( db.countRecords() != 10346 ) {
			++err;
			System.out.println("Database should have 10,346");
		}
		System.out.println();
		
		System.out.println("Total Deaths: " + decForm.format(db.getTotalDeaths()));
		if ( db.getTotalDeaths() != 196696 ) {
			++err;
			System.out.println("Total deaths should be: 196,696");
		}
		System.out.println();
		
		System.out.println("Total Infections: " + decForm.format(db.getTotalInfections()));
		if ( db.getTotalInfections() != 7032090 ) {
			++err;
			System.out.println( "Infections should be: 7,032,090");
		}
		System.out.println();
		
		// check peak daily deaths for 5/5
		CovidEntry mostDeaths = db.peakDailyDeaths(5, 5);
		System.out.println("State with most deaths on " + mostDeaths.getMonth() + "/" + mostDeaths.getDay() + ": " + mostDeaths);
		if ( !mostDeaths.getState().equals("PA") ) {
			++err;
			System.out.println( "State with most deaths for 5/5 is PA");
		}
		if ( mostDeaths.getDailyDeaths() != 554 ) {
			++err;
			System.out.println( "Deaths for 5/5 is PA: 554") ;
		}
		System.out.println();

		// tests total deaths
		CovidEntry mostTotalDeaths = db.mostTotalDeaths();
		System.out.println("State with highest total deaths: " + mostTotalDeaths.getState() + " with " + decForm.format(mostTotalDeaths.getTotalDeaths()) + " deaths.");
		System.out.println();
		if (!mostTotalDeaths.getState().equals("NY")) {
			++err;
			System.out.println("State with most total deaths is NY");
		}
		
		//tests peak daily deaths by state
		CovidEntry peakDD = db.peakDailyDeaths("MI");
		System.out.println("Peak daily deaths in " + peakDD.toString());
		System.out.println();
		if (!peakDD.getState().equals("MI")) {
			++err;
			System.out.println("State should be MI");
		}
		if (peakDD.getDailyDeaths() != 169) {
			++err;
			System.out.println("peak daily deaths should be 169");					
		}
		
		//tests peak daily deaths by date		
		CovidEntry peakDD2 = db.peakDailyDeaths(5, 5);
		System.out.println("Peak daily deaths in " + peakDD2.toString());
		System.out.println();
		if (!peakDD2.getState().equals("PA")) {
			++err;
			System.out.println("State should be PA");
		}
		if (peakDD2.getDailyDeaths() != 554) {
			++err;
			System.out.println("peak daily deaths should be 554");					
		}
		
		//tests toptendeaths by date
		ArrayList<CovidEntry> topTen = db.topTenDeaths(5, 5);
		var = null;
		
		System.out.println("Top Ten Daily Deaths for 5/5");
		System.out.println();
		for (int i = 0; i < topTen.size(); ++i) {
			var = topTen.get(i);
			System.out.println(var.toString());
		}
		if (!topTen.get(0).getState().equals("PA")) {
			++err;
			System.out.println("State should be PA");		
		}
		if (!topTen.get(9).getState().equals("CA")) {
			++err;
			System.out.println("State should be CA");		
		}
		System.out.println();
		
		//tests safe to open 
		ArrayList<CovidEntry> safe = db.safeToOpen("MI");
		var = null;
		
		System.out.println("MI is safe to open.");
		System.out.println();
		for (int i = 0; i < safe.size(); ++i) {
			var = safe.get(i);
			System.out.println(var.toString());
		}
		if (safe.get(0).getDailyInfections() != 443) {
			++err;
			System.out.println("Infections should be 443");		
		}
		if (safe.get(4).getDailyInfections() != 205) {
			++err;
			System.out.println("Infections should be 205");		
		}
		System.out.println();
		
		//tests minimum daily infections method
		ArrayList<CovidEntry> minD = db.listMinimumDailyInfections(6, 12, 1000);
		
		System.out.println("All states with at least 1000 infections on 6/12");
		System.out.println();
		var = null;
		
		for (int i = 0; i < minD.size(); ++i) {
			var = minD.get(i);
			System.out.println(var.toString());
		}
		if (!minD.get(0).getState().equals("TX")) {
			++err;
			System.out.println("State should be TX");		
		}
		if (minD.get(4).getDailyInfections() != 1654) {
			++err;
			System.out.println("Infections should be 1,654");		
		}
		System.out.println();
		

		//prints out the number of errors
		System.out.println("Number of Errors: " + err);
		System.out.println ("Testing ends");
	}

}
