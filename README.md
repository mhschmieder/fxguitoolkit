# FxGuiToolkit
The FxGuiToolkit library is an open source project for Java 8 that serves as a growing collection of basic utilities and custom classes for stuff that is needed by a lot of JavaFX based application code bases. This library will be small at first, mostly serving as a sanity check for the AWT and Swing based libraries as I begin to finalize those into permanent code. In many cases, the functionality and implementation will be similar to GuiToolkit, to the degree that this is possible.

The initial release of Version 1 will require Java 8 as the JDK/JRE target, due to its use of newer language features. I will check to see if it might be made less restrictive as it may only need Java 7, but anything earlier than that introduces unpleasantires on macOS in terms of independent standalone builds.

There will be a modularized version soon, that supports Java 14+. If I find a way to make it compatible with other still-supported versions of Java (I think Java 11 is still supported by Oracle, but not Java 12 or Java 10, and maybe not Java 9 either), I will do what I can, but may have trouble finding an appropriate JDK still available to download and test against.

Eclipse and NetBeans related support files are included as they are generic and are agnostic to the OS or to the user's system details and file system structure, so it seems helpful to post them in order to accelerate the integration of this library into a user's normal IDE project workflow and build cycle.

The Javadocs are 100% compliant and complete, but I am still learning how to publish those at the hosting site that I think is part of Maven Central, as it is a bad idea to bloat a GitHub project with such files and to complicate repository changes (just as with binary files and archices). Hopefully later tonight!

As a confidence boost at both ends, LayoutUtilities has a main() function that prints "Hello Maven from FxGuiToolkit" to the console (e.g. the one in Eclipse IDE). By running Maven's clean task, then the install task, you can quickly gain confidence that everything is integrated properly, by then running the main class and seeing the console and confirming that this library was the source of the validation message.

This projects may eventually depend on my upcoming FxGraphicsToolkit library, and will be marked as such in the Maven POM file.
