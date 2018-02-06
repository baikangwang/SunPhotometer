using System;
using Newtonsoft.Json.Linq;
using System.Collections.Generic;
using System.Data;
using System.IO;
using System.Linq;
using System.Text;
using NLog.Config;
using NLog.Targets;
using NLog;

namespace SunPhotometer
{
    public class App
    {
        protected string DataBaseDir { get; }

        public string SK { get; }
        public string AodDir { get; }
        public Stations Stations { get; }

        public Encoding Encoding { get { return Encoding.UTF8; } }

        public Logger Logger { get; }

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
                this.DataBaseDir = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "data", "AOD");
            }
            this.AodDir = Path.Combine(this.DataBaseDir, "aod");

            string stations_file = Path.Combine(this.DataBaseDir, "stations_aod.csv");

            this.Stations = Stations.Read(stations_file);

            // initial logs
            this.Logger = this.ConfigLogger(Convert.ToString(objConf.log_dir));

            this.SK = Convert.ToString(objConf.SK);
        }

        private Logger ConfigLogger(string log_dir)
        {
            // Step 1. Create configuration object 
            var config = new LoggingConfiguration();

            // Step 2. Create targets and add them to the configuration 
            // var consoleTarget = new ColoredConsoleTarget();
            // config.AddTarget("console", consoleTarget);

            var fileTarget = new FileTarget();
            config.AddTarget("file", fileTarget);

            // Step 3. Set target properties 
            // consoleTarget.Layout = @"${date:format=HH\:mm\:ss} ${logger} ${message}";
            fileTarget.FileName = Path.Combine(log_dir, "sunphotometer_map.log");
            fileTarget.Layout = @"${date:format=yyyy\-MM\-dd HH\:mm\:ss} ${message}";

            // Step 4. Define rules
            // var rule1 = new LoggingRule("*", LogLevel.Debug, consoleTarget);
            // config.LoggingRules.Add(rule1);

            var rule2 = new LoggingRule("*", LogLevel.Debug, fileTarget);
            config.LoggingRules.Add(rule2);

            // Step 5. Activate the configuration
            LogManager.Configuration = config;

            return LogManager.GetLogger("sunphotometer_map");
        }
    }
}
