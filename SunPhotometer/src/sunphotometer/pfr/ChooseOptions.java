package sunphotometer.pfr;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ChooseOptions implements ActionListener {

    MainWindow mainy;

    JButton ccb = new JButton("Cancel");

    //---All the crap for Period selection---
    JRadioButton optPeriod[] = new JRadioButton[3];
    ButtonGroup grpPeriod = new ButtonGroup();
    JButton okbPeriod = new JButton("OK");
    int preliminarySelectedPeriod;

    //---All the crap for Langley method selection---
    JRadioButton optLangley[] = new JRadioButton[3];
    ButtonGroup grpLangley = new ButtonGroup();
    JButton okbLangley = new JButton("OK");
    int preliminarySelectedLangley;

    //---Airrange settings-------------------
    JTextField airrangeTextField0;
    JTextField airrangeTextField1;
    JButton okbAirrange = new JButton("OK");

    //--- Meteo settings ---
    JRadioButton[] optMete = new JRadioButton[4];
    ButtonGroup grpMete = new ButtonGroup();
    JButton okbMete = new JButton("OK");
    JButton okbSetMete = new JButton("OK");
    int preliminarySelectedMete;
    JTextField[] meteSetTextField = new JTextField[4];
    File helpSelectedMeteFile = null;
    JButton chooseMeteFile = new JButton("Choose a File containing Meteorological Data");

    //---for V0 settings (aerov0)---
    JRadioButton[] optAerov0 = new JRadioButton[4];
    ButtonGroup grpAerov0 = new ButtonGroup();
    JButton okbAerov0 = new JButton("OK");
    int preliminarySelectedAerov0;

    //--- for manual setting of V0
    JButton okbSetAerov0 = new JButton("OK");
    JTextField[] aerov0SetTextField = new JTextField[4];

    //---for Default settings --
    JButton okbSetDefault = new JButton("OK");
    JButton ccbSetDefault = new JButton("Cancel");
    JFrame settingsFrame = new JFrame("My Default Settings");

    public ChooseOptions(MainWindow mainy) {
        this.mainy = mainy;

        // add all the ActionListeners to the cancel and ok buttons
        ccb.addActionListener(this);
        okbPeriod.addActionListener(this);
        okbLangley.addActionListener(this);
        okbAirrange.addActionListener(this);
        okbMete.addActionListener(this);
        okbSetMete.addActionListener(this);
        okbAerov0.addActionListener(this);
        okbSetAerov0.addActionListener(this);
        chooseMeteFile.addActionListener(this);
        okbSetDefault.addActionListener(this);
        ccbSetDefault.addActionListener(this);

    }

//----------------------------------------------------------------------------
    public void periodSelection() {
        preliminarySelectedPeriod = mainy.selectedPeriod;
        JPanel pane = new JPanel();
        BorderLayout bordy = new BorderLayout();
        pane.setLayout(bordy);
        pane.add("North", mainy.menuBar);

        JPanel questionPanel = new JPanel();
        GridLayout gridy = new GridLayout(5, 1);
        questionPanel.setLayout(gridy);

        JLabel question = new JLabel("Over what period of time shall the desired run be done?");
        optPeriod[0] = new JRadioButton("Whole Day");
        optPeriod[1] = new JRadioButton("Morning only");
        optPeriod[2] = new JRadioButton("Afternoon only");

        questionPanel.add(question);

        for (int i = 0; i < 3; i++) {
            optPeriod[i].addActionListener(this);
            grpPeriod.add(optPeriod[i]);
            questionPanel.add(optPeriod[i]);
        }

        optPeriod[mainy.selectedPeriod].setSelected(true);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okbPeriod);
        buttonPanel.add(ccb);

        questionPanel.add(buttonPanel);

        pane.add("Center", questionPanel);

        JLabel commentLabel = new JLabel("Please select a time range.");
        pane.add("South", commentLabel);

        mainy.setContentPane(pane);
        mainy.setVisible(true);

    }	// end periodSelection()

