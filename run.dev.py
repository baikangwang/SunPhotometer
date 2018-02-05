"""
run aod process
"""
import pickle
import logging
from Sunphotometer import *


def main():
    # convert json_setting path to abspath
    aod_setting = AodSetting(json_setting='app.json')

    # initial logging
    sub_log_dir = os.path.join(aod_setting.log_dir, date.strftime("%Y%m"))
    if not os.path.exists(sub_log_dir):
        os.makedirs(sub_log_dir)

    log_file = os.path.join(
        sub_log_dir, '{0}.log'.format(date.strftime("%Y-%m-%d")))
    logging.basicConfig(filename=log_file, level=logging.INFO,
                        format="%(asctime)s %(levelname)s: %(message)s")
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
