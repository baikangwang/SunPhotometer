package sunphotometer.pfr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class WarningLogger {

    String warnlogName = "inifiles/warnlog.pre";
    int numberOfWarnings;
//----------------------------------------------------------------------	

    public void create() {
        numberOfWarnings = 0;
        try {
            FileWriter file = new FileWriter(warnlogName);
            BufferedWriter buff = new BufferedWriter(file);
            {
                buff.write("%This is a log file for one specific run of DustTracker");
            }
            buff.flush();
            buff.close();
        } catch (IOException e) {
            System.out.println("WARNING: Error while creating Error log File.");
        }
    }
//----------------------------------------------------------------------	

    public void addWarning(String warning) {
        String[] lines = retrieve();
        try {
            FileWriter file = new FileWriter(warnlogName);
            BufferedWriter buff = new BufferedWriter(file);
            {

                for (int i = 0; i < lines.length; i++) {
                    buff.write(lines[i]);
                    buff.newLine();
                }
                buff.write(warning);
                numberOfWarnings++;
            }
            buff.flush();
            buff.close();
        } catch (IOException e) {
            System.out.println("WARNING: Error while creating Error log File.");
        }
    }
//----------------------------------------------------------------------	

    public String[] retrieve() {
        String[] lines = new String[1023];
        int count = 0;
        try {
            FileReader file = new FileReader(warnlogName);
            BufferedReader buff = new BufferedReader(file);
            {
                boolean neof = true; // (not end of file...)
                while (neof) {
                    String line = buff.readLine();
                    if (line != null) {
                        lines[count] = line;
                        count++;
                    } else {
                        neof = false;
                    }
                }
                buff.close();
            }
        } catch (IOException e) {
            System.out.println("WARNING: Error while creating Error log File.");
        }

        String[] resizedLines = new String[count];
        for (int i = 0; i < count; i++) {
            resizedLines[i] = lines[i];
        }
        return resizedLines;

    }

//----------------------------------------------------------------------	
    public static void main(String[] args) {
        WarningLogger logi = new WarningLogger();
        logi.create();
        logi.addWarning("I warn you");
        logi.addWarning("sorry, wasn't necessary..");
    }

}
