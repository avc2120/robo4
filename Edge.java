public class Edge {

    public Edge(Vertex argTarget, double argWeight) {
        target = argTarget;
        weight = argWeight;
    }

    public String toString()
    {
    	return "( Target: " + target + ", Weight: " + weight + ") ";
    }

    public final Vertex target;
    public final double weight;
}
