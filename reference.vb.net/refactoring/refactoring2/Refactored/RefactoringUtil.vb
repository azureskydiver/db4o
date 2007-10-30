' Copyright (C) 2007  db4objects Inc.  http://www.db4o.com 

Imports Db4objects.Db4o
Imports Db4objects.Db4odoc.Refactoring.Initial

Namespace Db4objects.Db4odoc.Refactoring.Refactored
    Class RefactoringUtil

        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args As String())
            MoveValues()
        End Sub

        Public Shared Sub MoveValues()
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim result As IObjectSet = container.[Get](New Initial.C())
                For i As Integer = 0 To result.Count - 1
                    Dim c As Initial.C = DirectCast(result(i), Initial.C)
                    Dim e As New E()
                    e.name = c.name
                    e.number = c.number
                    container.Delete(c)
                    container.[Set](e)

                Next
            Finally
                container.Close()
                System.Console.WriteLine("Done")
            End Try
        End Sub
        ' end moveValues


    End Class

End Namespace
