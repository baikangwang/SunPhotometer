package sunphotometer.pfr;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.io.*;

public class MainWindow extends JFrame implements ActionListener {

    /* needs the following classes to run:
			AerosolOpticalDepth.class		-- direct
			ConvertStringToNumbers.class  	-- helperClass of ReadLevel2File.class
			DoTheCheck.class				-- direct
			DrawGraphOnCanvas.class			-- direct
			LangleyExtrapol.class			-- direct
			ModifiedJulianDate.class		-- helperClass of AerosolOpticalDepth.class
			ReadLevel2File.class			-- direct
			Regression.class				-- helperClass of LangleyExtrapol.class and AerosolOpticalDepth.class	
			PrintSampler.class				-- direct
			HeaderData.class				-- needed for ReadLevel2File.class
			SplitFileIntoLines.class		-- needed for ReadLevel2File.class
			WriteLogFiles.class				-- direct
			HelpWindow						-- direct
			warningLogger					-- direct
			IntermediateScreen				-- direct
			OutputWriter					-- direct			

     */

    // default variables which may be necessary if default.ini file is lost
    int defaultSelectedPeriod = 0;
    int defaultSelectedLangley = 0;
    double[] defaultSelectedAirmassRange = {2.00, 6.00};
    int defaultSelectedMete = 0;
    double[] defaultMeteDat = {300.0, 1013.5, 50, 15};
    ;		
		int defaultSelectedAerov0 = 0;
    double[] defaultAerov0Values = {0.0, 0.0, 0.0, 0.0};
    File defaultSelectedMeteFile = null;

    String versionNumber = "1.1.1";

    String programDirectory;		//where is this program allocated
    boolean logFilesWriting = true;

    // invoke some classes which provide basic features..		
    JFrame helpframe;
    WarningLogger warnLog = new WarningLogger();
    IntermediateScreen display = new IntermediateScreen(this);
    ChooseOptions myOpt = new ChooseOptions(this);

    JButton ccb = new JButton("Cancel");
    JButton printButton = new JButton("Print this Window");

    // Provide a Windowkiller for the Graphical Outputs
    WindowListener closingGraph = new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            JPanel pane = new JPanel();
            BorderLayout bordy = new BorderLayout();
            pane.setLayout(bordy);
            pane.add("North", menuBar);
            ImageIcon aboutIcon = new ImageIcon(getClass().getResource("/images/proceed.gif"));
            JLabel aboutLabel = new JLabel(aboutIcon);
            pane.add("Center", aboutLabel);
            JLabel commentLabel = new JLabel("Action accepted. Please proceed.");
            pane.add("South", commentLabel);
            setContentPane(pane);
            setVisible(true);
        }
    };

    //global variables
    JLabel welcomeScreen;

    ReadLevel2File thisLevel2File;
    int[] thisDayData;
    LangleyExtrapol thisLangleyExtrapol;
    AerosolOpticalDepth thisAerosolOpticalDepth;

    //menu variables
    //---	
    JMenuBar menuBar = new JMenuBar();
    JMenu file = new JMenu("File");
    JMenu options = new JMenu("Options");
    JMenu results = new JMenu("View");
    JMenu run = new JMenu("Run");
    JMenu help = new JMenu("Help");
    //---
    JMenuItem open = new JMenuItem("Open");
    JMenuItem close = new JMenuItem("Close");
    JMenu save = new JMenu("Save");
    JMenuItem writeLevel3 = new JMenuItem("Write Level 3 File");
    JMenuItem writeLangley = new JMenuItem("Write Langley File");
    JMenuItem exit = new JMenuItem("Exit");
    //---
    JMenuItem periode = new JMenuItem("Period for Langley Cal.");
    JMenuItem langley = new JMenuItem("Langley Method");
    JMenuItem airrange = new JMenuItem("Airmass Range");
    JMenuItem mete = new JMenuItem("Meteorological Data");
    JMenuItem aerov0 = new JMenuItem("V_o Values");
    JMenuItem setDefault = new JMenuItem("Edit Default Settings");
    JMenuItem getDefault = new JMenuItem("Set to Default");
    //---
    JMenuItem signal = new JMenuItem("Signals");
    JMenuItem pointing = new JMenuItem("Pointing");
    JMenuItem temp = new JMenuItem("Temperature");
    JMenuItem head = new JMenuItem("Level 2 Header");

    JMenu aeroplot = new JMenu("Aerosol");
    JMenuItem opticalDepth = new JMenuItem("Aerosol Optical Depths");
    JMenuItem angAlpha = new JMenuItem("Angstr�m Alpha");
    JMenuItem angBeta = new JMenuItem("Angstr�m Beta");
    JMenuItem aerosolTable = new JMenuItem("Summary of Results");

    JMenu langleyplot = new JMenu("Langley");
    JMenuItem langleyGraph = new JMenuItem("Langley Plots");
    JMenuItem langleyReg = new JMenuItem("Langley Plots with Regression");
    JMenuItem langleyTable = new JMenuItem("Summary of Results");

    JMenu scaling = new JMenu("Scaling");
    JMenuItem aeroScaling = new JMenuItem("Scaling for Aerosol Plots");
    JMenuItem langleyScaling = new JMenuItem("Scaling for Langley Plots");

    //---
    JMenuItem runaero = new JMenuItem("Aerosol Optical Depths");
    JMenuItem runlangley = new JMenuItem("Langley Calibration");
    //---
    JMenuItem about = new JMenuItem("About");
    JMenuItem manual = new JMenuItem("Manual");

//============= file section =============================================
    File chosenFile;

//============= options section ==========================================
    int selectedPeriod;

    int selectedLangley;

    double[] selectedAirmassRange;
    double selectedLowerAirmassRange;
    double selectedUpperAirmassRange;

    int selectedMete;
    double[] meteDat;
    File selectedMeteFile;

    double[] aerov0Values;
    int selectedAerov0 = 0;

//--------------------------------
    //---scaling:
    JTextField[] scalingTextField;
    double[] aeroScale = new double[4];
    JButton okbScaleAero = new JButton("OK");

    double[] langleyScale = new double[4];
    JButton okbScaleLangley = new JButton("OK");

