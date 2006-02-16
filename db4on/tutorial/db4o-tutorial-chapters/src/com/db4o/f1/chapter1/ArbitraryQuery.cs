using com.db4o.query;

namespace com.db4o.f1.chapter1
{
	public class ArbitraryQuery : Predicate
    {
    	private int[] _points;
    	
    	public ArbitraryQuery(int[] points)
    	{
    		_points=points;
    	}
    
    	public bool match(Pilot pilot)
    	{
        	foreach (int points in _points)
        	{
        		if (pilot.Points == points)
        		{
        			return true;
        		}
        	}
        	return pilot.Name.StartsWith("Rubens");
    	}
    }
}
