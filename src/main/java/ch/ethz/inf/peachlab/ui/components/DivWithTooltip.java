package ch.ethz.inf.peachlab.ui.components;

import ch.ethz.inf.peachlab.ui.HasRender;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

import java.io.Serial;

public class DivWithTooltip extends Div implements HasRender {

    private static final String JS = """
      const container = this;
      const tooltip = container.querySelector('.matrix-tooltip');
    
      container.addEventListener('mousemove', e => {
        const cell = e.target.closest('%s');
        if (!cell) {
          tooltip.style.display = 'none';
          return;
        }
    
        tooltip.innerHTML = cell.dataset.tooltip;
        tooltip.style.display = 'block';

        tooltip.style.left = e.clientX + 10 + 'px';
        tooltip.style.top = e.clientY + 'px';
      });
    
      container.addEventListener('mouseleave', () => {
        tooltip.style.display = 'none';
      });
    """;
    @Serial
    private static final long serialVersionUID = -1098911426567764261L;

    private final String hoverTarget;

    public DivWithTooltip(String hoverTarget) {
        this.hoverTarget = hoverTarget;
    }

    private Component createTooltip() {
        Div tooltip = new Div();
        tooltip.addClassName("matrix-tooltip");
        tooltip.getStyle()
                .set("position", "absolute")
                .set("display", "none")
                .set("pointer-events", "none")
                .set("z-index", "1000");

        return tooltip;
    }

    @Override
    public void render() {
        removeAll();
        add(createTooltip());
        getElement().executeJs(JS.formatted(hoverTarget));
    }
}
