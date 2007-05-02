/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;

namespace Db4objects.Db4odoc.ClientServer
{
    public class PressureSensorReadout : SensorReadout
    {
        double _pressure;
        
        public PressureSensorReadout(DateTime time, Car car, string description, double pressure)
            : base(time, car, description)
        {
            _pressure = pressure;
        }
        
        public double Pressure
        {
            get
            {
                return _pressure;
            }
        }
        
        override public string ToString()
        {
            return string.Format("{0} pressure : {1}", base.ToString(), _pressure);
        }
    }
}