//===========================end options section ==========================		
//====================================================================================		
//-------------Initializer/Contructor-------------------------------------------------			
//====================================================================================
    public MainWindow() {
        super("DustTracker analysis");

        //---check the .ini files ---
        defaultReader();
        scalingReader();
        checkLogging();
        resetDefault();
        selectedAirmassRange = defaultSelectedAirmassRange;

        warnLog.create();

        FileDialog fili = new FileDialog(this);
        programDirectory = fili.getDirectory();

        //---set the menu bar---	
        //--- first plug the major subgroups together---
        //---menu file---
        open.addActionListener(this);
        close.addActionListener(this);
        writeLevel3.addActionListener(this);
        writeLangley.addActionListener(this);
        exit.addActionListener(this);

        file.add(open);
        file.add(close);
        save.add(writeLevel3);
        save.add(writeLangley);
        file.add(save);
        file.addSeparator();
        file.add(exit);

        writeLevel3.setEnabled(false);
        writeLangley.setEnabled(false);

        //--- menu options ---
        periode.addActionListener(this);
        langley.addActionListener(this);
        airrange.addActionListener(this);
        mete.addActionListener(this);
        aerov0.addActionListener(this);
        getDefault.addActionListener(this);
        setDefault.addActionListener(this);

        options.add(periode);
        options.add(langley);
        options.add(airrange);
        options.add(mete);
        options.add(aerov0);
        options.add(setDefault);
        options.addSeparator();
        options.add(getDefault);

        //--- menu plots ---
        signal.addActionListener(this);
        pointing.addActionListener(this);
        temp.addActionListener(this);
        head.addActionListener(this);
        opticalDepth.addActionListener(this);
        angAlpha.addActionListener(this);
        angBeta.addActionListener(this);
        aerosolTable.addActionListener(this);
        langleyGraph.addActionListener(this);
        langleyReg.addActionListener(this);
        langleyTable.addActionListener(this);
        aeroScaling.addActionListener(this);
        langleyScaling.addActionListener(this);

        results.add(signal);
        results.add(pointing);
        results.add(temp);
        results.add(head);
        results.addSeparator();
        results.add(aeroplot);
        aeroplot.add(opticalDepth);
        aeroplot.add(angAlpha);
        aeroplot.add(angBeta);
        aeroplot.add(aerosolTable);
        results.add(langleyplot);
        langleyplot.add(langleyGraph);
        langleyplot.add(langleyReg);
        langleyplot.add(langleyTable);
        results.addSeparator();
        results.add(scaling);
        scaling.add(aeroScaling);
        scaling.add(langleyScaling);

        results.setEnabled(false);

        //--- menu run ---
        runaero.addActionListener(this);
        runlangley.addActionListener(this);

        run.add(runaero);
        run.add(runlangley);

        //--- menu help ---
        about.addActionListener(this);
        manual.addActionListener(this);

        help.add(about);
        help.add(manual);

        //--- now plug these subgroups together to get the menu bar---
        //--- menu bar ---
        menuBar.add(file);
        menuBar.add(options);
        menuBar.add(results);
        menuBar.add(run);
        menuBar.add(help);

        //---set up the content of the main window---		
        JPanel pane = new JPanel();
        BorderLayout bordy = new BorderLayout();
        pane.setLayout(bordy);

        pane.add("North", menuBar);

        ImageIcon welcome = new ImageIcon(getClass().getResource("/sunphotometer/pfr/images/dusty.gif"));
        welcomeScreen = new JLabel(welcome);
        pane.add("Center", welcomeScreen);

        JLabel commentLabel = new JLabel("Welcome");
        pane.add("South", commentLabel);

        setContentPane(pane);

        // add all the ActionListeners to the cancel and ok buttons
        ccb.addActionListener(this);
        okbScaleAero.addActionListener(this);
        okbScaleLangley.addActionListener(this);
    }

//=======================================================================
//                  here the method definitions start
//=======================================================================
//-------------------first input-output methods--------------------------
// reading of the default.ini file
    public void defaultReader() {
        String[] lines = new String[18];
        boolean exceptionCatched = false;

        try {
            FileReader file = new FileReader("inifiles/default.ini");
            BufferedReader buff = new BufferedReader(file);
            int i = 0;
            boolean neof = true; //(not end of file...)
            while (i < 18 && neof) {
                String line = buff.readLine();
                lines[i] = line;
                if (lines[i] == null) {
                    neof = false;
                }
                i++;
            }
            buff.close();
        } catch (IOException e) {
            warnLog.addWarning("Problems occurred while reading default values");
            exceptionCatched = true;
        }

        if (!exceptionCatched) {
            defaultSelectedPeriod = Integer.parseInt(lines[1]);
            defaultSelectedLangley = Integer.parseInt(lines[2]);
            defaultSelectedAirmassRange[0] = Double.parseDouble(lines[3]);
            defaultSelectedAirmassRange[1] = Double.parseDouble(lines[4]);
            defaultSelectedMete = Integer.parseInt(lines[5]);
            if (!(lines[6].equals("null")) && !(lines[7].equals("null"))) {
                defaultSelectedMeteFile = new File(lines[7], lines[6]);
            }
            defaultMeteDat[0] = Double.parseDouble(lines[8]);
            defaultMeteDat[1] = Double.parseDouble(lines[9]);
            defaultMeteDat[2] = Double.parseDouble(lines[10]);
            defaultMeteDat[3] = Double.parseDouble(lines[11]);
            defaultSelectedAerov0 = Integer.parseInt(lines[12]);
            defaultAerov0Values[0] = Double.parseDouble(lines[13]);
            defaultAerov0Values[1] = Double.parseDouble(lines[14]);
            defaultAerov0Values[2] = Double.parseDouble(lines[15]);
            defaultAerov0Values[3] = Double.parseDouble(lines[16]);
        }
    }		//end defaultReader()

//-----------------------------------------------------------------------
// writing of the default.ini file
    public void defaultWriter() {
        try {
            FileWriter file = new FileWriter("inifiles/default.ini");
            BufferedWriter buff = new BufferedWriter(file);
            {
                //Header Data first

                buff.write("%Default Settings for DustTracker # This is a generated File: do NOT modify manually");
                buff.newLine();
                buff.write(Integer.toString(defaultSelectedPeriod), 0, 1);
                buff.newLine();
                buff.write(Integer.toString(defaultSelectedLangley), 0, 1);
                buff.newLine();
                buff.write(Double.toString(defaultSelectedAirmassRange[0]));
                buff.newLine();
                buff.write(Double.toString(defaultSelectedAirmassRange[1]));
                buff.newLine();
                buff.write(Integer.toString(defaultSelectedMete), 0, 1);
                buff.newLine();
                if (defaultSelectedMeteFile != null) {
                    buff.write(defaultSelectedMeteFile.getName());
                    buff.newLine();
                    buff.write(defaultSelectedMeteFile.getParent());
                    buff.newLine();
                } else {
                    buff.write("null");
                    buff.newLine();
                    buff.write("null");
                    buff.newLine();
                }
                for (int i = 0; i < 4; i++) {
                    buff.write(Double.toString(defaultMeteDat[i]));
                    buff.newLine();
                }
                buff.write(Integer.toString(defaultSelectedAerov0), 0, 1);
                buff.newLine();
                for (int i = 0; i < 4; i++) {
                    buff.write(Double.toString(defaultAerov0Values[i]));
                    buff.newLine();
                }
                buff.write("end", 0, 3);
                buff.newLine();
            }
            buff.flush();
            buff.close();
        } catch (IOException e) {
            warnLog.addWarning("WARNING: Error while attempting to write Default Settings to File.");
        }
    }		// end defaultWriter()

