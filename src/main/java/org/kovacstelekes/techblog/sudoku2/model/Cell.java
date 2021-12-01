package org.kovacstelekes.techblog.sudoku2.model;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;

import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;

public class Cell {
    private static final Collection<Integer> ALL_VALUES = IntStream.range(1, 10).boxed().collect(toList());
    private final String name;
    private final int ordinal;
    private final Set<Integer> values = new TreeSet<>(ALL_VALUES);

    public Cell(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }

    public Set<Integer> values() {
        return ImmutableSet.copyOf(values);
    }

    public void solution(Integer value) {
        System.out.println("Found solution for " + name + ": " + value);
        setValue(value);
    }

    private void setValue(Integer value) {
        values.clear();
        values.add(value);
    }

    public int numberOfOptions() {
        return values.size();
    }

    public boolean isSolved() {
        return values.size() == 1;
    }

    public Optional<Integer> solution() {
        return isSolved() ? Optional.of(values.iterator().next()) : empty();
    }

    public boolean removeValue(Integer solutionOfAnotherCell) {
        boolean previouslyHadValue = values.remove(solutionOfAnotherCell);
        // FIXME: if this is removed, detectImpossible goes crazy; with this present, valid cases fail
        if (values.isEmpty()) {
            throw new RuntimeException("No values remained in " + this);
        }
        if (previouslyHadValue && isSolved()) {
            System.out.println("The only possible value for " + name + " is " + solution().get());
        }
        return previouslyHadValue;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "name='" + name + '\'' +
                ", values=" + values +
                '}';
    }

    public void setKnownValue(int value) {
        setValue(value);
    }

    public int ordinal() {
        return ordinal;
    }
}
