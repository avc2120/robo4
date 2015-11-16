import java.util.*;
import java.io.*;

public class PathPlanning
{
	private static Vertex start;
	private static Vertex goal;
	private static ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	private static ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	public static final int robot_width = 0.35;
	
	public static void main(String[] args)
	{
		readStartGoal("hw4_start_goal.txt");
		readObstacles("hw4_world_and_obstacles_convex.txt");
		buildAdjacency(vertices);
		dijkstra(start);
		for(Vertex v: vertices)
		{
			List<Vertex> path = getShortestPathTo(v);
			System.out.println("Path: " + path);
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
				in.nextLine();

				Obstacle obstacle = new Obstacle();
				for(int j = 0; j < num_vertices; j++)
				{
					String[] points = in.nextLine().split(" ");
					for(String p: points)
					{
						System.out.println(p);
					}
					vertices.add(new Vertex(Double.parseDouble(points[0]), Double.parseDouble(points[1])));
					obstacle.addVertex(new Vertex(Double.parseDouble(points[0]), Double.parseDouble(points[1])));
				}
				obstacles.add(obstacle);
			}
			in.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        }
	}
	public Polygon growObstacles(double amount)
	{
		assert vertices.size() == numVertices;
		
		Polygon poly = clone();
		Point left, right, normleft, normright, p;
		Point eleft1, eleft2, eright1, eright2; // extended points
		Point center = new Point(0,0);
		int i;
		/* calculate the center of the polygon */
		for(i = 0; i < numVertices; i++)
			center = center.translate(vertices.get(i));
		center = center.mult(1/((double)numVertices));
		
		/* move the line segments out */
		for(i = 0; i < numVertices; i++) {
			// grab two edges
			left = this.vertices.get((i-1 + numVertices) % numVertices);
			p = this.vertices.get(i % numVertices);
			right = this.vertices.get((i+1) % numVertices);
			
			/* turn the normals right side out (center is always inside the polygon */
			normleft  = left.sub(p).perpendicular().unit();
			normright = p.sub(right).perpendicular().unit();
			if(normleft.dot(p.sub(center)) < 0)
				normleft = normleft.mult(-1.0);
			if(normright.dot(p.sub(center)) < 0)
				normright = normright.mult(-1.0);
			normleft = normleft.mult(amount);
			normright = normright.mult(amount);
			
			/* move the points out */
			eleft1 = left.translate(normleft);
			eleft2 = p.translate(normleft);
			eright1 = p.translate(normright);
			eright2 = right.translate(normright);
			
			/* find the intersection */
			p = intersectLines(eleft1, eleft2, eright1, eright2);
			assert p != null;
					
			poly.vertices.set(i, p);
		}
		return poly;
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

    public static void buildAdjacency(List<Vertex> vertices)
    {
    	for(Vertex v: vertices)
    	{
    		for (Vertex ov: vertices)
    		{
    			if(ov != v)
    			{
    				double dist = Vertex.distance(v,ov);
    				v.adjacencies.add(new Edge(ov, dist));
    			}
    		}
    	}
    }


}