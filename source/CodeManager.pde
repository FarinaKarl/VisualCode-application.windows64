/* 
Copyright (C) 2014 Carlo Farina 
The program is a semi-free software. 
The license terms are specified in the License file.
*/

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;

class CodeManager {

  String pastedLines; 
  String pastedCode = "";
  String codice [] = new String [1];

  float skipTime; // gestisce la pressione ripetuta del tasto di skip
  float skipDelay = 0.5;

  void run() {
    pasteCode();     
    codice[0] = pastedCode;
    saveStrings(codeFileName, codice);
  }

  void pasteCode() {

    keyPressed(); 

    key = '/'; // risolve bug Ctrl+v

    if (codeIndex > 4) { // se l'ultente ha terminato la compilazione 
      compilationCompleted = true;
      if (pastedCode != "") // se l'utente ha incollato almeno una delle componenti del codice
        codeCreated = true;
    }
  }

  void keyPressed() {

    if (codeIndex == 0 && key == 0x16) { // Ctrl+v
      pastedLines = GetTextFromClipboard(); // acquisisce le variabili globali 
      pastedCode = pastedCode + pastedLines + '\n'; // memorizza le variabili globali nella stringa destinata all'intero codice
      codeIndex = 1;
    } 

    if (codeIndex > 0 && key == 0x16 && (!pastedLines.equals(GetTextFromClipboard())) ) { // Ctrl+v
      pastedLines = GetTextFromClipboard(); // acquisisce le componenti del codice una ad una

      // controlli sul copia e incolla      
      if ( (codeIndex == 1 && (pastedLines.indexOf("setup()")>= 0 || pastedLines.indexOf("setup ()")>= 0)) || (codeIndex == 2 && (pastedLines.indexOf("draw()")>= 0 || pastedLines.indexOf("draw ()")>= 0)) || codeIndex == 3 || (codeIndex == 4 && (pastedLines.indexOf("class")>= 0)) ) {
        pastedCode = pastedCode + pastedLines + '\n'; // memorizza il codice per intero un pezzo alla volta
        codeIndex++;
      }
    }

    // salta la memorizzazione di una parte del codice    
    if ((key == 's' || key == 'S') && millis() / 1000 > skipTime + skipDelay) {

      skipTime = millis() / 1000;

      if (codeIndex == 0)
        pastedLines = ""; // evita NullPointerException in caso l'utente salti "Paste Global Variables"

      codeIndex++;
    }
  }

  String GetTextFromClipboard() {

    String text = (String) GetFromClipboard(DataFlavor.stringFlavor);
    return text;
  }

  Object GetFromClipboard(DataFlavor flavor) {
    Clipboard clipboard = getToolkit().getSystemClipboard();
    Transferable contents = clipboard.getContents(null);
    Object obj = null;
    if (contents != null && contents.isDataFlavorSupported(flavor))
    {
      try
      {
        obj = contents.getTransferData(flavor);
      }
      catch (UnsupportedFlavorException exu) // Unlikely but we must catch it
      {
        println("Unsupported flavor: " + exu);
        //~ exu.printStackTrace();
      }
      catch (java.io.IOException exi)
      {
        println("Unavailable data: " + exi);
        //~ exi.printStackTrace();
      }
    }
    return obj;
  }
}

