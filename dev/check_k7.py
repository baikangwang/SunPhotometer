import os
import datetime
import Sunphotometer.spdata as spdata
import glob

k7dir = 'D:\Working\Projects\SunPhotometer\data\AOD\k7'
t = datetime.datetime(2009,1,1)
stid = '54662'
stk7dir = os.path.join(k7dir, stid, t.strftime('%Y%m'))
fns = glob.glob(os.path.join(stk7dir,'*.k7'))
for fn in fns:
    if os.path.getsize(fn) > 0:
        print fn
        spdata.decode(fn)
        