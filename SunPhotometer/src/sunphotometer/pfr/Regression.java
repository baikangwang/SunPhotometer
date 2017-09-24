package sunphotometer.pfr;

class Regression {

    double Slope = 0;
    double Intercept = 0;
    double R = 0;
    double[] XAxis;
    double[] YAxis;
    double StandardSY; 		//Standarddeviation in y-direction of data from regression line
    double SigmaSlope;		//Standarddeviation or Slope
    double SigmaIntercept;	//Standarddeviation of Intercept

    /*	
		this class calculates some regression related values, which are stated above.
		it may easily be extended to calculate some more sophisticated approaches.
		please note that the definition of StandardSY might be a reason for discussion.
	
     */
    public Regression Standard(double[] XAxis, double[] YAxis) {
        Regression StandReg = new Regression();

        //mean Values
        double MeanX = 0;
        for (int i = 0; i < XAxis.length; i++) {
            MeanX = MeanX + XAxis[i];
        }
        MeanX = MeanX / XAxis.length;

        double MeanY = 0;
        for (int i = 0; i < YAxis.length; i++) {
            MeanY = MeanY + YAxis[i];
        }
        MeanY = MeanY / YAxis.length;

        //Standard Deviations
        double SigmaX2 = 0;
        for (int i = 0; i < XAxis.length; i++) {
            SigmaX2 = SigmaX2 + ((MeanX - XAxis[i]) * (MeanX - XAxis[i]));
        }
        SigmaX2 = SigmaX2 / (XAxis.length - 1);

        double SigmaY2 = 0;
        for (int i = 0; i < YAxis.length; i++) {
            SigmaY2 = SigmaY2 + ((MeanY - YAxis[i]) * (MeanY - YAxis[i]));
        }
        SigmaY2 = SigmaY2 / (YAxis.length - 1);

        //Covariance
        double CovarianceXY = 0;
        for (int i = 0; i < XAxis.length; i++) {
            CovarianceXY = CovarianceXY + ((XAxis[i] - MeanX) * (YAxis[i] - MeanY));
        }
        CovarianceXY = CovarianceXY / (YAxis.length - 1);

        //	this.R=(CovarianceXY)/(Math.sqrt(SigmaY2*SigmaX2));  not sure whether this is the same...
        {
            double SumXY = 0;
            double SumX = 0;
            double SumY = 0;
            double SumXX = 0;
            double SumYY = 0;
            for (int i = 0; i < XAxis.length; i++) {
                SumXY = SumXY + XAxis[i] * YAxis[i];
                SumXX = SumXX + XAxis[i] * XAxis[i];
                SumYY = SumYY + YAxis[i] * YAxis[i];
                SumX = SumX + XAxis[i];
                SumY = SumY + YAxis[i];
            }
            this.R = (XAxis.length * SumXY - SumX * SumY) / (Math.sqrt((XAxis.length * SumXX - SumX * SumX) * (XAxis.length * SumYY - SumY * SumY)));
        }

        this.Slope = (CovarianceXY / SigmaX2);
        this.Intercept = MeanY - (this.Slope * MeanX);

        //now calculate standard deviation from regression line
        // = mean residue-squares
        double StandardSY2 = 0;
        for (int i = 0; i < XAxis.length; i++) {
            StandardSY2 = StandardSY2 + ((YAxis[i] - (XAxis[i] * Slope + Intercept)) * (YAxis[i] - (XAxis[i] * Slope + Intercept)));
        }
        StandardSY2 = StandardSY2 / (XAxis.length - 1);

        double StandardSY = Math.sqrt(StandardSY2);

        //not to sure about that, that's just for fun, no relevance
        double sa = Math.sqrt((1 / (XAxis.length)) + (MeanX * MeanX / ((XAxis.length - 1) * SigmaX2)));
        double sb = Math.sqrt(1 / ((XAxis.length - 1) * SigmaX2));

//		System.out.println(StandardSY);
        this.R = R;
        this.Slope = Slope;
        this.Intercept = Intercept;
        this.XAxis = XAxis;
        this.YAxis = YAxis;
        this.StandardSY = StandardSY;
        this.SigmaIntercept = sa * StandardSY;
        this.SigmaSlope = sb * StandardSY;

        return this;
    }

//------------------------------------------------------------------------
    /*
			Filtered Regression means that basically the same is done as above.
			a simple regression is calculated but afterwards all the datapoints
			which are further away from the regression line than 2 standard deviations
			are filtered away in order to account for errors of measurement 
			therefore we obtain a filtered dataset, upon which a second regression is done.
     */
    public Regression FilteredRegression(double[] XAxis, double[] YAxis) {
        Regression FilterReg = new Regression();

        Regression PreliminaryStandard = FilterReg.Standard(XAxis, YAxis);

        int CountData = 0;

        for (int i = 0; i < XAxis.length; i++) {
            if (Math.abs(YAxis[i] - (XAxis[i] * PreliminaryStandard.Slope + PreliminaryStandard.Intercept)) < (2 * PreliminaryStandard.StandardSY)) {
                XAxis[CountData] = XAxis[i];

                YAxis[CountData] = YAxis[i];
                CountData++;
            }
        }

        // Reduce Size of Arrays
        double[] XAxisNew = new double[CountData];
        double[] YAxisNew = new double[CountData];

        for (int i = 0; i < CountData; i++) {
            XAxisNew[i] = XAxis[i];
            YAxisNew[i] = YAxis[i];
        }

        Regression SecondaryReg = FilterReg.Standard(XAxisNew, YAxisNew);

        this.R = SecondaryReg.R;
        this.Slope = SecondaryReg.Slope;
        this.Intercept = SecondaryReg.Intercept;
        this.XAxis = SecondaryReg.XAxis;
        this.YAxis = SecondaryReg.YAxis;
        this.StandardSY = SecondaryReg.StandardSY;
        this.SigmaSlope = SecondaryReg.SigmaSlope;
        this.SigmaIntercept = SecondaryReg.SigmaIntercept;

        return this;
    }
//----------------------------------------------------------------------------------		

