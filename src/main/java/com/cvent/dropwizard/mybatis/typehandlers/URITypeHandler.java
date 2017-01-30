package com.cvent.dropwizard.mybatis.typehandlers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Function;

import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * URI type handler
 */
@MappedTypes(URI.class)
public class URITypeHandler extends AbstractTypeHandler<URI> {

    private static final Logger LOG = LoggerFactory.getLogger(URITypeHandler.class);

    public URITypeHandler() {
        this(URITypeHandler::toURI, null);
    }

    protected URITypeHandler(Function<String, URI> format, URI nullValue) {
        super(format, nullValue);
    }

    private static URI toURI(String value) {
        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            LOG.error("URI transformation fail for: " + value, e);
        }
        return null;
    }

}
