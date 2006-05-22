using com.db4o.f1.chapter3;
using com.db4o.query;
	
namespace com.db4o.f1.chapter6
{	
	public class EvenHistoryEvaluation : Evaluation
	{
		public void Evaluate(Candidate candidate)
		{
			Car car=(Car)candidate.GetObject();
			candidate.Include(car.History.Count % 2 == 0);
		}
	}
}