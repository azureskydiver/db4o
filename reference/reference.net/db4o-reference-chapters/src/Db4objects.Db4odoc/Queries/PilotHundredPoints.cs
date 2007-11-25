using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Queries
{
	public class PilotHundredPoints : Predicate 
	{
		public bool Match(Pilot pilot) 
		{
			return pilot.Points == 100;
		}
	}
}
