package sunphotometer.pfr;

import java.io.*;
import java.awt.*;

public class OutputWriter {

    public File whereSaveFile(String nameOfFile, MainWindow mainy) {	// opens the file dialog, to choose which file shall be opened
        // helper method for the following two methods..		

        String savePath = new String();
        String loadPath = new String();

        boolean exocc = false;
        try {
            FileReader file = new FileReader("inifiles/prefer.ini");
            BufferedReader buff = new BufferedReader(file);
            loadPath = buff.readLine();
            savePath = buff.readLine();  // it is on the second line....
            buff.close();
        } catch (IOException e) {
            exocc = true;
        }

        FileDialog saveFile = new FileDialog(mainy, " Indicate Directory ", FileDialog.SAVE);
        if (!exocc) {
            saveFile.setDirectory(savePath);
        }
        saveFile.setFile(nameOfFile);
        saveFile.setVisible(true);
        String directoryName = saveFile.getDirectory();
        String fileName = saveFile.getFile();
        File targetFile;
        if (fileName != null) {
            targetFile = new File(directoryName, fileName);
        } else {
            targetFile = new File(directoryName, nameOfFile);
        }

        if (directoryName != null) {
            try {
                FileWriter file = new FileWriter("inifiles/prefer.ini");
                BufferedWriter buff = new BufferedWriter(file);
                {
                    buff.write(loadPath);
                    buff.newLine();
                    buff.write(directoryName);
                }
                buff.flush();
                buff.close();
            } catch (IOException e) {
                exocc = true;
            }
        }
        if (directoryName == null) {
            targetFile = null;
        }
        return targetFile;
    }	//	end whereSaveFile();

//-----------------------------------------------------------------------------------
    public void level3Writer(MainWindow mainy) {	// writes the Level 3 files..

        String spacy;

        String filename = mainy.chosenFile.getName();
        String dirname = mainy.chosenFile.getParent();
        filename = filename.substring(0, filename.length() - 3) + "003";

        File outputFile = whereSaveFile(filename, mainy);
        if (outputFile == null) {
            outputFile = new File(dirname, filename);
        }

        try {
            FileWriter file = new FileWriter(outputFile);
            BufferedWriter buff = new BufferedWriter(file);
            {
                if (mainy.thisLevel2File.station.exists) {
                    buff.write("%STATION   = " + mainy.thisLevel2File.station.writtenData);
                    buff.newLine();
                }
                if (mainy.thisLevel2File.latitude.exists) {
                    buff.write("%LATITUDE  = " + mainy.thisLevel2File.latitude.numericalData[0]);
                    buff.newLine();
                }
                if (mainy.thisLevel2File.longitude.exists) {
                    buff.write("%LONGITUDE = " + mainy.thisLevel2File.longitude.numericalData[0]);
                    buff.newLine();
                }
                if (mainy.thisLevel2File.altitude.exists) {
                    buff.write("%ALTITUDE  = " + mainy.thisLevel2File.altitude.numericalData[0]);
                    buff.newLine();
                }
                if (mainy.thisLevel2File.zone.exists) {
                    buff.write("%ZONE      = " + mainy.thisLevel2File.zone.numericalData[0]);
                    buff.newLine();
                }
                if (mainy.thisLevel2File.statident.exists) {
                    buff.write("%STATIDENT = " + mainy.thisLevel2File.statident.writtenData);
                    buff.newLine();
                }
                if (mainy.thisLevel2File.date.exists) {
                    buff.write("%DATE      = " + mainy.thisLevel2File.date.writtenData);
                    buff.newLine();
                }
                if (mainy.thisLevel2File.normpress.exists) {
                    buff.write("%NORMPRESS = " + mainy.thisLevel2File.normpress.writtenData);
                    buff.newLine();
                }

                buff.write("%AIRTEMP   = " + Double.toString(mainy.meteDat[3]));
                buff.newLine();

                buff.write("%AIRPRESS  = " + Double.toString(mainy.meteDat[1]));
                buff.newLine();

                buff.write("%OZONE     = " + Double.toString(mainy.meteDat[0]));
                buff.newLine();

                buff.write("%HUMIDITY  = " + Double.toString(mainy.meteDat[2]));
                buff.newLine();

                if (mainy.thisLevel2File.instrument.exists) {
                    buff.write("%INSTRUMENT= " + mainy.thisLevel2File.instrument.writtenData);
                    buff.newLine();
                }
                if (mainy.thisLevel2File.instident.exists) {
                    buff.write("%INSTIDENT = " + mainy.thisLevel2File.instident.writtenData);
                    buff.newLine();
                }
                if (mainy.thisLevel2File.nchannels.exists) {
                    buff.write("%NCHANNELS = " + mainy.thisLevel2File.nchannels.writtenData);
                    buff.newLine();
                }
                if (mainy.thisLevel2File.nreadings.exists) {
                    buff.write("%NREADINGS = " + mainy.thisLevel2File.nreadings.writtenData);
                    buff.newLine();
                }
                if (mainy.thisLevel2File.caldate.exists) {
                    buff.write("%CALDATE   = " + mainy.thisLevel2File.caldate.writtenData);
                    buff.newLine();
                }
                if (mainy.thisLevel2File.wavelength.exists) {
                    buff.write("%WAVELENGTH= ");
                    for (int j = 0; j < mainy.thisLevel2File.wavelength.numericalData.length; j++) {
                        if (Double.toString(mainy.thisLevel2File.wavelength.numericalData[j]).length() < 7) {
                            buff.write(Double.toString(mainy.thisLevel2File.wavelength.numericalData[j]) + " ");
                            spacy = "  ";
                            for (int i = 0; i < (7 - Double.toString(mainy.thisLevel2File.wavelength.numericalData[j]).length()); i++) {
                                spacy = spacy + " ";
                            }
                            buff.write(spacy);
                        } else {
                            buff.write(Double.toString(mainy.thisLevel2File.wavelength.numericalData[j]).substring(0, 7) + " ");
                        }
                    }
                    buff.newLine();
                }
                if (mainy.thisLevel2File.calibrat.exists) {
                    buff.write("%CALIBRAT  = ");
                    for (int j = 0; j < mainy.thisAerosolOpticalDepth.EV.length; j++) {
                        if (Double.toString(mainy.thisAerosolOpticalDepth.EV[j]).length() < 7) {
                            buff.write(Double.toString(mainy.thisAerosolOpticalDepth.EV[j]) + " ");
                            spacy = "  ";
                            for (int i = 0; i < (7 - Double.toString(mainy.thisAerosolOpticalDepth.EV[j]).length()); i++) {
                                spacy = spacy + " ";
                            }
                            buff.write(spacy);
                        } else {
                            buff.write(Double.toString(mainy.thisAerosolOpticalDepth.EV[j]).substring(0, 7) + " ");
                        }
                    }
                    buff.newLine();
                }
                if (mainy.thisLevel2File.slope.exists) {
                    buff.write("%SLOPE     = ");
                    for (int j = 0; j < mainy.thisLevel2File.slope.numericalData.length; j++) {
                        if (Double.toString(mainy.thisLevel2File.slope.numericalData[j]).length() < 7) {
                            buff.write(Double.toString(mainy.thisLevel2File.slope.numericalData[j]) + " ");
                            spacy = "  ";
                            for (int i = 0; i < (7 - Double.toString(mainy.thisLevel2File.slope.numericalData[j]).length()); i++) {
                                spacy = spacy + " ";
                            }
                            buff.write(spacy);
                        } else {
                            buff.write(Double.toString(mainy.thisLevel2File.slope.numericalData[j]).substring(0, 7) + " ");
                        }
                    }
                    buff.newLine();
                }
                if (mainy.thisLevel2File.o3abs.exists) {
                    buff.write("%O3ABS     = ");
                    for (int j = 0; j < mainy.thisLevel2File.o3abs.numericalData.length; j++) {
                        if (Double.toString(mainy.thisLevel2File.o3abs.numericalData[j]).length() < 7) {
                            buff.write(Double.toString(mainy.thisLevel2File.o3abs.numericalData[j]) + " ");
                            spacy = "  ";
                            for (int i = 0; i < (7 - Double.toString(mainy.thisLevel2File.o3abs.numericalData[j]).length()); i++) {
                                spacy = spacy + " ";
                            }
                            buff.write(spacy);
                        } else {
                            buff.write(Double.toString(mainy.thisLevel2File.o3abs.numericalData[j]).substring(0, 7) + "   ");
                        }
                    }
                    buff.newLine();
                }
                if (mainy.thisLevel2File.sunearth.exists) {
                    buff.write("%SUNEARTH  = " + mainy.thisLevel2File.sunearth.writtenData);
                    buff.newLine();
                }

                if (mainy.thisLevel2File.declinat.exists) {
                    buff.write("%DECLINAT  = " + mainy.thisLevel2File.declinat.writtenData);
                    buff.newLine();
                }
                if (mainy.thisLevel2File.eqoftime.exists) {
                    buff.write("%EQOFTIME  = " + mainy.thisLevel2File.eqoftime.writtenData);
                    buff.newLine();
                }
                if (mainy.thisLevel2File.noon.exists) {
                    buff.write("%NOON      = " + mainy.thisLevel2File.noon.writtenData);
                    buff.newLine();
                }
                if (mainy.thisLevel2File.logfile.exists) {
                    buff.write("%LOGFILE   = " + mainy.thisLevel2File.logfile.writtenData);
                    buff.newLine();
                }
                if (mainy.thisLevel2File.version.exists) {
                    buff.write("%VERSION   = " + mainy.versionNumber);
                    buff.newLine();
                }
                if (mainy.thisLevel2File.comment.exists) {
                    buff.write("%COMMENT   = " + mainy.thisLevel2File.comment.writtenData);
                    buff.newLine();
                }

                buff.write("%RAYLSCATT = ");
                for (int j = 0; j < mainy.thisAerosolOpticalDepth.NumberOfChannels; j++) {
                    if (Double.toString(mainy.thisAerosolOpticalDepth.justDoSomeCalc.Rayleigh[j]).length() < 7) {
                        buff.write(Double.toString(mainy.thisAerosolOpticalDepth.justDoSomeCalc.Rayleigh[j]) + "  ");
                        spacy = "  ";
                        for (int i = 0; i < (7 - Double.toString(mainy.thisAerosolOpticalDepth.justDoSomeCalc.Rayleigh[j]).length()); i++) {
                            spacy = spacy + " ";
                        }
                        buff.write(spacy);
                    } else {
                        buff.write(Double.toString(mainy.thisAerosolOpticalDepth.justDoSomeCalc.Rayleigh[j]).substring(0, 7) + "  ");
                    }
                }
                buff.newLine();
            }

            buff.write("%MEANAOD   = ");
            for (int j = 0; j < mainy.thisAerosolOpticalDepth.NumberOfChannels; j++) {
                if (Double.toString(mainy.thisAerosolOpticalDepth.mTau[j]).length() < 7) {
                    buff.write(Double.toString(mainy.thisAerosolOpticalDepth.mTau[j]) + "  ");
                    spacy = "  ";
                    for (int i = 0; i < (7 - Double.toString(mainy.thisAerosolOpticalDepth.mTau[j]).length()); i++) {
                        spacy = spacy + " ";
                    }
                    buff.write(spacy);
                } else {
                    buff.write(Double.toString(mainy.thisAerosolOpticalDepth.mTau[j]).substring(0, 7) + "  ");
                }
            }
            buff.newLine();

            buff.write("%MEANALPHA = ");
            if (Double.toString(mainy.thisAerosolOpticalDepth.avgAlpha).length() < 7) {
                buff.write(Double.toString(mainy.thisAerosolOpticalDepth.avgAlpha));
            } else {
                buff.write(Double.toString(mainy.thisAerosolOpticalDepth.avgAlpha).substring(0, 7));
            }
            buff.newLine();

            buff.write("%MEANBETA  = ");
            if (Double.toString(mainy.thisAerosolOpticalDepth.avgBeta).length() < 7) {
                buff.write(Double.toString(mainy.thisAerosolOpticalDepth.avgBeta));
            } else {
                buff.write(Double.toString(mainy.thisAerosolOpticalDepth.avgBeta).substring(0, 7));
            }
            buff.newLine();

            buff.write("%END");
            buff.newLine();

            // now write the body data
            for (int i = 0; i < mainy.thisAerosolOpticalDepth.noPoints; i++) {	// TIME
                if (Double.isNaN(mainy.thisLevel2File.BodyData[i + mainy.thisAerosolOpticalDepth.indexData[0]][0]) || Double.isInfinite(mainy.thisLevel2File.BodyData[i + mainy.thisAerosolOpticalDepth.indexData[0]][0])) {
                    buff.write("NaN      ");
                } else if (Double.toString(mainy.thisLevel2File.BodyData[i + mainy.thisAerosolOpticalDepth.indexData[0]][0]).length() < 6) {
                    buff.write(Double.toString(mainy.thisLevel2File.BodyData[i + mainy.thisAerosolOpticalDepth.indexData[0]][0]) + " ");
                    spacy = "  ";
                    for (int j = 0; j < (6 - Double.toString(mainy.thisLevel2File.BodyData[i + mainy.thisAerosolOpticalDepth.indexData[0]][0]).length()); j++) {
                        spacy = spacy + " ";
                    }
                    buff.write(spacy);
                } else {
                    buff.write(Double.toString(mainy.thisLevel2File.BodyData[i + mainy.thisAerosolOpticalDepth.indexData[0]][0]).substring(0, 6) + "   ");
                }
                // AOD'S
                for (int k = 0; k < mainy.thisAerosolOpticalDepth.NumberOfChannels; k++) {
                    if (Double.isNaN(mainy.thisAerosolOpticalDepth.allTau[i][k]) || Double.isInfinite(mainy.thisAerosolOpticalDepth.allTau[i][k])) {
                        buff.write("NaN      ");
                    } else if (Double.toString(mainy.thisAerosolOpticalDepth.allTau[i][k]).length() < 6) {
                        buff.write(Double.toString(mainy.thisAerosolOpticalDepth.allTau[i][k]) + " ");
                        spacy = "  ";
                        for (int j = 0; j < (6 - Double.toString(mainy.thisAerosolOpticalDepth.allTau[i][k]).length()); j++) {
                            spacy = spacy + " ";
                        }
                        buff.write(spacy);
                    } else {
                        buff.write(Double.toString(mainy.thisAerosolOpticalDepth.allTau[i][k]).substring(0, 6) + "   ");
                    }
                }
                // ALPHA
                if (Double.isNaN(mainy.thisAerosolOpticalDepth.allAlpha[i]) || Double.isInfinite(mainy.thisAerosolOpticalDepth.allAlpha[i])) {
                    buff.write("NaN      ");
                } else if (Double.toString(mainy.thisAerosolOpticalDepth.allAlpha[i]).length() < 6) {
                    buff.write(Double.toString(mainy.thisAerosolOpticalDepth.allAlpha[i]) + " ");
                    spacy = "  ";
                    for (int j = 0; j < (6 - Double.toString(mainy.thisAerosolOpticalDepth.allAlpha[i]).length()); j++) {
                        spacy = spacy + " ";
                    }
                    buff.write(spacy);
                } else {
                    buff.write(Double.toString(mainy.thisAerosolOpticalDepth.allAlpha[i]).substring(0, 6) + "   ");
                }
                // BETA
                if (Double.isNaN(mainy.thisAerosolOpticalDepth.allBeta[i]) || Double.isInfinite(mainy.thisAerosolOpticalDepth.allBeta[i])) {
                    buff.write("NaN      ");
                } else if (Double.toString(mainy.thisAerosolOpticalDepth.allBeta[i]).length() < 6) {
                    buff.write(Double.toString(mainy.thisAerosolOpticalDepth.allBeta[i]) + " ");
                    spacy = "  ";
                    for (int j = 0; j < (6 - Double.toString(mainy.thisAerosolOpticalDepth.allBeta[i]).length()); j++) {
                        spacy = spacy + " ";
                    }
                    buff.write(spacy);
                } else {
                    buff.write(Double.toString(mainy.thisAerosolOpticalDepth.allBeta[i]).substring(0, 6) + "   ");
                }
                // Quality Flag level 2
                if (Integer.toString((int) mainy.thisLevel2File.BodyData[i + mainy.thisAerosolOpticalDepth.indexData[0]][11]).length() < 6) {
                    buff.write(Integer.toString((int) mainy.thisLevel2File.BodyData[i + mainy.thisAerosolOpticalDepth.indexData[0]][11]) + " ");
                    spacy = "  ";
                    for (int j = 0; j < (6 - Integer.toString((int) mainy.thisLevel2File.BodyData[i + mainy.thisAerosolOpticalDepth.indexData[0]][11]).length()); j++) {
                        spacy = spacy + " ";
                    }
                    buff.write(spacy);
                } else {
                    buff.write(Integer.toString((int) mainy.thisLevel2File.BodyData[i + mainy.thisAerosolOpticalDepth.indexData[0]][11]).substring(0, 6) + "   ");
                }

                // Quality Flag level 3			
                if (mainy.thisAerosolOpticalDepth.filterMask[i]) {
                    buff.write("0  ");
                } else if (mainy.thisLevel2File.BodyData[i + mainy.thisAerosolOpticalDepth.indexData[0]][11] < 16) {
                    buff.write("1  ");
                } else {
                    buff.write("129");
                }

                buff.newLine();
            }

            buff.flush();
            buff.close();
        } catch (IOException e) {
            WarningLogger warnLog = new WarningLogger();
            warnLog.addWarning("WARNING: Error while attempting to write Default Settings to File.");
        }
    }	// end level3Writer()

//-----------------------------------------------------------------------
    public void langleyWriter(MainWindow mainy) {	// writes the Langley files... (.lan)...

        String spacy;
        String filename = mainy.chosenFile.getName();
        String dirname = mainy.chosenFile.getParent();
        filename = filename.substring(0, filename.length() - 3) + "lan";

        File outputFile = whereSaveFile(filename, mainy);
        if (outputFile == null) {
            outputFile = new File(dirname, filename);
        }

        try {
            FileWriter file = new FileWriter(outputFile);
            BufferedWriter buff = new BufferedWriter(file);
            {
                //Header Data first

                buff.write("%Data of Langley Calibration for " + mainy.thisLevel2File.TheDate + " in " + mainy.thisLevel2File.station.writtenData + " with " + mainy.thisLevel2File.instrument.writtenData);
                buff.newLine();
                String usedLangleyMethod;
                if (mainy.selectedLangley == 0) {
                    usedLangleyMethod = "classic";
                } else {
                    usedLangleyMethod = "refined";
                }
                buff.write("%Used Method: " + usedLangleyMethod);
                buff.newLine();
                buff.write("% First column: air mass / then 4 columns with 'log(S/S_o)' / then 2 flag: first flag = level 2 flag, second flag = if (level 2 >= 16) then 128 else 0, used (0) or not (1)");
                buff.newLine();
                for (int i = 0; i < mainy.thisLangleyExtrapol.noPoints; i++) {
                    if (Double.isNaN(mainy.thisLangleyExtrapol.airMass[i]) || Double.isInfinite(mainy.thisLangleyExtrapol.airMass[i])) {
                        buff.write("NaN      ");
                    } else if (Double.toString(mainy.thisLangleyExtrapol.airMass[i]).length() < 6) {
                        buff.write(Double.toString(mainy.thisLangleyExtrapol.airMass[i]) + " ");
                        spacy = " ";
                        for (int j = 0; j < (6 - Double.toString(mainy.thisLangleyExtrapol.airMass[i]).length()); j++) {
                            spacy = spacy + " ";
                        }
                        buff.write(spacy);
                    } else {
                        buff.write(Double.toString(mainy.thisLangleyExtrapol.airMass[i]).substring(0, 6) + "   ");
                    }

                    for (int k = 0; k < mainy.thisLangleyExtrapol.numberOfChannels; k++) {
                        if (Double.isNaN(mainy.thisLangleyExtrapol.absorbtionData[i][k]) || Double.isInfinite(mainy.thisLangleyExtrapol.absorbtionData[i][k])) {
                            buff.write("NaN      ");
                        } else if (Double.toString(mainy.thisLangleyExtrapol.absorbtionData[i][k]).length() < 6) {
                            buff.write(Double.toString(mainy.thisLangleyExtrapol.absorbtionData[i][k]) + " ");
                            spacy = " ";
                            for (int j = 0; j < (6 - Double.toString(mainy.thisLangleyExtrapol.absorbtionData[i][k]).length()); j++) {
                                spacy = spacy + " ";
                            }
                            buff.write(spacy);
                        } else {
                            buff.write(Double.toString(mainy.thisLangleyExtrapol.absorbtionData[i][k]).substring(0, 6) + "   ");
                        }
                    }

                    // Quality Flag level 2
                    if (Integer.toString((int) mainy.thisLevel2File.BodyData[i + mainy.thisLangleyExtrapol.indexData[0]][11]).length() < 6) {
                        buff.write(Integer.toString((int) mainy.thisLevel2File.BodyData[i + mainy.thisLangleyExtrapol.indexData[0]][11]) + " ");
                        spacy = "  ";
                        for (int j = 0; j < (6 - Integer.toString((int) mainy.thisLevel2File.BodyData[i + mainy.thisLangleyExtrapol.indexData[0]][11]).length()); j++) {
                            spacy = spacy + " ";
                        }
                        buff.write(spacy);
                    } else {
                        buff.write(Integer.toString((int) mainy.thisLevel2File.BodyData[i + mainy.thisLangleyExtrapol.indexData[0]][11]).substring(0, 6) + "   ");
                    }

                    // Quality Flag level 3	
                    if (mainy.thisLangleyExtrapol.filterMask[i]) {
                        buff.write("0  ");
                    } else if (mainy.thisLevel2File.BodyData[i + mainy.thisLangleyExtrapol.indexData[0]][11] < 16) {
                        buff.write("1  ");
                    } else {
                        buff.write("129");
                    }
                    buff.newLine();
                }

            }
            buff.flush();
            buff.close();
        } catch (IOException e) {
            WarningLogger warnLog = new WarningLogger();
            warnLog.addWarning("WARNING: Error while attempting to write Default Settings to File.");
        }

    }	//end langleyWriter()

}
