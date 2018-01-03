using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using BMap.NET;
using BMap.NET.WindowsForm;

namespace SunPhotometer
{
    public partial class Form2 : Form
    {
        public Form2()
        {
            InitializeComponent();
            BMapConfiguration.SK = "GMqDruxjOz2PkfMlNKmDlscc";
            BMapConfiguration.LoadMapMode = LoadMapMode.Cache;
            BMapConfiguration.VerificationMode = VerificationMode.IPWhiteList;
            BMapConfiguration.MapCachePath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "cache");
            this.bMap.Zoom = 5;
            this.bMap.EnableCity = false;
            this.bMap.EnableScaleMap = false;
            this.bMap.EnableDragMap = false;
            this.bMap.EnableMapInfo = false;
            this.bMap.EnableToolsBar = false;
            this.bMap.MapStyle = MapStyle.Station;
        }

        private void Form2_Load(object sender, EventArgs e)
        {
            // defaults enabledragMap
            this.ckbDrag.Checked = false;
            this.ckbStyle.Checked = false;

            string csvfile = @"D:\Working\Projects\SunPhotometer\data\AOD\stations_aod.csv";

            foreach(var station in Stations.Read(csvfile))
            {
                this.bMap.AddMarks(new BMap.NET.WindowsForm.LatLngPoint(station.Lontitude, station.Latitude),
                    station.Name,station.Name);
            }
        }

        private void btZoom_Click(object sender, EventArgs e)
        {
            if (btZoom.Text == "+")
            {
                btZoom.Text = "-";
                this.bMap.Zoom = 6;
            }
            else
            {
                btZoom.Text = "+";
                this.bMap.Zoom = 5;
            }
        }

        private void ckbDrag_CheckedChanged(object sender, EventArgs e)
        {
            this.bMap.EnableDragMap = this.ckbDrag.Checked;
        }

        private void ckbStyle_CheckedChanged(object sender, EventArgs e)
        {
            this.bMap.MapStyle = ckbStyle.Checked ? MapStyle.Station : MapStyle.Normal;
        }
    }
}
