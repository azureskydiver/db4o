using System;
using System.Collections.Generic;
using System.Text;
using OManager.BusinessLayer.UIHelper;
using OManager.DataLayer.Connection;
using OMControlLibrary.Common;
using System.Collections;

namespace OMControlLibrary
{
	public class SaveIndexClass
	{
		ArrayList fieldnames;
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
			get { return fieldnames; }
			set { fieldnames = value; }
		}
		public SaveIndexClass(string classname)
		{
			this.fieldnames = new ArrayList( );
			this.classname = classname;
			this.indexed = new ArrayList( );
		}

		internal void SaveIndex()
		{

			dbInteraction.SetIndexedConfiguration(fieldnames, classname, indexed);


		}
	}
}