//-------------------------------------------------------------------------
// reading of the scaling.ini file (contains specifications about the scaling of the graphs)
    public void scalingReader() {
        String[] lines = new String[9];
        boolean exceptionCatched = false;

        try {
            FileReader file = new FileReader("inifiles/scaling.ini");
            BufferedReader buff = new BufferedReader(file);
            int i = 0;
            boolean neof = true; //(not end of file...)
            while (i < 9 && neof) {
                String line = buff.readLine();
                lines[i] = line;
                if (lines[i] == null) {
                    neof = false;
                }
                i++;
            }
            buff.close();
        } catch (IOException e) {
            warnLog.addWarning("Problems occurred while reading scaling.ini values");
            exceptionCatched = true;
        }

        if (!exceptionCatched) {
            for (int i = 0; i < 4; i++) {
                aeroScale[i] = Double.parseDouble(lines[i + 1]);
                langleyScale[i] = Double.parseDouble(lines[i + 5]);
            }
        }
    }	// end scalingReader()

//-----------------------------------------------------------------------
// writing of the scaling.ini file
    public void scalingWriter() {
        try {
            FileWriter file = new FileWriter("inifiles/scaling.ini");
            BufferedWriter buff = new BufferedWriter(file);
            {
                buff.write("%Scaling Settings for DustTracker # This is a generated File: do NOT modify manually");
                buff.newLine();
                for (int i = 0; i < 4; i++) {
                    buff.write(Double.toString(aeroScale[i]));
                    buff.newLine();
                }
                for (int i = 0; i < 4; i++) {
                    buff.write(Double.toString(langleyScale[i]));
                    buff.newLine();
                }
            }
            buff.flush();
            buff.close();
        } catch (IOException e) {
            warnLog.addWarning("WARNING: Error while attempting to write Scaling Settings to File.");
        }
    }	// end scalingWriter()

//-------------------------------------------------------------------------
// reading of the meteo file
// find the appropriate day and get the corresponding data
    public void meteoReader(int theDate, ReadLevel2File TheContent) {
        boolean exceptionCatched = false;
        double[] meteArray = new double[4];
        boolean found = false; // specified Date found...
        try {
            FileReader file = new FileReader(selectedMeteFile);
            BufferedReader buff = new BufferedReader(file);
            boolean eof = false; //(not end of file...)
            int i = 0;
            while (!found && !eof) {
                String line = buff.readLine();
                if (line == null) {
                    eof = true;
                } else {
                    StringTokenizer stt = new StringTokenizer(line);
                    if (stt.hasMoreTokens()) {
                        if (Integer.parseInt(stt.nextElement().toString()) == theDate) {
                            found = true;
                            while (i < 4 && stt.hasMoreTokens()) {
                                meteArray[i] = Double.parseDouble(stt.nextElement().toString());
                                i++;
                            }
                        }
                    }
                    if (found) {
                        if (i < 4) {
                            found = false;
                            eof = true;
                        }
                    }
                }
            }
            buff.close();
            if (found) {		//take values from file, if not exist, take from header,if neither exist, default
                if (meteArray[0] != -99.9) {
                    meteDat[0] = meteArray[0];
                } else if (TheContent.ozone.exists) {
                    meteDat[0] = TheContent.ozone.numericalData[0];
                } else {
                    meteDat[0] = defaultMeteDat[0];
                }
                if (meteArray[1] != -99.9) {
                    meteDat[1] = meteArray[1];
                } else if (TheContent.airpress.exists) {
                    meteDat[1] = TheContent.airpress.numericalData[0];
                } else {
                    meteDat[1] = defaultMeteDat[1];
                }
                if (meteArray[2] != -99.9) {
                    meteDat[2] = meteArray[2];
                } else {
                    meteDat[2] = defaultMeteDat[2];
                }
                if (meteArray[3] != -99.9) {
                    meteDat[3] = meteArray[3];
                } else if (TheContent.airtemp.exists) {
                    meteDat[3] = TheContent.airtemp.numericalData[0];
                } else {
                    meteDat[3] = defaultMeteDat[3];
                }
            }
        } catch (IOException e) {
            warnLog.addWarning("WARNING: Problems occurred while reading meteo data file");
            exceptionCatched = true;
        }
        if (!found) {
            selectedMete = 0;
        }
    }	// end meteoReader()

//----------------------------------------------------------------------
    public void checkLogging() {

        String line = new String();
        boolean exceptionCatched = false;

        try {
            FileReader file = new FileReader("inifiles/logging.ini");
            BufferedReader buff = new BufferedReader(file);
            line = buff.readLine();
            line = buff.readLine();
            buff.close();
        } catch (IOException e) {
            warnLog.addWarning("Problems occurred while reading logging.ini values");
            exceptionCatched = true;
        }

        if (!exceptionCatched) {
            line.trim();
            line.toUpperCase();
            logFilesWriting = line.startsWith("ENABLE");
        }

    }	//end checkLogging();

//=======================================================================	
//			The different Actions
//-----------------------------------------------------------------------	
// 		 First the few actions of the menu file
//=======================================================================
    public void fileOpener() {	// opens the file dialog, to choose which file shall be opened

        String loadPath = new String();
        String savePath = new String();
        boolean exocc = false;
        try {
            FileReader file = new FileReader("inifiles/prefer.ini");
            BufferedReader buff = new BufferedReader(file);
            loadPath = buff.readLine();
            savePath = buff.readLine();
            buff.close();
        } catch (IOException e) {
            exocc = true;
        }

        results.setEnabled(false);
        writeLevel3.setEnabled(false);
        writeLangley.setEnabled(false);

        FilenameFilter filty = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.endsWith(".002")) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        FileDialog openSesame = new FileDialog(this, " Open Sesame", FileDialog.LOAD);
        openSesame.setFilenameFilter(filty);
        if (!exocc || ((loadPath != null) && (loadPath != "-1"))) {
            openSesame.setDirectory(loadPath);
        }
        openSesame.setVisible(true);
        String fileName = openSesame.getFile();
        String directoryName = openSesame.getDirectory();
        if (fileName != null && directoryName != null) {
            chosenFile = new File(directoryName, fileName);
            try {
                FileWriter file = new FileWriter("inifiles/prefer.ini");
                BufferedWriter buff = new BufferedWriter(file);
                {
                    buff.write(directoryName);
                    buff.newLine();
                    if (savePath != null) {
                        buff.write(savePath);
                    }
                }
                buff.flush();
                buff.close();
            } catch (IOException e) {
                exocc = true;
            }
        } else {
            chosenFile = null;
        }
        if (chosenFile != null) {
            display.displayAcceptedProceedScreen();

        }
    }	//	end fileOpener()

//-----------------------------------------------------------------------
    public void meisterPropper() //called on close
    {
        resetDefault();
        chosenFile = null;
        selectedAirmassRange = defaultSelectedAirmassRange;
        results.setEnabled(false);
        display.displayAcceptedProceedScreen();
        writeLevel3.setEnabled(false);
        writeLangley.setEnabled(false);
    }	// end meisterPropper()

//=================== now the actions of the menu options ===============

    /* all these actions are described in the class:
chooseOptions.java...

     */
//------------------------------------------------------------------------
    public void resetDefault() //called on getDefault
    {
        selectedPeriod = defaultSelectedPeriod;
        selectedLangley = defaultSelectedLangley;
        selectedLowerAirmassRange = defaultSelectedAirmassRange[0];
        selectedUpperAirmassRange = defaultSelectedAirmassRange[1];
        selectedMete = defaultSelectedMete;
        meteDat = defaultMeteDat;
        selectedAerov0 = defaultSelectedAerov0;
        aerov0Values = defaultAerov0Values;
        selectedMeteFile = defaultSelectedMeteFile;
    }	// end resetDefault()

