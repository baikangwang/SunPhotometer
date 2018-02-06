using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;

namespace SunPhotometer
{
    internal static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        private static void Main()
        {
            App.Current.Logger.Info("--- SunPhotometer Run ---");
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new fmMap());
        }
    }
}