# 🤓: "Why don't you use Gradle?"
This folder contains .jar files of some plugins ChatPlugin integrates with.
Before you ask - these plugins' code has not been uploaded to any [Maven](https://maven.apache.org/) repositories:
the only way to interact with them is to add them as local .jar dependencies. They are shipped with the plugin's source for this reason.

Not all plugins' versions are up-to-date because:
- it is not required - only basic and (mostly) update-proof API methods are called by ChatPlugin
- some paid plugins (Matrix 🫵🏼) do not even expose API methods for free anymore 🤦🏼

If you are the developer of one of these plugins, please upload the code to a Maven repository and open a new issue or pull request.
This should apply for every public project, though. Always upload artifacts and use a modern buildtool, like [Gradle](https://gradle.org).
