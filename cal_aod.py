import Sunphotometer.spdata as spdata

#Set folders
datadir = r'C:\TEMP\Data\AOD\processing'
ddir = os.path.join(datadir, 'data')
wdir = r'C:\TEMP\Data\AOD\Aot'
#wdir = r'D:\MyProgram\VB.Net\AOPView\bin\Debug\Aot'
ascdir = os.path.join(ddir, 'Ascii')
if not os.path.exists(ascdir):
    os.makedirs(ascdir)
aotdir = os.path.join(datadir, 'aot')
if not os.path.exists(aotdir):
    os.makedirs(aotdir)

#Calculate AOD
print 'Calculate AOD...'
fn = 'Merging201401'
nsufn = os.path.join(ascdir, fn + '.NSU')
if not os.path.exists(nsufn):
    print 'Output nsu file...'
    k7fn = os.path.join(ddir, fn + '.K7')
    rr = spdata.decode(k7fn)
    r = spdata.extract(rr, 'NSU')    
    spdata.save(r, nsufn)

exefn = os.path.join(wdir, 'ESPESOR.EXE')
inputfn = os.path.join(ddir, 'inputpar.dat')
ozonefn = os.path.join(wdir, 'ozono.dat')
calfn = os.path.join(datadir, r'calibration\calibr746.cal')
taofn = os.path.join(aotdir, fn + '.tao')
lat = 41.76
lon = 123.41
alt = 110
spdata.cal_aot(wdir, calfn, taofn, nsufn, lat, lon, alt, alpha=1)

print 'Done!'