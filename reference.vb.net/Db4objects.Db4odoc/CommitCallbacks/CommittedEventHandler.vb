' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Events
Imports Db4objects.Db4o.Ext
Imports Db4objects.Db4o.Foundation


Namespace Db4objects.Db4odoc.CommitCallbacks
    Public Class CommittedEventHandler

        Private _objectContainer As IObjectContainer

        Private Delegate Sub OnCommittedHandler(ByVal sender As Object, ByVal args As CommitEventArgs)

        Public Sub New(ByVal objectContainer As IObjectContainer)
            _objectContainer = objectContainer
        End Sub

        Private Function CreateCommittedEventHandler(ByVal objectContainer As IObjectContainer) As OnCommittedHandler
            Return AddressOf OnCommitted
        End Function

        Public Sub OnCommitted(ByVal sender As Object, ByVal args As CommitEventArgs)
            ' get all the updated objects
            Dim updated As IObjectInfoCollection = args.Updated
            For Each info As IObjectInfo In updated
                Dim obj As Object = info.GetObject()
                ' refresh object on the client
                _objectContainer.Ext().Refresh(obj, 2)
            Next
        End Sub

    End Class
End Namespace

