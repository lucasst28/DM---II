package grapher.ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main extends JFrame {    
    Main(String title, String[] expressions) {
        super(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        Grapher grapher = new Grapher(this);        
        for (String expression : expressions) {
            grapher.addFunction(expression); // Adiciona a função
        }
        
        add(grapher);
        pack();
    }

    public static void main(String[] argv) {
        final String[] expressions = argv;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() { 
                new Main("Grapher", expressions).setVisible(true);
            }
        });
    }
}
