namespace com.db4o.f1.chapter5
{
    using System;
    
    public class Car
    {
        string _model;
        Pilot _pilot;
        SensorReadout _history;
        
        public Car(string model)
        {
            _model = model;
            _pilot = null;
            _history = null;
        }
        
        public Pilot Pilot
        {
            get
            {
                return _pilot;
            }
            
            set
            {
                _pilot = value;
            }
        }
        
        public string Model
        {
            get
            {
                return _model;
            }
        }
        
        public SensorReadout GetHistory()
        {
            return _history;
        }
        
        public void Snapshot()
        {        
            AppendToHistory(new TemperatureSensorReadout(
                DateTime.Now, this, "oil", PollOilTemperature()));
            AppendToHistory(new TemperatureSensorReadout(
                DateTime.Now, this, "water", PollWaterTemperature()));
            AppendToHistory(new PressureSensorReadout(
                DateTime.Now, this, "oil", PollOilPressure()));
        }

        protected double PollOilTemperature()
        {
            return 0.1*CountHistoryElements();
        }
        
        protected double PollWaterTemperature()
        {
            return 0.2*CountHistoryElements();
        }
        
        protected double PollOilPressure()
        {
            return 0.3*CountHistoryElements();
        }
        
        override public string ToString()
        {
            return _model + "[" + _pilot + "]/" + CountHistoryElements();
        }
        
        private int CountHistoryElements()
        {
            return (_history == null ? 0 : _history.CountElements());
        }
        
        private void AppendToHistory(SensorReadout readout)
        {
            if (_history == null)
            {
                _history = readout;
            }
            else
            {
                _history.Append(readout);
            }
        }
    }
}
