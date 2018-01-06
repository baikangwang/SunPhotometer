import os
import sys
import os.path as path
import datetime
import Sunphotometer.spdata as spdata
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

        t = datetime.datetime(year, month, day)
        spdata.download(stime=t, ftp_dir=self.aodSetting.ftp_root, data_dir=self.aodSetting.download_dir, ftp_ip=self.aodSetting.ftp_ip,
                        user=self.aodSetting.ftp_user, pword=self.aodSetting.ftp_psw)

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

        k7dir = self.aodSetting.k7_dir
        t = datetime.datetime(year, month, day)
        ddir = os.path.join(self.aodSetting.dd_dir, t.strftime('%Y%m'))
        if not os.path.exists(ddir):
            raise IOError

        stids = self.aodSetting.stations.getstNames()

        # Loop - unrar files for each station
        for stid in stids:
            stk7dir = os.path.join(k7dir, stid, t.strftime('%Y%m'))
            if not os.path.isdir(stk7dir):
                os.makedirs(stk7dir)
            fns = glob.glob(os.path.join(ddir, '*' + stid + '*.rar'))
            for fn in fns:
                if os.path.getsize(os.path.join(ddir, fn)) == 0:
                    continue
                if fn.endswith('.rar') or fn.endswith('.RAR'):
                    print fn
                    spdata.unrar(os.path.join(ddir, fn), stk7dir)

        print 'Finish...'

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
        k7dir = self.aodSetting.k7_dir  # path.join(baseDir, 'k7')
        mdir = self.aodSetting.merge_dir  # path.join(baseDir, 'merge')
        t = datetime.datetime(year, month, day)

        stids = self.aodSetting.stations.getstNames()

        # Loop - merge k7 files for each station
        for stid in stids:
            print stid
            stk7dir = path.join(k7dir, stid, t.strftime('%Y%m'))
            if not path.isdir(stk7dir):
                continue

            fns = glob.glob(path.join(stk7dir, '*.k7'))
            if len(fns) == 0:
                continue

            # check k7 and remove it if empty file
            for fn in fns:
                if path.getsize(fn) == 0:
                    print fn
                    fns.remove(fn)

            stmdir = path.join(mdir, stid)
            if not os.path.exists(stmdir):
                os.makedirs(stmdir)
            outfn = path.join(stmdir, stid + '_' +
                              t.strftime('%Y%m') + '_merge.k7')
            spdata.merge_files(fns, outfn)

        print 'Finish...'

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

        """   # date stamp
        t = datetime.datetime(year, month, day)

        ddir = self.aodSetting.data_dir  # os.path.join(datadir, 'data')
        # r'D:\Working\Projects\SunPhotometer\Sunphotometer\Aot'
        wdir = self.aodSetting.p_aot_dir
        #wdir = r'D:\MyProgram\VB.Net\AOPView\bin\Debug\Aot'
        ascdir = self.aodSetting.ascii_dir
        if not path.exists(ascdir):
            os.makedirs(ascdir)
        aotdir = self.aodSetting.aot_dir
        if not path.exists(aotdir):
            os.makedirs(aotdir)

        stations = self.aodSetting.stations  # Stations().read(stfn)

        # Calculate AOD
        print 'Calculate AOD...'

        for stname in stations.getstNames():
            # fn = '54662'
            station = stations.get(stname)
            fn = station.stId
            k7fn = path.join(self.aodSetting.merge_dir, fn, fn + "_" +
                             t.strftime("%Y%m") + "_merge.K7")
            if not os.path.exists(k7fn):
                continue
            print station
            nsufn = path.join(ascdir, fn, fn + "_" +
                              t.strftime("%Y%m") + '.NSU')
            if not os.path.exists(nsufn):
                print 'Output nsu file...'
                rr = spdata.decode(k7fn)
                r = spdata.extract(rr, 'NSU')
                spdata.save(r, nsufn)

            exefn = path.join(wdir, 'ESPESOR.EXE')
            inputfn = path.join(wdir, 'inputpar.dat')
            ozonefn = path.join(wdir, 'ozono.dat')
            # calfn = path.join("D:\Working\Projects\SunPhotometer\Sunphotometer", "CalFile",'calibr746.cal')
            calfn = path.join(self.aodSetting.p_cal_dir,
                              "calibr" + station.calibr + ".cal")
            taofn = path.join(aotdir, fn, fn + "_" +
                              t.strftime("%Y%m") + '.tao')
            lat = station.lat  # 28.90  # 41.76
            lon = station.lon  # 121.63  # 123.41
            alt = station.alt  # 91.5  # 110

            print "wdir: " + wdir
            print "calfn: " + calfn
            print "taofn: " + taofn
            print "nsufn: " + nsufn

            spdata.cal_aot(wdir, calfn, taofn, nsufn, lat, lon, alt, alpha=1)

        print 'Done!'
