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

class PersonByAgeOrSpouseName : com.db4o.query.Predicate
{
	string _name;

	int _age;

	public PersonByAgeOrSpouseName(int age, string name)
	{
		_age = age;
		_name = name;
	}

	public bool Match(Person candidate)
	{
		return candidate.Age == _age
			|| candidate.Spouse.Name == _name;
	}
}

class PersonByAgeAndName : com.db4o.query.Predicate
{
	string _name;

	int _age;

	public PersonByAgeAndName(int age, string name)
	{
		_age = age;
		_name = name;
	}

	public bool Match(Person candidate)
	{
		return candidate.Age == _age
			&& candidate.Name == _name;
	}
}

class PersonByAgeOrNames : com.db4o.query.Predicate
{
	string _name1;

	string _name2;

	int _age;

	public PersonByAgeOrNames(int age, string name1, string name2)
	{
		_name1 = name1;
		_name2 = name2;
		_age = age;
	}

	public bool Match(Person candidate)
	{
		return candidate.Age == _age
			&& (candidate.Name == _name1 || candidate.Name == _name2);
	}
}

class PersonByAgeRange : com.db4o.query.Predicate
{
	int _begin;
	int _end;

	public PersonByAgeRange(int begin, int end)
	{
		_begin = begin;
		_end = end;
	}

	public bool Match(Person candidate)
	{
		return candidate.Age >= _begin && candidate.Age <= _end;
	}
}

public class PredicateSubject
{
	public void Setup(ObjectContainer container)
	{
		container.Set(new Person(23, "jbe"));
		container.Set(new Person(23, "Ronaldinho"));
		
		Person rbo = new Person(30, "rbo");
		rbo.Spouse = new Person(29, "ma");
		container.Set(rbo);
	}

	public void TestByAgeRange(ObjectContainer container)
	{
		Setup(container);
		AssertResult(
			container.Query(new PersonByAgeRange(23, 29)),
			"jbe", "Ronaldinho", "ma");
		AssertResult(
			container.Query(new PersonByAgeRange(28, 30)),
			"rbo", "ma");
	}

	public void TestByAgeOrNames(ObjectContainer container)
	{
		Setup(container);
		AssertResult(
			container.Query(new PersonByAgeOrNames(23, "jbe", "Ronaldinho")),
			"jbe", "Ronaldinho");
		AssertResult(
			container.Query(new PersonByAgeOrNames(23, "jbe", "rbo")),
			"jbe");
		AssertResult(
			container.Query(new PersonByAgeOrNames(30, "jbe", "Ronaldinho")));
	}

	public void TestByAgeAndName(ObjectContainer container)
	{
		Setup(container);
		AssertResult(
			container.Query(new PersonByAgeAndName(23, "jbe")),
			"jbe");
	}

	public void TestByAgeOrSpouseName(ObjectContainer container)
	{
		Setup(container);
		AssertResult(
			container.Query(new PersonByAgeOrSpouseName(23, "ma")),
			"jbe", "rbo", "Ronaldinho");
	}
	
	public void TestByName(ObjectContainer container)
	{
		Setup(container);
		AssertResult(
				container.Query(new PersonByName("jbe")),
				"jbe");
	}

	public void TestByAge(ObjectContainer container)
	{
		Setup(container);
		AssertResult(
				container.Query(new PersonByAge(30)),
				"rbo");
		AssertResult(
				container.Query(new PersonByAge(23)),
				"jbe", "Ronaldinho");
	}
	
	public void TestBySpouseName(ObjectContainer container)
	{
		Setup(container);
		AssertResult(
				container.Query(new PersonBySpouseName("ma")),
				"rbo");
	}

	void AssertResult(IList result, params string[] expected)
	{
		Assert.AreEqual(expected.Length, result.Count);
		foreach (string name in expected)
		{
			AssertContains(result, name);
		}	
	}

	void AssertContains(IList result, string expected)
	{
		foreach (Person p in result)
		{
			if (p.Name == expected) return;
		}
		Assert.Fail("Expected '" + expected + "'.");
	}
}

