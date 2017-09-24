package sunphotometer.pfr;

import java.io.*;

public class MyProgram {

    public static void main(String[] arguments) {
        MyProgram p = new MyProgram();

        ReadLevel2File TheContent = new ReadLevel2File();
        String TheFile = "dav_n26_20000618.002";

        //	FileSystem fily = new FileSystem();
        String TheDirectory = "c:/eigenedateien/javaclasses/beta3";

        File thisFile = new File(TheDirectory, TheFile);

        TheContent.TheMain(thisFile);

        int[] DayData;
        DoTheCheck TheCheck = new DoTheCheck();
        DayData = TheCheck.DoFirstCheck(TheContent);

        double[] theMeteoData = {300, 1000, 50, 15};
        double[] airRange = {2, 6};

        LangleyExtrapol MyExtrapolation = new LangleyExtrapol(DayData, TheContent, theMeteoData, 0);
        MyExtrapolation.theMain(1, airRange);
        double[] EVvalues = {3.5, 3.5, 3.5, 3.5};

        AerosolOpticalDepth AeroDepth = new AerosolOpticalDepth(TheContent);
        AeroDepth.TheMain(MyExtrapolation, DayData, EVvalues, theMeteoData, 0, 0);

//		AerosolOpticalDepth AeroDepth = new AerosolOpticalDepth();
//			AeroDepth = AeroDepth.TheMain(TheContent, MyExtrapolation, DayData);
//		WriteOutputToFile OutNow = new	WriteOutputToFile();
//			OutNow.WriteAODFile("dav_n26_20000618.aod",TheContent,AeroDepth);
//			OutNow.WriteCALFile("dav_n26_20000618.cal",TheContent,MyExtrapolation);
    }

}
