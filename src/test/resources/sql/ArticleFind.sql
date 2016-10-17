-- Website
INSERT INTO website(id, name, url, active)
  VALUES (100, 'Mboa Football', 'http://www.mboafootball.com', TRUE);

-- Feeds
INSERT INTO feed VALUES (100, 100, 'rss', 'CMR', 'Mboa Football', 'http://mboafootball.com/rss', TRUE);

-- Articles
INSERT INTO article(id, feed_id, image_id, STATUS, title, slug, country_code, language_code, published_date, url)
  VALUES('d0fc08117a071843564f9f8cb0530af7', 100, null, 0, 'Article #100', 'Slug #100', 'CMR', 'FR', '2013-11-15 12:30', 'http://feed100/100');
