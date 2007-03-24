Imports System
Imports System.Collections.Generic
Imports System.Text
Namespace Db4objects.Db4odoc.SelectivePersistence

    Class Test2
        Private test1 As Test1
        Private name As String
        Private transientClass As NotStorable

        Public Sub New(ByVal name As String, ByVal transientClass As NotStorable, ByVal test1 As Test1)
            Me.test1 = test1
            Me.name = name
            Me.transientClass = transientClass
        End Sub

        Public Overloads Overrides Function ToString() As String
            If transientClass Is Nothing Then
                Return String.Format("{0}/{1}; test1: {2}", name, "Nothing", test1.ToString())
            Else
                Return String.Format("{0}/{1}; test1: {2}", name, transientClass.ToString(), test1.ToString())
            End If
        End Function
    End Class
End Namespace