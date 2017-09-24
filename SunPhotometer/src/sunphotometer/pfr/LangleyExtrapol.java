package sunphotometer.pfr;

public class LangleyExtrapol {		// the following classes are required:
    // Regression.class

    WarningLogger warnLog = new WarningLogger();

    // data which should be given, for these calculations (either by the user or preceeding operations)
    int[] indexData;				// index of first data to take, index of last data to take}
    int method; 					//1=refined, 0=classic
    int timeRange; 					// 0=wholeDay,1=morning only,2=afternoon only
    double[] theMeteoData;
    double LowAirMassLimit;			//(airmass Range...)
    double UppAirMassLimit;
    int MinNumberOfPoints = 10;		// minmal numbers needed for reasonable regression
    int numberOfChannels;
    double[] theWaveLengths;
    int noPoints;     				// indexData[1]-indexData[0]+1
    ReadLevel2File thisLevel2File;	// the level 2 file, where most of the data should come from

    // data calculated or retrieved in this class...		
    boolean[] filterMask;			// to indicate which points may still be used for a langley calibration (true = yes)
    double[] revisedElevation;		// elevation corrected for refraction
    double[] airMass;				// air mass corresponding to elevation/time
    double[][] absorbtionData;
    boolean[][] absorbtionFilter;
    boolean notEnoughPoints;		// decide whether enough points for langley calibration (regression)
    int minPoints;					// minimal number of points which where available for the langley calibration of one wavelength
    int numberOfPoints;				// number of Points available for regression

    double[] Rayleigh;				// rayleighcoefficients
    boolean RayleighFlag;			// rayleighcoefficients calculated on the run..

    double[][] plottingAbsorbtion;	//	filtered data, ready for plotting
    double[] plottingAirmass;		//

    Regression LangleyRegressionData[];
    double[] waterVapourAirmass;	//WaterVapourAirmass as AerosolAirmass
    double[] Calibrate;			//new V1 from langleyCalibration
    double[] CalibrateStdDev;   	//The estimated StandardDeviation of that V1           
    double[] calibrationSlope;		// slope of the calibration
    double[] calibrationIntercept;	// intercept of calibration
    double[] Ratios;				// ratio V1/V0

    // indexes (set in order that if something changes, this changes may be implemented more easily)
    int IndexOfOzone = 0;	//Position of the Ozone value in the meteo data
    int PressureIndex = 2;  //Index Of DataColumn for Pressure in Level2 File Body
    int qualityFlagIndex = 11;
    int dataIndex = 3;

