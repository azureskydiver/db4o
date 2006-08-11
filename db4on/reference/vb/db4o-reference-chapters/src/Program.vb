Module Program

    Sub Main(ByVal args As String())
        com.db4odoc.f1.clientserver.ClientServerExample.Main(args)
        com.db4odoc.f1.evaluations.TranslatorExample.Main(args)
        com.db4odoc.f1.indexes.IndexedExample.fillUpDB()
        com.db4odoc.f1.indexes.IndexedExample.noIndex()
        com.db4odoc.f1.indexes.IndexedExample.fullIndex()
        com.db4odoc.f1.indexes.IndexedExample.pilotIndex()
        com.db4odoc.f1.indexes.IndexedExample.pointsIndex()
        com.db4odoc.f1.diagnostics.DiagnosticExample.TestTranslatorDiagnostics()
    End Sub

End Module
