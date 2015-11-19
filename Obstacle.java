/************************************************************************
 * Alice Chang (avc2120), Phillip Godzin (pgg2105), Martin Ong (mo2454)
 * Computational Aspects of Robotics
 * FALL 2015
**************************************************************************/
import java.util.*;
public class Obstacle
{
	public ArrayList<Vertex> vertices;
	public int num_vertices;
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

	public boolean equals(Obstacle other)
	{
		ArrayList<Vertex> this_vertices = this.getVertices();
		ArrayList<Vertex> other_vertices = other.getVertices();
		int min = other.getVertices().size()<this.getVertices().size()? other.getVertices().size(): this.getVertices().size();
		for(int i = 0; i < min; i++)
		{
			if (!this_vertices.get(i).equals(other_vertices.get(i)))
			{
				return false;
			}
		}
		return true;
	}
	
	public Obstacle clone()
	{
		Obstacle new_obstacle = new Obstacle((ArrayList<Vertex>)this.vertices.clone());
		for(int i = 0; i < new_obstacle.getSize(); i++)
		{
			new_obstacle.setVertex(i, new_obstacle.getVertex(i).clone());
		}
		return new_obstacle;
	}

	public Obstacle growObstacles(double robot_width)
	{
		//Create copy of same obstacle
		Obstacle new_obstacle = clone();
		//Find centroid of the obstacle
		Vertex center = getCentroid();
		Vertex new_vertex = new Vertex();
		for(int i = 0; i < num_vertices; i++) 
		{
			//Get adjacent vertices
			Vertex left = this.vertices.get((i-1 + num_vertices) % num_vertices);
			new_vertex = this.vertices.get(i % num_vertices);
			Vertex right = this.vertices.get((i+1) % num_vertices);

			//subtract right by left vertex and set to perpendicular unit length
			Vertex normleft  = left.subtract(new_vertex).perpendicular().unit();
			Vertex normright = new_vertex.subtract(right).perpendicular().unit();

			//set to positive if negative
			if(normleft.dot(new_vertex.subtract(center)) < 0)
				normleft = normleft.multiply(-1.0);
			if(normright.dot(new_vertex.subtract(center)) < 0)
				normright = normright.multiply(-1.0);
			
			//multiply unit normal left by robot width
			normleft = normleft.multiply(robot_width);
			normright = normright.multiply(robot_width);	
			
			//find intersection of lines
			new_vertex = Vertex.lineIntersection(left.translate(normleft), new_vertex.translate(normleft), new_vertex.translate(normright), right.translate(normright));
			
			//set new grown vertex of polygon
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
		int count = 0;
		for(int i = 0; i < num_vertices; i++) 
		{
			Vertex v3 = this.vertices.get(i % num_vertices).clone();
			Vertex v4 = this.vertices.get((i+1) % num_vertices).clone();
			Vertex inter = Vertex.rayIntersects(v,v2, v3,v4);
			if(inter != null) 
			{
				if(!inter.equals(v))
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
				overlapping_vertices.add(vertex);
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
	
	static class VertexComparator implements Comparator<Vertex>
	 {
	     public int compare(Vertex v1, Vertex v2)
	     {
	    	 if(v1.x < v2.x)
	    		 return -1;
	    	 else if(v1.x > v2.x)
	    		 return 1;
	    	 else
	    		 if (v1.y < v2.y)
	    			 return -1;
	    		 else if (v1.x > v2.x)
	    			 return 1;
	    		 else
	    			 return 0;
	     }
	 }
	
	public Obstacle convexHull()
	{
		//Clones original obstacle
		Obstacle o = clone();
		ArrayList<Vertex> vtx = o.getVertices();
		//Sort by x and then y values of Vertex
		Collections.sort(vtx, new VertexComparator());
		ArrayList<Vertex> low = convexHelper(vtx);
		Collections.reverse(vtx);
		ArrayList<Vertex> high = convexHelper(vtx);
		low.addAll(high);
		o.vertices = (ArrayList<Vertex>)(low.clone());
		o.num_vertices = o.vertices.size();
		return o;
	}
	
	public ArrayList<Vertex> convexHelper(ArrayList<Vertex>  vtx)
	{
		ArrayList<Vertex> v = new ArrayList<Vertex>();
		for(Vertex pt : vtx)
		{
			while(v.size() >= 2 && Vertex.counter(v.get(v.size()-2), v.get(v.size()-1), pt ) <= 0)
				v.remove(v.size()-1);
			v.add(pt);
		}
		v.remove(v.size()-1);
		return v;
	}


	public boolean intersectObstacle(Vertex p1, Vertex p2)
	{
		for(int i = 0; i < num_vertices; i++) {
			//getting adjacent vertices
			Vertex p3 = this.vertices.get(i % num_vertices).clone();
			Vertex p4 = this.vertices.get((i+1) % num_vertices).clone();
			Vertex inter = Vertex.intersectLineSegments(p1,p2, p3,p4);
			if(inter != null && !(inter.equals(p1) || inter.equals(p2)))
			{
				return true;
			}
			if((p1.equals(p3) && p2.equals(p4)) || (p2.equals(p3) && p1.equals(p4))) 
			{
				return false;
			}
		}
		return false;
	}
	
	public boolean notAdjPointsInObstacle(Vertex v1, Vertex v2)
	{
		if (this.vertices.contains(v1) && this.vertices.contains(v2))
		{
			if(!(Math.abs(this.vertices.indexOf(v1)- this.vertices.indexOf(v2)) == 1 || Math.abs(this.vertices.indexOf(v1)- this.vertices.indexOf(v2)) == vertices.size()-1))
				return true;
			else
				return false;
		}
		return false;
	}
	

}