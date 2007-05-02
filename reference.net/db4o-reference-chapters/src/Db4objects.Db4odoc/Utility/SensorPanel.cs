/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

namespace Db4objects.Db4odoc.Utility
{
	public class SensorPanel
	{
		private object _sensor;
		private SensorPanel _next;
	
		public SensorPanel()
		{
			// default constructor for instantiation
		}
	
		public SensorPanel(int value)
		{
			_sensor = value;
		}
	
		public SensorPanel CreateList(int length)
		{
			return CreateList(length, 1);
		}
	
		public SensorPanel CreateList(int length, int first)
		{
			int val = first;
			SensorPanel root = NewElement(first);
			SensorPanel list = root;
			while(--length > 0)
			{
				list._next = NewElement(++ val);
				list = list._next;
			}
			return root;
		}

		public SensorPanel Next
		{
			get
			{
				return _next;
			}
            
			set
			{
				_next = value;
			}
		}

		public object Sensor
		{
			get
			{
				return _sensor;
			}
            
			set
			{
				_sensor = value;
			}
		}
	
		protected SensorPanel NewElement(int value)
		{
			return new SensorPanel(value);
		}
		
		public override string ToString() 
		{
			return "Sensor #" + _sensor ;
		}
	}
}
