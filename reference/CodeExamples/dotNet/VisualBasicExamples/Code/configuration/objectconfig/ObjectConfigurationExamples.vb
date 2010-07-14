Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config

Namespace Db4oDoc.Code.Configuration.ObjectConfig
    Public Class ObjectConfigurationExamples
        Private Const DatabaseFile As [String] = "database.db4o"

        Private Shared Sub SetMinimalActivationDepth()
            ' #example: Set minimum activation depth
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.Common.ObjectClass(GetType(Person)).MinimumActivationDepth(2)
            ' #end example

            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, DatabaseFile)
            container.Close()
        End Sub


        Private Shared Sub CallConstructor()
            ' #example: Call constructor
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.Common.ObjectClass(GetType(Person)).CallConstructor(True)
            ' #end example

            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, DatabaseFile)
            container.Close()
        End Sub

    End Class

    Public Class Person
    End Class
End Namespace