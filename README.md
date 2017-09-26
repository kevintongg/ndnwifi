## P2P Communication
This project is a proof of concept on establishing a P2P connection between two or more android-devices, and make them communicate using Multicast (UDP). This is accomplished by utilizing the [WiFi P2P API](http://developer.android.com/guide/topics/connectivity/wifip2p.html), also known as WiFi Direct.

### Prerequisites
 - [Android Studio Bundle](http://developer.android.com/sdk/index.html#)
 - [Java Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
 - [Git](http://git-scm.com/downloads)

### Getting started
If you encounter problems, see "troubleshooting" section further down.
- Install Java Development Kit
- Install Android Studio Bundle
- Set up Android SDK with [Android Support Library](https://developer.android.com/tools/support-library/setup.html)
- Install Git
- Check out the project from GitHub repository
- Import project in Android Studio

### Things to note
- All devices you want to connect to each other have to search for each other simultaneously. This is because each device will not advertise itself to other devices until the discovery process is started.
- If you disconnect from a WiFi Direct network, you have to run the discovery process again **before** trying to reconnect. If you don't, you will get a "Busy"-error, and you may have to turn WiFi on your phone off and on again.

### Troubleshooting
- [Android Studio cannot find git.exe (Create process error=2)](https://github.com/bouvet-bergen/p2pcommunication/wiki/Set-git-executable-path)
- [Android Studio cannot find JDK](https://github.com/bouvet-bergen/p2pcommunication/wiki/Set-JAVA_HOME-environment-variable)
- Android Virtual Devices does not support WiFi, so testing the app on a physical device is recommended.
- WiFi Direct on devices with older Android-versions than 4.2 Jelly Bean has several bugs. [Issue tracker](https://code.google.com/p/android/issues/detail?id=43004)
- ["Message not multicasted" when trying to send messages](https://github.com/bouvet-bergen/p2pcommunication/wiki/Message-not-multicasted)

### FAQ
- Minimum SDK version: 16
- Target SDK version: 21

### Libraries used
- [ButterKnife](http://jakewharton.github.io/butterknife/)



