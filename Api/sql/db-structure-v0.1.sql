--
-- The base database structure for this project
-- Mikko Hilpinen
--

CREATE DATABASE prophetic_roundabout_db
    DEFAULT CHARACTER SET utf8
    DEFAULT COLLATE utf8_general_ci;
USE prophetic_roundabout_db;


-- INSERT CITADEL AND EXODUS DB STRUCTURE HERE


-- ROUNDABOUT EXODUS EXTENSIONS ----------------------------------

-- Task 7 / Description 17: Hosting a meeting
INSERT INTO task (id) VALUES (7);
INSERT INTO description (id, role_id, language_id, `text`) VALUES (17, 1, 1, 'Host a meeting');
INSERT INTO task_description (task_id, description_id) VALUES (7, 17);

-- User Role 3 / Description 18: Host
INSERT INTO organization_user_role (id) VALUES (3);
INSERT INTO description (id, role_id, language_id, `text`) VALUES (18, 1, 1, 'Host');
INSERT INTO user_role_description (role_id, description_id) VALUES (3, 18);

-- Owners and hosts are allowed to host meetings
INSERT INTO user_role_right (role_id, task_id) VALUES (1, 7), (3, 7);


-- PROPHETIC ROUNDABOUT TABLES  ----------------------------------

-- Server side settings store
CREATE TABLE setting(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    category VARCHAR(24) NOT NULL,
    field VARCHAR(32) NOT NULL,
    json_value VARCHAR(96),
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE INDEX s_key_idx (category, field)

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

INSERT INTO setting (category, field, json_value) VALUES
    ('api', 'address', '"http://localhost:9999/roundabout/"'),
    ('api', 'root-path', '"roundabout"'),
    ('zoom', 'auth-uri', '"https://zoom.us/oauth/authorize"'),
    ('zoom', 'token-uri', '"https://zoom.us/oauth/token"'),
    ('zoom', 'redirect-uri', '"http://localhost:9999/roundabout/api/v1/zoom/login/response"'),
    ('zoom', 'client-id', NULL),
    ('zoom', 'client-secret', NULL),
    ('zoom', 'auth-result-page-uri', '"http://localhost:8080/zoom-auth-result"'),
    ('zoom', 'authentication-timeout-hours', 22),
    ('zoom', 'max-user-wait-seconds', 8),
    ('zoom', 'api-base-uri', '"https://api.zoom.us/v2/"');

-- Logs server side errors
-- Severity levels are as follows:
-- 0: Debug - Debugging logs that are purely informative and don't indicate a problem
-- 1: Warning - Warnings that may indicate a problem but are often not necessary to act upon
-- 2: Problem - A problem in the software that can be recovered from but which should be fixed when possible
-- 3: Error - A problem in the software that renders a portion of the service unavailable (high priority fix)
-- 4: Critical Failure - A problem in the software that prevents the use thereof and must be fixed ASAP
-- The problem table contains error categories
CREATE TABLE problem(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    context VARCHAR(96) NOT NULL,
    severity INT NOT NULL DEFAULT 2,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE INDEX p_location_idx (severity, context)

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
-- The problem_occurrence table contains problem events
CREATE TABLE problem_occurrence(
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    problem_id INT NOT NULL,
    message VARCHAR(255),
    stack TEXT,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX po_time_idx (created),

    CONSTRAINT po_problem_ref_fk FOREIGN KEY po_problem_ref_idx (problem_id)
        REFERENCES problem(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Used for logging incoming requests and their responses
CREATE TABLE request(
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    method VARCHAR(5) NOT NULL DEFAULT 'GET',
    path VARCHAR(128),
    status_code INT NOT NULL DEFAULT 200,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    duration_milli_seconds INT NOT NULL DEFAULT 0,

    INDEX r_creation_idx (created)

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Used for storing Zoom authorization attempts coming from the client
-- (will be matched when/if receiving a code from the Zoom auth)
CREATE TABLE zoom_authentication_attempt(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    token VARCHAR(36) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed DATETIME,

    INDEX zaa_auth_idx (created, closed, token),

    CONSTRAINT zaa_u_attempt_owner_ref_fk FOREIGN KEY zaa_u_attempt_owner_ref_fk (user_id)
        REFERENCES `user`(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Used for storing refresh tokens for Zoom users for request authentication
-- Refresh tokens are used for acquiring session tokens
-- Scope values are separated by :
CREATE TABLE zoom_refresh_token(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    token VARCHAR(700) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expiration DATE NOT NULL,
    deprecated_after DATETIME,

    INDEX zrt_expiration_idx (deprecated_after, expiration),

    CONSTRAINT zrt_u_owner_link_fk FOREIGN KEY zrt_u_owner_link_idx (user_id)
        REFERENCES `user`(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
-- Used for authenticating requests to Zoom
CREATE TABLE zoom_session_token(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    refresh_token_id INT NOT NULL,
    token VARCHAR(700) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expiration DATETIME NOT NULL,

    INDEX zat_expiration_idx (expiration),

    CONSTRAINT zst_master_token_ref_fk FOREIGN KEY zst_master_token_ref_idx (refresh_token_id)
        REFERENCES zoom_refresh_token(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Contains more information about a user
CREATE TABLE user_roundabout_settings(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    time_zone_id VARCHAR(64),
    owns_pro_zoom_account BOOLEAN NOT NULL DEFAULT FALSE,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deprecated_after DATETIME,

    INDEX urs_deprecation_idx (deprecated_after),

    CONSTRAINT urs_u_settings_owner_ref_fk FOREIGN KEY urs_u_settings_owner_ref_idx (user_id)
        REFERENCES `user`(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Lists scheduled Roundabout meetings
-- Join url is for participants
CREATE TABLE meeting(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    zoom_id BIGINT NOT NULL,
    zoom_uuid VARCHAR(128) NOT NULL,
    host_id INT NOT NULL,
    host_organization_id INT NOT NULL,
    name VARCHAR(96) NOT NULL,
    start_time DATETIME NOT NULL,
    planned_duration_minutes INT NOT NULL,
    password VARCHAR(10) NOT NULL,
    join_url VARCHAR(255) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX m_start_idx (start_time),

    CONSTRAINT m_u_meeting_host_ref_fk FOREIGN KEY m_u_meeting_host_ref_idx (host_id)
        REFERENCES `user`(id) ON DELETE CASCADE,
    CONSTRAINT m_o_parent_organization_ref_fk FOREIGN KEY m_o_parent_organization_ref_idx (host_organization_id)
        REFERENCES organization(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Contains meeting start urls. These are only for the meeting hosts and expire after 2 hours.
CREATE TABLE meeting_start_url(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    meeting_id INT NOT NULL,
    url VARCHAR(999) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expiration DATETIME NOT NULL,

    INDEX msu_expiration_idx (expiration),

    CONSTRAINT msu_m_meeting_ref_fk FOREIGN KEY msu_m_meeting_ref_idx (meeting_id)
        REFERENCES meeting(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;