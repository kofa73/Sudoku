package org.kovacstelekes.techblog.sudoku2.model.model;

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
    private final Set<Integer> values = new TreeSet<>(ALL_VALUES);

    public Cell(String name) {
        this.name = name;
    }

    Set<Integer> values() {
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

    public boolean isSolved() {
        return values.size() == 1;
    }

    public Optional<Integer> solution() {
        return isSolved() ? Optional.of(values.iterator().next()) : empty();
    }

    public void removeValue(Integer solutionOfAnotherCell) {
        boolean previouslyHadValue = values.remove(solutionOfAnotherCell);
        if (previouslyHadValue && isSolved()) {
            System.out.println("The only possible value for " + name + " is " + solution().get());
        }
    }

    @Override
    public String toString() {
        return "Cell{" +
                "name='" + name + '\'' +
                ", values=" + values +
                '}';
    }

    public void setKnownValue(int value) {
        System.out.println("Initialised known value " + name + ": " + value);
        setValue(value);
    }
}
