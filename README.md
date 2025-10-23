# FxControls

The FxControls library is an open source project for Java 8 that serves as a growing collection of basic utilities and custom classes for stuff that is needed by a lot of JavaFX based application code bases. This library will be small at first, mostly serving as a sanity check for the AWT and Swing based libraries as I begin to finalize those into permanent code. In many cases, the functionality and implementation will be similar to GuiToolkit, to the degree that this is possible.

Although the official JavaFX module system includes layouts and stages under graphics instead of under controls, I find it more logical to group them here as my fxgraphics library is more about drawing and imaging.

The initial release of Version 1 will require Java 8 as the JDK/JRE target, due to its use of newer language features.

There will be a modularized version at some point, that supports Java 14+. If I find a way to make it compatible with long term support versions of Java (such as Java 11), I will do what I can, but may have trouble finding an appropriate JDK still available to download and test against.

Eclipse and NetBeans related support files are included as they are generic and are agnostic to the OS or to the user's system details and file system structure, so it seems helpful to post them in order to accelerate the integration of this library into a user's normal IDE project workflow and build cycle.

The Javadocs are not yet 100% compliant or complete, as this is a rushed initial posting whose main purpose is to make my open source work available in my new job.

As a confidence boost at both ends, LayoutFactory has a main() function that prints "Hello Maven from FxGuiToolkit" to the console (e.g. the one in Eclipse IDE). By running Maven's clean task, then the install task, you can quickly gain confidence that everything is integrated properly, by then running the main class and seeing the console and confirming that this library was the source of the validation message.

This project may eventually be split up to depend on my upcoming FxGraphics library, and will be marked as such in the Maven POM file.

Until I publish to Maven Central or elswehere, I am attempting to provide pre-built JAR files for binary builds, source code, and Javadocs.

Recently, I have pulled in some helpful code that makes use of Qoppa's free jPDFWriter library for transcoding HTML to PDF. Unfortunately, the Maven Central coordinates for recent versions of this library are broken, and older versions are not available there, so for now you will need to download your own copy of the library JAR (not the desktop product that shows usages) from the following URL:

https://www.qoppa.com/pdfwriter/download/#jardownload

Once downloaded, you will need to create an M2 cache for it. The POM file for this librfary will do this for you, but will leave it with blank placeholders due to the failure to find at Maven Central. As the JAR file is not named correctly according to Maven standards, copy it to "~.m2/com/qoppa/jpdfwriter/jpdfwriter/v2021R1.00" and then rename the JAR file from "jPDFWriter.v2021R1.00.jar" to "jpdfwriter-v2021R.100.jar". This should align it with the entry in this library's POM file.

This decoupling allows me to finally start switching most of my libraries from Java 8 to Java 25.


