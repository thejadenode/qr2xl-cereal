import com.fazecast.jSerialComm.SerialPort;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainGUI extends JFrame implements Runnable, ThreadFactory {

    private Webcam webcam = null;
    private WebcamPanel panel = null;
    private JPanel mainPanel;
    private JPanel panelCamera;
    private JButton btnFileSelector;
    private JPanel panelInfo;
    private JPanel panelFileBar;
    private JPanel panelHeader;
    private JLabel txtInfo;
    private JLabel txtHeader;
    private JLabel txtFilePath;
    private JLabel txtSubInfo;
    private JFileChooser jFileChooser;

    private Executor executor = Executors.newSingleThreadExecutor(this);
    private SheetsHelper sheetsHelper = new SheetsHelper();
    private Boolean isProcessing = false;
    private String dateStamp;
    private Boolean isLockedScanner = true;
    private SerialHelper serialHelper = new SerialHelper();

    public MainGUI(String title){
        super(title);

        Dimension size = WebcamResolution.QVGA.getSize();

        System.out.println(Webcam.getWebcams());

        webcam = Webcam.getWebcams().get(0);
        //webcam.setViewSize(size);
//
        panel = new WebcamPanel(webcam);
//        panel.setPreferredSize(size);
        panel.setFPSDisplayed(true);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true);
        panel.setFitArea(true);
        panelCamera.add(panel);

        jFileChooser = new JFileChooser();
        jFileChooser.setAcceptAllFileFilterUsed(false);
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Excel Files", "xls", "xlsx"));

        dateStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());

        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        btnFileSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = jFileChooser.showOpenDialog(MainGUI.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {

                    File file = jFileChooser.getSelectedFile();
                    sheetsHelper.setFile(file);
                    System.out.println("File location is at " + sheetsHelper.getFilePath());
                    txtFilePath.setText(sheetsHelper.getFilePath());
                    isLockedScanner = false;

                }
            }
        });
        executor.execute(this);
    }

    public static void main(String[] args) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainGUI("App Title").setVisible(true);

            }
        });
    }

    @Override
    public void validate() {
        super.validate();
        Font labelFont = txtInfo.getFont();
        String labelText = txtInfo.getText();
        int stringWidth = txtInfo.getFontMetrics(labelFont).stringWidth(labelText);
        int componentWidth = txtInfo.getSize().width;
        System.out.println("label width: " + componentWidth);
        System.out.println("font size: " + labelFont.getSize());
        double widthRatio = (double)componentWidth / (double)stringWidth;
        int newFontSize = (int)(labelFont.getSize() * widthRatio);
        System.out.println("new font size: " + newFontSize);
        int componentHeight = txtInfo.getHeight();
        int fontSizeToUse = Math.min(newFontSize, componentHeight);
        txtInfo.setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));

        Font txtHeaderFont = txtHeader.getFont();
        String txtHeaderText = txtHeader.getText();
        int txtHeaderstringWidth = txtHeader.getFontMetrics(txtHeaderFont).stringWidth(txtHeaderText);
        int txtHeadercomponentWidth = txtHeader.getSize().width;
        System.out.println("label width: " + txtHeadercomponentWidth);
        System.out.println("font size: " + txtHeaderFont.getSize());
        double txtHeaderwidthRatio = (double)txtHeadercomponentWidth / (double)txtHeaderstringWidth;
        int txtHeadernewFontSize = (int)(txtHeaderFont.getSize() * txtHeaderwidthRatio);
        System.out.println("new font size: " + txtHeadernewFontSize);
        int txtHeadercomponentHeight = txtHeader.getHeight();
        int txtHeaderfontSizeToUse = Math.min(txtHeadernewFontSize, txtHeadercomponentHeight);
        txtHeader.setFont(new Font(txtHeaderFont.getName(), Font.PLAIN, txtHeaderfontSizeToUse));

        System.out.println("Validated");
    }

    @Override
    public void run() {
        do {
            if (!isLockedScanner) {
                if (!isProcessing) {
                    isProcessing = true;
                    txtInfo.setText("");
                    txtSubInfo.setText("");
                    panelFileBar.setBackground(Color.GRAY);
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Result result = null;
                    BufferedImage image = null;


                    if (webcam.isOpen()) {
                        System.out.println("webcam is open and scanning");
                        if ((image = webcam.getImage()) == null) {
                            continue;
                        }

                        LuminanceSource source = new BufferedImageLuminanceSource(image);
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                        try {
                            result = new MultiFormatReader().decode(bitmap);
                        } catch (NotFoundException e) {
                            // fall thru, it means there is no QR code in image
                        }
                    }

                    if (result != null) {
                        txtInfo.setText(result.getText() + ", please scan your temperature");
                        String timeStamp = getCurrentTimeStamp();
                        System.out.println("Scanned: " + result.getText() + " at " + timeStamp);
                        panelFileBar.setBackground(Color.GREEN);

                        String temp = serialHelper.getTemperature();
                        System.out.println("Temperature is " + serialHelper.getTemperature());
                        txtInfo.setText(result.getText());
                        txtSubInfo.setText(temp +"\u00B0" + "C | " + getTemperatureMeaning(Float.parseFloat(temp)));
                        switch(getTemperatureMeaning(Float.parseFloat(temp))){
                            case "Mild Fever": txtSubInfo.setForeground(Color.ORANGE);
                                break;

                            case "Fever": txtSubInfo.setForeground(Color.RED);
                                break;

                            case "Normal": txtSubInfo.setForeground(Color.GREEN);
                                break;
                        }

                        try {
                            if (!sheetsHelper.isExisting(result.getText(), dateStamp)) {
                                sheetsHelper.appendValue(result.getText(), dateStamp, timeStamp);
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    isProcessing = false;
                }
            } else {
                panelFileBar.setBackground(Color.GRAY);
            }
        } while (true);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "example-runner");
        t.setDaemon(true);
        return t;
    }

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    public static String getTemperatureMeaning(float temp){
        if (temp >= 38) return "Fever";
        else if (temp >= 37) return "Mild Fever";
        else return "Normal";
    }
}
