# Ttech mpod-android


## Requirements
```$xslt
- Gradle 5.1.1 or above
- Android Studio 3.4.1 or above
- Android SDK Tools 26.1.1 or above
```



## Installing the project locally
```$xslt
Install latest version of Android Studio. Link: [Android Studio](https://developer.android.com/studio)
Clone the project and save locally.
Open Android Studio, select "Import project (Gradle, Eclipse ADT, etc.)"
Find the saved project, and open the Refresh directory inside the root directory.
```





## Making your external android device visible to your computer
```$xslt
On your android device, go to Settings > About phone. Look for build number and tap 7 times to activate developer mode.
Go back to the main settings menu, look for "Developer options" and click.
Under Debugging, enable USB debugging. 
```




## Connecting an external device for testing
```$xslt
Connect android device and computer via USB
When the android device prompts you to allow access to your data, click allow.
Make sure in Developer Options under Networking, USB configuration is set to MTP or Media Transfer Protocol.
```



## Running the project from the IDE
```$xslt
On the toolbar underneath the Menu Bar, click the green play button.
Select a deployment target. This can be your connected device or an emulator. 
You can create an emulator by clicking "Create New Virtual Device"
Click Ok.
```

## Building and exporting the APK file
```$xslt
On the Menu Bar, click the Build > Generate Signed Bundle or APK.
Click APK.

FIRST TIME: Click create new. Fill in all the information. Not all information under "Certificate" is necessary.
RETURNING TIME: Click choose existing.

Fill in the passwords and alias. Click Next.
For build variants select "release". For signature versions, check both V1 (Jar Signature) and V2 (Full APK Signature).
Click Finish.
```