    /*
			New Filtered Regression means that basically the same is done as above.
			a simple regression is calculated but afterwards all the datapoints
			which are further away from the regression line than 1 standard deviations downwards or 3 stddev upwards 
			are filtered away in order to account for errors of measurement 
			then again those which are further away than 2 stddev
			therefore we obtain a filtered dataset, upon which a second regression is done.
     */
    public Regression newFilteredRegression(double[] XAxis, double[] YAxis) {
        Regression newFilterReg = new Regression();

        Regression PreliminaryStandard = newFilterReg.Standard(XAxis, YAxis);

        int CountData = 0;
        double theoValue;
        for (int i = 0; i < XAxis.length; i++) {
            theoValue = XAxis[i] * PreliminaryStandard.Slope + PreliminaryStandard.Intercept;
            if ((YAxis[i] - theoValue) < (3 * PreliminaryStandard.StandardSY) && (theoValue - YAxis[i]) < (1.5 * PreliminaryStandard.StandardSY)) {
                XAxis[CountData] = XAxis[i];
                YAxis[CountData] = YAxis[i];
                CountData++;
            }
        }

        // Reduce Size of Arrays
        double[] XAxisNew = new double[CountData];
        double[] YAxisNew = new double[CountData];

        for (int i = 0; i < CountData; i++) {
            XAxisNew[i] = XAxis[i];
            YAxisNew[i] = YAxis[i];
        }

        Regression SecondaryStandard = newFilterReg.Standard(XAxisNew, YAxisNew);

        CountData = 0;

        for (int i = 0; i < XAxisNew.length; i++) {
            if (Math.abs(YAxisNew[i] - (XAxisNew[i] * SecondaryStandard.Slope + SecondaryStandard.Intercept)) < (2 * SecondaryStandard.StandardSY)) {
                XAxisNew[CountData] = XAxisNew[i];
                YAxisNew[CountData] = YAxisNew[i];
                CountData++;
            }
        }

        // Reduce Size of Arrays
        double[] XAxisNew2 = new double[CountData];
        double[] YAxisNew2 = new double[CountData];

        for (int i = 0; i < CountData; i++) {
            XAxisNew2[i] = XAxisNew[i];
            YAxisNew2[i] = YAxisNew[i];
        }

        Regression FinalReg = newFilterReg.Standard(XAxisNew2, YAxisNew2);

        this.R = FinalReg.R;
        this.Slope = FinalReg.Slope;
        this.Intercept = FinalReg.Intercept;
        this.XAxis = FinalReg.XAxis;
        this.YAxis = FinalReg.YAxis;
        this.StandardSY = FinalReg.StandardSY;
        this.SigmaSlope = FinalReg.SigmaSlope;
        this.SigmaIntercept = FinalReg.SigmaIntercept;

        return this;
    }

}
