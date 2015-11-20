/*
 * Copyright (c) 2015 Timo Stülten <timo@stuelten.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.ckc.agwa.pilight.services.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * An abstract implementation for JAX-RS {@link MessageBodyReader} and {@link MessageBodyWriter}.
 * <p>
 * Sub-classes must extend this abstract class, add a default constructor calling
 * {@link #AbstractMessageBodyWriter(Class)} with the pojo's class as argument.
 * Additionally the sub-class must have the annotations
 * {@code @javax.ws.rs.Provider} and
 * {@code @javax.ws.rs.ext.Produces({"text/plain", "application/json"})}.
 * </p>
 *
 * @param <T> the type (of the pojo) the real converter converts
 * @author Timo Stülten
 */
public abstract class AbstractMessageBodyWriter<T>
        implements MessageBodyReader<T>, MessageBodyWriter<T> {
    /**
     * The {@link Logger}
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMessageBodyWriter.class);

    /**
     * The real class.
     */
    protected Class<T> clazz;

    // ----------------------------------------------------------------------

    protected AbstractMessageBodyWriter(Class<T> clazz) {
        Objects.requireNonNull(clazz);
        this.clazz = clazz;
    }

    // ----------------------------------------------------------------------

    private boolean isSupportedClass(Class<?> type, MediaType mediaType) {
        return clazz.isAssignableFrom(type);
    }

    // ----------------------------------------------------------------------
    // Reader
    // ----------------------------------------------------------------------

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isSupportedClass(type, mediaType) && MediaType.APPLICATION_JSON_TYPE.equals(mediaType);
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                      MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        T ret;
        if (MediaType.APPLICATION_JSON_TYPE.equals(mediaType)) {
            ObjectMapper mapper = new ObjectMapper();
            ret = mapper.readValue(entityStream, new TypeReference() {
            });
        } else {
            LOGGER.warn("Unsupported MediaType: '{}'", mediaType);
            throw new UnsupportedOperationException("Unsupported MediaType: " + mediaType);
        }
        return ret;
    }

    // ----------------------------------------------------------------------
    // Writer
    // ----------------------------------------------------------------------

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isSupportedClass(type, mediaType)
                && (MediaType.APPLICATION_JSON_TYPE.equals(mediaType) || MediaType.TEXT_PLAIN_TYPE.equals(mediaType));
    }

    @Override
    public long getSize(T object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(T object, Class<?> clazz, Type type, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> mm, OutputStream out) throws IOException, WebApplicationException {
        String buffer;
        if (MediaType.APPLICATION_JSON_TYPE.equals(mediaType)) {
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                buffer = objectMapper.writeValueAsString(object);
            } catch (IOException e) {
                LOGGER.info("writeTo(): Ignore '{}'", e);
                buffer = "";
            }
        } else if (MediaType.TEXT_PLAIN_TYPE.equals(mediaType)) {
            buffer = "" + object;
        } else {
            LOGGER.warn("Unsupported MediaType: '{}'", mediaType);
            throw new UnsupportedOperationException("Unsupported MediaType: " + mediaType);
        }
        try (PrintStream printStream = new PrintStream(out)) {
            printStream.print(buffer);
        }
    }

}
