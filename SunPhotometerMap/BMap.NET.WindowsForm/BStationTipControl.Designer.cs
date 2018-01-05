﻿namespace BMap.NET.WindowsForm
{
    partial class BStationTipControl
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

        #region Component Designer generated code

        /// <summary> 
        /// Required method for Designer support - do not modify 
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(BStationTipControl));
            this.label1 = new System.Windows.Forms.Label();
            this.lblName = new System.Windows.Forms.Label();
            this.picClose = new System.Windows.Forms.PictureBox();
            this.dgAod = new System.Windows.Forms.DataGridView();
            ((System.ComponentModel.ISupportInitialize)(this.picClose)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.dgAod)).BeginInit();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.label1.BackColor = System.Drawing.Color.LightGray;
            this.label1.Location = new System.Drawing.Point(1, 35);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(877, 1);
            this.label1.TabIndex = 2;
            this.label1.Text = "label1";
            // 
            // lblName
            // 
            this.lblName.AutoSize = true;
            this.lblName.Font = new System.Drawing.Font("Microsoft YaHei", 10.5F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblName.Location = new System.Drawing.Point(8, 7);
            this.lblName.Name = "lblName";
            this.lblName.Size = new System.Drawing.Size(79, 19);
            this.lblName.TabIndex = 3;
            this.lblName.Text = "标记点名称";
            // 
            // picClose
            // 
            this.picClose.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.picClose.Cursor = System.Windows.Forms.Cursors.Hand;
            this.picClose.Image = ((System.Drawing.Image)(resources.GetObject("picClose.Image")));
            this.picClose.Location = new System.Drawing.Point(853, 10);
            this.picClose.Name = "picClose";
            this.picClose.Size = new System.Drawing.Size(13, 15);
            this.picClose.TabIndex = 4;
            this.picClose.TabStop = false;
            this.picClose.Click += new System.EventHandler(this.picClose_Click);
            // 
            // dgAod
            // 
            this.dgAod.AllowUserToAddRows = false;
            this.dgAod.AllowUserToDeleteRows = false;
            this.dgAod.AllowUserToOrderColumns = true;
            this.dgAod.AllowUserToResizeRows = false;
            this.dgAod.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.dgAod.BackgroundColor = System.Drawing.Color.White;
            this.dgAod.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgAod.Location = new System.Drawing.Point(12, 48);
            this.dgAod.Name = "dgAod";
            this.dgAod.Size = new System.Drawing.Size(854, 476);
            this.dgAod.TabIndex = 5;
            // 
            // BStationTipControl
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.Color.White;
            this.Controls.Add(this.dgAod);
            this.Controls.Add(this.picClose);
            this.Controls.Add(this.lblName);
            this.Controls.Add(this.label1);
            this.Name = "BStationTipControl";
            this.Size = new System.Drawing.Size(879, 541);
            this.Load += new System.EventHandler(this.BMarkerTipControl_Load);
            this.Click += new System.EventHandler(this.BMarkerTipControl_Click);
            this.Paint += new System.Windows.Forms.PaintEventHandler(this.BStationTipControl_Paint);
            this.MouseMove += new System.Windows.Forms.MouseEventHandler(this.BStationTipControl_MouseMove);
            ((System.ComponentModel.ISupportInitialize)(this.picClose)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.dgAod)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label lblName;
        private System.Windows.Forms.PictureBox picClose;
        private System.Windows.Forms.DataGridView dgAod;
    }
}
