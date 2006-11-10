using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Evaluations
{	
	public class EvenHistoryEvaluation : IEvaluation
	{
		public void Evaluate(ICandidate candidate)
		{
			Car car=(Car)candidate.GetObject();
			candidate.Include(car.History.Count % 2 == 0);
		}
	}
}