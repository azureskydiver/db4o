' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports com.db4o
Imports com.db4o.query
Imports com.db4o.diagnostic
Imports System
Imports System.IO

Namespace com.db4odoc.f1.diagnostics
    Public Class DiagnosticExample
        Inherits Util
        Public Shared Sub TestEmpty()
            Db4oFactory.Configure().Diagnostic().AddListener(New DiagnosticToConsole())
            File.Delete(Util.YapFileName)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(Util.YapFileName)
            Try
                SetEmptyObject(db)
            Finally
                db.Close()
            End Try
        End Sub

        Private Shared Sub SetEmptyObject(ByVal db As ObjectContainer)
            Dim empty As Empty = New Empty()
            db.Set(empty)
        End Sub

        Public Shared Sub TestArbitrary()
            Db4oFactory.Configure().Diagnostic().AddListener(New DiagnosticToConsole())
            File.Delete(Util.YapFileName)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(Util.YapFileName)
            Try
                Dim pilot As evaluations.Pilot = New evaluations.Pilot("Rubens Barrichello", 99)
                db.Set(pilot)
                QueryPilot(db)
            Finally
                db.Close()
            End Try
        End Sub

        Private Shared Sub QueryPilot(ByVal db As ObjectContainer)
            Dim i() As Integer = New Integer() {19, 100}

            Dim result As ObjectSet = db.Query(New ArbitraryQuery(i))
            ListResult(result)
        End Sub

        Public Shared Sub TestIndexDiagnostics()
            Db4oFactory.Configure().Diagnostic().RemoveAllListeners()
            Db4oFactory.Configure().Diagnostic().AddListener(New IndexDiagListener())
            Db4oFactory.Configure().UpdateDepth(3)
            File.Delete(Util.YapFileName)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(Util.YapFileName)
            Try
                Dim pilot1 As evaluations.Pilot = New evaluations.Pilot("Rubens Barrichello", 99)
                db.Set(pilot1)
                Dim pilot2 As evaluations.Pilot = New evaluations.Pilot("Michael Schumacher", 100)
                db.Set(pilot2)
                QueryPilot(db)
                SetEmptyObject(db)
                Dim query As Query = db.Query()
                query.Constrain(GetType(evaluations.Pilot))
                query.Descend("_points").Constrain("99")
                Dim result As ObjectSet = query.Execute()
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub

        Public Shared Sub TestTranslatorDiagnostics()
            StoreTranslatedCars()
            RetrieveTranslatedCars()
            RetrieveTranslatedCarsNQ()
            RetrieveTranslatedCarsNQUnopt()
            RetrieveTranslatedCarsSODAEv()
        End Sub

        Public Shared Sub StoreTranslatedCars()
            Db4oFactory.Configure().ExceptionsOnNotStorable(True)
            Db4oFactory.Configure().ObjectClass(GetType(evaluations.Car)).Translate(New evaluations.CarTranslator())
            Db4oFactory.Configure().ObjectClass(GetType(evaluations.Car)).CallConstructor(True)
            File.Delete(Util.YapFileName)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim car1 As evaluations.Car = New evaluations.Car("BMW")
                System.Diagnostics.Trace.WriteLine("ORIGINAL: " + car1.ToString())
                db.Set(car1)
                Dim car2 As evaluations.Car = New evaluations.Car("Ferrari")
                System.Diagnostics.Trace.WriteLine("ORIGINAL: " + car2.ToString())
                db.Set(car2)
            Catch exc As Exception
                System.Diagnostics.Trace.WriteLine(exc.Message)
                Return
            Finally
                db.Close()
            End Try
        End Sub

        Public Shared Sub RetrieveTranslatedCars()
            Db4oFactory.Configure().Diagnostic().RemoveAllListeners()
            Db4oFactory.Configure().Diagnostic().AddListener(New TranslatorDiagListener())
            Db4oFactory.Configure().ExceptionsOnNotStorable(True)
            Db4oFactory.Configure().ObjectClass(GetType(evaluations.Car)).Translate(New evaluations.CarTranslator())
            Db4oFactory.Configure().ObjectClass(GetType(evaluations.Car)).CallConstructor(True)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim query As Query = db.Query()
                query.Constrain(GetType(evaluations.Car))
                Dim result As ObjectSet = query.Execute()
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub

        Public Shared Sub RetrieveTranslatedCarsNQ()
            Db4oFactory.Configure().Diagnostic().RemoveAllListeners()
            Db4oFactory.Configure().Diagnostic().AddListener(New TranslatorDiagListener())
            Db4oFactory.Configure().ExceptionsOnNotStorable(True)
            Db4oFactory.Configure().ObjectClass(GetType(evaluations.Car)).Translate(New evaluations.CarTranslator())
            Db4oFactory.Configure().ObjectClass(GetType(evaluations.Car)).CallConstructor(True)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim result As ObjectSet = db.Query(New NewCarModel())
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub

        Public Shared Sub RetrieveTranslatedCarsNQUnopt()
            Db4oFactory.Configure().OptimizeNativeQueries(False)
            Db4oFactory.Configure().Diagnostic().RemoveAllListeners()
            Db4oFactory.Configure().Diagnostic().AddListener(New TranslatorDiagListener())
            Db4oFactory.Configure().ExceptionsOnNotStorable(True)
            Db4oFactory.Configure().ObjectClass(GetType(evaluations.Car)).Translate(New evaluations.CarTranslator())
            Db4oFactory.Configure().ObjectClass(GetType(evaluations.Car)).CallConstructor(True)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim result As ObjectSet = db.Query(New NewCarModel())
                ListResult(result)
            Finally
                Db4oFactory.Configure().OptimizeNativeQueries(True)
                db.Close()
            End Try
        End Sub

        Public Shared Sub RetrieveTranslatedCarsSODAEv()
            Db4oFactory.Configure().Diagnostic().RemoveAllListeners()
            Db4oFactory.Configure().Diagnostic().AddListener(New TranslatorDiagListener())
            Db4oFactory.Configure().ExceptionsOnNotStorable(True)
            Db4oFactory.Configure().ObjectClass(GetType(evaluations.Car)).Translate(New evaluations.CarTranslator())
            Db4oFactory.Configure().ObjectClass(GetType(evaluations.Car)).CallConstructor(True)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim query As Query = db.Query()
                query.Constrain(GetType(evaluations.Car))
                query.Constrain(New CarEvaluation())
                Dim result As ObjectSet = query.Execute()
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
    End Class

	
End Namespace
