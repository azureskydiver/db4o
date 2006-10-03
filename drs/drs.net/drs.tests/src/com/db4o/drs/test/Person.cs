namespace com.db4o.drs.test
{
	public class Person
	{
		private string _name;

		private int _age;

		public virtual void SetName(string name)
		{
			this._name = name;
		}

		public virtual string GetName()
		{
			return _name;
		}

		public virtual void SetAge(int age)
		{
			this._age = age;
		}

		public virtual int GetAge()
		{
			return _age;
		}

		public Person(string name, int age)
		{
			this._name = name;
			this._age = age;
		}
	}
}
