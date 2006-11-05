package com.db4odoc.f1;

import com.db4odoc.f1.activating.ActivationExample;
import com.db4odoc.f1.blobs.BlobExample;
import com.db4odoc.f1.clientserver.ClientServerExample;
import com.db4odoc.f1.clientserver.DeepExample;
import com.db4odoc.f1.clientserver.ExtClientExample;
import com.db4odoc.f1.clientserver.TransactionExample;
import com.db4odoc.f1.debugging.DebugExample;
import com.db4odoc.f1.diagnostics.DiagnosticExample;
import com.db4odoc.f1.enums.EnumExample;
import com.db4odoc.f1.identity.IdentityExample;
import com.db4odoc.f1.indexes.IndexedExample;
import com.db4odoc.f1.ios.IOExample;
import com.db4odoc.f1.lists.CollectionExample;
import com.db4odoc.f1.metainf.MetaInfExample;
import com.db4odoc.f1.persist.PeekPersistedExample;
import com.db4odoc.f1.refactoring.RefactoringExample;
import com.db4odoc.f1.reflections.ReflectorExample;
import com.db4odoc.f1.remote.RemoteExample;
import com.db4odoc.f1.selpersist.MarkTransientExample;
import com.db4odoc.f1.serialize.SerializeExample;
import com.db4odoc.f1.staticfields.StaticFieldExample;
import com.db4odoc.f1.utility.UtilityExample;
import com.db4odoc.f1.uuids.UUIDExample;



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
        MetaInfExample.main(args);
        RemoteExample.main(args);
        RefactoringExample.main(args);
        PeekPersistedExample.main(args);
        IdentityExample.main(args);
    	SerializeExample.main(args);
    	UtilityExample.main(args);
    	MarkTransientExample.main(args);
    }
}
