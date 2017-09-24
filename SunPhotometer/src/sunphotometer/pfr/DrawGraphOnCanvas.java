package sunphotometer.pfr;

import java.awt.*;

/*
#######################################
Programmed by Urs Zimmerli, Switzerland

         urs.zimmerli@gmx.net

 feel free to changes, but please keep 
   my name mentionned in the source 
#######################################
 */
class DrawGraphOnCanvas extends java.awt.Canvas {	// define all the global variables needed in this class

    double[][] x;
    String xLabel, yLabel, title, subtitle;
    double[][] y;
    int numberOfPlots;
    int plotOption;
    double xDisplayMin;
    double xDisplayMax;
    double yDisplayMin;
    double yDisplayMax;
    double xDisplayRange;
    double yDisplayRange;
    int xOrderOfMag;
    int yOrderOfMag;
    int limitOption = 0;

    int xCase;
    int yCase;
    double xTick;
    double yTick;

    int[][] xInt;
    int[][] yInt;
    int scalingOption;

    int xSize;
    int ySize;

    double eValue = 2.3025851;

    // for regression lines
    double[] maxPoints;
    double[] minPoints;
    boolean regressionOption = false;
    double xMax;

//=====================================================================
//---------------Constructors -----------------------------------------
//=====================================================================
//---------------plot Graph with autoscaling---------------------------
    DrawGraphOnCanvas(double[][] x, double[][] y, String[] labels, int numberOfPlots, int plotOption) {
        /* 	requirements: an x-array containing the x-coordinates of the points to plot x[numberOfPoints][numberOfPlots] 
			y: is an array of [numberOfPoints][numberOfPlots]... therefore you get a pair of points for every field in these arrays
			every column of y contains one set of y-coordinates, corresponding to one graph
			labels: an array of strings, with 	title at index 0
												subtitle at index 1
												xlabel at index 2
												ylabel at index 3
			numberOfPlots: how many plot shall be drawn? that means if y is of size [x.length][width], the first numberOfPlots-columns of
				that y array are plotted, therefore numberOfPlots must be smaller than the width of the y array. 
			PlotOption: if PlotOption = 0 only dots are plotted, otherwise a line is plotted...
         */
        this.numberOfPlots = numberOfPlots;
        this.y = y;
        this.x = x;
        this.xLabel = labels[2];
        this.yLabel = labels[3];
        this.title = labels[0];
        this.subtitle = labels[1];
        this.plotOption = plotOption;
        this.scalingOption = 0;
    }

//----------------plot graph with fixed Scaling on both axis-------------------------------------------------------------------------------------------------------------	
    DrawGraphOnCanvas(double[][] x, double[][] y, String[] labels, int numberOfPlots, int plotOption, double[] xLimit, double[] yLimit) {
        /* 	requirements: an x-array containing the x-coordinates of the points to plot x[numberOfPoints][numberOfPlots] 
			y: is an array of [numberOfPoints][numberOfPlots]... therefore you get a pair of points for every field in these arrays
			every column of y contains one set of y-coordinates, corresponding to one graph
			labels: an array of strings, with 	title at index 0
												subtitle at index 1
												xlabel at index 2
												ylabel at index 3
			numberOfPlots: how many plot shall be drawn? that means if y is of size [x.length][width], the first numberOfPlots-columns of
				that y array are plotted, therefore numberOfPlots must be smaller than the width of the y array. 
			PlotOption: if PlotOption = 0 only dots are plotted, otherwise a line is plotted...
			xLimit = {xmin,xmax}; yLimit = {ymin,ymax} indicate the maximum or the minimum values which shall be displayed (chose reasonably, otherwise no plot or just bullshit, since there is no filter)		
         */
        this.numberOfPlots = numberOfPlots;
        this.y = y;
        this.x = x;
        this.xLabel = labels[2];
        this.yLabel = labels[3];
        this.title = labels[0];
        this.subtitle = labels[1];
        this.plotOption = plotOption;
        this.xDisplayMin = xLimit[0];
        this.xDisplayMax = xLimit[1];
        this.yDisplayMin = yLimit[0];
        this.yDisplayMax = yLimit[1];

        this.scalingOption = 1;

    }

//------------------ plot graph with fixed Scaling of one axis only-----------------------------------------------------------------------------------------------------------	
    DrawGraphOnCanvas(double[][] x, double[][] y, String[] labels, int numberOfPlots, int plotOption, double[] Limit, int limitOption) {
        /* 	requirements: an x-array containing the x-coordinates of the points to plot x[numberOfPoints][numberOfPlots] 
			y: is an array of [numberOfPoints][numberOfPlots]... therefore you get a pair of points for every field in these arrays
			every column of y contains one set of y-coordinates, corresponding to one graph
			labels: an array of strings, with 	title at index 0
												subtitle at index 1
												xlabel at index 2
												ylabel at index 3
			in the limit array are the limits stated for one axis only.
			numberOfPlots: how many plot shall be drawn? that means if y is of size [x.length][width], the first numberOfPlots-columns of
				that y array are plotted, therefore numberOfPlots must be smaller than the width of the y array. 
			PlotOption: if PlotOption = 0 only dots are plotted, otherwise a line is plotted...
			Limit = {min,max}; indicate the maximum or the minimum values which shall be displayed (chose reasonably, otherwise no plot or just bullshit, since there is no filter)		
			the axis is assigned in the limit option:
			0= xAxis
			1= yAxis
         */

        this.numberOfPlots = numberOfPlots;
        this.y = y;
        this.x = x;
        this.xLabel = labels[2];
        this.yLabel = labels[3];
        this.title = labels[0];
        this.subtitle = labels[1];
        this.plotOption = plotOption;
        this.xDisplayMin = Limit[0];
        this.xDisplayMax = Limit[1];
        this.yDisplayMin = Limit[0];
        this.yDisplayMax = Limit[1];
        this.limitOption = limitOption;

        this.scalingOption = 2;

    }

//----------------------plot graph with fixed Scaling and regression lines------------------------------------------------------
    DrawGraphOnCanvas(double[][] x, double[][] y, String[] labels, int numberOfPlots, int plotOption, double[] xLimit, double[] yLimit, double[] slopes, double[] intercepts) {
        /* 	requirements: an x-array containing the x-coordinates of the points to plot x[numberOfPoints][numberOfPlots] 
			y: is an array of [numberOfPoints][numberOfPlots]... therefore you get a pair of points for every field in these arrays
			every column of y contains one set of y-coordinates, corresponding to one graph
			labels: an array of strings, with 	title at index 0
												subtitle at index 1
												xlabel at index 2
												ylabel at index 3
			numberOfPlots: how many plot shall be drawn? that means if y is of size [x.length][width], the first numberOfPlots-columns of
				that y array are plotted, therefore numberOfPlots must be smaller than the width of the y array. 
			PlotOption: if PlotOption = 0 only dots are plotted, otherwise a line is plotted...
			xLimit = {xmin,xmax}; yLimit = {ymin,ymax} indicate the maximum or the minimum values which shall be displayed (chose reasonably, otherwise no plot or just bullshit, since there is no filter)		
			slopes and intercepts are arrays containing the regression lines corresponding to the graph of the same index.
		
         */
        this.numberOfPlots = numberOfPlots;
        this.y = y;
        this.x = x;
        this.xLabel = labels[2];
        this.yLabel = labels[3];
        this.title = labels[0];
        this.subtitle = labels[1];
        this.plotOption = plotOption;
        this.xDisplayMin = xLimit[0];
        this.xDisplayMax = xLimit[1];
        this.yDisplayMin = yLimit[0];
        this.yDisplayMax = yLimit[1];

        xMax = x[0][0];
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < numberOfPlots; j++) {
                if (x[i][j] > xMax) {
                    xMax = x[i][j];
                }
            }
        }

        maxPoints = new double[slopes.length];
        minPoints = new double[intercepts.length];

        maxPoints = slopes;
        minPoints = intercepts;

        for (int i = 0; i < intercepts.length; i++) {
            minPoints[i] = xDisplayMin * maxPoints[i] + minPoints[i];
            maxPoints[i] = (xMax - xDisplayMin) * maxPoints[i] + minPoints[i];
        }

        this.regressionOption = true;
        this.scalingOption = 1;

    }

