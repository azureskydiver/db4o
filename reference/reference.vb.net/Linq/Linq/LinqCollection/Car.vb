' Copyright (C) 2008 db4objects Inc. http://www.db4o.com 

Namespace Db4objects.Db4odoc.LinqCollection
    Class Car
        Private _model As String

        Private _pilot As Pilot

        Public Sub New(ByVal model As String, ByVal pilot As Pilot)
            _model = model
            _pilot = pilot
        End Sub

        Public ReadOnly Property Pilot() As Pilot
            Get
                Return _pilot
            End Get
        End Property

        Public ReadOnly Property Model() As String
            Get
                Return _model
            End Get
        End Property

        Public Overloads Overrides Function ToString() As String
            Return String.Format("{0}[{1}]", _model, _pilot)
        End Function
    End Class
End Namespace
