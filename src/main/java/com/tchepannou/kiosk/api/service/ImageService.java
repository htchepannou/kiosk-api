package com.tchepannou.kiosk.api.service;

import com.google.common.base.Strings;
import com.tchepannou.kiosk.api.domain.Image;
import com.tchepannou.kiosk.core.service.FileService;
import com.tchepannou.kiosk.core.service.HttpService;
import org.apache.tika.Tika;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ImageService {
    @Autowired
    Tika tika;

    @Autowired
    FileService fileService;

    @Autowired
    HttpService httpService;

    @Value("${kiosk.image.public.baseUrl}")
    String publicBaseUrl;

    public List<Image> extractImages(final String html, final String baseUrl) {
        final Document doc = parseHtml(html, baseUrl);
        final Elements elts = doc.body().select("img");

        return elts.stream()
                .map(elt -> toImage(elt))
                .filter(img -> img != null)
                .collect(Collectors.toList());
    }

    public Image download(final Image img) throws IOException {
        String key = null;

        if (img.getUrl() != null) {
            key = doDownload(img);
        } else if (img.getBase64Content() != null) {
            key = doStoreBase64Content(img);
        }

        if (key != null){
            img.setKey(key);
            img.setPublicUrl(publicUrl(img));
            getDimension(img);

            return img;
        }
        return null;
    }

    private String doDownload(final Image img) throws IOException {
        final String url = img.getUrl();
        final String keyPrefix = keyPrefix(img);
        final String key = httpService.get(url, keyPrefix, fileService);
        final String contentType = tika.detect(key);

        if (contentType != null && contentType.startsWith("image/")) {
            img.setContentType(contentType);
        }

        return key;
    }

    private String doStoreBase64Content(final Image img) throws IOException {
        final byte[] bytes = DatatypeConverter.parseBase64Binary(img.getBase64Content());
        try (final ByteArrayInputStream in = new ByteArrayInputStream(bytes)){
            final BufferedImage image = ImageIO.read(in);

            try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                ImageIO.write(image, "png", out);

                final String key = keyPrefix(img) + ".png";
                fileService.put(key, new ByteArrayInputStream(out.toByteArray()));

                return key;
            }
        }
    }

    private String keyPrefix(final Image img) {
        return "/images/" + img.getId() + "/0";
    }

    private String publicUrl(final Image img) {
        return publicBaseUrl + "/" + img.getId();
    }

    private Image toImage(final Element elt) {
        String src = elt.attr("src");
        if (Strings.isNullOrEmpty(src)) {
            return null;
        }

        final Image img = new Image();
        if (src.startsWith("data:image/")) {

            final int i = src.indexOf(';');
            final int j = src.indexOf(',');
            img.setContentType(src.substring("data:".length(), i));
            img.setBase64Content(src.substring(j + 1));

        } else {

            src = elt.attr("abs:src");

            img.setUrl(src);
            img.setContentType(tika.detect(src));

        }

        img.setId(Image.generateId(src));
        img.setTitle(elt.attr("alt"));

        return img;
    }

    private Document parseHtml(final String html, final String baseUrl) {
        final Document doc = Jsoup.parse(html);
        doc.setBaseUri(baseUrl);
        return doc;
    }

    private void getDimension(final Image image) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        fileService.get(image.getKey(), out);

        final BufferedImage bimg = ImageIO.read(new ByteArrayInputStream(out.toByteArray()));
        if (bimg != null) {
            image.setWidth(bimg.getWidth());
            image.setHeight(bimg.getHeight());
        } else {
            image.setWidth(0);
            image.setHeight(0);
        }
    }

}
