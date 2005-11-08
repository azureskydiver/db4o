namespace com.db4o.f1.chapter4
{
    using com.db4o.query;

	public class RetrieveAllSensorReadoutsPredicate : Predicate {
		public boolean match(SensorReadout candidate){
    		return true;
    	}
   	}
}