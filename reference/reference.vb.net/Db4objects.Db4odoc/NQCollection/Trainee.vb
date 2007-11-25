Namespace Db4objects.Db4odoc.NQCollection

    Class Trainee
        Implements Person
        Private _name As String
        Private _instructor As Pilot

        Public Sub New(ByVal name As String, ByVal pilot As Pilot)
            _name = name
            _instructor = pilot
        End Sub

        Public ReadOnly Property Name() As String
            Get
                Return _name
            End Get
        End Property

        Public ReadOnly Property Instructor() As Pilot
            Get
                Return _instructor
            End Get
        End Property

        Public Overloads Overrides Function ToString() As String
            Return String.Format("{0}({1})", _name, _instructor)
        End Function
    End Class
End Namespace