//-----------------------------------------------------------------------
    public void langleySelection() {
        preliminarySelectedLangley = mainy.selectedLangley;
        JPanel pane = new JPanel();
        BorderLayout bordy = new BorderLayout();
        pane.setLayout(bordy);
        pane.add("North", mainy.menuBar);

        JPanel questionPanel = new JPanel();
        GridLayout gridy = new GridLayout(5, 1);
        questionPanel.setLayout(gridy);

        JLabel question = new JLabel("What Langley Calibration Method would you like?");
        optLangley[0] = new JRadioButton("Classic");
        optLangley[1] = new JRadioButton("Refined");
        optLangley[2] = new JRadioButton("Ratio");

        questionPanel.add(question);
        optLangley[2].setEnabled(false);

        for (int i = 0; i < 3; i++) {
            optLangley[i].addActionListener(this);
            grpLangley.add(optLangley[i]);
            questionPanel.add(optLangley[i]);
        }

        optLangley[mainy.selectedLangley].setSelected(true);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okbLangley);
        buttonPanel.add(ccb);

        questionPanel.add(buttonPanel);

        pane.add("Center", questionPanel);

        JLabel commentLabel = new JLabel("     ");
        pane.add("South", commentLabel);

        mainy.setContentPane(pane);
        mainy.setVisible(true);

    }	// end langleySelection

//-----------------------------------------------------------------------
    public void airrangeSelection() {
        JPanel pane = new JPanel();
        BorderLayout bordy = new BorderLayout();
        pane.setLayout(bordy);
        pane.add("North", mainy.menuBar);

        JPanel questionPanel = new JPanel();
        GridLayout gridy = new GridLayout(5, 1);
        questionPanel.setLayout(gridy);

        airrangeTextField0 = new JTextField(Double.toString(mainy.selectedLowerAirmassRange), 4);
        airrangeTextField1 = new JTextField(Double.toString(mainy.selectedUpperAirmassRange), 4);

        JLabel question = new JLabel("Please set the limits for the Airmassfilter,");
        JLabel secPart = new JLabel("any data point not lying in these boudaries will be ignored.");
        JLabel loaima = new JLabel("Lower airmass limit");
        JLabel upaima = new JLabel("Upper airmass limit");
        JPanel firstP = new JPanel();
        JPanel secondP = new JPanel();

        questionPanel.add(question);
        questionPanel.add(secPart);

        firstP.add(loaima);
        secondP.add(upaima);
        firstP.add(airrangeTextField0);
        secondP.add(airrangeTextField1);
        questionPanel.add(firstP);
        questionPanel.add(secondP);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okbAirrange);
        buttonPanel.add(ccb);

        questionPanel.add(buttonPanel);

        pane.add("Center", questionPanel);

        JLabel commentLabel = new JLabel("The recommended Airmasslimits are about 2 and 6.");
        pane.add("South", commentLabel);

        mainy.setContentPane(pane);
        mainy.setVisible(true);

    }	// end airrangeSelection()
//------------------------------------------------------------------------

    public void airrangeErrorSelection() {
        JPanel pane = new JPanel();
        BorderLayout bordy = new BorderLayout();
        pane.setLayout(bordy);
        pane.add("North", mainy.menuBar);

        JPanel questionPanel = new JPanel();
        GridLayout gridy = new GridLayout(5, 1);
        questionPanel.setLayout(gridy);

        airrangeTextField0 = new JTextField(Double.toString(mainy.selectedLowerAirmassRange), 4);
        airrangeTextField1 = new JTextField(Double.toString(mainy.selectedUpperAirmassRange), 4);

        JLabel question = new JLabel("The upper airmass limit must be bigger than the lower!");
        JLabel secPart = new JLabel("Please check your values.");
        JLabel loaima = new JLabel("Lower airmass limit");
        JLabel upaima = new JLabel("Upper airmass limit");
        JPanel firstP = new JPanel();
        JPanel secondP = new JPanel();

        questionPanel.add(question);
        questionPanel.add(secPart);

        firstP.add(loaima);
        secondP.add(upaima);
        firstP.add(airrangeTextField0);
        secondP.add(airrangeTextField1);
        questionPanel.add(firstP);
        questionPanel.add(secondP);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okbAirrange);
        buttonPanel.add(ccb);

        questionPanel.add(buttonPanel);

        pane.add("Center", questionPanel);

        JLabel commentLabel = new JLabel("The recommended Airmasslimits are about 2 and 6.");
        pane.add("South", commentLabel);

        mainy.setContentPane(pane);
        mainy.setVisible(true);

    }	// end airrangeErrorSelection()

