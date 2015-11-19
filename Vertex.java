import java.util.*;

public class Vertex implements Comparable<Vertex>
{
    public ArrayList<Edge> adjacencies;
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex previous;

    public double x;
	public double y;

	public Vertex(double x, double y)
	{
		this.x = x;
		this.y = y;
		adjacencies = new ArrayList<Edge>();
	}

	public Vertex()
	{
		x = y = 0.0;
	}

	public Vertex clone()
    {
    	return new Vertex(this.x, this.y);
    }

	public int compareTo(Vertex other)
    {
        return Double.compare(minDistance, other.minDistance);
    }

	// direction of vector
	public double direction()
	{
		return Math.atan2(y, x);
	}

	// Euclidean distance between two points
	public static double distance(Vertex v1, Vertex v2)
	{
		double dx = v1.x - v2.x;
		double dy = v1.y - v2.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	// point equality using bit representation
	public boolean equals(Object o)
	{
		if (o instanceof Vertex) {
			Vertex p = (Vertex) o;
			return Double.doubleToLongBits(x) ==
			       Double.doubleToLongBits(p.x) &&
			       Double.doubleToLongBits(y) ==
			       Double.doubleToLongBits(p.y);
		}
		return false;
	}

	// length of vector
	public double magnitude()
	{
		return Math.sqrt(x * x + y * y);
	}

	public Vertex multiply(Double magnitude)
    {
    	return new Vertex(this.x*magnitude, this.y*magnitude);
    }

    public Vertex subtract(Vertex vertex)
    {
    	return new Vertex(this.x - vertex.x, this.y - vertex.y);
    }

    public Vertex subtract(int x, int y)
    {
    	return new Vertex(this.x- x, this.y - y);
    }

	public String toString()
	{
		return x + " " + y;
	}

    public Vertex translate(int x, int y)
    {
    	return new Vertex(this.x + x, this.y + y);
    }

    public Vertex translate(Vertex vertex)
    {
    	return new Vertex(this.x + vertex.x, this.y + vertex.y);
    }

    public Vertex perpendicular()
    {
    	return new Vertex(this.y, -this.x);
    }

    public double dot(Vertex other)
	{
		return this.x*other.x + this.y*other.y;
	}

    public Vertex rotate(double delta_angle)
	{
		double angle = Math.atan2(this.y, this.x);
		double distance = Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
		double x = distance*Math.cos(angle + delta_angle);
		double y = distance*Math.sin(angle + delta_angle);
		return new Vertex(x,y);
	}

	public static boolean intersects(Vertex v1, Vertex v2, Vertex v3, Vertex v4)
	{
		if(v1.x - v2.x == 0 || v3.x - v4.x == 0)
		{
			if (v1.x - v2.x == 0 && v3.x - v4.x == 0 && !v1.equals(v3))
			{
				System.out.println("Parallel Vertical Lines");
				return false;
			}
			else if (v1.x - v2.x == 0 && v3.x - v4.x == 0 && v1.equals(v3))
			{
				System.out.println("Same Vertical Line!");
				return false;
			}
		}
		double m1 = (v1.y - v2.y)/(v1.x - v2.x);
		double m2 = (v3.y - v4.y)/(v3.x - v4.x);
		if(m1 == m2)
		{
			return false;
		}
		return true;
	}

	public static Vertex intersectLineSegments(Vertex v1, Vertex v2, Vertex v3, Vertex v4)
	{
		double d1, d2;
		Vertex p = lineIntersection(v1, v2, v3, v4);
		if(p == null)
			return null;
		d1 = distance(v1, v2);
		d2 = distance(v3, v4);
		// check the line intersections themselves
		if(d1 < distance(p, v1) || d1 < distance(p, v2) ||
		   d2 < distance(p, v3) || d2 < distance(p, v4))
			return null;
		return p;
	}

	public static Vertex rayIntersects(Vertex v1, Vertex v2, Vertex v3, Vertex v4)
	{
		Vertex intersection = lineIntersection(v1, v2, v3, v4);
		if(intersection == null)
		{
			// System.out.println("Lines don't intersect, so Rays don't intersect either!");
			return null;
		}
		double dist = distance(v3, v4);
		// checks if rays in same direction, distance from intersection to one end of second line is greater than total length of second line
		if((dist < distance(intersection, v3) || dist < distance(intersection, v4)) || (v1.subtract(v2).dot(v1.subtract(intersection)) < 0))
		{
			return null;
		}
		return intersection;
	}

	public static Vertex lineIntersection(Vertex v1, Vertex v2, Vertex v3, Vertex v4)
	{
		
		double difference = (v1.x - v2.x)*(v3.y-v4.y) - (v1.y-v2.y)*(v3.x-v4.x);
		if (difference == 0.0)
		{
//			 System.out.println("Lines are Parallel!");
			return null; 
		}
		double x = ((v3.x-v4.x)*(v1.x*v2.y-v1.y*v2.x)-(v1.x-v2.x)*(v3.x*v4.y-v3.y*v4.x))/difference;
		double y = ((v3.y-v4.y)*(v1.x*v2.y-v1.y*v2.x)-(v1.y-v2.y)*(v3.x*v4.y-v3.y*v4.x))/difference;
		return new Vertex(x, y);
	}

	public Vertex unit()
	{
		double dist = distance(this, new Vertex(0,0));
		if(dist == 0.0)
		{
			return new Vertex(0,0);
		}
		return this.clone().multiply(1/dist);
	}

	public static double ccw(Vertex v1, Vertex v2, Vertex v3)
	{
		return (v2.x - v1.x)*(v3.y - v1.y) - (v2.y - v1.y)*(v3.x - v1.x);
	}
	


}