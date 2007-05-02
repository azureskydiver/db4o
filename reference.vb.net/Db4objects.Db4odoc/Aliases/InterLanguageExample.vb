' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Aliases

    Class InterLanguageExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args As String())
            GetObjects(ConfigureAlias())
        End Sub
        ' end Main

        Public Shared Function ConfigureAlias() As IConfiguration
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.AddAlias(New WildcardAlias("com.db4odoc.aliases.*", "Db4objects.Db4odoc.Aliases.*, Db4objects.Db4odoc"))
            configuration.AddAlias(New TypeAlias("com.db4o.ext.Db4oDatabase", "Db4objects.Db4o.Ext.Db4oDatabase, Db4objects.Db4o"))
            Return configuration
        End Function
        ' end ConfigureAlias

        Public Shared Sub GetObjects(ByVal configuration As IConfiguration)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim result As IObjectSet = db.Query(GetType(Pilot))
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end GetObjects

        Public Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace