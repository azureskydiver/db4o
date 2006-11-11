' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System
Imports Db4objects.Db4o.Reflect
Imports Db4objects.Db4o.Reflect.Net

Namespace Db4objects.Db4odoc.Reflections
    Public Class LoggingArray
        Implements IReflectArray
        Private ReadOnly _reflector As IReflector

        Public Sub New(ByVal pReflector As IReflector)
            _reflector = pReflector
        End Sub

        Public Function Dimensions(ByVal arr As Object) As Integer() Implements IReflectArray.Dimensions
            Return New Integer() {GetLength(arr)}
        End Function

        Function [Get](ByVal onArray As Object, ByVal index As Integer) As Object Implements IReflectArray.Get
            Return (CType(onArray, Object()))(index)
        End Function

        Function Flatten(ByVal a_shaped As Object, ByVal a_dimensions() As Integer, ByVal a_currentDimension As Integer, ByVal a_flat() As Object, ByVal a_flatElement As Integer) As Integer Implements IReflectArray.Flatten
            Dim shaped() As Object = CType(a_shaped, Object())
            System.Array.Copy(shaped, 0, a_flat, 0, shaped.Length)
            Return shaped.Length
        End Function

        Function GetComponentType(ByVal a_class As IReflectClass) As IReflectClass Implements IReflectArray.GetComponentType
            While a_class.IsArray()
                a_class = a_class.GetComponentType()
            End While
            Return a_class
        End Function

        Public Function GetLength(ByVal array As Object) As Integer Implements IReflectArray.GetLength
            Return (CType(array, Object())).Length
        End Function

        Function IsNDimensional(ByVal a_class As IReflectClass) As Boolean Implements IReflectArray.IsNDimensional
            Return False
        End Function

        Function GetNetType(ByVal a_class As IReflectClass) As Type
            Return (CType(a_class, NetClass)).GetNetType()
        End Function

        Function NewInstance(ByVal componentType As IReflectClass, ByVal dimensions As Integer()) As Object Implements IReflectArray.NewInstance
            Return NewInstance(componentType, dimensions(0))
        End Function


        Function NewInstance(ByVal componentType As IReflectClass, ByVal length As Integer) As Object Implements IReflectArray.NewInstance
            Return System.Array.CreateInstance(GetNetType(componentType), length)
        End Function


        Sub [Set](ByVal onArray As Object, ByVal index As Integer, ByVal element As Object) Implements IReflectArray.Set
            Dim shaped() As Object = CType(onArray, Object())
            shaped.SetValue(element, index)
            Return
        End Sub

        Function Shape(ByVal a_flat() As Object, ByVal a_flatElement As Integer, ByVal a_shaped As Object, ByVal a_dimensions() As Integer, ByVal a_currentDimension As Integer) As Integer Implements IReflectArray.Shape
            Dim shaped() As Object = CType(a_shaped, Object())
            System.Array.Copy(a_flat, 0, shaped, 0, a_flat.Length)
            Return a_flat.Length
        End Function
    End Class
End Namespace