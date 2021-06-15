package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Class for Planet Objects
 * @author Tanvir
 *
 */
public class Planet {
	public static ArrayList<Planet> allPlanets;
	String name;
	int id;
	int x;
	int y;
	int radius;
	boolean isMain;
	long population;
	long maxPopulation;
	public ArrayList<Planet>connections;
	/**
	 * This constructor initializes most fields for a Planet Object
	 * @param id - Planet id
	 * @param name - Planet name
	 * @param x - Planet X position
	 * @param y - Planet Y position
	 * @param radius - Planet radius
	 * @param isMain - If the planet is Earth
	 * @param population - Planet population
	 * @param maxPopulation - Planet capacity
	 */
	public Planet(int id, String name, int x, int y, int radius, boolean isMain, long population, long maxPopulation) {
		this.id = id;
		this.name = name;
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.isMain = isMain;
		this.population = population;
		this.maxPopulation = maxPopulation;
		this.connections = new ArrayList<Planet>();
	}
	/**
	 * Empty Planet Constructor, used to access non-static methods
	 */
	public Planet() {
	}
	/**
	 * Uses various functions to generate N random Planet objects.
	 * @param N - No of Planets
	 * @return - ArrayList of Planets
	 */
	public ArrayList<String> genPlanet(int N) {
		if(allPlanets == null) allPlanets = new ArrayList<Planet>();
		String query="";
		ArrayList<Planet> planets = new ArrayList<Planet>();
		int count = 0;
		for(int i=0;i<N;i++) {
			int pID = count; count++;
			String pName = genName();
			int pRadius = genRadius();
			Coord pos = genCoord(pRadius);
			int pX = pos.x;
			int pY = pos.y;
			boolean pMain;
			if(i==0)pMain=true;
			else pMain = false;
			long pop = genPop();
			long mPop = genMaxPop();
			Planet p = new Planet(pID,pName,pX,pY,pRadius,pMain,pop,mPop);
			allPlanets.add(p);
			planets.add(p);
		}
		ArrayList<String> res = new ArrayList<String>();
		for(int i=0;i<N;i++) {
			genConnect(planets.get(i));
		}
		for(int i=0;i<N;i++) {
			query ="";
			Planet p = planets.get(i);
			query+="insert into planetInfo (id, planet_name, is_source, x, y, radius, population, maxPopulation, connections) values ";
			query+="("+p.id+", "+"\""+p.name+"\", "+p.isMain+", "+p.x+", "+p.y+", "+p.radius+", "+p.population+", "+p.maxPopulation+", \"";
			for(int j=0;j<p.connections.size();j++) query+=p.connections.get(j).id+" ";
			query+="\");";
			res.add(query);
		}
		return res;
	}
	/**
	 * Generates random name for planet. There are 26^8 name possibilities. 
	 * So there's is a low chance of name-collision
	 * @return - A name
	 */
	private String genName() {
		String name = "Planet ";
		String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (int i=0;i<8;i++) {
			int idx = (int) (Math.random()*26);
			name+=alpha.charAt(idx);
		}
		return name;
	}
	/**
	 * Randomly Generates X and Y positions for a Planet. 
	 * It will fit in a 1000x1000 plane and will not collide with other
	 * planets.
	 * @param radius - Radius of Planet
	 * @return - Coord(X,Y) object
	 */
	private Coord genCoord(int radius) {
		int x,y;
		while(true) {
			x = (int) (Math.random()*1000);
			y = (int) (Math.random()*1000);
			boolean inside = false;
			for(int i=0;i<allPlanets.size();i++) {
				Planet p = allPlanets.get(i);
				double dist = Math.sqrt(Math.pow(p.x-x,2.0)+Math.pow(p.y-y,2.0));
				if(dist <= p.radius+radius+20) {
					inside = true;
				}
				if(x <radius || 1000-x<radius || y<radius ||1000-y<radius)inside = true;
			}
			if(!inside)break;
		}
		return new Coord(x,y);
	} 
	
