UPDATE website SET title_sanitize_regex = NULL;
UPDATE website SET title_sanitize_regex = 'Journal Du Cameroun.com:+(.[^:]+)' WHERE id = 1010;
