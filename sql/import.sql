CREATE TABLE competition_entity
(
    id                   BIGINT                      NOT NULL,
    title                VARCHAR(255)                NOT NULL,
    subtitle             VARCHAR(255)                NOT NULL,
    overview             VARCHAR,
    slug                 VARCHAR(255)                NOT NULL,
    total_submissions    BIGINT                      NOT NULL,
    deadline_date        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    avg_cells_per_kernel DOUBLE PRECISION            NOT NULL,
    avg_votes            DOUBLE PRECISION            NOT NULL,
    avg_lines_per_kernel DOUBLE PRECISION            NOT NULL,
    transition_matrix    JSONB,
    main_label_stats     JSONB,
    CONSTRAINT pk_competitionentity PRIMARY KEY (id)
);

CREATE TABLE competition_tags
(
    competition_id BIGINT NOT NULL,
    slug           VARCHAR(255)
);

ALTER TABLE competition_tags
    ADD CONSTRAINT fk_competitiontags_on_competition_entity FOREIGN KEY (competition_id) REFERENCES competition_entity (id);

CREATE TABLE cluster_entity
(
    cluster_id            BIGINT NOT NULL,
    summary               TEXT,
    local_cluster_id      BIGINT,
    cluster_size          BIGINT NOT NULL,
    avg_cells_per_kernel  DOUBLE PRECISION NOT NULL,
    avg_votes             DOUBLE PRECISION NOT NULL,
    avg_lines_per_kernel  DOUBLE PRECISION NOT NULL,
    transition_matrix     JSONB,
    main_label_stats      JSONB,
    source_competition_id BIGINT,
    CONSTRAINT pk_clusterentity PRIMARY KEY (cluster_id)
);

ALTER TABLE cluster_entity
    ADD CONSTRAINT FK_CLUSTERENTITY_ON_SOURCECOMPETITIONID FOREIGN KEY (source_competition_id) REFERENCES competition_entity (id);


CREATE TABLE kernel_entity
(
    kernel_version_id     BIGINT                      NOT NULL,
    creation_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    version_number        INTEGER                     NOT NULL,
    title                 VARCHAR(255),
    total_votes           INTEGER                     NOT NULL,
    total_views           INTEGER                     NOT NULL,
    total_comments        INTEGER                     NOT NULL,
    current_url_slug      VARCHAR(255),
    author_user_name      VARCHAR(255),
    author_display_name   VARCHAR(255),
    num_lines             INTEGER                     NOT NULL,
    cell_count            INTEGER                     NOT NULL,
    label_sequence        JSONB,
    transition_matrix     JSONB,
    transition_matrix_norm JSONB,
    main_label_stats      JSONB,
    main_label_stats_norm JSONB,
    complexiti_features_norm JSONB,
    n_grams JSONB,
    source_competition_id BIGINT,
    cluster_id            BIGINT,
    CONSTRAINT pk_kernelentity PRIMARY KEY (kernel_version_id)
);

ALTER TABLE kernel_entity
    ADD CONSTRAINT FK_KERNELENTITY_ON_CLUSTER FOREIGN KEY (cluster_id) REFERENCES cluster_entity (cluster_id);

ALTER TABLE kernel_entity
    ADD CONSTRAINT FK_KERNELENTITY_ON_SOURCECOMPETITIONID FOREIGN KEY (source_competition_id) REFERENCES competition_entity (id);

CREATE TABLE cell_entity
(
    id                BIGINT   NOT NULL,
    cell_id           INTEGER  NOT NULL,
    source            TEXT,
    source_line_count INTEGER  NOT NULL,
    cell_type         SMALLINT NOT NULL,
    main_label        SMALLINT,
    kernel_version_id BIGINT,
    CONSTRAINT pk_cellentity PRIMARY KEY (id)
);

ALTER TABLE cell_entity
    ADD CONSTRAINT FK_CELLENTITY_ON_KERNELVERSIONID FOREIGN KEY (kernel_version_id) REFERENCES kernel_entity (kernel_version_id);

alter table cell_entity
    alter column source type varchar using source::varchar;

\copy competition_entity(id, title, subtitle, overview, slug, total_submissions, deadline_date, main_label_stats, transition_matrix, avg_cells_per_kernel, avg_lines_per_kernel, avg_votes) FROM '/home/tim/IdeaProjects/kaggle-vis/scripts/Competitions_stats_tmp.csv' DELIMITER ',' CSV HEADER
\copy competition_tags(competition_id, slug) FROM '/home/tim/IdeaProjects/kaggle-vis/scripts/CompetitionTags.csv' DELIMITER ',' CSV HEADER
\copy cluster_entity(cluster_id, local_cluster_id, cluster_size, main_label_stats, transition_matrix, source_competition_id, summary, avg_cells_per_kernel, avg_lines_per_kernel, avg_votes) FROM '/home/tim/IdeaProjects/kaggle-vis/scripts/Clusters_analyzed.csv' DELIMITER ',' CSV HEADER
\copy kernel_entity(kernel_version_id, source_competition_id, creation_date, version_number, title, total_votes, total_views, total_comments, current_url_slug, author_user_name, author_display_name, main_label_stats, label_sequence, transition_matrix, num_lines, cell_count, transition_matrix_norm, main_label_stats_norm, complexiti_features_norm, n_grams, cluster_id) FROM '/home/tim/IdeaProjects/kaggle-vis/scripts/AllCompetitionKernels_clustered.csv' DELIMITER ',' CSV HEADER
\copy cell_entity(id, kernel_version_id, cell_id, source, cell_type, main_label, source_line_count) FROM '/media/tim/Data/Thesis/Cells_predicted_tmp.csv' DELIMITER ',' CSV HEADER