/* grapher.ui.Main
 * (c) blanch@imag.fr 2021–2024                                            */

 package grapher.ui;

 import javax.swing.JFrame;
 import javax.swing.SwingUtilities;
 
 
 // main that launch a grapher.ui.Grapher
 
 public class Main extends JFrame {
	 Main(String title, String[] expressions) {
		 super(title);
		 setDefaultCloseOperation(EXIT_ON_CLOSE);
		 
		 Grapher grapher = new Grapher(this);		
		 for(String expression: expressions) {
			 grapher.addFunction(expression);
		 }
		 
		 add(grapher);
		 pack();
	 }
 
	 public static void main(String[] argv) {
		 final String[] expressions = argv;
		 SwingUtilities.invokeLater(new Runnable() {
			 public void run() { 
				 new Main("grapher", expressions).setVisible(true);
			 }
		 });
	 }
 }
 