#!/bin/bash

# This script updates certain fields of XML format that was being used for storing the biocurated data in `HpoCaseAnnotator` software.
# The files compatible with version 1.0.6 are lifted to version 1.0.7

USAGE="\nUSAGE:\n$0 input_dir output_dir\n"

# Check that the first argument is directory
if ! [ -d "$1" ]; then
printf "The first argument must be a directory '$1'\n";
printf "$USAGE"
exit 1;
fi

if ! [ -d "$2" ]; then
printf "The second argument must be a directory '$2'\n";
printf "$USAGE"
exit 1;
fi

for F in $(ls $1/*.xml); do
FNAME=$(basename $F)
# GENOTYPES FIRST, THEN SEX, FINALLY CRYPTIC_SPLICE_SITE_TYPE 
cat $F | sed -e 's/<string>heterozygous</<string>HETEROZYGOUS</' \
-e 's/<string>homozygous</<string>HOMOZYGOUS_ALTERNATE</' \
-e 's/<string>hemizygous</<string>HEMIZYGOUS</' \
-e 's/<string>not_reported</<string>UNDEFINED</' \
-e 's/<string>male</<string>MALE</' \
-e 's/<string>female</<string>FEMALE</' \
-e 's/<string>3 splice site</<string>THREE_PRIME</' \
-e 's/<string>5 splice site</<string>FIVE_PRIME</' \
-e 's/org.monarchinitiative.hrmd_gui.model/org.monarchinitiative.hpo_case_annotator.model.xml_model/' \
> $2/$FNAME;
done

echo "Done!"
exit 0
