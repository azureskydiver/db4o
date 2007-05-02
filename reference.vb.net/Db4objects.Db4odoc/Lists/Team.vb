' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System.Collections

Namespace Db4objects.Db4odoc.Lists
    Public Class Team
        Private _pilots As IList
        Private _name As String

        Public Sub New()
            _pilots = CollectionFactory.NewList()
        End Sub

        Public Property Name() As String
            Get
                Return _name
            End Get
            Set(ByVal Value As String)
                _name = Value
            End Set
        End Property

        Public Sub AddPilot(ByVal pilot As Pilot)
            _pilots.Add(pilot)
        End Sub

        Public Function GetPilot(ByVal index As Integer) As evaluations.Pilot
            Return CType(_pilots(index), evaluations.Pilot)
        End Function

        Public Sub RemovePilot(ByVal index As Integer)
            _pilots.Remove(index)
        End Sub

        Public Sub UpdatePilot(ByVal index As Integer, ByVal NewPilot As evaluations.Pilot)
            _pilots(index) = NewPilot
        End Sub
    End Class
End Namespace
