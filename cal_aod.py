import os
import os.path as path
import datetime
import Sunphotometer.spdata as spdata

# date stamp
t = datetime.datetime(2009,1,1)
#Set folders
basedir='D:\Working\Projects\SunPhotometer\data\AOD'
# datadir = r'C:\TEMP\Data\AOD\processing'
ddir = basedir# os.path.join(datadir, 'data')
wdir = r'D:\Working\Projects\SunPhotometer\Sunphotometer\Aot'
#wdir = r'D:\MyProgram\VB.Net\AOPView\bin\Debug\Aot'
ascdir = path.join(ddir, 'Ascii')
if not path.exists(ascdir):
    os.makedirs(ascdir)
aotdir = path.join(ddir, 'aot')
if not path.exists(aotdir):
    os.makedirs(aotdir)

#Calculate AOD
print 'Calculate AOD...'
fn = '54662'
nsufn = path.join(ascdir, fn,fn+"_"+t.strftime("%Y%m")+'.NSU')
if not os.path.exists(nsufn):
    print 'Output nsu file...'
    k7fn = path.join(ddir,"merge",fn, fn +"_"+t.strftime("%Y%m")+"_merge.K7")
    rr = spdata.decode(k7fn)
    r = spdata.extract(rr, 'NSU')    
    spdata.save(r, nsufn)

exefn = path.join(wdir, 'ESPESOR.EXE')
inputfn = path.join(wdir, 'inputpar.dat')
ozonefn = path.join(wdir, 'ozono.dat')
# calfn = path.join("D:\Working\Projects\SunPhotometer\Sunphotometer", "CalFile",'calibr746.cal')
calfn = path.join("D:\Working\Projects\SunPhotometer\Sunphotometer", "CalFile",'calibr27.cal')
taofn = path.join(aotdir,fn, fn +"_"+t.strftime("%Y%m")+'.tao')
lat = 28.90#41.76
lon = 121.63#123.41
alt = 91.5#110

print "wdir: "+ wdir
print "calfn: "+calfn
print "taofn: "+taofn
print "nsufn: "+nsufn

spdata.cal_aot(wdir, calfn, taofn, nsufn, lat, lon, alt, alpha=1)

print 'Done!'