Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config

Namespace Db4oDoc.Code.Configuration.Common
    Public Class CommonConfigurationExamples
        Private Const DatabaseFile As String = "database.db4o"


        Private Shared Sub ExampleForCommonConfig()
            ' #example: change activation depth
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.Common.ActivationDepth = 2
            ' other configurations...

            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, DatabaseFile)
            ' #end example
            container.Close()
        End Sub

        Private Shared Sub InternStrings()
            ' #example: intern strings
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.Common.InternStrings = True
            ' #end example

            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, DatabaseFile)

            container.Close()
        End Sub

        Private Shared Sub NameProvider()
            ' #example: set a name-provider
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.Common.NameProvider(New SimpleNameProvider("Database"))
            ' #end example

            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, DatabaseFile)

            container.Close()
        End Sub


        Private Shared Sub ChangeWeakReferenceCollectionIntervall()
            ' #example: change weak reference collection interval
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.Common.WeakReferenceCollectionInterval = (10 * 1000)
            ' #end example

            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, DatabaseFile)

            container.Close()
        End Sub

        Private Shared Sub MarkTransient()
            CleanUp()

            ' #example: add an transient marker annotatin
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.Common.MarkTransient(GetType(TransientMarkerAttribute).FullName)
            ' #end example

            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, DatabaseFile)
            container.Store(New WithTransient())
            container.Close()

            ReadWithTransientMarker()

            CleanUp()
        End Sub

        Private Shared Sub CleanUp()
            System.IO.File.Delete(DatabaseFile)
        End Sub

        Private Shared Sub ReadWithTransientMarker()
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.Common.MarkTransient(GetType(TransientMarkerAttribute).FullName)
            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, DatabaseFile)
            Dim instance As WithTransient = container.Query(Of WithTransient)()(0)

            AssertTransientNotStored(instance)

            container.Close()
        End Sub

        Private Shared Sub AssertTransientNotStored(ByVal instance As WithTransient)
            If instance.TransientString IsNot Nothing Then
                Throw New Exception("Transient was stored!")
            End If
        End Sub
    End Class

    Friend Class WithTransient
        <TransientMarker()> _
        Private m_transientString As String = "New"

        Public Property TransientString() As String
            Get
                Return m_transientString
            End Get
            Set(ByVal value As String)
                m_transientString = value
            End Set
        End Property
    End Class

    <AttributeUsage(AttributeTargets.Field)> _
    Friend Class TransientMarkerAttribute
        Inherits Attribute
    End Class
End Namespace