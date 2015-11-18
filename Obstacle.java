import java.util.*;
import java.awt.geom.Line2D;
public class Obstacle
{
	public ArrayList<Vertex> vertices;
	public int num_vertices;
	public Obstacle()
	{
		vertices = new ArrayList<Vertex>();
		num_vertices = 0;
	}

	public Obstacle(int num)
	{
		vertices = new ArrayList<Vertex>();
		for(int i = 0; i < num; i++)
		{
			vertices.add(new Vertex());
		}
		num_vertices = num;
	}
	public Obstacle(ArrayList<Vertex> vertices)
	{
		this.vertices = vertices;
		num_vertices = vertices.size();
	}

	public void addVertex(Vertex v)
	{
		vertices.add(v);
		num_vertices++;
	}

	public void setVertices(ArrayList<Vertex> vertices)
	{
		this.vertices = vertices;
		num_vertices = vertices.size();
	}

	public void setVertex(int i, Vertex v)
	{
		vertices.set(i, v);
	}

	public Vertex getVertex(int i)
	{
		return vertices.get(i);
	}

	public ArrayList<Vertex> getVertices()
	{
		return vertices;
	}

	public int getSize()
	{
		return num_vertices;
	}

	public Obstacle clone()
	{
		Obstacle new_obstacle = new Obstacle();
		new_obstacle.setVertices((ArrayList<Vertex>)this.vertices.clone());
		for(int i = 0; i < new_obstacle.getSize(); i++)
		{
			new_obstacle.setVertex(i, new_obstacle.getVertex(i).clone());
		}
		return new_obstacle;
	}

	public Obstacle growObstacles(double robot_width)
	{	
		Obstacle new_obstacle = clone();
		Vertex left, right, normleft, normright, p;
		Vertex eleft1, eleft2, eright1, eright2;
		Vertex center = getCentroid();
		Vertex new_vertex = new Vertex();
		System.out.println("Num Vertices: " + num_vertices);
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
					
			new_obstacle.setVertex(i, new_vertex);
		}
		return new_obstacle;
	}

	public Vertex getCentroid()
	{
		Vertex center = new Vertex(0.0, 0.0);
		for(int i = 0; i < num_vertices; i++) 
		{
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

	public String toString()
	{
		String result = "";
		for(Vertex v: vertices)
		{
			result = result + v + ", ";
		}
		return result;
	}

	public Obstacle convexHull()
	{
		// System.out.println(vertices);
		Obstacle p = clone();
		ArrayList<Vertex> pts = (ArrayList<Vertex>)vertices.clone();
		
		//sort!!!
		for(int i = 0; i < pts.size(); i++)
		{
			for(int j = i; j < pts.size(); j++)
			{
				if(pts.get(j).x < pts.get(i).x)
					swap(pts, i, j);
				else if(pts.get(j).x == pts.get(i).x)
				{
					if(pts.get(j).y < pts.get(i).y)
						swap(pts,i,j);
				}
			}
		}
		
		ArrayList<Vertex> lower = new ArrayList<Vertex>();
		for(Vertex pt : pts)
		{
			while(lower.size() >= 2 && Vertex.ccw(lower.get(lower.size()-2), lower.get(lower.size()-1), pt ) <= 0)
				lower.remove(lower.size()-1); //pop
			lower.add(pt);
		}
		lower.remove(lower.size()-1);
		
		ArrayList<Vertex> upper = new ArrayList<Vertex>();

		//sort reverse!!!
		for(int i = 0; i < pts.size(); i++)
		{
			for(int j = i; j < pts.size(); j++)
			{
				if(pts.get(j).x > pts.get(i).x)
					swap(pts, i, j);
				else if(pts.get(j).x == pts.get(i).x)
				{
					if(pts.get(j).y > pts.get(i).y)
						swap(pts,i,j);
				}
			}
		}		

		for(Vertex pt : pts)
		{
			while(upper.size() >= 2 && Vertex.ccw(upper.get(upper.size()-2), upper.get(upper.size()-1), pt ) <= 0)
				upper.remove(upper.size()-1); //pop
			upper.add(pt);
		}
		upper.remove(upper.size()-1);
		
		lower.addAll(upper);
		
		p.vertices = (ArrayList<Vertex>)(lower.clone());
		p.num_vertices = p.vertices.size();
		return p;
	}


	public boolean intersectPolygon(Vertex p1, Vertex p2)
	{
		Vertex p3, p4, inter;
		for(int i = 0; i < num_vertices; i++) {
			p3 = this.vertices.get(i % num_vertices).clone();
			p4 = this.vertices.get((i+1) % num_vertices).clone();
			inter = Vertex.intersectLineSegments(p1,p2, p3,p4);
			if(inter != null){
				if(!(inter.equals(p1) || inter.equals(p2))) {
					return true;
				}
			}
			/* allow movement along a polygon */
			if((p1.equals(p3) && p2.equals(p4)) || (p2.equals(p3) && p1.equals(p4))) {
				return false;
			}
		}
		return false;
	}
	
	public boolean notAdjPointsInObstacle(Vertex v1, Vertex v2)
	{
		if (this.vertices.contains(v1) && this.vertices.contains(v2))
		{
			if(!(Math.abs(this.vertices.indexOf(v1)- this.vertices.indexOf(v2)) == 1 || 
					Math.abs(this.vertices.indexOf(v1)- this.vertices.indexOf(v2)) == vertices.size()-1))
				return true;
			else
				return false;
		}
		return false;
	}
	
	private void swap(ArrayList<Vertex> pts, int i, int j)
	{
		Vertex temp = pts.get(i);
		pts.set(i, pts.get(j));
		pts.set(j, temp);
	}

}