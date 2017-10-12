import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class Interfaz {
	AnilloToken proceso;
	private static JFrame MainWindow = new JFrame();        								
	private static JButton Start = new JButton("Empezar");			
	private static JButton B_crash = new JButton("Matar Proceso");						
	private static JLabel L_Message = new JLabel("Elige Proceso");
	private static JLabel L_Conv = new JLabel();
	public static JTextArea TA_status = new JTextArea();				
	public static JScrollPane SP_Conversation = new JScrollPane();
        
        
    public static JComboBox jComboBox1 = new javax.swing.JComboBox();

	public static void main(String args[]){ 
		BuildMainWindow();
		Initialise();
	}


        //Inicializa la ventana
	private static void Initialise() { 
		Start.setEnabled(true);
	}

	//Posicionamiento de los botones
	private static void BuildMainWindow() { 
		MainWindow.setTitle("AnilloToken");
		MainWindow.setSize(100, 500);
		MainWindow.setVisible(true);
		ConfigurationMainWindow();
		algoritmo();
	}

	//Lanzamos los 5 procesos y los añadimos a la caja de selección
	 public void startAction(java.awt.event.ActionEvent evt) { 
	        for (int i = 1; i < 6; i++) {
                    proceso = new AnilloToken(i, 50000 + i, this);
	            proceso.start();
	            jComboBox1.addItem("Proceso " + i);
	        }
	    }
	
        //Algoritmo del Anillo
	private static void algoritmo() {
		Start.addActionListener(
			new java.awt.event.ActionListener()
			{
				@Override
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					Interfaz gui = new Interfaz();
					gui.startAction(evt);
				}
			}
		);

		//Botón de matar proceso
                B_crash.addActionListener( 
			new java.awt.event.ActionListener()
			{
				@Override
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					 crashActionPerform(evt);
				}

				private void crashActionPerform(java.awt.event.ActionEvent evt) {
			        String crash = jComboBox1.getSelectedItem().toString();
			        try {
			            Socket socket = new Socket("127.0.0.1", 50000 + Integer.parseInt(crash.substring(crash.length() - 1)));
			            PrintWriter pw = new PrintWriter(socket.getOutputStream());
			            pw.println("Muerto");
			            pw.close();
			            socket.close();
			        } catch (UnknownHostException ex) {
			            Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
			        } catch (IOException ex) {
			            Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
			        }
			    }
			}
		);
		
	}
	
	 public void outputStatus(String text) {
	        TA_status.append(text);
	        TA_status.append("\n");
	    }

        //Configuración de la ventana principal
	private static void ConfigurationMainWindow() { 
		MainWindow.setSize(390,350);
		MainWindow.getContentPane().setLayout(null);
		B_crash.setBounds(250, 10, 125, 25);
		Start.setBounds(10, 270, 363, 25);
		Start.setBackground(new Color(152,251,152));
		
		
		MainWindow.getContentPane().add(B_crash);
		MainWindow.getContentPane().add(Start);
		MainWindow.getContentPane().add(L_Message);
		MainWindow.getContentPane().add(L_Conv);
		MainWindow.getContentPane().add(jComboBox1);
		jComboBox1.setBounds(120, 10, 90,20);
		L_Message.setBounds(10, 10, 160, 20);
		L_Conv.setBounds(100,70,140,16);
		
		TA_status.setColumns(20);
		TA_status.setRows(5);
		TA_status.setEditable(false);
		
		SP_Conversation.setViewportView(TA_status);
		MainWindow.getContentPane().add(SP_Conversation);
		SP_Conversation.setBounds(10, 90, 363, 180);
		MainWindow.getContentPane().add(SP_Conversation);	
	}

}
