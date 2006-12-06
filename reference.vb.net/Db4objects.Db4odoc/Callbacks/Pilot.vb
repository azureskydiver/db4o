Namespace Db4objects.Db4odoc.Callbacks
    Public Class Pilot
        Private _name As String

        Public Sub New(ByVal name As String)
            _name = name
        End Sub

        Public ReadOnly Property Name() As String
            Get
                Return _name
            End Get
        End Property

        Public Overloads Overrides Function ToString() As String
            Return _name
        End Function

    End Class
End Namespace
