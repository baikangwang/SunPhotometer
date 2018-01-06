"""
run aod process
"""

from aodprocess import *
from aodsetting import *


# init setting
data_dir = 'D:\Working\Projects\SunPhotometer\data\AOD'
para_dir = 'D:\Working\Projects\SunPhotometer\Sunphotometer'
ftp_ip = '192.168.3.20'
ftp_user = 'bkwang'
ftp_psw = 'bkwang'
aod_setting = AodSetting(data_dir, para_dir, ftp_ip, ftp_user, ftp_psw)

# init process
process = AodProcess(aod_setting)

# run
date = datetime.datetime.today()
# process.run(date.year,date.month,date.day,3)
# process.run(date.year,date.month,date.day,4)
process.run()
