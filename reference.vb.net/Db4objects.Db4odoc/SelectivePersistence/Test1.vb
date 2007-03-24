Namespace Db4objects.Db4odoc.SelectivePersistence

    Class Test1
        Private name As String
        Private transientClass As NotStorable

        Public Sub New(ByVal name As String, ByVal transientClass As NotStorable)
            Me.name = name
            Me.transientClass = transientClass
        End Sub

        Public Overloads Overrides Function ToString() As String
            If transientClass Is Nothing Then
                Return String.Format("{0}/{1}", name, "Nothing")
            Else
                Return String.Format("{0}/{1}", name, transientClass.ToString())
            End If
        End Function
    End Class
End Namespace