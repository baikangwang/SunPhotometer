package sunphotometer.pfr;

import java.awt.*;
import javax.swing.*;

public class IntermediateScreen {

    MainWindow mainy;

    public IntermediateScreen(MainWindow mainy) {
        this.mainy = mainy;

    }

//=======================================================================
//			These are the different intermediate Screens
//			they report what's happening at the moment
//-----------------------------------------------------------------------
    public void displayAbout() {
        JPanel pane = new JPanel();
        BorderLayout bordy = new BorderLayout();
        pane.setLayout(bordy);

        pane.add("North", mainy.menuBar);

        JPanel centerPanel = new JPanel();
        ImageIcon aboutIcon = new ImageIcon(getClass().getResource("/images/about.gif"));
        JLabel aboutLabel = new JLabel(aboutIcon);
        JLabel currentVersion = new JLabel("Current Version   :    " + mainy.versionNumber);

        centerPanel.add(currentVersion);
        centerPanel.add(aboutLabel);

        pane.add("Center", centerPanel);

        JLabel commentLabel = new JLabel("About this program ...");
        pane.add("South", commentLabel);

        mainy.setContentPane(pane);
        mainy.setVisible(true);
    }	// end displayAbout()

//-----------------------------------------------------------------------
    public void displayFileWarning() {
        JPanel pane = new JPanel();
        BorderLayout bordy = new BorderLayout();
        pane.setLayout(bordy);

        pane.add("North", mainy.menuBar);

        ImageIcon aboutIcon = new ImageIcon(getClass().getResource("/images/filewarning.gif"));
        JLabel aboutLabel = new JLabel(aboutIcon);
        pane.add("Center", aboutLabel);

        JLabel commentLabel = new JLabel("There has no file been opened.");
        pane.add("South", commentLabel);

        mainy.setContentPane(pane);
        mainy.setVisible(true);
    }	// end displayFileWarning()

//----------------------------------------------------------------------------
    public void displayWorking() {
        JPanel pane = new JPanel();
        BorderLayout bordy = new BorderLayout();
        pane.setLayout(bordy);

        pane.add("North", mainy.menuBar);

        ImageIcon aboutIcon = new ImageIcon(getClass().getResource("/images/working.gif"));
        JLabel aboutLabel = new JLabel(aboutIcon);
        pane.add("Center", aboutLabel);

        JLabel commentLabel = new JLabel("The program is calculating.");
        pane.add("South", commentLabel);

        mainy.setContentPane(pane);
        mainy.setVisible(true);
    }	// end displayWorking()	

//-----------------------------------------------------------------------
    public void displayNotEnoughPoints() {
        JPanel pane = new JPanel();
        BorderLayout bordy = new BorderLayout();
        pane.setLayout(bordy);

        pane.add("North", mainy.menuBar);

        ImageIcon aboutIcon = new ImageIcon(getClass().getResource("/images/nopoints.gif"));
        JLabel aboutLabel = new JLabel(aboutIcon);
        pane.add("Center", aboutLabel);

        JLabel commentLabel = new JLabel("Not enough points for Langley calibration found.");
        pane.add("South", commentLabel);

        mainy.results.setEnabled(true);
        mainy.aeroplot.setEnabled(false);
        mainy.writeLevel3.setEnabled(false);
        mainy.aeroScaling.setEnabled(false);
        mainy.langleyplot.setEnabled(false);
        mainy.writeLangley.setEnabled(false);
        mainy.langleyScaling.setEnabled(false);

        mainy.setContentPane(pane);
        mainy.setVisible(true);
    }	// end displayNotEnoughPoints()

//-----------------------------------------------------------------------
    public void displayFinished(int opt) {
        JPanel pane = new JPanel();
        BorderLayout bordy = new BorderLayout();
        pane.setLayout(bordy);

        pane.add("North", mainy.menuBar);
        mainy.results.setEnabled(true);
        if (opt == 0) {
            mainy.aeroplot.setEnabled(false);
            mainy.writeLevel3.setEnabled(false);
            mainy.aeroScaling.setEnabled(false);
            mainy.langleyplot.setEnabled(true);
            mainy.writeLangley.setEnabled(true);
            mainy.langleyScaling.setEnabled(true);
        }
        if (opt == 1) {
            mainy.aeroplot.setEnabled(true);
            mainy.writeLevel3.setEnabled(true);
            mainy.aeroScaling.setEnabled(true);
            if (mainy.selectedAerov0 == 2) {
                mainy.langleyplot.setEnabled(true);
                mainy.writeLangley.setEnabled(true);
                mainy.langleyScaling.setEnabled(true);
            } else {
                mainy.langleyplot.setEnabled(false);
                mainy.writeLangley.setEnabled(false);
                mainy.langleyScaling.setEnabled(false);
            }
        }

        ImageIcon aboutIcon = new ImageIcon(getClass().getResource("/images/finished.gif"));
        JLabel aboutLabel = new JLabel(aboutIcon);
        pane.add("Center", aboutLabel);

        JLabel commentLabel = new JLabel("The Calculations have been executed succesfully.");
        pane.add("South", commentLabel);

        mainy.setContentPane(pane);
        mainy.setVisible(true);
    }	//	end displayFinished()

//-----------------------------------------------------------------------
    public void displayCancelProceedScreen() {
        JPanel pane = new JPanel();
        BorderLayout bordy = new BorderLayout();
        pane.setLayout(bordy);

        pane.add("North", mainy.menuBar);

        ImageIcon aboutIcon = new ImageIcon(getClass().getResource("/images/proceed.gif"));
        JLabel aboutLabel = new JLabel(aboutIcon);
        pane.add("Center", aboutLabel);

        JLabel commentLabel = new JLabel("Action cancelled, no action performed. Please proceed.");
        pane.add("South", commentLabel);

        mainy.setContentPane(pane);
        mainy.setVisible(true);
    }	// end displayCancelProceedScreen()	

//-----------------------------------------------------------------------
    public void displayAcceptedProceedScreen() {
        JPanel pane = new JPanel();
        BorderLayout bordy = new BorderLayout();
        pane.setLayout(bordy);

        pane.add("North", mainy.menuBar);

        ImageIcon aboutIcon = new ImageIcon(getClass().getResource("/sunphotometer/pfr/images/proceed.gif"));
        JLabel aboutLabel = new JLabel(aboutIcon);
        pane.add("Center", aboutLabel);

        JLabel commentLabel = new JLabel("Action accepted. Please proceed.");
        pane.add("South", commentLabel);

        mainy.setContentPane(pane);
        mainy.setVisible(true);
    }	// end displayAcceptedProceedScreen()

// 	now all intermediate information screens are defined
}
