' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Namespace Db4objects.Db4odoc.Structured
    Public Class Car
        Private _model As String

        Private _pilot As Pilot

        Public Sub New(ByVal model As String)
            _model = model
            _pilot = Nothing
        End Sub

        Public Property Pilot() As Pilot
            Get
                Return _pilot
            End Get
            Set(ByVal value As Pilot)
                _pilot = value
            End Set
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