//------------------------------------------------------------------------
//===================== menu view=========================================
//		here the different graph windows are created....
//========================================================================
    public String makeSubtitle() {	// just a helping method to create a standard subtitle containing stationname, instrumentname and date

        String station;
        String instrument;
        if (thisLevel2File.station.exists) {
            station = thisLevel2File.station.writtenData;
        } else {
            station = " --- ";
        }
        if (thisLevel2File.instrument.exists) {
            instrument = thisLevel2File.instrument.writtenData;
        } else {
            instrument = " --- ";
        }
        String date = Integer.toString(thisLevel2File.TheDate);
        String subtitle = ("in " + station + " with " + instrument + " on " + date);

        return subtitle;

    }	// end makeSubtitle()

//------------------------------------------------------------------------
    public void showSignals() {
        display.displayAcceptedProceedScreen();
        String xLabel = "Time [UT hrs]";
        String yLabel = "Signals [V]";
        String title = "Signals";

        String subtitle = makeSubtitle();

        int indexLow = thisDayData[0];
        int indexHigh = thisDayData[2];
        if (indexLow > 10) {
            indexLow = indexLow - 10;
        }
        if (indexHigh < (thisLevel2File.BodyDataLength - 10)) {
            indexHigh = indexHigh + 10;
        }

        int myLength = indexHigh - indexLow + 1;
        double[][] x = new double[myLength][thisLangleyExtrapol.numberOfChannels];
        double[][] y = new double[myLength][thisLangleyExtrapol.numberOfChannels];

        for (int i = 0; i < myLength; i++) {
            for (int j = 0; j < thisLangleyExtrapol.numberOfChannels; j++) {
                x[i][j] = thisLevel2File.BodyData[i + indexLow][0];
                y[i][j] = thisLevel2File.BodyData[i + indexLow][j + 3];
            }
        }

        String[] labels = {title, subtitle, xLabel, yLabel};
        double[] yLimits = {0, 5};

        DrawGraphOnCanvas can = new DrawGraphOnCanvas(x, y, labels, thisLangleyExtrapol.numberOfChannels, 1, yLimits, 1);

        JFrame f = new PrintSampler(title, can);
        f.setSize(540, 480);
        f.addWindowListener(closingGraph);
        f.setVisible(true);

    }	// end showSignals()

//------------------------------------------------------------------------
    public void showPointing() {
        display.displayAcceptedProceedScreen();
        String xLabel = "Time [UT hrs]";
        String yLabel = "Pointing [arcmin]";
        String title = ("Pointing offset");

        String subtitle = makeSubtitle();

        int indexLow = thisDayData[0];
        int indexHigh = thisDayData[2];

        int myLength = indexHigh - indexLow + 1;
        double[] x = new double[myLength];
        double[][] y = new double[myLength][3];

        for (int i = 0; i < myLength; i++) {
            x[i] = thisLevel2File.BodyData[i + indexLow][0];
            for (int j = 0; j < 2; j++) {
                y[i][j] = thisLevel2File.BodyData[i + indexLow][j + thisLangleyExtrapol.numberOfChannels + 5];
            }
            y[i][2] = Math.sqrt((y[i][0] * y[i][0]) + (y[i][1] * y[i][1]));
        }

        //filter not reasonable values
        int count = 0;
        for (int i = 0; i < myLength; i++) {
            if ((y[i][0] < 98) && (y[i][1] < 98)) {
                x[count] = x[i];
                for (int j = 0; j < 3; j++) {
                    y[count][j] = y[i][j];
                }
                count++;
            }
        }

        //resize the arrays for plotting
        double[][] xf = new double[count][3];
        double[][] yf = new double[count][3];
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < 3; j++) {
                xf[i][j] = x[i];
                yf[i][j] = y[i][j];
            }
        }

        String[] labels = {title, subtitle, xLabel, yLabel};

        double[] pointingLimits = {-45, 45};

        DrawGraphOnCanvas can = new DrawGraphOnCanvas(xf, yf, labels, 3, 0, pointingLimits, 1);

        JFrame f = new PrintSampler(title, can);
        f.setSize(540, 480);
        f.addWindowListener(closingGraph);
        f.setVisible(true);

    }	// end showPointing()

//------------------------------------------------------------------------
    public void showTemperature() {
        display.displayAcceptedProceedScreen();
        String xLabel = "Time [UT hrs]";
        String yLabel = "Temperature [�C]";
        String title = ("Temperatureprofile ");

        String subtitle = makeSubtitle();

        int indexLow = 0;
        int indexHigh = thisLevel2File.BodyDataLength - 1;

        int myLength = indexHigh - indexLow + 1;
        double[][] x = new double[myLength][2];
        double[][] y = new double[myLength][2];

        for (int i = 0; i < myLength; i++) {
            x[i][0] = thisLevel2File.BodyData[i + indexLow][0];
            x[i][1] = thisLevel2File.BodyData[i + indexLow][0];
            y[i][0] = thisLevel2File.BodyData[i + indexLow][thisLangleyExtrapol.numberOfChannels + 3];
            y[i][1] = thisLevel2File.BodyData[i + indexLow][thisLangleyExtrapol.numberOfChannels + 4];
        }

        String[] labels = {title, subtitle, xLabel, yLabel};

        DrawGraphOnCanvas can = new DrawGraphOnCanvas(x, y, labels, 2, 1);

        JFrame f = new PrintSampler(title, can);
        f.setSize(540, 480);
        f.addWindowListener(closingGraph);
        f.setVisible(true);

    }	// end showTemperature()

//------------------------------------------------------------------------
    public void showHeader() {
        JFrame graphFrame = new JFrame("The Content of the Header");
        graphFrame.setSize(380, 360);

        JPanel pane = new JPanel();

        JPanel content = new JPanel();
        GridLayout gridy = new GridLayout(thisLevel2File.HeaderSize, 1);
        content.setLayout(gridy);

        JLabel[] header = new JLabel[thisLevel2File.HeaderSize];
        for (int i = 0; i < thisLevel2File.HeaderSize; i++) {
            header[i] = new JLabel(thisLevel2File.TheLines[i]);
            content.add(header[i]);
        }

        JScrollPane scroller = new JScrollPane(content);
        Dimension pref = new Dimension(360, 300);
        scroller.setPreferredSize(pref);

        pane.add(scroller);

        graphFrame.setContentPane(pane);
        graphFrame.setVisible(true);
        graphFrame.addWindowListener(closingGraph);

    }	// end showHeader()

