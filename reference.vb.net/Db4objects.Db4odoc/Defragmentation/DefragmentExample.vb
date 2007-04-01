' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Defragment

Namespace Db4objects.Db4odoc.Defragmentation

    Class DefragmentExample
        Private Const DB_FILE As String = "test.db4o"
        Private Const BACKUP_FILE As String = "test.bap"

        Public Shared Sub Main(ByVal args As String())
            SimplestDefragment()
            ConfiguredDefragment()
            DefragmentWithListener()
        End Sub
        ' end Main

        Public Shared Sub SimplestDefragment()
            Try
                Defragment.Defrag(DB_FILE)
            Catch ex As Exception
                System.Console.WriteLine(ex.Message)
            End Try
        End Sub
        ' end SimplestDefragment

        Public Shared Sub ConfiguredDefragment()
            Dim config As DefragmentConfig = New DefragmentConfig(DB_FILE, BACKUP_FILE, New TreeIDMapping)
            config.ObjectCommitFrequency(5000)
            config.Db4oConfig(Db4oFactory.CloneConfiguration)
            config.ForceBackupDelete(True)
            config.UpgradeFile(DB_FILE + ".upg")
            Try
                Defragment.Defrag(config)
            Catch ex As Exception
                System.Console.WriteLine(ex.Message)
            End Try
        End Sub
        ' end ConfiguredDefragment

        Public Shared Sub DefragmentWithListener()
            Dim config As DefragmentConfig = New DefragmentConfig(DB_FILE, BACKUP_FILE)
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