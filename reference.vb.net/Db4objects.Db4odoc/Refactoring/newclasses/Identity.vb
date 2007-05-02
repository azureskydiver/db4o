' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System

Namespace Db4objects.Db4odoc.Refactoring.Newclasses
    Public Class Identity
        Private _name As String
        Private _id As String

        Public Sub New(ByVal name As String, ByVal id As String)
            _name = name
            _id = id
        End Sub

        Public Property Name() As String
            Get
                Return _name
            End Get
            Set(ByVal Value As String)
                _name = Value
            End Set
        End Property


        Public Overloads Overrides Function ToString() As String
            Return String.Format("{0}[{1}]", _name, _id)
        End Function
    End Class
End Namespace