//=================================================================================================================0
//							methods
//=================================================================================================================0
//--------------------------------------------------------------------------------------------------------------
// automatically scaling such that all the points are lying in the area of plotting with reasonable borders
    public void autoXScaling() {
        this.xSize = getSize().width;

        // search for the maxima in the arrays		
        double xMax = x[0][0];
        double xMin = x[0][0];

        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < numberOfPlots; j++) {
                if (x[i][j] > xMax) {
                    xMax = x[i][j];
                }
                if (x[i][j] < xMin) {
                    xMin = x[i][j];
                }
            }
        }

        //here starts the autoscaling bit, it's kind of tricky but best is to remember, that the plotting area is going from
        //0.075 xSize to 0.875 xSize
        // and from 0.125 ySize to 0.925 ySize
        // therefore the autoscaling attempts to put all the points into this range (in a reasonable way)
        int[][] xi = new int[x.length][numberOfPlots];

        //----- first the scaling for the xAxis----------------
        if (xMax > 0 && xMin < ((xMax - xMin) / 2) && xMin > 0) {
            xCase = 1;
            xOrderOfMag = (int) (Math.log(xMax) / eValue);
            if (Math.log(xMax - xMin) < 0) {
                xOrderOfMag = xOrderOfMag - 1;
            }
            if (((Math.log(xMax) / eValue) - xOrderOfMag) < 0.5) {
                xTick = 0.5;
            } else {
                xTick = 1;
            }

            xDisplayMin = 0;
            xDisplayMax = 0.0;
            while (xDisplayMax < (1.01 * xMax)) {
                xDisplayMax = xDisplayMax + (xTick * Math.exp(xOrderOfMag * eValue));
            }
            xDisplayRange = xDisplayMax - xDisplayMin;

            for (int i = 0; i < x.length; i++) {
                for (int j = 0; j < numberOfPlots; j++) {
                    xi[i][j] = (int) (((x[i][j] / xDisplayRange) * 0.8 * xSize) + (0.075 * xSize));
                }
            }
        } else if (xMax > 0) {
            xCase = 2;
            xOrderOfMag = (int) (Math.log(xMax - xMin) / eValue);
            if (Math.log(xMax - xMin) < 0) {
                xOrderOfMag = xOrderOfMag - 1;
            }
            if (((Math.log(xMax - xMin) / eValue) - xOrderOfMag) < 0.5) {
                xTick = 0.5;
            } else {
                xTick = 1;
            }

            xDisplayMin = 0;
            if (xMin < 0) {
                while (xDisplayMin > (1.01 * xMin)) {
                    xDisplayMin = xDisplayMin - (xTick * Math.exp(xOrderOfMag * eValue));
                }
            } else {
                {
                    while (xDisplayMin < (1.01 * xMin)) {
                        xDisplayMin = xDisplayMin + (xTick * Math.exp(xOrderOfMag * eValue));
                    }
                }
                xDisplayMin = xDisplayMin - (xTick * Math.exp(xOrderOfMag * eValue));
            }

            xDisplayMax = 0.0;
            while (xDisplayMax < (1.01 * xMax)) {
                xDisplayMax = xDisplayMax + (xTick * Math.exp(xOrderOfMag * eValue));
            }
            xDisplayRange = xDisplayMax - xDisplayMin;

            for (int i = 0; i < x.length; i++) {
                for (int j = 0; j < numberOfPlots; j++) {
                    xi[i][j] = (int) ((((x[i][j] - xDisplayMin) / xDisplayRange) * 0.8 * xSize) + (0.075 * xSize));
                }
            }
        } else if (xMax < 0 && xMax < ((xMin - xMax) / 2)) {
            xCase = 3;
            xOrderOfMag = (int) (Math.log(xMax - xMin) / eValue);
            if (Math.log(xMax - xMin) < 0) {
                xOrderOfMag = xOrderOfMag - 1;
            }
            if (((Math.log(xMax - xMin) / eValue) - xOrderOfMag) < 0.5) {
                xTick = 0.5;
            } else {
                xTick = 1;
            }

            xDisplayMin = 0.0;
            while (xDisplayMin > (1.01 * xMin)) {
                xDisplayMin = xDisplayMin - (xTick * Math.exp(xOrderOfMag * eValue));
            }

            xDisplayMax = 0.0;
            while (xDisplayMax > (1.01 * xMax)) {
                xDisplayMax = xDisplayMax - (xTick * Math.exp(xOrderOfMag * eValue));
            }
            xDisplayMax = xDisplayMax + (xTick * Math.exp(xOrderOfMag * eValue));

            xDisplayRange = xDisplayMax - xDisplayMin;

            for (int i = 0; i < x.length; i++) {
                for (int j = 0; j < numberOfPlots; j++) {
                    xi[i][j] = (int) ((((x[i][j] - xDisplayMin) / xDisplayRange) * 0.8 * xSize) + (0.075 * xSize));
                }
            }
        } else {
            xCase = 4;
            xOrderOfMag = (int) (Math.log(-xMin) / eValue);
            if (Math.log(-xMin) < 0) {
                xOrderOfMag = xOrderOfMag - 1;
            }
            if (((Math.log(-xMin) / eValue) - xOrderOfMag) < 0.5) {
                xTick = 0.5;
            } else {
                xTick = 1;
            }

            xDisplayMin = 0.0;
            xDisplayMax = 0.0;
            while (xDisplayMin > (1.01 * xMin)) {
                xDisplayMin = xDisplayMin - (xTick * Math.exp(xOrderOfMag * eValue));
            }
            xDisplayRange = xDisplayMax - xDisplayMin;

            for (int i = 0; i < x.length; i++) {
                for (int j = 0; j < numberOfPlots; j++) {
                    xi[i][j] = (int) (((1 + (x[i][j] / xDisplayRange)) * 0.8 * xSize) + (0.075 * xSize));
                }
            }
        }
        this.xInt = xi;
    }

