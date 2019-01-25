package com.github.wromijn.halo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Accessors;

import java.net.URL;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Accessors(chain = true)
@NoArgsConstructor
public class Link {
    private @NonNull String href;
    private Boolean templated;
    private String type;
    private URL deprecation;
    private String name;
    private URL profile;
    private String title;
    private String hreflang;

    public Link(String href) {
        this();
        setHref(href);
    }

    public Link setHref(String href) {
        this.href = href;
        if (href.contains("{")) {
            templated = true;
        } else if (templated != null && templated) {
            templated = null;
        }
        return this;
    }
}
