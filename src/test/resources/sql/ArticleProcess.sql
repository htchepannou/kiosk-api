-- Website
INSERT INTO website VALUES (100, 'Mboa Football', 'http://www.mboafootball.com', NULL, NULL, NULL, NULL, FALSE);

-- Feeds
INSERT INTO feed VALUES (100, 100, 'rss', 'CMR', 'Mboa Football', 'http://mboafootball.com/rss', TRUE);
INSERT INTO feed VALUES (200, 100, 'rss', 'CMR', 'Cameroon Post Online', 'http://www.cameroonpostline.com/feed/', TRUE);

-- Articles
INSERT INTO article(id, feed_id, STATUS, title, slug, country_code, language_code, published_date, url)
VALUES('100', 100, 0, 'Article #100', 'Slug #100', 'CMR', 'FR', '2013-11-15 12:30', 'http://feed100/100');

INSERT INTO article(id, feed_id, STATUS, title, slug, country_code, language_code, published_date, url)
VALUES('101', 100, 1, 'Article #101', 'Slug #101', 'CMR', 'FR', '2013-11-15 13:30', 'http://feed100/101');

INSERT INTO article(id, feed_id, STATUS, title, slug, country_code, language_code, published_date, url)
VALUES('200', 200, 0, 'Article #200', 'Slug #200', 'CMR', 'FR', '2013-11-15 15:30', 'http://feed200/200');
