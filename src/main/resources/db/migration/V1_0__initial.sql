-- feeds
CREATE TABLE feed (
  id           BIGINT      NOT NULL PRIMARY KEY,
  type         CHAR(3)     NOT NULL,
  country_code CHAR(3),
  name         VARCHAR(64) NOT NULL,
  url          TEXT        NOT NULL,
  active       BOOL
)
  ENGINE = InnoDB;

INSERT INTO feed VALUES (1001, 'rss', 'CMR', 'Mboa Football', 'http://mboafootball.com/rss', FALSE);
INSERT INTO feed VALUES (1002, 'rss', 'CMR', 'Cameroon Post Online', 'http://www.cameroonpostline.com/feed', TRUE);
INSERT INTO feed VALUES (1003, 'rss', 'CMR', 'Spark Cameroon', 'http://www.sparkcameroun.com/feed', TRUE);
INSERT INTO feed VALUES (1004, 'rss', 'CMR', 'Cameroon Post', 'http://www.cameroonpostline.com/feed', TRUE);
INSERT INTO feed VALUES (1005, 'rss', 'CMR', 'camer.be', 'http://www.camer.be/rss.php', TRUE);
INSERT INTO feed VALUES (1006, 'rss', 'CMR', 'camer24.de', 'http://www.camer24.de/feed', TRUE);
INSERT INTO feed VALUES (1007, 'rss', 'CMR', 'Cameroun Sports', 'http://www.camerounsports.info/feed', TRUE);


-- articles
CREATE TABLE article (
  id             CHAR(32) NOT NULL PRIMARY KEY,
  title          VARCHAR(256),
  slug           TEXT,
  country_code   CHAR(3),
  language_code  CHAR(3),
  published_date DATETIME,
  status         INT,
  url            TEXT     NOT NULL,

  feed_id        BIGINT   NOT NULL REFERENCES feed (id)
)
  ENGINE = InnoDB;
