package ch.ethz.inf.peachlab.ui.components;

import ch.ethz.inf.peachlab.ui.HasRender;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

import java.io.Serial;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_POSITION_RELATIVE;

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
    
        const rect = container.getBoundingClientRect();
    
        tooltip.innerHTML = cell.dataset.tooltip;
        tooltip.style.display = 'block';
    
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;
    
        tooltip.style.left = (x + 10) + 'px';
        tooltip.style.top = y + 'px';
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
        initStyles();
    }

    private void initStyles() {
        addClassNames(STYLE_POSITION_RELATIVE);
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
