public class Edge
{
    public Vertex target;
    public double weight;
    public Edge(Vertex argTarget, double argWeight) {
        target = argTarget;
        weight = argWeight;
    }
    
    @Override
    public String toString()
    {
    	return "( Target: " + target + ", Weight: " + weight + ") ";
    }

    @Override
    public boolean equals(Object o) 
    {
        if (o == this) 
        {
            return true;
        }
        if (!(o instanceof Edge)) 
        {
            return false;
        }
        Edge c = (Edge) o;
        return target.equals(c.target) && Double.compare(weight, c.weight) == 0;
    }
}
