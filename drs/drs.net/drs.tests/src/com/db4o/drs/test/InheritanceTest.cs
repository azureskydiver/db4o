namespace com.db4o.drs.test
{
	public class InheritanceTest : com.db4o.drs.test.DrsTestCase
	{
		private static readonly string NEW_NAME = "Jeanette";

		private static readonly string NEW_STUDENTNO = "VANJ";

		private const int NEW_AGE = 41;

		private const int OLD_AGE = 37;

		private static readonly string OLD_STUDENTNO = "VANP";

		private static readonly string OLD_NAME = "Peter";

		public virtual void Test()
		{
			Store();
			Replicate(A().Provider(), B().Provider(), OLD_NAME, OLD_STUDENTNO, OLD_AGE);
			ModifyInB();
			Replicate(B().Provider(), A().Provider(), NEW_NAME, NEW_STUDENTNO, NEW_AGE);
		}

		private void ModifyInB()
		{
			com.db4o.drs.test.Student student = GetTheStudent(B().Provider());
			EnsureOneInstance(B().Provider(), typeof(com.db4o.drs.test.Person));
			student.SetAge(NEW_AGE);
			student.SetStudentNo(NEW_STUDENTNO);
			student.SetName(NEW_NAME);
			B().Provider().Update(student);
			B().Provider().Commit();
			EnsureDetails(B().Provider(), NEW_NAME, NEW_STUDENTNO, NEW_AGE);
			EnsureDetails(A().Provider(), OLD_NAME, OLD_STUDENTNO, OLD_AGE);
		}

		private void Replicate(com.db4o.drs.inside.TestableReplicationProviderInside providerFrom
			, com.db4o.drs.inside.TestableReplicationProviderInside providerTo, string name, 
			string studentno, int age)
		{
			ReplicateAll(providerFrom, providerTo);
			EnsureDetails(providerFrom, name, studentno, age);
			EnsureDetails(providerTo, name, studentno, age);
		}

		private void Store()
		{
			com.db4o.drs.test.Student _student = new com.db4o.drs.test.Student(OLD_NAME, OLD_AGE
				);
			_student.SetStudentNo(OLD_STUDENTNO);
			A().Provider().StoreNew(_student);
			A().Provider().Commit();
			EnsureDetails(A().Provider(), OLD_NAME, OLD_STUDENTNO, OLD_AGE);
		}

		private void EnsureDetails(com.db4o.drs.inside.TestableReplicationProviderInside 
			provider, string name, string studentno, int age)
		{
			EnsureOneInstance(provider, typeof(com.db4o.drs.test.Person));
			EnsureOneInstance(provider, typeof(com.db4o.drs.test.Student));
			com.db4o.drs.test.Person person = GetThePerson(provider);
			Db4oUnit.Assert.AreEqual(name, person.GetName());
			Db4oUnit.Assert.AreEqual(age, person.GetAge());
			com.db4o.drs.test.Student student = GetTheStudent(provider);
			Db4oUnit.Assert.AreEqual(studentno, student.GetStudentNo());
			Db4oUnit.Assert.AreEqual(name, student.GetName());
			Db4oUnit.Assert.AreEqual(age, student.GetAge());
		}

		private com.db4o.drs.test.Person GetThePerson(com.db4o.drs.inside.TestableReplicationProviderInside
			 provider)
		{
			return (com.db4o.drs.test.Person)GetOneInstance(provider, typeof(com.db4o.drs.test.Person
				));
		}

		private com.db4o.drs.test.Student GetTheStudent(com.db4o.drs.inside.TestableReplicationProviderInside
			 provider)
		{
			return (com.db4o.drs.test.Student)GetOneInstance(provider, typeof(com.db4o.drs.test.Student
				));
		}
	}
}
