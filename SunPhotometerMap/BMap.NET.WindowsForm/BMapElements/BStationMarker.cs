using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace BMap.NET.WindowsForm.BMapElements
{
    internal class BStationMarker : BMarker
    {
        /// <summary>
        /// The Id of the station
        /// </summary>
        public string Station { get; set; }

        public override void Draw(Graphics g, LatLngPoint center, int zoom, Size screen_size)
        {
            Point p = MapHelper.GetScreenLocationByLatLng(Location, center, zoom, screen_size);  //屏幕坐标
            Bitmap b = Properties.BMap.icon_station;
            g.DrawImage(b, new Rectangle(p.X - b.Width / 2, p.Y - b.Height, b.Width, b.Height));
            using (Font f = new Font("微软雅黑", 9))
            {
                Size s = TextRenderer.MeasureText(Name, f);  //字体占用像素
                g.FillRectangle(Brushes.Wheat, new Rectangle(new Point(p.X + b.Width / 2 + 5, p.Y - b.Height), new Size(s.Width + 6, s.Height + 6)));
                g.DrawRectangle(Pens.Gray, new Rectangle(new Point(p.X + b.Width / 2 + 5, p.Y - b.Height), new Size(s.Width + 6, s.Height + 6)));
                g.DrawString(Name, f, Brushes.Black, new PointF(p.X + b.Width / 2 + 5 + 3, p.Y - b.Height + 3));
            }
            this.Rect = new Rectangle(p.X - b.Width / 2, p.Y - b.Height, b.Width, b.Height);
        }
    }
}
