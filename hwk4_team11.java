import java.util.*;
public class Hwk4_team11
{
	private Point start;
	private Point goal;
	private static ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	public static void main(String[] args)
	{

	}

	public void readStartGoal(String fileName)
	{
		try {
            FileReader fileReader = new FileReader(fileName);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            if((line = bufferedReader.readLine()) != null) 
            {
                double[] points = line.split(" ");
                start = new Point(points[0], points[1]);
            }
            if((line = bufferedReader.readLine()) != null) 
            {
                double[] points = line.split(" ");
                goal = new Point(points[0], points[1]);
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
					obstacle.addVertex(new Point(points[0], points[1]));
				}
				obstacles.add(obstacle);
			}         
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        }
	}
}