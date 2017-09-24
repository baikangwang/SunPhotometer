package sunphotometer.pfr;

public class AerosolOpticalDepth
	
{	/*
		requires the following classes to run
		LangleyExtrapol.class
		Regression.class		

		all subfunctions managed by _theMain_	method
	*/
	
	int AODOption =1;	
	int TimeRangeOption =1;
	ReadLevel2File thisLevel2File;	
	WarningLogger warnLog = new WarningLogger();	

	int NumberOfChannels;
	int[] SEL = {368,412,500,675,778,862};
	int AmountOfUsefulChannels;		//Amount of Channels on which the AOD was based.
	int IndexOfOzone =0;	
	int PressureIndex =2;
	int[] AvailableChannels;		// which channels are available...
	int[] indexData; 				// index of first value to take, index of last value to take
	int dataIndex=3;				// index of column where measurements data start in level2file
	int noPoints;					// number of points handled (determined through time range, and points in this time range)
	LangleyExtrapol justDoSomeCalc; // a helping object of langley extrapol, to retrieve some data
	boolean[] filterMask;			// true = taken for calc, false = not taken for calc
	double[] theMeteoData;			// array containing {ozone, pressure, humidity , temperature}
	
	double[][] allTau;
	double[] allTauTime;
	
	double[] allAlpha;
	double[] allBeta ; 			 	// Alpha and beta of every point
		
	
	
	double[][] tau;					//	Aerosol opticla depths [points][channels]	filtered
	double[] tauTime;				//	time at which these points occured		fileterd
	double[] alpha;					//	the corresponding Angstr�m Alpha	filtered
	double[] beta;					//	the corresponding Angstr�m Beta	 filtered values...
	
	
	double avgAlpha;				// Averaged Alpha
	double avgBeta;					// Averaged Beta
	
	
	double R2;						// PearsonCorrellation of the Regression through the avarage Tau
	
	double[] EV;					// The Elektircal potentials  of the equipement
	double[] mTau;					// Average Tau (for each channel )
	double[] stdDevTau;				// Standard deviations of these Taus.
	
//----------------------------------------------------------------------------
	public AerosolOpticalDepth(ReadLevel2File ThisLevel2File)
	{	thisLevel2File = ThisLevel2File;
		// check which channels are available
		NumberOfChannels=thisLevel2File.wavelength.numericalData.length;
		AvailableChannels = WhichChannels();
	}	// end constructor


//-----------------------------------------------------------------------------

	public int[] WhichChannels()
	{
		/*	Select channels within 3nm of World Meteorologic Organisation (WMO)
			aerosol wavelengths first all PFR channels then augmented from 12
			12 WMO assume standard PFR if just 4 channels are present
			store channels indices and check if there are at least 2 channels
			(this part may shorten for GAW-PFR only Routine)
	
			Searches the Wavelengths in the header of the level 2 File whether
			they match with predefined wavelengths. if so, the wavelengths in the
			header get assigned the value of the index of the corresponding Wavelength
			(which are stored in the array SEL) + 1 , if they are not found
			they are assigned a value 0, (therefore the +1 to be able to distinguish)
			the array with the Positions (basically the assigned values) is returned
			and the number of found Wavelengths is stored as a class variable.
		*/	

		int[] TheChannels=new int[NumberOfChannels];
		int UsefulChannels =0;

		for (int i=0; i<NumberOfChannels; i++)
		{	boolean WavLenNotFound=true;
			int j=0;
			while (j<SEL.length && WavLenNotFound) 
			{	if (Math.abs(SEL[j]-thisLevel2File.wavelength.numericalData[i])<3)
				{	WavLenNotFound = false;
					TheChannels[i]=j+1;
					UsefulChannels++;
				}
				j++;
			}
			if (WavLenNotFound) TheChannels[i]=0;
		}
		this.AmountOfUsefulChannels = UsefulChannels;
		return TheChannels;
	}	// end WhichChannels

//-------------------------------------------------------------------------------
		public double[][] CheckTheQuality()
		{	/* This method looks for 0 or negativ signals and replaces them by 0
				A warning is displayed
			*/
					
			int CountBadData = 0;
			double[][] signals = new double[noPoints][NumberOfChannels];
			
			for (int i=0;i<noPoints;i++)
			{	for (int j=0; j<(NumberOfChannels);j++)
				{	signals[i][j] = thisLevel2File.BodyData[i+indexData[0]][dataIndex+j];
					if (signals[i][j]<=0)
					{	CountBadData++;
						signals[i][j]=0;
					}
				}
			}
			if (CountBadData!=0)
			warnLog.addWarning("WARNING: negativ or zero signals found (in " + CountBadData + " cases)!");
			return signals;	
		}	//	end CheckTheQuality

//-------------------------------------------------------------------------------

