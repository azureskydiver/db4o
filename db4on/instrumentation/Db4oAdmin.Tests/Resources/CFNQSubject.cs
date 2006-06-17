/* Copyright (C) 2004 - 2006  db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;
using System.Collections.Generic;
using com.db4o;
using Db4oUnit;

public class Item
{
	private string _name;

	public Item(string name)
	{
		_name = name;
	}

	public string Name
	{
		get { return _name; }
	}
}

// TODO: query invocation with comparator
// TODO: query invocation with comparison
public class CFNQSubject
{
	public static void SetUp(ObjectContainer container)
	{	
		container.Set(new Item("foo"));
		container.Set(new Item("bar"));
	}

	public static void TestInlineStaticDelegate(ObjectContainer container)
	{
		SetUp(container);
		IList<Item> items = container.Query<Item>(delegate(Item candidate)
		{
			return candidate.Name == "foo";
		});
		CheckResult(items);
	}

	public static void TestInlineClosureDelegate(ObjectContainer container)
	{
		SetUp(container);
		string name = "foo";
		IList<Item> items = container.Query<Item>(delegate(Item candidate)
		{
			return candidate.Name == name;
		});
		CheckResult(items);
	}

	public static void TestStaticMemberDelegate(ObjectContainer container)
	{
		SetUp(container);
		IList<Item> items = container.Query<Item>(CFNQSubject.MatchFoo);
		CheckResult(items);
	}

	public static void TestMultipleQueryInvocations(ObjectContainer container)
	{
		SetUp(container);
		CheckResult(container.Query<Item>(CFNQSubject.MatchFoo));
		CheckResult(container.Query<Item>(CFNQSubject.MatchFoo));
		CheckResult(container.Query<Item>(CFNQSubject.MatchFoo));
	}

	delegate ObjectContainer ObjectContainerAccessor();

	public static void TestInlineStaticDelegateInsideExpression(ObjectContainer container)
	{
		SetUp(container);
		ObjectContainerAccessor getter = delegate { return container; };
		CheckResult(getter().Query<Item>(delegate(Item candidate)
		{
			return candidate.Name == "foo";
		}));
	}

	public static void TestInstanceMemberDelegate(ObjectContainer container)
	{
		SetUp(container);
		IList<Item> items = container.Query<Item>(new QueryItemByName("foo").Match);
		CheckResult(items);
	}

	private static void CheckResult(IList<Item> items)
	{
		Assert.AreEqual(1, items.Count);
		Assert.AreEqual("foo", items[0].Name);
	}

	static bool MatchFoo(Item candidate)
	{
		return candidate.Name == "foo";
	}

	class QueryItemByName
	{
		string _name;

		public QueryItemByName(string name)
		{
			_name = name;
		}

		public bool Match(Item candidate)
		{
			return candidate.Name == _name;
		}
	}
}
