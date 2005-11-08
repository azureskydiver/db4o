namespace com.db4o.f1.chapter4
{
    using com.db4o.query;

	public class RetrieveAllObjectsPredicate : Predicate {
		public bool match(object candidate){
    		return true;
    	}
   	}
}