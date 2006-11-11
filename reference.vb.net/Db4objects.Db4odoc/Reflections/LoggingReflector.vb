' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System
Imports Db4objects.Db4o.Reflect
Imports Db4objects.Db4o.Reflect.Net

Namespace Db4objects.Db4odoc.Reflections
    Public Class LoggingReflector
        Implements IReflector
        Private _arrayHandler As LoggingArray
        Private _parent As IReflector

        Public Sub New()
        End Sub

        Public Overridable Function Array() As IReflectArray Implements IReflector.Array
            If _arrayHandler Is Nothing Then
                _arrayHandler = New LoggingArray(_parent)
            End If
            Return _arrayHandler
        End Function

        Public Overridable Function ConstructorCallsSupported() As Boolean Implements IReflector.ConstructorCallsSupported
            Return True
        End Function

        Public Overridable Function ForClass(ByVal clazz As System.Type) As IReflectClass Implements IReflector.ForClass
            Dim rc As IReflectClass = New NetClass(_parent, clazz)
            If rc Is Nothing Then
                Console.WriteLine("ForClass: " + clazz.FullName + " -> ... ")
            Else
                Console.WriteLine("ForClass: " + clazz.FullName + " -> " + (rc.GetName()))
            End If
            Return rc
        End Function

        Public Overridable Function ForName(ByVal className As String) As IReflectClass Implements IReflector.ForName
            Dim clazz As System.Type = Nothing
            Try
                clazz = Sharpen.Lang.TypeReference.FromString(className).Resolve()
    
            Catch e As System.TypeLoadException
                Return Nothing
            End Try

            Dim rc As IReflectClass = ForClass(clazz)
            If rc Is Nothing Then
                Console.WriteLine("ForName: " + clazz.FullName + " -> ... ")
            Else
                Console.WriteLine("ForName: " + clazz.FullName + " -> " + (rc.GetName()))
            End If
            Return rc

        End Function

        Public Overridable Function ForObject(ByVal a_object As Object) As IReflectClass Implements IReflector.ForObject
            If a_object Is Nothing Then
                Return Nothing
            End If
            Dim rc As IReflectClass = _parent.ForClass(a_object.GetType())
            If rc Is Nothing Then
                Console.WriteLine("ForObject: " + a_object.ToString + " -> ... ")
            Else
                Console.WriteLine("ForObject: " + a_object.ToString + " -> " + (rc.GetName()))
            End If
            Return rc
        End Function

        Public Overridable Function IsCollection(ByVal candidate As IReflectClass) As Boolean Implements IReflector.IsCollection
            Dim result As Boolean = False
            If (candidate.IsArray()) Then
                result = False
            End If
            If (GetType(System.Collections.ICollection).IsAssignableFrom((CType(candidate, NetClass).GetNetType()))) Then
                result = True
            End If
            Console.WriteLine("Type " + candidate.GetName() + " isCollection: " + result.ToString())
            Return result
        End Function

        Public Overridable Sub SetParent(ByVal reflector As IReflector) Implements IReflector.SetParent
            _parent = reflector
        End Sub

        Public Overridable Function DeepClone(ByVal context As Object) As Object Implements IReflector.DeepClone
            Return New LoggingReflector()
        End Function
    End Class
End Namespace
