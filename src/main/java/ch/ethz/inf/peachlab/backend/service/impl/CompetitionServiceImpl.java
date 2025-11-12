package ch.ethz.inf.peachlab.backend.service.impl;

import ch.ethz.inf.peachlab.backend.dao.csv.CompetitionCsvDao;
import ch.ethz.inf.peachlab.backend.dao.exception.DaoException;
import ch.ethz.inf.peachlab.backend.dao.impl.CompetitonDao;
import ch.ethz.inf.peachlab.backend.service.CompetitionService;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.filter.CompetitionFilter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class CompetitionServiceImpl extends BaseServiceImpl<CompetitionEntity, CompetitionFilter> implements CompetitionService {

    private static final LocalDateTime EARLIEST_DATE = LocalDateTime.of(2020, 1, 1, 0, 0);
    private static final Long MIN_SUBMISSIONS = 500L;

    public CompetitionServiceImpl(CompetitonDao dao) {
        super(dao);
    }

    @Override
    public ServiceResponse<Void> importCompetitionsFromCsv() {
        ServiceResponse<Void> response = new ServiceResponse<>();

        CompetitionCsvDao competitionCsvDao = new CompetitionCsvDao();
        List<CompetitionEntity> competitions = new ArrayList<>();
        try {
            Iterator<CompetitionEntity> it = competitionCsvDao.getAllCompetitions();
            while(it.hasNext()) {
                CompetitionEntity competition = it.next();
                if (competition.getDeadlineDate().isBefore(EARLIEST_DATE)) {
                    continue;
                }
                if (competition.getTotalSubmissions() < MIN_SUBMISSIONS) {
                    continue;
                }
                competition.updateCreateModifyFields("IMPORT");
                competitions.add(competition);
            }
        } catch (DaoException e) {
            getLogger().error("Error while importing competitions from csv", e);
            response.addErrorMessage("service.competition.import.error");
            return response;
        }

        ServiceResponse<Page<CompetitionEntity>> saveResponse = saveAll(competitions);
        if (saveResponse.hasErrorMessages()) {
            saveResponse.getErrorMessages().forEach(response::addErrorMessage);
        }

        return response;
    }
}
