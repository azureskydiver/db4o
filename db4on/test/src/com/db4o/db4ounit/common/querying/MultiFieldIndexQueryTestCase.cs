namespace com.db4o.db4ounit.common.querying
{
	/// <exclude></exclude>
	public class MultiFieldIndexQueryTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase().RunSolo();
		}

		public class Book
		{
			public com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person[] authors;

			public string title;

			public Book()
			{
			}

			public Book(string title, com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person[]
				 authors)
			{
				this.title = title;
				this.authors = authors;
			}

			public override string ToString()
			{
				string ret = title;
				if (authors != null)
				{
					for (int i = 0; i < authors.Length; i++)
					{
						ret += "\n  " + authors[i].ToString();
					}
				}
				return ret;
			}
		}

		public class Person
		{
			public string firstName;

			public string lastName;

			public Person()
			{
			}

			public Person(string firstName, string lastName)
			{
				this.firstName = firstName;
				this.lastName = lastName;
			}

			public override string ToString()
			{
				return "Person " + firstName + " " + lastName;
			}
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			IndexAllFields(config, typeof(com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Book)
				);
			IndexAllFields(config, typeof(com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person)
				);
		}

		protected virtual void IndexAllFields(com.db4o.config.Configuration config, System.Type
			 clazz)
		{
			System.Reflection.FieldInfo[] fields = j4o.lang.JavaSystem.GetDeclaredFields(clazz
				);
			for (int i = 0; i < fields.Length; i++)
			{
				IndexField(config, clazz, fields[i].Name);
			}
			System.Type superclass = clazz.BaseType;
			if (superclass != null)
			{
				IndexAllFields(config, superclass);
			}
		}

		protected override void Store()
		{
			com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person aaron = new 
				com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person("Aaron", "OneOK"
				);
			com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person bill = new 
				com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person("Bill", "TwoOK"
				);
			com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person chris = new 
				com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person("Chris", "ThreeOK"
				);
			com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person dave = new 
				com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person("Dave", "FourOK"
				);
			com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person neil = new 
				com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person("Neil", "Notwanted"
				);
			com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person nat = new com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person
				("Nat", "Neverwanted");
			Db().Set(new com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Book(
				"Persistence possibilities", new com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person
				[] { aaron, bill, chris }));
			Db().Set(new com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Book(
				"Persistence using S.O.D.A.", new com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person
				[] { aaron }));
			Db().Set(new com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Book(
				"Persistence using JDO", new com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person
				[] { bill, dave }));
			Db().Set(new com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Book(
				"Don't want to find Phil", new com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person
				[] { aaron, bill, neil }));
			Db().Set(new com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Book(
				"Persistence by Jeff", new com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person
				[] { nat }));
		}

		public virtual void Test()
		{
			com.db4o.query.Query qBooks = NewQuery();
			qBooks.Constrain(typeof(com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Book)
				);
			qBooks.Descend("title").Constrain("Persistence").Like();
			com.db4o.query.Query qAuthors = qBooks.Descend("authors");
			com.db4o.query.Query qFirstName = qAuthors.Descend("firstName");
			com.db4o.query.Query qLastName = qAuthors.Descend("lastName");
			com.db4o.query.Constraint cAaron = qFirstName.Constrain("Aaron").And(qLastName.Constrain
				("OneOK"));
			com.db4o.query.Constraint cBill = qFirstName.Constrain("Bill").And(qLastName.Constrain
				("TwoOK"));
			cAaron.Or(cBill);
			com.db4o.ObjectSet results = qAuthors.Execute();
			Db4oUnit.Assert.AreEqual(4, results.Size());
			while (results.HasNext())
			{
				com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person person = (com.db4o.db4ounit.common.querying.MultiFieldIndexQueryTestCase.Person
					)results.Next();
				Db4oUnit.Assert.IsTrue(person.lastName.EndsWith("OK"));
			}
		}
	}
}
