import os
import sys
import datetime
import Sunphotometer.spdata as spdata
# from dataset.midata import * # for readtable
from stations import Station, Stations
import glob

baseDir = "D:\Working\Projects\SunPhotometer\data\AOD"
ddir = os.path.join(baseDir, 'download')
k7dir = os.path.join(baseDir, 'k7')
t = datetime.datetime(2009, 1, 1)
ddir = os.path.join(ddir, t.strftime('%Y%m'))
if not os.path.exists(ddir):
    raise IOError

# Read stations from file
stfn = os.path.join(baseDir, 'stations_aod.csv')
# table = readtable(stfn, delimiter=',', format='%s%3f%s', encoding='gb2312')
stations = Stations().read(stfn)
stids = stations.getstNames()  # table['Stid']

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
