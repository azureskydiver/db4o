namespace Db4objects.Drs.Test
{
	public class Car
	{
		internal string _model;

		internal Db4objects.Drs.Test.Pilot _pilot;

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
