/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
namespace com.db4o
{
	/// <summary>base class for all database aware objects</summary>
	/// <exclude></exclude>
	public class P1Object : com.db4o.Db4oTypeImpl
	{
		[com.db4o.Transient]
		private com.db4o.Transaction i_trans;

		[com.db4o.Transient]
		private com.db4o.YapObject i_yapObject;

		public P1Object()
		{
		}

		internal P1Object(com.db4o.Transaction a_trans)
		{
			i_trans = a_trans;
		}

		public virtual void activate(object a_obj, int a_depth)
		{
			if (i_trans != null)
			{
				if (a_depth < 0)
				{
					i_trans.i_stream.activate1(i_trans, a_obj);
				}
				else
				{
					i_trans.i_stream.activate1(i_trans, a_obj, a_depth);
				}
			}
		}

		public virtual int activationDepth()
		{
			return 1;
		}

		public virtual int adjustReadDepth(int a_depth)
		{
			return a_depth;
		}

		internal virtual void checkActive()
		{
			if (i_trans != null)
			{
				if (i_yapObject == null)
				{
					i_yapObject = i_trans.i_stream.getYapObject(this);
					if (i_yapObject == null)
					{
						i_trans.i_stream.set(this);
						i_yapObject = i_trans.i_stream.getYapObject(this);
					}
				}
				if (validYapObject())
				{
					i_yapObject.activate(i_trans, this, activationDepth(), false);
				}
			}
		}

		public virtual object createDefault(com.db4o.Transaction a_trans)
		{
			throw com.db4o.YapConst.virtualException();
		}

		internal virtual void deactivate()
		{
			if (validYapObject())
			{
				i_yapObject.deactivate(i_trans, activationDepth());
			}
		}

		internal virtual void delete()
		{
			if (i_trans != null)
			{
				if (i_yapObject == null)
				{
					i_yapObject = i_trans.i_stream.getYapObject(this);
				}
				if (validYapObject())
				{
					i_trans.i_stream.delete3(i_trans, i_yapObject, this, 0);
				}
			}
		}

		protected virtual void delete(object a_obj)
		{
			if (i_trans != null)
			{
				i_trans.i_stream.delete(a_obj);
			}
		}

		protected virtual long getIDOf(object a_obj)
		{
			if (i_trans == null)
			{
				return 0;
			}
			return i_trans.i_stream.getID(a_obj);
		}

		protected virtual com.db4o.Transaction getTrans()
		{
			return i_trans;
		}

		public virtual bool hasClassIndex()
		{
			return false;
		}

		public virtual void preDeactivate()
		{
		}

		public virtual void setTrans(com.db4o.Transaction a_trans)
		{
			i_trans = a_trans;
		}

		public virtual void setYapObject(com.db4o.YapObject a_yapObject)
		{
			i_yapObject = a_yapObject;
		}

		protected virtual void store(object a_obj)
		{
			if (i_trans != null)
			{
				i_trans.i_stream.setInternal(i_trans, a_obj, true);
			}
		}

		public virtual object storedTo(com.db4o.Transaction a_trans)
		{
			i_trans = a_trans;
			return this;
		}

		internal virtual object streamLock()
		{
			if (i_trans != null)
			{
				i_trans.i_stream.checkClosed();
				return i_trans.i_stream.Lock();
			}
			return this;
		}

		internal virtual void store(int a_depth)
		{
			if (i_trans != null)
			{
				if (i_yapObject == null)
				{
					i_yapObject = i_trans.i_stream.getYapObject(this);
					if (i_yapObject == null)
					{
						i_trans.i_stream.setInternal(i_trans, this, true);
						i_yapObject = i_trans.i_stream.getYapObject(this);
						return;
					}
				}
				update(a_depth);
			}
		}

		internal virtual void update()
		{
			update(activationDepth());
		}

		internal virtual void update(int depth)
		{
			if (validYapObject())
			{
				i_trans.i_stream.beginEndSet(i_trans);
				i_yapObject.writeUpdate(i_trans, depth);
				i_trans.i_stream.checkStillToSet();
				i_trans.i_stream.beginEndSet(i_trans);
			}
		}

		private bool validYapObject()
		{
			return (i_trans != null) && (i_yapObject != null) && (i_yapObject.getID() > 0);
		}
	}
}
