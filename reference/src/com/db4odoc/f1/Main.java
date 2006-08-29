package com.db4odoc.f1;

import com.db4odoc.f1.blobs.BlobExample;
import com.db4odoc.f1.clientserver.ClientServerExample;
import com.db4odoc.f1.clientserver.DeepExample;
import com.db4odoc.f1.clientserver.TransactionExample;
import com.db4odoc.f1.debugging.DebugExample;
import com.db4odoc.f1.diagnostics.DiagnosticExample;
import com.db4odoc.f1.indexes.IndexedExample;
import com.db4odoc.f1.lists.CollectionExample;
import com.db4odoc.f1.reflections.ReflectorExample;
import com.db4odoc.f1.activating.ActivationExample;




public class Main {
    public static void main(String[] args) throws Exception {
        DeepExample.main(args);
        TransactionExample.main(args);
        ClientServerExample.main(args);
        IndexedExample.fillUpDB();
        IndexedExample.noIndex();
        IndexedExample.fullIndex();
        IndexedExample.pilotIndex();
        IndexedExample.pointsIndex();
        DiagnosticExample.testEmpty();
        DiagnosticExample.testArbitrary();
        DiagnosticExample.testIndexDiagnostics();
        DiagnosticExample.testTranslatorDiagnostics();
    	BlobExample.main(args);
    	CollectionExample.main(args);
    	ReflectorExample.main(args);
        //ReflectorExample.testReflector(); //should be commented for successful doctor build
    	DebugExample.main(args);
    	ActivationExample.main(args);
    }
}
