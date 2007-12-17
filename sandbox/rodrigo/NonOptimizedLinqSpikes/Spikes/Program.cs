using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using Db4objects.Db4o;
using System.Reflection;

namespace Spikes {
    static class Program {

        public static void Main(string[] args) {

            PrepareDatabase();

            using (var system = new FarmSystem()) {
                var cow1 = RandomCow(system);
                var cow2 = RandomCow(system);

                // “find all cows whose average milk during august was less than 0.75 of the herd’s average.”

                var august = new DateTime(2007, 8, 1);
                var september = august.AddMonths(1);

                Benchmark("find all cows whose average milk during august was less than 0.75 of the herd’s average.", () => {
                    var herdAverage = system.HerdAverage(august, september);
                    var threshold = .90 * herdAverage;
                    return from c in system.Cows
                                  let CowAverage = system.CowAverage(c, august, september)
                                  where CowAverage < threshold
                                  select new { c.Code, CowAverage };
                }, ()=>null);

                // The example queries:
                // A. For each cow, calculate total milk between date1 and date2

                Time("For each cow, calculate total milk between date1 and date2", () => {
                    var result2 = from c in system.Cows
                                  let CowTotal = system.CowTotal(c, august, september)
                                  select new { c.Code, CowTotal };

                    result2.PrettyPrint();
                });


                // B. Find cows whose total(date1,date2) < X

                Time("Find cows whose total(date1,date2) < X", () => {
                    var result3 = from c in system.Cows
                                  let CowTotal = system.CowTotal(c, august, september)
                                  where CowTotal < 2
                                  select new { c.Code, CowTotal };

                    result3.PrettyPrint();
                });


                // C. Find cows whose total(date1,date2) < 0.75 * average total(date1,date2)

                // D. Days from last give_birth event for cows who are in active milking
                // [these are cows that have a "give_birth" event and no "drying" event
                // after it]

                var result4 = from c in system.Cows
                            let LastGaveBirth = system.DateOfLastEvent(c, "give_birth")
                            let LastDrying = system.DateOfLastEvent(c, "drying")
                            where LastDrying == null || LastGaveBirth > LastDrying 
                            select new { c.Code, (DateTime.Now - LastGaveBirth.Value).TotalDays };

                result4.PrettyPrint();


                // E. How many cows gave birth in each month during the last year
                var lastYear = new DateTime(2007, 1, 1);
                var lastYearEnd = lastYear.AddYears(1);

                var result6 = from e in system.Events
                              where e.Id == "give_birth" && e.Date >= lastYear && e.Date <= lastYearEnd
                              group e.Cow by e.Date.Month into g
                              select new {
                                  Month = g.Key,
                                  Count = g.Distinct().Count()
                              };

                result6.PrettyPrint();

            }
        }

        private static Cow RandomCow(FarmSystem system) {
            var code = "Cow " + new Random().Next(TotalCows);
            return (from c in system.Cows where c.Code == code select c).First();
        }

        delegate IEnumerable<T> QueryBlock<T>();

        private static void Benchmark<T>(string label, QueryBlock<T> linq, QueryBlock<T> optimized) {
            Time("LINQ => " + label, () => linq().PrettyPrint());
            Time("Optimized => " + label, () => optimized().PrettyPrint());
        }

        delegate void CodeBlock();

        private static void Time(string label, CodeBlock code) {
            Console.WriteLine(" ======  {0} ======= ", label);
            DateTime start = DateTime.Now;
            try {
                code();
            } finally {
                Console.WriteLine("'{0}' took {1}ms", label, (DateTime.Now - start).TotalMilliseconds);
            }
        }

        const string BigFile = "bigfile.odb";

        private static void PrepareDatabase() {
            File.Delete(FarmSystem.DefaultFileLocation);

            File.Delete(BigFile);

            if (!File.Exists(BigFile)) {
                Time("Database generation", GenerateBigFile);
            }
            File.Copy(BigFile, FarmSystem.DefaultFileLocation);
        }

        const int TotalCows = 30;

        static readonly string[] EventIds = new[] { "give_birth", "drying", "sick" };

        static void GenerateBigFile() {

            var random = new Random();

            using (var system = new FarmSystem(BigFile)) {

                Cow[] cows = new Cow[TotalCows];
                for (int i = 0; i < TotalCows; ++i) {
                    cows[i] = new Cow("Cow " + i);
                    system.Track(cows[i]);
                }

                int days = 0;
                foreach (DateTime day in (DateTime.Now - TimeSpan.FromDays(365)).DaysUntil(DateTime.Now)) {
                    Console.Write("{0} ", ++days);
                    foreach (Cow cow in cows) {
                        system.Milked(new Milking(cow, day, (float)(2 * random.NextDouble())));
                        system.Milked(new Milking(cow, day.AddHours(8), (float)(2 * random.NextDouble())));
                        system.Milked(new Milking(cow, day.AddHours(8), (float)(2 * random.NextDouble())));
                        system.CustomEvent(cow, day, EventIds[random.Next(EventIds.Length)]);
                    }
                }
                Console.WriteLine();
            }
        }
    }
}

