using System;
using System.Collections;
using System.Text;
using Db4oAdmin.Tests.Framework;
using com.db4o;

public class Person
{
	private int _age;
	private string _name;

	public int Age
	{
		get { return _age; }
		set { _age = value; }
	}

	public string Name
	{
		get { return _name; }
		set { _name = value; }
	}
	
	public Person (int age, string name)
	{
		_age = age;
		_name = name;
	}
}

public class PersonByName : com.db4o.query.Predicate
{
	string _name;

	public PersonByName(string name)
	{
		_name = name;
	}

	public bool Match(Person item)
	{
		return item.Name == _name;
	}
}

public class PersonByAge : com.db4o.query.Predicate
{
	int _age;

	public PersonByAge(int age)
	{
		_age = age;
	}

	public bool Match(Person item)
	{
		return item.Age == _age;
	}
}

public class PredicateSubject
{
	public static void Setup(ObjectContainer container)
	{
		container.Set(new Person(23, "jbe"));
		container.Set(new Person(30, "rbo"));
	}
	
	public static void TestByName(ObjectContainer container)
	{
		Setup(container);
		AssertResult(
				container.Query(new PersonByName("jbe")),
				"jbe");
	}

	public static void TestByAge(ObjectContainer container)
	{
		Setup(container);
		AssertResult(
				container.Query(new PersonByAge(30)),
				"rbo");
	}

	static void AssertResult(IList result, string expected)
	{
		Assert.AreEqual(1, result.Count);
		Assert.AreEqual(expected, ((Person)result[0]).Name);
	}
}