//------------------------------------------------------------------------
    public void meteSelection() {
        preliminarySelectedMete = mainy.selectedMete;
        JPanel pane = new JPanel();
        BorderLayout bordy = new BorderLayout();
        pane.setLayout(bordy);
        pane.add("North", mainy.menuBar);

        JPanel questionPanel = new JPanel();
        GridLayout gridy = new GridLayout(7, 1);
        questionPanel.setLayout(gridy);

        JLabel question = new JLabel("What meteorological data would you like to use?");
        optMete[0] = new JRadioButton("Use actual values (= from Header if available, else default).");
        optMete[1] = new JRadioButton("Specify new values.");
        optMete[2] = new JRadioButton("Use default values.");
        optMete[3] = new JRadioButton("Read meteorological data from file.");
        questionPanel.add(question);

        for (int i = 0; i < 4; i++) {
            optMete[i].addActionListener(this);
            grpMete.add(optMete[i]);
            questionPanel.add(optMete[i]);
        }

        // these Textfields are not needed here, but they must exist in order to prevent nullPointerExceptions..
        meteSetTextField = new JTextField[4];
        for (int i = 0; i < 4; i++) {
            meteSetTextField[i] = new JTextField(Double.toString(mainy.meteDat[i]), 6);
        }

        // as long as no read from file available:
        optMete[mainy.selectedMete].setSelected(true);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okbMete);
        buttonPanel.add(ccb);

        questionPanel.add(buttonPanel);

        pane.add("Center", questionPanel);

        JLabel commentLabel = new JLabel("Please select meteorological data.");
        pane.add("South", commentLabel);

        mainy.setContentPane(pane);
        mainy.setVisible(true);

    }	// end meteSelection()
//-----------------------------------------------------------------------

    public void setMeteDat() {
        JPanel pane = new JPanel();
        BorderLayout bordy = new BorderLayout();
        pane.setLayout(bordy);
        pane.add("North", mainy.menuBar);

        JPanel questionPanel = new JPanel();
        GridLayout gridy = new GridLayout(6, 1);
        questionPanel.setLayout(gridy);

        meteSetTextField = new JTextField[4];

        meteSetTextField[0] = new JTextField(Double.toString(mainy.meteDat[0]), 6);
        meteSetTextField[1] = new JTextField(Double.toString(mainy.meteDat[1]), 6);
        meteSetTextField[2] = new JTextField(Double.toString(mainy.meteDat[2]), 6);
        meteSetTextField[3] = new JTextField(Double.toString(mainy.meteDat[3]), 6);

        JLabel question = new JLabel("Please select appropriate meteorological data:");
        JLabel ozone = new JLabel("Ozone [DU]");
        JLabel pressure = new JLabel("Pressure [hPa]");
        JLabel humidity = new JLabel("Humidity [%]");
        JLabel temperature = new JLabel("Temperature [�C]");

        JPanel firstP = new JPanel();
        JPanel secondP = new JPanel();
        JPanel thirdP = new JPanel();
        JPanel fourthP = new JPanel();

        firstP.add(ozone);
        firstP.add(meteSetTextField[0]);
        secondP.add(pressure);
        secondP.add(meteSetTextField[1]);
        thirdP.add(humidity);
        thirdP.add(meteSetTextField[2]);
        fourthP.add(temperature);
        fourthP.add(meteSetTextField[3]);

        questionPanel.add(question);
        questionPanel.add(firstP);
        questionPanel.add(secondP);
        questionPanel.add(thirdP);
        questionPanel.add(fourthP);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okbSetMete);
        buttonPanel.add(ccb);

        questionPanel.add(buttonPanel);

        pane.add("Center", questionPanel);

        JLabel commentLabel = new JLabel("Please select reasonable values...");
        pane.add("South", commentLabel);

        mainy.setContentPane(pane);
        mainy.setVisible(true);
    }	// end setMeteDat()

