package com.cvent.dropwizard.mybatis.exceptionMappers;

import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper to trap unhandled SQL exceptions and prevent the query text from leaking into response messages
 */
public class PersistenceExceptionMapper implements ExceptionMapper<PersistenceException> {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceExceptionMapper.class);

    private static final Map<String, String> RESPONSE_MESSAGE = new HashMap<>();

    static {
        RESPONSE_MESSAGE.put("message", "Internal error");
    }

    @Override
    public Response toResponse(PersistenceException exception) {
        LOG.error("Unhandled SQL Exception", exception);

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(RESPONSE_MESSAGE)
                .build();
    }
}
