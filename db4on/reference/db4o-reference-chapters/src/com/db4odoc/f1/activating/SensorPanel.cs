/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using System;

namespace com.db4odoc.f1.activating
{
	public class SensorPanel
	{
		public Object _sensor;
		public SensorPanel _next;
	
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
	
		protected SensorPanel NewElement(int value)
		{
			return new SensorPanel(value);
		}
		
		public override String ToString() 
		{
			return "Sensor #" + _sensor ;
		}
	}
}
