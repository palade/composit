/*
 * Copyright 2013 Centro de Investigación en Tecnoloxías da Información (CITIUS),
 * University of Santiago de Compostela (USC) http://citius.usc.es.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.usc.citius.composit.core.matcher.graph;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import es.usc.citius.composit.core.matcher.MatchTable;

import java.util.Map;
import java.util.Set;

/**
 * @author Pablo Rodríguez Mier <<a href="mailto:pablo.rodriguez.mier@usc.es">pablo.rodriguez.mier@usc.es</a>>
 */
public abstract class AbstractMatchGraph<E, T extends Comparable<T>> implements MatchGraph<E, T> {

    protected Map<E, T> filter(final Map<E, T> map, final T type, final TypeSelector selector) {
        // First get all from matchTable and then filter using the selector
        return Maps.filterEntries(map, new Predicate<Map.Entry<E, T>>() {
            @Override
            public boolean apply(Map.Entry<E, T> input) {
                switch (selector) {
                    case AT_LEAST:
                        // The match type is at least as good as the provided one.
                        // Example: Match at least subsumes: accepts exact, plugin and subsumes
                        return input.getValue().compareTo(type) <= 0;
                    case AT_MOST:
                        return input.getValue().compareTo(type) >= 0;
                    default:
                        return input.getValue().equals(type);
                }
            }
        });
    }

    @Override
    public Map<E, T> getTargetElementsMatchedBy(final E source, final T type, final TypeSelector selector) {
        // First get all from matchTable and then filter using the selector
        return filter(getTargetElementsMatchedBy(source), type, selector);
    }

    @Override
    public Map<E, T> getSourceElementsThatMatch(final E target, final T type, final TypeSelector selector) {
        return filter(getSourceElementsThatMatch(target), type, selector);
    }

    @Override
    public MatchTable<E, T> partialMatch(Set<E> source, Set<E> target) {
        MatchTable<E,T> matchResult = new MatchTable<E, T>();
        for(E y : target){
            Map<E, T> x = getSourceElementsThatMatch(y);
            for(E ex : x.keySet()){
                if (source.contains(ex)){
                    matchResult.addMatch(ex, y, x.get(ex));
                    break;
                }
            }
        }
        return matchResult;
    }

    @Override
    public MatchTable<E, T> fullMatch(Set<E> source, Set<E> target) {
        MatchTable<E,T> matchResult = new MatchTable<E, T>();
        for(E x : source){
            Map<E, T> y = getTargetElementsMatchedBy(x);
            for(E ey : y.keySet()){
                if (target.contains(ey)){
                    matchResult.addMatch(x, ey, y.get(ey));
                }
            }
        }
        return matchResult;
    }

    @Override
    public T match(E source, E target) {
        return getTargetElementsMatchedBy(source).get(target);
    }

}
