using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace SunPhotometer
{
    public class Stations:List<Station>
    {
        public static Stations Read(string file)
        {
            var stations = new Stations();
            using (StreamReader sr=new StreamReader(file, Encoding.GetEncoding("GB2312")))
            {
                foreach (var line in Csv.CsvReader.Read(sr, new Csv.CsvOptions()
                {
                    HeaderMode = Csv.HeaderMode.HeaderPresent
                }))
                {
                    stations.Add(new Station(line["Stid"], line["Lat"], line["Lon"],
                        line["Alt"], line["Stname"], line["calibr"]));
                }
            }

            return stations;

        }
    }

    public class Station
    {
        public string StationId { get; set; }
        public string Name { get; set; }

        public float Latitude { get; set; }

        public float Lontitude { get; set; }

        public float Altitude { get; set; }

        public string Calibr { get; set; }

        public Station(string stId,string lat,string lon,string alt,string stname,string calibr)
        {
            this.StationId = stId;
            this.Latitude = float.Parse(lat);
            this.Lontitude = float.Parse(lon);
            this.Altitude = float.Parse(alt);
            this.Name = stname;
            this.Calibr = calibr;
        }
    }
}
