@ECHO OFF

:: ================================================================================================================== ::
::              Launch HpoCaseAnnotator app by double-clicking on the batch script in the file browser.               ::
:: ================================================================================================================== ::

%JAVA_HOME%\bin\java  --add-reads org.monarchinitiative.hca.app=ALL-UNNAMED^
 --add-exports=javafx.base/com.sun.javafx.event=org.controlsfx.controls^
 --module-path "lib"^
 --module org.monarchinitiative.hca.app
