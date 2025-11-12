package ch.ethz.inf.peachlab.backend.service;

import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.filter.CompetitionFilter;

public interface CompetitionService extends BaseService<CompetitionEntity, CompetitionFilter> {

    ServiceResponse<Void> importCompetitionsFromCsv();
}
