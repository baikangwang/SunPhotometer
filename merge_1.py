import os
import os.path as path
import datetime
import Sunphotometer.spdata as spdata
from dataset.midata import * # for readtable
import glob

baseDir='D:\Working\Projects\SunPhotometer\data\AOD'
k7dir = path.join(baseDir,'k7')
mdir = path.join(baseDir,'merge')
t = datetime.datetime(2009,1,1)

#Read stations from file
stfn = path.join(baseDir,'stations_aod.csv')
table = readtable(stfn, delimiter=',', format='%s%3f%s', encoding='gb2312')
stids = table['Stid']
#stids = ['53487']

#Loop - merge k7 files for each station
for stid in stids:
    print stid
    stk7dir = path.join(k7dir, stid, t.strftime('%Y%m'))
    if not path.isdir(stk7dir):
        continue
        
    fns = glob.glob(path.join(stk7dir,'*.k7'))
    if len(fns) == 0:
        continue
        
    for fn in fns:
        if path.getsize(fn) == 0:
            print fn
            fns.remove(fn)

    stmdir = path.join(mdir, stid)
    if not os.path.exists(stmdir):
        os.makedirs(stmdir)
    outfn = path.join(stmdir, stid + '_' + t.strftime('%Y%m') + '_merge.k7')
    spdata.merge_files(fns, outfn)
        
print 'Finish...'