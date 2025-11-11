package ch.ethz.inf.peachlab.ui;

public interface HasRender {

    // First call to render should always be super.render() or removeAll()
    void render();
}
