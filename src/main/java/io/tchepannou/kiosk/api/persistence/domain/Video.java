package io.tchepannou.kiosk.api.persistence.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "link_fk")
    private Link link;

    @Column(name = "embed_url", length = 100)
    private String embedUrl;

    public String getEmbedUrl() {
        return embedUrl;
    }

    public void setEmbedUrl(final String embedUrl) {
        this.embedUrl = embedUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(final Link link) {
        this.link = link;
    }
}
