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
 * Each byte stores the value in hex, can be used directly
 */
public class HexDateTime {
    public byte year;    //year 1900 = 0, so add 1900 to get four-digit year, or subtract 100 to get two-digit year after 1999.
    public byte month;
    public byte day;
    public byte hour;
    public byte minute;
    public byte second;
    
    /**
     * Constructor
     * @param r RandomAccessFile
     * @throws IOException 
     */
    public HexDateTime(RandomAccessFile r) throws IOException{
        this.year = r.readByte();
        this.month = r.readByte();
        this.day = r.readByte();
        this.hour = r.readByte();
        this.minute = r.readByte();
        this.second = r.readByte();
    }
}
