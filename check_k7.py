import Sunphotometer.spdata as spdata
import glob

k7dir = 'C:/temp/data/AOD/k7'
t = datetime.datetime(2017,1,1)
stid = '53487'
stk7dir = os.path.join(k7dir, stid + '/' + t.strftime('%Y%m'))
fns = glob.glob(stk7dir + '/*.k7')
for fn in fns:
    if os.path.getsize(fn) > 0:
        print fn
        spdata.decode(fn)
        