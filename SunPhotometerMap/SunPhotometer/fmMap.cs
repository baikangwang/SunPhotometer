using BMap.NET;
using BMap.NET.WindowsForm;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace SunPhotometer
{
    public partial class fmMap : Form
    {
        public fmMap()
        {
            InitializeComponent();
            BMapConfiguration.SK = App.Current.SK;
            BMapConfiguration.LoadMapMode = LoadMapMode.CacheServer;
            BMapConfiguration.VerificationMode = VerificationMode.IPWhiteList;
            BMapConfiguration.MapCachePath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "cache");
            this.bMap.Zoom = 5;
            this.bMap.EnableCity = false;
            this.bMap.EnableScaleMap = true;
            this.bMap.MaxZoom = 7;
            this.bMap.MinZoom = 5;
            this.bMap.EnableDragMap = true;
            this.bMap.EnableMapInfo = false;
            this.bMap.EnableToolsBar = false;
            this.bMap.MapStage = MapStage.Station;
            this.bMap.MapStyle = MapStyle.hardedge;
            this.bMap.StationVisibleChanged += BMap_StationVisibleChanged;
        }

        private object BMap_StationVisibleChanged(object sender, StationEventAgrs e)
        {
            var stations = App.Current.Stations;
            var station = stations[e.Station];

            var aod = new AOD(station, e.Date);
            var data = aod.HasData ? aod.ToDataTable() : null;
            if (data == null)
                App.Current.Logger.Warn("{0}({1}) hasn't AOD data in {2:yyyy/MM}", station.Name, station.StationId, e.Date);
            else
                App.Current.Logger.Info("{0}({1}) has AOD data in {2:yyyy/MM}", station.Name, station.StationId, e.Date);

            return data;
        }

        private void bMap_Load(object sender, EventArgs e)
        {
            foreach (var station in App.Current.Stations.Values)
            {
                this.bMap.AddMarks(new BMap.NET.WindowsForm.LatLngPoint(station.Lontitude, station.Latitude),
                    station.Name, station.Name, station.StationId);
                // App.Current.Logger.Info("{0}({1}) loaded [lon:{2},lat:{3}]", station.Name, station.StationId, station.Lontitude,station.Latitude);
            }
        }
    }
}
