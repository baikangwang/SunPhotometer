using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Windows.Forms;

namespace SunPhotometer
{
    public partial class Form3 : Form
    {
        public Form3()
        {
            InitializeComponent();
            this.dgv.ScrollBars = ScrollBars.Both;
            this.dgv.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill;
            this.dgv.VirtualMode = true;
            dgv.RowHeadersWidthSizeMode = DataGridViewRowHeadersWidthSizeMode.DisableResizing; //or even better .DisableResizing. Most time consumption enum is DataGridViewRowHeadersWidthSizeMode.AutoSizeToAllHeaders
            // set it to false if not needed
            dgv.RowHeadersVisible = false;
            // Double buffering can make DGV slow in remote desktop
            // https://10tec.com/articles/why-datagridview-slow.aspx (Slow DataGridView rendering and scrolling)
            Type dgvType = this.dgv.GetType();
            PropertyInfo pi = dgvType.GetProperty("DoubleBuffered",
              BindingFlags.Instance | BindingFlags.NonPublic);
            pi.SetValue(dgv, true, null);
        }

        private void Form3_Load(object sender, EventArgs e)
        {
            string csvfile = @"D:\Working\Projects\SunPhotometer\data\AOD\stations_aod.csv";

            var stations = Stations.Read(csvfile);
            var station = stations.FirstOrDefault(s => s.StationId == "54662");

            var aod = new AOD(station, DateTime.Now);

            #region solution 1

            //this.dgv.Columns.Clear();
            // this.dgv.Rows.Clear();
            //foreach (var header in aod.Headers)
            //{
            //    var column = new DataGridViewTextBoxColumn();
            //    column.ValueType = header.DataType;
            //    column.Name = header.LabelName;
            //    column.Visible = header.Visible;
            //    column.HeaderText = header.LabelName;
            //    column.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
            //    column.ReadOnly = true;
            //    this.dgv.Columns.Add(column);
            //}
            //foreach (var row in aod.DataRows)
            //    this.dgv.Rows.Add(row);

            #endregion solution 1

            #region solution 2

            //this.dgv.ColumnCount = aod.Headers.Count;
            //for (int i = 0; i < aod.Headers.Count; i++)
            //{
            //    this.dgv.Columns[i].Name = aod.Headers[i].LabelName;
            //}

            //List<DataGridViewRow> rows = new List<DataGridViewRow>(aod.DataRows.Count);

            //foreach (var objRow in aod.DataRows)
            //{
            //    DataGridViewRow row = new DataGridViewRow();
            //    row.CreateCells(this.dgv);
            //    for (int i = 0; i < objRow.Length; i++)
            //    {
            //        row.Cells[i].Value = objRow[i];
            //    }
            //    rows.Add(row);
            //}
            //this.dgv.Rows.AddRange(rows.ToArray());

            #endregion solution 2

            #region solution 3

            //((ISupportInitialize)dgv).BeginInit();
            this.dgv.DataSource = aod.ToDataTable();
            //((ISupportInitialize)dgv).EndInit();

            #endregion solution 3
        }
    }
}