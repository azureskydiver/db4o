/* Copyright (C) 2004 - 2005  db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.test.cluster.*;
import com.db4o.test.reflect.*;
import com.db4o.test.soda.*;

public class Jdk1_2TestSuite extends TestSuite{
    
    public Class[] tests(){
        return new Class[] {
            ArrayListInHashMap.class,
            CascadeToHashMap.class,
            ClusterQueryImplementsList.class,
            CollectionActivation.class,
            Db4oLinkedList.class,
            Db4oHashMap.class,
            DeleteRemovedMapElements.class,
            DiscreteArrayInMap.class,
            ExtendsHashMap.class,
            ExternalBlobs.class,
            GenericObjects.class,
            HashMapClearUnsaved.class,
            JdkComparatorSort.class,
            KeepCollectionContent.class,
            MassUpdates.class,
            MultipleEvaluationGetObjectCalls.class,
            ObjectSetAsList.class,
            ObjectSetAsIterator.class,
            OrClassConstraintInList.class,
            PrimitivesInCollection.class,
            QueryForList.class,
            Reflection.class,
            RefreshList.class,
            SelectDistinct.class,
            SodaEvaluation.class,
            StoreBigDecimal.class,
            StringCaseInsensitive.class,
            StringInLists.class,
            TestHashMap.class,
            TestStringBuffer.class,
            TestTreeMap.class,
            TransientClone.class,
            TreeSetCustomComparable.class,
            UpdatingDb4oVersions.class
            

        };
    }
    
}