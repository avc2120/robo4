import java.util.*;
import java.io.*;

public class PathPlanning
{
	private static Vertex start;
	private static Vertex goal;
	private static ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	private static ArrayList<Obstacle> sanitized_obstacles = new ArrayList<Obstacle>();
	private static ArrayList<Obstacle> grown_obstacles = new ArrayList<Obstacle>();
	private static ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	private static ArrayList<Vertex> grown_vertices = new ArrayList<Vertex>();
	public static final double robot_width = 0.35;
	
	public static void main(String[] args)
	{
		readStartGoal("hw4_start_goal.txt");
		System.out.println("Successfully Read Start and Goal File");
		readObstacles("hw4_world_and_obstacles_convex.txt");
		System.out.println("Successfully Read Obstacles File");

		//check for overlaps
		for(int i = 0; i < obstacles.size(); i++)
		{
			for(int j=i; j < obstacles.size(); j++)
			{
				ArrayList<Vertex> new_obstacle_vertices = new ArrayList<Vertex>();
				if(!obstacles.get(i).isOverlap(obstacles.get(j)).isEmpty() || !obstacles.get(j).isOverlap(obstacles.get(i)).isEmpty())
				{
					ArrayList<Vertex> overlapping_vertices = obstacles.get(i).isOverlap(obstacles.get(j));
					overlapping_vertices.addAll(obstacles.get(j).isOverlap(obstacles.get(i)));
					new_obstacle_vertices.addAll(obstacles.get(i).getVertices());
					new_obstacle_vertices.addAll(obstacles.get(j).getVertices());
					for(Vertex overlap_vertex: overlapping_vertices)
					{
						new_obstacle_vertices.remove(overlap_vertex);
					}
					sanitized_obstacles.add(new Obstacle(new_obstacle_vertices));
				}
			}
		}
		System.out.println("Succesfully Checked for Overlaps");
		//grow convex hull of objects
		for(Obstacle obstacle: sanitized_obstacles)
		{
			Obstacle grownObstacle = obstacle.growObstacles(robot_width);
			grown_obstacles.add(grownObstacle);
			grown_vertices.addAll(grownObstacle.getVertices());
		}
		System.out.println("Successfull Grown Obstacles");
		// buildAdjacency(vertices);
		// dijkstra(start);
		// for(Vertex v: vertices)
		// {
		// 	List<Vertex> path = getShortestPathTo(v);
		// 	System.out.println("Path: " + path);
		// }
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
            	System.out.println(line);
                String[] points = line.split(" ");
                start = new Vertex(Double.parseDouble(points[0]), Double.parseDouble(points[1]));
                vertices.add(start);
            }
            if(in.hasNext()) 
            {
            	String line = in.nextLine();
                String[] points = line.split(" ");
                goal = new Vertex(Double.parseDouble(points[0]), Double.parseDouble(points[1]));
                vertices.add(goal);
            }  
            in.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        }

	}

	public static void readObstacles(String fileName)
	{
		try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            Scanner in = new Scanner(bufferedReader);

            int num_obstacles = in.nextInt();
			in.nextLine();
			for(int i = 0; i < num_obstacles; i++)
			{
				int num_vertices = in.nextInt();
				System.out.println(num_vertices);
				in.nextLine();
				ArrayList<Vertex> object_vertices = new ArrayList<Vertex>();
				for(int j = 0; j < num_vertices; j++)
				{
					String[] points = in.nextLine().split(" ");
					object_vertices.add(new Vertex(Double.parseDouble(points[0]), Double.parseDouble(points[1])));	
				}
				System.out.println(object_vertices);
				Obstacle o = new Obstacle(object_vertices);
				obstacles.add(o);
			}
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        }
	}

	
	public static void dijkstra(Vertex source)
    {
        source.minDistance = 0.0;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
      	vertexQueue.add(source);

		while (!vertexQueue.isEmpty()) 
		{
		    Vertex u = vertexQueue.poll();

	        for (Edge e : u.adjacencies)
	        {
	            Vertex v = e.target;
	            double weight = e.weight;
	            double distanceThroughU = u.minDistance + weight;
			if (distanceThroughU < v.minDistance) {
			    vertexQueue.remove(v);
			    v.minDistance = distanceThroughU ;
			    v.previous = u;
			    vertexQueue.add(v);
			}
            }
        }
    }

    public static List<Vertex> getShortestPathTo(Vertex target)
    {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
        {
            path.add(vertex);
        }
        Collections.reverse(path);
        return path;
    }

    public static void buildAdjacency(List<Obstacle> grown_obstacles)
    {
    	return;
    	// for(Obstacle obstacle: grown_obstacles)
    	// {
    	// 	for (Vertex v: obstacle.getVertices())
    	// 	{
    	// 		if(ov != v)
    	// 		{
    	// 			double dist = Vertex.distance(v,ov);
    	// 			v.adjacencies.add(new Edge(ov, dist));
    	// 		}
    	// 	}
    	// }
    }


}