	public  double[][] CalculateTau(double[] EV, double[] TheMeteoData, double[][] checkedSignals)		
	{	/*	the Aerosol Optical Depth (tau)
			is calculated...
		*/	
	
		double[][] prelimTau = new double[noPoints][NumberOfChannels];
			
		for (int i=1; i<noPoints;i++)
		{	for (int j=0;j<NumberOfChannels;j++)
			{	prelimTau[i][j] = - Math.log(checkedSignals[i][j]/EV[j]);
			}
		}

		double[][] Tr = justDoSomeCalc.RayleighCoeff();	// get the rayleigh coefficients as well


		//Calculation of a variable to quantify the Absorbtion due to Ozone		
				
			double[] PreliminaryTo = new double[NumberOfChannels];
			for (int i=0;i<NumberOfChannels;i++)
			{	PreliminaryTo[i]=thisLevel2File.o3abs.numericalData[i]*TheMeteoData[IndexOfOzone]/1000;
			}		
			double[] To = PreliminaryTo;


		//Calcualtate a corresponding Ozone Airmass
		//(again, the method defined in LangleyExtrapol.class is used...
									
			double[] OzoneAirMass = justDoSomeCalc.CalculateOzoneAirmass();
		
		//Water-Vapour Airmass according to Kasten Arch.Meteo.Geophys.Bioklim Ser.B, Bd.14, 206-223 (1966)
			double[] WaterVapourAirmass =  new double[noPoints];
			for (int k=0; k<noPoints; k++	)
			{	WaterVapourAirmass[k] = 1/(Math.sin((justDoSomeCalc.revisedElevation[k]/57.29578))+(0.0548 * Math.exp((-1.452*Math.log(justDoSomeCalc.revisedElevation[k]+2.65)))));  
			}
			
			double[][] Tau = new double[noPoints][NumberOfChannels];
			for (int i=0; i<noPoints;i++)
			{	for (int j=0;j<NumberOfChannels;j++)
				{	prelimTau[i][j]=((prelimTau[i][j] - Tr[i][j]*justDoSomeCalc.airMass[i]-To[j]*OzoneAirMass[i])/WaterVapourAirmass[i]);
				} 
			}
			return prelimTau;
	}	// end CalculateTau

//-------------------------------------------------------------------------------

	public double[] statisticsWithFilter(double[] Data)
	{	// generalised method to calculate some characteristic numbers
		// as the average and the standard deviation.
		// after a first calculation 
		// all datapoints lying away more than 2 stddev are neglected and the average is calculted again.

		double[] StatDat = calculateStatistics(Data);

		double Avg1Data = StatDat[0];
		double StdDevData=StatDat[1];
		
		double AvgData=0;	
		double Sigma2Data=0;
		int DataCount=0;


		// calculate new average but neglect points which are more than 2std away from other points
		for (int i=0;i<Data.length;i++)
		{	if ((!(Double.isNaN(Data[i]))) && (!(Double.isInfinite(Data[i]))) && filterMask[i] && ((Math.abs(Data[i]-Avg1Data))<(3*StdDevData)))
			{	AvgData = AvgData + Data[i];
				DataCount++;
			}
			else filterMask[i]=false;	
		}
		
		
		AvgData = AvgData/DataCount; //new average without filtered points
		
		double[] Results = {AvgData, StdDevData};
		return Results;
	}	// end StatisticsWithFilter


		
//-------------------------------------------------------------------------------


	public double[] calculateStatistics(double[] Data)
	{	// generalised method to calculate some characteristic numbers
		// as the average and the standard deviation.
		
		double Avg1Data=0;
		double Sigma2Data=0;
		double StdDevData=0;
		int DataCount=0;

		// calculate simple mean value
		for (int i=0;i<Data.length;i++)
		{	if ((!(Double.isNaN(Data[i]))) && (!(Double.isInfinite(Data[i]))) && filterMask[i])
			{	Avg1Data = Avg1Data + Data[i];
				DataCount++;
			}	
		}
		
		Avg1Data = Avg1Data/DataCount;
		DataCount=0;

		// calculate simple standard deviation
		for (int i=0;i<Data.length;i++)
		{	if ((!(Double.isNaN(Data[i]))) && (!(Double.isInfinite(Data[i])))  && filterMask[i])		
			{	Sigma2Data = Sigma2Data + (Data[i]-Avg1Data)*(Data[i]-Avg1Data);
				DataCount++;
			}	
		}	

		Sigma2Data = Sigma2Data/(DataCount-1);		
		StdDevData = Math.sqrt(Sigma2Data);
		DataCount=0;

		double[] Results = {Avg1Data, StdDevData};
		return Results;
	}	// end calculateStatistics


		
//-------------------------------------------------------------------------------


