package grapher.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.Point;
import java.awt.Cursor;

public class Interaction extends MouseAdapter implements MouseWheelListener {
    private Grapher grapher;
    private Point startDrag;
    private boolean dragging = false;
    
    public Interaction(Grapher grapher) {
        this.grapher = grapher;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) { // Botão esquerdo
            startDrag = e.getPoint();
            grapher.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cursor de mão
            dragging = true;
        } else if (e.getButton() == MouseEvent.BUTTON3) { // Botão direito
            startDrag = e.getPoint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragging) {
            Point currentDrag = e.getPoint();
            int dX = currentDrag.x - startDrag.x;
            int dY = currentDrag.y - startDrag.y;
            grapher.translate(dX, dY); // Mover o gráfico
            startDrag = currentDrag; // Atualiza o ponto de início
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && dragging) {
            dragging = false;
            grapher.setCursor(Cursor.getDefaultCursor());
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            Point endDrag = e.getPoint();
            grapher.zoom(startDrag, endDrag); // Zoom na área selecionada
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) { // Clique esquerdo
            grapher.zoom(e.getPoint(), -5); // Zoom in
        } else if (e.getButton() == MouseEvent.BUTTON3) { // Clique direito
            grapher.zoom(e.getPoint(), 5); // Zoom out
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Point center = e.getPoint();
        int notches = e.getWheelRotation();
        if (notches < 0) {
            grapher.zoom(center, -5); // Zoom in
        } else {
            grapher.zoom(center, 5); // Zoom out
        }
    }
}
