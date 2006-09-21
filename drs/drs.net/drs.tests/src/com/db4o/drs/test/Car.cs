namespace com.db4o.drs.test
{
	public class Car
	{
		internal string _model;

		internal com.db4o.drs.test.Pilot _pilot;

		public Car()
		{
		}

		public Car(string model)
		{
			_model = model;
		}

		public virtual string GetModel()
		{
			return _model;
		}

		public virtual void SetModel(string model)
		{
			_model = model;
		}
	}
}
