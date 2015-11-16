import java.util.ArrayList;
import java.util.List;

public class Obstacle {
    private List<Vertex> vertices;
    private int num_vertices = vertices.size();

    public Obstacle() {
        vertices = new ArrayList<Vertex>();
    }

    public Obstacle(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public void addVertex(Vertex p) {
        vertices.add(p);
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    /*public Vertex growObstacles(double robot_width) {
        Vertex new_obstacle = new Vertex();
        Vertex left, right, normleft, normright, p;
        Vertex eleft1, eleft2, eright1, eright2; // extended points
        Vertex center = new Vertex(0, 0);

        for (int i = 0; i < num_vertices; i++) {
            center = center.translate(vertices.get(i));
        }
        center = center.multiply(1 / ((double) num_vertices));

		// move the line segments out
        for (int i = 0; i < num_vertices; i++) {
            // grab two edges
            left = this.vertices.get((i - 1 + num_vertices) % num_vertices);
            Vertex new_vertex = this.vertices.get(i % num_vertices);
            right = this.vertices.get((i + 1) % num_vertices);
			
			// turn the normals right side out (center is always inside the polygon
            normleft = left.subtract(new_vertex).perpendicular().unit();
            normright = new_vertex.subtract(right).perpendicular().unit();

            if (normleft.dot(new_vertex.subtract(center)) < 0) {
                normleft = normleft.multiply(-1.0);
            }
            if (normright.dot(new_vertex.subtract(center)) < 0) {
                normright = normright.multiply(-1.0);
            }
            normleft = normleft.multiply(robot_width);
            normright = normright.multiply(robot_width);
			
			// move the points out
            eleft1 = left.translate(normleft);
            eleft2 = new_vertex.translate(normleft);
            eright1 = new_vertex.translate(normright);
            eright2 = right.translate(normright);
			
			// find the intersection
            new_vertex = intersectLines(eleft1, eleft2, eright1, eright2);

            new_obstacle.addVertex(new_vertex);
        }
        return new_obstacle;
    }*/

    public static boolean intersects(Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
        if (v1.x - v2.x == 0 || v3.x - v4.x == 0) {
            if (v1.x - v2.x == 0 && v3.x - v4.x == 0 && !v1.equals(v3)) {
                System.out.println("Parallel Vertical Lines");
                return false;
            } else if (v1.x - v2.x == 0 && v3.x - v4.x == 0 && v1.equals(v3)) {
                System.out.println("Same Vertical Line!");
                return false;
            }
        }
        double m1 = (v1.y - v2.y) / (v1.x - v2.x);
        double m2 = (v3.y - v4.y) / (v3.x - v4.x);
        if (m1 == m2) {
            return false;
        }
        return true;
    }

    public static Vertex intersection(Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
        double m1, m2, b1, b2;
        double x, y;
        if (!intersects(v1, v2, v3, v4)) {
            return null;
        }

        m1 = (v1.y - v2.y) / (v1.x - v2.x);
        m2 = (v3.y - v4.y) / (v3.x - v4.x);
        b1 = v1.y - m1 * v1.x;
        b2 = v3.y - m2 * v3.x;

        x = (b2 - b1) / (m1 - m2);
        y = m1 * x + b1;

        return new Vertex(x, y);
    }

    public static boolean lineSegmentIntersects(Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
        Vertex intersection_point;
        if (intersects(v1, v2, v3, v4)) {
            intersection_point = intersection(v1, v2, v3, v4);
            double x = intersection_point.x;
            if (Math.min(v1.x, v2.x) < x && x < Math.max(v1.x, v2.x) &&
                    Math.min(v3.x, v4.x) < x && x < Math.max(v3.x, v4.x)) {
                return true;
            }
            return false;
        }
        return false;
    }
}