//----------------------------------------------------------------------	
    public void autoYScaling() {
        this.ySize = getSize().height;

        double yMax = y[0][0];
        double yMin = y[0][0];

        for (int i = 0; i < y.length; i++) {
            for (int j = 0; j < numberOfPlots; j++) {
                if (y[i][j] > yMax) {
                    yMax = y[i][j];
                }
                if (y[i][j] < yMin) {
                    yMin = y[i][j];
                }
            }
        }

        //here starts the autoscaling bit, it's kind of tricky but best is to remember, that the plotting area is going from
        // and from 0.125 ySize to 0.925 ySize
        // therefore the autoscaling attempts to put all the points into this range (in a reasonable way)
        int[][] yi = new int[x.length][numberOfPlots];

        if (yMax > 0 && yMin < ((yMax - yMin) / 2) && yMin > 0) {
            yCase = 1;
            yOrderOfMag = (int) (Math.log(yMax) / eValue);
            if (Math.log(yMax) < 0) {
                yOrderOfMag = yOrderOfMag - 1;
            }
            if (((Math.log(yMax) / eValue) - yOrderOfMag) < 0.5) {
                yTick = 0.5;
            } else {
                yTick = 1;
            }

            yDisplayMin = 0;
            yDisplayMax = 0.0;
            while (yDisplayMax < (1.01 * yMax)) {
                yDisplayMax = yDisplayMax + (yTick * Math.exp(yOrderOfMag * eValue));
            }
            yDisplayRange = yDisplayMax - yDisplayMin;

            for (int i = 0; i < x.length; i++) {
                for (int j = 0; j < numberOfPlots; j++) {
                    yi[i][j] = (int) (ySize - (((y[i][j] / yDisplayRange) * 0.8 * ySize) + (0.075 * ySize)));
                }
            }
        } else if (yMax > 0) {
            yCase = 2;
            yOrderOfMag = (int) (Math.log(yMax - yMin) / eValue);
            if (Math.log(yMax - yMin) < 0) {
                yOrderOfMag = yOrderOfMag - 1;
            }
            if (((Math.log(yMax - yMin) / eValue) - yOrderOfMag) < 0.5) {
                yTick = 0.5;
            } else {
                yTick = 1;
            }

            yDisplayMin = 0;
            if (yMin < 0) {
                while (yDisplayMin > (1.01 * yMin)) {
                    yDisplayMin = yDisplayMin - (yTick * Math.exp(yOrderOfMag * eValue));
                }
            } else {
                {
                    while (yDisplayMin < (0.99 * yMin)) {
                        yDisplayMin = yDisplayMin + (yTick * Math.exp(yOrderOfMag * eValue));
                    }
                }
                yDisplayMin = yDisplayMin - (yTick * Math.exp(yOrderOfMag * eValue));
            }

            yDisplayMax = 0.0;
            while (yDisplayMax < (1.01 * yMax)) {
                yDisplayMax = yDisplayMax + (yTick * Math.exp(yOrderOfMag * eValue));
            }
            yDisplayRange = yDisplayMax - yDisplayMin;

            for (int i = 0; i < x.length; i++) {
                for (int j = 0; j < numberOfPlots; j++) {
                    yi[i][j] = (int) (ySize - ((((y[i][j] - yDisplayMin) / (yDisplayRange)) * 0.8 * ySize) + (0.075 * ySize)));
                }
            }
        } else if (yMax < 0 && yMax < ((yMin - yMax) / 2)) {
            yCase = 3;
            yOrderOfMag = (int) (Math.log(yMax - yMin) / eValue);
            if (Math.log(yMax - yMin) < 0) {
                yOrderOfMag = yOrderOfMag - 1;
            }
            if (((Math.log(yMax - yMin) / eValue) - yOrderOfMag) < 0.5) {
                yTick = 0.5;
            } else {
                yTick = 1;
            }

            yDisplayMin = 0.0;
            while (yDisplayMin > (1.01 * yMin)) {
                yDisplayMin = yDisplayMin - (yTick * Math.exp(yOrderOfMag * eValue));
            }

            yDisplayMax = 0.0;
            while (yDisplayMax > (0.99 * yMax)) {
                yDisplayMax = yDisplayMax - (yTick * Math.exp(yOrderOfMag * eValue));
            }
            yDisplayMax = yDisplayMax + (yTick * Math.exp(yOrderOfMag * eValue));

            yDisplayRange = yDisplayMax - yDisplayMin;

            for (int i = 0; i < x.length; i++) {
                for (int j = 0; j < numberOfPlots; j++) {
                    yi[i][j] = (int) (ySize - ((((y[i][j] - yDisplayMin) / yDisplayRange) * 0.8 * ySize) + (0.075 * ySize)));
                }
            }
        } else {
            yCase = 4;
            yOrderOfMag = (int) (Math.log(-yMin) / eValue);
            if (Math.log(-yMin) < 0) {
                yOrderOfMag = yOrderOfMag - 1;
            }
            if (((Math.log(-yMin) / eValue) - yOrderOfMag) < 0.5) {
                yTick = 0.5;
            } else {
                yTick = 1;
            }

            yDisplayMin = 0.0;
            yDisplayMax = 0.0;
            while (yDisplayMin > (1.01 * yMin)) {
                yDisplayMin = yDisplayMin - (yTick * Math.exp(yOrderOfMag * eValue));
            }
            yDisplayRange = yDisplayMax - yDisplayMin;

            for (int i = 0; i < x.length; i++) {
                for (int j = 0; j < numberOfPlots; j++) {
                    yi[i][j] = (int) (ySize - (((1 + (y[i][j] / yDisplayRange)) * 0.8 * ySize) + (0.075 * ySize)));
                }
            }
        }

        //end of scaling..
        this.yInt = yi;

    }

