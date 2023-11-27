package zPracticaT;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class MyAppTest {

    @Test
    public void testBuscarPatron() {
        MyApp myApp = new MyApp();


        myApp.textArea.setText("Este es un ejemplo de texto para buscar un patrón.");


        String patron = "Este";
        myApp.buscarPatron(patron);


        assertTrue(myApp.textArea.getHighlighter().getHighlights().length > 0);


        String textoResaltado = myApp.textArea.getText().substring(
                myApp.textArea.getHighlighter().getHighlights()[0].getStartOffset(),
                myApp.textArea.getHighlighter().getHighlights()[0].getEndOffset()
        );


        assertTrue(textoResaltado.equals(patron));
    }
}