		public double[] WhichEVTake(LangleyExtrapol ThisLangley, double[] EVvalues)
		{
			double[] EV= new double[NumberOfChannels];
			// According to AODOption choose which values to take for V0

			switch (AODOption)
		{	case 0:								//use V0 From Level 2 File Header
				for (int i=0;i<NumberOfChannels;i++)
				EV[i] = thisLevel2File.calibrat.numericalData[i];
				break;
			case 1:								//apply linear drift in mv/day since last calibration here there is still an error
				ModifiedJulianDate MJD = new ModifiedJulianDate();
				int AmountOfDays = MJD.HowManyDays(((int)thisLevel2File.date.numericalData[0]),((int)thisLevel2File.date.numericalData[1]),((int)thisLevel2File.date.numericalData[2]),((int)thisLevel2File.caldate.numericalData[0]),((int)thisLevel2File.caldate.numericalData[1]),((int)thisLevel2File.caldate.numericalData[2]));
				for (int i=0;i<NumberOfChannels;i++)
				EV[i] = thisLevel2File.calibrat.numericalData[i]+(AmountOfDays*thisLevel2File.slope.numericalData[i]/1000);						
				break;
			case 2:								//use today's Langley values (from the part before)
				for (int i=0;i<NumberOfChannels;i++)
				EV[i] = ThisLangley.Calibrate[i];
				break;
			case 3:								//use given by calling the method
				for (int i=0;i<NumberOfChannels;i++)
				EV[i] = EVvalues[i];
				break;
			default:							//default use HeaderData of level 2 file
				for (int i=0;i<NumberOfChannels;i++)
				EV[i] = thisLevel2File.calibrat.numericalData[i];
				warnLog.addWarning("WARNING: invalid option for V_o");
			}
		return EV;
		}	// end WhichEVTake


//-------------------------------------------------------------------------------


