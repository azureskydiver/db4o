' Copyright (C) 2007 db4objects Inc. http://www.db4o.com


Namespace Db4objects.Db4odoc.Equality
    Class Pilot
        Private _name As String
        Private _points As Integer

        Public Sub New(ByVal name As String, ByVal points As Integer)
            _name = name
            _points = points
        End Sub

        Public Overloads Overrides Function Equals(ByVal obj As Object) As Boolean
            Dim pilot As Pilot = DirectCast(obj, Pilot)
            Return pilot.Name.Equals(_name) AndAlso pilot.Points = _points
        End Function
        ' end Equals

        Public Overloads Overrides Function GetHashCode() As Integer
            Return _name.GetHashCode() Xor _points.GetHashCode()
        End Function
        ' end GetHashCode

        Public Property Name() As String
            Get
                Return _name
            End Get
            Set(ByVal value As String)
                _name = value
            End Set
        End Property

        Public ReadOnly Property Points() As Integer
            Get
                Return _points
            End Get
        End Property

        Public Overloads Overrides Function ToString() As String
            Return String.Format("{0}/{1}", _name, _points)
        End Function
    End Class
End Namespace