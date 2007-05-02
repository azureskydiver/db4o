' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Namespace Db4objects.Db4odoc.CommitCallbacks

    Class Car
        Private _name As String
        Private _model As Integer
        Private _pilot As Pilot

        Public Sub New(ByVal name As String, ByVal model As Integer, ByVal pilot As Pilot)
            _name = name
            _model = model
            _pilot = pilot
        End Sub

        Public WriteOnly Property Model() As Integer
            Set(ByVal value As Integer)
                _model = value
            End Set
        End Property

        Public WriteOnly Property Pilot() As Pilot
            Set(ByVal value As Pilot)
                _pilot = value
            End Set
        End Property

        Public Overloads Overrides Function ToString() As String
            Return String.Format("Car: {0} {1} Pilot: {2} ", _name, _model, _pilot.Name)
        End Function
    End Class
End Namespace