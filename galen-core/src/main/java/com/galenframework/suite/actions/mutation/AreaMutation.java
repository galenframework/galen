package com.galenframework.suite.actions.mutation;

import com.galenframework.page.Rect;

import java.util.List;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class AreaMutation {
    private String mutationName;
    private Function<Rect, Rect> mutationFunction;

    public AreaMutation(String mutationName, Function<Rect, Rect> mutationFunction) {
        this.mutationName = mutationName;
        this.mutationFunction = mutationFunction;
    }

    public static List<AreaMutation> generateStandardMutations(MutationOptions options) {
        final int offset = options.getPositionOffset();

        return asList(
            new AreaMutation(format("drag left by %dpx", offset), (area) -> area.drag(-offset, 0)),
            new AreaMutation(format("drag right by %dpx", offset), (area) -> area.drag(offset, 0)),
            new AreaMutation(format("drag top by %dpx", offset), (area) -> area.drag(0, -offset)),
            new AreaMutation(format("drag down by %dpx", offset), (area) -> area.drag(0, offset)),

            new AreaMutation(format("increase width by %dpx", offset), (area) -> area.distort(0, 0, offset, 0)),
            new AreaMutation(format("decrease width by %dpx", offset), (area) -> area.distort(0, 0, -offset, 0)),
            new AreaMutation(format("increase height by %dpx", offset), (area) -> area.distort(0, 0, 0, offset)),
            new AreaMutation(format("decrease height by %dpx", offset), (area) -> area.distort(0, 0, 0, -offset)),

            new AreaMutation(format("move left edge right by %dpx", offset), (area) -> area.distort(offset, 0, -offset, 0)),
            new AreaMutation(format("move top edge down by %dpx", offset), (area) -> area.distort(0, offset, 0, -offset))
        );
    }

    public String getMutationName() {
        return mutationName;
    }

    public void setMutationName(String mutationName) {
        this.mutationName = mutationName;
    }

    public Function<Rect, Rect> getMutationFunction() {
        return mutationFunction;
    }

    public void setMutationFunction(Function<Rect, Rect> mutationFunction) {
        this.mutationFunction = mutationFunction;
    }

    public Rect mutate(Rect area) {
        return mutationFunction.apply(area);
    }
}
