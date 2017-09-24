package sunphotometer.pfr;

import java.io.File;

public class ReadLevel2File {	
    // requires the following classes
    // ConvertStringToNumbers.class
    // HeaderData.class
    // SplitFileIntoLines.class

    WarningLogger warnLog = new WarningLogger();

    int NumberOfLines;		// numbers of lines of the whole file
    String[] TheLines;		// all the lines in an array of strings
    int HeaderSize;			// numbers of lines of the header
    int BodyDataWidth;		// numbers of columes in the data body
    int BodyDataLength;		// numbers of lines in the data body
    double[][] BodyData;	// array of [BodyDataLength][BodyDataWidth] with the numbers in the body
    String[] HeaderWrittenData;	// all the data in the header (all the stuff behind the "=")
    String[] allTheKeyWords;// just all the 10 digits before the keysign;
    int TheDate;			// the date in the following form yyyymmdd as an integer
    File actualFile;

    //HeaderData objects containing the headerdata
    HeaderData latitude = new HeaderData(false);
    HeaderData longitude = new HeaderData(false);
    HeaderData altitude = new HeaderData(false);
    HeaderData zone = new HeaderData(false);
    HeaderData normpress = new HeaderData(false);
    HeaderData nchannels = new HeaderData(false);
    HeaderData wavelength = new HeaderData(false);
    HeaderData caldate = new HeaderData(false);
    HeaderData calibrat = new HeaderData(false);
    HeaderData slope = new HeaderData(false);
    HeaderData o3abs = new HeaderData(false);
    HeaderData raylscatt = new HeaderData(false);
    HeaderData nreadings = new HeaderData(false);
    HeaderData date = new HeaderData(false);
    HeaderData airtemp = new HeaderData(false);
    HeaderData airpress = new HeaderData(false);
    HeaderData ozone = new HeaderData(false);
    HeaderData sunearth = new HeaderData(false);
    HeaderData declinat = new HeaderData(false);
    HeaderData eqoftime = new HeaderData(false);
    HeaderData noon = new HeaderData(false);

    HeaderData station = new HeaderData(false);
    HeaderData statident = new HeaderData(false);
    HeaderData instrument = new HeaderData(false);
    HeaderData instident = new HeaderData(false);
    HeaderData logfile = new HeaderData(false);
    HeaderData comment = new HeaderData(false);
    HeaderData version = new HeaderData(false);

    /*-----------------------------------------------------------------
                    A few Methods
-----------------------------------------------------------------*/
//----------------------------------------------------------------
    String[] SplitTheString(String[] TheLines, String KeySign, int KeyWordLength) /* 
	Splits the Strings corresponding to the Header of the Level2file into two parts:
	The result is an array of doubled the length of the header:
	The first part are the 10-character-strings before the KeySign
	The second half of this array corresponds to everything behind the KeySign
	The order is the same as in the header.
	The END line is in the last field of the first half of the mentioned array
	The very last field of the array contains the string "empty"
	
	Problems:
	until the KeyWord end all the lines must contain a KeySign
	The String before the KeySign must at least have the length of KeyWordLength
	The KeyWordLength-long String just before the KeySign is handled as The KeyWord
	
	The line containing the end keyword can be of any size and form,
	the only requirement is that it must _not_ contain a KeySign 
	
	The KeySign can be anything, but it has to be taken care, that there is no
	KeySign in the substring before the true KeySign (afterwards doesn't matter)
	
	
	Required Input:
	An array which has to be examined,	
	A KeySign opun which the distinction may be made
     */ {
        boolean control = true;
        int ArrayLength = TheLines.length;
        int HeaderSize = 0;
        String[] StringSplit1 = new String[(2 * ArrayLength)];
        while (control) {
            int KeyPosition = TheLines[HeaderSize].indexOf(KeySign);
            if ((KeyPosition == -1) || (HeaderSize == (ArrayLength - 1))) {
                control = false;
                StringSplit1[HeaderSize] = TheLines[HeaderSize];
                StringSplit1[(ArrayLength + HeaderSize)] = "empty";
                HeaderSize++;
            } else {
                StringSplit1[HeaderSize] = TheLines[HeaderSize].substring((KeyPosition - KeyWordLength), KeyPosition);
                StringSplit1[(ArrayLength + HeaderSize)] = TheLines[HeaderSize].substring((KeyPosition + 1));
                HeaderSize++;
            }
        }

        //check if it was really the end
        String CheckEnd = StringSplit1[(HeaderSize - 1)].toUpperCase();
        int CheckIfEnd = CheckEnd.indexOf("END");
        if (CheckIfEnd == -1) {
            warnLog.addWarning("Error while determining structure of Level 2 file.");
        }

        //create new, compacter array
        String[] StringSplit = new String[(2 * HeaderSize)];
        for (int i = 0; i < (2 * HeaderSize); i++) {
            if (i < HeaderSize) {
                StringSplit[i] = StringSplit1[i];
            } else {
                StringSplit[i] = StringSplit1[(ArrayLength + i - HeaderSize)];
            }
        }
        return StringSplit;
    }	// end SplitTheString
//-----------------------------------------------------------------

