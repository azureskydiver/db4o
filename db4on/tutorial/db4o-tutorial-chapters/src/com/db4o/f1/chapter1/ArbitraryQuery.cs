namespace com.db4o.f1.chapter1
{
    using com.db4o.query;

    public class ArbitraryQuery : Predicate
    {
    	private int[] _points;
    	
    	public ArbitraryQuery(int[] points)
    	{
    		_points=points;
    	}
    
    	public bool match(Pilot pilot)
    	{
        	for (int i=0;i<_points.Length;i++)
        	{
        		if (pilot.Points == _points[i])
        		{
        			return true;
        		}
        	}
        	return pilot.Name.StartsWith("Rubens");
    	}
    }
}
