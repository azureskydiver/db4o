using System.Collections.Generic;
using Db4objects.Db4o.Collections;

namespace UGMan6000.Transparent.Model
{
	public class Group
	{
		private ArrayList4<User> _users = new ArrayList4<User>();
		private string _name;

		public Group(string name)
		{
			_name = name;
		}

		public string Name
		{
			get { return _name;  }
		}

		public IEnumerable<User> Users
		{
			get { return _users; }
		}

		public void AddUser(User user)
		{
			if (_users.Contains(user))
			{
				return;
			}
			user.AddedToGroup(this);
			_users.Add(user);
		}

		public override string ToString()
		{
			return _name;
		}
	}

	public class User
	{
		private ArrayList4<Group> _groups = new ArrayList4<Group>();
		private string _name;

		public User(string name)
		{
			_name = name;
		}

		public string Name
		{
			get { return _name; }
		}

		public IEnumerable<Group> Groups
		{
			get { return _groups; }
		}

		internal void AddedToGroup(Group g)
		{
			_groups.Add(g);
		}

		public override string ToString()
		{
			return _name;
		}
	}
}
