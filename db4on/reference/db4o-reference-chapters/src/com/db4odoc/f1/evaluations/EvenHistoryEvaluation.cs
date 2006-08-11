using com.db4o.query;
	
namespace com.db4odoc.f1.evaluations
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