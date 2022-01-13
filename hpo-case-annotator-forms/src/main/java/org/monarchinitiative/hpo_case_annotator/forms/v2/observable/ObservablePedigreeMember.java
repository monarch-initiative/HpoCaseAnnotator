package org.monarchinitiative.hpo_case_annotator.forms.v2.observable;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;

public class ObservablePedigreeMember extends BaseObservableIndividual {

    private final StringProperty paternalId = new SimpleStringProperty(this, "paternalId");
    private final StringProperty maternalId = new SimpleStringProperty(this, "maternalId");
    private final BooleanProperty proband = new SimpleBooleanProperty(this, "isProband");

    public ObservablePedigreeMember() {
    }


    public ObservablePedigreeMember(Builder builder) {
        super(builder);
        paternalId.set(builder.parentalId);
        maternalId.set(builder.maternalId);
        proband.set(builder.isProband);

    }

    public String getPaternalId() {
        return paternalId.get();
    }

    public void setPaternalId(String paternalId) {
        this.paternalId.set(paternalId);
    }

    public StringProperty paternalIdProperty() {
        return paternalId;
    }

    public String getMaternalId() {
        return maternalId.get();
    }

    public void setMaternalId(String maternalId) {
        this.maternalId.set(maternalId);
    }

    public StringProperty maternalIdProperty() {
        return maternalId;
    }

    public boolean isProband() {
        return proband.get();
    }

    public void setProband(boolean proband) {
        this.proband.set(proband);
    }

    public BooleanProperty probandProperty() {
        return proband;
    }

    public ObservableStringValue probandCheckMark() {
        return Bindings.createStringBinding(() -> proband.get() ? FormUtils.checkMark() : "", proband);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends BaseObservableIndividual.Builder<Builder> {

        private String parentalId;
        private String maternalId;
        private boolean isProband;

        protected Builder() {
            super();
        }

        public Builder setParentalId(String parentalId) {
            this.parentalId = parentalId;
            return self();
        }

        public Builder setMaternalId(String maternalId) {
            this.maternalId = maternalId;
            return self();
        }

        public Builder setProband(boolean proband) {
            isProband = proband;
            return self();
        }

        @Override
        protected Builder self() {
            return this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public ObservablePedigreeMember build() {
            return new ObservablePedigreeMember(this);
        }
    }

}