    void GetTheHeaderSorted(String[] TheLines, int NumberOfLines) /*	spits the header in the part before and after the "="
		identifies the keywords with the sort the keyword method 
		cuts away comments after the data in the header
		and produces an array indicating the order of the keywords (just to find them easier for further reference);
		and an array containing the numerical header data
     */ {
        String KeySign = "=";
        int KeyWordLength = 10;
        String SplittedString[] = SplitTheString(TheLines, KeySign, KeyWordLength);

        this.HeaderSize = SplittedString.length / 2;
        String[] KeyWords = new String[HeaderSize];
        String[] ArgForKeyWord = new String[HeaderSize];
        allTheKeyWords = new String[HeaderSize];

        for (int i = 0; i < (HeaderSize); i++) {
            allTheKeyWords[i] = SplittedString[i];
            KeyWords[i] = SplittedString[i].trim();
            ArgForKeyWord[i] = SplittedString[(i + HeaderSize)].trim();
        }

        this.HeaderWrittenData = ArgForKeyWord;

        // cut away comments...
        String commentSign = "%";
        for (int i = 0; i < ArgForKeyWord.length; i++) {
            int commentSignPosition = ArgForKeyWord[i].indexOf(KeySign);
            if (commentSignPosition != -1) {
                ArgForKeyWord[i].substring(0, commentSignPosition);
            }
        }

        ConvertStringToNumbers a = new ConvertStringToNumbers();

        for (int i = 0; i < (HeaderSize - 1); i++) {
            if (KeyWords[i].regionMatches(true, 0, "LATITUDE", 0, KeyWords[i].length())) {
                latitude.exists = true;
                latitude.writtenData = ArgForKeyWord[i];
                latitude.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "LONGITUDE", 0, KeyWords[i].length())) {
                longitude.exists = true;
                longitude.writtenData = ArgForKeyWord[i];
                longitude.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "ALTITUDE", 0, KeyWords[i].length())) {
                altitude.exists = true;
                altitude.writtenData = ArgForKeyWord[i];
                altitude.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "ZONE", 0, KeyWords[i].length())) {
                zone.exists = true;
                zone.writtenData = ArgForKeyWord[i];
                zone.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "NORMPRESS", 0, KeyWords[i].length())) {
                normpress.exists = true;
                normpress.writtenData = ArgForKeyWord[i];
                normpress.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "NCHANNELS", 0, 8)) {
                nchannels.exists = true;
                nchannels.writtenData = ArgForKeyWord[i];
                nchannels.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "WAVELENGTH", 0, KeyWords[i].length())) {
                wavelength.exists = true;
                wavelength.writtenData = ArgForKeyWord[i];
                wavelength.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "CALDATE", 0, KeyWords[i].length())) {
                caldate.exists = true;
                caldate.writtenData = ArgForKeyWord[i];
                caldate.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "CALIBRAT", 0, KeyWords[i].length())) {
                calibrat.exists = true;
                calibrat.writtenData = ArgForKeyWord[i];
                calibrat.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "SLOPE", 0, KeyWords[i].length())) {
                slope.exists = true;
                slope.writtenData = ArgForKeyWord[i];
                slope.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "O3ABS", 0, KeyWords[i].length())) {
                o3abs.exists = true;
                o3abs.writtenData = ArgForKeyWord[i];
                o3abs.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "RAYLSCATT", 0, KeyWords[i].length())) {
                raylscatt.exists = true;
                raylscatt.writtenData = ArgForKeyWord[i];
                raylscatt.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "NREADINGS", 0, KeyWords[i].length())) {
                nreadings.exists = true;
                nreadings.writtenData = ArgForKeyWord[i];
                nreadings.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "DATE", 0, KeyWords[i].length())) {
                date.exists = true;
                date.writtenData = ArgForKeyWord[i];
                date.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "AIRTEMP", 0, KeyWords[i].length())) {
                airtemp.exists = true;
                airtemp.writtenData = ArgForKeyWord[i];
                airtemp.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "AIRPRESS", 0, KeyWords[i].length())) {
                airpress.exists = true;
                airpress.writtenData = ArgForKeyWord[i];
                airpress.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "OZONE", 0, KeyWords[i].length())) {
                ozone.exists = true;
                ozone.writtenData = ArgForKeyWord[i];
                ozone.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "SUNEARTH", 0, KeyWords[i].length())) {
                sunearth.exists = true;
                sunearth.writtenData = ArgForKeyWord[i];
                sunearth.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "DECLINAT", 0, KeyWords[i].length())) {
                declinat.exists = true;
                declinat.writtenData = ArgForKeyWord[i];
                declinat.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "EQOFTIME", 0, KeyWords[i].length())) {
                eqoftime.exists = true;
                eqoftime.writtenData = ArgForKeyWord[i];
                eqoftime.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "NOON", 0, KeyWords[i].length())) {
                noon.exists = true;
                noon.writtenData = ArgForKeyWord[i];
                noon.numericalData = a.ConvertStringToDoubles(ArgForKeyWord[i]);
            } else if (KeyWords[i].regionMatches(true, 0, "STATION", 0, KeyWords[i].length())) {
                station.exists = true;
                station.writtenData = ArgForKeyWord[i].trim();
            } else if (KeyWords[i].regionMatches(true, 0, "STATIDENT", 0, KeyWords[i].length())) {
                statident.exists = true;
                statident.writtenData = ArgForKeyWord[i].trim();
            } else if (KeyWords[i].regionMatches(true, 0, "INSTRUMENT", 0, KeyWords[i].length())) {
                instrument.exists = true;
                instrument.writtenData = ArgForKeyWord[i].trim();
            } else if (KeyWords[i].regionMatches(true, 0, "INSTIDENT", 0, KeyWords[i].length())) {
                instident.exists = true;
                instident.writtenData = ArgForKeyWord[i].trim();
            } else if (KeyWords[i].regionMatches(true, 0, "LOGFILE", 0, KeyWords[i].length())) {
                logfile.exists = true;
                logfile.writtenData = ArgForKeyWord[i].trim();
            } else if (KeyWords[i].regionMatches(true, 0, "COMMENT", 0, KeyWords[i].length())) {
                comment.exists = true;
                comment.writtenData = ArgForKeyWord[i].trim();
            } else if (KeyWords[i].regionMatches(true, 0, "VERSION", 0, KeyWords[i].length())) {
                version.exists = true;
                version.writtenData = ArgForKeyWord[i].trim();
            }
        }

    }	// end GetHeaderSorted