 	public void TheMain(LangleyExtrapol ThisLangley, int[] DayData, double[] EVvalues, double[] TheMeteoData, int opt, int range)
	{	/*	
			opt = option determines, which V_0 (or EV) values to take, see whichEVvalues, for further detail.	
			LangleyExtrapol, a langley extrapolation, in which the first few steps of retrieving data were already done.
			range = over which time range shall the langley calibration be performed (0=whole day, 1= morning,2=afternoon)
			DayData = {IndexOfDawn,IndexOfNoon,IndexOfDusk} in bodydata...
			EVvalues = some V_0 values which may be chosen through an option...
			
			This is the main Method which manages all the other methods
		*/	
		// handle the options and set given stuff global...
			AODOption= opt;
			TimeRangeOption=range;	
			theMeteoData = TheMeteoData;	
		//Check which Data to use as V0
		double[] EV = WhichEVTake(ThisLangley,EVvalues);
		this.EV=EV;
			
		// prepare data for calculation of the Aerosol optical depths	
		// aerosol shall always be the whole day, therefore no coupling of with TimeRange, but 0
		justDoSomeCalc = new LangleyExtrapol(DayData,thisLevel2File,theMeteoData,0);
		indexData =justDoSomeCalc.indexData;
		noPoints = indexData[1]-indexData[0]+1;
	
		double[][] checkedSignals = CheckTheQuality();

		filterMask = justDoSomeCalc.filterMask;


	// calculate the aerosol optical depths
		allTau = CalculateTau(EV, TheMeteoData, checkedSignals);		

	// construct an array with the corresponding times
		double[] tauTime = new double[noPoints];
		allTauTime = new double[noPoints];
		for (int i=0;i<noPoints;i++)
		{	tauTime[i]=thisLevel2File.BodyData[i+indexData[0]][0];	
			allTauTime[i]=thisLevel2File.BodyData[i+indexData[0]][0];	
		}
				
	// helper arrays	
		double[][] LogTau = new double[noPoints][NumberOfChannels];
		allAlpha = new double[noPoints];
		allBeta  = new double[noPoints];
		double[] LogLambda = new double[NumberOfChannels];

		{	//numerical manipulation
			for (int i=0;i<NumberOfChannels;i++)
				{	LogLambda[i] = Math.log(thisLevel2File.wavelength.numericalData[i]/1000);
					for (int j=0;j<noPoints;j++)
						LogTau[j][i]=Math.log(allTau[j][i]);
				}
		
		
			// do calculate the angstr�m coefficients
			Regression[] RegData = new Regression[noPoints];
			for (int i=0; i<noPoints;i++)
			{//	if (filterMask[i])
				{	double[] RegLogTau = new double[NumberOfChannels];
					double[] RegLogLamda =  new double[NumberOfChannels];	
	
					for (int j=0;j<NumberOfChannels;j++)
					{	RegLogTau[j]=LogTau[i][j];
					}
					
					Regression ThisReg = new Regression();
					ThisReg.Standard(LogLambda,RegLogTau);	
					RegData[i]=ThisReg;
					allAlpha[i]=-ThisReg.Slope;
					allBeta[i]=Math.exp(ThisReg.Intercept);
				}
			}	
		}
		
		// now calculate averaged values for the arrays calculated above
		
		double StdDevAlpha;
		double StdDevBeta;
		double[] AvgTau= new double[NumberOfChannels];
		double[] StdDevTau = new double[NumberOfChannels];
		double[] StatisticsData;
				
		for (int k=0;k<NumberOfChannels;k++)
		{	double OneTau[] = new double[noPoints];
			for (int i=0;i<noPoints;i++)
			{	OneTau[i]=allTau[i][k];
			}
			StatisticsData = statisticsWithFilter(OneTau);
			AvgTau[k] = StatisticsData[0];
			StdDevTau[k]= StatisticsData[1];
		}
			
		StatisticsData = calculateStatistics(allAlpha);
		avgAlpha = StatisticsData[0];
		StdDevAlpha= StatisticsData[1];
		
		StatisticsData = calculateStatistics(allBeta);
		avgBeta= StatisticsData[0];
		StdDevBeta= StatisticsData[1];
		

// try to filter out the data which wasn't considered for calculating the average either		
		
		double[] helpAlpha = new double[noPoints];
		double[] helpBeta = new double[noPoints];
		double[][] helpTau = new double[noPoints][NumberOfChannels];
		double[] helpTauTime = new double[noPoints];
		
		
		int filterCount = 0;
		boolean filterTrigger;
		for (int i=0;i< tauTime.length;i++)
		{	filterTrigger = true;
			if (!filterMask[i] && Math.abs(allAlpha[i]-avgAlpha)<(2*StdDevAlpha)) filterTrigger=false;
			helpAlpha[filterCount]=allAlpha[i];
			if (!filterMask[i] && Math.abs(allBeta[i]-avgBeta)<(2*StdDevBeta)) filterTrigger=false;
			helpBeta[filterCount]=allBeta[i];
			for (int j=0;j<AmountOfUsefulChannels;j++)
			{	if (!filterMask[i] && Math.abs(allTau[i][j]-AvgTau[j])<(2*StdDevTau[j])) filterTrigger=false;
				helpTau[filterCount][j] = allTau[i][j];
			}
			helpTauTime[filterCount]=tauTime[i];
			if (filterTrigger) 
			{	filterCount++;
			}				
		}		
		
//		resize arrays prepare for plotting and other stuff

		double[] finalAlpha = new double[filterCount];
		double[] finalBeta = new double[filterCount];
		double[][] finalTau = new double[filterCount][NumberOfChannels]; 
		double[] finalTauTime = new double[filterCount];
		
		for (int i=0; i<filterCount ;i++)
		{	finalAlpha[i]=helpAlpha[i];
			finalBeta[i]=helpBeta[i];
			finalTauTime[i]=helpTauTime[i];
			for (int j=0;j<NumberOfChannels;j++)  
			{	finalTau[i][j] = helpTau[i][j];
			}
		}
		
		
	// return values for plotting and results		
		this.mTau=AvgTau;
		this.stdDevTau = StdDevTau;
		this.tauTime = finalTauTime;
		this.alpha = finalAlpha;
		this.beta = finalBeta;	
		this.tau = finalTau;

		// what probably should be done is to filter for useful channels, has not been done,
		// it has only been checked for some channels...

		
				double[] RegLogTau = new double[NumberOfChannels];
	
				for (int j=0;j<NumberOfChannels;j++)
				{	RegLogTau[j]=Math.log(AvgTau[j]);	
				}					
				
				Regression AvgReg = new Regression();
				AvgReg.Standard(LogLambda,RegLogTau);	
				double otherAvgAlpha = -AvgReg.Slope;
				double otherAvgBeta = Math.exp(AvgReg.Intercept);
				this.R2 = AvgReg.R*AvgReg.R;


		}			//end theMain




//==================================================================


}