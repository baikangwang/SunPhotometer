using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

namespace SunPhotometer
{
    public class AOD
    {
        public Station Station { get; }

        protected AODConfig AODConfig;

        public List<string[]> DataSet;

        public AOD(Station station)
        {
            this.Station = station;
            this.AODConfig = AODConfig.Singleton;
        }

        public void Load(string timeStamp)
        {
            string dataDir = AODConfig.AODBaseDirectory;
            string file = Path.Combine(dataDir, Station.StationId, string.Format("{0}_{1}.ta2", Station.StationId, timeStamp));
            DataSet = new List<string[]>();
            using (StreamReader sr = new StreamReader(file, AODConfig.Encoding))
            {
                // var lines = Csv.CsvReader.Read(sr, new Csv.CsvOptions() { HeaderMode = Csv.HeaderMode.HeaderPresent, Separator = ' ', TrimData = true }).ToList();
                Dictionary<string, int> headers = new Dictionary<string, int>();
                bool headerRead = false;
                while (!sr.EndOfStream)
                {
                    string line = sr.ReadLine();
                    var arrUnits = Regex.Split(line, @"\s+",RegexOptions.IgnorePatternWhitespace).Where(u=>!string.IsNullOrEmpty(u)).ToArray();
                    // header
                    if (!headerRead)
                    {
                        for(int i = 0; i < arrUnits.Length; i++)
                        {
                            headers.Add(arrUnits[i], i);
                        }
                        headerRead = true;
                        continue;
                    }
                    string[] values = new string[AODConfig.Fields.Count];
                    for (int i=0;i<AODConfig.Fields.Count;i++)
                    {
                        var field = AODConfig.Fields[i];
                        if (headers.ContainsKey(field.RawName))
                        {
                            values[i] = arrUnits[headers[field.RawName]];
                        }
                    }
                    DataSet.Add(values);
                }
            }
        }
    }

    public class AODConfig
    {
        public List<AODField> Fields { get; }

        public string AODBaseDirectory { get; }

        public Encoding Encoding { get; }

        public static AODConfig _singleton = new AODConfig();

        public static AODConfig Singleton
        {
            get
            {
                return _singleton;
            }
        }

        protected AODConfig()
        {
            this.AODBaseDirectory = @"D:\Working\Projects\SunPhotometer\data\AOD\aot\";
            this.Encoding = Encoding.UTF8;

            string configFile = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "aodconfig.json");
            if (!File.Exists(configFile)) throw new Exception("NOT Found AodConfig");
            var content = File.ReadAllText(configFile, Encoding.UTF8);
            var objConf = (dynamic)JObject.Parse(content);
            var arrFields = (JArray)objConf.fields;
            this.Fields = arrFields.Select(e =>
            {
                var objField = (dynamic)((JObject)e);
                string raw = Convert.ToString(objField.raw);
                string label = Convert.ToString(objField.label);
                int index = Convert.ToInt32(objField.index);
                bool visible = Convert.ToBoolean(objField.visible);
                return new AODField(raw, label, index, visible);
            }).OrderBy(f => f.Index).ToList<AODField>();
        }
    }

    public class AODField
    {
        public string RawName { get; set; }
        public string LabelName { get; set; }

        public bool Visible { get; set; }

        public int Index { get; set; }

        public AODField(string raw,string label,int index,bool visible=true)
        {
            this.RawName = raw;
            this.LabelName = label;
            this.Index = index;
            this.Visible = visible;
        }
    }
}