//-----------------------------------------------------------------------
    public void aerov0Selection() {
        preliminarySelectedAerov0 = mainy.selectedAerov0;

        JPanel pane = new JPanel();
        BorderLayout bordy = new BorderLayout();
        pane.setLayout(bordy);
        pane.add("North", mainy.menuBar);

        JPanel questionPanel = new JPanel();
        GridLayout gridy = new GridLayout(6, 1);
        questionPanel.setLayout(gridy);

        JLabel question = new JLabel("Which V0 would you like for the AOD calculation?");
        optAerov0[0] = new JRadioButton("Default Values from Header");
        optAerov0[1] = new JRadioButton("Interpolated Values to actual Date");
        optAerov0[2] = new JRadioButton("Values from current Langley calibration");
        optAerov0[3] = new JRadioButton("Enter values manually.");

        questionPanel.add(question);

        //These textfields are not needed here, but they must exist, since otherwise, the actionListenern produces errors..
        for (int i = 0; i < 4; i++) {
            aerov0SetTextField[i] = new JTextField(Double.toString(mainy.aerov0Values[i]), 6);
        }

        for (int i = 0; i < 4; i++) {
            optAerov0[i].addActionListener(this);
            grpAerov0.add(optAerov0[i]);
            questionPanel.add(optAerov0[i]);
        }

        optAerov0[mainy.selectedAerov0].setSelected(true);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okbAerov0);
        buttonPanel.add(ccb);

        questionPanel.add(buttonPanel);

        pane.add("Center", questionPanel);

        JLabel commentLabel = new JLabel("Find a suitable value for the V0.");
        pane.add("South", commentLabel);

        mainy.setContentPane(pane);
        mainy.setVisible(true);

    }	// end aerov0Selection()

//------------------------------------------------------------------------
    public void setAerov0() {
        JPanel pane = new JPanel();
        BorderLayout bordy = new BorderLayout();
        pane.setLayout(bordy);
        pane.add("North", mainy.menuBar);

        JPanel questionPanel = new JPanel();
        GridLayout gridy = new GridLayout(6, 1);
        questionPanel.setLayout(gridy);

        JLabel question = new JLabel("Please enter new V0 for the four channels [Volt]");
        questionPanel.add(question);

        JPanel fieldSet0 = new JPanel();
        JPanel fieldSet1 = new JPanel();
        JPanel fieldSet2 = new JPanel();
        JPanel fieldSet3 = new JPanel();

        JLabel[] textFieldComment = new JLabel[4];

        textFieldComment[0] = new JLabel("862nm Channel");
        textFieldComment[1] = new JLabel("500nm Channel");
        textFieldComment[2] = new JLabel("412nm Channel");
        textFieldComment[3] = new JLabel("368nm Channel");

        for (int i = 0; i < 4; i++) {
            aerov0SetTextField[i] = new JTextField(Double.toString(mainy.aerov0Values[i]), 6);
        }

        fieldSet0.add(textFieldComment[0]);
        fieldSet1.add(textFieldComment[1]);
        fieldSet2.add(textFieldComment[2]);
        fieldSet3.add(textFieldComment[3]);

        fieldSet0.add(aerov0SetTextField[0]);
        fieldSet1.add(aerov0SetTextField[1]);
        fieldSet2.add(aerov0SetTextField[2]);
        fieldSet3.add(aerov0SetTextField[3]);

        questionPanel.add(fieldSet0);
        questionPanel.add(fieldSet1);
        questionPanel.add(fieldSet2);
        questionPanel.add(fieldSet3);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okbSetAerov0);
        buttonPanel.add(ccb);

        questionPanel.add(buttonPanel);

        pane.add("Center", questionPanel);

        JLabel commentLabel = new JLabel("Please specify the actual values...");
        pane.add("South", commentLabel);

        mainy.setContentPane(pane);
        mainy.setVisible(true);

    }	// end setAerov0()

