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
namespace com.db4o.ext
{
	/// <summary>callback methods.</summary>
	/// <remarks>
	/// callback methods.
	/// <br /><br /><b>Examples: ../com/db4o/samples/callbacks.</b><br /><br />
	/// This interface only serves as a lists of all available callback methods.
	/// Every method is called individually, independant of implementing this interface.<br /><br />
	/// <b>Using callbacks</b><br />
	/// Simply implement one or more of the listed methods in your application classes to
	/// do tasks before activation, deactivation, delete, new or update, to cancel the
	/// action about to be performed and to respond to the performed task.
	/// <br /><br />Callback methods are typically used for:
	/// <br />- cascaded delete
	/// <br />- cascaded update
	/// <br />- cascaded activation
	/// <br />- restoring transient members on instantiation
	/// <br /><br />Callback methods follow regular calling conventions. Methods in superclasses
	/// need to be called explicitely.
	/// <br /><br />All method calls are implemented to occur only once, upon one event.
	/// </remarks>
	public interface ObjectCallbacks
	{
		/// <summary>called before an Object is activated.</summary>
		/// <remarks>called before an Object is activated.</remarks>
		/// <param name="container">the <code>ObjectContainer</code> the object is stored in.
		/// 	</param>
		/// <returns>false to prevent activation.</returns>
		bool objectCanActivate(com.db4o.ObjectContainer container);

		/// <summary>called before an Object is deactivated.</summary>
		/// <remarks>called before an Object is deactivated.</remarks>
		/// <param name="container">the <code>ObjectContainer</code> the object is stored in.
		/// 	</param>
		/// <returns>false to prevent deactivation.</returns>
		bool objectCanDeactivate(com.db4o.ObjectContainer container);

		/// <summary>called before an Object is deleted.</summary>
		/// <remarks>
		/// called before an Object is deleted.
		/// <br /><br />In a client/server setup this callback method will be executed on
		/// the server.
		/// </remarks>
		/// <param name="container">the <code>ObjectContainer</code> the object is stored in.
		/// 	</param>
		/// <returns>false to prevent the object from being deleted.</returns>
		bool objectCanDelete(com.db4o.ObjectContainer container);

		/// <summary>called before an Object is stored the first time.</summary>
		/// <remarks>called before an Object is stored the first time.</remarks>
		/// <param name="container">the <code>ObjectContainer</code> is about to be stored to.
		/// 	</param>
		/// <returns>false to prevent the object from being stored.</returns>
		bool objectCanNew(com.db4o.ObjectContainer container);

		/// <summary>called before a persisted Object is updated.</summary>
		/// <remarks>called before a persisted Object is updated.</remarks>
		/// <param name="container">the <code>ObjectContainer</code> the object is stored in.
		/// 	</param>
		/// <returns>false to prevent the object from being updated.</returns>
		bool objectCanUpdate(com.db4o.ObjectContainer container);

		/// <summary>called upon activation of an object.</summary>
		/// <remarks>called upon activation of an object.</remarks>
		/// <param name="container">the <code>ObjectContainer</code> the object is stored in.
		/// 	</param>
		void objectOnActivate(com.db4o.ObjectContainer container);

		/// <summary>called upon deactivation of an object.</summary>
		/// <remarks>called upon deactivation of an object.</remarks>
		/// <param name="container">the <code>ObjectContainer</code> the object is stored in.
		/// 	</param>
		void objectOnDeactivate(com.db4o.ObjectContainer container);

		/// <summary>called after an object was deleted.</summary>
		/// <remarks>
		/// called after an object was deleted.
		/// <br /><br />In a client/server setup this callback method will be executed on
		/// the server.
		/// </remarks>
		/// <param name="container">the <code>ObjectContainer</code> the object was stored in.
		/// 	</param>
		void objectOnDelete(com.db4o.ObjectContainer container);

		/// <summary>called after a new object was stored.</summary>
		/// <remarks>called after a new object was stored.</remarks>
		/// <param name="container">the <code>ObjectContainer</code> the object is stored to.
		/// 	</param>
		void objectOnNew(com.db4o.ObjectContainer container);

		/// <summary>called after an object was updated.</summary>
		/// <remarks>called after an object was updated.</remarks>
		/// <param name="container">the <code>ObjectContainer</code> the object is stored in.
		/// 	</param>
		void objectOnUpdate(com.db4o.ObjectContainer container);
	}
}
