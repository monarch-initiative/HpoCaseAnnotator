package org.monarchinitiative.hpo_case_annotator.db;


import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

public class DiseaseCaseDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiseaseCaseDao.class);

    private final DataSource dataSource;

    public DiseaseCaseDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Collection<String> getDiseaseCaseIds() {
        return Collections.emptyList();
    }

    public int insertDiseaseCase(DiseaseCase dc) {
        String publicationSql = "";
        String diseaseSql = "";

        int updated = 0;

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement publicationPs = connection.prepareStatement(publicationSql);
                 PreparedStatement diseasePs = connection.prepareStatement(diseaseSql)) {

                /*

                transcripts.setString(1, transcript.getContig());
                transcripts.setInt(2, transcript.getTxBegin());
                transcripts.setInt(3, transcript.getTxEnd());
                transcripts.setInt(4, coordinates.getBegin());
                transcripts.setInt(5, coordinates.getEnd());
                transcripts.setBoolean(6, transcript.getStrand());
                transcripts.setString(7, transcript.getAccessionId());
                updatedTx += transcripts.executeUpdate();

                for (SplicingExon exon : transcript.getExons()) {
                    exons.setString(1, transcript.getAccessionId());
                    exons.setInt(2, exon.getBegin());
                    exons.setInt(3, exon.getEnd());
                    updatedExons += exons.executeUpdate();
                }

                for (SplicingIntron intron : transcript.getIntrons()) {
                    introns.setString(1, transcript.getAccessionId());
                    introns.setInt(2, intron.getBegin());
                    introns.setInt(3, intron.getEnd());
                    introns.setDouble(4, intron.getDonorScore());
                    introns.setDouble(5, intron.getAcceptorScore());
                    updatedIntrons += introns.executeUpdate();
                }
                */

                connection.commit();
            } catch (SQLException e) {
                LOGGER.warn("Error occured during update, rolling back", e);
                connection.rollback();
            }
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            LOGGER.warn("Error occurred", e);
        }

        return updated;
    }

}
