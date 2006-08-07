package com.db4odoc.f1;

import com.db4odoc.f1.clientserver.*;
import com.db4odoc.f1.diagnostics.*;
import com.db4odoc.f1.indexes.*;


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
    }
}
