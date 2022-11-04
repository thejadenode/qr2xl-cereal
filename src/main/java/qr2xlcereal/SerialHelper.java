/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qr2xlcereal;
import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.io.InputStream;

import java.io.IOException;

/**
 *
 * @author pjgal
 */
public class SerialHelper {
    private static int serialPort = 5;
    private static int errorCorrect = 4; //used to adjust temperature sensor


    public static void main(String[] args) {
        getAllPorts();
    }

    public static String getTemperature(){
        SerialPort comPort = SerialPort.getCommPorts()[getArduinoPort()];
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
                    System.out.println("[TempScanner]: " + word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        comPort.closePort();
        System.out.println("[TempScanner] Orig Temperature: " + temperature);
        System.out.println("[TempScanner] Final Temperature " + "(+" + errorCorrect + "): " + Double.parseDouble(temperature)+errorCorrect);
        temperature = "" + (Double.parseDouble(temperature)+errorCorrect);
        return temperature;
    }

    public static void getAllPorts() {
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        System.out.println("[PortFinder] Number of serial port available:{}" + serialPorts.length);
        for (int portNo = 0; portNo < serialPorts.length; portNo++) {
            //System.out.println("SerialPort[{" + portNo+1 + "}]:[{" + serialPorts[portNo].getSystemPortName() + "},{" + serialPorts[portNo].getDescriptivePortName() + " }]");
            System.out.println("[PortFinder] SerialPort[{" + portNo + "}]:[{" + serialPorts[portNo].getSystemPortName() + "},{" + serialPorts[portNo].getPortDescription() + " }]");
        }
    }

    public static int getArduinoPort(){
        getAllPorts();
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        System.out.println("[PortFinder] Number of serial port available:{}" + serialPorts.length);
        for (int portNo = 0; portNo < serialPorts.length; portNo++) {
          if (serialPorts[portNo].getPortDescription().length() >= 17){
              //System.out.println(serialPorts[portNo+1].getPortDescription().substring(0, 17));
              if (serialPorts[portNo].getPortDescription().substring(0, 17).equals("USB Serial Device")){
                  System.out.println("[PortFinder] Arduino is on port " + (portNo));
                  return portNo;
              }
          }
            //System.out.println("SerialPort[{" + portNo+1 + "}]:[{" + serialPorts[portNo].getSystemPortName() + "},{" + serialPorts[portNo].getPortDescription() + " }]");
        }
        System.out.println("[PortFinder] !!!Arduino Port cannot be found!!!");
        return 0;
    }
    
    public static void checkup(){
        int arduinoPort = getArduinoPort();
        if (getArduinoPort()==0){
            System.out.println("[>Checkup] Temperature sensor cannot be found.");
        } else System.out.println(">[Checkup] Temperature sensor is on port " + arduinoPort);
    }
}
