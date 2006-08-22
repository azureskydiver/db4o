/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.Collections;
using com.db4odoc.f1.evaluations;

namespace com.db4odoc.f1.lists
{
	public class Team
	{
		private IList _pilots;
		private string _name;

		public Team()
		{
			_pilots = CollectionFactory.newList();	
		}

		public string Name
		{
			get
			{
				return _name;
			}
            
			set
			{
				_name = value;
			}
		}

		public void AddPilot(Pilot pilot)
		{
			_pilots.Add(pilot);
		}
	
		public Pilot GetPilot(int index)
		{
			return (Pilot)_pilots[index]; 
		}
	
		public void RemovePilot(int index)
		{
			_pilots.Remove(index);
		}
	
		public void UpdatePilot(int index, Pilot newPilot)
		{
			_pilots[index] = newPilot;
		}
	}
}
