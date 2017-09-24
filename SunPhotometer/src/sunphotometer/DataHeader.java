/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sunphotometer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yaqiang Wang generic record Note: a data block can be as few as two
 * bytes long, which has nothing more than a FLAG byte and a byte count (=2).
 * You can't assume a full header is present - read the FLAG and the byte count,
 * then decide if there is anything more to read.
 */
public class DataHeader {

    public byte flag;          //byte 0 hex record flag, see below
    public int rec_size;      //byte 1 hex number of bytes
    public byte nscen;         //senario number - ignore if x80.
    /**
     * filter number, for for almucantar and principle plain measurements.
     * Status code, for STA blocks. Meaningless otherwise?
     */
    public byte fltnb;
    public DecDateTime dataDate;  //6 bytes
    
    private String type;
    private boolean validDate;
    private String aDateStr;
    private String bDateStr;
    public int length;
    
    /**
     * Constructor
     */
    public DataHeader(){
        
    }

    /**
     * Constructor
     *
     * @param r RndomAccessFile
     * @throws java.io.IOException
     */
    public DataHeader(RandomAccessFile r) throws IOException {
        this.length = (int)r.getFilePointer();
        this.flag = r.readByte();
        String sFlag = String.format("%02X", this.flag).toUpperCase();
        String year, month, day, hour, minute, second;
        Date date;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy,HH:mm:ss");
        switch (sFlag) {
            case "DE":
                type = "NSU";
                break;
            case "88":
                type = "BLK";
                break;
            case "86":
                type = "PP1";
                break;
            case "8E":
                type = "ALL";
                break;
            case "8D":
                type = "ALR";
                break;
            case "8B":
                type = "PPP";
                break;
            case "DF":
                type = "SUN";
                break;
            case "80":
                type = "STA";
                break;
            case "DC":
                type = "SSK";
                break;
            default:
                type = "UN";
                break;
        }
        this.rec_size = DataProcess.byte2Int(r.readByte());
        if (type.equals("UN")) {            
            if (this.rec_size < 2) {
                while (true) {
                    if (String.format("%02X", r.readByte()).toUpperCase().equals("FE")) {
                        r.readByte();
                        break;
                    }
                }
            }
            r.skipBytes(this.rec_size - 2);            
        } else {
            if (this.rec_size < 10) {
                while (true) {
                    if (String.format("%02X", r.readByte()).toUpperCase().equals("FE")) {
                        r.readByte();
                        break;
                    }
                }
            }
            String size = String.valueOf(this.rec_size);
            this.nscen = r.readByte();
            this.fltnb = r.readByte();
            this.dataDate = new DecDateTime(r);
            year = String.format("%02X", this.dataDate.year);
            //year = DatatypeConverter.printHexBinary(new byte[]{this.dataDate.year});
            //if (year.length() == 1)
            //    year = "0" + year;
            try{
                if (Integer.parseInt(year) > 80){
                    year = "19" + year;
                } else {
                    year = "20" + year;
                }
            } catch (NumberFormatException e){
                e.printStackTrace();
            }
//            if (DataProcess.byte2Int(this.dataDate.year) > 80){
//                year = String.valueOf(DataProcess.byte2Int(this.dataDate.year) + 1900);
//            } else {
//                year = String.valueOf(DataProcess.byte2Int(this.dataDate.year) + 2000);
//            }
            month = String.format("%02X", this.dataDate.month);
            day = String.format("%02X", this.dataDate.day);
            hour = String.format("%02X", this.dataDate.hour);
            minute = String.format("%02X", this.dataDate.minute);
            second = String.format("%02X", this.dataDate.second);
            this.aDateStr = year + "-" + month + "-" + day + " " + hour + ":" +
                    minute + ":" + second;
            try {
                date = format.parse(aDateStr);
                this.validDate = true;
                this.bDateStr = format1.format(date);                
            } catch (ParseException ex) {
                Logger.getLogger(DataHeader.class.getName()).log(Level.SEVERE, null, ex);
                this.validDate = false;
            }
        }    
        this.length = (int)r.getFilePointer() - this.length;
    }
    
    /**
     * Get type
     * @return Type string
     */
    public String getType(){
        return this.type;
    }
    
    /**
     * Get if this data header has valid data or not.
     * @return Boolean
     */
    public boolean isValidDate(){
        return this.validDate;
    }
    
    /**
     * Get aDateStr
     * @return aDateStr
     */
    public String getADateStr(){
        return this.aDateStr;
    }
    
    /**
     * Get bDateSr
     * @return bDateStr
     */
    public String getBDateStr(){
        return this.bDateStr;
    }
}
