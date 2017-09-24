import Sunphotometer.spdata as spdata

ldir = 'S:/data/download'
t = datetime.datetime(2017,1,1)
spdata.download(t, data_dir=ldir)