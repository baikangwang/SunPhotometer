import csv


class Station:
    def __init__(self, stId='', lat='', lon='', alt='', stname='', calibr=''):
        """A station object from the csv file, stations_aod.

        Parameters
        ----------
        self: 
        stId='': string, the station Id
        lat='': float, the latitude of the station
        lon='': float, the longtitude of the station
        alt='': float, the altitude of the station
        stname='': string, the name of the station
        calibr='': the parameter file name of the station

        Returns
        -------

        """
        self.stId = stId
        self.lat = float(lat)
        self.lon = float(lon)
        self.alt = float(alt)
        self.stname = stname
        self.calibr = calibr

    def hasCalibr(self):
        """Check if the station has configured parameter file.

        Parameters
        ----------
        None

        Returns
        -------
        bool
            True if the calibr is not empty
        """
        return self.calibr != '' and self.calibr is not None


class Stations:

    def __init__(self):
        """The collection of the stations from the csv file, stations_aod.csv.

        Parameters
        ----------
        None 

        Returns
        -------

        """
        self.__stations = dict()

    def read(self, csv_file):
        """read station entries from csvfile.

        Parameters
        ----------
        csv_file:string, the csv file path

        Returns
        -------
        Stations
            the instance itself
        """
        with open(csv_file, 'rb') as csvfile:
            spamreader = csv.reader(csvfile, delimiter=",", quotechar='|')
            for row in spamreader:
                if row[0] is None or row[0] == "" or row[0] == "Stid":
                    continue
                station = Station(row[0], row[1], row[2],
                                  row[3], row[4], row[5])
                self.__stations[station.stId] = station
        return self

    def get(self, stId):
        """Set docstring here.

        Parameters
        ----------
        stId: string, a station Id

        Returns
        -------
        Station
            an instance of Station, None if the station Id is not existing
        """
        return self.__stations.get(stId)

    def getstNames(self):
        """gets a list of station names.

        Parameters
        ----------
        None

        Returns
        -------
        list
            a list of name strings
        """
        return self.__stations.keys()
