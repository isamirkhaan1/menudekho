package datalayer;

import java.io.BufferedReader;

/**
 * Created by Samir KHan on 9/5/2016.
 */
public class BufferedReaderToString {

    public static String convert(BufferedReader reader){
        String line, data = "";
    try {
        while ((line = reader.readLine()) != null) {
            data = data + line + "\n";
        }
    }catch (Exception exp){

    }finally {
        return data;
    }
    }
}
