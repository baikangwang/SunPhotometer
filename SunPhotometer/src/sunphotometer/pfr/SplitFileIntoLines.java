package sunphotometer.pfr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SplitFileIntoLines {

    WarningLogger warnLog = new WarningLogger();

    int CountTheLines(File theFile) /* The method CountTheLines counts the lines of a specified File
		the filename has to be handed over to the method as a string
		the method return an integer value which corresponds to the number of 
		lines*/ {
        int CountLines = 0;
        try {
            FileReader file = new FileReader(theFile);
            BufferedReader buff = new BufferedReader(file);
            boolean eof = false;
            while (!eof) {
                String line = buff.readLine();
                if (line == null) {
                    eof = true;
                } else {
                    CountLines++;
                }
            }
            buff.close();
        } catch (IOException e) {
            warnLog.addWarning("WARNING: Exception while splitting file into lines");
        }
        return CountLines;
    }

//---------------------------------------------------------------

    /* The method SplitIntoLines takes a file and takes every line and 
puts it into a array of strings. the index+1 of the array corresponds
to the number of the line ( java starts counting with zero and
the first line is 1)
Required Input the number of lines the specified file has
and of the size corresponding to the number of lines, which afterwards
is manipulated.
The relevant Output is the manipulated array of strings*/
    String[] SplitIntoLines(File theFile, int numberOfLines) {
        String[] lines = new String[numberOfLines];

        try {
            FileReader file = new FileReader(theFile);
            BufferedReader buff = new BufferedReader(file);
            for (int i = 0; i < (numberOfLines); i++) {
                String line = buff.readLine();
                lines[i] = line;
            }
            buff.close();
        } catch (IOException e) {
            warnLog.addWarning("WARNING: Problems occurred while splitting file into lines");
        }
        return lines;
    }

}
