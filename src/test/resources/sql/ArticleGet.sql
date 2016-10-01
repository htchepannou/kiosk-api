-- Website
INSERT INTO website VALUES (100, 'Mboa Football', 'http://www.mboafootball.com', NULL, NULL, NULL, NULL, FALSE);

-- Feeds
INSERT INTO feed VALUES (100, 100, 'rss', 'CMR', 'Mboa Football', 'http://mboafootball.com/rss', TRUE);

-- Articles
INSERT INTO article(id, feed_id, STATUS, title, slug, country_code, language_code, published_date, url)
  VALUES('100', 100, 0, 'Article #100', 'Slug #100', 'CMR', 'FR', '2013-11-15 12:30', 'http://feed100/100');

INSERT INTO article(id, feed_id, STATUS, title, slug, country_code, language_code, published_date, url)
VALUES('101', 100, 1, 'Article #101', 'Slug #101', 'CMR', 'FR', '2013-11-15 13:30', 'http://feed100/101');
