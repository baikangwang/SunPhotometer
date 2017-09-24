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
 * @author Yaqiang Wang
 */
public class FileHeader {
    public byte auto;       //byte 0 auto code Older instruments use 3B (59) newer 3F (63)
    public byte country;    //byte 1 country code
    public byte district;   //byte 2 district code
    /**
     * byte 3-4 hex InstNum =256*inst_num[0]+inst_num[1], new inst,
     * for the older instrument use only one byte inst_num[1]
     * changed structure from array of two bytes to BigEndianInt. Now use
     * inst_num.high instead of inst_num(0), and inst_num.low instead
     * of inst_num(1)
     */
    public BigEndianInt inst_num;
    public byte[] unkw2;          //byte 5-83 lots of goodies here will expand
    /**
     * byte 84-85 hex Latitude (mins) = 256*lat_min[0]+lat_min[1]-32768
     * same conversion from array of two bytes to BigEndianInt
     */
    public BigEndianInt lat_min;
    /**
     * byte 86 hex Longitude (hours) = long_hrs -128
     * but negative values are off-by-one??? BLO shows up as -7, although
     * -6 is entered into the instrument. We get 0 for the first 15 zone east of
     * Greenwich (0-15 E long.), so long_hrs represents the western boundary of
     * the 15 long. zone. Note, however, that for W long. the long_min and
     * long_secs values that follow are distances added to the west of the
     * eastern boundary....
     */
    public byte long_hrs;
    public byte long_min;    //byte 87 hex
    public byte long_secs;   //byte 88 hex
    public byte[] unkw3;
    public String name;
    public byte[] unkw4;
    public HexDateTime instDate;  //6 bytes
    public HexDateTime pcDate;    //6 bytes
    public byte[] pad;            //padding (100 bytes) to a total header size of 256 bytes
    
    /**
     * Constructor
     * @param r RandowAccessFile
     * @throws IOException 
     */
    public FileHeader(RandomAccessFile r) throws IOException{
        byte[] bytes = new byte[8];
        this.auto = r.readByte();
        this.country = r.readByte();
        this.district = r.readByte();
        this.inst_num = new BigEndianInt();
        this.inst_num.high = r.readByte();
        this.inst_num.low = r.readByte();
        this.unkw2 = new byte[79];
        r.read(this.unkw2);
        this.lat_min = new BigEndianInt();
        this.lat_min.high = r.readByte();
        this.lat_min.low = r.readByte();
        this.long_hrs = r.readByte();
        this.long_min = r.readByte();
        this.long_secs = r.readByte();
        this.unkw3 = new byte[39];
        r.read(this.unkw3);
        r.read(bytes);
        this.name = new String(bytes).trim();
        this.unkw4 = new byte[8];
        r.read(this.unkw4);
        this.instDate = new HexDateTime(r);
        this.pcDate = new HexDateTime(r);
        this.pad = new byte[100];
        r.read(this.pad);
    }
    
    /**
     * Get parameters
     * @return Parameters
     */
    public Parameters getParameters(){
        Parameters par = new Parameters();
        par.auto = Byte.toString(this.auto);
        par.country = Byte.toString(this.country);
        par.inst_num = String.valueOf(256 * this.inst_num.high + this.inst_num.low);
        par.lat_min = String.valueOf(256 * this.lat_min.high + this.lat_min.low - 32768);
        par.longitude = String.valueOf(this.long_hrs + this.long_min / 60 + this.long_secs / 3600);
        return par;
    }
}
