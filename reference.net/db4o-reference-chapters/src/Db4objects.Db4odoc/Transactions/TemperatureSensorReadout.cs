/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;

namespace Db4objects.Db4odoc.Transactions
{   
    public class TemperatureSensorReadout : SensorReadout
    {
        double _temperature;

        public TemperatureSensorReadout(DateTime time, Car car, string description, double temperature)
            : base(time, car, description)
        {
            _temperature = temperature;
        }
        
        public double Temperature
        {
            get
            {
                return _temperature;
            }
        }
        
        override public string ToString()
        {
            return string.Concat(base.ToString(), " temp: ", _temperature.ToString());
        }
    }
}
