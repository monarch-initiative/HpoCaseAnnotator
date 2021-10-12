package org.monarchinitiative.hpo_case_annotator.core.utils;

public class BreakendAltComposer {

    private BreakendAltComposer() {
        // private no-op
    }

    public static String composeBreakendAltAllele(String contig, String pos, String ref, boolean leftPositive, boolean rightPositive, String inserted) {
        // with breakends, we refer to Section 5.4 | Figure 2 in VCF 4.3
        if (leftPositive) {
            // left breakend is on POSITIVE strand
            return rightPositive
                    // U-V breakends
                    ? String.format("%s%s[%s:%s[", ref, inserted, contig, pos)
                    // W-Y breakends
                    : String.format("%s%s]%s:%s]", ref, inserted, contig, pos);
        } else {
            // left breakend is on NEGATIVE strand
            return rightPositive
                    ? String.format("[%s:%s[%s%s", contig, pos, inserted, ref)
                    : String.format("]%s:%s]%s%s", contig, pos, inserted, ref);
        }
    }
}
