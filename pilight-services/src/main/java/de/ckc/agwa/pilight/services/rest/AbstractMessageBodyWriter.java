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

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Creates message bodies for {@link de.ckc.agwa.pilight.services.Family}.
 *
 * @author stuelten
 */
@Provider
@Produces({"text/plain", "application/json"})
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

    private boolean isSupported(Class<?> type, MediaType mediaType) {
        return clazz.isAssignableFrom(type) && isSupportedMediaType(mediaType);
    }

    private boolean isSupportedMediaType(MediaType mediaType) {
        return MediaType.APPLICATION_JSON_TYPE.equals(mediaType)
                || MediaType.TEXT_PLAIN_TYPE.equals(mediaType);
    }

    // ----------------------------------------------------------------------

    /**
     * Ascertain if the MessageBodyReader can produce an instance of a
     * particular type. The {@code type} parameter gives the
     * class of the instance that should be produced, the {@code genericType} parameter
     * gives the {@link Type java.lang.reflect.Type} of the instance
     * that should be produced.
     * E.g. if the instance to be produced is {@code List<String>}, the {@code type} parameter
     * will be {@code java.util.List} and the {@code genericType} parameter will be
     * {@link ParameterizedType java.lang.reflect.ParameterizedType}.
     *
     * @param type        the class of instance to be produced.
     * @param genericType the type of instance to be produced. E.g. if the
     *                    message body is to be converted into a method parameter, this will be
     *                    the formal type of the method parameter as returned by
     *                    {@code Method.getGenericParameterTypes}.
     * @param annotations an array of the annotations on the declaration of the
     *                    artifact that will be initialized with the produced instance. E.g. if the
     *                    message body is to be converted into a method parameter, this will be
     *                    the annotations on that parameter returned by
     *                    {@code Method.getParameterAnnotations}.
     * @param mediaType   the media type of the HTTP entity, if one is not
     *                    specified in the request then {@code application/octet-stream} is
     *                    used.
     * @return {@code true} if the type is supported, otherwise {@code false}.
     */
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isSupported(type, mediaType);
    }

    /**
     * Read a type from the {@link InputStream}.
     * <p>
     * In case the entity input stream is empty, the reader is expected to either return a
     * Java representation of a zero-length entity or throw a {@link NoContentException}
     * in case no zero-length entity representation is defined for the supported Java type.
     * A {@code NoContentException}, if thrown by a message body reader while reading a server
     * request entity, is automatically translated by JAX-RS server runtime into a {@link BadRequestException}
     * wrapping the original {@code NoContentException} and rethrown for a standard processing by
     * the registered {@link ExceptionMapper exception mappers}.
     * </p>
     *
     * @param type         the type that is to be read from the entity stream.
     * @param genericType  the type of instance to be produced. E.g. if the
     *                     message body is to be converted into a method parameter, this will be
     *                     the formal type of the method parameter as returned by
     *                     {@code Method.getGenericParameterTypes}.
     * @param annotations  an array of the annotations on the declaration of the
     *                     artifact that will be initialized with the produced instance. E.g.
     *                     if the message body is to be converted into a method parameter, this
     *                     will be the annotations on that parameter returned by
     *                     {@code Method.getParameterAnnotations}.
     * @param mediaType    the media type of the HTTP entity.
     * @param httpHeaders  the read-only HTTP headers associated with HTTP entity.
     * @param entityStream the {@link InputStream} of the HTTP entity. The
     *                     caller is responsible for ensuring that the input stream ends when the
     *                     entity has been consumed. The implementation should not close the input
     *                     stream.
     * @return the type that was read from the stream. In case the entity input stream is empty, the reader
     * is expected to either return an instance representing a zero-length entity or throw
     * a {@link NoContentException} in case no zero-length entity representation is
     * defined for the supported Java type.
     * @throws IOException             if an IO error arises. In case the entity input stream is empty
     *                                 and the reader is not able to produce a Java representation for
     *                                 a zero-length entity, {@code NoContentException} is expected to
     *                                 be thrown.
     * @throws WebApplicationException if a specific HTTP error response needs to be produced.
     *                                 Only effective if thrown prior to the response being committed.
     */
    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                      MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        T ret;
        ObjectMapper mapper = new ObjectMapper();
        ret = mapper.readValue(entityStream, new TypeReference<T>() {
        });
        return ret;
    }

    // ----------------------------------------------------------------------

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isSupported(type, mediaType);
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
                buffer = objectMapper.writeValueAsString(type);
            } catch (IOException e) {
                LOGGER.info("writeTo(): Ignore '{}'", e);
                buffer = "";
            }
        } else if (MediaType.TEXT_PLAIN_TYPE.equals(mediaType)) {
            buffer = "" + object;
        } else {
            throw new UnsupportedOperationException("Unsupported MediaType: " + mediaType);
        }
        try (PrintStream printStream = new PrintStream(out)) {
            printStream.print(buffer);
        }
    }

}
