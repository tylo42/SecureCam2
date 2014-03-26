# Users schema

# --- !Ups

CREATE TABLE Node(
    id       INT UNSIGNED    AUTO_INCREMENT,
    hostname VARCHAR(255)    NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE Camera(
    id          INT UNSIGNED    NOT NULL,
    port        INT UNSIGNED    NOT NULL,
    description	VARCHAR(255),
    node_id     INT UNSIGNED    NOT NULL,
    PRIMARY KEY(id)
);

ALTER TABLE Camera ADD CONSTRAINT RefNode
    FOREIGN KEY (node_id)
    REFERENCES Node(id);

CREATE TABLE Video(
    id           INT UNSIGNED   AUTO_INCREMENT,
    time         INT UNSIGNED   NOT NULL,
    video        VARCHAR(255)   NOT NULL,
    picture      VARCHAR(255)   NOT NULL,
    flagged      boolean        NOT NULL   DEFAULT 0,
    camera_id    INT UNSIGNED   NOT NULL,
    PRIMARY KEY(id)
);

CREATE INDEX RefCam ON camera(id);

ALTER TABLE Video ADD CONSTRAINT RefCam
    FOREIGN KEY (camera_id)
    REFERENCES camera(id);

CREATE TABLE User (
    username VARCHAR(32)  NOT NULL,
    password VARCHAR(64)  NOT NULL,
    salt     VARCHAR(64)  NOT NULL,
    role_id  INT UNSIGNED NOT NULL,
    PRIMARY KEY(username)
);

CREATE TABLE Role (
    id   INT UNSIGNED AUTO_INCREMENT,
    name VARCHAR(8),
    PRIMARY KEY(id)
);

ALTER TABLE User ADD CONSTRAINT RefRole
    FOREIGN KEY (role_id)
    REFERENCES Role(id);

INSERT INTO Role(name) VALUES('view');
INSERT INTO Role(name) VALUES('admin');
INSERT INTO Role(name) VALUES('super');

# --- !Downs

DROP TABLE User;
DROP TABLE Video;
DROP TABLE Camera;
DROP TABLE Node;
DROP TABLE Role;