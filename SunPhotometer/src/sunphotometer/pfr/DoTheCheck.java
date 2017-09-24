package sunphotometer.pfr;

public class DoTheCheck {

    /*	This Class does some basic checks on the data given by the Level 2 file.
		Since the data of the measurements are stored in the array BodyData, basically all the checks
		are done on this array.		
     */
    WarningLogger warnLog = new WarningLogger();
    int dataIndex = 3; // column where measurements start in body..

    int[] DoFirstCheck(ReadLevel2File ThisLevel2File) {
//find some solar parameters
/*
	find dusk, dawn and noon
	the criterion for dusk or dawn is: the measurement it the first/last one to have an elevation>0 
	noon is the point of maximal elevation.
	if there are several points of maximal elevation, noon is the avarage of the hours at which these points occured.
	furthermore it is kept track of the indexes of these events.
	the noon hour index is the index of that measurement point at which the noonhour occured.
	if the noonhour occurs between two measurement points it is the index of the point after the noon hour
         */
        double[] DayHour = new double[ThisLevel2File.BodyDataLength];
        double[] TheElevation = new double[ThisLevel2File.BodyDataLength];
        boolean DuskOccurred = false;
        boolean DawnOccurred = false;

//Find Index of Dawn
        int DawnCount = 0;
        while (!DawnOccurred) {
            if (ThisLevel2File.BodyData[DawnCount][1] > 0) {
                DawnOccurred = true;
            }
            DawnCount++;
        }
        DawnCount--;

//Find Index of Dusk		
        int DuskCount = ThisLevel2File.BodyDataLength;
        while (!DuskOccurred) {
            DuskCount--;
            if (ThisLevel2File.BodyData[DuskCount][1] > 0) {
                DuskOccurred = true;
            }
        }

        double DawnHour = ThisLevel2File.BodyData[DawnCount][0];
        double DuskHour = ThisLevel2File.BodyData[DuskCount][0];

//Find time Of Noon		
        double MaxElevation = 0;
        int AmountOfMaxElevation = 0;
        int[] CountMaxElevation = new int[64];
        for (int i = DawnCount; i < DuskCount; i++) {
            if (ThisLevel2File.BodyData[i][1] > MaxElevation) {
                AmountOfMaxElevation = 0;
                MaxElevation = ThisLevel2File.BodyData[i][1];
                CountMaxElevation[0] = i;
            } else if (ThisLevel2File.BodyData[i][1] == MaxElevation) {
                AmountOfMaxElevation++;
                CountMaxElevation[AmountOfMaxElevation] = i;
            }
        }
        AmountOfMaxElevation++;
        double NoonHour = 0;
        for (int i = 0; i < (AmountOfMaxElevation); i++) {
            NoonHour = NoonHour + ThisLevel2File.BodyData[(CountMaxElevation[i])][0];
        }
        NoonHour = NoonHour / AmountOfMaxElevation;

//Find the corresponding Index of Noon		
        boolean NoonOccurred = false;
        int NoonCount = DawnCount;
        while (!NoonOccurred) {
            if (ThisLevel2File.BodyData[NoonCount][0] >= NoonHour) {
                NoonOccurred = true;
            }
            NoonCount++;
        }
        NoonCount--;

        // find the maximal wavelength (longest wavelength)
        int MaxWavelengthIndex = 0;
        double MaxWavelength = 0;
        for (int i = 0; i < (ThisLevel2File.wavelength.numericalData.length); i++) {
            if (ThisLevel2File.wavelength.numericalData[i] > MaxWavelength) {
                MaxWavelength = ThisLevel2File.wavelength.numericalData[i];
                MaxWavelengthIndex = i;
            }
        }

        /*	unfortunately not all the data between dawn and dusk are useful for our calculations
			therefore we search for an other special criterion:
			The Index at which the Signal was stronger than one eight of the calibration V0	
			find the earliest hour and the latest hour
         */
        double CritValue;
        if (ThisLevel2File.calibrat.exists) {
            CritValue = ThisLevel2File.calibrat.numericalData[MaxWavelengthIndex] / 8;
        } else {
            CritValue = 0.4;
        }
        boolean MinHourOccurred = false;
        boolean MaxHourOccurred = false;
        int IndexToSearch = MaxWavelengthIndex + dataIndex;
        double MinHour;
        double MaxHour;

//Find time of earliest useful data		
        int MinHourCount = DawnCount;
        int MaxHourCount = DuskCount;

        while ((!MinHourOccurred) && (MinHourCount < (DuskCount + 1))) {
            if (ThisLevel2File.BodyData[MinHourCount][IndexToSearch] > CritValue) {
                MinHourOccurred = true;
            }
            MinHourCount++;
        }
        MinHourCount--;

        if (MinHourOccurred) {	//Find time of latest useful data		
            while (!MaxHourOccurred) {
                MaxHourCount--;
                if (ThisLevel2File.BodyData[MaxHourCount][IndexToSearch] > 0) {
                    MaxHourOccurred = true;
                }
            }

            MinHour = ThisLevel2File.BodyData[MinHourCount][0];
            MaxHour = ThisLevel2File.BodyData[MaxHourCount][0];
        } else {	// use different approach because very overcast day
            MinHourCount = DawnCount;
            while (!MinHourOccurred) {
                if (ThisLevel2File.BodyData[MinHourCount][1] > 5.0) {
                    MinHourOccurred = true;
                }
                MinHourCount++;
            }
            MinHourCount--;

            while (!MaxHourOccurred) {
                MaxHourCount--;
                if (ThisLevel2File.BodyData[MaxHourCount][1] > 0) {
                    MaxHourOccurred = true;
                }
            }

            MinHour = ThisLevel2File.BodyData[MinHourCount][0];
            MaxHour = ThisLevel2File.BodyData[MaxHourCount][0];
        }

        /*---------------------------------------------------------------------
                     Check The Flags
		This is just a general test of the dayquality. it is checked whether the 
		flags (of the level two file, last column) reported an overcast day or whether the day was quite good.
		the whole shit is reported.
		the criterions to decide whether a day was good or not are quite arbitrary
         */
        int DayQuality;
        int SumOverflow = 0;
        int SumNoOvercast = 0;
        int SumSunPointing = 0;
        int SumTemperature = 0;

        for (int i = 0; i < ThisLevel2File.BodyDataLength; i++) {
            DayQuality = (int) ThisLevel2File.BodyData[i][11];
            int OverflowFlag = (DayQuality % 128) / 64;
            int OvercastFlag = (DayQuality % 64) / 16;
            if (OvercastFlag == 0) {
                SumNoOvercast++;
            }
            int SunPointingFlag = (DayQuality % 4) / 2;
            int TemperatureFlag = (DayQuality % 2);
            SumOverflow += OverflowFlag;
            SumSunPointing += SunPointingFlag;
            SumTemperature += TemperatureFlag;

        }

        double MeanDayHourDiff = 0;
        for (int i = 0; i < (ThisLevel2File.BodyDataLength - 1); i++) {
            MeanDayHourDiff += (ThisLevel2File.BodyData[(i + 1)][0] - ThisLevel2File.BodyData[i][0]);
        }
        MeanDayHourDiff = MeanDayHourDiff / (ThisLevel2File.BodyDataLength - 1);

        double SunshineDuration = (MeanDayHourDiff * SumNoOvercast) / (DuskHour - DawnHour);
        if (SunshineDuration < 0.1 && SunshineDuration > 0) {
            System.out.println("Warning: Very little Sunshine");
        }
        if (SunshineDuration == 0) {
            warnLog.addWarning("WARNING: The whole day was overcast, no sunshine at all.");
        }
        if (SumTemperature > 10) {
            warnLog.addWarning("WARNING: Sensor reported several (> 10 times) temperature excursions.");
        }
        if (SumSunPointing > 10) {
            warnLog.addWarning("WARNING: Pointing out of Sun reported several times (> 10 times).");
        }
        if (SumOverflow > 10) {
            warnLog.addWarning("WARNING: Signal overrange reported several times (> 10 times).");
        }

        int[] DayData = {MinHourCount, NoonCount, MaxHourCount};

        return DayData;

    }
}
