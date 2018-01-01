import os
import os.path as path
import datetime
# from dataset.midata import * # for readtable
from stations import Station, Stations
import Sunphotometer.spdata as spdata

# date stamp
t = datetime.datetime(2009, 1, 1)
# Set folders
basedir = 'D:\Working\Projects\SunPhotometer\data\AOD'
# datadir = r'C:\TEMP\Data\AOD\processing'
ddir = basedir  # os.path.join(datadir, 'data')
wdir = r'D:\Working\Projects\SunPhotometer\Sunphotometer\Aot'
#wdir = r'D:\MyProgram\VB.Net\AOPView\bin\Debug\Aot'
ascdir = path.join(ddir, 'Ascii')
if not path.exists(ascdir):
    os.makedirs(ascdir)
aotdir = path.join(ddir, 'aot')
if not path.exists(aotdir):
    os.makedirs(aotdir)

# Read stations from file
stfn = path.join(basedir, 'stations_aod.csv')
# table = readtable(stfn, delimiter=',', format='%s%3f%s', encoding='gb2312')
stations = Stations().read(stfn)
# stids = stations.getstNames()  # table['Stid']

# Calculate AOD
print 'Calculate AOD...'

for stname in stations.getstNames():
    # fn = '54662'
    station=stations.get(stname)
    fn=station.stId
    k7fn = path.join(ddir, "merge", fn, fn + "_" +
                    t.strftime("%Y%m") + "_merge.K7")
    if not os.path.exists(k7fn):
        continue
    print station
    nsufn = path.join(ascdir, fn, fn + "_" + t.strftime("%Y%m") + '.NSU')
    if not os.path.exists(nsufn):
        print 'Output nsu file...'
        rr = spdata.decode(k7fn)
        r = spdata.extract(rr, 'NSU')
        spdata.save(r, nsufn)

    exefn = path.join(wdir, 'ESPESOR.EXE')
    inputfn = path.join(wdir, 'inputpar.dat')
    ozonefn = path.join(wdir, 'ozono.dat')
    # calfn = path.join("D:\Working\Projects\SunPhotometer\Sunphotometer", "CalFile",'calibr746.cal')
    calibrFileName="calibr"+station.calibr+".cal"
    calfn = path.join(
        "D:\Working\Projects\SunPhotometer\Sunphotometer", "CalFile", calibrFileName)
    taofn = path.join(aotdir, fn, fn + "_" + t.strftime("%Y%m") + '.tao')
    lat = station.lat #28.90  # 41.76
    lon = station.lon # 121.63  # 123.41
    alt = station.alt # 91.5  # 110

    print "wdir: " + wdir
    print "calfn: " + calfn
    print "taofn: " + taofn
    print "nsufn: " + nsufn

    spdata.cal_aot(wdir, calfn, taofn, nsufn, lat, lon, alt, alpha=1)

print 'Done!'