    // useful global variables.
    double maximumAirmass;	// maximal Airmass found in data
    double minimumAirmass;

//----------------------------------------------------------------------------------------------------------------------------------------------		
    public LangleyExtrapol(int[] DayData, ReadLevel2File ThisLevel2File, double[] theMeteoData, int timeRange) {

        /*	DayData = {IndexOfDawn, IndexOfNoon, IndexOfDusk} (indexes according to Bodydata in Level2File...
			timeRange: integer Determing which time range shall be taken for Langley Calibration: 0=whole day, 1=morning 2=afternoon
			meteoData, some meteorological data..
         */
        //assign the options values...
        this.timeRange = timeRange;
        this.thisLevel2File = ThisLevel2File;
        this.theMeteoData = theMeteoData;
        // get some relevant data

        numberOfChannels = thisLevel2File.wavelength.numericalData.length;

        theWaveLengths = new double[numberOfChannels];
        for (int i = 0; i < numberOfChannels; i++) {
            theWaveLengths[i] = thisLevel2File.wavelength.numericalData[i];
        }

        //Check what day range was chosen
        this.indexData = WhatIndexData(timeRange, DayData);
        this.noPoints = indexData[1] - indexData[0] + 1;
        this.filterMask = new boolean[noPoints];

        // correct the Elevation
        revisedElevation = CalculateRefraction(indexData, theMeteoData);

        // take the data and apply the different filters
        for (int i = 0; i < noPoints; i++) {
            if (thisLevel2File.BodyData[(i + indexData[0])][qualityFlagIndex] < 16) {
                filterMask[i] = true;
            } else {
                filterMask[i] = false;
            }
        }

        airMass = CalculateAirmass();

    }	//end constructor

//---------------------------------------------------------------------------
    public void theMain(int method, double[] airMassRange) {
        this.method = method;
        this.LowAirMassLimit = airMassRange[0];
        this.UppAirMassLimit = airMassRange[1];

        //	The Data which goes into the Regression has to be checked whether
        //	the Airmass calculated is in between the specified limits UppAirMassLimit and LowAirMassLimit...
        for (int i = 0; i < (noPoints); i++) {
            if ((airMass[i] < LowAirMassLimit) || (airMass[i] > UppAirMassLimit)) {
                filterMask[i] = false;
            }
        }

        //search for the extrema
        minimumAirmass = 6;
        maximumAirmass = 2;
        for (int i = 0; i < (noPoints); i++) {
            if (airMass[i] > maximumAirmass && filterMask[i]) {
                maximumAirmass = airMass[i];
            }
            if (airMass[i] < minimumAirmass && filterMask[i]) {
                minimumAirmass = airMass[i];
            }
        }

        // check whether there are enough points for a reliable Regression		
        numberOfPoints = 0;
        for (int i = 0; i < noPoints; i++) {
            if (filterMask[i]) {
                numberOfPoints++;
            }
        }

        if (numberOfPoints < MinNumberOfPoints) {
            warnLog.addWarning("WARNING: Not enough Points available for a reliable Regression, sorry no regression done.");
            notEnoughPoints = true;
        } else { // go into the calculations distinguish between refined & classic method
            switch (method) {
                case 0:								//use classic
                    Regression RegressionData1[] = ClassicalLangley();
                    this.LangleyRegressionData = RegressionData1;
                    break;
                case 1: 	 						//use refined
                    Regression RegressionData2[] = RefinedLangley(theMeteoData);
                    this.LangleyRegressionData = RegressionData2;
                    break;
                default:							//use classic
                    Regression RegressionData3[] = ClassicalLangley();
                    this.LangleyRegressionData = RegressionData3;
            }

            // Now we have the Langley logarithimc intercepts calculate new V1 for reasonable
            // ratios, else keep old and mark them by ratio=1 and stanarddeviation = 0;
            Ratios = new double[numberOfChannels];
            Calibrate = new double[numberOfChannels];
            CalibrateStdDev = new double[numberOfChannels];
            double[] StdDevRatio = new double[numberOfChannels];
            calibrationSlope = new double[numberOfChannels];
            calibrationIntercept = new double[numberOfChannels];
            // modify the results slightly in order to be able to display the quantities desiered		

            for (int i = 0; i < numberOfChannels; i++) {
                Ratios[i] = Math.exp(this.LangleyRegressionData[i].Intercept);
                StdDevRatio[i] = Math.exp(this.LangleyRegressionData[i].SigmaIntercept) - 1;
                calibrationIntercept[i] = this.LangleyRegressionData[i].Intercept;
                calibrationSlope[i] = this.LangleyRegressionData[i].Slope;

                if ((Ratios[i] < 0.8) || (Ratios[i] > 1.2)) {
                    Ratios[i] = 1;
                    StdDevRatio[i] = 0;
                }
                Calibrate[i] = Ratios[i] * thisLevel2File.calibrat.numericalData[i];
                CalibrateStdDev[i] = StdDevRatio[i] * thisLevel2File.calibrat.numericalData[i];
            }

            //prepare data for plotting:
            int maxPoints = 0;
            minPoints = noPoints;
            int useablePoints;
            for (int j = 0; j < numberOfChannels; j++) {
                useablePoints = 0;
                for (int i = 0; i < noPoints; i++) {
                    if (filterMask[i] && absorbtionFilter[i][j]) {
                        useablePoints++;
                    }
                }
                if (useablePoints > maxPoints) {
                    maxPoints = useablePoints;
                }
                if (useablePoints < minPoints) {
                    minPoints = useablePoints;
                }
            }

            plottingAbsorbtion = new double[maxPoints][numberOfChannels];
            plottingAirmass = new double[maxPoints];

            int count = 0;
            boolean decision;
            for (int i = 0; (i < noPoints && count < maxPoints); i++) {
                if (filterMask[i]) {
                    decision = true;
                    for (int j = 0; j < numberOfChannels; j++) {
                        if (!absorbtionFilter[i][j]) {
                            decision = false;
                        }
                        plottingAbsorbtion[count][j] = absorbtionData[i][j];
                    }
                    if (decision) {
                        plottingAirmass[count] = airMass[i];
                        count++;
                    }
                }
            }
        }
    }	// end theMain

//-------------------------------------------------------------------
    public double[] CalculateRefraction(int[] IndexData, double[] TheMeteoData) {
        /*
			additive refraction correction to 'true' altitude
			after Samundsson in 
			J.Meeus, Astronomical Algorithms, Willmann-Bell
			%Richmond, 1991 p102								*/

        //given the elevation this calculates the correction due to refraction 

        // needed input (information about period and their starting indexes (indexData)
        // the data itself (ThisLevel2File)
        // some meteorological data (ozone,press,hum,temp) as an array of double in this order.
        double[] RevisedElevation = new double[noPoints];
        for (int i = 0; i < noPoints; i++) {
            if (thisLevel2File.BodyData[(i + IndexData[0])][1] < (-1)) {
                RevisedElevation[i] = (-1);
            } else {
                RevisedElevation[i] = thisLevel2File.BodyData[(i + IndexData[0])][1];
            }

            RevisedElevation[i] = (RevisedElevation[i] + ((10.3) / (RevisedElevation[i] + (5.11)))) * (3.14159) / 180;

            RevisedElevation[i] = ((1.02) / Math.tan(RevisedElevation[i]));
            if (method == 1) {
                RevisedElevation[i] = RevisedElevation[i] * (TheMeteoData[1] / 1010) * 283 / (273 + TheMeteoData[3]);
            }
            RevisedElevation[i] = RevisedElevation[i] / 60 + thisLevel2File.BodyData[i + IndexData[0]][1];

        }
        return RevisedElevation;
    }	// end CalculateRefraction

//-------------------------------------------------------------------	
    public double[] CalculateAirmass() {
        /*function am = kasten(elv)
			% KASTEN Airmass approximation
			% am = kasten(elv);  % elv in degrees
			% Ref: Kasten Applied Optics 28,4735-4738 (1989)*/

 /* 	Calculates the corresponding Airmass to an Elevation Specified
				
         */

        double[] Se = new double[noPoints];
        double[] Pt = new double[noPoints];
        double[] airmass = new double[noPoints];
        double[] HelpInput = new double[noPoints];
        double[][] Output = new double[noPoints][(numberOfChannels + 4)];
        int DataLength = 0;

        //here comes the Kastenequation
        for (int i = 0; i < (noPoints); i++) {
            if (revisedElevation[i] < 0) {
                revisedElevation[i] = 0;
            } else if (revisedElevation[i] > 90) {
                revisedElevation[i] = 90;
            } else {
                revisedElevation[i] = revisedElevation[i];
            }

            Se[i] = Math.sin((revisedElevation[i] / 57.29578));
            Pt[i] = Math.exp((Math.log(revisedElevation[i] + (6.07995))) * (-1.6364));

            airmass[i] = (1 / (Se[i] + (0.50572 * Pt[i])));

        }
        return airmass;
    }	// end CalculateAirmass				

//-------------------------------------------------------------------
    public Regression[] ClassicalLangley() {
        /* the classical Langley Calibration	
         */

        //bring it into a format compatible to the regression method		
        double[] Airmass = new double[numberOfPoints];
        double[] Absorbtion = new double[numberOfPoints];

        // now do the real langley Calibration
        Regression[] RegData = new Regression[numberOfChannels];

        absorbtionFilter = new boolean[noPoints][numberOfChannels];
        absorbtionData = new double[noPoints][numberOfChannels];

        // filter NaNs and sensless data first
        int ThisRegCount;
        for (int i = 0; i < numberOfChannels; i++) {
            ThisRegCount = 0;
            for (int j = 0; j < noPoints; j++) {
                absorbtionData[j][i] = Math.log(thisLevel2File.BodyData[j + indexData[0]][(i + dataIndex)] / thisLevel2File.calibrat.numericalData[i]);
                if ((!(Double.isNaN(absorbtionData[j][i]))) && (absorbtionData[j][i] > (-100000)) && filterMask[j]) {
                    absorbtionFilter[j][i] = true;
                    Absorbtion[ThisRegCount] = absorbtionData[j][i];
                    Airmass[ThisRegCount] = airMass[j];
                    ThisRegCount++;
                } else {
                    absorbtionFilter[j][i] = false;
                }
            }

            //construct the arrays needed for regression
            double[] RegAirmass = new double[ThisRegCount];
            double[] RegAbsorbtion = new double[ThisRegCount];
            for (int j = 0; j < ThisRegCount; j++) {
                RegAbsorbtion[j] = Absorbtion[j];
                RegAirmass[j] = Airmass[j];
            }

            // here there comes the true langley regression					
            Regression ThisReg = new Regression();
            ThisReg.newFilteredRegression(RegAirmass, RegAbsorbtion);
            RegData[i] = ThisReg;
        }

        // resize the array , get rid of unnecessary data
        return RegData;
    }	// end ClassicalLangley

//-------------------------------------------------------------------
    public double[] CalculateOzoneAirmass() {
        /*			Ozon airmass after W.D.Komhyr (1989)
				elv: solar elevation [deg];
				hp: O3 peak height [km];
				r: station height a.s.l [km]

			Correction function 
			needed: sorted data (for the elevation)
					headerdata (for the altitude and the latitude)
			
			this method is only needed for the refined langley Calibration
         */

        double[] sza = new double[noPoints];
        double[] MassOfOzone = new double[noPoints];

        double EarthRadius = 6371.229;
        // interpolate OzoneLayerheight from Komhyr's table
        double OzoneLayerHeight = 26 - (Math.abs(thisLevel2File.latitude.numericalData[0]) * 9 / 90); //latitude correction

        double EROLH = EarthRadius + OzoneLayerHeight;
        double ERSH = EarthRadius + (thisLevel2File.altitude.numericalData[0] / 1000); //=(EarthRadius + Altitude of STation)	

        for (int i = 0; i < noPoints; i++) {
            sza[i] = 90 - revisedElevation[i];
            MassOfOzone[i] = (EROLH) / (Math.sqrt((EROLH * EROLH) - ((ERSH * ERSH) * (Math.sin(sza[i] / 57.29578)) * (Math.sin(sza[i] / 57.29578)))));
        }
        return MassOfOzone;

        /*	                  OPERATIONS HANDBOOK 
				- OZONE OBSERVATIONS WITH A DOBSON SPECTROPHOTOMETER 
				- W. D. Komhyr June, 1980 (NOAA)
				
				calculation of mu                                                   
				           R + h           
				  mu =  ---------------------------                                                         (10)
				        sqrt((R+h)2 - (R+r)2 sin2 Z)
				
				  R = mean earth radius (6371.229 km);
				  r = height of the station
				  h = height of the ozone layer
				  Z = solar zenith angle.
				
				Latitude   Height h
				Deg           Km
				 �  0         26
				 � 10         25
				 � 20         24
				 � 30         23
				 � 40         22
				 � 50         21
				 � 60         20
				 � 70         19
				 � 80         18
				 � 90         17	
         */
    }	// end CalculateOzoneAirmass

//-----------------------------------------------------------------------------------
    public double[] RayleighCalculation() {
        /*	RAYLEIGH3 optical depth
					tau = rayleigh3(wavelength)
					wavelength in nm;  (wavelength)    
					Ref: B.Bodhaine et al. J.Atm. and Ocean. Tech., 16, 1854-1861 (1999)
				

					HeaderData needed::
					Altitude, latitue, Wavelengths and then some messy calculations
				
					this method is only needed for the refined langley calibration
         */

        double[] tr = new double[numberOfChannels];
        double CarbonDioxideConcentration = 360; //ppm assumed CO2 conc.
        double Latitude = thisLevel2File.latitude.numericalData[0];
        double Altitude = thisLevel2File.altitude.numericalData[0];
        double[] Wavelength = theWaveLengths;

        for (int i = 0; i < numberOfChannels; i++) {
            Wavelength[i] = thisLevel2File.wavelength.numericalData[i];
        }

        double c2fi = Math.cos(Latitude / 28.64789);	// corresponds to cos(2 phi)	
        double Gravity0 = (980.616 * (1 - ((2.6373e-3) * c2fi) + ((5.9e-6) * c2fi * c2fi)));
        // mass weighted mean height of US std.atmosphere as a function of geometric height
        double z = ((0.73737 * Altitude) + 5517.56);
        double Gravity = Gravity0 - (z * ((3.085462e-4) + ((2.27e-7) * c2fi))) + (((7.254e-11) + ((1e-13) * c2fi)) * z * z) - (((1.517e-17) + ((6e-20) * c2fi)) * z * z * z);

        // calculate refractive index of dry air at 1013.25hPa, 288.15K 
        // and 300pm CO2 after Peck and Reeder (1972)
        double[] VolumeRatio = {78.084, 20.946, 0.934, (CarbonDioxideConcentration * 1.e-4)}; //(N2, O2, Ar, CO2)

        for (int i = 0; i < numberOfChannels; i++) {
            double InvWav2 = (1e6) / (Wavelength[i] * Wavelength[i]); //inverse WL^2 in microns
            // scale to assumed CO2 (360ppm) as given by Edl�n (1966)	
            double NAir = (8060.51 + (2480990 / (132.274 - InvWav2)) + (17455.7 / (39.32957 - InvWav2)));
            NAir = (1 + ((1e-8) * NAir * (1 + ((0.54e-6) * (CarbonDioxideConcentration - 300)))));
            double nm1 = (((NAir * NAir) - 1) * ((NAir * NAir) - 1));
            double np2 = (((NAir * NAir) + 2) * ((NAir * NAir) + 2));
            //calculate King factor using Bates (1984) depolarization
            double n2 = (1.034 + ((3.17e-4) * InvWav2));	//N2
            double o2 = (1.096 + ((1.385e-3) * InvWav2) + ((1.448e-4) * InvWav2 * InvWav2)); //O2
            double Ar = 1; 		// Argon is isotropic
            double co2 = 1.15;		// constant for CO2
            double Kingfactor = ((n2 * VolumeRatio[0]) + (o2 * VolumeRatio[1]) + (Ar * VolumeRatio[2]) + (co2 * VolumeRatio[3]));
            Kingfactor = Kingfactor / 100;

            double Avogadro = 6.0221367e23; 		// Avogadro's Number
            double MolVol = 22414.1;				// Molar Volume in cm^3
            double MolecPerVol = (Avogadro / MolVol) * (273.15 / 288.15);
            //Molecular scattering cross section in cm^2 per molecule
            double InvWav4 = (1e28 / (Wavelength[i] * Wavelength[i] * Wavelength[i] * Wavelength[i]));
            double sgm = ((744.15064 * InvWav4 * nm1 * Kingfactor) / (MolecPerVol * MolecPerVol * np2));
            //mean molecular weight								
            double MolecWeight = (28.9595 + ((1.50556e-5) * CarbonDioxideConcentration));
            tr[i] = ((sgm * 1013250 * Avogadro) / (MolecWeight * Gravity));
        }

        return tr;

    }	// end RayleighCalculation
//--------------------------------------------------------------------------------

