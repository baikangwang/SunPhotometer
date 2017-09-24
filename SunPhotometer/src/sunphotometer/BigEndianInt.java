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
public class BigEndianInt {
    public byte high;
    public byte low;
    
    /**
     * Constructor
     */
    public BigEndianInt(){
        
    }
    
    /**
     * Constructor
     * @param r RandomAccessFile
     * @throws java.io.IOException
     */
    public BigEndianInt(RandomAccessFile r) throws IOException{
        this.high = r.readByte();
        this.low = r.readByte();
    }
}
