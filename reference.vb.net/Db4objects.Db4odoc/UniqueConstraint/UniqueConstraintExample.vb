' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Constraints
Namespace Db4objects.Db4odoc.UniqueConstraint

    Class UniqueConstraintExample
        Private Const FILENAME As String = "test.db"

        Public Shared Sub Main(ByVal args As String())
            Configure()
            StoreObjects()
        End Sub
        ' end Main

        Private Shared Function Configure() As IConfiguration
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration
            configuration.ObjectClass(GetType(Pilot)).ObjectField("_name").Indexed(True)
            configuration.Add(New UniqueFieldValueConstraint(GetType(Pilot), "_name"))
            Return configuration
        End Function
        ' end Configure

        Private Shared Sub StoreObjects()
            File.Delete(FILENAME)
            Dim server As IObjectServer = Db4oFactory.OpenServer(Configure, FILENAME, 0)
            Dim pilot1 As Pilot = Nothing
            Dim pilot2 As Pilot = Nothing
            Try
                Dim client1 As IObjectContainer = server.OpenClient
                Try
                    ' creating and storing pilot1 to the database
                    pilot1 = New Pilot("Rubens Barichello", 99)
                    client1.Set(pilot1)
                    Dim client2 As IObjectContainer = server.OpenClient
                    Try
                        ' creating and storing pilot2 to the database
                        pilot2 = New Pilot("Rubens Barichello", 100)
                        client2.Set(pilot2)
                        ' end commit the changes
                        client2.Commit()
                    Catch ex As UniqueFieldValueConstraintViolationException
                        System.Console.WriteLine("Unique constraint violation in client2 saving: " + pilot1.ToString())
                        client2.Rollback()
                    Finally
                        client2.Close()
                    End Try
                    'Pilot Rubens Barichello is already in the database,
                    ' commit will fail
                    client1.Commit()
                Catch ex As UniqueFieldValueConstraintViolationException
                    System.Console.WriteLine("Unique constraint violation in client1 saving: " + pilot2.ToString())
                    client1.Rollback()
                Finally
                    client1.Close()
                End Try
            Finally
                server.Close()
            End Try
        End Sub
        ' end StoreObjects

    End Class
End Namespace