import java.text.DecimalFormat;

/* 
CIS 217
Tom Williams
04/06/23
Week 12
Lab - Final Project Covid Database

This code will take data from a csv file and create a database of over 10,000 covid data records. This will allow the user to determine statistics related to covid-19.
*/ 

public class CovidEntry implements Comparable<Object> {

	String state;
	int month;
	int day;
	int dailyDeaths;
	int dailyInfections;
	int totDeaths;
	int totInfections;
	
	public CovidEntry (String st, int m, int d, int di, int dd, int ti, int td) {
		state = st;
		month = m;
		day = d;
		dailyDeaths = dd;
		dailyInfections = di;
		totDeaths = td;
		totInfections = ti;
	}
	
	public int getMonth() {
		return month;
	}
	
	public int getDay() {
		return day;
	}
	
	public String getState() {
		return state;
	}
	
	public int getDailyInfections() {
		return dailyInfections;
	}
	
	public int getDailyDeaths() {
		return dailyDeaths;
	}
	
	public int getTotalInfections() {
		return totInfections;
	}
	
	public int getTotalDeaths() {
		return totDeaths;
	}
	
	public String toString() {
		
		DecimalFormat decForm = new DecimalFormat("#,###");
		
		String entry = state + " " +  
				month + "/" + day + " " + 
				decForm.format(dailyInfections) + " infections, " + 
				decForm.format(dailyDeaths) + " deaths";
		return entry;
	}
	
	public int compareTo(Object other) {
		CovidEntry c = (CovidEntry) other;
		return c.dailyDeaths - dailyDeaths;
	}
}
