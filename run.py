"""
run aod process
"""
import pickle
from Sunphotometer import *


# init setting
data_dir = 'D:\Working\Projects\SunPhotometer\data\AOD'
para_dir = 'D:\Working\Projects\SunPhotometer\Sunphotometer'
ftp_ip = '192.168.3.20'
ftp_user = 'bkwang'
ftp_psw = 'bkwang'


def main():
    # convert json_setting path to abspath
    aod_setting = AodSetting(json_setting='app.json')
    # print pickle.dumps(aod_setting)
    # exit()
    # init process
    process = AodProcess(aod_setting)

    # run
    # date = datetime.datetime.today()
    date = datetime.datetime(2017, 2, 1)
    process.run_dev(date.year, date.month, date.day, 4)
    # process.run_dev(date.year,date.month,date.day,4)
    # process.run(date.year, date.month, date.day)


if __name__ == '__main__':
    main()
