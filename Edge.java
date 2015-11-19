/************************************************************************
 * Alice Chang (avc2120), Phillip Godzin (pgg2105), Martin Ong (mo2454)
 * Computational Aspects of Robotics
 * FALL 2015
**************************************************************************/
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
            return true;
        if (!(o instanceof Edge)) 
            return false;
        Edge e = (Edge) o;
        return target.equals(e.target) && Double.compare(weight, e.weight) == 0;
    }
}