	/**
	 * Randomly generates planet radius
	 * @return - Radius
	 */
	private int genRadius() {
		return (int)(Math.random()*40)+10;
	} 
	/**
	 * Randomly generates planet population
	 * @return - Population
	 */
	private long genPop() {
		return (long)(Math.random()*1000000);
	} 
	/**
	 * Randomly generates planet capacity
	 * @return - Capacity
	 */
	private long genMaxPop() {
		return (long)((Math.random()*1000)*1000)+(int)((Math.random()*4000000)+1000000);
	} 
	/**
	 * Randomly generates planet connections. 
	 * Connections are a list of planets that mutually trust each other
	 * to do business (like transporting people)
	 */
	private void genConnect(Planet cur) {
		int connects = (int)((Math.random()*3)+2);
		ArrayList<Dist>sortDist = new ArrayList<Dist>();
		for(int i=0;i<allPlanets.size();i++) {
			Planet p = allPlanets.get(i);
			if(p.id ==cur.id)continue;
			sortDist.add(new Dist(Math.sqrt(Math.pow(p.x-cur.x,2.0)+Math.pow(p.y-cur.y,2.0)),i));
		}
		Collections.sort(sortDist,new sortDist());
		boolean cont = false;
		for(int i=0;i<connects;i++) {
			cont =false;
			Planet ngh = allPlanets.get(sortDist.get(i).index);
			for(int j=0;j<ngh.connections.size();j++) {
				if(ngh.connections.get(j).id==cur.id) {cont = true; break;}
			} 
			if(!cont)ngh.connections.add(cur);
			cont =false;
			for(int j=0;j<cur.connections.size();j++) {
				if(cur.connections.get(j).id == ngh.id) {cont = true; break;}
			}
			if(!cont)cur.connections.add(ngh);
			allPlanets.set(sortDist.get(i).index, ngh);
		}
		sortDist.clear();
	}
	/**
	 * Clears planet data
	 */
	public void clearPlanets() {
		allPlanets.clear();
	}
	/**
	 * Generates a sprite for a planet based on planet fields
	 * @param p - Planet
	 * @return - Circle Sprite
	 */
	public static Circle genSprite(Planet p) {
		Circle circle = new Circle();
		if(p.isMain) circle.setFill(Color.BLUE);
		else circle.setFill(Color.LIGHTGRAY);
		circle.setRadius(p.radius);
		return circle;
	}
	/**
	 * Gets string data from MySQL DB and converts it into an 
	 * ArrayList of Planets
	 * @param data - String data from MySQL
	 */
	public void readPlanets(String data) {
		data = data.replaceAll(",", "");
		String[] lines = data.split("\n");
		String[] conn = new String[lines.length];
		for(int i=0;i<lines.length;i++) {
			//System.out.println(lines[i]);
			Scanner sc = new Scanner(lines[i]);
			int id = sc.nextInt();
			String name = sc.next(); name+="_"+sc.next();
			int mn = sc.nextInt();
			boolean isMain = false; if(mn ==1)isMain = true;
			int x = sc.nextInt();
			int y = sc.nextInt();
			int rad = sc.nextInt();
			long pop = sc.nextLong();
			long mPop = sc.nextLong();
			Planet p = new Planet(id,name,x,y,rad,isMain,pop,mPop);
			String rem="";
			while(sc.hasNext())rem+=sc.next()+" ";
			conn[i] = rem;
			allPlanets.add(p);
			sc.close();
		}
		for(int i=0;i<conn.length;i++) {
			String cur= conn[i];
			Scanner sc = new Scanner(cur);
			allPlanets.get(i).connections = new ArrayList<Planet>();
			while(sc.hasNextInt()) {
				int x = sc.nextInt();
				if(x!=i) {
					allPlanets.get(i).connections.add(allPlanets.get(x));
				}
			}
			sc.close();
		}
	}
}
/**
 * Holds X and Y coordinates
 * @author Tanvir
 *
 */
class Coord{
	int x;
	int y;
	public Coord(int x, int y) {
		this.x =x;
		this.y = y;
	}
}
/**
 * Holds a distance and Planet index. 
 * Useful for Sorting to create planet connections
 */
class Dist {
	double dist;
	int index;
	public Dist(double d, int idx) {
		this.dist = d;
		this.index = idx;
	}
}
/**
 * Comparator for Dist Objects
 * @author Tanvir
 *
 */
class sortDist implements Comparator<Dist>{
	@Override
	public int compare(Dist o1, Dist o2) {
		if(o1.dist<o2.dist)return -1;
		if(o1.dist>o2.dist)return 1;
		return 0;
	}
}
