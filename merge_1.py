import Sunphotometer.spdata as spdata
import glob

k7dir = 'C:/temp/data/AOD/k7'
mdir = 'C:/temp/data/AOD/merge'
t = datetime.datetime(2017,1,1)

#Read stations from file
stfn = 'C:/Temp/data/aod/stations_aod.csv'
table = readtable(stfn, delimiter=',', format='%s%3f%s', encoding='gb2312')
stids = table['Stid']
#stids = ['53487']

#Loop - merge k7 files for each station
for stid in stids:
    print stid
    stk7dir = os.path.join(k7dir, stid + '/' + t.strftime('%Y%m'))
    if not os.path.isdir(stk7dir):
        continue
        
    fns = glob.glob(stk7dir + '/*.k7')
    if len(fns) == 0:
        continue
        
    for fn in fns:
        if os.path.getsize(fn) == 0:
            print fn
            fns.remove(fn)

    stmdir = os.path.join(mdir, stid)
    if not os.path.exists(stmdir):
        os.makedirs(stmdir)
    outfn = os.path.join(stmdir, stid + '_' + t.strftime('%Y%m') + '_merge.k7')
    spdata.merge_files(fns, outfn)
        
print 'Finish...'