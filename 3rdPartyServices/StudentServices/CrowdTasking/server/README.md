##Crowd Tasking for GAE

Set up a project in IDEA:

* Create Run/Debug configuration. Add Google AppEngine Dev Server, enter a name or just click on the Application server and press ok. You can select different port if you want. Default is 8080.
* Add 2 Java global libraries. (File->Projct Structure->Global Libraries):
    * AppEngineDev -> GAE_SDK/lib/shared
    * AppEngineAPI -> GAE_SDK/lib/user

    Press cancel when IDEA wants to add library to the selected module. The libraries are already there.