package ch.ethz.inf.peachlab.ui.views.kernel;

import ch.ethz.inf.peachlab.model.entity.HasCellData;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public record Toc(String title, HasCellData cell, List<Toc> children) {

    public static List<Toc> buildTocTree(List<TocElement> elements) {
        List<Toc> roots = new ArrayList<>();
        Deque<LevelNode> stack = new ArrayDeque<>();

        for (TocElement element : elements) {
            Toc current = new Toc(element.title(), element.cell(), new ArrayList<>());
            int level = element.level();

            // Pop until we find a parent with lower level
            while (!stack.isEmpty() && stack.peek().level >= level) {
                stack.pop();
            }

            if (stack.isEmpty()) {
                // Top-level heading
                roots.add(current);
            } else {
                // Child of previous level
                stack.peek().node.children().add(current);
            }

            stack.push(new LevelNode(level, current));
        }

        return roots;
    }

    private record LevelNode(int level, Toc node) {}
}
