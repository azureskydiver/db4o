/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

namespace Db4objects.Db4odoc.Typehandler
{
	public class Car
	{
		private System.Text.StringBuilder model;

		private System.Text.StringBuilder modelCopy;

		public Car(string model)
		{
			this.model = new System.Text.StringBuilder(model);
			modelCopy = new System.Text.StringBuilder("Copy: " + model);
		}

		public virtual string GetModel()
		{
			return model.ToString();
		}

		public override string ToString()
		{
			return model == null ? "null" : model.ToString() + " " + modelCopy.ToString();
		}
	}
}
