' Copyright (C) 2008 db4objects Inc. http://www.db4o.com 

Namespace Db4objects.Db4odoc.LinqCollection
    Class Pilot
        Private _name As String
        Private _points As Integer

        Public Sub New(ByVal name As String, ByVal points As Integer)
            _name = name
            _points = points
        End Sub

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

        Public Overloads Overrides Function Equals(ByVal obj As Object) As Boolean
            If TypeOf obj Is Pilot Then
                Return (DirectCast(obj, Pilot).Name.Equals(_name) AndAlso DirectCast(obj, Pilot).Points = _points)
            End If
            Return False
        End Function

        Public Overloads Overrides Function GetHashCode() As Integer
            Return _name.GetHashCode() + _points
        End Function

        Public Overloads Overrides Function ToString() As String
            Return String.Format("{0}/{1}", _name, _points)
        End Function
    End Class
End Namespace
