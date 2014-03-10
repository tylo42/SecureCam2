# Users schema

# --- !Ups

CREATE TABLE Node(
    node_id     INT UNSIGNED    AUTO_INCREMENT,
    hostname    VARCHAR(255)    NOT NULL,
    PRIMARY KEY(node_id)
);

CREATE TABLE Camera(
    camera_id   INT UNSIGNED    NOT NULL,
    port        INT UNSIGNED    NOT NULL,
    description	VARCHAR(255),
    node_id     INT UNSIGNED    NOT NULL,
    PRIMARY KEY(camera_id)
);

ALTER TABLE Camera ADD CONSTRAINT RefNode
    FOREIGN KEY (node_id)
    REFERENCES Node(node_id);

CREATE TABLE Video(
    vid_id       INT UNSIGNED   AUTO_INCREMENT,
    time         INT UNSIGNED   NOT NULL,
    video_name   VARCHAR(255)   NOT NULL,
    picture_name VARCHAR(255)   NOT NULL,
    event        INT UNSIGNED   NOT NULL,
    flagged      boolean        NOT NULL   DEFAULT 0,
    camera_id    INT UNSIGNED   NOT NULL,
    PRIMARY KEY(vid_id)
);

CREATE INDEX RefCam ON camera(camera_id);

ALTER TABLE Video ADD CONSTRAINT RefCam
    FOREIGN KEY (camera_id)
    REFERENCES camera(camera_id);

CREATE TABLE User (
    username varchar(255) NOT NULL,
    password varchar(64) NOT NULL,
    salt varchar(64) NOT NULL,
    PRIMARY KEY(username)
);

# --- !Downs

DROP TABLE User;
DROP TABLE Video;
DROP TABLE Camera;
DROP TABLE Node;