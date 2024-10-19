package grapher.ui;

/* imports *****************************************************************/
import java.math.BigDecimal;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.awt.Point;

import javax.swing.JPanel;

import java.util.Vector;

import static java.lang.Math.*;

import grapher.fc.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/* Grapher *****************************************************************/
public class Grapher extends JPanel {
    static final int MARGIN = 40;
    static final int STEP = 5;

    static final BasicStroke dash = new BasicStroke(1, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND, 1.f, new float[] { 4.f, 4.f }, 0.f);

    protected int W = 400;
    protected int H = 300;

    protected double xmin, xmax;
    protected double ymin, ymax;

    protected Vector<Function> functions;

	protected DefaultListModel<String> functionListModel; // Modelo para a JList
    protected JList<String> functionList; // Lista de funções

    // Painel para o gráfico
    private JPanel graphPanel;

	public Grapher() {
		xmin = -PI/2.; xmax = 3*PI/2;
		ymin = -1.5;   ymax = 1.5;
		functions = new Vector<Function>();
	
		Interaction interaction = new Interaction(this);
		
		// Configurando o painel gráfico e adicionando os listeners
		graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGraph(g); // Desenha o gráfico
            }
        };
        graphPanel.setPreferredSize(new Dimension(W, H));
        graphPanel.addMouseListener(interaction);
        graphPanel.addMouseMotionListener(interaction);
        graphPanel.addMouseWheelListener(interaction);

		// Configurando o modelo e a lista de funções
        functionListModel = new DefaultListModel<>();
        functionList = new JList<>(functionListModel);

        // Adicionando um campo de texto e botões para adicionar e apagar funções
        JTextField functionInput = new JTextField();
        JButton addButton = new JButton("+");
        JButton removeButton = new JButton("-");

        // Painel para a lista e a caixa de entrada
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.add(new JScrollPane(functionList), BorderLayout.CENTER);
        listPanel.add(functionInput, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel(); // Painel para os botões
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        listPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Ação para o botão de adicionar função
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String functionText = functionInput.getText();
                if (!functionText.isEmpty()) {
                    try {
                        addFunction(functionText);
                        functionListModel.addElement(functionText);  // Adiciona a função à lista
                        functionInput.setText("");  // Limpa o campo de texto após a adição
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Função inválida!");
                    }
                }
            }
        });

        // Ação para o botão de remover função
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = functionList.getSelectedIndex();
                if (selectedIndex != -1) {
                    functions.remove(selectedIndex); // Remove a função da lista
                    functionListModel.remove(selectedIndex); // Remove da lista exibida
                    repaint(); // Repaint para atualizar o gráfico
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione uma função para remover!");
                }
            }
        });

        // Adicionando a lista à esquerda e o gráfico à direita
        setLayout(new BorderLayout());
        add(listPanel, BorderLayout.WEST);
        add(graphPanel, BorderLayout.CENTER); // Adiciona o painel gráfico
    }

    // Método para desenhar o gráfico no painel gráfico
    protected void drawGraph(Graphics g) {
        W = graphPanel.getWidth();
        H = graphPanel.getHeight();

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // background
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, W, H);

        // box
        W -= 2 * MARGIN;
        H -= 2 * MARGIN;
        if (W < 0 || H < 0) {
            return;
        }

        // plot
        g2.setColor(Color.BLACK);
        g2.drawRect(MARGIN, MARGIN, W, H);

        g2.clipRect(MARGIN, MARGIN, W, H);

        // x values
        final int N = W / STEP + 1;
        final double dx = dx(STEP);
        double xs[] = new double[N];
        int Xs[] = new int[N];
        for (int i = 0; i < N; i++) {
            double x = xmin + i * dx;
            xs[i] = x;
            Xs[i] = X(x);
        }

        for (Function f : functions) {
            // y values
            int Ys[] = new int[N];
            for (int i = 0; i < N; i++) {
                Ys[i] = Y(f.y(xs[i]));
            }

            g2.drawPolyline(Xs, Ys, N);
        }

        g2.setClip(null);

        // axes
        g2.drawString("x", W + MARGIN, H + MARGIN + 10);
        g2.drawString("y", MARGIN - 10, MARGIN);

        drawXTick(g2, BigDecimal.ZERO);
        drawYTick(g2, BigDecimal.ZERO);

        BigDecimal xstep = unit((xmax - xmin) / 10);
        BigDecimal ystep = unit((ymax - ymin) / 10);

        g2.setStroke(dash);
        for (BigDecimal x = xstep; x.doubleValue() < xmax; x = x.add(xstep)) {
            drawXTick(g2, x);
        }
        for (BigDecimal x = xstep.negate(); x.doubleValue() > xmin; x = x.subtract(xstep)) {
            drawXTick(g2, x);
        }
        for (BigDecimal y = ystep; y.doubleValue() < ymax; y = y.add(ystep)) {
            drawYTick(g2, y);
        }
        for (BigDecimal y = ystep.negate(); y.doubleValue() > ymin; y = y.subtract(ystep)) {
            drawYTick(g2, y);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(W, H);
    }

    // handling function list

    void addFunction(String expression) {
        addFunction(FunctionFactory.createFunction(expression));
    }

    void addFunction(Function function) {
        functions.add(function);
        repaint();
    }

    // auxiliary methods

    protected double dx(int dX) {
        return (double) ((xmax - xmin) * dX / W);
    }

    protected double dy(int dY) {
        return -(double) ((ymax - ymin) * dY / H);
    }

    protected double x(int X) {
        return xmin + dx(X - MARGIN);
    }

    protected double y(int Y) {
        return ymin + dy((Y - MARGIN) - H);
    }

    protected int X(double x) {
        int Xs = (int) round((x - xmin) / (xmax - xmin) * W);
        return Xs + MARGIN;
    }

    protected int Y(double y) {
        int Ys = (int) round((y - ymin) / (ymax - ymin) * H);
        return (H - Ys) + MARGIN;
    }

    protected void drawXTick(Graphics2D g2, BigDecimal x) {
        double _x = x.doubleValue();
        if (_x > xmin && _x < xmax) {
            final int X0 = X(_x);
            g2.drawLine(X0, MARGIN, X0, H + MARGIN);
            g2.drawString(x.toString(), X0, H + MARGIN + 15);
        }
    }

    protected void drawYTick(Graphics2D g2, BigDecimal y) {
        double _y = y.doubleValue();
        if (_y > ymin && _y < ymax) {
            final int Y0 = Y(_y);
            g2.drawLine(0 + MARGIN, Y0, W + MARGIN, Y0);
            g2.drawString(y.toString(), 5, Y0);
        }
    }

    protected static BigDecimal unit(double w) {
        int scale = (int) floor(log10(w));
        w /= pow(10, scale);
        BigDecimal value;
        if (w < 2) {
            value = new BigDecimal(2);
        } else if (w < 5) {
            value = new BigDecimal(5);
        } else {
            value = new BigDecimal(10);
        }
        return value.movePointRight(scale);
    }

    // interaction
    void translate(int dX, int dY) {
        double dx = dx(dX);
        double dy = dy(dY);
        xmin -= dx;
        xmax -= dx;
        ymin -= dy;
        ymax -= dy;
        repaint();
    }

    void zoom(Point center, int ticks) {
        double px = (double) (center.x - MARGIN) / W;
        double py = (double) (center.y - MARGIN) / H;

        double newWidth = (xmax - xmin) * (ticks > 0 ? 0.9 : 1.1);
        double newHeight = (ymax - ymin) * (ticks > 0 ? 0.9 : 1.1);

        xmin = xmin + px * (xmax - xmin) - px * newWidth;
        xmax = xmin + newWidth;

        ymin = ymin + py * (ymax - ymin) - py * newHeight;
        ymax = ymin + newHeight;

        repaint();
    }

    void zoom(Point p, Point q) {
        double x0 = x(p.x);
        double x1 = x(q.x);
        double y0 = y(p.y);
        double y1 = y(q.y);

        if (x0 != x1) {
            xmin = min(x0, x1);
            xmax = max(x0, x1);
        }
        if (y0 != y1) {
            ymin = min(y0, y1);
            ymax = max(y0, y1);
        }
        repaint();
    }
}
