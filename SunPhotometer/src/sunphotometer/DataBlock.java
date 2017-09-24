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
 */
public class DataBlock {
    public DataHeader data_header;     //Total of 10 bytes
    /**
     * buffer for 255 hex observation values, in pairs
     * for many measurements, the second-last entry is temperature (in tenths, offset by 40, so T = count/10 - 40)
     * for all measurements, the last pair of bytes are an end flag (first byte, = xFE) and a repeat of the
     * block byte count (rec_size, above). This give s check that no data is missing or erroneous bytes added
     * in a serial transfer, I presume.
     */
    public BigEndianInt buffer; 
    
    /**
     * Constructor
     */
    public DataBlock(){
        this.data_header = new DataHeader();
        this.buffer = new BigEndianInt();
    }
    
    /**
     * Constructor
     * @param r RandomAccessFile
     * @throws IOException 
     */
    public DataBlock(RandomAccessFile r) throws IOException{
        this.data_header = new DataHeader(r);
        this.buffer.high = r.readByte();
        this.buffer.low = r.readByte();
    }
}
