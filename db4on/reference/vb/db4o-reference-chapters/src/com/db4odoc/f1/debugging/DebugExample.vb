' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports com.db4o
Imports com.db4o.query

Namespace com.db4odoc.f1.debugging
    Public Class DebugExample
        Inherits Util
        Public Shared Sub Main(ByVal args() As String)
            SetCars()
        End Sub

        Public Shared Sub SetCars()
            Db4o.Configure().MessageLevel(3)
            File.Delete(Util.YapFileName)
            Dim db As ObjectContainer = Db4o.OpenFile(Util.YapFileName)
            Try
                Dim car1 As Car = New Car("BMW")
                db.Set(car1)
                Dim car2 As Car = New Car("Ferrari")
                db.Set(car2)
                db.Deactivate(car1, 2)
                Dim query As Query = db.Query()
                query.Constrain(GetType(Car))
                Dim results As ObjectSet = query.Execute()
                ListResult(results)
            Finally
                db.Close()
            End Try
            Db4o.Configure().MessageLevel(0)
        End Sub

    End Class
End Namespace

