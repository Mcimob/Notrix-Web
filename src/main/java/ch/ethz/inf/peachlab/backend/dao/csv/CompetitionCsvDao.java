package ch.ethz.inf.peachlab.backend.dao.csv;

import ch.ethz.inf.peachlab.app.AppConfiguration;
import ch.ethz.inf.peachlab.app.SpringContext;
import ch.ethz.inf.peachlab.backend.dao.exception.DaoException;
import ch.ethz.inf.peachlab.logger.HasLogger;
import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

/**
 * Streaming-safe DAO for reading large competition CSV files.
 */
public class CompetitionCsvDao implements Closeable, HasLogger {

    private static final Logger log = LoggerFactory.getLogger(CompetitionCsvDao.class);

    private final AppConfiguration config;
    private Reader reader;
    private CsvToBean<CompetitionEntity> csvToBean;
    private Iterator<CompetitionEntity> iterator;

    public CompetitionCsvDao() {
        this.config = SpringContext.getBean(AppConfiguration.class);
    }

    /**
     * Returns an iterator over competitions, keeping the stream open until closed.
     */
    public Iterator<CompetitionEntity> getAllCompetitions() throws DaoException {
        try {
            // Open the reader once and keep it open for iteration
            this.reader = new FileReader(config.getCsvLocation() + "/Competitions.csv");

            this.csvToBean = new CsvToBeanBuilder<CompetitionEntity>(reader)
                    .withType(CompetitionEntity.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withThrowExceptions(false) // continue parsing on bad rows
                    .build();

            this.iterator = csvToBean.iterator();
            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    boolean hasNext = iterator.hasNext();
                    if (!hasNext) {
                        closeQuietly();
                    }
                    return hasNext;
                }

                @Override
                public CompetitionEntity next() {
                    return iterator.next();
                }
            };
        } catch (Exception e) {
            closeQuietly();
            throw new DaoException("Error parsing CSV", e);
        }
    }

    @Override
    public void close() throws IOException {
        closeQuietly();
    }

    private void closeQuietly() {
        if (reader != null) {
            try {
                reader.close();
                getLogger().debug("Closed CSV reader for {}", config.getCsvLocation() + "/Competitions.csv");
            } catch (IOException e) {
                getLogger().warn("Error closing CSV reader", e);
            } finally {
                reader = null;
                iterator = null;
                csvToBean = null;
            }
        }
    }
}
