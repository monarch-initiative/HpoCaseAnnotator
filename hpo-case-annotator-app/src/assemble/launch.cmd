@ECHO OFF

:: ================================================================================================================== ::
::              Launch HpoCaseAnnotator app by double-clicking on the batch script in the file browser.               ::
:: ================================================================================================================== ::

%JAVA_HOME%\bin\java  --add-reads org.monarchinitiative.hca.app=ALL-UNNAMED^
 --add-exports=javafx.base/com.sun.javafx.event=org.controlsfx.controls^
 --add-opens javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED^
 --add-opens javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED^
 --add-opens javafx.graphics/com.sun.javafx.util=ALL-UNNAMED^
 --add-opens javafx.base/com.sun.javafx.logging=ALL-UNNAMED^
 --module-path "lib"^
 --module org.monarchinitiative.hca.app
