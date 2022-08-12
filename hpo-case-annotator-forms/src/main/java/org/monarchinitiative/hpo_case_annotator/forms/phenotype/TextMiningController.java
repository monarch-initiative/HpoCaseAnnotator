package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("prototype")
public class TextMiningController {

    @FXML
    private TabPane contentTabPane;
    @FXML
    private Tab submitTab;
    @FXML
    private Tab reviewTab;
    @FXML
    private TextField textField;

    @FXML
    private void initialize() {

    }

}
