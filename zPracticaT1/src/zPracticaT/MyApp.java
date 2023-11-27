package zPracticaT;


import javax.swing.*;



import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MyApp extends JFrame {
    JTextArea textArea;
    private JFileChooser fileChooser;
    private static final Logger logger = LogManager.getLogger(MyApp.class);

    public MyApp() {
 
    	
        setTitle("Editor de Texto");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        //Configuración del menú
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Archivo");

        JMenu fileMenu1 = new JMenu("Más");
        
        //abrir un archivo
        JMenuItem openItem = new JMenuItem("Abrir");
        openItem.addActionListener(e -> abrirArchivo());
        fileMenu.add(openItem);

        //guardar un archivo
        JMenuItem saveItem = new JMenuItem("Guardar");
        saveItem.addActionListener(e -> guardarArchivo());
        fileMenu.add(saveItem);

        JButton searchButton = new JButton("Buscar Patrón");
        searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String patron = JOptionPane.showInputDialog(MyApp.this, "Introduce el patrón a buscar:");
		      
				 buscarPatron(patron);
				
				
			}
		});
        
        add(searchButton, BorderLayout.NORTH);
        
        
        
        JMenuItem formatNumberItem = new JMenuItem("Formatear Número");
        formatNumberItem.addActionListener(e -> formatearNumero());
        fileMenu1.add(formatNumberItem);
        
       
        JMenuItem GuardarEnZip = new JMenuItem("Guardar como Zip");
        GuardarEnZip.addActionListener(e -> GuardarZip());
        fileMenu1.add(GuardarEnZip);
        
        
        
        
        
        
        JMenuItem randomButton = new JMenuItem("Número Aleatorio");
        randomButton.addActionListener(e -> mostrarNumeroAleatorio());
        fileMenu1.add(randomButton);
        

        
        menuBar.add(fileMenu);
        menuBar.add(fileMenu1);
        setJMenuBar(menuBar);

        //Selector de archivos
        fileChooser = new JFileChooser();


        setVisible(true);


        logger.info("La aplicación se ha iniciado.");

    
        
        
        
    }
    
    private void GuardarZip(){


		String paquete = MyApp.class.getPackage().getName();
	
		
		File dirActual = new File( System.getProperty("user.dir") + "/src/" + 
				 paquete.replaceAll( "\\.", "/" ) + "/" );
	
		File[] fics = dirActual.listFiles();
		for (File f : fics) 
		    if (f.isFile()) 
		        System.out.println("  " + f.getName());
		System.out.println( "Comprimiendo estos ficheros a fichero test.zip..." );
		comprimirAZip( "test.zip", fics );
    	
    	
    	
    	
    }
    
    private static void comprimirAZip( String nomZip, File[] listaFics ) {
	    // Crear un buffer para la lectura de ficheros
	    byte[] buffer = new byte[1024];
	    try {
	        // Crear el fichero Zip
	        ZipOutputStream ficZip = new ZipOutputStream(new FileOutputStream(nomZip) );
	        // Comprimir cada fichero en ese zip
			for (File f : listaFics) 
				if (f.isFile()) {
					System.out.println( "Comprimiendo " + f.getName() + "..." );
		            FileInputStream in = new FileInputStream( f );
		            // Añade punto de entrada zip (entry)
		            ficZip.putNextEntry(new ZipEntry(f.getName()));
		            // Mueve los bytes al zip a través del buffer
		            int dato;
		            while ((dato = in.read(buffer)) > 0) {
		                ficZip.write(buffer, 0, dato);
		            }
		            // Completar la entrada
		            ficZip.closeEntry();
		            in.close();
		        }
	        // Cerrar el fichero zip
	        ficZip.close();
	       
	        logger.debug("Fichero zip " + nomZip + " acabado correctamente.");
	    } catch (IOException e) {
	    	e.printStackTrace();
	        logger.error("Error al comprimir.");
	    }	
    }
    

    
    private void formatearNumero() {
        String input = JOptionPane.showInputDialog(this, "Introduce un número formato americano:");
        try {
            double numero = Double.parseDouble(input);
            
            DecimalFormat dfLocale = new DecimalFormat();
            
            
            textArea.append(dfLocale.format(numero)+"");
        
            JOptionPane.showMessageDialog(this, "Número Formateado a Local: " +  dfLocale.format( numero ));
            logger.debug("Se formateó el número: {}",  dfLocale.format( numero ));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Entrada no válida. Introduce un número válido.");
            logger.error("Error al formatear el número. Entrada no válida.", ex);
        }
    }
    
    

    void buscarPatron(String patron) {
       
        if (patron != null && !patron.isEmpty()) {
            //Eliminar resaltados anteriores
            textArea.getHighlighter().removeAllHighlights();

            Pattern pattern = Pattern.compile(patron);
            Matcher matcher = pattern.matcher(textArea.getText());

            //Resaltar lo que coincida
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                try {
                    DefaultHighlighter.DefaultHighlightPainter painter =
                            new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
                    textArea.getHighlighter().addHighlight(start, end, painter);
                } catch (BadLocationException e) {
                    logger.error("Error al resaltar la coincidencia.", e);
                }
            }

            logger.debug("Búsqueda de patrón completada: {}", patron);
        }
    }

    private void abrirArchivo() {
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                textArea.setText(content.toString());
                logger.debug("Se abrió el archivo: {}", file.getName());
            } catch (IOException e) {
                logger.error("Error al abrir el archivo.", e);
            }
        }
    }

    private void guardarArchivo() {
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(textArea.getText());
                logger.debug("Se guardó el archivo como: {}", file.getName());
            } catch (IOException e) {
                logger.error("Error al guardar el archivo.", e);
            }
        }
    }

    private void mostrarNumeroAleatorio() {
        Random random = new Random();
        int randomNumber = random.nextInt(100);
        JOptionPane.showMessageDialog(this, "Número Aleatorio: " + randomNumber);
  
        textArea.append(randomNumber+"");
        logger.debug("Se generó un número aleatorio: {}", randomNumber);
    }

    public static void main(String[] args) {
    // Los mensajes los se guardan en app.log
    // La carpeta resources contiene el archivo .xml para el funcionamiento del log
    
    	SwingUtilities.invokeLater(() -> {
            MyApp myApp = new MyApp();
            myApp.textArea.setText("Este es un ejemplo de texto.");
       
           
        });
    }
}
