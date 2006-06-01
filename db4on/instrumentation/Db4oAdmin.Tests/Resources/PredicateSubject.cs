using System;
using System.Collections.Generic;
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

public class PredicateSubject
{
	public static void Setup(ObjectContainer container)
	{
		container.Set(new Person(23, "jbe"));
		container.Set(new Person(30, "rbo"));
	}
	
	public static void TestByName(ObjectContainer container)
	{
		ObjectSet result = container.Query(new PersonByName("jbe"));
		Assert.AreEqual(1, result.Count);
		Assert.AreEqual("jbe", ((Person)result[0]).Name);
	}
}

