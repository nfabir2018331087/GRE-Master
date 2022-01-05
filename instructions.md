## To Run this Project:  
This is an android project. It is built using Android Studio.  

So, for running this project in your PC, first you need to have Android Studio. Download android studio and set it up.  
You also need to have JDK installed in your PC, as this project is coded in Java.  

After setting up the environment, you need to clone this project from this repository.  

![image](https://user-images.githubusercontent.com/52916568/148169064-d90ab83b-6713-4fdf-9011-ac6daa2b384d.png)  
To clone this project with the code showing above you need to have git in your local device and you need to know the functionalities of git. Or you can download the zipped 
file and extract.

Then open the project from android studio. Wait some times for Gradle to sync up the project. Before clicking run button you need to set up an AVD(Android Virtual Device)
which is a virtual emulator where you can install and run this app. Sometimes AVD doesn't work properly(As we've faced), you can connect your phone to your pc and run on it.  
To install and run this project on your phone you need to turn on your USB Debugging option of your phone, which you will find in Settings>Developer options(May vary in different device). And also running android project on your phone is a lot less heavier than running on AVD as android studio itself is a very heavy software.  

For storing data we have used Firebase as a database. Realtime database, Firebase storage, Cloud Messaging are the elements we used from firebase.


## FAQ:  
### Q1: Why this project isn't running on my PC?  
Ans: This can be happen for some issues. First let the project sync up completely after opening. Dependencies can be outdated as we have used Picasso, FirebaseUI and etc. So, update them to the latest versions.  
### Q2: Why can't I login or signup?  
Ans: As we have faced this sometimes too, it's probably because of the selected API version of the AVD. Try to run the project on your phone and keep your internet connection on.



