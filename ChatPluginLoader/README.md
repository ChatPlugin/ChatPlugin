ChatPlugin uses a custom class loader (which is common to both the free and the premium version) to load the plugin's and its libraries' classes. If all the libraries were included in the plugin's JAR, its size would have been something like ~25 MB. However by default no external libraries are required (except the database connector one): they are downloaded only if you enable certain modules, like, for example, the Discord integration or the IP lookup.
\
This loader also includes the plugin's APIs.
