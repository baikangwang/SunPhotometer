/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sunphotometer;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author wyq
 * Stores two digits of each item as a pair of 4-bit values. Weird, but true.
 * A hex dump of the value looks right in decimal form, e.g. binary 0010 0001
 * codes a 2 and a 1, and "21" is the correct decimal month/hour/minute etc.,
 * even though binary 00100001 is &H21 or 33 decimal.
 */
public class DecDateTime {
    /**
     * only stores a 2-digit year, so "04" is 2004. I'm only guessing, but will dates
     * from the 1990s be stored as "90", "91", "92" ...? If so, the four-digit year is NOT
     * just year+2000
     */
    public byte year; 
    public byte month;
    public byte day;
    public byte hour;
    public byte minute;
    public byte second;
    
    /**
     * Constructor
     * @param r RandomAccessFile
     * @throws java.io.IOException
     */
    public DecDateTime(RandomAccessFile r) throws IOException{
        this.year = r.readByte();
        this.month = r.readByte();
        this.day = r.readByte();
        this.hour = r.readByte();
        this.minute = r.readByte();
        this.second = r.readByte();
    }
}
