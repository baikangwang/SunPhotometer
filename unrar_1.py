import Sunphotometer.spdata as spdata
import glob

ddir = 'C:/temp/data/AOD/download'
k7dir = 'C:/temp/data/AOD/k7'
t = datetime.datetime(2017,1,1)
ddir = os.path.join(ddir, t.strftime('%Y%m'))
if not os.path.exists(ddir):
    raise IOError    

#Read stations from file
stfn = 'C:/Temp/data/aod/stations_aod.csv'
table = readtable(stfn, delimiter=',', format='%s%3f%s', encoding='gb2312')
stids = table['Stid']

#Loop - unrar files for each station
for stid in stids:
    stk7dir = os.path.join(k7dir, stid + '/' + t.strftime('%Y%m'))
    if not os.path.isdir(stk7dir):
        os.makedirs(stk7dir)
    fns = glob.glob(ddir + '/*' + stid + '*.rar')
    for fn in fns:
        if os.path.getsize(os.path.join(ddir, fn)) == 0:
            continue
        if fn.endswith('.rar') or fn.endswith('.RAR'):
            print fn
            spdata.unrar(os.path.join(ddir, fn), stk7dir) 
        
print 'Finish...'