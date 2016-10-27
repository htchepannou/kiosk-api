CREATE INDEX article__published_date__title ON article(published_date, title);

CREATE INDEX article__status__published_date ON article(status, published_date);
