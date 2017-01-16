package io.github.waka.sevenhack.data.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(name = "description")
public final class Description {

    @Attribute(name="type",required=false)
    public String contentType;

    @Text(required = false)
    public String description;
}
