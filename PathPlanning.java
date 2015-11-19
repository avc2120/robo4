/************************************************************************
 * Alice Chang (avc2120), Phillip Godzin (pgg2105), Martin Ong (mo2454)
 * Computational Aspects of Robotics
 * FALL 2015
**************************************************************************/
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class PathPlanning extends JFrame
{
	private static Vertex start;
	private static Vertex goal;
	private static Obstacle room;
	private static ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	private static ArrayList<Obstacle> grown_obstacles = new ArrayList<Obstacle>();
	private static ArrayList<Vertex> grown_vertices = new ArrayList<Vertex>();
	private static ArrayList<ArrayList<Vertex>> paths = new ArrayList<ArrayList<Vertex>>();
	public static final double robot_width = 0.35;
	
	public PathPlanning() 
	{
        setTitle("Path Planning");
        setSize(new Dimension(650, 350));
        setVisible(true);
    }

	public static void main(String[] args)
	{	
		readStartGoal("hw4_start_goal.txt");
		System.out.println("Successfully Read Start and Goal File");
		readObstacles("hw4_world_and_obstacles_convex.txt");
		System.out.println("Successfully Read Obstacles File");
		for(Obstacle o: obstacles.subList(1, obstacles.size()))
		{
			Obstacle grownObstacle = o.convexHull();
			grownObstacle = grownObstacle.growObstacles(robot_width/2);
			grown_obstacles.add(grownObstacle);
			grown_vertices.addAll(grownObstacle.getVertices());
		}
		System.out.println("Successfuly Grown Obstacles");
		
		buildAdjacency(grown_vertices);
		System.out.println("Successfully Built Adjacency Matrix");
		
		dijkstra(start);
		System.out.println("Successfully Finished Dijkstra's");
		
		for(Vertex v: grown_vertices)
		{
			ArrayList<Vertex> path = getShortestPathTo(v);
			paths.add(path);
		}
		PathPlanning pathPlanning = new PathPlanning();
		writePathsToFile("path.txt");
	}
	
	public static void writePathsToFile(String fileName) {
		try {
			FileWriter fw = new FileWriter(fileName);
            ArrayList<Vertex> path = getShortestPathTo(goal);
			for (Vertex v : path) {
				fw.write(v.toString() + "\n");
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readStartGoal(String fileName)
	{
		try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            Scanner in = new Scanner(bufferedReader);
            if(in.hasNext()) 
            {
            	String line = in.nextLine();
                String[] points = line.split(" ");
                start = new Vertex(Double.parseDouble(points[0]), Double.parseDouble(points[1]));
                grown_vertices.add(start);
            }
            if(in.hasNext()) 
            {
            	String line = in.nextLine();
                String[] points = line.split(" ");
                goal = new Vertex(Double.parseDouble(points[0]), Double.parseDouble(points[1]));
                grown_vertices.add(goal);
            }  
            in.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        }

	}

	public static void readObstacles(String fileName)
	{
		try 
		{
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            Scanner in = new Scanner(bufferedReader);

            int num_obstacles = in.nextInt();
			in.nextLine();
			for(int i = 0; i < num_obstacles; i++)
			{
				int num_vertices = in.nextInt();
				in.nextLine();
				ArrayList<Vertex> object_vertices = new ArrayList<Vertex>();
				for(int j = 0; j < num_vertices; j++)
				{
					String[] points = in.nextLine().split(" ");
					object_vertices.add(new Vertex(Double.parseDouble(points[0]), Double.parseDouble(points[1])));	
				}
				obstacles.add(new Obstacle(object_vertices));
				if(i == 0)
				{
					room = new Obstacle(object_vertices);
				}
			}
			in.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        }
	}
	
	public static void dijkstra(Vertex start)
    {
		//set initial source minimum distance to 0
        start.minDistance = 0.0;
        //priority queue sorted by minimum distance
        PriorityQueue<Vertex> queue = new PriorityQueue<Vertex>();
        //put start into queue
        queue.add(start);
		while (!queue.isEmpty()) 
		{
		    Vertex u = queue.poll();
	        for (Edge e : u.adjacencies)
	        {
	            Vertex v = e.target;
		        double dist = u.minDistance + e.weight;
		        //if found new better path, add it to the queue
		        if (dist < v.minDistance) 
				{
		        	queue.remove(v);
					v.minDistance = dist;
					v.previous = u;
					queue.add(v);
				}
            }
        }
    }

    public static ArrayList<Vertex> getShortestPathTo(Vertex target)
    {
        ArrayList<Vertex> path = new ArrayList<Vertex>();
        //iterates through the shortest path to specified vertex backwards
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
        {
            path.add(vertex);
        }
        //reverse list to get path from start
        Collections.reverse(path);
        return path;
    }

    public static void displayAdjacency(Graphics g)
    {
    	double maxw = 0.0;

    	for (Vertex v : grown_vertices) 
    	{
    		for (Edge e : v.adjacencies) 
    		{
    			if(e.weight != Double.POSITIVE_INFINITY)
    			{
    				maxw = Math.max(e.weight, maxw);
    			}
    		}
    	}
    	
    	for(Vertex v: grown_vertices)
    	{
    		for(Edge e: v.adjacencies)
    		{
    			Vertex dest = e.target;
    			Color color = e.weight == Double.POSITIVE_INFINITY? new Color(0,0,0,0):Color.blue;
  
	            g.setColor(color);
	            g.drawLine((int) (v.y * 40 + 160), (int) (v.x * 40 + 180),
	                    (int) (dest.y * 40 + 160), (int) (dest.x * 40 + 180));
    		}
    	}
    }
    
    public static void buildAdjacency(List<Vertex> grown_vertices) 
    {
        for (Vertex v : grown_vertices) 
        {
            for (Vertex ov : grown_vertices.subList(grown_vertices.indexOf(v), grown_vertices.size())) 
            {
                if (!ov.equals(v)) 
                {
                	boolean edge_added = false;
                	for(Obstacle obs: grown_obstacles)
                	{
                		Vertex mid = v.translate(ov).multiply(0.5);
                		//If vertex not in room
                		if(obs.isInterior(mid) && !obs.equals(room))
                		{
                			v.adjacencies.add(new Edge(ov, Double.POSITIVE_INFINITY));
                			edge_added = true;
                			break;
                		}
                		//If not adjacent points in obstacle
                		else if(obs.notAdjPointsInObstacle(v, ov)) 
						{
							v.adjacencies.add(new Edge(ov, Double.POSITIVE_INFINITY));
							edge_added = true;
							break;
						}
                		else if (!room.isInterior(v) || !room.isInterior(ov) || room.intersectObstacle(v,  ov))
    					{
							v.adjacencies.add(new Edge(ov, Double.POSITIVE_INFINITY));
							edge_added = true;
							break;
						}
//                		//If intersect other polygons
                		else if(obs.intersectObstacle(ov, v))
						{
							v.adjacencies.add(new Edge(ov, Double.POSITIVE_INFINITY));
							edge_added = true;
							break;
						}
                	}
                	if(edge_added == false)
                	{
	                    double dist = Vertex.distance(v, ov);
	                    v.adjacencies.add(new Edge(ov, dist));
	                    ov.adjacencies.add(new Edge(v, dist));
                	}
                 }
            }  
        }
    }

    public void paint(Graphics g) 
    {
    	//draws original obstacles
        g.setColor(Color.cyan);
        for (Obstacle o : obstacles) {
            // draw obstacles
            List<Vertex> vertices = o.getVertices();
            for (int i = 0; i < vertices.size() - 1; i++) {
                g.drawLine((int) (vertices.get(i).y * 40 + 160), (int) (vertices.get(i).x * 40 + 180),
                        (int) (vertices.get(i + 1).y * 40 + 160), (int) (vertices.get(i + 1).x * 40 + 180));
            }
            g.drawLine((int) (vertices.get(0).y * 40 + 160), (int) (vertices.get(0).x * 40 + 180),
                    (int) (vertices.get(vertices.size() - 1).y * 40 + 160), (int) (vertices.get(vertices.size() - 1).x * 40 + 180));
        }
        //draws grown objects
        g.setColor(Color.black);
        for (Obstacle o : grown_obstacles) {
            // draw obstacles
            List<Vertex> vertices = o.getVertices();
            for (int i = 0; i < vertices.size() - 1; i++) {
                g.drawLine((int) (vertices.get(i).y * 40 + 160), (int) (vertices.get(i).x * 40 + 180),
                        (int) (vertices.get(i + 1).y * 40 + 160), (int) (vertices.get(i + 1).x * 40 + 180));
        	}
            g.drawLine((int) (vertices.get(0).y * 40 + 160), (int) (vertices.get(0).x * 40 + 180),
                    (int) (vertices.get(vertices.size() - 1).y * 40 + 160), (int) (vertices.get(vertices.size() - 1).x * 40 + 180));
        }
        g.setColor(Color.blue);
        displayAdjacency(g);
        //draw start and end point
        g.setColor(Color.red);
        g.drawArc((int) (start.y * 40 + 155), (int) (start.x * 40 + 175), 10, 10, 0, 360);
        g.drawArc((int) (goal.y * 40 + 155), (int) (goal.x * 40 + 175), 10, 10, 0, 360);
        
        //draw paths
        g.setColor(Color.green);
        ArrayList<Vertex> path = getShortestPathTo(goal);
        for (int i = 0; i < path.size()-1; i++)
        {
			Vertex v = path.get(i);
			Vertex ov = path.get(i+1);
	    	g.drawLine((int) (v.y * 40 + 160), (int) (v.x * 40 + 180), (int) (ov.y * 40 + 160), (int) (ov.x * 40 + 180));
        }
    }

    public void printObstacles(ArrayList<Obstacle> obstacles)
    {
    	for(Obstacle o: obstacles)
    	{
    		System.out.println(o);
    	}
    }
    
    public void drawAllShortestPathToEachVertex(Graphics g)
    {
	    for (ArrayList<Vertex> path: paths)
	    {
	    	if(paths.indexOf(path) == paths.size()-1)
	    	{
	    		g.setColor(Color.yellow);
	    	}
	    	for(int i = 0; i < path.size()-1; i++)
	    	{
	    		Vertex v = path.get(i);
	    		Vertex ov = path.get(i+1);
		    	g.drawLine((int) (v.y * 40 + 160), (int) (v.x * 40 + 180), (int) (ov.y * 40 + 160), (int) (ov.x * 40 + 180));
	    	}
        }
    }


}