' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System.Threading

Namespace Db4objects.Db4odoc.Persist
    Public Class Change2
        Dim _thread As Thread
        Dim _running As Boolean
        Dim _car As Car

        Public Sub Init(ByVal car As Car)
            _car = car
            _running = True
            Dim threadStart As ThreadStart = New ThreadStart(AddressOf Run)
            _thread = New Thread(threadStart)
            _thread.Start()
        End Sub

        Private Sub Run()
            While (_running)
                _car.Temperature = GetConditionerTemperature()
                Thread.Sleep(10)
            End While
        End Sub

        Private Function GetConditionerTemperature() As Integer
            Return 17
        End Function

        Public Sub Kill()
            _running = False
        End Sub

    End Class
End Namespace

