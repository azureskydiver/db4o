namespace Db4objects.Drs.Test
{
	public class Student : Db4objects.Drs.Test.Person
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
