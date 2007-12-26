package com.db4odoc;

import com.db4odoc.activating.ActivationExample;
import com.db4odoc.aliases.AliasExample;
import com.db4odoc.blobs.BlobExample;
import com.db4odoc.cachedIO.CachedIOExample;
import com.db4odoc.callbacks.CallbacksExample;
import com.db4odoc.clientserver.ClientServerExample;
import com.db4odoc.clientserver.DeepExample;
import com.db4odoc.clientserver.ExtClientExample;
import com.db4odoc.clientserver.TransactionExample;
import com.db4odoc.concurrency.ConcurrencyExample;
import com.db4odoc.debugging.DebugExample;
import com.db4odoc.diagnostics.DiagnosticExample;
import com.db4odoc.enums.EnumExample;
import com.db4odoc.evaluations.EvaluationExample;
import com.db4odoc.identity.IdentityExample;
import com.db4odoc.indexes.IndexedExample;
import com.db4odoc.ios.IOExample;
import com.db4odoc.listdeleting.ListDeletingExample;
import com.db4odoc.listoperations.ListOperationsExample;
import com.db4odoc.lists.CollectionExample;
import com.db4odoc.metainfo.MetaInfoExample;
import com.db4odoc.persist.PeekPersistedExample;
import com.db4odoc.queries.NQExample;
import com.db4odoc.queries.PersistentExample;
import com.db4odoc.queries.QueryExample;
import com.db4odoc.querymode.QueryModesExample;
import com.db4odoc.refactoring.RefactoringExample;
import com.db4odoc.reflections.ReflectorExample;
import com.db4odoc.remote.RemoteExample;
import com.db4odoc.selectivepersistence.MarkTransientExample;
import com.db4odoc.selectivepersistence.TransientClassExample;
import com.db4odoc.serializing.SerializeExample;
import com.db4odoc.sorting.SortingExample;
import com.db4odoc.staticfields.StaticFieldExample;
import com.db4odoc.structured.StructuredExample;
import com.db4odoc.utility.UtilityExample;
import com.db4odoc.uuids.UUIDExample;



public class Main {
    public static void main(String[] args) throws Exception {
        DeepExample.main(args);
        TransactionExample.main(args);
        ClientServerExample.main(args);
        IndexedExample.main(args);
        DiagnosticExample.main(args); 
    	BlobExample.main(args);
    	CollectionExample.main(args);
    	ReflectorExample.main(args);
        //ReflectorExample.testReflector(); //should be commented for successful doctor build
    	DebugExample.main(args);
    	ActivationExample.main(args);
    	StaticFieldExample.main(args);
    	EnumExample.main(args);
    	UUIDExample.main(args);
        IOExample.main(args);
        ExtClientExample.main(args);
        MetaInfoExample.main(args);
        RemoteExample.main(args);
        RefactoringExample.main(args);
        PeekPersistedExample.main(args);
        IdentityExample.main(args);
    	SerializeExample.main(args);
    	UtilityExample.main(args);
    	MarkTransientExample.main(args);
    	CallbacksExample.main(args);
    	AliasExample.main(args);
    	CachedIOExample.main(args);
    	ConcurrencyExample.main(args);
    	EvaluationExample.main(args);
    	ListDeletingExample.main(args);
    	ListOperationsExample.main(args);
    	NQExample.main(args);
    	PersistentExample.main(args);
    	QueryExample.main(args);
    	QueryModesExample.main(args);
    	TransientClassExample.main(args);
    	MarkTransientExample.main(args);
    	SortingExample.main(args);
    	StructuredExample.main(args);
    }
}
