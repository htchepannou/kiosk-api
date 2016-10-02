-- website
CREATE TABLE website (
  id                 BIGINT    NOT NULL PRIMARY KEY,
  name               CHAR(100) NOT NULL,
  url                VARCHAR(256),
  article_url_prefix VARCHAR(256),
  article_url_suffix VARCHAR(256),
  title_css_selector VARCHAR(256),
  slug_css_selector  VARCHAR(256),
  active             BOOL
)
  ENGINE = InnoDB;

INSERT INTO website VALUES (1001, 'Mboa Football', 'http://www.mboafootball.com', NULL, NULL, NULL, NULL, FALSE);
INSERT INTO website VALUES (1002, 'Cameroon Post Online', 'http://www.cameroonpostline.com', NULL, NULL, NULL, NULL, TRUE);
INSERT INTO website VALUES (1003, 'Spark Cameroon', 'http://www.sparkcameroun.com/', NULL, NULL, NULL, NULL, TRUE);
INSERT INTO website VALUES (1004, 'Cameroon Post', 'http://www.cameroonpostline.com/', NULL, NULL, NULL, NULL, TRUE);
INSERT INTO website VALUES (1005, 'camer.be', 'http://www.camer.be', NULL, NULL, NULL, NULL, TRUE);
INSERT INTO website VALUES (1006, 'camer24.de', 'http://www.camer24.de', NULL, NULL, NULL, NULL, TRUE);
INSERT INTO website VALUES (1007, 'Culture Ebene', 'http://www.culturebene.com', NULL, NULL, NULL, NULL, TRUE);
INSERT INTO website VALUES (1008, 'Cameroun Sports', 'http://www.camerounsports.info', NULL, NULL, NULL, NULL, TRUE);
INSERT INTO website VALUES (1009, 'camerpost.com', 'http://www.camerpost.com', NULL, NULL, NULL, NULL, TRUE);
INSERT INTO website VALUES (1010, 'Journal Du Cameroun', 'http://www.journalducameroun.com', NULL, NULL, NULL, NULL, TRUE);
INSERT INTO website VALUES (1011, 'newsducamer.com', 'https://www.newsducamer.com', '/index.php', NULL, '.ItemView .itemTitle', NULL, TRUE);
INSERT INTO website VALUES (1012, 'La Nouvelle Expression', 'http://www.lanouvelleexpression.info', NULL, NULL, NULL, NULL, TRUE);
INSERT INTO website VALUES (1013, 'JeWanda-Magazine', 'http://www.jewanda-magazine.com', NULL, NULL, NULL, NULL, TRUE);

INSERT INTO website VALUES (1101, 'cameroon-info.net', 'http://www.cameroon-info.net', '/article', '.html', '.cp-post-content h3', NULL, TRUE);
INSERT INTO website VALUES (1102, 'Camfoot', 'http://www.camfoot.com', NULL, '.html', '.cartouche h1', NULL, TRUE);
INSERT INTO website VALUES (1103, 'Cameroun Tribune', 'http://www.cameroon-tribune.cm', '/articles', NULL, '#article-post h1', NULL, TRUE);
INSERT INTO website VALUES (1104, 'CameroonOnline.org', 'http://www.cameroononline.org', NULL, NULL, '.post-inner .post-title', NULL, TRUE);

-- feeds
CREATE TABLE feed (
  id           BIGINT      NOT NULL PRIMARY KEY,

  website_id   BIGINT      NOT NULL REFERENCES website (id),

  type         CHAR(3)     NOT NULL,
  country_code CHAR(3)     NOT NULL,
  name         VARCHAR(64) NOT NULL,
  url          TEXT        NOT NULL,
  active       BOOL
)
  ENGINE = InnoDB;

INSERT INTO feed VALUES (1001, 1001, 'rss', 'CMR', 'Mboa Football', 'http://www.mboafootball.com/rss', FALSE);
INSERT INTO feed VALUES (1002, 1002, 'rss', 'CMR', 'Cameroon Post Online', 'http://www.cameroonpostline.com/feed', TRUE);
INSERT INTO feed VALUES (1003, 1003, 'rss', 'CMR', 'Spark Cameroon', 'http://www.sparkcameroun.com/feed', TRUE);
INSERT INTO feed VALUES (1004, 1004, 'rss', 'CMR', 'Cameroon Post', 'http://www.cameroonpostline.com/feed', TRUE);
INSERT INTO feed VALUES (1005, 1005, 'rss', 'CMR', 'camer.be', 'http://www.camer.be/rss.php', TRUE);
INSERT INTO feed VALUES (1006, 1006, 'rss', 'CMR', 'camer24.de', 'http://www.camer24.de/feed', TRUE);
INSERT INTO feed VALUES (1007, 1007, 'rss', 'CMR', 'culturebene.com', 'http://www.culturebene.com/feed', TRUE);
INSERT INTO feed VALUES (1008, 1008, 'rss', 'CMR', 'camerounsports.info', 'http://www.camerounsports.info/feed', TRUE);
INSERT INTO feed VALUES (1009, 1009, 'rss', 'CMR', 'camerpost.com', 'http://www.camerpost.com/feed', TRUE);
INSERT INTO feed VALUES (1010, 1010, 'rss', 'CMR', 'Journal Du Cameroun', 'http://www.journalducameroun.com/rss', TRUE);
INSERT INTO feed VALUES (1011, 1011, 'rss', 'CMR', 'CameroonOnline.org', 'http://www.cameroononline.org/feed', TRUE);
INSERT INTO feed VALUES (1012, 1012, 'rss', 'CMR', 'La Nouvelle Expression',
                         'http://www.lanouvelleexpression.info/index.php?option=com_k2&view=itemlist&layout=category&task=category&id=92&Itemid=632&format=feed',
                         TRUE);
INSERT INTO feed VALUES (1013, 1013, 'rss', 'CMR', 'JeWanda-Magazine', 'http://www.jewanda-magazine.com/feed', TRUE);

INSERT INTO feed VALUES (1101, 1101, 'rss', 'CMR', 'cameroon-info.net', 's3://rss/1101.xml', TRUE);
INSERT INTO feed VALUES (1102, 1102, 'rss', 'CMR', 'Camfoot', 's3://rss/1102.xml', TRUE);
INSERT INTO feed VALUES (1103, 1103, 'rss', 'CMR', 'Cameroun Tribune', 's3://rss/1103.xml', TRUE);
INSERT INTO feed VALUES (1104, 1104, 'rss', 'CMR', 'newsducamer.com', 's3://rss/1104.xml', TRUE);


-- articles
CREATE TABLE article (
  id             CHAR(32) NOT NULL PRIMARY KEY,

  feed_id        BIGINT   NOT NULL REFERENCES feed (id),
  image_id       CHAR(32),

  title          VARCHAR(256),
  slug           TEXT,
  country_code   CHAR(3),
  language_code  CHAR(3),
  published_date DATETIME DEFAULT now(),
  status         INT,
  url            TEXT     NOT NULL,
  status_reason  VARCHAR(30),

  insertdatetime DATETIME DEFAULT now()
)
  ENGINE = InnoDB;

-- images
CREATE TABLE image(
  id             CHAR(32) NOT NULL PRIMARY KEY,

  url            TEXT,
  public_url     TEXT         NOT NULL,
  image_key      VARCHAR(100) NOT NULL,
  content_type   VARCHAR(100),
  title          VARCHAR(256),
  width          INT,
  height         INT,

  insertdatetime DATETIME DEFAULT now()
)
  ENGINE = InnoDB;
