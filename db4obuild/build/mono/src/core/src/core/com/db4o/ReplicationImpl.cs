/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

using System;
using j4o.lang;
using com.db4o.ext;
using com.db4o.query;
namespace com.db4o {

   internal class ReplicationImpl : Db4oReplication, Db4oReplicationConflict {
      internal YapStream i_source;
      internal Transaction i_sourceTrans;
      internal YapStream i_destination;
      internal Transaction i_destinationTrans;
      internal Db4oCallback i_conflictHandler;
      internal ReplicationRecord i_record;
      internal Db4oDatabase i_sourceDatabaseInDestination;
      private Object i_destinationObject;
      private Object i_sourceObject;
      private YapObject i_sourceYapObject;
      private int i_direction;
      private static int IGNORE = 0;
      private static int TO_DESTINATION = -1;
      private static int TO_SOURCE = 1;
      
      internal ReplicationImpl(YapStream yapstream, ObjectContainer objectcontainer) : base() {
         i_source = yapstream;
         i_sourceTrans = yapstream.checkTransaction(null);
         if (objectcontainer != null) {
            i_destination = (YapStream)objectcontainer;
            i_destinationTrans = i_destination.checkTransaction(null);
            i_source.i_handlers.i_replication = this;
            i_source.i_migrateFrom = i_destination;
            i_destination.i_handlers.i_replication = this;
            i_destination.i_migrateFrom = i_source;
            i_sourceDatabaseInDestination = i_destination.i_handlers.ensureDb4oDatabase(i_destinationTrans, i_source.identity());
            ObjectSet objectset1 = queryForReplicationRecord();
            if (objectset1.hasNext()) i_record = (ReplicationRecord)objectset1.next(); else {
               i_record = new ReplicationRecord();
               i_record.i_source = i_sourceDatabaseInDestination;
               i_record.i_target = i_destination.identity();
            }
         }
      }
      
      public void commit() {
         i_source.commit();
         i_destination.commit();
         long l1 = i_source.currentVersion() - 1L;
         long l_0_1 = i_destination.currentVersion() - 1L;
         i_record.i_version = l_0_1;
         if (l1 > l_0_1) {
            i_record.i_version = l1;
            i_destination.raiseVersion(i_record.i_version);
         } else if (l_0_1 > l1) {
            i_source.raiseVersion(i_record.i_version);
            i_source.commit();
         }
         i_destination.showInternalClasses(true);
         i_destination.set(i_record);
         i_destination.commit();
         i_destination.showInternalClasses(false);
         endReplication();
      }
      
      public void rollback() {
         if (i_destination != null) i_destination.rollback();
         i_source.rollback();
         endReplication();
      }
      
      private void endReplication() {
         i_source.i_migrateFrom = null;
         i_source.i_handlers.i_replication = null;
         i_destination.i_migrateFrom = null;
         i_destination.i_handlers.i_replication = null;
      }
      
      private ObjectSet queryForReplicationRecord() {
         i_destination.showInternalClasses(true);
         Query query1 = i_destination.querySharpenBug();
         query1.constrain(YapConst.CLASS_REPLICATIONRECORD);
         query1.descend("i_source").constrain(i_sourceDatabaseInDestination).identity();
         query1.descend("i_target").constrain(i_destination.identity()).identity();
         ObjectSet objectset1 = query1.execute();
         i_destination.showInternalClasses(false);
         return objectset1;
      }
      
      public void setConflictHandler(Db4oCallback db4ocallback) {
         i_conflictHandler = db4ocallback;
      }
      
      internal bool toDestination(Object obj) {
         lock (i_source.i_lock) {
            i_sourceObject = obj;
            i_sourceYapObject = i_source.getYapObject(obj);
            if (i_sourceYapObject != null) {
               VirtualAttributes virtualattributes1 = i_sourceYapObject.virtualAttributes(i_sourceTrans);
               if (virtualattributes1 != null) {
                  Object[] objs1 = i_destinationTrans.objectAndYapObjectBySignature(virtualattributes1.i_uuid, virtualattributes1.i_database.i_signature);
                  if (objs1[0] != null) {
                     YapObject yapobject1 = (YapObject)objs1[1];
                     i_destinationObject = objs1[0];
                     VirtualAttributes virtualattributes_1_1 = yapobject1.virtualAttributes(i_destinationTrans);
                     if (virtualattributes1.i_version <= i_record.i_version && virtualattributes_1_1.i_version <= i_record.i_version) {
                        i_destination.bind2(yapobject1, i_sourceObject);
                        return true;
                     }
                     if (virtualattributes1.i_version > i_record.i_version && virtualattributes_1_1.i_version > i_record.i_version) {
                        if (i_conflictHandler == null) {
                           i_destination.bind2(yapobject1, i_sourceObject);
                           return true;
                        }
                        i_direction = 0;
                        i_conflictHandler.callback(this);
                        if (i_direction == 0) {
                           i_destination.bind2(yapobject1, i_sourceObject);
                           return true;
                        }
                     } else {
                        i_direction = -1;
                        if (virtualattributes_1_1.i_version > i_record.i_version) i_direction = 1;
                     }
                     if (i_direction == 1) {
                        if (!yapobject1.isActive()) yapobject1.activate(i_destinationTrans, i_destinationObject, 1, false);
                        i_source.bind2(i_sourceYapObject, i_destinationObject);
                        i_source.setNoReplication(i_sourceTrans, i_destinationObject, 1, true);
                     } else {
                        if (!i_sourceYapObject.isActive()) i_sourceYapObject.activate(i_sourceTrans, i_sourceObject, 1, false);
                        i_destination.bind2(yapobject1, i_sourceObject);
                        i_destination.setNoReplication(i_destinationTrans, i_sourceObject, 1, true);
                     }
                     return true;
                  }
               }
            }
            return false;
         }
      }
      
      internal void destinationOnNew(YapObject yapobject) {
         if (i_sourceYapObject != null) {
            VirtualAttributes virtualattributes1 = i_sourceYapObject.virtualAttributes(i_sourceTrans);
            yapobject.i_virtualAttributes = new VirtualAttributes();
            VirtualAttributes virtualattributes_2_1 = yapobject.i_virtualAttributes;
            virtualattributes_2_1.i_uuid = virtualattributes1.i_uuid;
            virtualattributes_2_1.i_version = virtualattributes1.i_version;
            virtualattributes_2_1.i_database = virtualattributes1.i_database;
         }
      }
      
      public ObjectContainer destination() {
         return i_destination;
      }
      
      public Object destinationObject() {
         return i_destinationObject;
      }
      
      public ObjectContainer source() {
         return i_source;
      }
      
      public Object sourceObject() {
         return i_sourceObject;
      }
      
      public void useSource() {
         i_direction = -1;
      }
      
      public void useDestination() {
         i_direction = 1;
      }
   }
}