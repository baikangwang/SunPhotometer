package sunphotometer.pfr;

public class ModifiedJulianDate {

    /*	
		
	#####################################################################	
	#																	#		
	#	Please note:													#
	#																	#
	#	Programmed by Urs Zimmerli, Birkenhof, 8872 Weesen, Switzerland	#
	#	mail:						urs@zimmer.li						#
	#																	#
	#####################################################################	


	ModifiedJulianDate: Methods 		double WhatInModifiedJulian(int Year,int Month,int Day,int Hour)
											Calculates how many days elapsed since 17.11.1858
										
										int WhatInModifiedJulian(int Year,int Month,int Day)
	%%%%%%%%%%%%%%%%%%%%%%%%%%%%%			Calculates how many days elapsed since 17.11.1858
	% Note: 					%				
	%  Difference between		%		double HowManyDays(int Year1,int Month1,int Day1,int Hour1,int Year2,int Month2,int Day2,int Hour2)
	%  Julian and 				%			Calculates how many days elapsed between the two mentioned days
	%  Modified Julian is		%							
	%  Jul = ModJul + 2400000.5	%		int HowManyDays(int Year1,int Month1,int Day1,int Year2,int Month2,int Day2)
	%							%			Calculates how many days elapsed between the two mentioned dates
	%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
										double[] WhatInCalendar(double ModifiedJulianDate)	
											Given A ModifiedJulianDate, calculates back to true Calendary Dates:
											Output: Array{ Year, Month, Day, Hour}
										
										main: is just a test of the whole shit
	
	
	
	Formulas According to: 	Astronomie mit dem Personal Computer
							Oliver Montenbruck & Thomas Pfleger
							Zweite Auflage
							Springer Verlag Berlin Heidelberg 1994
							ISBN 3-540-57701-7
							Page 11 ff.									
     */
//-----------------------------------------------------------------------------
    public double WhatInModifiedJulian(int Year, int Month, int Day, double Hour) {
        /*
				convert a normal date into Julian date (with given hour as 0-24 if minutes specified as decimal)
         */
        double A = ((Year * 10000) + (Month * 100) + Day);
        int B;
        if (Month < 3) {
            Month = Month + 12;
            Year = Year - 1;
        }
        if (A < 15821004.1) {
            B = ((Year + 4716) / 4) - 1181;
        } else {
            B = ((int) (Year / 400)) - ((int) (Year / 100)) + ((int) (Year / 4));
        }
        A = (365 * Year) - 679004;
        double MJD = A + B + ((int) (30.6001 * (Month + 1))) + Day + ((int) (Hour / 24));
        return MJD;
    }

//-------------------------------------------------------------------------
    public int WhatInModifiedJulian(int Year, int Month, int Day) {	// same as above if no hour is specified
        double Hour = 0;
        double MJD = WhatInModifiedJulian(Year, Month, Day, Hour);
        int MJDint = (int) MJD;
        return MJDint;
    }

//--------------------------------------------------------------------------		
    public double HowManyDays(int Year1, int Month1, int Day1, double Hour1, int Year2, int Month2, int Day2, double Hour2) {	//calculate difference between two days (if hour is specified)
        double MJD1 = WhatInModifiedJulian(Year1, Month1, Day1, Hour1);
        double MJD2 = WhatInModifiedJulian(Year2, Month2, Day2, Hour2);
        double MJDDiff = Math.abs(MJD1 - MJD2);
        return MJDDiff;
    }

//--------------------------------------------------------------------------
    public int HowManyDays(int Year1, int Month1, int Day1, int Year2, int Month2, int Day2) {	// calculate difference between two days if no hour specified
        double MJD1 = WhatInModifiedJulian(Year1, Month1, Day1);
        double MJD2 = WhatInModifiedJulian(Year2, Month2, Day2);
        int MJDDiff = (int) Math.abs(MJD1 - MJD2);
        return MJDDiff;
    }

//---------------------------------------------------------------------------
    public double[] WhatInCalendar(double ModifiedJulianDate) {
        /* convert the julian date back to a normal date*/

        double JD = ModifiedJulianDate + 2400000.5;		//convert to Julian		
        int JDO = (int) (JD + 0.5);
        int C;
        if (JDO < 2299161.0) //check whether befor or after switch to gregorian...which introduced the whole mess
        {
            C = (JDO + 1524);
        } else {
            int B = (int) ((JDO - 1867216.25) / 36524.25);
            C = JDO + B - ((int) (B / 4)) + 1525;
        }
        int D = (int) ((C - 122.1) / 365.25);
        int E = (365 * D) + ((int) (D / 4));
        int F = (int) ((C - E) / 30.6001);
        int DAY = ((int) (C - E + 0.5)) - ((int) (30.6001 * F));
        int MONTH = F - 1 - (12 * ((int) (F / 14)));
        int YEAR = D - 4715 - ((int) ((7 + MONTH) / 10));
        double HOUR = 24.0 * (JD + 0.5 - JDO);

        double[] TheDate = {YEAR, MONTH, DAY, HOUR};
        return TheDate;
    }

//-------------------------------------------------------------------------
    public static void main(String[] arguments) {	// just a test version to show how it works....
        ModifiedJulianDate test = new ModifiedJulianDate();
        int Julian = test.WhatInModifiedJulian(2000, 4, 1);
        System.out.println(Julian);
        double[] MyDate = test.WhatInCalendar(((double) Julian));
        System.out.println(MyDate[0]);
        System.out.println(MyDate[1]);
        System.out.println(MyDate[2]);
        System.out.println(MyDate[3]);
    }

//===========================================================================
}
