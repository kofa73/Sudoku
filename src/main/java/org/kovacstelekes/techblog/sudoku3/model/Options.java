package org.kovacstelekes.techblog.sudoku3.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @param <V> the value type (Integer for the value of a cell, Cell for the position of a value in a Container)
*/
public class Options<V> {
    private final Set<Options<V>> relatedOptions = new HashSet<>();

    private final String name;
    private Set<V> possibleValues = new HashSet<>();
    private V solution = null;

    public Options(String name) {
        this.name = name;
    }

    public Options(String name, Set<V> possibleValues) {
        this.name = name;
        setPossibleValues(possibleValues);
    }

    public void setPossibleValues(Set<V> possibleValues) {
        if (solution != null) {
            throw new IllegalStateException(
                    this + " - solution = " + solution + " already found, cannot accept possibleValues=" + possibleValues
            );
        }
        this.possibleValues = new HashSet<>(possibleValues);
    }

    public void addPossibleValue(V possibleValue) {
        if (solution != null) {
            throw new IllegalStateException(
                    this + " - solution = " + solution + " already found, cannot accept possibleValue=" + possibleValue
            );
        }
        this.possibleValues.add(possibleValue);
    }

    public void addRelated(Collection<? extends Options<V>> group) {
        relatedOptions.addAll(group);
        relatedOptions.remove(this);
    }

    public boolean solution(V value) {
        boolean updated;
        if (!possibleValues.contains(value)) {
            throw new IllegalStateException(this + " cannot take value " + value + ". Possible values: " + possibleValues);
        } else {
            updated = (solution == null);
            if (updated) {
                solution = value;
//                if (!Board.disableDiags) {
//                    System.out.println("Solved " + this);
//                }
                possibleValues = Set.of(value);
                relatedOptions.forEach(related -> related.excludeValue(value));
            }
        }
        return updated;
    }

    public boolean excludeValue(V value) {
        boolean updated;
        if (!isSolved()) {
            updated = possibleValues.remove(value);
            if (possibleValues.size() == 1 && updated) {
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

    public Optional<V> solution() {
        return Optional.ofNullable(solution);
    }

    public boolean isSolved() {
        return solution != null;
    }

    public int numberOfOptions() {
        return possibleValues.size();
    }

    public Set<V> possibleValues() {
        return possibleValues;
    }
}
