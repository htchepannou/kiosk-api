package io.tchepannou.kiosk.api.config;

import com.google.common.collect.Sets;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

@EnableSwagger2
@Configuration
@ConfigurationProperties("swagger")
public class SwaggerConfiguration {

    private String version;
    private String title;
    private String description;
    private String termsPath;
    private String email;
    private String licenceType;
    private String licencePath;

    @Bean
    public Docket documentation() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(or(regex("/v1/.*?")))
                .build()
                .pathMapping("/")
                .protocols(Sets.newHashSet("http", "https"))
                .apiInfo(apiInfo())
        ;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .contact(email)
                .license(licenceType)
                .licenseUrl(licencePath)
                .version(version)
                .build();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getTermsPath() {
        return termsPath;
    }

    public void setTermsPath(final String termsPath) {
        this.termsPath = termsPath;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getLicenceType() {
        return licenceType;
    }

    public void setLicenceType(final String licenceType) {
        this.licenceType = licenceType;
    }

    public String getLicencePath() {
        return licencePath;
    }

    public void setLicencePath(final String licencePath) {
        this.licencePath = licencePath;
    }
}