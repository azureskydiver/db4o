namespace com.db4o.drs.test
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
	}
}
