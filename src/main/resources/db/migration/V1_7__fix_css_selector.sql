-- journal-du-cameroon
UPDATE website SET title_sanitize_regex = 'Journal Du Cameroun.com:+(.[^:]+)' WHERE id = 1010;

-- cameroon-info.net
UPDATE website SET title_sanitize_regex = '.[^:]+:+(.[^:]+)' WHERE id = 1101;

-- JeWandaMagazine
UPDATE feed SET url = 'http://www.jewanda-magazine.com/feed/' WHERE id = 1013;
UPDATE website SET image_css_selector = NULL WHERE id = 1013;
