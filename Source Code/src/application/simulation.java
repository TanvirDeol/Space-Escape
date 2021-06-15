package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * A class that contains various methods for finding optimal paths
 * @author Tanvir
 *
 */
public class simulation {
	String shortestRoute;
	double minDist;
	boolean[] vis;
	/**
	 * Conducts a depth-first-search using recursion from the Starting planets to all possible routes.
	 * Whenever it finds a path with a shorter distance, it updates the resulting path.
	 * @param p - Planet object
	 * @param dist - Distance traveled so far
	 * @param passengers - Passengers left on ship
	 * @param curRoute - String representation of route
	 */
	private void dfs(Planet p, double dist, long passengers,String curRoute) {
		System.out.println("Route-> "+curRoute);
		if(passengers <= 0) {
			if(dist<minDist) {shortestRoute = curRoute; minDist = dist;}
			return;
		}
		vis[p.id]=true;
		for(int i=0;i<p.connections.size();i++) {
			Planet next = p.connections.get(i);
			if(vis[next.id])continue;
			if(p.id != next.id) {
				dfs(next,dist+distance(p,next),passengers-(next.maxPopulation-next.population),curRoute+next.id+" ");
			}
		}
	}
	/**
	 * Conducts a modified Dijkstra algorithm to find shortest path back to home planet, 
	 * from any planet. Instead of just storing distances in an array, Planet id's are used as well
	 * so the program can backtrack the graph to find the route. Normal Dijkstra would just find 
	 * the value of the distance of the shortest path. 
	 * @param src - Planet to start from
	 * @param allPlanets - Array of all Planets
	 * @return - Returns an array to use for backtracking
	 */
	public ArrayList<Planet> shortestPath(Planet src, ArrayList<Planet>allPlanets) {
		//do actual shortest path algorithm, but record lengths with planet id's so you can backtrack 
		ArrayList<ArrayList<Node>>graph= new ArrayList<ArrayList<Node>>();
		for(int i=0;i<allPlanets.size();i++) {graph.add( new ArrayList<Node>());}
		for(int i=0;i<graph.size();i++) {
			Planet p = allPlanets.get(i);
			for(int j=0;j<p.connections.size();j++) {
				double dist = distance(p,p.connections.get(j));
				graph.get(p.id).add(new Node(p.connections.get(j),dist));
				graph.get(p.connections.get(j).id).add(new Node(p,dist));
			}
		}
		PriorityQueue<Node> pq = new PriorityQueue<>(new NodeSort());
		ArrayList<Pointer>dist = new ArrayList<Pointer>();
		for(int i=0;i<allPlanets.size();i++) {dist.add(new Pointer(-1,(double) Integer.MAX_VALUE));}
		dist.set(src.id, new Pointer(0.0));
		pq.add(new Node(src,0.0));
		while(!pq.isEmpty()) {
			Planet cur = pq.poll().pt;
			for(int i=0;i<graph.get(cur.id).size();i++) {
				Planet n = graph.get(cur.id).get(i).pt;
				double len = graph.get(cur.id).get(i).dist;
				if(dist.get(n.id).dist >dist.get(cur.id).dist+len) {
					dist.get(n.id).id= cur.id;
					dist.get(n.id).dist = dist.get(cur.id).dist+len;
					pq.add(new Node(n,dist.get(n.id).dist));
				}
			}
		}

		return backTrack(dist,src);
	}
	/**
	 * Initializes data for the depth-first-search function call. 
	 * @param src - Starting Planet
	 * @param ss - SpaceShip object
	 * @param allPlanets - Array of all planets
	 * @return - returns a ArrayList of Planets, which is the optimal route.
	 */
	public ArrayList<Planet> modifiedShortestPath(Planet src,SpaceShip ss, ArrayList<Planet>allPlanets) {
		//do a dfs that keeps expanding until the people remaining reaches 0. it records
		//the distance it took to get there, and chooses the option with the minimal distance.
		long people = ss.passengers;
		shortestRoute = "";
		vis = new boolean[allPlanets.size()];
		for(int i=0;i<vis.length;i++)vis[i]=false;
		minDist = Double.MAX_VALUE;
		dfs(src,0.0,people,"");
		String route = shortestRoute; 
		System.out.println(shortestRoute);
		return strToRoute(route);
		
	}
	/**
	 * Calculates distance between two planets
	 * @param p1 - Planet 1
	 * @param p2 - Planet 2
	 * @return - Distance
	 */
	public static double distance(Planet p1, Planet p2) {
		double dist= Math.sqrt(Math.pow(p2.x-p1.x,2.0)+Math.pow(p2.y-p1.y, 2.0));
		return dist;
	}
	/**
	 * Backtracks a graph with nodes representing the shortest distance to them, 
	 * in order to find the route from one planet to another.
	 * @param dist  - ArrayList of distance nodes from Dijkstra algorithm
	 * @param src - Planet to leave from
	 * @return - Returns a route of Planets
	 */
	public ArrayList<Planet> backTrack(ArrayList<Pointer> dist,Planet src){
		ArrayList<Planet> backRoute = new ArrayList<Planet>();
		int id = dist.get(0).id;
		backRoute.add(Planet.allPlanets.get(0));
		while(id != src.id) {
			backRoute.add(Planet.allPlanets.get(id));
			id = dist.get(id).id;
		}
		Collections.reverse(backRoute);
		return backRoute;
	}
	/**
	 * Converts a string representation of a route to a Planet representation. 
	 * This is done by using Planet id's
	 * @param route - String of route
	 * @return - ArrayList of Planets
	 */
	public ArrayList<Planet> strToRoute(String route){
		Scanner scan = new Scanner(route);
		ArrayList<Planet> r = new ArrayList<Planet>();
		while(scan.hasNextInt()) {
			int id = scan.nextInt();
			r.add(Planet.allPlanets.get(id));
		}
		scan.close();
		return r;
	}
	/**
	 * Creates a line object between one planet and another. 
	 * A line shows that both planets have a mutual connection
	 * and will allow passengers.
	 * @param a - Planet A
	 * @param b - Planet B
	 * @return - A Line Sprite
	 */
	public static Line createEdge(Planet a,Planet b) {
		Line ln = new Line(a.x,a.y,b.x,b.y);
		ln.setStroke(Color.YELLOW);
		ln.setStrokeWidth(6.0);
		ln.setOpacity(0.5);
		return ln;
	}
}
/**
 * Nodes are what are used in the graph. 
 * They hold a planet and the dist is the distance between that 
 * planet and the src planet
 * @author Tanvir
 *
 */
class Node {
	Planet pt;
	double dist;
	public Node(Planet p, double d) {
		this.pt =p;
		this.dist = d;
	}
}
/**
 * Class that implements a Comparator for the Node
 * @author Tanvir
 *
 */
class NodeSort implements Comparator<Node>{
	@Override
	public int compare(Node o1, Node o2) {
		if(o1.dist<o2.dist)return -1;
		if(o1.dist>o2.dist)return 1;
		return 0;
	}
}
/**
 * A Pointer is used in the resulting ArrayList of the modified Dijkstra.
 * Each planet has its dist, which is the length of the shortest distance to it
 * and it has an id, which is the id of the planet that comes before it in the optimal path.
 * The id allows for backtracking to find the actual route.
 * @author Tanvir
 *
 */
class Pointer{
	int id;
	double dist;
	public Pointer(int x,double d) {
		this.id =x;
		this.dist =d;
	}
	public Pointer(double d) {
		this.dist = d;
	}
}