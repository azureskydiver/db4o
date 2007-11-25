' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Query
Imports Db4objects.Db4o.Diagnostic

Namespace Db4objects.Db4odoc.Diagnostics
    Public Class DiagnosticExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args As String())
            TestEmpty()
            TestArbitrary()
            TestIndexDiagnostics()
            TestTranslatorDiagnostics()
        End Sub
        ' end Main

        Private Shared Sub TestEmpty()
            File.Delete(Db4oFileName)
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.Diagnostic().AddListener(New DiagnosticToConsole())

            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
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

        Private Shared Sub TestArbitrary()
            File.Delete(Db4oFileName)
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.Diagnostic().AddListener(New DiagnosticToConsole())
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
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

        Private Shared Sub TestIndexDiagnostics()
            File.Delete(Db4oFileName)
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.Diagnostic().RemoveAllListeners()
            configuration.Diagnostic().AddListener(New IndexDiagListener())
            configuration.UpdateDepth(3)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
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

        Private Shared Sub TestTranslatorDiagnostics()
            StoreTranslatedCars()
            RetrieveTranslatedCars()
            RetrieveTranslatedCarsNQ()
            RetrieveTranslatedCarsNQUnopt()
            RetrieveTranslatedCarsSODAEv()
        End Sub
        ' end TestTranslatorDiagnostics

        Private Shared Sub StoreTranslatedCars()
            File.Delete(Db4oFileName)
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ExceptionsOnNotStorable(True)
            configuration.ObjectClass(GetType(Car)).Translate(New CarTranslator())
            configuration.ObjectClass(GetType(Car)).CallConstructor(True)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
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

        Private Shared Sub RetrieveTranslatedCars()
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.Diagnostic().RemoveAllListeners()
            configuration.Diagnostic().AddListener(New TranslatorDiagListener())
            configuration.ExceptionsOnNotStorable(True)
            configuration.ObjectClass(GetType(Car)).Translate(New CarTranslator())
            configuration.ObjectClass(GetType(Car)).CallConstructor(True)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
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

        Private Shared Sub RetrieveTranslatedCarsNQ()
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.Diagnostic().RemoveAllListeners()
            configuration.Diagnostic().AddListener(New TranslatorDiagListener())
            configuration.ExceptionsOnNotStorable(True)
            configuration.ObjectClass(GetType(Car)).Translate(New CarTranslator())
            configuration.ObjectClass(GetType(Car)).CallConstructor(True)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim result As IObjectSet = db.Query(New NewCarModel())
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end RetrieveTranslatedCarsNQ

        Private Shared Sub RetrieveTranslatedCarsNQUnopt()
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.OptimizeNativeQueries(False)
            configuration.Diagnostic().RemoveAllListeners()
            configuration.Diagnostic().AddListener(New TranslatorDiagListener())
            configuration.ExceptionsOnNotStorable(True)
            configuration.ObjectClass(GetType(Car)).Translate(New CarTranslator())
            configuration.ObjectClass(GetType(Car)).CallConstructor(True)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim result As IObjectSet = db.Query(New NewCarModel())
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end RetrieveTranslatedCarsNQUnopt

        Private Shared Sub RetrieveTranslatedCarsSODAEv()
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.Diagnostic().RemoveAllListeners()
            configuration.Diagnostic().AddListener(New TranslatorDiagListener())
            configuration.ExceptionsOnNotStorable(True)
            configuration.ObjectClass(GetType(Car)).Translate(New CarTranslator())
            configuration.ObjectClass(GetType(Car)).CallConstructor(True)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
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

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace
