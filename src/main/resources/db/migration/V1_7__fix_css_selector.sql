-- journal-du-cameroon
UPDATE website SET title_sanitize_regex = 'Journal Du Cameroun.com:+(.[^:]+)' WHERE id = 1010;

-- cameroon-info.net
UPDATE website SET title_sanitize_regex = '.[^:]+:+(.[^:]+)' WHERE id = 1101;
