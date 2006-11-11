' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com
Imports System

Namespace Db4objects.Db4odoc.StaticFields
    Public Class Pilot
        Private _name As String
        Private _category As PilotCategories

        Public Sub New(ByVal name As String, ByVal Category As PilotCategories)
            Me._name = name
            Me._category = Category
        End Sub

        Public ReadOnly Property Category() As PilotCategories
            Get
                Return _category
            End Get
        End Property

        Public ReadOnly Property Name() As String
            Get
                Return _name
            End Get
        End Property

        Public Overrides Function ToString() As String
            Return String.Format("{0}/{1}", _name, _category)
        End Function
    End Class
End Namespace
