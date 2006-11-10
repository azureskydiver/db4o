using System;
namespace Db4objects.Db4odoc.UUIDs
{   
	public class Car
	{
		string _model;
		Pilot _pilot;
	
		public Car(string model, Pilot pilot)
		{
			_model = model;
			_pilot = pilot;
		}
        
		public Pilot Pilot
		{
			get
			{
				return _pilot;
			}
		}
        
		override public string ToString()
		{
			return string.Format("{0}[{1}]", _model, _pilot);
		}
	}
}

