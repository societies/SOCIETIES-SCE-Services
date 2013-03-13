------------------------------------------------------
-- Description
------------------------------------------------------

The Ijacket  presents a first gui where the user can choose a CIS and connect its Jacket to it.
The CISs available for choosing are the ones in which the application has been shared to
or in other words, the ones that match: Application.GlobalID = org.societies.thirdpartyservices.ijacketlib.IJacketDefines.AccountData.IJACKET_SERVICE_NAME

If the application has been started with an intent, it will look for the extra IjacketIntentExtras.CIS_ID in order to retrieve the id of the default community to choose from

If the user press the button to scan QR code the application will scan a QR code and connect via bluetooth to a jacket linked to that QR code. If there is no jacket, you can use the button for Jacketless test.
  
If you connect to a jacket, you will be able to test its actuators (vibration, display and on) through the buttons on the jacket Menu GUI.
And whenever an activity is published on the community configure on the first gui, this activity will be displayed on the Jacket display, as long as it has as a target:  AccountData.IJACKET_SERVICE_NAME
In the case of the jacketlesstest, instead of displaying the activity through the jacket display that is done instead through an Android Toast (the android equivalent to a pop-up)




------------------------------------------------------
-- Building
------------------------------------------------------


This project has been built with maven. 
For using maven just run "mvn clean install" to build the APK
and "mvn android:deploy  android:run" to deply the app and run it on your device or emulator.

However, before building you must mavenize the ubicollab sdk jar. 
The jar can be built from https://github.com/UbiCollab/UbiCollabSDK 
And it also can be found on the currUbiclobVersion folder within this project.
But you have to maveninze it so it matches the following import:

		    <groupId>no.sintef.ubicollab</groupId>
		    <artifactId>sdk</artifactId>
		    <version>/version> (Ive used the same as github)


Mavenizing the jar is not so difficult, you mainly have to run the following command:
mvn install:install-file -Dfile=<path-to-file> -DgroupId=<group-id> \
    -DartifactId=<artifact-id> -Dversion=<version> -Dpackaging=<packaging>

You also need to have built the ijacketLib aplklib which is located in this github repository.

If you do not use maven, you have to configure the project and add the jars yourself.

Also notice that this appplication is targeted to Android 4.1.2 and on. It uses content observers which are available just since that android api.