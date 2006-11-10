using System;

namespace Db4objects.Db4odoc.StaticFields
{
	public class PilotCategories
	{
		private string _qualification = null;
		public static  PilotCategories WINNER=new PilotCategories("WINNER");
		public static  PilotCategories TALENTED=new PilotCategories("TALENTED");
		public static  PilotCategories AVERAGE=new PilotCategories("AVERAGE");
		public static  PilotCategories DISQUALIFIED=new PilotCategories("DISQUALIFIED");
	
		private PilotCategories(String qualification)
		{
			this._qualification = qualification;
		}
	
		public PilotCategories()
		{
		
		}
	
		public void TestChange(String qualification)
		{
			this._qualification = qualification;
		}

		override public string ToString() 
		{
			return _qualification;
		}
	}
}
