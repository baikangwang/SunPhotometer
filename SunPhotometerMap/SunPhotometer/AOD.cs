using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Data;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

namespace SunPhotometer
{
    public class AOD
    {
        public Station Station { get; private set; }

        public DateTime DateStamp { get; private set; }

        protected AODConfig AODConfig = AODConfig.Singleton;

        public List<object[]> DataRows { get; private set; }

        public bool HasData { get { return DataRows.Count > 0; } }

        /// <summary>
        /// The columns
        /// TKey: the column name
        /// TValue: the index of column
        /// </summary>
        public List<AODField> Headers { get; private set; }

        public AOD(Station station, DateTime dateStamp)
        {
            this.Station = station;
            this.DateStamp = dateStamp;
            this.Headers = new List<AODField>();
            this.DataRows = new List<object[]>();
            this.Load(this.DateStamp);
        }

        private void Load(DateTime timeStamp)
        {
            string dataDir = Path.Combine(App.Current.AodDir, Station.StationId, timeStamp.ToString("yyyyMM")) ;
            if (!Directory.Exists(dataDir)) return;

            string[] files = Directory.GetFiles(dataDir, "*.ta2", SearchOption.TopDirectoryOnly);
            
            if (files.Length == 0) return;

            bool headerRead = false;
            int lineNo = 0;
            foreach (string file in files)
            {
                if (!File.Exists(file)) continue;
                lineNo = 0;
                using (StreamReader sr = new StreamReader(file, App.Current.Encoding))
                {
                    while (!sr.EndOfStream)
                    {
                        string line = sr.ReadLine();
                        // split the data row with the whitespaces separator, and ignore the empty value
                        var arrValues = Regex.Split(line, @"\s+", RegexOptions.Singleline).Where(u => !string.IsNullOrEmpty(u)).ToArray();
                        // header
                        if (lineNo==0 &&!headerRead)
                        {
                            foreach (var field in AODConfig.Fields)
                            {
                                // the first row is the header line
                                // the last column should be ignore
                                for (int i = 0; i < arrValues.Length - 1; i++)
                                {
                                    string strHeader = arrValues[i];
                                    if (Regex.IsMatch(strHeader, field.RawName, RegexOptions.IgnoreCase | RegexOptions.Compiled))
                                    {
                                        // consider Index as the column index of raw data here
                                        this.Headers.Add(new AODField(strHeader, field.LabelName, i, field.Visible, field.DataType));
                                        break;
                                    }
                                }
                            }

                            headerRead = true;
                            lineNo++;
                            continue;
                        }
                        else if(lineNo == 0)
                        {
                            // skip the header line
                            lineNo++;
                            continue;
                        }

                        object[] values = new object[this.Headers.Count];
                        for (int i = 0; i < this.Headers.Count; i++)
                        {
                            var header = Headers[i];

                            values[i] = Convert.ChangeType(arrValues[header.Index], header.DataType);
                        }
                        DataRows.Add(values);
                        lineNo++;
                    }
                }
            }
        }

        public DataView ToDataTable()
        {
            var table = new DataTable("AOD");
            foreach (var header in this.Headers)
                table.Columns.Add(header.LabelName, header.DataType);

            foreach (var objRow in this.DataRows)
                table.Rows.Add(objRow);

            var dataView = (from row in table.AsEnumerable()
                           orderby row.Field<int>("Year"), row.Field<int>("Month"), row.Field<int>("Day"), row.Field<int>("Hour"), row.Field<int>("Min"), row.Field<float>("Sec") ascending
                           select row).AsDataView();

            return dataView;
        }
    }

    public class AODConfig
    {
        public List<AODField> Fields { get; }

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
                string type = Convert.ToString(objField.datatype);
                return new AODField(raw, label, index, visible, type);
            }).OrderBy(f => f.Index).ToList<AODField>();
        }
    }

    public class AODField
    {
        public string RawName { get; set; }
        public string LabelName { get; set; }

        public bool Visible { get; set; }

        public int Index { get; set; }

        public Type DataType { get; set; }

        public AODField(string raw, string label, int index, bool visible = true, string dataType = "string")
        {
            this.RawName = raw;
            this.LabelName = label;
            this.Index = index;
            this.Visible = visible;
            switch (dataType)
            {
                case "int":
                    this.DataType = typeof(int);
                    break;

                case "float":
                    this.DataType = typeof(float);
                    break;

                case "bool":
                    this.DataType = typeof(bool);
                    break;

                case "datetime":
                    this.DataType = typeof(DateTime);
                    break;

                case "string":
                default:
                    this.DataType = typeof(string);
                    break;
            }
        }

        public AODField(string raw, string label, int index, bool visible, Type dataType)
        {
            this.RawName = raw;
            this.LabelName = label;
            this.Index = index;
            this.Visible = visible;
            this.DataType = dataType;
        }
    }
}