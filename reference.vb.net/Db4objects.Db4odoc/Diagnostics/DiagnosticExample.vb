' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query
Imports Db4objects.Db4o.Diagnostic
Imports System
Imports System.IO

Namespace Db4objects.Db4odoc.Diagnostics
    Public Class DiagnosticExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args As String())
            TestEmpty()
            TestArbitrary()
            TestIndexDiagnostics()
            TestTranslatorDiagnostics()
        End Sub
        ' end Main

        Public Shared Sub TestEmpty()
            Db4oFactory.Configure().Diagnostic().AddListener(New DiagnosticToConsole())
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                SetEmptyObject(db)
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestEmpty

        Private Shared Sub SetEmptyObject(ByVal db As IObjectContainer)
            Dim empty As Empty = New Empty()
            db.Set(empty)
        End Sub
        ' end SetEmptyObject

        Public Shared Sub TestArbitrary()
            Db4oFactory.Configure().Diagnostic().AddListener(New DiagnosticToConsole())
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim pilot As Pilot = New Pilot("Rubens Barrichello", 99)
                db.Set(pilot)
                QueryPilot(db)
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestArbitrary

        Private Shared Sub QueryPilot(ByVal db As IObjectContainer)
            Dim i() As Integer = New Integer() {19, 100}

            Dim result As IObjectSet = db.Query(New ArbitraryQuery(i))
            ListResult(result)
        End Sub
        ' end QueryPilot

        Public Shared Sub TestIndexDiagnostics()
            Db4oFactory.Configure().Diagnostic().RemoveAllListeners()
            Db4oFactory.Configure().Diagnostic().AddListener(New IndexDiagListener())
            Db4oFactory.Configure().UpdateDepth(3)
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim pilot1 As Pilot = New Pilot("Rubens Barrichello", 99)
                db.Set(pilot1)
                Dim pilot2 As Pilot = New Pilot("Michael Schumacher", 100)
                db.Set(pilot2)
                QueryPilot(db)
                SetEmptyObject(db)
                Dim query As IQuery = db.Query()
                query.Constrain(GetType(Pilot))
                query.Descend("_points").Constrain("99")
                Dim result As IObjectSet = query.Execute()
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestIndexDiagnostics

        Public Shared Sub TestTranslatorDiagnostics()
            StoreTranslatedCars()
            RetrieveTranslatedCars()
            RetrieveTranslatedCarsNQ()
            RetrieveTranslatedCarsNQUnopt()
            RetrieveTranslatedCarsSODAEv()
        End Sub
        ' end TestTranslatorDiagnostics

        Public Shared Sub StoreTranslatedCars()
            Db4oFactory.Configure().ExceptionsOnNotStorable(True)
            Db4oFactory.Configure().ObjectClass(GetType(Car)).Translate(New CarTranslator())
            Db4oFactory.Configure().ObjectClass(GetType(Car)).CallConstructor(True)
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim car1 As Car = New Car("BMW")
                System.Diagnostics.Trace.WriteLine("ORIGINAL: " + car1.ToString())
                db.Set(car1)
                Dim car2 As Car = New Car("Ferrari")
                System.Diagnostics.Trace.WriteLine("ORIGINAL: " + car2.ToString())
                db.Set(car2)
            Catch exc As Exception
                System.Diagnostics.Trace.WriteLine(exc.Message)
                Return
            Finally
                db.Close()
            End Try
        End Sub
        ' end StoreTranslatedCars

        Public Shared Sub RetrieveTranslatedCars()
            Db4oFactory.Configure().Diagnostic().RemoveAllListeners()
            Db4oFactory.Configure().Diagnostic().AddListener(New TranslatorDiagListener())
            Db4oFactory.Configure().ExceptionsOnNotStorable(True)
            Db4oFactory.Configure().ObjectClass(GetType(Car)).Translate(New CarTranslator())
            Db4oFactory.Configure().ObjectClass(GetType(Car)).CallConstructor(True)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim query As IQuery = db.Query()
                query.Constrain(GetType(Car))
                Dim result As IObjectSet = query.Execute()
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end RetrieveTranslatedCars

        Public Shared Sub RetrieveTranslatedCarsNQ()
            Db4oFactory.Configure().Diagnostic().RemoveAllListeners()
            Db4oFactory.Configure().Diagnostic().AddListener(New TranslatorDiagListener())
            Db4oFactory.Configure().ExceptionsOnNotStorable(True)
            Db4oFactory.Configure().ObjectClass(GetType(Car)).Translate(New CarTranslator())
            Db4oFactory.Configure().ObjectClass(GetType(Car)).CallConstructor(True)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim result As IObjectSet = db.Query(New NewCarModel())
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end RetrieveTranslatedCarsNQ

        Public Shared Sub RetrieveTranslatedCarsNQUnopt()
            Db4oFactory.Configure().OptimizeNativeQueries(False)
            Db4oFactory.Configure().Diagnostic().RemoveAllListeners()
            Db4oFactory.Configure().Diagnostic().AddListener(New TranslatorDiagListener())
            Db4oFactory.Configure().ExceptionsOnNotStorable(True)
            Db4oFactory.Configure().ObjectClass(GetType(Car)).Translate(New CarTranslator())
            Db4oFactory.Configure().ObjectClass(GetType(Car)).CallConstructor(True)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim result As IObjectSet = db.Query(New NewCarModel())
                ListResult(result)
            Finally
                Db4oFactory.Configure().OptimizeNativeQueries(True)
                db.Close()
            End Try
        End Sub
        ' end RetrieveTranslatedCarsNQUnopt

        Public Shared Sub RetrieveTranslatedCarsSODAEv()
            Db4oFactory.Configure().Diagnostic().RemoveAllListeners()
            Db4oFactory.Configure().Diagnostic().AddListener(New TranslatorDiagListener())
            Db4oFactory.Configure().ExceptionsOnNotStorable(True)
            Db4oFactory.Configure().ObjectClass(GetType(Car)).Translate(New CarTranslator())
            Db4oFactory.Configure().ObjectClass(GetType(Car)).CallConstructor(True)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim query As IQuery = db.Query()
                query.Constrain(GetType(Car))
                query.Constrain(New CarEvaluation())
                Dim result As IObjectSet = query.Execute()
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end RetrieveTranslatedCarsSODAEv

        Public Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace
