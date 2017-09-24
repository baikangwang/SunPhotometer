/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sunphotometer;

import com.github.junrar.extract.ExtractArchive;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author wyq
 */
public class DataProcess {

    /**
     * Decode K7 data file
     *
     * @param fileName K7 data file name
     * @return Data list
     * @throws FileNotFoundException
     */
    public static List decode(String fileName) throws FileNotFoundException, IOException {
        List<String> dataList = new ArrayList<>();
        String line, lastV = "";
        RandomAccessFile r = new RandomAccessFile(fileName, "r");
        //Read file header
        FileHeader fh = new FileHeader(r);
        Parameters par = fh.getParameters();
        String status;
        String BV, hirh, Hpe, Vpe, temp, BV12, infostatus, DD, CA;
        String PCr, NumError, ErrorType, TaskInError;
        String infoStatus1, infostatus2, infostatus3, infostatus4, infostatus5, infostatus6;
        int NumStepLost;
        int dNum;
        while (true) {
            if (r.getFilePointer() >= r.length()) {
                break;
            }
            //Reda data header
            DataHeader dh = new DataHeader(r);
            if (!dh.isValidDate()) {
                r.skipBytes(dh.rec_size - 10);
                continue;
            }

            if (r.getFilePointer() + dh.rec_size - 10 > r.length()) {
                break;
            }

            //Read data
            String size = String.valueOf(dh.rec_size);
            DataBlock db = new DataBlock();
            String type = dh.getType();
            line = "";
            dNum = 0;
            switch (type) {
                case "STA":
                    status = String.valueOf((char) dh.fltnb);
                    switch (status) {
                        case "b":
                            db.buffer.high = r.readByte();
                            db.buffer.low = r.readByte();
                            if ((String.format("%02X", db.buffer.high).toUpperCase()
                                    + String.format("%02X", db.buffer.high).toUpperCase()).equals("FFFB")) {
                                temp = "Abs";
                            } else {
                                temp = String.format("%.1f", (float) (256 * byte2Int(db.buffer.high) + byte2Int(db.buffer.low)) / 10 - 40);
                            }
                            BV = String.valueOf((float) byte2Int(r.readByte()) / 25);
                            hirh = String.valueOf((float) byte2Int(r.readByte()) / 2);
                            byte x = r.readByte();
                            byte y = r.readByte();
                            Hpe = "0x" + String.format("%02X", x);
                            Vpe = "0x" + String.format("%02X", y);
                            line = status + "," + temp + "," + BV + "," + hirh + "," + Hpe + "," + Vpe;
                            break;
                        case "r":
                            PCr = "0x" + String.format("%02X", r.readByte()).toUpperCase()
                                    + String.format("%02X", r.readByte()).toUpperCase();
                            NumError = Byte.toString(r.readByte());
                            infostatus = "0x" + String.format("%02X", r.readByte()).toUpperCase();
                            ErrorType = "0x" + String.format("%02X", r.readByte()).toUpperCase();
                            TaskInError = Byte.toString(r.readByte());
                            line = status + "," + PCr + "," + NumError + "," + infostatus + "," + ErrorType + "," + TaskInError;
                            break;
                        case "h":
                            db.buffer.high = r.readByte();
                            db.buffer.low = r.readByte();
                            temp = String.valueOf((float) (256 * byte2Int(db.buffer.high) + byte2Int(db.buffer.low)) / 10 - 40);
                            BV = String.valueOf((float) byte2Int(r.readByte()) / 25);
                            infostatus = "0x" + String.format("%02X", r.readByte()).toUpperCase();
                            DD = Byte.toString(r.readByte());
                            CA = Byte.toString(r.readByte());
                            line = status + "," + temp + "," + BV + "," + infostatus + "," + DD + "," + CA;
                            break;
                        case "p":
                            infoStatus1 = Byte.toString(r.readByte());
                            infostatus2 = Byte.toString(r.readByte());
                            infostatus3 = Byte.toString(r.readByte());
                            infostatus4 = Byte.toString(r.readByte());
                            infostatus5 = Byte.toString(r.readByte());
                            infostatus6 = Byte.toString(r.readByte());
                            line = status + "," + infoStatus1 + "," + infostatus2 + "," + infostatus3 + ","
                                    + infostatus4 + "," + infostatus5 + "," + infostatus6;
                            break;
                        case "s":
                            db.buffer.high = r.readByte();
                            db.buffer.low = r.readByte();
                            if ((String.format("%02X", db.buffer.high).toUpperCase()
                                    + String.format("%02X", db.buffer.high).toUpperCase()).equals("FFFB")) {
                                temp = "Abs";
                            } else {
                                temp = String.format("%.1f", (float) (256 * byte2Int(db.buffer.high) + byte2Int(db.buffer.low)) / 10 - 40);
                            }
                            BV = String.valueOf((float) byte2Int(r.readByte()) / 25);
                            infostatus = "0x" + String.format("%02X", r.readByte()).toUpperCase();
                            db.buffer.high = r.readByte();
                            db.buffer.low = r.readByte();
                            NumStepLost = 256 * byte2Int(db.buffer.high) + byte2Int(db.buffer.low);
                            line = status + "," + temp + "," + BV + "," + infostatus + "," + String.valueOf(NumStepLost);
                            break;
                        case "A":
                        case "B":
                        case "C":
                        case "D":
                            db.buffer.high = r.readByte();
                            db.buffer.low = r.readByte();
                            temp = String.format("%.1f", (float) (256 * byte2Int(db.buffer.high) + byte2Int(db.buffer.low)) / 10 - 40);
                            //hirh = "Abs";
                            BV = String.valueOf((float) byte2Int(r.readByte()) / 25);
                            if (status.equals("C")) {
                                infostatus = "0x" + String.format("%02X", r.readByte()).toUpperCase();
                                BV12 = String.valueOf((float) (byte2Int(r.readByte()) * 256 + byte2Int(r.readByte())) / 100);
                                line = status + "," + temp + "," + BV + "," + infostatus;
                            } else {
                                hirh = String.valueOf((float) byte2Int(r.readByte()) / 2);
                                BV12 = String.valueOf((float) (byte2Int(r.readByte()) * 256 + byte2Int(r.readByte())) / 100);
                                line = status + "," + temp + "," + hirh + "," + BV + "," + BV12;
                            }
                            break;
                        case "E":
                        case "F":
                            db.buffer.high = r.readByte();
                            db.buffer.low = r.readByte();
                            temp = String.valueOf((float) (256 * byte2Int(db.buffer.high) + byte2Int(db.buffer.low)) / 10 - 40);
                            BV = String.valueOf(byte2Int(r.readByte()) / 25);
                            infostatus = "0x" + String.format("%02X", r.readByte()).toUpperCase();
                            BV12 = String.valueOf((byte2Int(r.readByte()) * 256 + byte2Int(r.readByte())) / 100);
                            line = status + "," + temp + "," + "," + BV + "," + BV12 + "," + infostatus;
                            break;
                        default:
                            db.buffer.high = r.readByte();
                            db.buffer.low = r.readByte();
                            temp = String.valueOf((float) (256 * byte2Int(db.buffer.high) + byte2Int(db.buffer.low)) / 10 - 40);
                            BV = String.valueOf((float) byte2Int(r.readByte()) / 25);
                            infostatus = "0x" + String.format("%02X", r.readByte()).toUpperCase();
                            DD = Byte.toString(r.readByte());
                            CA = Byte.toString(r.readByte());
                            line = status + "," + temp + "," + "," + BV + "," + "," + infostatus + "," + DD + "," + CA;
                            break;
                    }
                    while (true) {
                        if (String.format("%02X", r.readByte()).toUpperCase().equals("FE")) {
                            r.readByte();
                            dataList.add(type + ";" + dh.getADateStr() + ";" + size + ";" + dh.getBDateStr() + "," + line);
                            break;
                        }
                    }
                    break;
                case "BLK":
                    while (true) {
                        db.buffer.high = r.readByte();
                        db.buffer.low = r.readByte();
                        if (String.format("%02X", db.buffer.high).toUpperCase().equals("FE")) {
                            int aLength, length;
                            aLength = line.length();
                            length = lastV.length();
                            line = line.substring(0, aLength - (length + 1))
                                    + line.substring(aLength - (length + 1) + (length + 1));
                            if (size.equals("40")) {
                                lastV = String.format("%.1f", (Float.parseFloat(lastV) / 10 - 40));
                            }
                            line = line + lastV;
                            dataList.add(type + ";" + dh.getADateStr() + ";" + size + ";" + dh.getBDateStr() + "," + line);
                            break;
                        } else {
                            if ((256 * byte2Int(db.buffer.high)) >= 32768) {
                                lastV = String.valueOf(32768 - (256 * byte2Int(db.buffer.high) + byte2Int(db.buffer.low)));
                            } else {
                                lastV = String.valueOf(256 * byte2Int(db.buffer.high) + byte2Int(db.buffer.low));
                            }
                            line = line + lastV + ",";
                        }
                    }
                    break;
                default:
                    while (true) {
                        db.buffer.high = r.readByte();
                        db.buffer.low = r.readByte();
                        if (String.format("%02X", db.buffer.high).toUpperCase().equals("FE")) {
                            lastV = String.format("%.1f", (Float.parseFloat(lastV) / 10 - 40));
                            line = line + lastV;
                            if (dh.rec_size - 10 < dNum * 2 + 2) {
                                String[] dataArray = line.split(",");
                                int dataNum = dataArray.length;
                                line = "";
                                for (int i = 1; i < (dh.rec_size - 10) / 2; i++) {
                                    line = dataArray[dataNum - i] + "," + line;
                                }
                                line = line.replaceAll("[c]+$", "");
                            }
                            if (type.equals("ALL") || type.equals("ALR") || type.equals("PP1")) {
                                line = Byte.toString(dh.fltnb) + "," + line;
                            }
                            line = dh.getBDateStr() + "," + line;
                            if (type.equals("SSK")) {
                                line = SSK2NSU(line);
                                type = "NSU";
                            }
                            dataList.add(type + ";" + dh.getADateStr() + ";" + size + ";" + line);
                            break;
                        }
                        if (dNum > 0) {
                            line = line + lastV + ",";
                        }
                        dNum += 1;
                        lastV = String.valueOf(256 * byte2Int(db.buffer.high) + byte2Int(db.buffer.low));
                    }
                    break;
            }
        }

        r.close();

        return dataList;
    }

    /**
     * Convert byte to int - byte in Java is signed
     *
     * @param b Input byte
     * @return Output integer
     */
    public static int byte2Int(byte b) {
        return b >= 0 ? (int) b : (int) (b + 256);
    }

    /**
     * Convert SSK to NSU
     *
     * @param ssk SSK string
     * @return NSU string
     */
    public static String SSK2NSU(String ssk) {
        List<Integer> idx = new ArrayList<>();
        for (int i = 0; i <= 20; i++) {
            if (i <= 9) {
                idx.add(i);
            } else if (i >= 17) {
                idx.add(i);
            }
        }
        idx.add(26);
        for (int i = 22; i <= 24; i++) {
            idx.add(i);
        }
        for (int i = 32; i <= 35; i++) {
            idx.add(i);
        }
        idx.add(41);
        for (int i = 37; i <= 39; i++) {
            idx.add(i);
        }
        idx.add(47);
        String[] ssks = ssk.split(",");
        String nsu = ssks[0];
        for (int i = 1; i < idx.size(); i++) {
            nsu = nsu + "," + ssks[idx.get(i)];
        }

        return nsu;
    }

    /**
     * Merge multiple K7 files to a single K7 file
     *
     * @param fileNames Input K7 files
     * @param outFileName Output merged K7 file
     * @throws java.io.FileNotFoundException
     */
    public static void mergeFiles(List<String> fileNames, String outFileName) throws FileNotFoundException, IOException {
        if (fileNames.size() < 1) {
            return;
        }

        RandomAccessFile bw = new RandomAccessFile(outFileName, "rw");
        byte[] data;
        DataHeader dh;
        List<String> dataList = new ArrayList<>();
        String line;
        int i = 0;
        for (String fn : fileNames) {
            //System.out.println(fn);
            RandomAccessFile r = new RandomAccessFile(fn, "r");
            byte[] fh = new byte[256];
            r.read(fh);
            if (i == 0){
                bw.write(fh);
            }
            
            while (true) {
                if (r.getFilePointer() >= r.length()) {
                    break;
                }
                //Reda data header
                dh = new DataHeader(r);
                if (!dh.isValidDate()) {
                    r.skipBytes(dh.rec_size - 10);
                    continue;
                }

                line = dh.getType() + ";" + dh.getADateStr();
                r.seek(r.getFilePointer() - dh.length);
                data = new byte[dh.rec_size];
                r.read(data);
                if (!dataList.contains(line)) {
                    dataList.add(line);                    
                    bw.write(data);
                }
            }
            r.close();
            i += 1;
        }

        bw.close();
    }

