using System;
using System.Collections;
using System.Text;
using Db4oUnit;
using com.db4o;

public class Person
{
	private int _age;
	private string _name;
	private Person _spouse;

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
	
	public Person Spouse
	{
		get { return _spouse; }
		set
		{	
			if (value == this) throw new ArgumentException("Spouse");			
			_spouse = value;
		}
	}
	
	public Person (int age, string name)
	{
		_age = age;
		_name = name;
	}
}

class PersonByName : com.db4o.query.Predicate
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

class PersonByAge : com.db4o.query.Predicate
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

class PersonBySpouseName : com.db4o.query.Predicate
{
	string _name;
	
	public PersonBySpouseName(string name)
	{
		_name = name;
	}
	
	public bool Match(Person candidate)
	{
		return candidate.Spouse.Name == _name;
	}
}

public class PredicateSubject
{
	public static void Setup(ObjectContainer container)
	{
		container.Set(new Person(23, "jbe"));
		
		Person rbo = new Person(30, "rbo");
		rbo.Spouse = new Person(29, "ma");
		container.Set(rbo);
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
	
	public static void TestBySpouseName(ObjectContainer container)
	{
		Setup(container);
		AssertResult(
				container.Query(new PersonBySpouseName("ma")),
				"rbo");
	}

	static void AssertResult(IList result, string expected)
	{
		Assert.AreEqual(1, result.Count);
		Assert.AreEqual(expected, ((Person)result[0]).Name);
	}
}

