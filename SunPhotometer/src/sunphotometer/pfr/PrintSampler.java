package sunphotometer.pfr;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PrintSampler extends JFrame {

    JButton btnPrnAll;
    JButton btnPrnImage;
    JButton closeWindow;
    Component scrollableObject;
    String FrameTitle;

    public PrintSampler(String title, Component bitmapImage) {
        super(title);
        FrameTitle = title;
        scrollableObject = bitmapImage;
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(scrollableObject);
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
        JPanel panel = new JPanel();
        btnPrnAll = new JButton("Print whole Window");
        panel.add(btnPrnAll);
        btnPrnImage = new JButton("Print Graph only");
        panel.add(btnPrnImage);
        closeWindow = new JButton("Close");
        panel.add(closeWindow);

        this.getContentPane().add(panel, BorderLayout.SOUTH);

//------------------------------------------------------------------------------
        ActionListener windowKiller = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnPrnAll.setEnabled(false);
                btnPrnImage.setEnabled(false);
                closeWindow.setEnabled(false);
                setVisible(false);
                dispose();
            }
        };

//------------------------------------------------------------------------------	
        ActionListener printSomething = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnPrnAll.setEnabled(false);
                btnPrnImage.setEnabled(false);
                closeWindow.setEnabled(false);
                Properties prnProp = new Properties();
                PrintJob prnJob = Toolkit.getDefaultToolkit().getPrintJob(PrintSampler.this, FrameTitle, prnProp);

                if (prnJob != null) {
                    System.out.println(prnProp);
                    JButton b = (JButton) evt.getSource();
                    Component c;
                    if (b == btnPrnAll) {
                        c = PrintSampler.this;
                    } else {
                        c = scrollableObject;
                    }

                    Graphics prnGr = prnJob.getGraphics();
                    prnGr.translate(
                            (prnJob.getPageDimension().width - c.getSize().width) / 2,
                            (prnJob.getPageDimension().height - c.getSize().height) / 2);
                    if (b == btnPrnAll) {
                        c.printAll(prnGr);
                    } else {
                        c.print(prnGr);
                    }

                    prnGr.dispose(); //Prints here 
                    prnJob.end(); //Release printer 
                }
                btnPrnAll.setEnabled(true);
                btnPrnImage.setEnabled(true);
                closeWindow.setEnabled(true);
            }
        };

        btnPrnAll.addActionListener(printSomething);
        btnPrnImage.addActionListener(printSomething);
        closeWindow.addActionListener(windowKiller);
    }

//------------------------------------------------------------------------------------------
    public static void main(String[] args) {
        JFrame f = new PrintSampler("Java/Swing print test", new JLabel(new ImageIcon("fishpier.jpg")));
        f.setSize(400, 400);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }
        });
        f.setVisible(true);
    }

} // end of class PrintSampler 
