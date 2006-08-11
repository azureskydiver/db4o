Imports System
Imports com.db4o
Namespace com.db4odoc.f1
    Public Class Util
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared ReadOnly ServerPort As Integer = 56128

        Public Shared ReadOnly ServerUser As String = "user"

        Public Shared ReadOnly ServerPassword As String = "password"

        Public Shared Sub ListResult(ByVal result As ObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub

        Public Shared Sub ListRefreshedResult(ByVal container As ObjectContainer, ByVal items As ObjectSet, ByVal depth As Integer)
            Console.WriteLine(items.Count)
            For Each item As Object In items
                container.Ext().Refresh(item, depth)
                Console.WriteLine(item)
            Next
        End Sub

        Public Shared Sub RetrieveAll(ByVal db As ObjectContainer)
            Dim result As ObjectSet = db.[Get](GetType(Object))
            ListResult(result)
        End Sub

        Public Shared Sub DeleteAll(ByVal db As ObjectContainer)
            Dim result As ObjectSet = db.[Get](GetType(Object))
            For Each item As Object In result
                db.Delete(item)
            Next
        End Sub

    End Class
End Namespace
