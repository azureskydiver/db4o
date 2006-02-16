using com.db4o.query;

namespace com.db4o.f1.chapter4
{
	public class RetrieveAllSensorReadoutsPredicate : Predicate 
	{
		public bool match(SensorReadout candidate)
		{
			return true;
		}
	}
}