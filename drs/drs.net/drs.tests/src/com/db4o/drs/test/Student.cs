namespace com.db4o.drs.test
{
	public class Student : com.db4o.drs.test.Person
	{
		private string _studentno;

		public Student(string name, int age) : base(name, age)
		{
		}

		public virtual void SetStudentNo(string studentno)
		{
			this._studentno = studentno;
		}

		public virtual string GetStudentNo()
		{
			return _studentno;
		}
	}
}