//-----------------------------------------------------------------------------		
// scaling within a fixed range
    public void fixedXScaling() {	// set the limits

        this.xSize = getSize().width;

        //here starts the scaling bit, it's kind of tricky but best is to remember, that the plotting area is going from
        //0.075 xSize to 0.875 xSize
        // therefore the autoscaling attempts to put all the points into this range (in a reasonable way)
        int[][] xi = new int[x.length][numberOfPlots];

        xDisplayRange = xDisplayMax - xDisplayMin;

        //x-scaling
        xOrderOfMag = (int) (Math.log(xDisplayRange) / eValue);
        if (Math.log(xDisplayRange) < 0) {
            xOrderOfMag = xOrderOfMag - 1;
        }
        if (((Math.log(xDisplayRange) / eValue) - xOrderOfMag) < 0.5) {
            xTick = 0.5;
        } else {
            xTick = 1;
        }

        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < numberOfPlots; j++) {
                xi[i][j] = (int) ((((x[i][j] - xDisplayMin) / xDisplayRange) * 0.8 * xSize) + (0.075 * xSize));
            }
        }

        if (xDisplayMax > 0) {
            xCase = 2;
        } else {
            xCase = 4;
        }

        this.xInt = xi;

    }

//-------------------------------------------------------------------------------------------
    public void fixedYScaling() {	// set the limits

        this.ySize = getSize().height;

        //here starts the scaling bit, it's kind of tricky but best is to remember, that the plotting area is going from
        // and from 0.125 ySize to 0.925 ySize
        // therefore the autoscaling attempts to put all the points into this range (in a reasonable way)
        int[][] yi = new int[x.length][numberOfPlots];

        yDisplayRange = yDisplayMax - yDisplayMin;

        //y-scaling
        yOrderOfMag = (int) (Math.log(yDisplayRange) / eValue);
        if (Math.log(yDisplayRange) < 0) {
            yOrderOfMag = yOrderOfMag - 1;
        }
        if (((Math.log(yDisplayRange) / eValue) - yOrderOfMag) < 0.5) {
            yTick = 0.5;
        } else {
            yTick = 1;
        }

        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < numberOfPlots; j++) {
                yi[i][j] = (int) (ySize - ((((y[i][j] - yDisplayMin) / (yDisplayRange)) * 0.8 * ySize) + (0.075 * ySize)));
            }
        }
        if (yDisplayMax > 0) {
            yCase = 2;
        } else {
            yCase = 4;
        }

        this.yInt = yi;
    }

