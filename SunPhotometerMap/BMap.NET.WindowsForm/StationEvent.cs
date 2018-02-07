using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace BMap.NET.WindowsForm
{
    public class StationEventAgrs : EventArgs
    {
        /// <summary>
        /// The Id of the station
        /// </summary>
        public string Station { get; set; }

        public DateTime Date { get; set; }
    }

    public delegate object DataSourceEvent(object sender, StationEventAgrs e);
}
