package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "ObservablePedigreeMember{" +
                "paternalId=" + paternalId.get() +
                ", maternalId=" + maternalId.get() +
                ", proband=" + proband.get() +
                "} " + super.toString();
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