public class Edge{

    public Edge(Vertex argTarget, double argWeight) {
        target = argTarget;
        weight = argWeight;
    }

    public String toString()
    {
    	return "( Target: " + target + ", Weight: " + weight + ") ";
    }

    @Override
    public boolean equals(Object o) {
 
        // If the object is compared with itself then return true  
        if (o == this) {
            return true;
        }
 
        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Edge)) {
            return false;
        }
         
        // typecast o to Complex so that we can compare data members 
        Edge c = (Edge) o;
         
        // Compare the data members and return accordingly 
        return target.equals(c.target)
                && Double.compare(weight, c.weight) == 0;
    }


    public Vertex target;
    public double weight;
}
