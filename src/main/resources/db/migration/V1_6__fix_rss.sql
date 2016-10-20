UPDATE feed SET url = 's3://rss/1005.rss' WHERE id = 1005;
UPDATE website SET title_sanitize_regex = '(.[^:]+):+.[^:]+' WHERE id = 1005;

UPDATE website SET title_sanitize_regex = '.[^:]+:+.[^:]+:(.[^:]+)' WHERE id = 1005;

UPDATE website SET image_css_selector = '.logoarticle img' WHERE id = 1102;

UPDATE website SET image_css_selector = '.itemBody .itemImageBlock img' WHERE id = 1014;
