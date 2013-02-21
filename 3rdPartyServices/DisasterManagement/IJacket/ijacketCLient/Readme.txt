------------------------------------------------------
-- Description
------------------------------------------------------

The Ijacket client presents a gui where the user can choose a CIS and send a message to it.
The CISs available for choosing are the ones in which the application has been shared to
or in other words, the ones that match: Application.GlobalID = org.societies.thirdpartyservices.ijacketlib.IJacketDefines.AccountData.IJACKET_CLIENT_SERVICE_NAME

If the application has been started with an intent, it will look for the extra IjacketIntentExtras.CIS_ID in order to retrieve the id of the default community
  
For publishing into the feed, the user just write his text in the text box and press send.

That will trigger the publishing of the activity with the following parameters:
ACTOR: username (retrieved from me table)
VERB: posted
OBJECT: text written in the text box
TARGET: AccountData.IJACKET_SERVICE_NAME



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