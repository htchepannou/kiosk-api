-- feeds
CREATE TABLE feed (
  id           BIGINT      NOT NULL PRIMARY KEY,
  type         CHAR(3)     NOT NULL,
  country_code CHAR(3),
  name         VARCHAR(64) NOT NULL,
  url          TEXT        NOT NULL
)
  ENGINE = InnoDB;

INSERT INTO feed VALUES (1001, 'rss', 'CMR', 'Mboa Football', 'http://mboafootball.com/rss');
INSERT INTO feed VALUES (1002, 'rss', 'CMR', 'Cameroon Post Online', 'http://www.cameroonpostline.com/feed/');
INSERT INTO feed VALUES (1003, 'rss', 'CMR', 'Spark Cameroon', 'http://www.sparkcameroun.com/feed/');
INSERT INTO feed VALUES (1004, 'rss', 'CMR', 'Cameroon Post', 'http://www.cameroonpostline.com/feed/');
INSERT INTO feed VALUES (1005, 'rss', 'CMR', 'camer.be', 'http://www.camer.be/rss.php');
INSERT INTO feed VALUES (1006, 'rss', 'CMR', 'camer24.de', 'http://www.camer24.de/feed/');


-- articles
CREATE TABLE article (
  keyhash         CHAR(32) NOT NULL PRIMARY KEY,
  title           VARCHAR(256),
  slug            TEXT,
  country_code    CHAR(3),
  language_code   CHAR(3),
  published_date  DATETIME,
  status          INT,
  url             TEXT     NOT NULL,

  inserttimestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updatetimestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  feed_id         BIGINT   NOT NULL REFERENCES feed (id)
)
  ENGINE = InnoDB;
