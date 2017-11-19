import Sunphotometer.spdata as spdata
import datetime

ldir = 'd:/Working/Projects/SunPhotometer/data'
t = datetime.datetime(2017,1,1)
spdata.download(t, data_dir=ldir)