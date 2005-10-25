namespace com.db4o.f1.chapter1
{
    using com.db4o.query;

    public class ArbitraryQuery : Predicate
    {
    	public boolean match(Pilot pilot)
    	{
        	for(int i=0;i<points.length;i++) {
        		if(pilot.getPoints()==points[i]) {
        			return true;
        		}
        	}
        	return pilot.getName().StartsWith("Rubens");
    	}
    }
}
