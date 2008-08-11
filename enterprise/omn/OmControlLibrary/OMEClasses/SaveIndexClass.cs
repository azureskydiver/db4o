using System;
using System.Collections.Generic;
using System.Text;
using OMControlLibrary.Common;
using System.Collections;

namespace OMControlLibrary
{
	public class SaveIndexClass
	{
		ArrayList fieldname;
		string classname;

		ArrayList indexed;

		public string Classname
		{
			get { return classname; }
			set { classname = value; }
		}

		public ArrayList Indexed
		{
			get { return indexed; }
			set { indexed = value; }
		}

		public ArrayList Fieldname
		{
			get { return fieldname; }
			set { fieldname = value; }
		}
		internal void SaveIndex()
		{
			for (int i = 0; i < fieldname.Count; i++)
			{
				Helper.DbInteraction.SetIndexedConfiguration(fieldname[i].ToString(), classname, Convert.ToBoolean(indexed[i]));
			}
		}
	}
}