//------------------------------------------------------------------------------------
    public void paint(Graphics g) {

        // do the scaling depending on which constructor was chosen...
        // this is controlled by the options which are set by the constructor
        if (scalingOption == 1) {
            fixedXScaling();
            fixedYScaling();
        } else if (scalingOption == 2) {
            if (limitOption == 0) {
                fixedXScaling();
                autoYScaling();
            } else {
                autoXScaling();
                fixedYScaling();
            }
        } else {
            autoXScaling();
            autoYScaling();
        }

        // now the graph(s) are drawn: the choice of colors is somehow random
        // (the reason was the program i wrote and the first 4 colors correspond to the colors of the light investigated in this program...)
        // so don't worry about the colors. and change their order...
        g.setColor(Color.white);
        g.fillRect(0, 0, (int) xSize, (int) ySize);

        Color[] graphColors = new Color[7];
        graphColors[4] = new Color(0, 0, 0);
        graphColors[2] = new Color(0, 0, 255);
        graphColors[1] = new Color(0, 255, 0);
        graphColors[0] = new Color(255, 0, 0);
        graphColors[5] = new Color(0, 255, 255);
        graphColors[3] = new Color(255, 0, 255);
        graphColors[6] = new Color(255, 200, 0);

        int colorCount = 0;

        // draw either line or dots (dots are little rectangles)
        // if you change to ovals use at least circles of size 3Pixels otherwise, u can't see them on certain systems.
        for (int j = 0; j < numberOfPlots; j++) {
            g.setColor(graphColors[colorCount]);
            int[] yiHelp = new int[xInt.length];
            int[] xiHelp = new int[xInt.length];
            for (int i = 0; i < xInt.length; i++) {
                xiHelp[i] = xInt[i][j];
                yiHelp[i] = yInt[i][j];
            }
            if (plotOption == 0) {
                if (regressionOption) {
                    for (int k = 0; k < xInt.length; k++) {
                        g.fillOval(xiHelp[k] - 1, yiHelp[k] - 1, 3, 3);
                    }
                } else {
                    for (int k = 0; k < xInt.length; k++) {
                        g.fillRect(xiHelp[k] - 1, yiHelp[k] - 1, 2, 2);
                    }
                }
            } else {
                g.drawPolyline(xiHelp, yiHelp, xInt.length);
            }
            colorCount++;
            if (colorCount == 7) {
                colorCount = 0;
            }
        }

        if (regressionOption) {	//scaling of the regression lines
            int[] intMaxPoints = new int[maxPoints.length];
            int[] intMinPoints = new int[maxPoints.length];
            colorCount = 0;

            for (int j = 0; j < maxPoints.length; j++) {
                g.setColor(graphColors[colorCount]);
                intMaxPoints[j] = (int) (ySize - ((((maxPoints[j] - yDisplayMin) / (yDisplayRange)) * 0.8 * ySize) + (0.075 * ySize)));
                intMinPoints[j] = (int) (ySize - ((((minPoints[j] - yDisplayMin) / (yDisplayRange)) * 0.8 * ySize) + (0.075 * ySize)));
                g.drawLine((int) (0.075 * xSize), intMinPoints[j], (int) ((0.8 * xMax * xSize / xDisplayRange) + 0.075 * xSize), intMaxPoints[j]);

                colorCount++;
                if (colorCount == 7) {
                    colorCount = 0;
                }
            }
        }

        //draw a line for the yAxis on the left hand side of the frame
        g.setColor(Color.black);
        g.drawLine((int) (0.075 * xSize), (int) (0.125 * ySize), (int) (0.075 * xSize), (int) (0.925 * ySize));

        // do all the writing (labels and title)
        Font titleFont = new Font("Serif", Font.BOLD, 24);
        g.setFont(titleFont);
        g.drawString(title, (int) (0.35 * xSize), (int) (0.05 * ySize));

        Font subtitleFont = new Font("Serif", Font.BOLD, 12);
        g.setFont(subtitleFont);
        g.drawString(subtitle, (int) (0.35 * xSize), (int) (0.085 * ySize));

        Font labelFont = new Font("Serif", Font.BOLD, 12);
        g.setFont(labelFont);
        g.drawString(yLabel, (int) (0.005 * xSize), (int) (0.06 * ySize));
        g.drawString(xLabel, (int) (0.45 * xSize), (int) (0.985 * ySize));

        // decide where to put the x-axis (top / bottom)
        // depending on the range of the y-values of the points
        // if y-values are negative, the xaxis line is on top,
        // else on the bottom (generally)
        // decide whether to draw a line at y=0
        int yAxisCoord = 0;
        int xAxisCoord = 0;

        if (yCase == 1) {
            xAxisCoord = (int) (0.925 * ySize);
            g.drawLine((int) (0.075 * xSize), xAxisCoord, (int) (0.875 * xSize), xAxisCoord);
        } else if (yCase == 2 && yDisplayMin > 0) {
            xAxisCoord = (int) (0.925 * ySize);
            g.drawLine((int) (0.075 * xSize), xAxisCoord, (int) (0.875 * xSize), xAxisCoord);
        } else if (yCase == 2) {
            int xZeroAxisCoord = (int) (ySize - (((-yDisplayMin / yDisplayRange) * 0.8 * ySize) + (0.075 * ySize)));
            g.drawLine((int) (0.075 * xSize), xZeroAxisCoord, (int) (0.875 * xSize), xZeroAxisCoord);
            xAxisCoord = (int) (0.925 * ySize);
            g.drawLine((int) (0.075 * xSize), xAxisCoord, (int) (0.875 * xSize), xAxisCoord);
        } else if (yCase == 4 || yCase == 3) {
            xAxisCoord = (int) (0.125 * ySize);
            g.drawLine((int) (0.075 * xSize), xAxisCoord, (int) (0.875 * xSize), xAxisCoord);
        }

        if (yOrderOfMag != 1 && yOrderOfMag != 0) {
            if (yCase > 2) {
                g.drawString(("*10^" + Integer.toString(yOrderOfMag)), (int) (0.005 * xSize), (int) (0.98 * ySize));
            } else {
                g.drawString(("*10^" + Integer.toString(yOrderOfMag)), (int) (0.005 * xSize), (int) (0.1 * ySize));
            }
        } else if (yOrderOfMag == 1) {
            yTick = 10 * yTick;
            yOrderOfMag = 0;
        }

        yAxisCoord = (int) (0.075 * xSize);

        // set the ticks
        // decide whether above or beyond xAxis
        // if xaxis on top of the frame, draw ticks above,
        // else draw beyond
        int TickCoord;
        int OOMCoord;
        if (yCase < 3) {
            TickCoord = (xAxisCoord + (int) (0.03 * ySize));
            OOMCoord = (xAxisCoord + (int) (0.045 * ySize));
        } else {
            TickCoord = (xAxisCoord - (int) (0.015 * ySize));
            OOMCoord = (xAxisCoord - (int) (0.03 * ySize));
        }

        // indicate the order of magnitude of the ticks if necessary
        if (xOrderOfMag != 1 && xOrderOfMag != 0) {
            g.drawString(("*10^" + Integer.toString(xOrderOfMag)), (int) (0.91 * xSize), OOMCoord);
        } else if (xOrderOfMag == 1) {
            xTick = 10 * xTick;
            xOrderOfMag = 0;
        }

        // now draw the tick lines and add specific values (values are either 0.5, 1, 1.5.... or 1,2,3... the order of magnitude added before indicates the true size)
        double Tick;
        switch (xCase) {
            case 1: //all points >0 & 0 included in xAxis
            {
                Tick = 0.0;
                while ((Tick * Math.exp(xOrderOfMag * eValue)) < (1.01 * xDisplayMax)) {
                    int xCoord = (int) ((((Tick * Math.exp(xOrderOfMag * eValue)) / xDisplayRange) * 0.8 * xSize) + (0.075 * xSize));
                    g.drawLine(xCoord, (int) (xAxisCoord - (0.005 * ySize)), xCoord, (int) (xAxisCoord + (0.005 * ySize)));
                    g.drawString(Double.toString(Tick), (xCoord - (int) (0.015 * ySize)), TickCoord);
                    Tick = Tick + xTick;
                }
            }
            break;
            case 2:
                if (xDisplayMin < 0) {
                    Tick = -xTick;
                    while ((Tick * Math.exp(xOrderOfMag * eValue)) > (1.01 * xDisplayMin)) {
                        int xCoord = (int) (((((Tick * Math.exp(xOrderOfMag * eValue)) - xDisplayMin) / (xDisplayRange)) * 0.8 * xSize) + (0.075 * xSize));
                        g.drawLine(xCoord, (int) (xAxisCoord - (0.005 * ySize)), xCoord, (int) (xAxisCoord + (0.005 * ySize)));
                        g.drawString(Double.toString(Tick), (xCoord - (int) (0.015 * ySize)), TickCoord);
                        Tick = Tick - xTick;
                    }
                    Tick = 0.0;
                    while ((Tick * Math.exp(xOrderOfMag * eValue)) < (1.01 * xDisplayMax)) {
                        int xCoord = (int) (((((Tick * Math.exp(xOrderOfMag * eValue)) - xDisplayMin) / (xDisplayRange)) * 0.8 * xSize) + (0.075 * xSize));
                        g.drawLine(xCoord, (int) (xAxisCoord - (0.005 * ySize)), xCoord, (int) (xAxisCoord + (0.005 * ySize)));
                        g.drawString(Double.toString(Tick), (xCoord - (int) (0.015 * ySize)), TickCoord);
                        Tick = Tick + xTick;
                    }
                } else {
                    Tick = 0.0;
                    while ((Tick * Math.exp(xOrderOfMag * eValue)) < (0.99 * xDisplayMin)) {
                        Tick = Tick + xTick;
                    }
                    while ((Tick * Math.exp(xOrderOfMag * eValue)) < (1.01 * xDisplayMax)) {
                        int xCoord = (int) (((((Tick * Math.exp(xOrderOfMag * eValue)) - xDisplayMin) / (xDisplayRange)) * 0.8 * xSize) + (0.075 * xSize));
                        g.drawLine(xCoord, (int) (xAxisCoord - (0.005 * ySize)), xCoord, (int) (xAxisCoord + (0.005 * ySize)));
                        g.drawString(Double.toString(Tick), (xCoord - (int) (0.015 * ySize)), TickCoord);
                        Tick = Tick + xTick;
                    }
                }
                break;
            case 3: {
                Tick = 0.0;
                while ((Tick * Math.exp(xOrderOfMag * eValue)) > (0.99 * xDisplayMax)) {
                    Tick = Tick - xTick;
                }
                while ((Tick * Math.exp(xOrderOfMag * eValue)) > (1.01 * xDisplayMin)) {
                    int xCoord = (int) (((((Tick * Math.exp(xOrderOfMag * eValue)) - xDisplayMin) / (xDisplayRange)) * 0.8 * xSize) + (0.075 * xSize));
                    g.drawLine(xCoord, (int) (xAxisCoord - (0.005 * ySize)), xCoord, (int) (xAxisCoord + (0.005 * ySize)));
                    g.drawString(Double.toString(Tick), (xCoord - (int) (0.015 * ySize)), TickCoord);
                    Tick = Tick - xTick;
                }
            }
            break;
            case 4: {
                Tick = 0.0;
                while ((Tick * Math.exp(xOrderOfMag * eValue)) > (1.01 * xDisplayMin)) {
                    int xCoord = (int) (((1 + ((Tick * Math.exp(xOrderOfMag * eValue)) / xDisplayRange)) * 0.8 * xSize) + (0.075 * xSize));
                    g.drawLine(xCoord, (int) (xAxisCoord - (0.005 * ySize)), xCoord, (int) (xAxisCoord + (0.005 * ySize)));
                    g.drawString(Double.toString(Tick), (xCoord - (int) (0.015 * ySize)), TickCoord);
                    Tick = Tick - xTick;
                }
            }
            break;
        }

        //----------------------yStuff--------
        // the tick business is exactly the same as before for the xAxis
        TickCoord = (int) (yAxisCoord - (0.07 * ySize));

        switch (yCase) {
            case 1: //all points >0 & 0 included in xAxis
            {
                Tick = 0.0;
                while ((Tick * Math.exp(yOrderOfMag * eValue)) < (1.01 * yDisplayMax)) {
                    int yCoord = (int) (ySize - ((((Tick * Math.exp(yOrderOfMag * eValue)) / yDisplayRange) * 0.8 * ySize) + (0.075 * ySize)));
                    g.drawLine((int) (yAxisCoord - (0.005 * xSize)), yCoord, (int) (yAxisCoord + (0.005 * xSize)), yCoord);
                    g.drawString(Double.toString(Tick), TickCoord, yCoord + (int) (0.015 * ySize));
                    Tick = Tick + yTick;
                }
            }
            break;
            case 2:
                if (yDisplayMin < 0) {
                    Tick = -yTick;
                    while ((Tick * Math.exp(yOrderOfMag * eValue)) > (1.01 * yDisplayMin)) {
                        int yCoord = (int) (ySize - (((((Tick * Math.exp(yOrderOfMag * eValue)) - yDisplayMin) / (yDisplayRange)) * 0.8 * ySize) + (0.075 * ySize)));
                        g.drawLine((int) (yAxisCoord - (0.005 * xSize)), yCoord, (int) (yAxisCoord + (0.005 * xSize)), yCoord);
                        g.drawString(Double.toString(Tick), TickCoord, yCoord + (int) (0.015 * ySize));
                        Tick = Tick - yTick;
                    }
                    Tick = 0.0;
                    while ((Tick * Math.exp(yOrderOfMag * eValue)) < (1.01 * yDisplayMax)) {
                        int yCoord = (int) (ySize - (((((Tick * Math.exp(yOrderOfMag * eValue)) - yDisplayMin) / (yDisplayRange)) * 0.8 * ySize) + (0.075 * ySize)));
                        g.drawLine((int) (yAxisCoord - (0.005 * xSize)), yCoord, (int) (yAxisCoord + (0.005 * xSize)), yCoord);
                        g.drawString(Double.toString(Tick), TickCoord, yCoord + (int) (0.015 * ySize));
                        Tick = Tick + yTick;
                    }
                } else {
                    Tick = 0.0;
                    while ((Tick * Math.exp(yOrderOfMag * eValue)) < (0.99 * yDisplayMin)) {
                        Tick = Tick + yTick;
                    }
                    while ((Tick * Math.exp(yOrderOfMag * eValue)) < (1.01 * yDisplayMax)) {
                        int yCoord = (int) (ySize - (((((Tick * Math.exp(yOrderOfMag * eValue)) - yDisplayMin) / (yDisplayRange)) * 0.8 * ySize) + (0.075 * ySize)));
                        g.drawLine((int) (yAxisCoord - (0.005 * xSize)), yCoord, (int) (yAxisCoord + (0.005 * xSize)), yCoord);
                        g.drawString(Double.toString(Tick), TickCoord, yCoord + (int) (0.015 * ySize));
                        Tick = Tick + yTick;
                    }
                }
                break;
            case 3: {
                Tick = 0.0;
                while ((Tick * Math.exp(yOrderOfMag * eValue)) > (0.99 * yDisplayMax)) {
                    Tick = Tick - yTick;
                }
                while ((Tick * Math.exp(yOrderOfMag * eValue)) > (1.01 * yDisplayMin)) {
                    int yCoord = (int) (ySize - (((((Tick * Math.exp(yOrderOfMag * eValue)) - yDisplayMin) / yDisplayRange) * 0.8 * ySize) + (0.075 * ySize)));
                    g.drawLine((int) (yAxisCoord - (0.005 * xSize)), yCoord, (int) (yAxisCoord + (0.005 * xSize)), yCoord);
                    g.drawString(Double.toString(Tick), TickCoord, yCoord + (int) (0.015 * ySize));
                    Tick = Tick - yTick;
                }
            }
            break;
            case 4: {
                Tick = 0.0;
                while ((Tick * Math.exp(yOrderOfMag * eValue)) > (1.01 * yDisplayMin)) {
                    int yCoord = (int) (ySize - (((1 + ((Tick * Math.exp(yOrderOfMag * eValue)) / yDisplayRange)) * 0.8 * ySize) + (0.075 * ySize)));
                    g.drawLine((int) (yAxisCoord - (0.005 * xSize)), yCoord, (int) (yAxisCoord + (0.005 * xSize)), yCoord);
                    g.drawString(Double.toString(Tick), TickCoord, yCoord + (int) (0.015 * ySize));
                    Tick = Tick - yTick;
                }
            }
            break;
        }

    }
}
