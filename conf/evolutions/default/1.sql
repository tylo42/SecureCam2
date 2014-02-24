# Users schema

# --- !Ups

CREATE TABLE Camera(
    camera_id   INT UNSIGNED    NOT NULL,
    hostname    VARCHAR(255)    NOT NULL,
    port        INT UNSIGNED    NOT NULL,
    description	VARCHAR(255),
    PRIMARY KEY(camera_id)
);

CREATE TABLE Video(
    vid_id       INT UNSIGNED   AUTO_INCREMENT,
    time         INT UNSIGNED   NOT NULL,
    video_name   VARCHAR(255)   NOT NULL,
    picture_name VARCHAR(255)   NOT NULL,
    event        INT UNSIGNED   NOT NULL,
    camera_id    INT UNSIGNED   NOT NULL,
    flagged      boolean        NOT NULL   DEFAULT 0,
    PRIMARY KEY(vid_id)
);

CREATE INDEX RefCam ON camera(camera_id);

ALTER TABLE video ADD CONSTRAINT RefCam
    FOREIGN KEY (camera_id)
    REFERENCES camera(camera_id);

CREATE TABLE User (
    user_id  INT UNSIGNED NOT NULL AUTO_INCREMENT,
    username varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    PRIMARY KEY(user_id)
);

# --- !Downs

DROP TABLE User;
DROP TABLE Video;
DROP TABLE Camera;