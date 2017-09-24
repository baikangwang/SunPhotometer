package sunphotometer.pfr;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
import java.awt.*;
import java.io.*;

public class HelpWindow extends JFrame implements HyperlinkListener {

    public HelpWindow() {
        super("Help");
        setSize(500, 500);

        // Set up the editor kit and document for the JEDitorPane 
        //Hashtable commands; 
        HTMLEditorKit kit = new HTMLEditorKit();

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JEditorPane editor;

        editor = new JEditorPane();

        editor.setContentType("text/html");
        editor.setEditorKit(kit);

        FileDialog fili = new FileDialog(this);
        String dir = fili.getDirectory();
        File helpy = new File(dir, "manual/index.html");

        try {
            editor.setPage(helpy.toURL());
        } catch (IOException e) {
            System.out.println("test not found");
        };
        editor.setEditable(false);

        editor.addHyperlinkListener(this);

        panel.add(new JScrollPane(editor), BorderLayout.CENTER);
        setContentPane(panel);
    }

//-----------------------------------------------------------------------
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            JEditorPane pane = (JEditorPane) e.getSource();
            if (e instanceof HTMLFrameHyperlinkEvent) {
                HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
                HTMLDocument doc = (HTMLDocument) pane.getDocument();
                doc.processHTMLFrameHyperlinkEvent(evt);
            } else {
                try {
                    pane.setPage(e.getURL());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

//----------------------------------------------------------------------
    public static void main(String[] args) {
        JFrame frame = new HelpWindow();

        WindowListener l = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        };

        frame.addWindowListener(l);
        frame.pack();
        frame.setVisible(true);

    }

}
