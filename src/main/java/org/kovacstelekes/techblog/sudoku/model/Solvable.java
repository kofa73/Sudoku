package org.kovacstelekes.techblog.sudoku.model;

import org.assertj.core.util.Preconditions;

import java.util.*;

import static java.util.Optional.empty;
import static org.assertj.core.util.Preconditions.checkArgument;
import static org.assertj.core.util.Preconditions.checkNotNull;

class Solvable<O> {
    private final Set<O> validOptions = new HashSet<>();
    private O solution;
    private final String name;

    Solvable(String name) {
        this.name = name;
    }


    void add(O option) {
        checkArgument(
                !validOptions.contains(option),
                "%s is already listed as an option",
                option
        );
        validOptions.add(option);
    }

    void addAll(Iterable<O> options) {
        for (O option : options) {
            add(option);
        }
    }

    /**
     * Excludes one of the options. If only one option remains, it's marked as the solution and is returned.
     * Otherwise, if already solved or more options remain, an empty value is returned.
     * @param option an option to exclude; must not match the already established solution, if one exists
     * @return the newly found solution, is this call led to its discovery
     */
    Optional<O> exclude(O option) {
        checkNotNull(option, "option must not be null");
        checkArgument(!option.equals(solution), "Contradiction: trying to exclude already established solution: " + solution);
        Optional<O> foundSolution = empty();
        if (solution == null) {
            validOptions.remove(option);
            System.out.println("Removed " + option + " from " + this);
            if (validOptions.size() == 1) {
                solution(validOptions.iterator().next());
                foundSolution = Optional.of(solution);
            }
        }
        return foundSolution;
    }

    Optional<O> solution() {
        return Optional.ofNullable(solution);
    }

    public void solution(O solution) {
        checkNotNull(solution, "solution must not be null");
        if (this.solution != null && !solution.equals(this.solution)) {
            throw new IllegalArgumentException(this + " - cannot override already established solution " + this.solution + " with " + solution);
        }
        validOptions.clear();
        this.solution = solution;
    }

    public int numberOfRemainingPossibilities() {
        return validOptions.size();
    }

    public Collection<O> remainingPossibilities() {
        return Collections.unmodifiableCollection(validOptions);
    }

    @Override
    public String toString() {
        return "Solvable{" +
                "name='" + name + '\'' +
                '}';
    }
}
