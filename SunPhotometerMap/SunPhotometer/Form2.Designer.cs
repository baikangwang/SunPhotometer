namespace SunPhotometer
{
    partial class Form2
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
            this.bMap = new BMap.NET.WindowsForm.BMapControl();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.ckbDrag = new System.Windows.Forms.CheckBox();
            this.btZoom = new System.Windows.Forms.Button();
            this.label1 = new System.Windows.Forms.Label();
            this.groupBox2 = new System.Windows.Forms.GroupBox();
            this.ckbStyle = new System.Windows.Forms.CheckBox();
            this.groupBox1.SuspendLayout();
            this.groupBox2.SuspendLayout();
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
            this.bMap.Location = new System.Drawing.Point(3, 18);
            this.bMap.MapStyle = BMap.NET.WindowsForm.MapStyle.Normal;
            this.bMap.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
            this.bMap.Mode = BMap.NET.MapMode.Normal;
            this.bMap.Name = "bMap";
            this.bMap.Size = new System.Drawing.Size(737, 679);
            this.bMap.TabIndex = 0;
            this.bMap.Zoom = 6;
            // 
            // groupBox1
            // 
            this.groupBox1.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left)));
            this.groupBox1.Controls.Add(this.ckbStyle);
            this.groupBox1.Controls.Add(this.ckbDrag);
            this.groupBox1.Controls.Add(this.btZoom);
            this.groupBox1.Controls.Add(this.label1);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(128, 701);
            this.groupBox1.TabIndex = 1;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Control";
            // 
            // ckbDrag
            // 
            this.ckbDrag.CheckAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.ckbDrag.Location = new System.Drawing.Point(10, 48);
            this.ckbDrag.Name = "ckbDrag";
            this.ckbDrag.Size = new System.Drawing.Size(104, 24);
            this.ckbDrag.TabIndex = 2;
            this.ckbDrag.Text = "Drag:";
            this.ckbDrag.UseVisualStyleBackColor = true;
            this.ckbDrag.CheckedChanged += new System.EventHandler(this.ckbDrag_CheckedChanged);
            // 
            // btZoom
            // 
            this.btZoom.Location = new System.Drawing.Point(94, 19);
            this.btZoom.Name = "btZoom";
            this.btZoom.Size = new System.Drawing.Size(20, 20);
            this.btZoom.TabIndex = 1;
            this.btZoom.Text = "+";
            this.btZoom.UseVisualStyleBackColor = true;
            this.btZoom.Click += new System.EventHandler(this.btZoom_Click);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(7, 22);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(48, 17);
            this.label1.TabIndex = 0;
            this.label1.Text = "Zoom:";
            // 
            // groupBox2
            // 
            this.groupBox2.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.groupBox2.Controls.Add(this.bMap);
            this.groupBox2.Location = new System.Drawing.Point(146, 13);
            this.groupBox2.Name = "groupBox2";
            this.groupBox2.Size = new System.Drawing.Size(743, 700);
            this.groupBox2.TabIndex = 2;
            this.groupBox2.TabStop = false;
            this.groupBox2.Text = "Stations";
            // 
            // ckbStyle
            // 
            this.ckbStyle.CheckAlign = System.Drawing.ContentAlignment.MiddleRight;
            this.ckbStyle.Location = new System.Drawing.Point(10, 78);
            this.ckbStyle.Name = "ckbStyle";
            this.ckbStyle.Size = new System.Drawing.Size(104, 24);
            this.ckbStyle.TabIndex = 3;
            this.ckbStyle.Text = "MapStyle:";
            this.ckbStyle.UseVisualStyleBackColor = true;
            this.ckbStyle.CheckedChanged += new System.EventHandler(this.ckbStyle_CheckedChanged);
            // 
            // Form2
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(901, 725);
            this.Controls.Add(this.groupBox2);
            this.Controls.Add(this.groupBox1);
            this.Name = "Form2";
            this.Text = "Form2";
            this.Load += new System.EventHandler(this.Form2_Load);
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.groupBox2.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion

        private BMap.NET.WindowsForm.BMapControl bMap;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.CheckBox ckbDrag;
        private System.Windows.Forms.Button btZoom;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.GroupBox groupBox2;
        private System.Windows.Forms.CheckBox ckbStyle;
    }
}