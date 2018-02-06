namespace SunPhotometer
{
    partial class fmMap
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(fmMap));
            this.bMap = new BMap.NET.WindowsForm.BMapControl();
            this.SuspendLayout();
            // 
            // bMap
            // 
            this.bMap.BDirectionBoard = null;
            this.bMap.BPlaceBox = null;
            this.bMap.BPlacesBoard = null;
            this.bMap.Dock = System.Windows.Forms.DockStyle.Fill;
            this.bMap.EnableCity = false;
            this.bMap.EnableDragMap = false;
            this.bMap.EnableMapInfo = false;
            this.bMap.EnableScaleMap = false;
            this.bMap.EnableToolsBar = false;
            this.bMap.LoadMode = BMap.NET.LoadMapMode.CacheServer;
            this.bMap.Location = new System.Drawing.Point(0, 0);
            this.bMap.MapStage = BMap.NET.WindowsForm.MapStage.Normal;
            this.bMap.MapStyle = BMap.NET.MapStyle.normal;
            this.bMap.MaxZoom = 19;
            this.bMap.MinZoom = 3;
            this.bMap.Mode = BMap.NET.MapMode.Normal;
            this.bMap.Name = "bMap";
            this.bMap.Size = new System.Drawing.Size(975, 685);
            this.bMap.TabIndex = 1;
            this.bMap.Zoom = 6;
            this.bMap.Load += new System.EventHandler(this.bMap_Load);
            // 
            // fmMap
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(975, 685);
            this.Controls.Add(this.bMap);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "fmMap";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "Sunphotometer Map";
            this.ResumeLayout(false);

        }

        #endregion

        private BMap.NET.WindowsForm.BMapControl bMap;
    }
}