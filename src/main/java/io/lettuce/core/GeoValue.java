/*
 * Copyright 2011-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.lettuce.core;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

import io.lettuce.core.internal.LettuceAssert;

/**
 * A Geo value extension to {@link Value}.
 *
 * @param <V> Value type.
 * @author Mark Paluch
 * @since 6.1
 */
@SuppressWarnings("serial")
public class GeoValue<V> extends Value<V> {

    private static final GeoValue<Object> EMPTY = new GeoValue<>(new GeoCoordinates(0, 0), null);

    private final GeoCoordinates coordinates;

    /**
     * Serializable constructor.
     */
    protected GeoValue() {
        super(null);
        this.coordinates = null;
    }

    private GeoValue(GeoCoordinates coordinates, V value) {
        super(value);
        this.coordinates = coordinates;
    }

    /**
     * Returns an empty {@code GeoValue} instance. No value is present for this instance.
     *
     * @param <V>
     * @return the {@link GeoValue}
     */
    @SuppressWarnings("unchecked")
    public static <V> GeoValue<V> empty() {
        return (GeoValue<V>) EMPTY;
    }

    /**
     * Creates a {@link GeoValue} from a {@code key} and {@code value}. The resulting value contains the value.
     *
     * @param longitude the longitude coordinate according to WGS84.
     * @param latitude the latitude coordinate according to WGS84.
     * @param value the value. Must not be {@code null}.
     * @param <T>
     * @param <V>
     * @return the {@link GeoValue}
     */
    public static <T extends V, V> GeoValue<V> just(double longitude, double latitude, T value) {
        return new GeoValue<>(new GeoCoordinates(longitude, latitude), value);
    }

    /**
     * Creates a {@link GeoValue} from a {@code key} and {@code value}. The resulting value contains the value.
     *
     * @param coordinates the coordinates.
     * @param value the value. Must not be {@code null}.
     * @param <T>
     * @param <V>
     * @return the {@link GeoValue}
     */
    public static <T extends V, V> GeoValue<V> just(GeoCoordinates coordinates, T value) {

        LettuceAssert.notNull(coordinates, "GeoCoordinates must not be null");

        return new GeoValue<>(coordinates, value);
    }

    public GeoCoordinates getCoordinates() {
        return coordinates;
    }

    /**
     * @return the longitude if this instance has a {@link #hasValue()}.
     * @throws NoSuchElementException if the value is not present.
     */
    public double getLongitude() {

        if (coordinates == null) {
            throw new NoSuchElementException();
        }

        return coordinates.getX().doubleValue();
    }

    /**
     * @return the latitude if this instance has a {@link #hasValue()}.
     * @throws NoSuchElementException if the value is not present.
     */
    public double getLatitude() {

        if (coordinates == null) {
            throw new NoSuchElementException();
        }

        return coordinates.getY().doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GeoValue)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        GeoValue<?> geoValue = (GeoValue<?>) o;
        return Objects.equals(coordinates, geoValue.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), coordinates);
    }

    @Override
    public String toString() {
        return hasValue() ? String.format("GeoValue[%s, %s]", coordinates, getValue())
                : String.format("GeoValue[%s].empty", coordinates);
    }

    /**
     * Returns a {@link GeoValue} consisting of the results of applying the given function to the value of this element. Mapping
     * is performed only if a {@link #hasValue() value is present}.
     *
     * @param <R> The element type of the new stream
     * @param mapper a stateless function to apply to each element
     * @return the new {@link GeoValue}
     */
    @SuppressWarnings("unchecked")
    public <R> GeoValue<R> map(Function<? super V, ? extends R> mapper) {

        LettuceAssert.notNull(mapper, "Mapper function must not be null");

        if (hasValue()) {
            return new GeoValue<>(coordinates, mapper.apply(getValue()));
        }

        return (GeoValue<R>) this;
    }

    /**
     * Returns a {@link GeoValue} consisting of the results of applying the given function to the {@link GeoCoordinates} of this
     * element. Mapping is performed only if a {@link #hasValue() value is present}.
     *
     * @param mapper a stateless function to apply to each element
     * @return the new {@link GeoValue}
     */
    public GeoValue<V> mapCoordinates(Function<? super GeoCoordinates, ? extends GeoCoordinates> mapper) {

        LettuceAssert.notNull(mapper, "Mapper function must not be null");

        if (hasValue()) {
            return new GeoValue<>(mapper.apply(coordinates), getValue());
        }

        return this;
    }

}
