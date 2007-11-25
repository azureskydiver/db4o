' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com
Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext

Namespace Db4objects.Db4odoc.Exceptions

    Class ExceptionExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args As String())
            Dim db As IObjectContainer = OpenDatabase()
            db.Close()
            OpenClient()
            Work()
        End Sub
        ' end Main

        Private Shared Function OpenDatabase() As IObjectContainer
            Dim db As IObjectContainer = Nothing
            Try
                db = Db4oFactory.OpenFile(Db4oFileName)
            Catch ex As DatabaseFileLockedException
                ' System.Console.WriteLine(ex.Message)
                ' ask the user for a new filename, print
                ' or log the exception message
                ' and close the application,
                ' find and fix the reason
                ' and try again
            End Try
            Return db
        End Function
        ' end OpenDatabase

        Private Shared Function OpenClient() As IObjectContainer
            Dim db As IObjectContainer = Nothing
            Try
                db = Db4oFactory.OpenClient("host", 40, "user", "password")
            Catch ex As Exception
                ' System.Console.WriteLine(ex.Message)
                ' ask the user for a new filename, print
                ' or log the exception message
                ' and close the application,
                ' find and fix the reason
                ' and try again
            End Try
            Return db
        End Function
        ' end OpenClient

        Private Shared Sub Work()
            Dim db As IObjectContainer = OpenDatabase()
            Try
                ' do some work with db4o
                db.Commit()
            Catch ex As Db4oException
                ' handle exception ....
            Catch ex As Exception
                ' handle exception ....
            Finally
                db.Close()
            End Try
        End Sub
        ' end Work

    End Class
End Namespace