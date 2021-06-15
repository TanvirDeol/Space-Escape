package application;

import java.util.ArrayList;
/**
 * A Route object holds the route the spaceship travels.
 * @author Tanvir
 *
 */
public class Route {
	Planet start;
	Planet end;
	ArrayList<Planet> route;
	double distance;
	double time;
	/**
	 * Route Constructor
	 * @param arr - ArrayList of Planets
	 */
	public Route(ArrayList<Planet> arr) {
		this.route = arr;
		this.start = Planet.allPlanets.get(0);
	}
	/**
	 * Converts a route to String form.
	 * Used when writing results to file.
	 */
	public String toString() {
		String s="";
		for(int i=0;i<route.size();i++) {
			s+=route.get(i).id+"-> ";
		}
		s+="end";
		return s;
	}
	/**
	 * Calculates total distance traveled in route
	 * Useful when writing results to file
	 */
	public void calcTotalDist() {
		double dist = simulation.distance(Planet.allPlanets.get(0), route.get(0));
		for(int i=0;i<route.size()-1;i++) {
			dist +=simulation.distance(route.get(i), route.get(i+1));
		}
		this.distance = dist;
	}
}
