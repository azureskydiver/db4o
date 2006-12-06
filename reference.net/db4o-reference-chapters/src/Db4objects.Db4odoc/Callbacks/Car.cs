/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.Callbacks
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
            
			set
			{
				_pilot = value;
			}
		}
        
		public string Model         
		{
			get
			{
				return _model;
			}
		}
        
		override public string ToString()
		{
			return string.Format("{0}[{1}]", _model, _pilot);
		}
	}
}
