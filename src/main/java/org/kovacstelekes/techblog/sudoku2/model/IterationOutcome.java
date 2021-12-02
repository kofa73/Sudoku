package org.kovacstelekes.techblog.sudoku2.model;

import java.util.function.Supplier;

public record IterationOutcome(boolean progressed, Outcome outcome) {
    static IterationOutcome reduce(IterationOutcome one, IterationOutcome other) {
        Outcome combinedOutcome;
        if (one.outcome == Outcome.INVALID || other.outcome == Outcome.INVALID) {
            combinedOutcome = Outcome.INVALID;
        } else if (one.outcome == Outcome.UNIQUE_SOLUTION && other.outcome == Outcome.UNIQUE_SOLUTION) {
            combinedOutcome = Outcome.UNIQUE_SOLUTION;
        } else {
            combinedOutcome = Outcome.SEVERAL_POSSIBILITIES;
        }

        boolean combinedProgressed = one.progressed || other.progressed;

        return new IterationOutcome(combinedProgressed, combinedOutcome);
    }

    public IterationOutcome or(Supplier<IterationOutcome> call) {
        IterationOutcome combined = this;
        if (outcome == Outcome.SEVERAL_POSSIBILITIES) {
            IterationOutcome otherOutcome = call.get();
            combined = new IterationOutcome(this.progressed || otherOutcome.progressed, otherOutcome.outcome);
        }
        return combined;
    }

    boolean needsAnotherIteration() {
        return progressed && outcome == Outcome.SEVERAL_POSSIBILITIES;
    }
}
