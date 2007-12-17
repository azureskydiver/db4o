using System;
using System.IO;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Db4objects.Db4o;

namespace Spikes {

    class FarmSystem : IDisposable {

        public static readonly string DefaultFileLocation = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), "farm.odb");

        private IObjectContainer _container;

        public FarmSystem() : this(DefaultFileLocation) {
        }

        public FarmSystem(string fname) {
            _container = Db4oFactory.OpenFile(GetConfig(), fname);
        }

        private Db4objects.Db4o.Config.IConfiguration GetConfig() {
            var config = Db4oFactory.NewConfiguration();
            config.ObjectClass(typeof(Cow)).ObjectField("_code").Indexed(true);

            var cowEventClass = config.ObjectClass(typeof(CowEvent));
            cowEventClass.ObjectField("_cow").Indexed(true);
            cowEventClass.ObjectField("_date").Indexed(true);
            return config;
        }

        public IObjectContainer Container {
            get { return _container; }
        }

        public void Track(Cow cow) {
            _container.Set(cow);
        }

        public void Milked(Milking milking) {
            _container.Set(milking);
        }

        public void CustomEvent(Cow cow, DateTime date, string id) {
            _container.Set(new CustomCowEvent(cow, date, id));
        }

        public IQueryable<Cow> Cows {
            get { return Query<Cow>(); }
        }

        public IQueryable<Milking> Milkings {
            get { return Query<Milking>(); }
        }

        public IQueryable<CustomCowEvent> Events {
            get { return Query<CustomCowEvent>(); }
        }

        public IQueryable<T> Query<T>() {
            // THIS ALLOWS LINQ TO BE USED WITH DB4O
            // IN UNOPTIMIZED MODE
            // WE ARE WORKING ON OPTIMIZED LINQ 
            return _container.Query<T>().AsQueryable();
        }

        public void Dispose() {
            _container.Dispose();
        }

        public DateTime? DateOfLastEvent(Cow c, string eventId) {
            return (from e in Events
                    where e.Id == eventId && e.Cow == c
                    orderby e.Date descending
                    select new DateTime?(e.Date)).FirstOrDefault();
        }

        public float CowTotal(Cow cow, DateTime begin, DateTime end) {
            return (from m in MilkingsForPeriod(begin, end)
                    where m.Cow == cow
                    select m.Amount).Sum();
        }

        public float CowAverage(Cow cow, DateTime begin, DateTime end) {
            return (from m in MilkingsForPeriod(begin, end)
                    where m.Cow == cow
                    select m.Amount).Average();
        }

        public float HerdAverage(DateTime begin, DateTime end) {
            return (from m in MilkingsForPeriod(begin, end)
                    select m.Amount).Average();
        }

        private IQueryable<Milking> MilkingsForPeriod(DateTime begin, DateTime end) {
            return from m in Milkings
                   where m.Date >= begin && m.Date <= end
                   select m;
        }
    }


}