//------------------------------------------------------------------------
    public void showAeroplot() {
        display.displayAcceptedProceedScreen();

        String xLabel = "Time [UT hrs]";
        String yLabel = "AOD";
        String title = ("AOD - Plot ");

        String subtitle = makeSubtitle();

        double[] x = thisAerosolOpticalDepth.tauTime;
        double[][] y = new double[x.length][thisAerosolOpticalDepth.NumberOfChannels];
        double[][] xf = new double[x.length][thisAerosolOpticalDepth.NumberOfChannels];

        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < thisAerosolOpticalDepth.NumberOfChannels; j++) {
                y[i][j] = thisAerosolOpticalDepth.tau[i][j];
                xf[i][j] = x[i];
            }
        }

        String[] labels = {title, subtitle, xLabel, yLabel};
        double[] yLimits = {aeroScale[0], aeroScale[1]};

        DrawGraphOnCanvas can = new DrawGraphOnCanvas(xf, y, labels, thisAerosolOpticalDepth.NumberOfChannels, 0, yLimits, 1);

        JFrame f = new PrintSampler(title, can);
        f.setSize(540, 480);
        f.addWindowListener(closingGraph);
        f.setVisible(true);

    }	// end showAeroplot()

//------------------------------------------------------------------------
    public void showAngBeta() {
        String xLabel = "Time [UT hrs]";
        String yLabel = "Angstr�m Beta";
        String title = ("Angstr�m Beta ");

        String subtitle = makeSubtitle();

        double[][] y = new double[thisAerosolOpticalDepth.tauTime.length][1];
        double[][] x = new double[thisAerosolOpticalDepth.tauTime.length][1];

        for (int i = 0; i < x.length; i++) {
            y[i][0] = thisAerosolOpticalDepth.beta[i];
            x[i][0] = thisAerosolOpticalDepth.tauTime[i];
        }

        String[] labels = {title, subtitle, xLabel, yLabel};
        double[] yLimits = {aeroScale[0], aeroScale[1]};

        DrawGraphOnCanvas can = new DrawGraphOnCanvas(x, y, labels, 1, 0, yLimits, 1);

        JFrame f = new PrintSampler(title, can);
        f.setSize(540, 480);
        f.addWindowListener(closingGraph);
        f.setVisible(true);

    }	// end showAngBeta()

//------------------------------------------------------------------------
    public void showAngAlpha() {
        String xLabel = "Time [UT hrs]";
        String yLabel = "Angstr�m Alpha";
        String title = ("Angstr�m Alpha");

        String subtitle = makeSubtitle();

        double[][] x = new double[thisAerosolOpticalDepth.tauTime.length][1];
        double[][] y = new double[thisAerosolOpticalDepth.tauTime.length][1];

        for (int i = 0; i < thisAerosolOpticalDepth.tauTime.length; i++) {
            y[i][0] = thisAerosolOpticalDepth.alpha[i];
            x[i][0] = thisAerosolOpticalDepth.tauTime[i];
        }

        String[] labels = {title, subtitle, xLabel, yLabel};
        double[] yLimits = {aeroScale[2], aeroScale[3]};

        DrawGraphOnCanvas can = new DrawGraphOnCanvas(x, y, labels, 1, 0, yLimits, 1);

        JFrame f = new PrintSampler(title, can);
        f.setSize(540, 480);
        f.addWindowListener(closingGraph);
        f.setVisible(true);

    }	// end showAngAlpha()

//------------------------------------------------------------------------
    public void showAerosolTable() {
        JFrame aeroframe = new JFrame();
        aeroframe.setSize(300, 300);
        JPanel pane = new JPanel();

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new GridLayout(3, 1));
        JLabel topString = new JLabel(("Aerosol Optical Depth Values on  " + Integer.toString(thisLevel2File.TheDate)));
        titlePanel.add(topString);
        if (thisLevel2File.station.exists && thisLevel2File.instrument.exists) {
            JLabel secondString = new JLabel(("in " + thisLevel2File.station.writtenData + " with " + thisLevel2File.instrument.writtenData));
            titlePanel.add(secondString);
        }
        JLabel thirdString = new JLabel("the following results were obtained:");
        titlePanel.add(thirdString);
        pane.add(titlePanel);

        JPanel aerostuff = new JPanel();
        aerostuff.setLayout(new GridLayout(5, 3));
        JLabel title1 = new JLabel("Lambda  ");
        JLabel title2 = new JLabel("V0      ");
        JLabel title3 = new JLabel("AOD       ");
        aerostuff.add(title1);
        aerostuff.add(title2);
        aerostuff.add(title3);

        JLabel[] channels = new JLabel[4];
        JLabel[] zeroPots = new JLabel[4];
        JLabel[] optDepths = new JLabel[4];
        for (int i = 0; i < 4; i++) {
            channels[i] = new JLabel(Double.toString(thisLevel2File.wavelength.numericalData[i]) + "    ");
            if (Double.toString(thisAerosolOpticalDepth.EV[i]).length() > 7) {
                zeroPots[i] = new JLabel((Double.toString(thisAerosolOpticalDepth.EV[i])).substring(0, 7) + "    ");
            } else {
                zeroPots[i] = new JLabel(Double.toString(thisAerosolOpticalDepth.EV[i]) + "    ");
            }
            if (Double.toString(thisAerosolOpticalDepth.mTau[i]).length() > 7) {
                optDepths[i] = new JLabel((Double.toString(thisAerosolOpticalDepth.mTau[i])).substring(0, 7) + "   ");
            } else {
                optDepths[i] = new JLabel((Double.toString(thisAerosolOpticalDepth.mTau[i])) + "   ");
            }
            aerostuff.add(channels[i]);
            aerostuff.add(zeroPots[i]);
            aerostuff.add(optDepths[i]);
        }
        pane.add(aerostuff);

        JPanel alphaPanel = new JPanel();
        JLabel angA = new JLabel("Average Angstr�m Alpha was:      ");
        JLabel angAVal;
        if (Double.toString(thisAerosolOpticalDepth.avgAlpha).length() > 7) {
            angAVal = new JLabel((Double.toString(thisAerosolOpticalDepth.avgAlpha)).substring(0, 7) + "   ");
        } else {
            angAVal = new JLabel((Double.toString(thisAerosolOpticalDepth.avgAlpha)) + "   ");
        }
        alphaPanel.add(angA);
        alphaPanel.add(angAVal);
        pane.add(alphaPanel);
        JPanel betaPanel = new JPanel();
        JLabel angB = new JLabel("Average Angstr�m Beta was:       ");
        JLabel angBVal;
        if (Double.toString(thisAerosolOpticalDepth.avgBeta).length() > 7) {
            angBVal = new JLabel((Double.toString(thisAerosolOpticalDepth.avgBeta)).substring(0, 7) + "   ");
        } else {
            angBVal = new JLabel(Double.toString(thisAerosolOpticalDepth.avgBeta) + "   ");
        }
        betaPanel.add(angB);
        betaPanel.add(angBVal);
        pane.add(betaPanel);

        JPanel meDaPa = new JPanel();
        JLabel press = new JLabel("Pressure [hPa]:  ");
        JLabel pressDa = new JLabel(Double.toString(meteDat[1]));
        JLabel ozo = new JLabel("  ///  Ozone [DU]:  ");
        JLabel ozoDa = new JLabel(Double.toString(meteDat[0]));
        meDaPa.add(press);
        meDaPa.add(pressDa);
        meDaPa.add(ozo);
        meDaPa.add(ozoDa);

        pane.add(meDaPa);

        aeroframe.setContentPane(pane);
        aeroframe.setVisible(true);
        aeroframe.addWindowListener(closingGraph);

    }	// end showAerosolTable()

