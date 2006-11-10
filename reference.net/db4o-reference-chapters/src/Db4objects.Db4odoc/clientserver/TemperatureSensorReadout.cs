using System;

namespace Db4objects.Db4odoc.Clientserver
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
