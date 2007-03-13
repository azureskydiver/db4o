/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.constraints;

import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.reflect.*;

/**
 * configures a field of a class to allow unique values only.
 */
public class UniqueFieldValueConstraint implements Constraint, ConfigurationItem {
	
	private final Object _clazz;
	private final String _fieldName;
	
	/**
	 * constructor to create a UniqueFieldValueConstraint. 
	 * @param clazz can be a class (Java) / Type (.NET) / instance of the class / fully qualified class name
	 * @param fieldName the name of the field that is to be unique. 
	 */
	public UniqueFieldValueConstraint(Object clazz, String fieldName) {
		_clazz = clazz;
		_fieldName = fieldName;
	}
	
	/**
	 * internal method, public for implementation reasons.
	 */
	public void apply(final ObjectContainerBase objectContainer) {
		ConstraintPlatform.addCommittingConstraint(objectContainer, this);	
	}
	
	private class Checker {
		
		private FieldMetadata _fieldMetaData;
		private ObjectContainerBase _objectContainer;
		private CommitEventArgs _commitEventArgs;
		
		public Checker(ObjectContainerBase objectContainer, CommitEventArgs cea) {
			this._objectContainer = objectContainer;
			this._commitEventArgs = cea;
		}
		
		public void check() {
			ensureSingleOccurence(_commitEventArgs.added());
			ensureSingleOccurence(_commitEventArgs.updated());
		}
		
		private void ensureSingleOccurence(ObjectInfoCollection col){
			Iterator4 i = col.iterator();
			while(i.moveNext()){
				ObjectInfo info = (ObjectInfo) i.current();
				int id = (int)info.getInternalID();
				HardObjectReference ref = HardObjectReference.peekPersisted(transaction(), id, 1);
				Object fieldValue = fieldMetadata().getOn(transaction(), ref._object);
				if(fieldValue == null){
					continue;
				}
				BTreeRange range = fieldMetadata().search(transaction(), fieldValue);
				if(range.size() > 1){
					throw new UniqueFieldValueConstraintViolationException(classMetadata().getName(), fieldMetadata().getName()); 
				}
			}
		}
		
		private FieldMetadata fieldMetadata() {
			if(_fieldMetaData != null){
				return _fieldMetaData;
			}
			_fieldMetaData = classMetadata().fieldMetadataForName(_fieldName);
			return _fieldMetaData;
		}
		
		private ClassMetadata classMetadata() {
			ReflectClass reflectClass = ReflectorUtils.reflectClassFor(_objectContainer.reflector(), _clazz);
			return _objectContainer.classMetadataForReflectClass(reflectClass); 
		}
		
		private final Transaction transaction() {
			return _objectContainer.getTransaction();
		}
	}

	public void check(ObjectContainerBase objectContainer, EventArgs ea) {
		new Checker(objectContainer, (CommitEventArgs) ea).check();
	}
}