    public static void removeDuplicate(String fn) throws FileNotFoundException, IOException {
        RandomAccessFile r = new RandomAccessFile(fn, "r");
        byte[] data;
        DataHeader dh;
        List<String> dataList = new ArrayList<>();
        List<byte[]> newData = new ArrayList<>();
        String line;
        byte[] fh = new byte[256];
        r.read(fh);
        while (true) {
            if (r.getFilePointer() >= r.length()) {
                break;
            }
            //Reda data header
            dh = new DataHeader(r);
            if (!dh.isValidDate()) {
                r.skipBytes(dh.rec_size - 10);
                continue;
            }

            line = dh.getType() + ";" + dh.getADateStr();
            if (!dataList.contains(line)) {
                dataList.add(line);
                r.seek(r.getFilePointer() - dh.length);
                data = new byte[dh.rec_size];
                r.read(data);
                newData.add(data);
            }
        }
        r.close();

        RandomAccessFile w = new RandomAccessFile(fn, "w");
        w.write(fh);
        for (byte[] d : newData) {
            w.write(d);
        }
        w.close();
    }
    
    /**
     * Write data list to an ASCII file
     * @param dataList The data list
     * @param type Data type
     * @param fileName File name
     * @throws IOException
     * @throws ParseException 
     */
    public static void writeASCIIFile(List<String> dataList, String type, String fileName) throws IOException, 
            ParseException{
        List<String> ndata = getDataByType(dataList, type);
        writeASCIIFile(ndata, fileName);
    }
    
