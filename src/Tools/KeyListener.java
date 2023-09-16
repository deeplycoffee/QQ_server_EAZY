package Tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class KeyListener {
    String getInput;

    static public String judgeLength(Scanner scanner,int num){
        String getInput = scanner.next();
        if(getInput.length()>=num)return getInput.substring(0,num);
        return getInput;
    }
    static public byte[] streamTurnToByte(InputStream is) throws IOException {
        byte[] buf = new byte[1024];//每次传1024byte
        int len ;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while ((len = is.read(buf))!=-1){
            byteArrayOutputStream.write(buf,0,len);
            byteArrayOutputStream.flush();
        }
        byte[] ans = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return ans;
    }

}
