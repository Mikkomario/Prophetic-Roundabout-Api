--
-- The base database structure for this project
-- Mikko Hilpinen
--

CREATE DATABASE prophetic_roundabout_db
    DEFAULT CHARACTER SET utf8
    DEFAULT COLLATE utf8_general_ci;
USE prophetic_roundabout_db;


-- INSERT CITADEL, EXODUS AND AMBASSADOR DB STRUCTURE HERE


-- ROUNDABOUT EXODUS EXTENSIONS ----------------------------------

-- Task 7: Hosting a meeting
-- Task 8: Share Zoom or Google account with organization
INSERT INTO task (id) VALUES (7), (8);

-- User Role 3: Host
INSERT INTO organization_user_role (id) VALUES (3);

-- Owners and hosts are allowed to host meetings
-- Owners and stewards are allowed to share their 3rd party accounts
INSERT INTO user_role_right (role_id, task_id) VALUES
    (1, 7), (3, 7),
    (1, 8), (2, 8);


-- ROUNDABOUT AMBASSADOR EXTENSION  ------------------------------

-- Services: 1 = Zoom, 2 = Google
INSERT INTO oauth_service (id, name) VALUES (1, 'Zoom'), (2, 'Google');

-- NB: Please set up the client id and client secret correctly when inserting settings to the database
-- They are not listed here because this document is available publicly in GitHub
INSERT INTO oauth_service_settings
    (service_id, client_id, client_secret, authentication_url, token_url, redirect_url) VALUES
    (1, '???', '???', 'https://zoom.us/oauth/authorize', 'https://zoom.us/oauth/token',
        'http://localhost:9999/roundabout/api/v1/services/zoom/auth/response'),
    (2, '???', '???', 'https://accounts.google.com/o/oauth2/auth', 'https://oauth2.googleapis.com/token',
        'http://localhost:9999/roundabout/api/v1/services/google/auth/response');

-- Scopes: 1 & 2 for Zoom (meeting:read & meeting:write), 3 for Google (send email)
INSERT INTO scope (id, service_id, service_side_name, client_side_name, priority) VALUES
    (1, 1, 'meeting:read', 'Read meeting information', 3),
    (2, 1, 'meeting:write', 'Schedule and edit meetings', 2),
    (3, 2, 'https://www.googleapis.com/auth/gmail.send', 'Send mail', 1);

-- All of the listed scopes are needed in meeting scheduling / hosting
INSERT INTO task_scope (task_id, scope_id) VALUES
    (7, 1), (7, 2), (7, 3);


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

-- Lists cases where users have shared one of their 3rd party service accounts with the whole organization (leadership)
-- E.g. When one gmail account is used for sending scheduled meeting invitations
CREATE TABLE shared_oauth(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    account_owner_id INT NOT NULL,
    shared_service_id INT NOT NULL,
    organization_id INT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deprecated_after DATETIME,

    INDEX so_deprecation_idx (deprecated_after),

    CONSTRAINT so_u_sharer_ref_fk FOREIGN KEY so_u_sharer_ref_idx (account_owner_id)
        REFERENCES `user`(id) ON DELETE CASCADE,
    CONSTRAINT so_os_shared_service_ref_fk FOREIGN KEY so_os_shared_service_ref_idx (shared_service_id)
        REFERENCES oauth_service(id) ON DELETE CASCADE,
    CONSTRAINT so_o_organization_ref_fk FOREIGN KEY so_o_organization_ref_idx (organization_id)
        REFERENCES organization(id) ON DELETE CASCADE

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