    double[][] RayleighCoeff() {	// choose appropriate Optical Depths and calculate the raleighCoefficients (scattering) for every DataPoint

        double[][] Tr = new double[noPoints][numberOfChannels];

        // if the rayleighoptdepths are given in the header use them , other wise calculate them
        double RayleighSum = 0;
        if (thisLevel2File.raylscatt.exists) {
            for (int i = 1; i < (thisLevel2File.raylscatt.numericalData.length); i++) {
                RayleighSum = RayleighSum + thisLevel2File.raylscatt.numericalData[i];
            }
        }

        if (RayleighSum != 0) {
            Rayleigh = thisLevel2File.raylscatt.numericalData;
            for (int i = 0; i < noPoints; i++) {
                for (int j = 0; j < numberOfChannels; j++) {
                    Tr[i][j] = ((thisLevel2File.BodyData[i][2] / 1013) * thisLevel2File.raylscatt.numericalData[j]);
                }
            }
        } else {
            Rayleigh = RayleighCalculation();
            RayleighFlag = true;
            for (int i = 0; i < noPoints; i++) {
                for (int j = 0; j < numberOfChannels; j++) {
                    Tr[i][j] = ((thisLevel2File.BodyData[i][2] / 1013) * Rayleigh[j]);
                }

            }
        }
        return Tr;
    }	// RayleighCoeff

//--------------------------------------------------------------------------------
    public Regression[] RefinedLangley(double[] TheMeteoData) {		// refined Langley method (basically the same as the classical just some corrections are applied.

        // method call to get the rayleighCoefficients
        double[][] Tr = RayleighCoeff();

        //Calculation of a variable to quantify the Absorbtion due to Ozone		
        double[] To = new double[numberOfChannels];
        for (int i = 0; i < numberOfChannels; i++) {
            To[i] = thisLevel2File.o3abs.numericalData[i] * TheMeteoData[IndexOfOzone] / 1000;
        }

        //Calcualtate a corresponding Ozone Airmass
        double[] OzoneAirMass = CalculateOzoneAirmass();

        //Water-Vapour Airmass according to Kasten Arch.Meteo.Geophys.Bioklim Ser.B, Bd.14, 206-223 (1966)
        // as approximation for aerosol air mass
        waterVapourAirmass = new double[noPoints];
        for (int k = 0; k < noPoints; k++) {
            waterVapourAirmass[k] = 1 / (Math.sin((revisedElevation[k] / 57.29578)) + (0.0548 * Math.exp((-1.452 * Math.log(revisedElevation[k] + 2.65)))));
        }

        //bring it into a format compatible to the regression method		
        absorbtionFilter = new boolean[noPoints][numberOfChannels];

        Regression[] RegData = new Regression[numberOfChannels];
        absorbtionData = new double[noPoints][numberOfChannels];

        for (int i = 0; i < noPoints; i++) {
            for (int j = 0; j < numberOfChannels; j++) {
                absorbtionData[i][j] = (thisLevel2File.BodyData[i + indexData[0]][j + dataIndex]) * (Math.exp(((airMass[i] * Tr[i][j]) + (OzoneAirMass[i] * To[j]))));
            }
        }

        double[] Absorbtion = new double[numberOfPoints];
        double[] helpWaterVapourAirmass = new double[numberOfPoints];

        int ThisRegCount;
        for (int i = 0; i < numberOfChannels; i++) {
            ThisRegCount = 0;

            // filter NaN's and senseless values
            for (int j = 0; j < noPoints; j++) {
                absorbtionData[j][i] = Math.log(absorbtionData[j][i] / thisLevel2File.calibrat.numericalData[i]);
                if (!(Double.isNaN(absorbtionData[j][i])) && (absorbtionData[j][i] > (-100000)) && filterMask[j]) {
                    Absorbtion[ThisRegCount] = absorbtionData[j][i];
                    helpWaterVapourAirmass[ThisRegCount] = waterVapourAirmass[j];
                    absorbtionFilter[j][i] = true;
                    ThisRegCount++;
                } else {
                    absorbtionFilter[j][i] = false;
                }
            }

            //Reduce Size of Arrays
            double[] RegAbsorbtion = new double[ThisRegCount];
            double[] RegWaterVapourAirmass = new double[ThisRegCount];

            for (int j = 0; j < ThisRegCount; j++) {
                RegAbsorbtion[j] = Absorbtion[j];
                RegWaterVapourAirmass[j] = helpWaterVapourAirmass[j];
            }

            Regression ThisReg = new Regression();
            ThisReg.newFilteredRegression(RegWaterVapourAirmass, RegAbsorbtion);
            RegData[i] = ThisReg;

            /*		OLD/Original code involve a filter first with airmass, then with WaterVapourAirmass,
				the filter which is used now just filters over WaterVapourAirmass...
		
				Regression FirstReg = new Regression();
				FirstReg.Standard(RegWaterVapourAirmass,RegAbsorbtion);
				
				int FilterCount=0;
								
				for (int j=0; j<ThisRegCount;j++)
				{	if (Math.abs(RegAbsorbtion[j]-(RegWaterVapourAirmass[j]*FirstReg.Slope + FirstReg.Intercept))<(2*FirstReg.StandardSY))
					{	RegWaterVapourAirmass[FilterCount] = RegWaterVapourAirmass[j];
						RegAbsorbtion[FilterCount] = RegAbsorbtion[j];

					FilterCount++;
					}	
				}
			
				// Reduce Size of Arrays and assign data for further processing
				double[] RegWaterVapourAirmassNew = new double[FilterCount];
				double[] RegAbsorbtionNew = new double[FilterCount];
			
				for (int j=0; j<FilterCount; j++)
				{	RegWaterVapourAirmassNew[j]=RegWaterVapourAirmass[j];	
					RegAbsorbtionNew[j]=RegAbsorbtion[j]; 
				}
				
				Regression ThisReg = new Regression();
				ThisReg.Standard(RegWaterVapourAirmassNew, RegAbsorbtionNew);
				
				RegData[i]=ThisReg;
             */
        }
        airMass = waterVapourAirmass;

        return RegData;
    }	// 	end RefinedLangley
//-------------------------------------------------------------------

    public int[] WhatIndexData(int DayTimeOption, int[] DayData) {
        /*	depending on the DayTimeOption, 
			this method chooses which time Range of the day to take
			
         */
        int[] IndexData = new int[2];
        switch (DayTimeOption) {
            case 0:								//use whole day
                IndexData[0] = (DayData[0] + 1);
                IndexData[1] = (DayData[2] - 1);
                break;
            case 1: 	 						//use morning only
                IndexData[0] = (DayData[0] + 1);
                IndexData[1] = (DayData[1]);
                break;
            case 2:								//use afternoon only
                IndexData[0] = DayData[1];
                IndexData[1] = (DayData[2] - 1);
                break;
            default:							//use whole day
                IndexData[0] = DayData[0];
                IndexData[1] = DayData[2];
                warnLog.addWarning("WARNING: invalid time range option");
        }
        return IndexData;
    }	// end WhatIndexData

//-------------------------------------------------------------------
}
