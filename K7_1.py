import os
import os.path as path
import Sunphotometer.spdata as spdata

basedir='D:\Working\Projects\SunPhotometer\data\AOD'
mergedir=path.join(basedir,"merge")
ascdir=path.join(basedir,"ascii")
fn = path.join(mergedir,"54662","54662_200901_merge.k7") #r'D:\Temp\binary\53787-20080722-31.K7'
#fn = r'D:\Temp\binary\c_sunph_rph_c56294_200706280003.K7'
r = spdata.decode(fn)
for line in r:
    if line[:3] == 'NSU':
        print line

#Output
r = spdata.extract(r, 'NSU')
stdir = path.join(ascdir, "54662")
if not os.path.exists(stdir):
    os.makedirs(stdir)
outfn = path.join(stdir,"54662_200901.nsu") #r'D:\Temp\binary\53787-20080722-31.nsu'

spdata.save(r, outfn)