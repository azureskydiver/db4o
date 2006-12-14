Imports System
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Aliases

    Class InterLanguageExample
        Private Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args As String())
            ConfigureAlias()
            GetObjects()
        End Sub
        ' end Main

        Public Shared Sub ConfigureAlias()
            Db4oFactory.Configure.AddAlias(New WildcardAlias("com.db4odoc.aliases.*", "Db4objects.Db4odoc.Aliases.*, Db4objects.Db4odoc"))
        End Sub
        ' end ConfigureAlias

        Public Shared Sub GetObjects()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
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