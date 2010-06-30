Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config

Namespace Db4oDoc.Code.Configuration.File
    Public Class FileConfiguration
        Public Shared Sub AsynchronousSync()
            ' #example: Allow asynchronous synchronisation of the file-flushes
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.File.AsynchronousSync(True)
            ' #end example
            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, "database.db4o")
            container.Close()

        End Sub

        Public Shared Sub ChangeBlobPath()
            ' #example: Configure the blob-path
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.File.BlobPath = "myBlobDirectory"
            ' #end example
            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, "database.db4o")
            container.Close()

        End Sub
        Public Shared Sub ReserveSpace()
            ' #example: Configure the growth size
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.File.DatabaseGrowthSize = 4096
            ' #end example
            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, "database.db4o")
            container.Close()

        End Sub
        Public Shared Sub DisableCommitRecovers()
            ' #example: Disable commit recovery
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.File.DisableCommitRecovery()
            ' #end example
            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, "database.db4o")
            container.Close()

        End Sub
        Public Shared Sub ReadOnlyMode()
            ' #example: Set read only mode
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.File.ReadOnly = True
            ' #end example
            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, "database.db4o")
            container.Close()

        End Sub
        Public Shared Sub RecoveryMode()
            ' #example: Enable recovery mode to open a corrupted database
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.File.RecoveryMode = True
            ' #end example
            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, "database.db4o")
            container.Close()
        End Sub
        Public Shared Sub ReserveStorageSpace()
            ' #example: Reserve storage space
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.File.ReserveStorageSpace = 1024 * 1024
            ' #end example
            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, "database.db4o")
            container.Close()
        End Sub
    End Class

End Namespace