import csv
from stations import Station, Stations

csvfile="./source/stations_aod.csv"

def readCSV(csv_file):
    stations_aod=dict();

    with open(csv_file,'rb') as csvfile:
        spamreader=csv.reader(csvfile,delimiter=",",quotechar='|')
        for row in spamreader:
            stations_aod[row[0]]=(row)
            # print(row[0])
            # print(", ".join(row))

    for key in stations_aod.keys():
        (stId,lat,lon,alt,stname,calibr)=stations_aod[key]
        print "stId={0},lat={1},lon={2},alt={3},stname={4},calibr={5}".format(stId,lat,lon,alt,stname,calibr)

def readCSV2(csv_file):
    stations=Stations()
    stations.read(csv_file)
    for name in stations.getstNames():
        station = stations.get(name)
        if station is not None:
            if station.hasCalibr():
                print "stId={0},lat={1},lon={2},alt={3},stname={4},calibr={5}".format(
                    station.stId,
                    station.lat,
                    station.lon,
                    station.alt,
                    station.stname,
                    station.calibr)
            else:
                print "{0} hasn't calibr".format(name)
        else:
            print "{0} not exists".format(name)

readCSV2(csvfile)
    