//-----------------------------------------------------------------
    double[][] GetTheBodySorted(String[] TheLines, int HeaderSize, int NumberOfLines) {
        /*	
			this method sorts the body of the level 2 file
			output:		 double[bodydatalength][bodydatawidth]
			needs to know size of the header/the lines/and such stuff...	
         */
        {
            ConvertStringToNumbers b = new ConvertStringToNumbers();
            double[] HelpData2 = b.ConvertStringToDoubles(TheLines[HeaderSize]);
            int BodyDataWidth = HelpData2.length;
            this.BodyDataWidth = BodyDataWidth; //new
        }

        double[][] PreliminaryBodyData = new double[NumberOfLines - HeaderSize][BodyDataWidth];
        int BodyDataErrorCount = 0;
        // collect the data with the class ConvertStringTo numbers

        for (int i = 0; i < (NumberOfLines - HeaderSize); i++) {
            ConvertStringToNumbers b = new ConvertStringToNumbers();
            double[] HelpData2 = b.ConvertStringToDoubles(TheLines[(HeaderSize + i)]);
            if (HelpData2.length == BodyDataWidth) {
                for (int j = 0; j < (BodyDataWidth); j++) {
                    PreliminaryBodyData[i][j] = HelpData2[j];
                }
            } else if (i == (NumberOfLines - HeaderSize - 1)) {
                BodyDataErrorCount++;
            } else {
                warnLog.addWarning("WARNING: Inconsistancies in Datastructure where observed, not all the lines contained");
                warnLog.addWarning("the same amount of Numbers. The mentioned lines were ommitted.");
                BodyDataErrorCount++;
            }
        }
        //now just plug the whole crap into double[][] (therefore the collected Data must be resized )
        // return the values

        double[][] MyBodyData;
        int BodyDataLength = NumberOfLines - HeaderSize - BodyDataErrorCount;
        this.BodyDataLength = BodyDataLength;
        if (BodyDataErrorCount != 0) {
            double HelpBodyData[][] = new double[BodyDataLength][BodyDataWidth];
            for (int i = 0; i < (BodyDataLength); i++) {
                for (int j = 0; j < (BodyDataWidth); j++) {
                    HelpBodyData[i][j] = PreliminaryBodyData[i][j];
                }
            }
            MyBodyData = HelpBodyData;
            return MyBodyData;
        } else {
            MyBodyData = PreliminaryBodyData;
        }
        return MyBodyData;

    }	// end GetTheBodySorted		

//-----------------------------------------------------------------
    ReadLevel2File TheMain(File theFile) {
        actualFile = theFile;
        SplitFileIntoLines splitter = new SplitFileIntoLines();

        //find the Number of lines  in that file
        NumberOfLines = splitter.CountTheLines(theFile);

        //generate an array of strings and plug the lines of that file into that string
        TheLines = splitter.SplitIntoLines(theFile, NumberOfLines);

        this.GetTheHeaderSorted(this.TheLines, this.NumberOfLines);

        this.BodyData = this.GetTheBodySorted(this.TheLines, this.HeaderSize, this.NumberOfLines);

        // generate the date for returning...			
        int[] TheDate = new int[3];
        if (date.exists) {
            this.TheDate = (int) date.numericalData[0] * 10000 + (int) date.numericalData[1] * 100 + (int) date.numericalData[2];
        }
        return this;
    }	// end TheMain

//-----------------------------------------------------------------
}
