' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config

Namespace Db4objects.Db4odoc.Evaluations

    Public Class CarTranslator
        Implements IObjectConstructor

        Public Function OnStore(ByVal container As IObjectContainer, ByVal applicationObject As Object) As Object Implements IObjectConstructor.OnStore
            Dim car As Car = CType(applicationObject, Car)

            Dim fullModel As String
            If HasYear(car.Model) Then
                fullModel = car.Model
            Else
                fullModel = car.Model + GetYear(car.Model)
            End If
            Return fullModel

        End Function


        Private Function GetYear(ByVal carModel As String) As String
            If carModel.Equals("BMW") Then
                Return " 2002"
            Else
                Return " 1999"
            End If
        End Function

        Private Function HasYear(ByVal carModel As String) As Boolean
            Return False
        End Function

        Public Function OnInstantiate(ByVal container As IObjectContainer, ByVal storedObject As Object) As Object Implements IObjectConstructor.OnInstantiate
            Dim model As String = DirectCast(storedObject, String)
            Return New Car(model)
        End Function

        Public Sub OnActivate(ByVal container As IObjectContainer, ByVal applicationObject As Object, ByVal storedObject As Object) Implements IObjectConstructor.OnActivate
        End Sub

        Public Function StoredClass() As System.Type Implements IObjectConstructor.StoredClass
            Return GetType(String)
        End Function

    End Class
End Namespace
