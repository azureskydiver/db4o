' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System

Namespace com.db4odoc.f1.serialize
    Public Class Car
        Public _model As String
        Public _pilot As Pilot

        Public Sub New()
            _model = ""
            _pilot = Nothing
        End Sub

        Public Sub New(ByVal model As String, ByVal pilot As Pilot)
            _model = model
            _pilot = pilot
        End Sub

        Public ReadOnly Property Pilot() As Pilot
            Get
                Return _pilot
            End Get
        End Property

        Public Overrides Function ToString() As String
            Return String.Format("{0}({1})", _model, _pilot)
        End Function
    End Class
End Namespace


