-- website
CREATE TABLE website (
  id                 BIGINT  NOT NULL PRIMARY KEY,
  name               CHAR(100) NOT NULL,
  url                VARCHAR(256),
  article_url_prefix VARCHAR(256),
  article_url_suffix VARCHAR(256),
  title_css_selector VARCHAR(256),
  slug_css_selector  VARCHAR(256),
  active             BOOL
)
  ENGINE = InnoDB;

INSERT INTO website VALUES (1001, 'Mboa Football', 'http://mboafootball.com', NULL, NULL, NULL, NULL, FALSE);
INSERT INTO website VALUES (1002, 'Cameroon Post Online', 'http://www.cameroonpostline.com', NULL, NULL, NULL, NULL, TRUE);
INSERT INTO website VALUES (1003, 'Spark Cameroon', 'http://www.sparkcameroun.com/', NULL, NULL, NULL, NULL, TRUE);
INSERT INTO website VALUES (1004, 'Cameroon Post', 'http://www.cameroonpostline.com/', NULL, NULL, NULL, NULL, TRUE);
INSERT INTO website VALUES (1005, 'camer.be', 'http://www.camer.be/rss.php', NULL, NULL, NULL, NULL, TRUE);
INSERT INTO website VALUES (1006, 'camer24.de', 'http://www.camer24.de', NULL, NULL, NULL, NULL, TRUE);
INSERT INTO website VALUES (1007, 'Culture Ebene', 'http://www.culturebene.com', NULL, NULL, NULL, NULL, TRUE);
INSERT INTO website VALUES (1008, 'Cameroun Sports', 'http://www.camerounsports.info', NULL, NULL, NULL, NULL, TRUE);

INSERT INTO website VALUES (1101, 'cameroon-info.net', 'http://cameroon-info.net', '/article', '.html', '.cp-post-content h3', NULL, TRUE);
INSERT INTO website VALUES (1102, 'Camfoot', 'http://camfoot.com', NULL, '.html', NULL, NULL, TRUE);
INSERT INTO website VALUES (1103, 'La Nouvelle Expression', 'http://www.lanouvelleexpression.info', NULL, NULL, NULL, NULL, TRUE);
INSERT INTO website VALUES (1104, 'Cameroun Tribune', 'http://www.cameroon-tribune.cm', NULL, NULL, '.article-post h1', NULL, TRUE);
