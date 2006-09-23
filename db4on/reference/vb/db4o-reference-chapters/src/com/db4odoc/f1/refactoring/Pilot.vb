' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System

Namespace com.db4odoc.f1.refactoring
    Public Class Pilot
        Private _name As String

        Public Sub New(ByVal name As String)
            Me._name = name
        End Sub

        Public Property Name() As String
            Get
                Return _name
            End Get
            Set(ByVal Value As String)
                _name = value
            End Set
        End Property
        Public Overrides Function ToString() As String
            Return _name
        End Function
    End Class
End Namespace

