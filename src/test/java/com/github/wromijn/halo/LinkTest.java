package com.github.wromijn.halo;

import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class LinkTest {

    private SchemaUtils schemaUtils = new SchemaUtils();

    @Test
    public void testItRecognisesTemplatedLinks() {
        Link templatedLink = new Link().setHref("/link/{id}");
        assertThat(templatedLink.getTemplated(), is(true));
        Link normalLink = new Link().setHref("/link");
        assertThat(normalLink.getTemplated(), is(nullValue()));
    }

    @Test
    public void testItDoesNotOverwriteTheTemplatedFlagWhenItIsExplicitlySetToFalse() {
        Link templatedLink = new Link().setTemplated(true).setHref("/link");
        assertThat(templatedLink.getTemplated(), is(nullValue()));
        Link normalLink = new Link().setTemplated(false).setHref("/link");
        assertThat(normalLink.getTemplated(), is(false));
    }

    @Test
    public void testConstructor() {
        Link link = new Link("/link");
        assertThat(link.getHref(), is("/link"));
    }

    @Test
    public void testMinimalSerialization() {
        Link link = new Link("/link");
        schemaUtils.assertSerializationMatchesSchema(link, "/schemas/link_test/minimal_link.json");
    }

    @Test
    public void testFullSerialization() throws Exception {
        Link link = new Link()
                .setHref("/link")
                .setTemplated(false)
                .setDeprecation(new URL("http://www.example.com/deprecation"))
                .setHreflang("en")
                .setName("name_value")
                .setProfile(new URL("http://www.example.com/profile"))
                .setTitle("title_value")
                .setType("type_value");
        schemaUtils.assertSerializationMatchesSchema(link, "/schemas/link_test/full_link.json");
    }
}
