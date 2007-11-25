' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Defragment

Namespace Db4objects.Db4odoc.Defragmentation

    Class DefragmentExample
        Private Const DbFile As String = "test.db4o"
        Private Const BackupFile As String = "test.bap"

        Public Shared Sub Main(ByVal args As String())
            CleanFilesForTesting()
            SimplestDefragment()
            CleanFilesForTesting()
            ConfiguredDefragment()
            CleanFilesForTesting()
            DefragmentWithListener()
        End Sub
        ' end Main

        Private Shared Sub CleanFilesForTesting()
            File.Delete("test.bap")
            File.Delete("test.db4o.backup")
        End Sub
        ' end CleanFilesForTesting


        Public Shared Sub SimplestDefragment()
            Try
                Defragment.Defrag(DbFile)
            Catch ex As Exception
                System.Console.WriteLine(ex.Message)
            End Try
        End Sub
        ' end SimplestDefragment

        Public Shared Sub ConfiguredDefragment()
            Dim config As DefragmentConfig = New DefragmentConfig(DbFile, BackupFile, New TreeIDMapping)
            config.ObjectCommitFrequency(5000)
            config.Db4oConfig(Db4oFactory.CloneConfiguration)
            config.ForceBackupDelete(True)
            config.UpgradeFile(DbFile + ".upg")
            Try
                Defragment.Defrag(config)
            Catch ex As Exception
                System.Console.WriteLine(ex.Message)
            End Try
        End Sub
        ' end ConfiguredDefragment

        Public Shared Sub DefragmentWithListener()
            Dim config As DefragmentConfig = New DefragmentConfig(DbFile, BackupFile)
            Try
                Defragment.Defrag(config, New DefragmentListener)
            Catch ex As Exception
                System.Console.WriteLine(ex.Message)
            End Try
        End Sub
        ' end DefragmentWithListener

    End Class

    Public Class DefragmentListener
        Implements IDefragmentListener

        Sub NotifyDefragmentInfo(ByVal info As DefragmentInfo) Implements IDefragmentListener.NotifyDefragmentInfo
            Throw New System.Exception("The method or operation is not implemented.")
        End Sub
    End Class
    ' end DefragmentListener

End Namespace