    /**
     * Write data list to an ASCII file
     * @param dataList The data list
     * @param fileName File name
     * @throws IOException
     * @throws ParseException 
     */
    public static void writeASCIIFile(List<String> dataList, String fileName) throws IOException, 
            ParseException{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format1 = new SimpleDateFormat("d M yyyy H m s");
        BufferedWriter sw = new BufferedWriter(new FileWriter(new File(fileName)));
        String dline;
        String[] data;     
        Date date;
        for (String line : dataList){
            data = line.split(";");
            dline = data[3];
            date = format.parse(data[1]);
            data = dline.split(",");
            line = format1.format(date);
            for (int i = 2; i < data.length; i++){
                line = line + " " + data[i];
            }
            sw.write(line);
            sw.write("\n");
        }
        sw.close();
    }
    
    /**
     * Get data list by data type
     * @param dataList Original data list
     * @param type Data type
     * @return Result data list
     */
    public static List getDataByType(List<String> dataList, String type){
        List<String> r = new ArrayList<>();
        int n = type.length();
        for (String line : dataList){
            if (line.substring(0, n).equals(type)){
                r.add(line);
            }
        }
        
        return r;
    }
    
    /**
     * Calculate AOT
     * @param exeFn Excute file
     * @param inputFn Input parameter file
     * @param calFn Calibration file
     * @param taoFn Result AOT file
     * @param lat Latitude of the station
     * @param lon Longitude of the station
     * @param alt Altitude of the station (m a.s.l)
     * @param ozonoFn Ozon file
     * @param nsuFn NSU data file
     * @throws java.io.IOException
     */
    public static void calAOT(String exeFn, String inputFn, String calFn, String taoFn, float lat,
            float lon, float alt, String ozonoFn, String nsuFn) throws IOException{
        //Write input parameter file
        BufferedWriter sw = new BufferedWriter(new FileWriter(new File(inputFn)));
        sw.write("1");
        sw.newLine();
        sw.write(calFn);
        sw.newLine();
        sw.write(taoFn);
        sw.newLine();
        sw.write("1");
        sw.newLine();
        sw.write(String.valueOf(lat));
        sw.newLine();
        sw.write(String.valueOf(lon));
        sw.newLine();
        sw.write(String.valueOf(alt));
        sw.newLine();
        sw.write("-2");
        sw.newLine();
        sw.write("-1");
        sw.newLine();
        sw.write(ozonoFn);
        sw.newLine();
        sw.write("1");
        sw.newLine();
        sw.write("4");
        sw.newLine();
        sw.write("2 3 4 5");
        sw.newLine();
        sw.write("-1");
        sw.newLine();
        sw.write(nsuFn);
        sw.close();
        
        //Calculate AOT
        Runtime.getRuntime().exec(exeFn + " " +  inputFn + " /G0");
    }
    
    /**
     * Unzip RAR file
     * @param rarFn RAR file
     * @param destFolder Destination folder
     */
    public static void unrar(String rarFn, String destFolder){
        File rar = new File(rarFn);
        File dfolder = new File(destFolder);
        ExtractArchive ea = new ExtractArchive();
        ea.extractArchive(rar, dfolder);
    }

}