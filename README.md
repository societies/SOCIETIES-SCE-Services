##Crowd Tasking for GAE

Set up a project in IDEA:

* Create Run/Debug configuration. Add Google AppEngine Dev Server, enter a name and press ok. You can select different port if you want. Default is 8080.
* Add 2 Java global libraries. (File->Projct Structure->Global Libraries):
	* AppEngineDev -> GAE_SDK/lib/shared
     * AppEngineAPI -> GAE_SDK/lib/user
 * Add 2 global libraries to the artifact