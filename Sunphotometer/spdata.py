# -*- coding: utf-8 -*-
import os
import time
import shutil
from datetime import datetime
from datetime import timedelta
from ftplib import FTP
from sunphotometer import DataProcess

def download(stime, etime=None, stations=None, ftp_dir='/data2/cawas', data_dir='S:/data', ftp_ip='10.32.8.175', \
    user='cawas', pword='cawas2015'):
    '''
    Download AOD data files from CAMS ftp server.
    
    :param stime: (*datetime*) Start time.
    :param etime: (*datetime*) End time.
    :param stations: (*list*) Station list. Default is None, all stations will be included.
    :param ftp_dir: (*string*) Ftp server directory.
    :param data_dir: (*string*) Local data directory.
    :param ftp_ip: (*string*) Ftp address.
    :param user: (*string*) Ftp user name.
    :param pword: (*string*) Ftp password.
    '''
    #Set directories
    net = 'AC'
    type = 'AOD'
    ftp_dir = ftp_dir + '/cawn_dat/aer-' + type
    
    #Login ftp
    print 'Login ftp host...'
    ftp = FTP(ftp_ip)
    ftp.login(user, pword)
    
    #Download the data
    print 'Start download data...'
    host_dir = ftp_dir + '/' + stime.strftime('%Y%m')
    print host_dir
    ftp.cwd(host_dir)
    local_dir = os.path.join(data_dir, stime.strftime('%Y%m'))
    print local_dir
    if not os.path.isdir(local_dir):
        os.makedirs(local_dir)

    filelist = []
    ftp.dir('*.*', filelist.append)
    n = 0
    for f in filelist:
        name = f.split(' ')[-1]
        print '    ' + name
        n += 1
        ftp.retrbinary('RETR %s' % name, open(os.path.join(local_dir, name), 'wb').write)

    #Quit ftp
    ftp.quit()
    print 'Total file number: ' + str(n)
    print 'Download Finished!'
    
def unrar(rarfn, dest_dir):
    '''
    Unzip RAR file.
    
    :param rarfn: (*string*) RAR data file.
    :param dest_dir: (*string*) Destination folder.
    '''
    DataProcess.unrar(rarfn, dest_dir)
    
def merge_files(infns, outfn):
    '''
    Merge multiple k7 data files to one file.
    
    :param infns: (*list*) Input k7 data files.
    :param outfn: (*string*) Output k7 data file.
    '''
    DataProcess.mergeFiles(infns, outfn)
    
def decode(k7fn):
    '''
    Decode k7 file to data list.
    
    :param k7fn: (*string*) K7 file name.
    
    :returns: (*list*) Data list.
    '''
    r = DataProcess.decode(k7fn)
    return r
    
def extract(data, type='NSU'):
    '''
    Extract data by type.
    
    :param data: (*list*) Data list.
    :param type: (*string*) Data type.
    
    :returns: (*list*) Extracted data list.
    '''
    r = DataProcess.getDataByType(data, type)
    return r
    
def save(data, fn):
    '''
    Save data to an ASCII file.
    
    :param data: (*list*) Data list.
    :param fn: (*string*) Output file name.
    '''
    DataProcess.writeASCIIFile(data, fn)
    
def cal_aot(wdir, calfn, taofn, nsufn, lat, lon, alt, insnum=1, cloud=1, no2=-2, \
        no2fn=None, ozone=-1, ozonefn='ozono.dat', alpha=1, nwave=4, waves='2 3 4 5', \
        press=-1):
    '''
    Calculate AOT.
    
    :param exefn: (*string*) Excution file name.
    :param inputfn: (*string*) Input file name.
    :param calfn: (*string*) Calibration file name.
    :param taofn: (*string*) Result AOT file
    :param lat: (*float*) Latitude.
    :param lon: (*float*) Longitude.
    :param alt: (*float*) Altitude.
    :param ozonefn: (*string*) Ozone file name.
    :param nsufn: (*string*) NSU data file name.
    '''
    #DataProcess.calAOT(exefn, inputfn, calfn, taofn, lat, lon, alt, ozonefn, nsufn)
    #wdir = r'C:\Program Files (x86)\ASTPWin\PlugIns\Aot'
    exefn = os.path.join(wdir, 'ESPESOR.EXE')
    inputfn = os.path.join(wdir, 'inputpar.dat')
    f = open(inputfn, 'w')
    f.write(str(insnum) + '\n')     #Instrument number (according with “instruments.dat”)
    f.write(calfn + '\n')           #Path of the calibration file
    f.write(taofn + '\n')           #Path of the output file
    f.write(str(cloud) + '\n')      #Cloud filtering (1 yes, 0 no)
    f.write(str(lat) + '\n')        #Latitude of measurement place
    f.write(str(lon) + '\n')        #Longitude of measurement place
    f.write(str(alt) + '\n')        #Surface height (m a.s.l)
    f.write(str(no2) + '\n')        #no2 mean latitude values
    if no2 == -1:
        f.write(no2fn + '\n')       #Path of the no2 file
    f.write(str(ozone) + '\n')      #ozone dobson units
    if ozone == -1:
        f.write(ozonefn + '\n')     #Path of the ozone file
    f.write(str(alpha) + '\n')      #alfa parameter calculation (0 no, 1 yes)
    f.write(str(nwave) + '\n')      #number of wavelength used in alpha calculation
    f.write(waves + '\n')           #position of the wavelength used in alpha calculation
    f.write(str(press) + '\n')      #Surface pressure
    #f.write(sskfn + '\n')           #Path of ascii ssk file (blank as colums separator)
    f.write(nsufn + '\n')           #Path of ascii nsu file (blank as colums separator)
    f.close()
    os.chdir(wdir)
    os.system('ESPESOR.EXE inputpar.dat /G0')
    
def test():
    print 'Test passed!'