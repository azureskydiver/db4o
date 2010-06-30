Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Internal.Ids

Namespace Db4oDoc.Code.Configuration.IdSystem
    Public Class IdSystemConfigurationExamples

        Private Shared Sub StackedBTreeIdSystem()
            ' #example: Use stacked B-trees for storing the ids
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.IdSystem.UseStackedBTreeSystem()
            ' #end example

            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, "database.db4o")
            container.Close()
        End Sub
        Private Shared Sub BTreeIdSystem()
            ' #example: Use a single B-tree for storing the ids.
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.IdSystem.UseSingleBTreeSystem()
            ' #end example

            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, "database.db4o")
            container.Close()
        End Sub
        Private Shared Sub UseMemoryIDSystem()
            ' #example: Use a in-memory id system
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.IdSystem.UseInMemorySystem()
            ' #end example

            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, "database.db4o")
            container.Close()
        End Sub

        Private Shared Sub PointerIdSystem()
            ' #example: Use pointers for storing the ids
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.IdSystem.UsePointerBasedSystem()
            ' #end example

            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, "database.db4o")
            container.Close()
        End Sub

        Private Shared Sub CustomIdSystem()
            ' #example: use a costume id system
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.IdSystem.UseCustomSystem(New CostumeIdSystemFactory())
            ' #end example

            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, "database.db4o")
            container.Close()
        End Sub

    End Class

    Class CostumeIdSystemFactory
        Implements IIdSystemFactory
        Public Function NewInstance(ByVal localObjectContainer As Db4objects.Db4o.Internal.LocalObjectContainer) _
            As IIdSystem Implements IIdSystemFactory.NewInstance
            Return New InMemoryIdSystem(localObjectContainer)
        End Function
    End Class


End Namespace