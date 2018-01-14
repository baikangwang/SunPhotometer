using System;
using Newtonsoft.Json.Linq;
using System.Collections.Generic;
using System.Data;
using System.IO;
using System.Linq;
using System.Text;

namespace SunPhotometer
{
    public class App
    {
        public string DataBaseDir { get; }

        public string AodDir { get; }
        public Stations Stations { get; }

        public Encoding Encoding { get { return Encoding.UTF8; } }

        private static App _app = new App();

        public static App Current { get { return _app; } }
        protected App()
        {
            string configFile = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "app.json");
            if (!File.Exists(configFile)) throw new Exception("NOT Found App config");
            var content = File.ReadAllText(configFile, Encoding.UTF8);
            var objConf = (dynamic)JObject.Parse(content);
            try
            {
                this.DataBaseDir = Convert.ToString(objConf.data_dir);
            }
            catch (Exception)
            {
                this.DataBaseDir = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "data","AOD");
            }
            this.AodDir = Path.Combine(this.DataBaseDir, "aod");
            string stations_file = Path.Combine(this.DataBaseDir, "stations_aod.csv");

            this.Stations = Stations.Read(stations_file);
        }
    }
}
