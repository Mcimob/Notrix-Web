window.attachNotebookMatrixHover = function (host, gridId) {
    let lastIndex = null;

    host.addEventListener("mousemove", event => {
        const index = event
            .composedPath()[1]
            ?.getAttribute("data-kernel-index");
        if (index == null || index === lastIndex) return;
        highlight(index);
    });

    host.addEventListener("mouseleave", () => {
        const grid = getGrid();
        if (grid) clearHighlights(grid);
        lastIndex = null;
    });

    function highlight(index) {
        const grid = getGrid();
        if (!grid) return;

        grid.scrollToIndex(index);
        clearHighlights(grid);

        const row = findRow(grid, index);
        if (!row) return;

        row.querySelectorAll("td").forEach(td =>
            td.part.add("hover-highlight")
        );

        lastIndex = index;
    }

    function findRow(grid, index) {
        return Array.from(
            grid.shadowRoot.querySelectorAll("tr[aria-rowindex]")
        ).find(r =>
            r.getAttribute("aria-rowindex") - 2 == index
        );
    }

    function clearHighlights(grid) {
        grid.shadowRoot
            .querySelectorAll('td[part~="hover-highlight"]')
            .forEach(td => td.part.remove("hover-highlight"));
    }

    function getGrid() {
        return document.getElementById(gridId);
    }
};