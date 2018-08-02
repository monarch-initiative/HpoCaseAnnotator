package org.monarchinitiative.hpo_case_annotator.controller;

import com.genestalker.springscreen.core.DialogController;
import com.genestalker.springscreen.core.FXMLDialog;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.monarchinitiative.hpo_case_annotator.gui.OptionalResources;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * This class is the controller for dialog that presents values of resources (values of {@link OptionalResources}
 * properties) to the user in a table. Individual resources are presented as rows where resource name is in the first
 * column and resource value is contained in the second column.<p>The content of table is read-only.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.2
 * @since 0.0
 */
@Deprecated
public final class ShowResourcesController implements DialogController {

//    private final HRMDResourceManager hrmdResourceManager;

    private FXMLDialog dialog;

    @FXML
    private TableView<Map.Entry> contentTableView;

    @FXML
    private TableColumn<Map.Entry, String> nameTableColumn;

    @FXML
    private TableColumn<Map.Entry, String> valueTableColumn;


//    public ShowResourcesController(HRMDResourceManager hrmdResourceManager) {
//        this.hrmdResourceManager = hrmdResourceManager;
//    }

    @Override
    public void setDialog(FXMLDialog dialog) {
        this.dialog = dialog;
    }


    /**
     * Set content of table, define Cell value factories. {@inheritDoc}
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getKey().toString()));
        valueTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getValue().toString()));

//        OptionalResources optionalResources = hrmdResourceManager.getResources();
//        contentTableView.getItems().addAll(optionalResources.getResourceMap().entrySet());
        // TODO - render resources in html
    }


    /* Meant to be used in testing only */
    TableView<Map.Entry> getContentTableView() {
        return this.contentTableView;
    }
}
