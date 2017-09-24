import Sunphotometer.spdata as spdata

fn = r'D:\Temp\binary\53787-20080722-31.K7'
#fn = r'D:\Temp\binary\c_sunph_rph_c56294_200706280003.K7'
r = spdata.decode(fn)
for line in r:
    if line[:3] == 'NSU':
        print line

#Output
r = spdata.extract(r, 'NSU')
outfn = r'D:\Temp\binary\53787-20080722-31.nsu'
spdata.save(r, outfn)