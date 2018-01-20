"""
class AodProcess

"""

import os
import sys
import os.path as path
import datetime
import spdata
import glob

from aodsetting import *
from stations import Station, Stations


class AodProcess:
    def __init__(self, aodSetting):
        self.aodSetting = aodSetting

    def run(self, year, month, day):
        """process a batch of k7 data to aod data

        Parameters
        ----------
        self: 
        year: the year of date range
        month: the moth of date range
        day: the day of date range

        Returns
        -------

        """
        self.__download(year, month, day)

        self.__unrar(year, month, day)

        self.__merge(year, month, day)

        self.__cal_aod(year, month, day)

    def run_dev(self, year, month, day, step):
        """run step by step for dev testing

        Parameters
        ----------
        self: 
        year: the year of date range
        month: the moth of date range
        day: the day of date range
        step=1: the process steps, 1=download, 2=unrar, 3=merge, 4=

        Returns
        -------

        """

        if step == 1:
            self.__download(year, month, day)
        if step == 2:
            self.__unrar(year, month, day)
        if step == 3:
            self.__merge(year, month, day)
        if step == 4:
            self.__cal_aod(year, month, day)

    def __download(self, year, month, day):
        """download k7 data through ftp.

        Parameters
        ----------
        self: 
        year: the year of date range
        month: the moth of date range
        day: the day of date range

        Returns
        -------

        """
        print 'Download...'
        t = datetime.datetime(year, month, day)
        spdata.download(stime=t, stations=self.aodSetting.stations, ftp_dir=self.aodSetting.ftp_root, data_dir=self.aodSetting.dd_dir, ftp_ip=self.aodSetting.ftp_ip,
                        user=self.aodSetting.ftp_user, pword=self.aodSetting.ftp_psw)
        print 'Download Done!'

    def __unrar(self, year, month, day):
        """unzip the downloaded k7 files.

        Parameters
        ----------
        self: 
        year: the year of date range
        month: the moth of date range
        day: the day of date range

        Returns
        -------

        """
        print 'Un-rar...'
        k7dir = self.aodSetting.k7_dir
        t = datetime.datetime(year, month, day)
        stids = self.aodSetting.stations.getstIds()

        # Loop - unrar files for each station
        for stid in stids:
            ddir = os.path.join(self.aodSetting.dd_dir, stid,
                                t.strftime('%Y%m'))
            if not os.path.exists(ddir):
                # print '{0} not exists'.format(ddir)
                continue
            fns = glob.glob(os.path.join(ddir, '*' + stid +
                                         '*' + t.strftime('%Y%m%d') + '*' + '*.rar'))
            for fn in fns:
                if os.path.getsize(os.path.join(ddir, fn)) == 0:
                    continue
                if fn.endswith('.rar') or fn.endswith('.RAR'):
                    print 'Un-rar [{0}] => {1}'.format(stid, fn)
                    stk7dir = os.path.join(
                        k7dir, stid, t.strftime('%Y%m'), t.strftime('%d'))
                    if not os.path.isdir(stk7dir):
                        os.makedirs(stk7dir)
                    spdata.unrar(os.path.join(ddir, fn), stk7dir)

        print 'Un-rar Done!'

    def __merge(self, year, month, day):
        """decoding the k7 files and merge all of k7 files in specific date range into a single k7 file

        Parameters
        ----------
        self: 
        year: the year of date range
        month: the moth of date range
        day: the day of date range

        Returns
        -------

        """
        print 'Merge...'

        k7dir = self.aodSetting.k7_dir  # path.join(baseDir, 'k7')
        mdir = self.aodSetting.merge_dir  # path.join(baseDir, 'merge')
        t = datetime.datetime(year, month, day)

        stids = self.aodSetting.stations.getstIds()

        # Loop - merge k7 files for each station
        for stid in stids:
            stk7dir = path.join(
                k7dir, stid, t.strftime('%Y%m'), t.strftime('%d'))
            if not path.isdir(stk7dir):
                continue

            fns = glob.glob(path.join(stk7dir, '*.k7'))
            if len(fns) == 0:
                continue

            # check k7 and remove it if empty file
            for fn in fns:
                if path.getsize(fn) == 0:
                    print 'Empty K7 [{0}] => {1} '.format(stid, fn)
                    fns.remove(fn)

            stmdir = path.join(mdir, stid, t.strftime('%Y%m'))
            if not os.path.exists(stmdir):
                os.makedirs(stmdir)

            outfn = path.join(stmdir, stid + '_' +
                              t.strftime('%Y%m%d') + '_merge.k7')
            spdata.merge_files(fns, outfn)
            print 'Merge [{0}] => {1}'.format(stid, outfn)

        print 'Merge Done!'

    def __cal_aod(self, year, month, day):
        """calculate the aod file

        Parameters
        ----------
        self: 
        year: the year of date range
        month: the moth of date range
        day: the day of date range

        Returns
        -------

        """
        print 'Calculate...'

        t = datetime.datetime(year, month, day)

        ddir = self.aodSetting.data_dir
        wdir = self.aodSetting.p_aot_dir
        ascdir = self.aodSetting.ascii_dir
        aotdir = self.aodSetting.aot_dir

        stations = self.aodSetting.stations

        # Calculate AOD
        print 'Calculate AOD...'

        for stId in stations.getstIds():
            # fn = '54662'
            station = stations.get(stId)
            fn = station.stId
            k7fn = path.join(self.aodSetting.merge_dir, fn, t.strftime('%Y%m'), fn + "_" +
                             t.strftime("%Y%m%d") + "_merge.K7")
            if not os.path.exists(k7fn):
                continue
            print '[{0}]: Ready'.format(fn)
            nsu_dir = path.join(ascdir, fn, t.strftime('%Y%m'))
            nsufn = path.join(nsu_dir, fn + "_" +
                              t.strftime("%Y%m%d") + '.NSU')
            if not os.path.exists(nsufn):
                if not os.path.exists(nsu_dir):
                    os.makedirs(nsu_dir)
                rr = spdata.decode(k7fn)
                r = spdata.extract(rr, 'NSU')
                spdata.save(r, nsufn)
                print '[{0}]: Output nsu file'.format(fn)

            # check if the external program and the parameter files are ready
            validated = True
            exefn = self.aodSetting.p_aot_exe
            if not os.path.exists(exefn):
                print '[{0}]: Not Found Aot program, {1}'.format(fn, exefn)
                validated = False

            inputfn = self.aodSetting.p_aot_input
            if not os.path.exists(inputfn):
                print '[{0}]: Not Found input parameter data, {1}'.format(fn, inputfn)
                validated = False

            ozonefn = self.aodSetting.p_aot_ozone
            if not os.path.exists(ozonefn):
                print '[{0}]: Not Found ozone data, {1}'.format(fn, ozonefn)
                validated = False

            calfn = path.join(self.aodSetting.p_cal_dir,
                              "calibr" + station.calibr + ".cal")
            if not os.path.exists(calfn):
                print '[{0}]: Not Found calculation paramter data, {1}'.format(fn, calfn)
                validated = False

            if validated:
                tao_dir = path.join(aotdir, fn, t.strftime('%Y%m'))
                if not os.path.exists(tao_dir):
                    os.makedirs(tao_dir)
                taofn = path.join(tao_dir, fn + "_" +
                                  t.strftime("%Y%m%d") + '.tao')
                lat = station.lat
                lon = station.lon
                alt = station.alt

                spdata.cal_aot(wdir, calfn, taofn, nsufn,
                               lat, lon, alt, alpha=1)
                print '[{0}] => {1}'.format(fn, taofn)
            else:
                print '[{0}]: Abort'.format(fn)

        print 'Calculate Done!'
