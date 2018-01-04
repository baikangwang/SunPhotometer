using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace SunPhotometer
{
    public partial class Form3 : Form
    {
        public Form3()
        {
            InitializeComponent();
        }

        private void Form3_Load(object sender, EventArgs e)
        {
            string csvfile = @"D:\Working\Projects\SunPhotometer\data\AOD\stations_aod.csv";

            var stations = Stations.Read(csvfile);
            var station = stations.FirstOrDefault(s => s.StationId == "54662");

            var aod = new AOD(station);
            aod.Load("200901");
            this.listBox1.Items.Clear();
            foreach(var values in aod.DataSet)
            {
                this.listBox1.Items.Add(string.Join(",", values));
            }
        }
    }
}
