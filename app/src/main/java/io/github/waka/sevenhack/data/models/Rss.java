package io.github.waka.sevenhack.data.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2005/Atom", prefix = "atom"),
        @Namespace(reference = "http://www.itunes.com/dtds/podcast-1.0.dtd", prefix = "itunes"),
        @Namespace(reference = "http://search.yahoo.com/mrss/", prefix = "media"),
        @Namespace(reference = "http://purl.org/dc/elements/1.1/", prefix = "dc")
})
@Root(name = "rss", strict = false)
public final class Rss {

    @Element(name = "channel")
    public Channel channel;
}
