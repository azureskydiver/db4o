Namespace Db4objects.Db4odoc.Aliases

    Class Pilot
        Private name As String
        Private points As Integer

        Public Sub New(ByVal name As String, ByVal points As Integer)
            Me.name = name
            Me.points = points
        End Sub

        Public Property PilotName() As String
            Get
                Return Name
            End Get
            Set(ByVal value As String)
                name = value
            End Set
        End Property

        Public Overloads Overrides Function ToString() As String
            Return String.Format("{0}/{1}", name, points)
        End Function
    End Class
End Namespace