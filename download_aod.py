import Sunphotometer.spdata as spdata
import datetime

ldir = 'd:/Working/Projects/SunPhotometer/data/AOD/download'#'S:/data/download' #
t = datetime.datetime(2017,1,1)
spdata.download(stime= t, ftp_dir='/sunphotometer', data_dir=ldir, ftp_ip='192.168.33.20', \
    user='bkwang', pword='bkwang')