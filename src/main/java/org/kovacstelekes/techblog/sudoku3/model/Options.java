package org.kovacstelekes.techblog.sudoku3.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class Options<E extends Options<E>> {
    private static final Collection<Integer> ALL_VALUES = IntStream.range(1, 10).boxed().collect(toList());
    private final Set<E> relatedOptions = new HashSet<>();

    private final String name;
    private Set<E> possibleValues = new HashSet<>();
    private E solution = null;

    public Options(String name) {
        this.name = name;
    }

    public void addRelated(Collection<E> group) {
        relatedOptions.addAll(group);
        relatedOptions.remove(this);
    }

    public boolean solution(E value) {
        boolean updated;
        if (!possibleValues.contains(value)) {
            throw new IllegalStateException(this + " cannot take value " + value + ". Possible values: " + possibleValues);
        } else {
            updated = (solution != null);
            if (updated) {
                solution = value;
                possibleValues = Set.of(value);
                relatedOptions.forEach(related -> related.excludeValue(value));
            }
        }
        return updated;
    }

    public boolean excludeValue(E value) {
        boolean updated;
        if (solution == null) {
            updated = possibleValues.remove(value);
            if (possibleValues.size() == 1) {
                solution(possibleValues.iterator().next());
            }
        } else if (solution == value) {
            throw new IllegalStateException(this + " has already been solved; cannot exclude solution = " + solution);
        } else {
            updated = false;
        }
        return updated;
    }

    @Override
    public String toString() {
        return name + possibleValues;
    }

    public Optional<E> solution() {
        return Optional.ofNullable(solution);
    }
}