//--------------------------------------------------------------------------
    public void enterDefaultSettings() {
        if (mainy.selectedMeteFile != null) {
            helpSelectedMeteFile = new File(mainy.selectedMeteFile.getParent(), mainy.selectedMeteFile.getName());
        } else {
            helpSelectedMeteFile = null;
        }

        settingsFrame.setSize(560, 600);

        preliminarySelectedPeriod = mainy.defaultSelectedPeriod;
        preliminarySelectedLangley = mainy.defaultSelectedLangley;
        preliminarySelectedMete = mainy.defaultSelectedMete;
        preliminarySelectedAerov0 = mainy.defaultSelectedAerov0;

        //-------------
        JPanel periodPanel = new JPanel();
        GridLayout gridy1 = new GridLayout(2, 1);
        periodPanel.setLayout(gridy1);

        JPanel periodOptPanel = new JPanel();
        GridLayout gridy2 = new GridLayout(1, 3);
        periodOptPanel.setLayout(gridy2);

        JLabel periodQuestion = new JLabel("Over what period of time shall the desired run be done?");
        optPeriod[0] = new JRadioButton("Whole Day");
        optPeriod[1] = new JRadioButton("Morning only");
        optPeriod[2] = new JRadioButton("Afternoon only");

        periodPanel.add(periodQuestion);

        for (int i = 0; i < 3; i++) {
            optPeriod[i].addActionListener(this);
            grpPeriod.add(optPeriod[i]);
            periodOptPanel.add(optPeriod[i]);
        }

        optPeriod[mainy.defaultSelectedPeriod].setSelected(true);

        periodPanel.add(periodOptPanel);
        //------------------------

        JPanel langleyPanel = new JPanel();
        langleyPanel.setLayout(gridy1);

        JPanel langleyOptPanel = new JPanel();
        langleyOptPanel.setLayout(gridy2);

        JLabel langleyQuestion = new JLabel("What Langley Calibration Method would you like?");
        optLangley[0] = new JRadioButton("Classic");
        optLangley[1] = new JRadioButton("Refined");
        optLangley[2] = new JRadioButton("Ratio");

        langleyPanel.add(langleyQuestion);
        optLangley[2].setEnabled(false);

        for (int i = 0; i < 3; i++) {
            optLangley[i].addActionListener(this);
            grpLangley.add(optLangley[i]);
            langleyOptPanel.add(optLangley[i]);
        }

        optLangley[mainy.defaultSelectedLangley].setSelected(true);

        langleyPanel.add(langleyOptPanel);

        //--------------------
        JPanel airrangePanel = new JPanel();
        GridLayout gridy3 = new GridLayout(3, 1);
        airrangePanel.setLayout(gridy3);

        airrangeTextField0 = new JTextField(Double.toString(mainy.defaultSelectedAirmassRange[0]), 4);
        airrangeTextField1 = new JTextField(Double.toString(mainy.defaultSelectedAirmassRange[1]), 4);

        JLabel airrangeQuestion = new JLabel("Please set the limits for the Airmassfilter, any data point outside will be ignored.");
        JLabel loaima = new JLabel("Lower airmass limit");
        JLabel upaima = new JLabel("Upper airmass limit");
        JPanel firstP = new JPanel();
        JPanel secondP = new JPanel();

        airrangePanel.add(airrangeQuestion);

        firstP.add(loaima);
        secondP.add(upaima);
        firstP.add(airrangeTextField0);
        secondP.add(airrangeTextField1);
        airrangePanel.add(firstP);
        airrangePanel.add(secondP);

        //---------------------
        JPanel metePanel = new JPanel();
        GridLayout gridy6 = new GridLayout(7, 1);
        metePanel.setLayout(gridy6);

        JLabel meteQuestion = new JLabel("What meteorological data would you like to use?");
        optMete[0] = new JRadioButton("Use actual values (= from Header if available, else default).");
        optMete[1] = new JRadioButton("Specify new, specific values.");
        optMete[2] = new JRadioButton("Set invariant default values.");
        optMete[3] = new JRadioButton("Read meteorological data from file.");
        metePanel.add(meteQuestion);

        for (int i = 0; i < 4; i++) {
            optMete[i].addActionListener(this);
            grpMete.add(optMete[i]);
        }

        metePanel.add(optMete[0]);
        metePanel.add(optMete[2]);

        optMete[mainy.defaultSelectedMete].setSelected(true);

        meteSetTextField = new JTextField[4];

        for (int i = 0; i < 4; i++) {
            meteSetTextField[i] = new JTextField(Double.toString(mainy.meteDat[i]), 6);
        }

        JLabel question = new JLabel("Please select appropriate meteorological data:");
        JLabel ozone = new JLabel("Ozone [DU]");
        JLabel pressure = new JLabel("Pressure [hPa]");
        JLabel humidity = new JLabel("Humidity [%]");
        JLabel temperature = new JLabel("Temperature [�C]");

        JPanel firstMeteP = new JPanel();
        GridLayout gridy14 = new GridLayout(1, 4);
        firstP.setLayout(gridy14);
        JPanel secondMeteP = new JPanel();
        secondP.setLayout(gridy14);

        firstMeteP.add(ozone);
        firstMeteP.add(meteSetTextField[0]);
        firstMeteP.add(pressure);
        firstMeteP.add(meteSetTextField[1]);
        secondMeteP.add(humidity);
        secondMeteP.add(meteSetTextField[2]);
        secondMeteP.add(temperature);
        secondMeteP.add(meteSetTextField[3]);

        metePanel.add(firstMeteP);
        metePanel.add(secondMeteP);

        if (mainy.defaultSelectedMete != 2) {
            for (int i = 0; i < meteSetTextField.length; i++) {
                meteSetTextField[i].setEnabled(false);
            }
        }

        metePanel.add(optMete[3]);

        metePanel.add(chooseMeteFile);

        if (mainy.defaultSelectedMete != 3) {
            chooseMeteFile.setEnabled(false);
        }

        //--------------------
        JPanel aerov0Panel = new JPanel();
        GridLayout gridy5 = new GridLayout(5, 1);
        aerov0Panel.setLayout(gridy5);

        JLabel aerov0Question = new JLabel("Which V0-Values would you like for the AOD calculation?");
        optAerov0[0] = new JRadioButton("from Header");
        optAerov0[1] = new JRadioButton("interpolated to actual Date");
        optAerov0[2] = new JRadioButton("from current Langley");
        optAerov0[3] = new JRadioButton("Enter values manually.");

        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(gridy2);

        aerov0Panel.add(aerov0Question);

        for (int i = 0; i < 4; i++) {
            optAerov0[i].addActionListener(this);
            grpAerov0.add(optAerov0[i]);
            upperPanel.add(optAerov0[i]);
        }
        aerov0Panel.add(upperPanel);

        JLabel[] textFieldComment = new JLabel[4];

        textFieldComment[0] = new JLabel("862nm Channel");
        textFieldComment[1] = new JLabel("500nm Channel");
        textFieldComment[2] = new JLabel("412nm Channel");
        textFieldComment[3] = new JLabel("368nm Channel");

        for (int i = 0; i < 4; i++) {
            aerov0SetTextField[i] = new JTextField(Double.toString(mainy.aerov0Values[i]), 6);
        }

        JPanel firstManuallyAerov0 = new JPanel();
        firstManuallyAerov0.setLayout(gridy14);
        JPanel secondManuallyAerov0 = new JPanel();
        secondManuallyAerov0.setLayout(gridy14);

        firstManuallyAerov0.add(textFieldComment[0]);
        firstManuallyAerov0.add(aerov0SetTextField[0]);
        firstManuallyAerov0.add(textFieldComment[1]);
        firstManuallyAerov0.add(aerov0SetTextField[1]);

        secondManuallyAerov0.add(textFieldComment[2]);
        secondManuallyAerov0.add(aerov0SetTextField[2]);
        secondManuallyAerov0.add(textFieldComment[3]);
        secondManuallyAerov0.add(aerov0SetTextField[3]);

        aerov0Panel.add(firstManuallyAerov0);
        aerov0Panel.add(secondManuallyAerov0);

        // apply the default settings
        optAerov0[mainy.defaultSelectedAerov0].setSelected(true);
        if (mainy.defaultSelectedAerov0 == 3) {
            for (int i = 0; i < 4; i++) {
                aerov0SetTextField[i].setEnabled(true);
            }
        } else {
            for (int i = 0; i < 4; i++) {
                aerov0SetTextField[i].setEnabled(false);
            }
        }

        //--------------------
        JPanel buttonPanel = new JPanel();
        GridLayout gridy12 = new GridLayout(1, 2);
        buttonPanel.add(okbSetDefault);
        buttonPanel.add(ccbSetDefault);

        //--------------------
        JPanel pane = new JPanel();
        pane.add(periodPanel);
        pane.add(langleyPanel);
        pane.add(airrangePanel);
        pane.add(metePanel);
        pane.add(aerov0Panel);
        pane.add(buttonPanel);

        settingsFrame.setContentPane(pane);
        settingsFrame.setVisible(true);

    }	// end enterDefaultSettings()