//------------------------------------------------------------------------
    public void showLangley(boolean langleyOption) {
        String xLabel = "Airmass";
        String yLabel = "log [S/S_o]";
        String title = ("Langley-Plots");
        String subtitle = makeSubtitle();

        double[] yLimits = new double[2];

        if (selectedLangley == 0) {
            yLimits[0] = langleyScale[0];
            yLimits[1] = langleyScale[1];
            title = "Classic " + title;
        } else {
            yLimits[0] = langleyScale[2];
            yLimits[1] = langleyScale[3];
            title = "Refined " + title;
        }

        double[] xLimits = {0, selectedAirmassRange[1]};

        String[] labels = {title, subtitle, xLabel, yLabel};

        double[][] extendedAirmass = new double[thisLangleyExtrapol.plottingAirmass.length][thisLangleyExtrapol.numberOfChannels];
        double[] regSlope = new double[thisLangleyExtrapol.numberOfChannels];
        double[] regInter = new double[thisLangleyExtrapol.numberOfChannels];

        for (int i = 0; i < thisLangleyExtrapol.plottingAirmass.length; i++) {
            for (int j = 0; j < thisLangleyExtrapol.numberOfChannels; j++) {
                extendedAirmass[i][j] = thisLangleyExtrapol.plottingAirmass[i];
                regSlope[j] = thisLangleyExtrapol.calibrationSlope[j];
                regInter[j] = thisLangleyExtrapol.calibrationIntercept[j];
            } 	// since if handed over directly, they are changed ... and not sensible anymore at a second plotting.
        }
        DrawGraphOnCanvas can = new DrawGraphOnCanvas(extendedAirmass, thisLangleyExtrapol.plottingAbsorbtion, labels, thisLangleyExtrapol.numberOfChannels, 0, xLimits, yLimits);
        if (langleyOption) {
            can = new DrawGraphOnCanvas(extendedAirmass, thisLangleyExtrapol.plottingAbsorbtion, labels, thisLangleyExtrapol.numberOfChannels, 0, xLimits, yLimits, regSlope, regInter);
        }

        JFrame f = new PrintSampler(title, can);
        f.setSize(540, 480);
        f.addWindowListener(closingGraph);
        f.setVisible(true);

    }	// end showLangley()

//-------------------------------------------------------------------------
    public void showLangleyTable() {
        JFrame langleyframe = new JFrame();
        langleyframe.setSize(320, 300);
        JPanel pane = new JPanel();

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new GridLayout(3, 1));
        JLabel topString;
        if (selectedLangley == 0) {
            topString = new JLabel(("Classic Langley Calibration on  " + Integer.toString(thisLevel2File.TheDate)));
        } else if (selectedLangley == 1) {
            topString = new JLabel(("Refined Langley Calibration on  " + Integer.toString(thisLevel2File.TheDate)));
        } else {
            topString = new JLabel(("Unknown Langley Calibration on  " + Integer.toString(thisLevel2File.TheDate)));
        }
        titlePanel.add(topString);
        if (thisLevel2File.station.exists && thisLevel2File.instrument.exists) {
            JLabel secondString = new JLabel(("in " + thisLevel2File.station.writtenData + " with " + thisLevel2File.instrument.writtenData));
            titlePanel.add(secondString);
        }
        JLabel thirdString = new JLabel("the following results were obtained:");
        titlePanel.add(thirdString);
        pane.add(titlePanel);

        JPanel langleystuff = new JPanel();
        langleystuff.setLayout(new GridLayout(5, 4));
        JLabel title0 = new JLabel("Lambda    ");
        JLabel title1 = new JLabel("  V_1     ");
        JLabel title2 = new JLabel("Slope     ");
        JLabel title3 = new JLabel("Ratio to V_o  ");
        langleystuff.add(title0);
        langleystuff.add(title1);
        langleystuff.add(title2);
        langleystuff.add(title3);

        JLabel[] channels = new JLabel[4];
        JLabel[] ratios = new JLabel[4];
        JLabel[] slopes = new JLabel[4];
        JLabel[] calibrate = new JLabel[4];
        for (int i = 0; i < 4; i++) {
            channels[i] = new JLabel(Double.toString(thisLevel2File.wavelength.numericalData[i]) + "    ");
            if (Double.toString(thisLangleyExtrapol.Calibrate[i]).length() > 7) {
                calibrate[i] = new JLabel(Double.toString(thisLangleyExtrapol.Calibrate[i]).substring(0, 7) + "    ");
            } else {
                calibrate[i] = new JLabel(Double.toString(thisLangleyExtrapol.Calibrate[i]) + "    ");
            }
            if (Double.toString(thisLangleyExtrapol.calibrationSlope[i]).length() > 7) {
                slopes[i] = new JLabel((Double.toString(thisLangleyExtrapol.calibrationSlope[i])).substring(0, 7) + "   ");
            } else {
                slopes[i] = new JLabel((Double.toString(thisLangleyExtrapol.calibrationSlope[i])) + "   ");
            }
            if (Double.toString(thisLangleyExtrapol.Ratios[i]).length() > 7) {
                ratios[i] = new JLabel(Double.toString(thisLangleyExtrapol.Ratios[i]).substring(0, 7) + "    ");
            } else {
                ratios[i] = new JLabel(Double.toString(thisLangleyExtrapol.Ratios[i]) + "    ");
            }

            langleystuff.add(channels[i]);
            langleystuff.add(calibrate[i]);
            langleystuff.add(slopes[i]);
            langleystuff.add(ratios[i]);
        }

        pane.add(langleystuff);

        JPanel pointPanel = new JPanel();
        pointPanel.setLayout(new GridLayout(1, 2));
        JLabel noPoi = new JLabel("Number of points:   >=   ");
        JLabel noPoint = new JLabel(Integer.toString(thisLangleyExtrapol.minPoints));
        pointPanel.add(noPoi);
        pointPanel.add(noPoint);
        pane.add(pointPanel);

        JPanel timePanel = new JPanel();
        timePanel.setLayout(new GridLayout(1, 2));
        JLabel timeRange = new JLabel("Period:  ");
        timePanel.add(timeRange);
        JLabel timeData = new JLabel("from   " + Double.toString(thisLevel2File.BodyData[thisLangleyExtrapol.indexData[0]][0]) + "    to    " + Double.toString(thisLevel2File.BodyData[thisLangleyExtrapol.indexData[1]][0]));
        timePanel.add(timeData);

        pane.add(timePanel);
        langleyframe.setContentPane(pane);
        langleyframe.setVisible(true);

        langleyframe.addWindowListener(closingGraph);

    }	// end showLangleyTable()

