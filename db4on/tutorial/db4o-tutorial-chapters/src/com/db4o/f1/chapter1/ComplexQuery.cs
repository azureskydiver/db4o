namespace com.db4o.f1.chapter1
{
    using com.db4o.query;

    public class ComplexQuery : Predicate
    {
    	public bool match(Pilot pilot)
    	{
	    	return pilot.Points>99
				&& pilot.Points<199
				|| pilot.Name=="Rubens Barrichello";
    	}
    }
}
