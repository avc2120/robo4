import java.util.*;

public class Obstacle
{
	private List<Vertex> vertices;
	public Obstacle()
	{
		vertices = new ArrayList<Vertex>();
	}

	public void addVertex(Vertex p)
	{
		vertices.add(p);
	}

	public List<Vertex> getVertices()
	{
		return vertices;
	}
}