namespace Db4oUnit
{
	public class NullTest : Db4oUnit.Test
	{
		internal string _label;

		public NullTest(string label)
		{
			_label = label;
		}

		public virtual string GetLabel()
		{
			return _label;
		}

		public virtual void Run(Db4oUnit.TestResult result)
		{
		}
	}
}
