package TestTools;

import com.fazecast.jSerialComm.SerialPort;
import org.apache.xml.serializer.SerializationHandler;

import java.io.IOException;
import java.io.InputStream;

public class SerialReader {
    public static void main(String[] args)  {
        getAllPorts();
        SerialPort comPort = SerialPort.getCommPorts()[3];
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        InputStream in = comPort.getInputStream();

        while(true){
            try {
                System.out.println(in.read());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //comPort.closePort();

    }

    public static void getAllPorts() {
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        System.out.println("Number of serial port available:{}" + serialPorts.length);
        for (int portNo = 0; portNo < serialPorts.length; portNo++) {
            System.out.println("SerialPort[{" + portNo+1 + "}]:[{" + serialPorts[portNo].getSystemPortName() + "},{" + serialPorts[portNo].getDescriptivePortName() + " }]");
        }
    }
}
