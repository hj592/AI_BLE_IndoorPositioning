# AI_BLE_Location_Positioning

**AI BLE Location Positioning** is a system that enhances BLE signal positioning accuracy by converting RSSI values into distances. The system collects RSSI data through embedded devices, transmits it to a server, and utilizes AI models to analyze the signal and convert it to distance to perform accurate location estimation.

## Published Paper
> <a href="https://doi.org/10.3390/electronics13224518">**Indoor Positioning Method by CNN-LSTM of Continuous Received Signal Strength Indicator**</a>
> 
> Jae-hyuk Yoon, Hee-jin Kim, Dong-seok Lee, Soon-kak Kwon
>
><img src="https://github.com/user-attachments/assets/92c9725e-6b4b-49a6-bb09-d802f6572514" width="400px"/>
## Project Overview
>- **Project Name**: AI BLE Location Positioning
>- **Project Duration**: June 2024 - October 2024
>- **Objective**: To improve BLE signal positioning accuracy by converting RSSI values ​​to distance using an AI-based model

## Previous Research
This project builds upon previous research efforts. You can explore the prior implementations here:
>- **Server**: [GitHub Repository](https://github.com/911lab/Server.git)
>- **Android App**: [GitHub Repository](https://github.com/911lab/Android-App.git)

## System Architecture

>![Image](https://github.com/user-attachments/assets/ac3c71a9-ef30-44eb-a832-99183024552d)
>1. **Embedded Device**: Collects BLE signals and transmits them to the server
>2. **Server**: Receives BLE data and estimates the location by estimating the distance using an AI model
>3. **CNN-LSTM Model**: Estimates the distance from each transmitter using continuous signal strengths
>4. **Monitoring System**: Visualizes location information
>
>>### 🛠 Tech Stack
>>| Category      | Tech Stack |
>>|--------------|-------------------------------------------------|
>>| **Embedded Device** | ![Raspberry Pi Pico W](https://img.shields.io/badge/Raspberry%20Pi%20Pico%20W-<색상코드>.svg?style=for-the-badge&logo=<로고이름>&logoColor=white) ![MicroPython](https://img.shields.io/badge/MicroPython-%23000000.svg?style=for-the-badge&logo=python&logoColor=white) |
>>| **Server**    | ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-%236DB33F.svg?style=for-the-badge&logo=spring-boot&logoColor=white) ![Java](https://img.shields.io/badge/Java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white) ![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-%23000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white) |
>>| **CNN-LSTM Model**  | ![PyTorch](https://img.shields.io/badge/PyTorch-%23EE4C2C.svg?style=for-the-badge&logo=pytorch&logoColor=white) ![Python](https://img.shields.io/badge/Python-%233776AB.svg?style=for-the-badge&logo=python&logoColor=white) |

## Project Demo

><img src="https://github.com/user-attachments/assets/f3b7bae1-a5de-41b9-b84d-3cfc392b2969" width="800"/>
