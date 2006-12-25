Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config

Namespace Db4objects.Db4odoc.Aliases

    Class AliasExample
        Private Shared ReadOnly YapFileName As String = "formula1.yap"
        Private Shared tAlias As TypeAlias

        Public Shared Sub Main(ByVal args As String())
            ConfigureClassAlias()
            SaveDrivers()
            RemoveClassAlias()
            GetPilots()
            SavePilots()
            ConfigureAlias()
            GetObjectsWithAlias()
        End Sub
        ' end Main

        Public Shared Sub ConfigureClassAlias()
            ' create a new alias
            tAlias = New TypeAlias("Db4objects.Db4odoc.Aliases.Pilot, Db4objects.Db4odoc", "Db4objects.Db4odoc.Aliases.Driver, Db4objects.Db4odoc")
            ' add the alias to the db4o configuration 
            Db4oFactory.Configure.AddAlias(tAlias)
            ' check how does the alias resolve
            Console.WriteLine("Stored name for Db4objects.Db4odoc.Aliases.Driver: " + tAlias.ResolveRuntimeName("Db4objects.Db4odoc.Aliases.Driver, Db4objects.Db4odoc"))
            Console.WriteLine("Runtime name for Db4objects.Db4odoc.Aliases.Pilot: " + tAlias.ResolveStoredName("Db4objects.Db4odoc.Aliases.Pilot, Db4objects.Db4odoc"))
        End Sub
        ' end ConfigureClassAlias

        Public Shared Sub RemoveClassAlias()
            Db4oFactory.Configure.RemoveAlias(tAlias)
        End Sub
        ' end RemoveClassAlias

        Public Shared Sub SaveDrivers()
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim driver As Driver = New Driver("David Barrichello", 99)
                db.Set(driver)
                driver = New Driver("Kimi Raikkonen", 100)
                db.Set(driver)
            Finally
                db.Close()
            End Try
        End Sub
        ' end SaveDrivers

        Public Shared Sub SavePilots()
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim pilot As Pilot = New Pilot("David Barrichello", 99)
                db.Set(pilot)
                pilot = New Pilot("Kimi Raikkonen", 100)
                db.Set(pilot)
            Finally
                db.Close()
            End Try
        End Sub
        ' end SavePilots

        Public Shared Sub GetPilots()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim result As IObjectSet = db.Get(GetType(Db4objects.Db4odoc.Aliases.Pilot))
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end GetPilots

        Public Shared Sub GetObjectsWithAlias()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim result As IObjectSet = db.Query(GetType(Db4objects.Db4odoc.Aliases.NewAlias.Pilot))
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end GetObjectsWithAlias

        Public Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Size)
            While result.HasNext
                Console.WriteLine(result.Next)
            End While
        End Sub
        ' end ListResult

        Public Shared Sub ConfigureAlias()
            Dim wAlias As WildcardAlias = New WildcardAlias("Db4objects.Db4odoc.Aliases.*", "Db4objects.Db4odoc.Aliases.NewAlias.*")
            Db4oFactory.Configure.AddAlias(wAlias)
            Console.WriteLine("Stored name for Db4objects.Db4odoc.Aliases.NewAlias.Pilot: " + wAlias.ResolveRuntimeName("Db4objects.Db4odoc.Aliases.NewAlias.Pilot"))
            Console.WriteLine("Runtime name for Db4objects.Db4odoc.Aliases.Pilot: " + wAlias.ResolveStoredName("Db4objects.Db4odoc.Aliases.Pilot"))
        End Sub
        ' end ConfigureAlias

    End Class
End Namespace