# Users schema

# --- !Ups

CREATE TABLE Node(
    id       INT UNSIGNED    AUTO_INCREMENT,
    hostname VARCHAR(255)    NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE Camera(
    id          INT UNSIGNED    AUTO_INCREMENT,
    port        INT UNSIGNED    NOT NULL,
    device      VARCHAR(255)    NOT NULL,
    description	VARCHAR(255),
    nodeId     INT UNSIGNED    NOT NULL,
    PRIMARY KEY(id)
);

ALTER TABLE Camera ADD CONSTRAINT RefNode
    FOREIGN KEY (nodeId)
    REFERENCES Node(id) ON DELETE CASCADE;

CREATE TABLE Video(
    id           INT UNSIGNED   AUTO_INCREMENT,
    time         INT UNSIGNED   NOT NULL,
    video        VARCHAR(255)   NOT NULL,
    picture      VARCHAR(255),
    flagged      boolean        NOT NULL   DEFAULT 0,
    event        INT UNSIGNED   NOT NULL,
    cameraId    INT UNSIGNED   NOT NULL,
    PRIMARY KEY(id)
);

CREATE INDEX RefCam ON camera(id);

ALTER TABLE Video ADD CONSTRAINT RefCam
    FOREIGN KEY (cameraId)
    REFERENCES camera(id) ON DELETE CASCADE;

CREATE TABLE User (
    id       INT UNSIGNED AUTO_INCREMENT,
    username VARCHAR(32)  NOT NULL,
    password VARCHAR(64)  NOT NULL,
    salt     VARCHAR(64)  NOT NULL,
    roleId  INT UNSIGNED NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE Role (
    id   INT UNSIGNED AUTO_INCREMENT,
    name VARCHAR(8),
    PRIMARY KEY(id)
);

ALTER TABLE User ADD CONSTRAINT RefRole
    FOREIGN KEY (roleId)
    REFERENCES Role(id);

INSERT INTO Role(name) VALUES('view');
INSERT INTO Role(name) VALUES('admin');
INSERT INTO Role(name) VALUES('super');

INSERT INTO Node(hostname) VALUES('localhost')

# --- !Downs

DROP TABLE User;
DROP TABLE Video;
DROP TABLE Camera;
DROP TABLE Node;
DROP TABLE Role;