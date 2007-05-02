' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config

Namespace Db4objects.Db4odoc.Aliases
    Class AliasExample
        Private Const Db4oFileName As String = "reference.db4o"
        Private Shared tAlias As TypeAlias

        Public Shared Sub Main(ByVal args As String())
            Dim configuration As IConfiguration = ConfigureClassAlias()
            SaveDrivers(configuration)
            RemoveClassAlias(configuration)
            GetPilots(configuration)
            SavePilots(configuration)
            configuration = ConfigureAlias()
            GetObjectsWithAlias(configuration)
        End Sub
        ' end Main

        Private Shared Function ConfigureClassAlias() As IConfiguration
            ' create a new alias
            tAlias = New TypeAlias("Db4objects.Db4odoc.Aliases.Pilot, Db4objects.Db4odoc", "Db4objects.Db4odoc.Aliases.Driver, Db4objects.Db4odoc")
            ' add the alias to the db4o configuration 
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.AddAlias(tAlias)
            ' check how does the alias resolve
            Console.WriteLine("Stored name for Db4objects.Db4odoc.Aliases.Driver: " + tAlias.ResolveRuntimeName("Db4objects.Db4odoc.Aliases.Driver, Db4objects.Db4odoc"))
            Console.WriteLine("Runtime name for Db4objects.Db4odoc.Aliases.Pilot: " + tAlias.ResolveStoredName("Db4objects.Db4odoc.Aliases.Pilot, Db4objects.Db4odoc"))
            Return configuration
        End Function
        ' end ConfigureClassAlias

        Private Shared Sub RemoveClassAlias(ByRef configuration As IConfiguration)
            configuration.RemoveAlias(tAlias)
        End Sub
        ' end RemoveClassAlias

        Private Shared Sub SaveDrivers(ByVal configuration As IConfiguration)
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
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

        Private Shared Sub SavePilots(ByVal configuration As IConfiguration)
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
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

        Private Shared Sub GetPilots(ByVal configuration As IConfiguration)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim result As IObjectSet = db.Get(GetType(Db4objects.Db4odoc.Aliases.Pilot))
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end GetPilots

        Private Shared Sub GetObjectsWithAlias(ByVal configuration As IConfiguration)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim result As IObjectSet = db.Query(GetType(Db4objects.Db4odoc.Aliases.NewAlias.Pilot))
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end GetObjectsWithAlias

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Size)
            While result.HasNext
                Console.WriteLine(result.Next)
            End While
        End Sub
        ' end ListResult

        Private Shared Function ConfigureAlias() As IConfiguration
            Dim wAlias As WildcardAlias = New WildcardAlias("Db4objects.Db4odoc.Aliases.*", "Db4objects.Db4odoc.Aliases.NewAlias.*")
            ' Add the Alias to the configuration
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.AddAlias(wAlias)
            Console.WriteLine("Stored name for Db4objects.Db4odoc.Aliases.NewAlias.Pilot: " + wAlias.ResolveRuntimeName("Db4objects.Db4odoc.Aliases.NewAlias.Pilot"))
            Console.WriteLine("Runtime name for Db4objects.Db4odoc.Aliases.Pilot: " + wAlias.ResolveStoredName("Db4objects.Db4odoc.Aliases.Pilot"))
            Return configuration
        End Function
        ' end ConfigureAlias

    End Class
End Namespace