//------------------------------------------------------------------------
    public void meteFileChooser() {
        FileDialog openMeteoFile = new FileDialog(mainy, " Open Meteo File ", FileDialog.LOAD);
        openMeteoFile.setVisible(true);
        String fileName = openMeteoFile.getFile();
        String directoryName = openMeteoFile.getDirectory();
        if (fileName != null && directoryName != null) {
            mainy.selectedMeteFile = new File(directoryName, fileName);
            mainy.selectedMete = 3;
            mainy.display.displayAcceptedProceedScreen();
        } else {
            mainy.display.displayCancelProceedScreen();
        }
    }	// end meteFileChooser()

//-------------------------------------------------------------------------------------
    public void actionPerformed(ActionEvent evt) {	//if (!evt.getValueIsAdjusting())
        {
            Object source = evt.getSource();
            if (source instanceof JButton) {
                if (source == ccb) {
                    mainy.display.displayCancelProceedScreen();
                } else if (source == okbPeriod) {
                    mainy.selectedPeriod = preliminarySelectedPeriod;
                    mainy.display.displayAcceptedProceedScreen();
                } else if (source == okbLangley) {
                    mainy.selectedLangley = preliminarySelectedLangley;
                    mainy.display.displayAcceptedProceedScreen();
                } else if (source == okbAirrange) {
                    if (Double.parseDouble(airrangeTextField1.getText()) > Double.parseDouble(airrangeTextField0.getText())) {
                        mainy.selectedLowerAirmassRange = Double.parseDouble(airrangeTextField0.getText());
                        mainy.selectedUpperAirmassRange = Double.parseDouble(airrangeTextField1.getText());
                        mainy.display.displayAcceptedProceedScreen();
                    } else {
                        airrangeErrorSelection();
                    }
                } else if (source == okbMete) {
                    if (preliminarySelectedMete == 0) {
                        mainy.selectedMete = preliminarySelectedMete;
                        mainy.meteDat = mainy.defaultMeteDat;
                        mainy.display.displayAcceptedProceedScreen();
                    } else if (preliminarySelectedMete == 2) {
                        mainy.meteDat = mainy.defaultMeteDat;
                        mainy.display.displayAcceptedProceedScreen();
                        mainy.selectedMete = 2;
                    } else if (preliminarySelectedMete == 1) {
                        setMeteDat();
                    } else if (preliminarySelectedMete == 3) {
                        mainy.display.displayAcceptedProceedScreen();
                        meteFileChooser();
                    }
                } else if (source == okbSetMete) {
                    for (int i = 0; i < meteSetTextField.length; i++) {
                        mainy.meteDat[i] = Double.parseDouble(meteSetTextField[i].getText());
                    }
                    mainy.selectedMete = 2;
                    mainy.display.displayAcceptedProceedScreen();
                } else if (source == okbAerov0) {
                    if (preliminarySelectedAerov0 == 3) {
                        for (int j = 0; j < 4; j++) {
                            aerov0SetTextField[j].setEnabled(true);
                        }
                        setAerov0();
                    } else {
                        mainy.selectedAerov0 = preliminarySelectedAerov0;
                        mainy.display.displayAcceptedProceedScreen();
                    }
                } else if (source == okbSetAerov0) {
                    mainy.selectedAerov0 = preliminarySelectedAerov0;
                    for (int i = 0; i < mainy.aerov0Values.length; i++) {
                        mainy.aerov0Values[i] = Double.parseDouble(aerov0SetTextField[i].getText());
                    }
                    mainy.display.displayAcceptedProceedScreen();
                } else if (source == chooseMeteFile) {
                    meteFileChooser();
                } else if (source == okbSetDefault) {
                    mainy.defaultSelectedPeriod = preliminarySelectedPeriod;
                    mainy.defaultSelectedLangley = preliminarySelectedLangley;
                    mainy.defaultSelectedMete = preliminarySelectedMete;
                    mainy.defaultSelectedAerov0 = preliminarySelectedAerov0;
                    if (preliminarySelectedMete == 3) {
                        mainy.defaultSelectedMeteFile = mainy.selectedMeteFile;
                    } else {
                        mainy.defaultSelectedMeteFile = null;
                    }
                    if (helpSelectedMeteFile != null) {
                        mainy.selectedMeteFile = helpSelectedMeteFile;
                    } else {
                        mainy.selectedMeteFile = null;
                    }
                    if (preliminarySelectedAerov0 == 3) {
                        for (int i = 0; i < mainy.aerov0Values.length; i++) {
                            mainy.defaultAerov0Values[i] = Double.parseDouble(aerov0SetTextField[i].getText());
                        }
                    }
                    if (preliminarySelectedMete == 2) {
                        for (int i = 0; i < mainy.aerov0Values.length; i++) {
                            mainy.defaultMeteDat[i] = Double.parseDouble(meteSetTextField[i].getText());
                        }
                    }
                    mainy.defaultSelectedAirmassRange[0] = Double.parseDouble(airrangeTextField0.getText());
                    mainy.defaultSelectedAirmassRange[1] = Double.parseDouble(airrangeTextField1.getText());

                    mainy.defaultWriter();
                    settingsFrame.setVisible(false);
                    mainy.display.displayAcceptedProceedScreen();
                } else if (source == ccbSetDefault) {
                    settingsFrame.setVisible(false);
                    mainy.display.displayCancelProceedScreen();
                }
            } else if (source instanceof JRadioButton) {
                for (int i = 0; i < 3; i++) {
                    if (source == optPeriod[i]) {
                        preliminarySelectedPeriod = i;
                    } else if (source == optLangley[i]) {
                        preliminarySelectedLangley = i;
                    }
                }
                for (int i = 0; i < 4; i++) {
                    if (source == optAerov0[i]) {
                        preliminarySelectedAerov0 = i;
                        if (i == 3) {
                            for (int j = 0; j < 4; j++) {
                                aerov0SetTextField[j].setEnabled(true);
                            }
                        } else {
                            for (int j = 0; j < 4; j++) {
                                aerov0SetTextField[j].setEnabled(false);
                            }
                        }
                    } else if (source == optMete[i]) {
                        preliminarySelectedMete = i;
                        if (i == 2) {
                            for (int j = 0; j < meteSetTextField.length; j++) {
                                meteSetTextField[j].setEnabled(true);
                            }
                        } else {
                            for (int j = 0; j < meteSetTextField.length; j++) {
                                meteSetTextField[j].setEnabled(false);
                            }
                        }
                        if (i == 3) {
                            chooseMeteFile.setEnabled(true);
                        } else {
                            chooseMeteFile.setEnabled(false);
                        }

                    }

                }
            }

        }
    }	// end actionPerformed(......

} // end class
