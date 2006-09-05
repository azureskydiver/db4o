' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System
Imports com.db4o.reflect
Imports com.db4o.reflect.net

Namespace com.db4odoc.f1.reflections
    Public Class LoggingReflector
        Implements Reflector
        Private _arrayHandler As LoggingArray
        Private _parent As Reflector

        Public Sub New()
        End Sub

        Public Overridable Function Array() As ReflectArray Implements Reflector.Array
            If _arrayHandler Is Nothing Then
                _arrayHandler = New LoggingArray(_parent)
            End If
            Return _arrayHandler
        End Function

        Public Overridable Function ConstructorCallsSupported() As Boolean Implements Reflector.ConstructorCallsSupported
            Return True
        End Function

        Public Overridable Function ForClass(ByVal clazz As j4o.lang.Class) As ReflectClass Implements Reflector.ForClass
            Dim rc As ReflectClass = New NetClass(_parent, clazz)
            If rc Is Nothing Then
                Console.WriteLine("ForClass: " + clazz.GetName + " -> ... ")
            Else
                Console.WriteLine("ForClass: " + clazz.GetName + " -> " + (rc.GetName()))
            End If
            Return rc
        End Function

        Public Overridable Function ForName(ByVal className As String) As ReflectClass Implements Reflector.ForName
            Try
                Dim clazz As j4o.lang.Class = j4o.lang.Class.ForName(className)
                Dim rc As ReflectClass = ForClass(clazz)
                If rc Is Nothing Then
                    Console.WriteLine("ForName: " + clazz.GetName + " -> ... ")
                Else
                    Console.WriteLine("ForName: " + clazz.GetName + " -> " + (rc.GetName()))
                End If
                Return rc
            Catch e As j4o.lang.ClassNotFoundException
                Return Nothing
            End Try
        End Function

        Public Overridable Function ForObject(ByVal a_object As Object) As ReflectClass Implements Reflector.ForObject
            If a_object Is Nothing Then
                Return Nothing
            End If
            Dim rc As ReflectClass = _parent.ForClass(j4o.lang.Class.GetClassForObject(a_object))
            If rc Is Nothing Then
                Console.WriteLine("ForObject: " + a_object.ToString + " -> ... ")
            Else
                Console.WriteLine("ForObject: " + a_object.ToString + " -> " + (rc.GetName()))
            End If
            Return rc
        End Function

        Public Overridable Function IsCollection(ByVal claxx As ReflectClass) As Boolean Implements Reflector.IsCollection
            Return False
        End Function

        Public Overridable Sub SetParent(ByVal reflector As Reflector) Implements Reflector.SetParent
            _parent = reflector
        End Sub

        Public Overridable Function DeepClone(ByVal context As Object) As Object Implements Reflector.DeepClone
            Return New LoggingReflector()
        End Function
    End Class
End Namespace
