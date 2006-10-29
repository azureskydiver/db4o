' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports System.Collections
Imports com.db4o
Imports com.db4o.query

Namespace com.db4odoc.f1.selpersist
    Public Class MarkTransientExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub main(ByVal args() As String)
            ConfigureTransient()
            SaveObjects()
            RetrieveObjects()
        End Sub

        Public Shared Sub ConfigureTransient()
            Db4o.Configure().MarkTransient("com.db4odoc.f1.selpersist.FieldTransient")
        End Sub

        Public Shared Sub SaveObjects()
            File.Delete(YapFileName)
            Dim oc As ObjectContainer = Db4o.OpenFile(YapFileName)
            Try
                Dim test As Test = New Test("Transient string", "Persistent string")
                oc.Set(test)
                Dim testc As TestCusomized = New TestCusomized("Transient string", "Persistent string")
                oc.Set(testc)
            Finally
                oc.Close()
            End Try
        End Sub

        Public Shared Sub RetrieveObjects()
            Dim oc As ObjectContainer = Db4o.OpenFile(YapFileName)
            Try
                Dim query As Query = oc.Query()
                query.Constrain(GetType(Object))
                Dim result As IList = query.Execute()
                ListResult(result)
            Finally
                oc.Close()
            End Try
        End Sub


        Public Shared Sub ListResult(ByVal result As IList)
            Console.WriteLine(result.Count)
            Dim x As Integer
            For x = 0 To result.Count - 1 Step x + 1
                Console.WriteLine(result(x))
            Next
        End Sub
    End Class
End Namespace

