import java.util.*;
public class PathPlanning
{
	private Point start;
	private Point goal;
	private static ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	private static ArrayList<Point> vertices = new ArrayList<Point>();
	public static void main(String[] args)
	{
		readStartGoal('hw4_start_goal.txt');
		readObstacles('hw4_world_and_obstacles_convex.txt');
		dijkstra(source);
		for(Vertex v: vertices)
		{
			List<Vertex> path = getShortestPathTo(v);
			System.out.println("Path: " + path);
		}
	}

	public void readStartGoal(String fileName)
	{
		try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            if((line = bufferedReader.readLine()) != null) 
            {
                double[] points = line.split(" ");
                start = new Vertex(points[0], points[1]);
                vertices.add(start);
            }
            if((line = bufferedReader.readLine()) != null) 
            {
                double[] points = line.split(" ");
                goal = new Vertex(points[0], points[1]);
                vertices.add(goal);
            }  
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        }

	}

	public void readObstacles(String fileName)
	{
		try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            Scanner in = new Scanner(bufferedReader);
			int num_obstacles = in.nextInt();
			for(int i = 0; i < num_obstacles; i++)
			{
				int num_vertices = in.nextInt();
				Obstacle obstacle = new obstacle();
				for(int j = 0; j < num_vertices; j++)
				{
					double[] points = in.nextLine().split(" ");
					nodes.add(new Vertex(points[0], points[1]));
					obstacle.addVertex(new Vertex(points[0], points[1]));
				}
				obstacles.add(obstacle);
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
}