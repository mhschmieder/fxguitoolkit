# fxspreadsheet
Decoupled wrapper for downstream use of ControlsFX SpreadsheetView.

The purpose of splitting this functionality off from my fxguitoolkit is that my contracts no longer bind me to Java 8 adherence, and SpreadsheetView in ControlsFX has had fits and starts in the Java 9+ world. Though it is once again available, its long-term development may be out of sync with overall JavaFX and ControlsFX updates, and I haven't had time yet to check my API compatibility or to publish my bugfixes from 2019 and earlier (if still relevant and needed).

This decoupling allows me to finally start switching most of my libraries from Java 8 to Java 25.
