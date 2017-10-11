import Sunphotometer.spdata as spdata

ldir = 'd:/Working/Projects/SunPhotometer.source/data'
t = datetime.datetime(2017,1,1)
spdata.download(t, data_dir=ldir)