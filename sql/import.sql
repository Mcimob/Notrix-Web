CREATE TABLE competition_entity
(
    id                BIGINT                      NOT NULL,
    title             VARCHAR(255)                NOT NULL,
    subtitle          VARCHAR(255)                NOT NULL,
    overview          VARCHAR,
    slug              VARCHAR(255)                NOT NULL,
    total_submissions BIGINT                      NOT NULL,
    deadline_date     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_competitionentity PRIMARY KEY (id)
);

CREATE TABLE cluster_entity
(
    id             BIGINT NOT NULL,
    description    VARCHAR(255),
    competition_id BIGINT,
    CONSTRAINT pk_clusterentity PRIMARY KEY (id)
);

ALTER TABLE cluster_entity
    ADD CONSTRAINT FK_CLUSTERENTITY_ON_COMPETITIONID FOREIGN KEY (competition_id) REFERENCES competition_entity (id);

CREATE TABLE kernel_entity
(
    kernel_version_id     BIGINT                      NOT NULL,
    creation_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    version_number        INTEGER                     NOT NULL,
    title                 VARCHAR(255)                NOT NULL,
    total_votes           INTEGER                     NOT NULL,
    total_views           INTEGER                     NOT NULL,
    total_comments        INTEGER                     NOT NULL,
    current_url_slug      VARCHAR(255),
    author_user_name      VARCHAR(255),
    author_display_name   VARCHAR(255),
    source_competition_id BIGINT,
    cluster_id            BIGINT,
    CONSTRAINT pk_kernelentity PRIMARY KEY (kernel_version_id)
);

ALTER TABLE kernel_entity
    ADD CONSTRAINT FK_KERNELENTITY_ON_CLUSTER FOREIGN KEY (cluster_id) REFERENCES cluster_entity (id);

ALTER TABLE kernel_entity
    ADD CONSTRAINT FK_KERNELENTITY_ON_SOURCECOMPETITIONID FOREIGN KEY (source_competition_id) REFERENCES competition_entity (id);

CREATE TABLE cell_entity
(
    id                BIGINT       NOT NULL,
    cell_id           INTEGER      NOT NULL,
    source            VARCHAR,
    cell_type         VARCHAR(255) NOT NULL,
    main_label        INTEGER,
    kernel_version_id BIGINT,
    CONSTRAINT pk_cellentity PRIMARY KEY (id)
);

ALTER TABLE cell_entity
    ADD CONSTRAINT FK_CELLENTITY_ON_KERNELVERSIONID FOREIGN KEY (kernel_version_id) REFERENCES kernel_entity (kernel_version_id);

ALTER TABLE cell_entity
    ALTER COLUMN source TYPE VARCHAR(255) USING (source::VARCHAR(255));

\copy competition_entity(id, title, subtitle, slug, total_submissions, deadline_date) FROM '/home/tim/IdeaProjects/kaggle-vis/scripts/Competitions.csv' DELIMITER ',' CSV HEADER
\copy kernel_entity(kernel_version_id, source_competition_id, creation_date, version_number, title, total_votes, total_views, total_comments, current_url_slug, author_user_name, author_display_name) FROM '/home/tim/IdeaProjects/kaggle-vis/scripts/AllCompetitionKernels.csv' DELIMITER ',' CSV HEADER
\copy cell_entity(id, kernel_version_id, cell_id, source, cell_type, main_label) FROM '/media/tim/Data/Thesis/Cells_predicted.csv' DELIMITER ',' CSV HEADER