//========================================================================
//-------------scaling stuff for Aerosol and Langley  --------------------
//========================================================================
    public void scaleAero() {
        JPanel pane = new JPanel();
        BorderLayout bordy = new BorderLayout();
        pane.setLayout(bordy);
        pane.add("North", menuBar);

        JPanel questionPanel = new JPanel();
        GridLayout gridy = new GridLayout(7, 1);
        questionPanel.setLayout(gridy);

        scalingTextField = new JTextField[4];
        for (int i = 0; i < 4; i++) {
            scalingTextField[i] = new JTextField(Double.toString(aeroScale[i]), 5);
        }

        JLabel question = new JLabel("Please, set the limit values for the y-Axis");
        JLabel secPart = new JLabel("wherein the data shall be plotted:");
        JLabel AODLimits = new JLabel("Limits for Angstr�m Beta and AOD Plots");
        JLabel alphaLimits = new JLabel("Limits for Angstr�m Alpha");
        JLabel from = new JLabel("from  ");
        JLabel to = new JLabel("   to   ");
        JLabel from1 = new JLabel("from  ");
        JLabel to1 = new JLabel("   to   ");
        JPanel firstP = new JPanel();
        JPanel secondP = new JPanel();
        JPanel thirdP = new JPanel();
        JPanel fourthP = new JPanel();

        questionPanel.add(question);
        questionPanel.add(secPart);

        firstP.add(AODLimits);
        secondP.add(from);
        secondP.add(scalingTextField[0]);
        secondP.add(to);
        secondP.add(scalingTextField[1]);

        thirdP.add(alphaLimits);
        fourthP.add(from1);
        fourthP.add(scalingTextField[2]);
        fourthP.add(to1);
        fourthP.add(scalingTextField[3]);

        questionPanel.add(firstP);
        questionPanel.add(secondP);
        questionPanel.add(thirdP);
        questionPanel.add(fourthP);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okbScaleAero);
        buttonPanel.add(ccb);

        questionPanel.add(buttonPanel);

        pane.add("Center", questionPanel);

        JLabel commentLabel = new JLabel("Please choose appropriate Scaling.");
        pane.add("South", commentLabel);

        setContentPane(pane);
        setVisible(true);

    }	// end scaleAero()

//-------------------------------------------------------------------------
    public void scaleLangley() {
        JPanel pane = new JPanel();
        BorderLayout bordy = new BorderLayout();
        pane.setLayout(bordy);
        pane.add("North", menuBar);

        JPanel questionPanel = new JPanel();
        GridLayout gridy = new GridLayout(7, 1);
        questionPanel.setLayout(gridy);

        scalingTextField = new JTextField[4];
        for (int i = 0; i < 4; i++) {
            scalingTextField[i] = new JTextField(Double.toString(langleyScale[i]), 5);
        }

        JLabel question = new JLabel("Please, set the limit values for the y-Axis");
        JLabel secPart = new JLabel("wherein the data shall be plotted:");
        JLabel classicLimits = new JLabel("Limits for the classic Langley Plots");
        JLabel refinedLimits = new JLabel("Limits for the refined Langley Plots");
        JLabel from1 = new JLabel("from  ");
        JLabel to1 = new JLabel("   to   ");
        JLabel from = new JLabel("from  ");
        JLabel to = new JLabel("   to   ");
        JPanel firstP = new JPanel();
        JPanel secondP = new JPanel();
        JPanel thirdP = new JPanel();
        JPanel fourthP = new JPanel();

        questionPanel.add(question);
        questionPanel.add(secPart);

        firstP.add(classicLimits);
        secondP.add(from1);
        secondP.add(scalingTextField[0]);
        secondP.add(to1);
        secondP.add(scalingTextField[1]);

        thirdP.add(refinedLimits);
        fourthP.add(from);
        fourthP.add(scalingTextField[2]);
        fourthP.add(to);
        fourthP.add(scalingTextField[3]);

        questionPanel.add(firstP);
        questionPanel.add(secondP);
        questionPanel.add(thirdP);
        questionPanel.add(fourthP);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okbScaleLangley);
        buttonPanel.add(ccb);

        questionPanel.add(buttonPanel);

        pane.add("Center", questionPanel);

        JLabel commentLabel = new JLabel("Please choose appropriate Scaling.");
        pane.add("South", commentLabel);

        setContentPane(pane);
        setVisible(true);

    }	// end scaleLangley()

//========================================================================
// 		menu run,
// the different run options
//========================================================================
    public void langleyRun() {
        if (chosenFile == null) {
            display.displayFileWarning();
        } else {
            display.displayWorking();

            selectedAirmassRange[0] = selectedLowerAirmassRange;
            selectedAirmassRange[1] = selectedUpperAirmassRange;

            ReadLevel2File TheContent = new ReadLevel2File();
            TheContent.TheMain(chosenFile);

            if (selectedMete == 3) {
                meteoReader(TheContent.TheDate, TheContent);
            }
            if (selectedMete == 0) //not else if, since if date not found, selectedMete is set to 0
            {
                if (TheContent.airtemp.exists) {
                    meteDat[3] = TheContent.airtemp.numericalData[0];
                }
                if (TheContent.airpress.exists) {
                    meteDat[1] = TheContent.airpress.numericalData[0];
                }
                if (TheContent.ozone.exists) {
                    meteDat[0] = TheContent.ozone.numericalData[0];
                }
            }

            int[] DayData;
            DoTheCheck TheCheck = new DoTheCheck();
            DayData = TheCheck.DoFirstCheck(TheContent);

            LangleyExtrapol MyExtrapolation = new LangleyExtrapol(DayData, TheContent, meteDat, selectedPeriod);
            MyExtrapolation.theMain(selectedLangley, selectedAirmassRange);

            thisLevel2File = TheContent;
            thisDayData = DayData;
            thisLangleyExtrapol = MyExtrapolation;
            if (thisLangleyExtrapol.notEnoughPoints) {
                display.displayNotEnoughPoints();
            } else {
                String calfilename;
                String aodfilename;
                String logfilename;

                if (TheContent.instident.exists) {
                    calfilename = "logfiles/" + TheContent.instrument.writtenData + ".cal";
                    logfilename = "logfiles/" + TheContent.instrument.writtenData + ".log";
                } else {
                    calfilename = "logfiles/" + chosenFile.getName() + ".cal";
                    logfilename = "logfiles/" + chosenFile.getName() + ".log";
                }

                WriteLogFiles logger = new WriteLogFiles();
                File calFile = new File(programDirectory, calfilename);
                File logFile = new File(programDirectory, logfilename);

                if (logFilesWriting) {
                    logger.WriteCALFile(calFile, thisLevel2File, thisLangleyExtrapol);
                    logger.WriteLOGFile(logFile, thisLevel2File, thisLangleyExtrapol, warnLog);
                }
                warnLog.create();
                display.displayFinished(0);
            }
        }

    }	// end langleyRun()

