"""
class AodSetting
"""
import sys
import os
import os.path as path
import json
from stations import Station, Stations


class AodSetting:
    def __init__(self, json_setting=None, data_dir=None, para_dir=None, ftp_ip=None, ftp_user=None, ftp_psw=None, ftp_root="/sunphotometer"):
        """Aod process setting. When json_setting presents then it initializes from the json setting file and ommit other parameters

        Parameters
        ----------
        self:
        json_setting: (*string*) the json setting file, if presents then initialize the setting object from the json setting file
        data_dir:     (*string*) the base data dir
        para_dir:     (*string*) the base parameter data dir
        ftp_ip:       (*string*) the ftp Ip
        ftp_user:     (*string*) the ftp user
        ftp_psw:      (*string*) the ftp password
        ftp_root:     (*string*) the default root ftp dir
        Returns
        -------

        """

        if json_setting is None:
            self.__init_normal(data_dir, para_dir, ftp_ip,
                               ftp_user, ftp_psw, ftp_root)
        else:
            self.__init_json(json_setting)

    def __init_json(self, json_setting):
        """initialize the aod setting from the json setting file

        Parameters
        ----------
        self: 
        json_setting: (*string*) the json setting file

        Returns
        -------

        """
        # convert json_setting path to abspath
        if not os.path.isfile(json_setting):
            json_setting = os.path.join(
                os.path.abspath(__file__), json_setting)

        if not os.path.exists(json_setting):
            raise IOError('{0} not exists'.format(json_setting))

        obj_setting = None
        try:
            with open(json_setting) as fjson:
                obj_setting = json.load(fjson)
        except:
            raise IOError('{0} invalid json format: {1}'.format(
                json_setting, sys.exc_info()[0]))

        if 'data_dir' in obj_setting:
            self.data_dir = obj_setting["data_dir"]
        else:
            self.data_dir = os.path.join(
                os.path.dirname(os.path.abspath(__file__)), 'data','AOD')

        if 'para_dir' in obj_setting:
            self.para_dir = obj_setting['para_dir']
        else:
            raise IOError('{0} invalid aod setting: {1}',
                          json_setting, 'para_dir not exists')

        if 'ftp_ip' in obj_setting:
            self.ftp_ip = obj_setting['ftp_ip']
        else:
            raise IOError('{0} invalid aod setting: {1}',
                          json_setting, 'ftp_ip not exists')

        if 'ftp_user' in obj_setting:
            self.ftp_user = obj_setting['ftp_user']
        else:
            raise IOError('{0} invalid aod setting: {1}',
                          json_setting, 'ftp_user not exists')

        if 'ftp_psw' in obj_setting:
            self.ftp_psw = obj_setting['ftp_psw']
        else:
            raise IOError('{0} invalid aod setting: {1}',
                          json_setting, 'ftp_psw not exists')

        if 'ftp_root' in obj_setting:
            self.ftp_root = obj_setting['ftp_root']
        else:
            raise IOError('{0} invalid aod setting: {1}',
                          json_setting, 'ftp_root not exists')

        self.__init_paras()

    def __init_normal(self, data_dir, para_dir, ftp_ip, ftp_user, ftp_psw, ftp_root="/sunphotometer"):
        """Initialize the Aod Setting

        Parameters
        ----------
        self: 
        data_dir:                      (*string*) the base data dir
        para_dir:                      (*string*) the base parameter data dir
        ftp_ip:                        (*string*) the ftp Ip
        ftp_user:                      (*string*) the ftp user
        ftp_psw:                       (*string*) the ftp password
        ftp_root="/sunphotometer":     (*string*) the default root ftp dir

        Returns
        -------

        """
        self.data_dir = data_dir
        self.para_dir = para_dir
        self.ftp_ip = ftp_ip
        self.ftp_user = ftp_user
        self.ftp_psw = ftp_psw
        self.ftp_root = ftp_root

        self.__init_paras()

    def __init_paras(self):
        """Initial data and parameters

        Parameters
        ----------
        self: 

        Returns
        -------

        """
        self.dd_dir = path.join(self.data_dir, "download")
        self.k7_dir = path.join(self.data_dir, "k7")
        self.merge_dir = path.join(self.data_dir, "merge")
        self.ascii_dir = path.join(self.data_dir, "ascii")
        self.aot_dir = path.join(self.data_dir, "aod")
        self.stations_file = path.join(self.data_dir, "stations_aod.csv")
        self.p_aot_dir = path.join(self.para_dir, "Aot")
        self.p_cal_dir = path.join(self.para_dir, "CalFile")
        self.p_aot_exe = path.join(self.p_aot_dir, "ESPESOR.EXE")
        self.p_aot_input = path.join(self.p_aot_dir, "inputpar.dat")
        self.p_aot_ozone = path.join(self.p_aot_dir, "ozono.dat")

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
