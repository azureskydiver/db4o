' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports com.db4o
Imports com.db4o.config

Namespace com.db4odoc.f1.evaluations

    Public Class CarTranslator
        Implements ObjectConstructor

        Public Function OnStore(ByVal container As ObjectContainer, ByVal applicationObject As Object) As Object Implements ObjectConstructor.OnStore
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

        Public Function OnInstantiate(ByVal container As ObjectContainer, ByVal storedObject As Object) As Object Implements ObjectConstructor.OnInstantiate
            Dim model As String = DirectCast(storedObject, String)
            Return New Car(model)
        End Function

        Public Sub OnActivate(ByVal container As ObjectContainer, ByVal applicationObject As Object, ByVal storedObject As Object) Implements ObjectConstructor.OnActivate
        End Sub

        Public Function StoredClass() As j4o.lang.Class Implements ObjectConstructor.StoredClass
            Return j4o.lang.[Class].GetClassForType(GetType(String))
        End Function

    End Class
End Namespace
