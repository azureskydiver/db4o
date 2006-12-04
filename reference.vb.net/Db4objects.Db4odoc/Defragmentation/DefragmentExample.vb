' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports Db4objects.Db4o.Defragment


Namespace Db4objects.Db4odoc.ClientServer
    Public Class DefragmentExample

        Public Shared Sub Main(ByVal args() As String)
            RunDefragment()
        End Sub
        ' end Main

        Public Shared Sub RunDefragment()
            Dim config As DefragmentConfig = New DefragmentConfig("sample.yap", "sample.bap")
            config.ForceBackupDelete(True)
            config.StoredClassFilter(New AvailableTypeFilter())
            Try
                Defragment.Defrag(config)
            Catch ex As Exception
                System.Console.WriteLine(ex.Message)
            End Try
        End Sub
        ' end RunDefragment
    End Class
End Namespace

