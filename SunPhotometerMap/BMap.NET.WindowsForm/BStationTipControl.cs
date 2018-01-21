using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Drawing.Drawing2D;
using BMap.NET.WindowsForm.BMapElements;
using System.Reflection;

namespace BMap.NET.WindowsForm
{
    /// <summary>
    /// 标记点信息显示控件
    /// </summary>
    partial class BStationTipControl : UserControl
    {
        private BMarker _marker;

        private event EventHandler _stationDateChanged;
        public event EventHandler StationDateChanged
        {
            add { _stationDateChanged += value; }
            remove { _stationDateChanged -= value; }
        }

        private DateTime _date = DateTime.Now;
        public DateTime Date { get { return _date; }set { _date = value; } }

        /// <summary>
        /// 与之对应的标记点
        /// </summary>
        public BMarker Marker
        {
            get
            {
                return _marker;
            }
            set
            {
                _marker = value;
                lblName.Text = _marker.Name;
            }
        }

        /// <summary>
        /// 构造方法
        /// </summary>
        public BStationTipControl()
        {
            InitializeComponent();
            this.dtpDate.Value = DateTime.Now;
            this.InitDataGridAod();
            SetStyle(ControlStyles.AllPaintingInWmPaint | ControlStyles.OptimizedDoubleBuffer, true);
            UpdateStyles();
        }

        #region 事件处理

        /// <summary>
        /// Load
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void BMarkerTipControl_Load(object sender, EventArgs e)
        {
            // this.dgAod.DataSource = this.AODData;
        }

        /// <summary>
        /// 关闭
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void picClose_Click(object sender, EventArgs e)
        {
            Visible = false;
        }

        /// <summary>
        /// 重绘
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void BStationTipControl_Paint(object sender, PaintEventArgs e)
        {
            //e.Graphics.SmoothingMode = SmoothingMode.HighQuality;
            ////边框
            //GraphicsPath gp = new GraphicsPath();
            //gp.AddLine(new Point(0, 0), new Point(Width - 1, 0));
            //gp.AddLine(new Point(Width - 1, 0), new Point(Width - 1, Height - 40 - 1));
            //gp.AddLine(new Point(Width / 3 + 20 - 1, Height - 40 - 1), new Point(Width - 1, Height - 40 - 1));
            //gp.AddLine(new Point(Width / 3 + 20 - 1, Height - 40 - 1), new Point(Width / 3 - 40, Height - 1));
            //gp.AddLine(new Point(Width / 3 - 40, Height - 1), new Point(Width / 3 - 20 + 1, Height - 40 - 1));
            //gp.AddLine(new Point(Width / 3 - 20 + 1, Height - 40 - 1), new Point(0, Height - 40 - 1));
            //gp.AddLine(new Point(0, 0), new Point(0, Height - 40 - 1));
            //e.Graphics.DrawPath(Pens.Gray, gp);
        }

        /// <summary>
        /// 鼠标移动
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void BStationTipControl_MouseMove(object sender, MouseEventArgs e)
        {
            if (new Rectangle(0, 130 - 15, Width, 26).Contains(e.Location))
            {
                Cursor = Cursors.Hand;
            }
            else
            {
                Cursor = Cursors.Arrow;
            }
        }

        /// <summary>
        /// 点击选项卡
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void BMarkerTipControl_Click(object sender, EventArgs e)
        {
            // Invalidate();
        }

        #endregion 事件处理

        #region private methods

        private void InitDataGridAod()
        {
            this.dgAod.ScrollBars = ScrollBars.Both;
            this.dgAod.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill;
            this.dgAod.VirtualMode = true;
            dgAod.RowHeadersWidthSizeMode = DataGridViewRowHeadersWidthSizeMode.DisableResizing; //or even better .DisableResizing. Most time consumption enum is DataGridViewRowHeadersWidthSizeMode.AutoSizeToAllHeaders
            // set it to false if not needed
            dgAod.RowHeadersVisible = false;
            // Double buffering can make DGV slow in remote desktop
            // https://10tec.com/articles/why-datagridview-slow.aspx (Slow DataGridView rendering and scrolling)
            Type dgvType = this.dgAod.GetType();
            PropertyInfo pi = dgvType.GetProperty("DoubleBuffered",
              BindingFlags.Instance | BindingFlags.NonPublic);
            pi.SetValue(dgAod, true, null);
        }

        #endregion private methods

        #region public methods

        public void SetAodData(object aodData)
        {
            if (aodData != null)
            {
                this.dgAod.DataSource = aodData;
                label3.Text = string.Format("{0}: {1} 行", this._date.ToString("yyyy - MM"), this.dgAod.Rows.Count);
            }
            else
            {
                this.dgAod.DataSource = null;
                label3.Text = string.Format("{0}: {1}", this._date.ToString("yyyy - MM"), "没有数据");
            }
        }

        #endregion public methods

        private void dtpDate_ValueChanged(object sender, EventArgs e)
        {
            this._date = this.dtpDate.Value;
            if (_stationDateChanged != null)
                this._stationDateChanged(sender, e);
        }

        private void BStationTipControl_VisibleChanged(object sender, EventArgs e)
        {
            if (!this.Visible)
                this._date = DateTime.Now;
        }
    }
}