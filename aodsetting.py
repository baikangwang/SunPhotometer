"""
class AodSetting
"""

import os
import os.path as path
from stations import Station, Stations


class AodSetting:
    def __init__(self, data_dir, para_dir, ftp_ip, ftp_user, ftp_psw, ftp_root="/sunphotometer"):
        """Aod process setting.

        Parameters
        ----------
        self:
        data_dir: the base data dir
        para_dir: the base parameter data dir
        ftp_ip: the ftp Ip
        ftp_user: the ftp user
        ftp_psw: the ftp password
        ftp_root: the default root ftp dir
        Returns
        -------

        """
        self.data_dir = data_dir
        self.para_dir = para_dir
        self.ftp_ip = ftp_ip
        self.ftp_user = ftp_user
        self.ftp_psw = ftp_psw
        self.ftp_root = ftp_root
        self.dd_dir = path.join(self.data_dir, "download")
        self.k7_dir = path.join(self.data_dir, "k7")
        self.merge_dir = path.join(self.data_dir, "merge")
        self.ascii_dir = path.join(self.data_dir, "ascii")
        self.aot_dir = path.join(self.data_dir, "aod")
        self.stations_file = path.join(self.data_dir, "stations_aod.csv")
        self.p_aot_dir = path.join(self.para_dir, "Aot")
        self.p_cal_dir = path.join(self.para_dir, "CalFile")
        self.p_aot_exe=path.join(self.p_aot_dir,"ESPESOR.EXE")
        self.p_aot_input=path.join(self.p_aot_dir,"inputpar.dat")
        self.p_aot_ozone=path.join(self.p_aot_dir,"ozono.dat")
        
        # init dirs
        
        # init data dirs
        if not path.exists(self.data_dir):
            os.makedirs(self.data_dir)
        if not path.exists(self.dd_dir):
            os.makedirs(self.dd_dir)
        if not path.exists(self.k7_dir):
            os.makedirs(self.k7_dir)
        if not path.exists(self.merge_dir):
            os.makedirs(self.merge_dir)
        if not path.exists(self.ascii_dir):
            os.makedirs(self.ascii_dir)
        if not path.exists(self.aot_dir):
            os.makedirs(self.aot_dir)

        # init parameter data dirs
        if not path.exists(self.para_dir):
            os.makedirs(self.para_dir)
        if not path.exists(self.p_aot_dir):
            os.makedirs(self.p_aot_dir)
        if not path.exists(self.p_cal_dir):
            os.makedirs(self.p_cal_dir)

        # Read stations from file
        self.stations = Stations().read(self.stations_file)
