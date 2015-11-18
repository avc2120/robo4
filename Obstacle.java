import java.util.*;
import java.awt.geom.Line2D;
public class Obstacle
{
	private ArrayList<Vertex> vertices;
	private int num_vertices;
	public Obstacle()
	{
		vertices = new ArrayList<Vertex>();
		num_vertices = 0;
	}

	public Obstacle(ArrayList<Vertex> vertices)
	{
		this.vertices = vertices;
		num_vertices = vertices.size();
	}

	public void addVertex(Vertex p)
	{
		vertices.add(p);
		num_vertices++;
	}

	public ArrayList<Vertex> getVertices()
	{
		return vertices;
	}

	public Obstacle growObstacles(double robot_width)
	{
		Obstacle new_obstacle = new Obstacle();
		Vertex left, right, normleft, normright, p;
		Vertex eleft1, eleft2, eright1, eright2; // extended points
		Vertex center = getCentroid();
		Vertex new_vertex = new Vertex();
		
		/* move the line segments out */
		for(int i = 0; i < num_vertices; i++) 
		{
			// grab two edges
			left = this.vertices.get((i-1 + num_vertices) % num_vertices);
			new_vertex = this.vertices.get(i % num_vertices);
			right = this.vertices.get((i+1) % num_vertices);
			
			/* turn the normals right side out (center is always inside the polygon */
			normleft  = left.subtract(new_vertex).perpendicular().unit();
			normright = new_vertex.subtract(right).perpendicular().unit();

			if(normleft.dot(new_vertex.subtract(center)) < 0)
			{
				normleft = normleft.multiply(-1.0);
			}
			if(normright.dot(new_vertex.subtract(center)) < 0)
			{
				normright = normright.multiply(-1.0);
			}
			normleft = normleft.multiply(robot_width);
			normright = normright.multiply(robot_width);
			
			/* move the points out */
			eleft1 = left.translate(normleft);
			eleft2 = new_vertex.translate(normleft);
			eright1 = new_vertex.translate(normright);
			eright2 = right.translate(normright);
			
			/* find the intersection */
			new_vertex = Vertex.lineIntersection(eleft1, eleft2, eright1, eright2);
					
			new_obstacle.addVertex(new_vertex);
		}
		return new_obstacle;
	}

	public Vertex getCentroid()
	{
		Vertex center = new Vertex(0.0, 0.0);
		for(int i = 0; i < num_vertices; i++) {
			center = center.translate(vertices.get(i));
		}
		center = center.multiply(1/(double)num_vertices);
		return center;
	}

	/*Checks if Vertex is inside Obstacle
	The number of intersections for a ray passing from exterior of the polygon to any point
	If odd, point lies inside, and else otherwise*/
	public boolean isInterior(Vertex v)
	{
		Vertex center = getCentroid();
		Vertex direction = v.subtract(center);
		Vertex v2 = v.translate(direction);
		Vertex v3, v4, inter;
		int count = 0;
		for(int i = 0; i < num_vertices; i++) {
			v3 = this.vertices.get(i % num_vertices).clone();
			v4 = this.vertices.get((i+1) % num_vertices).clone();
			inter = Vertex.rayIntersects(v,v2, v3,v4);
			if(inter != null) {
				if(inter.equals(v))
					continue;
				count += 1;
			}
		}
		return count % 2 == 1;
	}

	public ArrayList<Vertex> isOverlap(Obstacle obstacle)
	{
		ArrayList<Vertex> vertices = obstacle.getVertices();
		ArrayList<Vertex> overlapping_vertices = new ArrayList<Vertex>();
		for(Vertex vertex: vertices)
		{
			if(this.isInterior(vertex))
			{
				overlapping_vertices.add(vertex);
			}
		}
		return overlapping_vertices;
	}
}