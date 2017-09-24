package sunphotometer.pfr;

public class ConvertStringToNumbers {

    /*	
	Basically an automatized "parseNumber"/Tokenizer that means given a string containing just Numbers 
	and spaces inbetween. this class returns an array containing these numbers.
	not really revolutionary, but the main advandtage is based on the basic idea.
	
	A String of numbers separated by any Sign may be sorted into the numbers contained if slight changes
	are applied. 
	
     */

//-------------------------------------------------------------------------
    int[] ConvertStringToIntegers(String InString) {
        int[] keepTrack = new int[255];
        int TheFinalData[];
        InString = InString.trim();
        if (InString.length() == 0) {
            int[] TheData = new int[1];
            TheData[0] = 0;
            TheFinalData = TheData;
        } else {
            int control = InString.indexOf(" ");
            int count = 0;
            while (control != (- 1)) {
                keepTrack[count] = Integer.parseInt(InString.substring(0, (control)));
                InString = InString.substring(control);
                InString = InString.trim();
                control = InString.indexOf(" ");
                count++;
            }
            keepTrack[count] = Integer.parseInt(InString);
            int[] TheData = new int[(count + 1)];
            for (int i = 0; i < (count + 1); i++) {
                TheData[i] = keepTrack[i];
            }
            TheFinalData = TheData;
        }
        return TheFinalData;
    }

//-----------------------------------------------------------------------
    double[] ConvertStringToDoubles(String InString) {
        double[] keepTrack = new double[255];
        double[] TheFinalData;
        InString = InString.trim();
        if (InString.length() == 0) {
            double[] TheData = new double[1];
            TheData[0] = 0;
            TheFinalData = TheData;
        } else {
            int control = InString.indexOf(" ");
            int count = 0;
            while (control != (- 1)) {
                keepTrack[count] = Double.parseDouble(InString.substring(0, (control)));
                InString = InString.substring(control);
                InString = InString.trim();
                control = InString.indexOf(" ");
                count++;
            }
            keepTrack[count] = Double.parseDouble(InString);
            double[] TheData = new double[(count + 1)];
            for (int i = 0; i < (count + 1); i++) {
                TheData[i] = keepTrack[i];
            }
            TheFinalData = TheData;
        }
        return TheFinalData;

    }

//--------------------------------------------------------------------------------
    public static void main(String[] arguments) // a little Example how it may be used
    {
        ConvertStringToNumbers a = new ConvertStringToNumbers();
        String intzahlen = "32 45 65 76 12 14";

        int[] diezahlen = a.ConvertStringToIntegers(intzahlen);
        for (int i = 0; i < diezahlen.length; i++) {
            System.out.println(diezahlen[i]);
        }
        double[] TheNumbers = a.ConvertStringToDoubles(intzahlen);
        for (int i = 0; i < TheNumbers.length; i++) {
            System.out.println(TheNumbers[i]);
        }
        double[] TheNumbers2 = a.ConvertStringToDoubles("       ");
        for (int i = 0; i < TheNumbers2.length; i++) {
            System.out.println(TheNumbers2[i]);
        }
    }

}
