import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.InputStream;

public class SerialHelper {
    private static int serialPort = 5;


    public static void main(String[] args) {
        getAllPorts();

        System.out.println("Distance is " + getTemperature());
    }

    public static String getTemperature(){
        SerialPort comPort = SerialPort.getCommPorts()[serialPort];
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        InputStream in = comPort.getInputStream();
        String temperature = "";


        try {
            int x = -1;
            System.out.println(x!=32);
            //Stops at new line
            while (x!=10){
                if (x!=0) {
                    String word = "";
                    x = in.read();
                    word += (char)x;
                    while (x!=32){

                        x = in.read();
                        word += (char)x;
                        if (x==32) {
                            break;
                        } else if (x==10){
                            break;
                        }
                    }

                    temperature = word;
                    System.out.println("SCANNED: " + word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        comPort.closePort();
        return temperature;
    }

    public static void getAllPorts() {
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        System.out.println("Number of serial port available:{}" + serialPorts.length);
        for (int portNo = 0; portNo < serialPorts.length; portNo++) {
            System.out.println("SerialPort[{" + portNo+1 + "}]:[{" + serialPorts[portNo].getSystemPortName() + "},{" + serialPorts[portNo].getDescriptivePortName() + " }]");
        }
    }
}
