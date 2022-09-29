package org.monarchinitiative.hpo_case_annotator.forms.mining;

public enum ReviewStatus {
        UNREVIEWED,
        APPROVED,
        REJECTED;

        public boolean isUnreviewed() {
                return this.equals(UNREVIEWED);
        }

        public boolean isApproved() {
                return this.equals(APPROVED);
        }

        public boolean isRejected() {
                return this.equals(REJECTED);
        }
}
