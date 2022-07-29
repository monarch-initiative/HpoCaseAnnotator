package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.monarchinitiative.hpo_case_annotator.model.v2.PedigreeMember;
import org.monarchinitiative.hpo_case_annotator.observable.Updateable;

import java.util.Optional;

public class ObservablePedigreeMember extends BaseObservableIndividual<PedigreeMember> implements PedigreeMember, Updateable<PedigreeMember> {

    private final StringProperty paternalId = new SimpleStringProperty(this, "paternalId");
    private final StringProperty maternalId = new SimpleStringProperty(this, "maternalId");
    private final BooleanProperty proband = new SimpleBooleanProperty(this, "proband");

    public ObservablePedigreeMember() {
    }

    public ObservablePedigreeMember(Builder builder) {
        super(builder);
        paternalId.set(builder.parentalId);
        maternalId.set(builder.maternalId);
        proband.set(builder.isProband);
    }

    @Override
    public Optional<String> getPaternalId() {
        return Optional.ofNullable(paternalId.get());
    }

    public void setPaternalId(String paternalId) {
        this.paternalId.set(paternalId);
    }

    public StringProperty paternalIdProperty() {
        return paternalId;
    }

    @Override
    public Optional<String> getMaternalId() {
        return Optional.ofNullable(maternalId.get());
    }

    public void setMaternalId(String maternalId) {
        this.maternalId.set(maternalId);
    }

    public StringProperty maternalIdProperty() {
        return maternalId;
    }

    @Override
    public boolean isProband() {
        return proband.get();
    }

    public void setProband(boolean proband) {
        this.proband.set(proband);
    }

    public BooleanProperty probandProperty() {
        return proband;
    }

    @Override
    public void update(PedigreeMember data) {
        super.update(data);
        if (data == null) {
            setPaternalId(null);
            setMaternalId(null);
            setProband(false);
        } else {
            setPaternalId(data.getPaternalId().orElse(null));
            setMaternalId(data.getMaternalId().orElse(null));
            setProband(data.isProband());
        }
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
