import java.util.*;

public class Point implements Comparable <Point> {

	public double x;
	public double y;

	public Point(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public Point()
	{
		x = y = 0.0;
	}

	// length of vector
	public double magnitude()
	{
		return Math.sqrt(x * x + y * y);
	}

	// direction of vector
	public double direction()
	{
		return Math.atan2(y, x);
	}

	// Euclidean distance between two points
	public static double distance(Point p1, Point p2)
	{
		double dx = p1.x - p2.x;
		double dy = p1.y - p2.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	// point equality using bit representation
	public boolean equals(Object o)
	{
		if (o instanceof Point) {
			Point p = (Point) o;
			return Double.doubleToLongBits(x) ==
			       Double.doubleToLongBits(p.x) &&
			       Double.doubleToLongBits(y) ==
			       Double.doubleToLongBits(p.y);
		}
		return false;
	}

	// print point
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}

	// order points lexicographically
	public int compareTo(Point p)
	{
		if (x < p.x) return -1;
		if (x > p.x) return  1;
		if (y < p.y) return -1;
		if (y > p.y) return  1;
 		return 0;
	}
}
