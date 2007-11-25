namespace Db4objects.Db4odoc.StaticFields
{
	public class PilotCategories
	{
		private string _qualification = null;
		public static  PilotCategories Winner = new PilotCategories("WINNER");
		public static  PilotCategories Talented = new PilotCategories("TALENTED");
		public static  PilotCategories Average = new PilotCategories("AVERAGE");
		public static  PilotCategories Disqualified = new PilotCategories("DISQUALIFIED");
	
		private PilotCategories(string qualification)
		{
			this._qualification = qualification;
		}
	
		public PilotCategories()
		{
		
		}
	
		public void TestChange(string qualification)
		{
			this._qualification = qualification;
		}

		override public string ToString() 
		{
			return _qualification;
		}
	}
}
