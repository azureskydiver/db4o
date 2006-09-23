' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System

Namespace com.db4odoc.f1.refactoring.Newclasses
    Public Class Pilot
        Private _name As Identity

        Public Sub New(ByVal name As Identity)
            Me._name = name
        End Sub

        Public Property Name() As Identity
            Get
                Return _name
            End Get
            Set(ByVal Value As Identity)
                _name = value
            End Set
        End Property

        Public Overrides Function ToString() As String
            Return _name.ToString()
        End Function
    End Class
End Namespace
