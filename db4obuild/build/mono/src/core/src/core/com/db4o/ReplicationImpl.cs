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
	internal class ReplicationImpl : com.db4o.ext.Db4oReplication, com.db4o.ext.Db4oReplicationConflict
	{
		internal readonly com.db4o.YapStream i_source;

		internal readonly com.db4o.Transaction i_sourceTrans;

		internal com.db4o.YapStream i_destination;

		internal com.db4o.Transaction i_destinationTrans;

		internal com.db4o.ext.Db4oCallback i_conflictHandler;

		internal com.db4o.ReplicationRecord i_record;

		internal com.db4o.ext.Db4oDatabase i_sourceDatabaseInDestination;

		private object i_destinationObject;

		private object i_sourceObject;

		private com.db4o.YapObject i_sourceYapObject;

		private int i_direction;

		private const int IGNORE = 0;

		private const int TO_DESTINATION = -1;

		private const int TO_SOURCE = 1;

		internal ReplicationImpl(com.db4o.YapStream a_source, com.db4o.ObjectContainer a_destination
			)
		{
			i_source = a_source;
			i_sourceTrans = a_source.checkTransaction(null);
			if (a_destination != null)
			{
				i_destination = (com.db4o.YapStream)a_destination;
				i_destinationTrans = i_destination.checkTransaction(null);
				i_source.i_handlers.i_replication = this;
				i_source.i_migrateFrom = i_destination;
				i_destination.i_handlers.i_replication = this;
				i_destination.i_migrateFrom = i_source;
				i_sourceDatabaseInDestination = i_destination.i_handlers.ensureDb4oDatabase(i_destinationTrans
					, i_source.identity());
				com.db4o.ObjectSet objectSet = queryForReplicationRecord();
				if (objectSet.hasNext())
				{
					i_record = (com.db4o.ReplicationRecord)objectSet.next();
				}
				else
				{
					i_record = new com.db4o.ReplicationRecord();
					i_record.i_source = i_sourceDatabaseInDestination;
					i_record.i_target = i_destination.identity();
				}
			}
		}

		public virtual void commit()
		{
			i_source.commit();
			i_destination.commit();
			long i_sourceVersion = i_source.currentVersion() - 1;
			long i_destinationVersion = i_destination.currentVersion() - 1;
			i_record.i_version = i_destinationVersion;
			if (i_sourceVersion > i_destinationVersion)
			{
				i_record.i_version = i_sourceVersion;
				i_destination.raiseVersion(i_record.i_version);
			}
			else
			{
				if (i_destinationVersion > i_sourceVersion)
				{
					i_source.raiseVersion(i_record.i_version);
					i_source.commit();
				}
			}
			i_destination.showInternalClasses(true);
			i_destination.set(i_record);
			i_destination.commit();
			i_destination.showInternalClasses(false);
			endReplication();
		}

		public virtual void rollback()
		{
			if (i_destination != null)
			{
				i_destination.rollback();
			}
			i_source.rollback();
			endReplication();
		}

		private void endReplication()
		{
			i_source.i_migrateFrom = null;
			i_source.i_handlers.i_replication = null;
			i_destination.i_migrateFrom = null;
			i_destination.i_handlers.i_replication = null;
		}

		private com.db4o.ObjectSet queryForReplicationRecord()
		{
			i_destination.showInternalClasses(true);
			com.db4o.query.Query q = i_destination.querySharpenBug();
			q.constrain(com.db4o.YapConst.CLASS_REPLICATIONRECORD);
			q.descend("i_source").constrain(i_sourceDatabaseInDestination).identity();
			q.descend("i_target").constrain(i_destination.identity()).identity();
			com.db4o.ObjectSet objectSet = q.execute();
			i_destination.showInternalClasses(false);
			return objectSet;
		}

		public virtual void setConflictHandler(com.db4o.ext.Db4oCallback callback)
		{
			i_conflictHandler = callback;
		}

		internal virtual bool toDestination(object a_sourceObject)
		{
			lock (i_source.i_lock)
			{
				i_sourceObject = a_sourceObject;
				i_sourceYapObject = i_source.getYapObject(a_sourceObject);
				if (i_sourceYapObject != null)
				{
					com.db4o.VirtualAttributes vas = i_sourceYapObject.virtualAttributes(i_sourceTrans
						);
					if (vas != null)
					{
						object[] arr = i_destinationTrans.objectAndYapObjectBySignature(vas.i_uuid, vas.i_database
							.i_signature);
						if (arr[0] != null)
						{
							com.db4o.YapObject yod = (com.db4o.YapObject)arr[1];
							i_destinationObject = arr[0];
							com.db4o.VirtualAttributes vad = yod.virtualAttributes(i_destinationTrans);
							if (vas.i_version <= i_record.i_version && vad.i_version <= i_record.i_version)
							{
								i_destination.bind2(yod, i_sourceObject);
								return true;
							}
							if (vas.i_version > i_record.i_version && vad.i_version > i_record.i_version)
							{
								if (i_conflictHandler == null)
								{
									i_destination.bind2(yod, i_sourceObject);
									return true;
								}
								i_direction = IGNORE;
								i_conflictHandler.callback(this);
								if (i_direction == IGNORE)
								{
									i_destination.bind2(yod, i_sourceObject);
									return true;
								}
							}
							else
							{
								i_direction = TO_DESTINATION;
								if (vad.i_version > i_record.i_version)
								{
									i_direction = TO_SOURCE;
								}
							}
							if (i_direction == TO_SOURCE)
							{
								if (!yod.isActive())
								{
									yod.activate(i_destinationTrans, i_destinationObject, 1, false);
								}
								i_source.bind2(i_sourceYapObject, i_destinationObject);
								i_source.setNoReplication(i_sourceTrans, i_destinationObject, 1, true);
							}
							else
							{
								if (!i_sourceYapObject.isActive())
								{
									i_sourceYapObject.activate(i_sourceTrans, i_sourceObject, 1, false);
								}
								i_destination.bind2(yod, i_sourceObject);
								i_destination.setNoReplication(i_destinationTrans, i_sourceObject, 1, true);
							}
							return true;
						}
					}
				}
				return false;
			}
		}

		internal virtual void destinationOnNew(com.db4o.YapObject a_yod)
		{
			if (i_sourceYapObject != null)
			{
				com.db4o.VirtualAttributes vas = i_sourceYapObject.virtualAttributes(i_sourceTrans
					);
				a_yod.i_virtualAttributes = new com.db4o.VirtualAttributes();
				com.db4o.VirtualAttributes vad = a_yod.i_virtualAttributes;
				vad.i_uuid = vas.i_uuid;
				vad.i_version = vas.i_version;
				vad.i_database = vas.i_database;
			}
		}

		public virtual com.db4o.ObjectContainer destination()
		{
			return i_destination;
		}

		public virtual object destinationObject()
		{
			return i_destinationObject;
		}

		public virtual com.db4o.ObjectContainer source()
		{
			return i_source;
		}

		public virtual object sourceObject()
		{
			return i_sourceObject;
		}

		public virtual void useSource()
		{
			i_direction = TO_DESTINATION;
		}

		public virtual void useDestination()
		{
			i_direction = TO_SOURCE;
		}
	}
}
