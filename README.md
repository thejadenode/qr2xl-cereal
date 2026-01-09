# qr2xl-cereal

A Java-based desktop application to automate temperature screening, designed to be used as a functional kiosk for a high school.

## What it does

This application automates the process of temperature screening and data logging. It performs the following functions:

*   **Personnel Identification:** Captures and decodes QR codes using a webcam to identify individuals.
*   **Temperature Sensing:** Integrates with an Arduino-Uno-based temperature sensor via a serial port to read temperature data.
*   **Data Logging:** Records the captured ID, temperature, and a timestamp into a Microsoft Excel spreadsheet. A new worksheet is automatically created for each day.

## Technology Stack

*   **Core:** Java
*   **GUI:** Java Swing (UI created with IntelliJ IDEA's GUI Designer)
*   **QR Code Processing:** Google ZXing
*   **Excel Manipulation:** Apache POI
*   **Serial Communication:** jSerialComm
*   **Webcam Access:** Webcam-Capture
*   **Build Tool:** Gradle

## How to Run

1.  **Prerequisites:**
    *   Java Development Kit (JDK) installed.
    *   An Arduino-based temperature sensor connected to a serial port.
    *   A webcam.

2.  **Running the application:**
    *   Clone this repository.
    *   Open a terminal or command prompt in the project's root directory.
    *   Execute the following command:
        ```bash
        ./gradlew run
        ```
    *   On Windows, you should use:
        ```bash
        gradlew.bat run
        ```

This will start the application.
