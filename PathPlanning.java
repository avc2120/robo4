import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.List;

public class PathPlanning extends JFrame
{
	private static Vertex start;
	private static Vertex goal;
	private static ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	private static ArrayList<Obstacle> grown_obstacles = new ArrayList<Obstacle>();
	private static ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	private static ArrayList<Vertex> grown_vertices = new ArrayList<Vertex>();
	private static ArrayList<List<Vertex>> paths = new ArrayList<List<Vertex>>();
	public static final double robot_width = 0.35;
	
	public PathPlanning() 
	{
        setTitle("Path Planning");
        setSize(new Dimension(650, 350));
        setVisible(true);
    }

	public static void main(String[] args)
	{	
		Vertex v1 = new Vertex(3,7);
		Vertex v2 = new Vertex(5,7);
		Vertex v3 = new Vertex(2,3);
		Vertex v4 = new Vertex(1,9);
		System.out.println(Vertex.intersection(v1,v2,v3,v4));

		readStartGoal("hw4_start_goal.txt");
		System.out.println("Successfully Read Start and Goal File");
		readObstacles("hw4_world_and_obstacles_convex.txt");
		System.out.println("Successfully Read Obstacles File");
		for(int i = 0; i < obstacles.size(); i++)
		{
			if(i > 0)
			{
				Obstacle o = obstacles.get(i).makeConvex();
				o = o.growObstacles(robot_width/2);
				grown_obstacles.add(o);
			}
		}
		// for(Obstacle o: obstacles.subList(1, obstacles.size()))
		// {
		// 	Obstacle grownObstacle = o.makeConvex();
		// 	grownObstacle = grownObstacle.growObstacles(robot_width/((double)2));
		// 	grown_obstacles.add(grownObstacle);
		// 	grown_vertices.addAll(grownObstacle.getVertices());
		// }

		System.out.println("Successfuly Grown Obstacles");
		PathPlanning pathPlanning = new PathPlanning();
		System.out.print("Original: ");
		System.out.println(obstacles);
		System.out.println("Grown: ");
		System.out.println(grown_obstacles);
		buildAdjacency(grown_vertices);
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
				obstacles.add(new Obstacle(object_vertices));
				if(i==2)
				{
					break;
				}
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

    public static void buildAdjacency(List<Vertex> vertices) {
        for (Vertex v : vertices) {
            for (Vertex ov : vertices) {
                if (ov != v) {
                    double dist = Vertex.distance(v, ov);
                    v.adjacencies.add(new Edge(ov, dist));
                }
            }
        }
    }

    public void paint(Graphics g) 
    {
        g.setColor(Color.cyan);
        for (Obstacle o : obstacles) {
            // draw obstacles
            List<Vertex> vertices = o.getVertices();
            for (int i = 0; i < vertices.size() - 1; i++) {
                /*System.out.println((int) (vertices.get(i).y * 40 + 160) + ", " + (int) (vertices.get(i).x * 40 + 180)
                        + ", " + (int) (vertices.get(i + 1).y * 40 + 160) + ", " + (int) (vertices.get(i + 1).x * 40 + 180));*/
                g.drawLine((int) (vertices.get(i).y * 40 + 160), (int) (vertices.get(i).x * 40 + 180),
                        (int) (vertices.get(i + 1).y * 40 + 160), (int) (vertices.get(i + 1).x * 40 + 180));
            }
            /*System.out.println((int) (vertices.get(0).y * 40 + 160) + ", " + (int) (vertices.get(0).x * 40 + 180)
                    + ", " + (int) (vertices.get(vertices.size()-1).y * 40 + 160) + ", " + (int) (vertices.get(vertices.size()-1).x * 40 + 180));*/
            g.drawLine((int) (vertices.get(0).y * 40 + 160), (int) (vertices.get(0).x * 40 + 180),
                    (int) (vertices.get(vertices.size() - 1).y * 40 + 160), (int) (vertices.get(vertices.size() - 1).x * 40 + 180));
        }

        g.setColor(Color.blue);
        for (Obstacle o : grown_obstacles) {
            // draw obstacles
            List<Vertex> vertices = o.getVertices();
            for (int i = 0; i < vertices.size() - 1; i++) {
                /*System.out.println((int) (vertices.get(i).y * 40 + 160) + ", " + (int) (vertices.get(i).x * 40 + 180)
                        + ", " + (int) (vertices.get(i + 1).y * 40 + 160) + ", " + (int) (vertices.get(i + 1).x * 40 + 180));*/
                g.drawLine((int) (vertices.get(i).y * 40 + 160), (int) (vertices.get(i).x * 40 + 180),
                        (int) (vertices.get(i + 1).y * 40 + 160), (int) (vertices.get(i + 1).x * 40 + 180));
        	}
            /*System.out.println((int) (vertices.get(0).y * 40 + 160) + ", " + (int) (vertices.get(0).x * 40 + 180)
                    + ", " + (int) (vertices.get(vertices.size()-1).y * 40 + 160) + ", " + (int) (vertices.get(vertices.size()-1).x * 40 + 180));*/
            g.drawLine((int) (vertices.get(0).y * 40 + 160), (int) (vertices.get(0).x * 40 + 180),
                    (int) (vertices.get(vertices.size() - 1).y * 40 + 160), (int) (vertices.get(vertices.size() - 1).x * 40 + 180));
        }


        //draw start and end point
        g.setColor(Color.red);
        g.drawArc((int) (start.y * 40 + 155), (int) (start.x * 40 + 175), 10, 10, 0, 360);
        g.drawArc((int) (goal.y * 40 + 155), (int) (goal.x * 40 + 175), 10, 10, 0, 360);
        System.out.println((int) (paths.get(0).get(0).y * 40 + 160) + ", " + (int) (paths.get(0).get(0).x * 40 + 180)
                + ", " + (int) (paths.get(0).get(0).y * 40 + 160) + ", " + (int) (paths.get(0).get(0).x * 40 + 180));

        for (int j = 1; j < paths.size() - 2; j++) {
            List<Vertex> vertices = paths.get(j);
            // draw paths
            g.setColor(Color.blue);
            g.drawLine((int) (vertices.get(0).y * 40 + 160), (int) (vertices.get(0).x * 40 + 180),
                    (int) (vertices.get(1).y * 40 + 160), (int) (vertices.get(1).x * 40 + 180));

        }
    }

    public void printObstacles(ArrayList<Obstacle> obstacles)
    {
    	for(Obstacle o: obstacles)
    	{
    		System.out.println(o);
    	}
    }


}