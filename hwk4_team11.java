import java.util.*;
public class Hwk4_team11
{
	private Point start;
	private Point goal;
	public static void main(String[] args)
	{

	}

	public void readStartGoal(String filename)
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
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }

	}

	public void readObstacles(String filename)
	{

	}
}