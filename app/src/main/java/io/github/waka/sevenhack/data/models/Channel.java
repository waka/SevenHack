package io.github.waka.sevenhack.data.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "channel", strict = false)
public final class Channel {

    @Element(name = "title")
    public String title;

    @ElementList(name = "description", inline = true, required = false)
    public List<Description> descriptions;

    @Namespace(reference = "http://www.itunes.com/dtds/podcast-1.0.dtd")
    @Element(name = "summary", required = false)
    public String summary;

    @Namespace(reference = "http://www.itunes.com/dtds/podcast-1.0.dtd")
    @Element(name = "author", required = false)
    public String author;

    @ElementList(name = "item", inline = true)
    public List<Item> items;

    public String getDescription() {
        return (summary != null) ? summary : descriptions.get(0).description;
    }
}