//------------------------------------------------------------------------
    public void aeroRun() {
        if (chosenFile == null) {
            display.displayFileWarning();
        } else {
            display.displayWorking();

            selectedAirmassRange[0] = selectedLowerAirmassRange;
            selectedAirmassRange[1] = selectedUpperAirmassRange;

            ReadLevel2File TheContent = new ReadLevel2File();
            TheContent.TheMain(chosenFile);

            if (selectedMete == 3) {
                meteoReader(TheContent.TheDate, TheContent);
            }
            if (selectedMete == 0) //not else if, since if date not found, selectedMete is set to 0
            {
                if (TheContent.airtemp.exists) {
                    meteDat[3] = TheContent.airtemp.numericalData[0];
                }
                if (TheContent.airpress.exists) {
                    meteDat[1] = TheContent.airpress.numericalData[0];
                }
                if (TheContent.ozone.exists) {
                    meteDat[0] = TheContent.ozone.numericalData[0];
                }
            }

            int[] DayData;
            DoTheCheck TheCheck = new DoTheCheck();
            DayData = TheCheck.DoFirstCheck(TheContent);

            String calfilename;
            String aodfilename;
            String logfilename;

            WriteLogFiles logger = new WriteLogFiles();

            if (TheContent.instident.exists) {
                calfilename = "logfiles/" + TheContent.instrument.writtenData + ".cal";
                aodfilename = "logfiles/" + TheContent.instrument.writtenData + ".aod";
                logfilename = "logfiles/" + TheContent.instrument.writtenData + ".log";
            } else {
                calfilename = "logfiles/" + chosenFile.getName() + ".cal";
                aodfilename = "logfiles/" + chosenFile.getName() + ".aod";
                logfilename = "logfiles/" + chosenFile.getName() + ".log";
            }

            File calFile = new File(programDirectory, calfilename);
            File aodFile = new File(programDirectory, aodfilename);
            File logFile = new File(programDirectory, logfilename);

            LangleyExtrapol MyExtrapolation = new LangleyExtrapol(DayData, TheContent, meteDat, selectedPeriod);
            if (selectedAerov0 == 2) {
                MyExtrapolation.theMain(selectedLangley, selectedAirmassRange);
                this.thisLangleyExtrapol = MyExtrapolation;

                if (MyExtrapolation.notEnoughPoints) {
                    display.displayNotEnoughPoints();
                } else {
                    AerosolOpticalDepth AeroDepth = new AerosolOpticalDepth(TheContent);
                    AeroDepth.TheMain(MyExtrapolation, DayData, aerov0Values, meteDat, selectedAerov0, selectedPeriod);

                    thisLevel2File = TheContent;
                    thisDayData = DayData;
                    thisLangleyExtrapol = MyExtrapolation;
                    thisAerosolOpticalDepth = AeroDepth;
                    if (logFilesWriting) {
                        logger.WriteAODFile(aodFile, thisLevel2File, thisAerosolOpticalDepth);
                        logger.WriteCALFile(calFile, thisLevel2File, thisLangleyExtrapol);
                        logger.WriteLOGFile(logFile, thisLevel2File, thisLangleyExtrapol, warnLog);
                        logger.WriteLOGFile(logFile, thisLevel2File, thisAerosolOpticalDepth, warnLog);
                    }
                    display.displayFinished(1);
                }
            } else {
                AerosolOpticalDepth AeroDepth = new AerosolOpticalDepth(TheContent);
                AeroDepth.TheMain(MyExtrapolation, DayData, aerov0Values, meteDat, selectedAerov0, selectedPeriod);

                thisLevel2File = TheContent;
                thisDayData = DayData;
                thisLangleyExtrapol = MyExtrapolation;
                thisAerosolOpticalDepth = AeroDepth;
                if (logFilesWriting) {
                    logger.WriteAODFile(aodFile, thisLevel2File, thisAerosolOpticalDepth);
                    logger.WriteLOGFile(logFile, thisLevel2File, thisAerosolOpticalDepth, warnLog);
                }
                warnLog.create();
                display.displayFinished(1);
            }
        }

    }	// end aeroRun()

//------------------------------------------------------------------------
    public void actionPerformed(ActionEvent evt) {	//if (!evt.getValueIsAdjusting())
        {
            Object source = evt.getSource();
            if (source instanceof JButton) {
                if (source == ccb) {
                    display.displayCancelProceedScreen();
                } else if (source == okbScaleAero) {
                    for (int i = 0; i < aeroScale.length; i++) {
                        aeroScale[i] = Double.parseDouble(scalingTextField[i].getText());
                    }
                    scalingWriter();
                    display.displayAcceptedProceedScreen();
                } else if (source == okbScaleLangley) {
                    for (int i = 0; i < langleyScale.length; i++) {
                        langleyScale[i] = Double.parseDouble(scalingTextField[i].getText());
                    }
                    scalingWriter();
                    display.displayAcceptedProceedScreen();
                }
            } else if (source instanceof JMenuItem) {
                if (source == open) {
                    display.displayAcceptedProceedScreen();
                    fileOpener();
                } else if (source == close) {
                    display.displayAcceptedProceedScreen();
                    meisterPropper();
                } else if (source == writeLevel3) {
                    display.displayAcceptedProceedScreen();
                    OutputWriter out = new OutputWriter();
                    out.level3Writer(this);
                } else if (source == writeLangley) {
                    display.displayAcceptedProceedScreen();
                    OutputWriter out = new OutputWriter();
                    out.langleyWriter(this);
                } else if (source == exit) {
                    System.exit(0);
                } else if (source == periode) {
                    myOpt.periodSelection();
                } else if (source == langley) {
                    myOpt.langleySelection();
                } else if (source == airrange) {
                    myOpt.airrangeSelection();
                } else if (source == mete) {
                    myOpt.meteSelection();
                } else if (source == aerov0) {
                    myOpt.aerov0Selection();
                } else if (source == getDefault) {
                    resetDefault();
                    display.displayAcceptedProceedScreen();
                } else if (source == setDefault) {
                    myOpt.enterDefaultSettings();
                } else if (source == signal) {
                    showSignals();
                } else if (source == pointing) {
                    showPointing();
                } else if (source == temp) {
                    showTemperature();
                } else if (source == head) {
                    showHeader();
                } else if (source == opticalDepth) {
                    showAeroplot();
                } else if (source == langleyGraph) {
                    showLangley(false);
                } else if (source == langleyReg) {
                    showLangley(true);
                } else if (source == angBeta) {
                    showAngBeta();
                } else if (source == angAlpha) {
                    showAngAlpha();
                } else if (source == aerosolTable) {
                    showAerosolTable();
                } else if (source == langleyTable) {
                    showLangleyTable();
                } else if (source == aeroScaling) {
                    scaleAero();
                } else if (source == langleyScaling) {
                    scaleLangley();
                } else if (source == runaero) {
                    aeroRun();
                } else if (source == runlangley) {
                    langleyRun();
                } else if (source == about) {
                    display.displayAbout();
                } else if (source == manual) {
                    display.displayAcceptedProceedScreen();
                    helpframe = new HelpWindow();

                    WindowListener helpHelp = new WindowAdapter() {
                        public void windowClosing(WindowEvent e) {
                            helpframe.setVisible(false);
                        }
                    };
                    helpframe.addWindowListener(helpHelp);
                    helpframe.setVisible(true);
                }
            }

        }
    }	// end actionPerformed(......

//-----------------------------------------------------------------------
    public static void main(String[] args) {
        JFrame frame = new MainWindow();

        WindowListener l = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        };

        frame.addWindowListener(l);
        frame.pack();
        frame.setVisible(true);

    }	// end main(...)
}	// end .class
