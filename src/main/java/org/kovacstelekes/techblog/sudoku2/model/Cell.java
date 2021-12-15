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
    private static final Collection<Integer> ALL_VALUES = IntStream.range(1, 10).boxed().toList();
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

    public IterationOutcome solution(Integer value) {
//        System.out.println("Found solution for " + name + ": " + value);
        return setValue(value);
    }

    private IterationOutcome setValue(Integer value) {
        Outcome outcome;
        boolean progressed = false;

        if (values.size() == 0 || (values.size() == 1 && !values.iterator().next().equals(value))) {
//            System.out.println(this + " cannot store value " + value);
            outcome = Outcome.INVALID;
        } else {
            outcome = Outcome.UNIQUE_SOLUTION;
            if (values.size() != 1) {
                values.clear();
                values.add(value);
                progressed = true;
            }
        }
        return new IterationOutcome(progressed, outcome);
    }

    public int numberOfOptions() {
        return values.size();
    }

    public Outcome outcome() {
        return switch (values.size()) {
            case 1 -> Outcome.UNIQUE_SOLUTION;
            case 0 -> Outcome.INVALID;
            default -> Outcome.SEVERAL_POSSIBILITIES;
        };

    }

    public Optional<Integer> solution() {
        return outcome() == Outcome.UNIQUE_SOLUTION ? Optional.of(values.iterator().next()) : empty();
    }

    public IterationOutcome removeValue(Integer solutionOfAnotherCell) {
        boolean previouslyHadValue = values.remove(solutionOfAnotherCell);
        // FIXME: if this is removed, detectImpossible goes crazy; with this present, valid cases fail
        if (values.isEmpty()) {
            // throw new IllegalStateException();
            return new IterationOutcome(false, Outcome.INVALID);
        }
        if (previouslyHadValue && outcome() == Outcome.UNIQUE_SOLUTION) {
//            System.out.println("The only possible value for " + name + " is " + solution().get());
        }
        return new IterationOutcome(previouslyHadValue, outcome());
    }

    @Override
    public String toString() {
        return "Cell{" +
                "name='" + name + '\'' +
                ", values=" + values +
                '}';
    }

    public void setKnownValue(int value) {
        if (setValue(value).outcome() == Outcome.INVALID) {
            throw new IllegalStateException(this + " cannot take value " + value);
        }
    }

    public int ordinal() {
        return ordinal;
    }
}
