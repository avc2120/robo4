import java.util.ArrayList;
import java.util.List;

public class Vertex implements Comparable<Vertex> {
    public List<Edge> adjacencies;
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex previous;

    public double x;
    public double y;

    public Vertex(double x, double y) {
        this.x = x;
        this.y = y;
        adjacencies = new ArrayList<Edge>();
    }

    public Vertex() {
        x = y = 0.0;
    }

    // length of vector
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    // direction of vector
    public double direction() {
        return Math.atan2(y, x);
    }

    // Euclidean distance between two points
    public static double distance(Vertex p1, Vertex p2) {
        double dx = p1.x - p2.x;
        double dy = p1.y - p2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // point equality using bit representation
    public boolean equals(Object o) {
        if (o instanceof Vertex) {
            Vertex p = (Vertex) o;
            return Double.doubleToLongBits(x) ==
                    Double.doubleToLongBits(p.x) &&
                    Double.doubleToLongBits(y) ==
                            Double.doubleToLongBits(p.y);
        }
        return false;
    }

    // print point
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public int compareTo(Vertex other) {
        return Double.compare(minDistance, other.minDistance);
    }
    // order points lexicographically
    // public int compareTo(Vertex p)
    // {
    // 	if (x < p.x) return -1;
    // 	if (x > p.x) return  1;
    // 	if (y < p.y) return -1;
    // 	if (y > p.y) return  1;
    // 		return 0;
    // }


}