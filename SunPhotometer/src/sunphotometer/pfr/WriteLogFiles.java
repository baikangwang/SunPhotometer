package sunphotometer.pfr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteLogFiles {
//-------------------------------------------------------------------------------

    public void WriteAODFile(File fileName, ReadLevel2File thisLevel2File, AerosolOpticalDepth AOD) {
        boolean existsAlready = false;
        String[] existingLines = new String[1]; //this initialization is only necessary to compile...

        if (fileName.exists()) {
            existsAlready = true;
            SplitFileIntoLines splitter = new SplitFileIntoLines();
            existingLines = splitter.SplitIntoLines(fileName, splitter.CountTheLines(fileName));
        }

        try {
            FileWriter file = new FileWriter(fileName);
            BufferedWriter buff = new BufferedWriter(file);
            {
                if (existsAlready) {
                    for (int i = 0; i < existingLines.length; i++) {
                        buff.write(existingLines[i]);
                        buff.newLine();
                    }
                } else {	//Header Data first

                    buff.write("%Aerosol Optical Depth (AOD) History File for  ");
                    if (thisLevel2File.instrument.exists) {
                        buff.write(thisLevel2File.instrument.writtenData);
                    } else {
                        buff.write(" _INSTRUMENT NOT IDENTIFIED_ ");
                    }

                    buff.write(" at station ");
                    if (thisLevel2File.station.exists) {
                        buff.write(thisLevel2File.station.writtenData);
                    } else {
                        buff.write(" _STATION NOT IDENTIFIED_ ");
                    }

                    buff.newLine();

                    // now some descriptive data
                    buff.write("%Date     Opt  Press   Ozone    N     ");
                    for (int i = 0; i < AOD.AmountOfUsefulChannels; i++) {
                        buff.write(makeString(Double.toString(thisLevel2File.wavelength.numericalData[i]), 5));
                        buff.write("    ");
                    }

                    buff.write("Alpha    Beta   Pearson-R2");
                    buff.newLine();
                }

                buff.write(makeString(Integer.toString(thisLevel2File.TheDate), 8));
                buff.write("   ");
                buff.write(Integer.toString(AOD.TimeRangeOption));
                buff.write("   ");
                buff.write(makeString(Double.toString(AOD.theMeteoData[1]), 5));
                buff.write("   ");
                buff.write(makeString(Double.toString(AOD.theMeteoData[0]), 5));
                buff.write("   ");
                buff.write(makeString(Integer.toString(AOD.noPoints), 4));
                buff.write("   ");
                for (int i = 0; i < AOD.AmountOfUsefulChannels; i++) {
                    buff.write(makeString(Double.toString(AOD.mTau[i]), 7));
                    buff.write("  ");
                }
                buff.write(makeString(Double.toString(AOD.avgAlpha), 7));
                buff.write("  ");

                buff.write(makeString(Double.toString(AOD.avgBeta), 7));
                buff.write("  ");

                buff.write(makeString(Double.toString(AOD.R2), 7));
            }
            buff.flush();

            buff.close();
        } catch (IOException e) {
            System.out.println("WARNING: Error while attempting to write AOD results to file.");
        }
    }

//----------------------------------------------------------------------------
    public void WriteCALFile(File fileName, ReadLevel2File thisLevel2File, LangleyExtrapol CAL) {
        boolean existsAlready = false;

        String[] existingLines = new String[1]; //this initialization is only necessary to compile...
        if (fileName.exists()) {
            existsAlready = true;
            SplitFileIntoLines splitter = new SplitFileIntoLines();
            existingLines = splitter.SplitIntoLines(fileName, splitter.CountTheLines(fileName));
        }

        try {
            FileWriter file = new FileWriter(fileName);
            BufferedWriter buff = new BufferedWriter(file);

            {
                if (existsAlready) {
                    for (int i = 0; i < existingLines.length; i++) {
                        buff.write(existingLines[i]);
                        buff.newLine();
                    }
                } else {
                    //Header Data first

                    buff.write("%Langley History File for ");
                    if (thisLevel2File.instrument.exists) {
                        buff.write(thisLevel2File.instrument.writtenData);
                    } else {
                        buff.write(" _INSTRUMENT NOT IDENTIFIED_ ");
                    }

                    buff.write(" at station ");
                    if (thisLevel2File.station.exists) {
                        buff.write(thisLevel2File.station.writtenData);
                    } else {
                        buff.write(" _STATION NOT IDENTIFIED_ ");
                    }

                    buff.newLine();

                    // now some descriptive data
                    buff.write("%                                   |      exoatm. valuesEV         |           StdDevEV            |          LangleySlopes        |");
                    buff.newLine();
                    buff.write("%Date     M   O  AMlow  AMhigh  N     ");
                    for (int j = 0; j < 3; j++) {
                        for (int i = 0; i < CAL.numberOfChannels; i++) {
                            buff.write(makeString(Double.toString(CAL.theWaveLengths[i]), 5));
                            buff.write("   ");
                        }
                    }
                    buff.newLine();

                }

                buff.write(makeString(Integer.toString(thisLevel2File.TheDate), 8));
                buff.write("  ");

                if (CAL.method == 1) {
                    buff.write("2");
                } else {
                    buff.write("1"); //2=refined,1=classic
                }
                buff.write("   ");
                buff.write(Integer.toString(CAL.timeRange)); //0=wholeDay,1=morningOnly,2=afternoonOnly
                buff.write("  ");

                buff.write(makeString(Double.toString(CAL.minimumAirmass), 4));
                buff.write("   ");
                buff.write(makeString(Double.toString(CAL.maximumAirmass), 4));
                buff.write("    ");
                buff.write(makeString(Integer.toString(CAL.numberOfPoints), 4));
                buff.write("    ");

                for (int i = 0; i < CAL.numberOfChannels; i++) {
                    buff.write(makeString(Double.toString(CAL.Calibrate[i]), 5));
                    buff.write("   ");
                }
                for (int i = 0; i < CAL.numberOfChannels; i++) {
                    buff.write(makeString(Double.toString(CAL.CalibrateStdDev[i]), 6));
                    buff.write("  ");
                }
                for (int i = 0; i < CAL.numberOfChannels; i++) {
                    buff.write(makeString(Double.toString(CAL.calibrationSlope[i]), 6));
                    buff.write("  ");
                }

            }
            buff.flush();

            buff.close();
        } catch (IOException e) {
            System.out.println("WARNING: Error while attempting to write AOD results to file.");
        }
    }

//------------------------------------------------------------------------------------------------  
    public void WriteLOGFile(File fileName, ReadLevel2File thisLevel2File, AerosolOpticalDepth AOD, WarningLogger warnLog) {
        boolean existsAlready = false;

        String[] existingLines = new String[1]; //this initialization is only necessary to compile...
        if (fileName.exists()) {
            existsAlready = true;
            SplitFileIntoLines splitter = new SplitFileIntoLines();
            existingLines = splitter.SplitIntoLines(fileName, splitter.CountTheLines(fileName));
        }

        try {
            FileWriter file = new FileWriter(fileName);
            BufferedWriter buff = new BufferedWriter(file);

            {
                if (existsAlready) {
                    for (int i = 0; i < existingLines.length; i++) {
                        buff.write(existingLines[i]);
                        buff.newLine();
                    }
                } else {
                    buff.write("%LOG / History File for ");
                    if (thisLevel2File.instrument.exists) {
                        buff.write(thisLevel2File.instrument.writtenData);
                    } else {
                        buff.write(" _INSTRUMENT NOT IDENTIFIED_ ");
                    }

                    buff.write(" at station ");
                    if (thisLevel2File.station.exists) {
                        buff.write(thisLevel2File.station.writtenData);
                    } else {
                        buff.write(" _STATION NOT IDENTIFIED_ ");
                    }

                    buff.newLine();
                    buff.newLine();
                    buff.newLine();
                    buff.newLine();

                }

                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MM dd");
                String today = dateFormat.format(date);
                buff.write("AerosolOD:      " + today);
                buff.newLine();

                buff.write("Data Source:     " + thisLevel2File.actualFile.toString());
                buff.newLine();

                buff.write("Pressure = " + makeString(Double.toString(AOD.theMeteoData[1]), 5) + "   Ozone = " + makeString(Double.toString(AOD.theMeteoData[0]), 5) + "   N = " + Integer.toString(AOD.noPoints));
                buff.newLine();

                buff.write("ExtraAtm. Value: ");
                switch (AOD.AODOption) {
                    case 0:								//use V0 From Level 2 File Header	
                        buff.write("from Level 2 Header");
                        break;
                    case 1:								//apply linear drift in mv/day since last calibration here there is still an error
                        buff.write("from Level 2 Header with correction for linear drift");
                        break;
                    case 2:								//use today's Langley values (from the part before)
                        buff.write("today's Langley Calibration");
                        break;
                    case 3:								//use given by calling the method
                        buff.write("set by Hand");
                        break;
                    default:							//default use HeaderData of level 2 file
                        buff.write("from Level 2 Header");
                }
                buff.newLine();

                buff.write("Wavelengths      AOD     AOD-StdDev");
                buff.newLine();

                for (int i = 0; i < AOD.AmountOfUsefulChannels; i++) {
                    buff.write("    ");
                    buff.write(makeString(Double.toString(thisLevel2File.wavelength.numericalData[i]), 5));
                    buff.write("      ");
                    buff.write(makeString(Double.toString(AOD.mTau[i]), 7));
                    buff.write("      ");
                    buff.write(makeString(Double.toString(AOD.stdDevTau[i]), 7));
                    buff.newLine();
                }

                buff.write("Alpha:         " + makeString(Double.toString(AOD.avgAlpha), 7));
                buff.newLine();

                buff.write("Beta:          " + makeString(Double.toString(AOD.avgBeta), 7));
                buff.newLine();

                buff.write("Pearson-R2:    " + makeString(Double.toString(AOD.R2), 7));
                buff.newLine();

                String[] lines = warnLog.retrieve();
                if (lines.length > 1) {
                    for (int i = 1; i < lines.length; i++) {
                        buff.write(lines[i]);
                        buff.newLine();
                    }
                }

                buff.newLine();
                buff.newLine();
                buff.newLine();

            }
            buff.flush();

            buff.close();
        } catch (IOException e) {
            System.out.println("WARNING: Error while attempting to write AOD results to file.");
        }
    }
//-----------------------------------------------------------------------------------------------------------------

    public void WriteLOGFile(File fileName, ReadLevel2File thisLevel2File, LangleyExtrapol CAL, WarningLogger warnLog) {
        boolean existsAlready = false;

        String[] existingLines = new String[1]; //this initialization is only necessary to compile...
        if (fileName.exists()) {
            existsAlready = true;
            SplitFileIntoLines splitter = new SplitFileIntoLines();
            existingLines = splitter.SplitIntoLines(fileName, splitter.CountTheLines(fileName));
        }

        try {
            FileWriter file = new FileWriter(fileName);
            BufferedWriter buff = new BufferedWriter(file);

            {
                if (existsAlready) {
                    for (int i = 0; i < existingLines.length; i++) {
                        buff.write(existingLines[i]);
                        buff.newLine();
                    }
                } else {
                    buff.write("%LOG / History File for ");
                    if (thisLevel2File.instrument.exists) {
                        buff.write(thisLevel2File.instrument.writtenData);
                    } else {
                        buff.write(" _INSTRUMENT NOT IDENTIFIED_ ");
                    }

                    buff.write(" at station ");
                    if (thisLevel2File.station.exists) {
                        buff.write(thisLevel2File.station.writtenData);
                    } else {
                        buff.write(" _STATION NOT IDENTIFIED_ ");
                    }

                    buff.newLine();
                    buff.newLine();
                    buff.newLine();
                    buff.newLine();
                }

                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MM dd");
                String today = dateFormat.format(date);
                buff.write("LangleyCal:     " + today);
                buff.newLine();

                buff.write("Data Source:    " + thisLevel2File.actualFile.toString());
                buff.newLine();

                buff.write("Pressure = " + makeString(Double.toString(CAL.theMeteoData[1]), 5) + "   Ozone = " + makeString(Double.toString(CAL.theMeteoData[0]), 5) + "   N = " + Integer.toString(CAL.minPoints));
                buff.newLine();

                if (CAL.method == 0) {
                    buff.write("Method:     Classic");
                } else if (CAL.method == 1) {
                    buff.write("Method:     Refined");
                } else {
                    buff.write("Method:   	Could not be identified");
                }

                buff.newLine();

                buff.write("Wavelengths     V1       dV1     Slope    Ratio    ");
                buff.newLine();

                for (int i = 0; i < CAL.numberOfChannels; i++) {
                    buff.write("    ");
                    buff.write(makeString(Double.toString(CAL.theWaveLengths[i]), 5));
                    buff.write("     ");
                    buff.write(makeString(Double.toString(CAL.Calibrate[i]), 5));
                    buff.write("   ");
                    buff.write(makeString(Double.toString(CAL.CalibrateStdDev[i]), 6));
                    buff.write("  ");
                    buff.write(makeString(Double.toString(CAL.calibrationSlope[i]), 6));
                    buff.write("  ");
                    buff.write(makeString(Double.toString(CAL.Ratios[i]), 6));
                    buff.write("  ");
                    buff.newLine();
                }

                String[] lines = warnLog.retrieve();
                if (lines.length > 1) {
                    for (int i = 1; i < lines.length; i++) {
                        buff.write(lines[i]);
                        buff.newLine();
                    }
                }

                buff.newLine();
                buff.newLine();

            }
            buff.flush();

            buff.close();
        } catch (IOException e) {
            System.out.println("WARNING: Error while attempting to write AOD results to file.");
        }
    }
//------------------------------------------------------------------------------------------------

    public String makeString(String bla, int desiredLength) {
        String fittedString;
        if (bla.length() > desiredLength) {
            fittedString = bla.substring(0, desiredLength);
        } else {
            fittedString = bla;
            for (int i = 0; i < (bla.length() - desiredLength); i++) {
                fittedString = fittedString + " ";
            }
        }
        return fittedString;
    }

}
