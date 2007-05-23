namespace Db4objects.Drs.Quickstart.Simple
{
	public class Pilot
	{
		public string _name;

		public int _age;

		public Pilot()
		{
		}

		internal Pilot(string name, int age)
		{
			this._name = name;
			this._age = age;
		}

		public override string ToString()
		{
			return _name + "/" + _age;
		}
	}
}
