import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.awt.datatransfer.Clipboard; 
import java.awt.datatransfer.Transferable; 
import java.awt.datatransfer.DataFlavor; 
import java.awt.datatransfer.UnsupportedFlavorException; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class VisualCode extends PApplet {

/* 
 Copyright (C) 2014 Carlo Farina 
 The program is a semi-free software. 
 The license terms are specified in the License file.
 */

CodeManager codeManager = new CodeManager();
Finder finder = new Finder();
String codeFileName = "code.txt";

final static String ICON  = "icon.png";

int lineDistance;
int codeIndex;
int closingTime = 1;

float codeReadyTime;

boolean codeCreated;
boolean compilationCompleted;
boolean artificialFunction;
boolean codeReadyTimeUpdated;

int coloreCodice;

public void setup() {  
  size(150, 150);

  changeAppIcon( loadImage(ICON) );

  textAlign(CENTER, CENTER);
  textSize(14);
}

public void draw() {

  codeManager.run(); // crea il file di testo del codice

  background(0);

  if (codeIndex == 0) {
    text("Paste imports and/or", width / 2, height / 2 - height / 5);
    text("global variables", width / 2, height / 2);
    text("or press 'S'", width / 2, height / 2 + height / 5);
  }
  if (codeIndex == 1) {
    text("Paste setup()", width / 2, height / 2 - height / 11);
    text("or press 'S'", width / 2, height / 2 + height / 11);
  }
  if (codeIndex == 2) {
    text("Paste draw()", width / 2, height / 2 - height / 11);
    text("or press 'S'", width / 2, height / 2 + height / 11);
  }
  if (codeIndex == 3) {
    text("Paste methods", width / 2, height / 2 - height / 11);
    text("or press 'S'", width / 2, height / 2 + height / 11);
  }
  if (codeIndex == 4) {
    text("Paste classes", width / 2, height / 2 - height / 11);
    text("or press 'S'", width / 2, height / 2 + height / 11);
  }
  if (codeIndex > 4) {
    text("code.txt", width / 2, height / 2 - height / 11);
    if (codeCreated)                                            // se il codice incollato dall'utente NON \u00e8 vuoto
      text("is ready", width / 2, height / 2 + height / 11);
    if (!codeCreated)                                           // se il codice incollato dall'utente \u00e8 vuoto
      text("is empty", width / 2, height / 2 + height / 11);
  }

  if (codeCreated) { // se il codice incollato dall'utente NON \u00e8 vuoto ne elabora il contenuto
    String [] righe = loadStrings(codeFileName);

    finder.setSize(righe); 
    finder.findFunctions(righe); // trova le funzioni nel codice
    finder.findClasses(righe);
    finder.fixParenthesis(righe); // aggiunge le graffe a if/for se non ne hanno
    finder.fixComments(righe);

    lineDistance = finder.sketchHeight / righe.length;

    // dichiara le variabili alla prima riga del codice
    righe[0] = "PGraphics osCanvas; int mouseX1, mouseX2, mouseY1, mouseY2, deltaX, deltaY; float scala = 1; boolean showCanvas, scalaCanvas, canvasSwitch, keySwitch, mouseSwitch;" + righe[0];

    disegnaSetup(finder, righe);
    disegnaDraw(finder, righe);
    disegnaFunzioni(finder, righe);

    saveStrings(codeFileName, righe);
  }

  if (compilationCompleted) {

    // inizia il conteggio per la chiusura del programma

    if (!codeReadyTimeUpdated) { 
      codeReadyTime = millis() / 1000;
      codeReadyTimeUpdated = true;
    }

    if (millis() / 1000 > codeReadyTime + closingTime)
      exit();
  }
}

public void disegnaSetup(Finder finder, String [] righe) {

  String substring = "";

  for (int i = finder.rigaInizioSetup; i <= finder.rigaFineSetup; i++) { 

    substring = finder.fixText(righe, i);

    coloreCodice = color(0xffB5FF0D); 

    if (i == finder.rigaInizioSetup)   
      righe[i] = righe[i] + "osCanvas = createGraphics(" + finder.sketchWidth * 7 + ", " + finder.sketchHeight * 7 + "); osCanvas.beginDraw(); osCanvas.noStroke(); osCanvas.fill(0); osCanvas.textSize(" + finder.sketchHeight / righe.length * 0.9f + "); osCanvas.text(" + '"' + substring + '"' + ", 0, " + ((i+1) * lineDistance - lineDistance / 4) + "); osCanvas.fill(" + coloreCodice + ", 180); osCanvas.rect(0, " + i * lineDistance + ", " + finder.sketchWidth + ", " + lineDistance + ");";

    if (i > finder.rigaInizioSetup && i < finder.rigaFineSetup) 
      righe[i] = righe[i] + "osCanvas.fill(0); osCanvas.text(" + '"' + substring + '"' + ", 0, " + ((i+1) * lineDistance - lineDistance / 4) + "); osCanvas.fill(" + coloreCodice + ", 180); osCanvas.rect(0, " + i * lineDistance + ", " + finder.sketchWidth + ", " + lineDistance + ");";

    if (i == finder.rigaFineSetup)
      righe[i] = "osCanvas.fill(0); osCanvas.text(" + '"' + righe[i] + '"' + ", 0, " + ((i+1) * lineDistance - lineDistance / 4) + "); osCanvas.fill(" + coloreCodice + ", 180); osCanvas.rect(0, " + i * lineDistance + ", " + finder.sketchWidth + ", " + lineDistance + "); " + righe[i];
  }
}

public void disegnaDraw(Finder finder, String [] righe) { 

  String substring = "";

  for (int i = finder.rigaInizioDraw; i <= finder.rigaFineDraw; i++) {

    substring = finder.fixText(righe, i);

    coloreCodice = color(0xffB5FF0D);

    if (i == finder.rigaInizioDraw)
      righe[i] = righe[i] + "if (!canvasSwitch && (key == 's' || key == 'S')) showCanvas = true; if (canvasSwitch && (key == 'h' || key == 'H')) showCanvas = false; if (showCanvas && mousePressed) { deltaX = deltaX + mouseX - pmouseX; deltaY = deltaY + mouseY - pmouseY;} translate(deltaX, deltaY); if (!showCanvas) translate(-deltaX, -deltaY); if ((key == '+'  || key == '-') && scalaCanvas) { osCanvas.scale(scala); scalaCanvas = false;} pushMatrix(); osCanvas.background(" + 255 + "); osCanvas.fill(0); osCanvas.text(" + '"' + substring + '"' + ", 0, " + ((i+1) * lineDistance - lineDistance / 4) + "); osCanvas.fill(" + coloreCodice + ", 180); osCanvas.rect(0, " + i * lineDistance + ", " + finder.sketchWidth + ", " + lineDistance + ");";

    if (i > finder.rigaInizioDraw && i < finder.rigaFineDraw) 
      righe[i] = righe[i] + "osCanvas.fill(0); osCanvas.text(" + '"' + substring + '"' + ", 0, " + ((i+1) * lineDistance - lineDistance / 4) + "); osCanvas.fill(" + coloreCodice + ", 180); osCanvas.rect(0, " + i * lineDistance + ", " + finder.sketchWidth + ", " + lineDistance + ");";

    if (i == finder.rigaFineDraw)
      righe[i] = "osCanvas.fill(0); osCanvas.text(" + '"' + righe[i] + '"' + ", 0, " + ((i+1) * lineDistance - lineDistance / 4) + "); osCanvas.fill(" + coloreCodice + ", 180); osCanvas.rect(0, " + i * lineDistance + ", " + finder.sketchWidth + ", " + lineDistance + "); if (keyPressed) keyPressedFunction(); if (!keyPressed && !keySwitch) keyReleasedFunction(); if (mousePressed) mousePressedFunction(); if (!mousePressed && !mouseSwitch) mouseReleasedFunction(); if (mouseX != pmouseX && mouseY != pmouseY) mouseMovedFunction(); osCanvas.endDraw(); popMatrix(); if (showCanvas) { background(#FFFFFF); translate(0, 0, 1); image(osCanvas, 0, 0);} " + righe[i]; // translate sulla Z per posizionare l'osCanvas pi\u00f9 avanti rispetto al canvas principale
  }
}

public void disegnaFunzioni(Finder finder, String [] righe) {

  String substring = "";

  int endFunctionCharIndex = 0;

  boolean type = false;

  for (int i = finder.rigaFineDraw + 1; i < righe.length; i++) {

    substring = finder.fixText(righe, i);

    coloreCodice = color(0xffB5FF0D);

    if (righe[i].indexOf("keyPressed(")> 0 || righe[i].indexOf("keyPressed (")> 0 || righe[i].indexOf("keyReleased(")> 0 || righe[i].indexOf("keyReleased (")> 0 || righe[i].indexOf("mousePressed(")> 0 || righe[i].indexOf("mousePressed (")> 0 || righe[i].indexOf("mouseReleased(")> 0 || righe[i].indexOf("mouseReleased (")> 0) { // cerca funzioni artificiali
      artificialFunction = true;
    }

    if (righe[i].indexOf(") {")> 0 || righe[i].indexOf("){")> 0) { // cerca riga iniziale del resto delle funzioni
      type = true;
    }

    if (i >= finder.rigaInizioClassi) // se sono in una classe
      endFunctionCharIndex = 2;

    if (type && ( righe[i].indexOf("}")== endFunctionCharIndex || righe[i].indexOf("return")>= 0) ) { // cerca riga finale o con comando return nel resto delle funzioni
      type = false;
      if (!artificialFunction) {
        righe[i] = "osCanvas.fill(0); osCanvas.text(" + '"' + righe[i] + '"' + ", 0, " + ((i+1) * lineDistance - lineDistance / 4) + "); osCanvas.fill(" + coloreCodice + ", 180); osCanvas.rect(0, " + i * lineDistance + ", " + finder.sketchWidth + ", " + lineDistance + ");" + righe[i];
      }
    }

    if (type) { 
      righe[i] = righe[i] + "osCanvas.fill(0); osCanvas.text(" + '"' + substring + '"' + ", 0, " + ((i+1) * lineDistance - lineDistance / 4) + "); osCanvas.fill(" + coloreCodice + ", 180); osCanvas.rect(0, " + i * lineDistance + ", " + finder.sketchWidth + ", " + lineDistance + ");";
    }
  }  

  // aggiunge in fondo allo sketch le funzioni per gestire la scala e lo switch del canvas
  if (!finder.findKeyPressed(righe)) // se nel codice non era gi\u00e0 presente keyPressed()
    righe[righe.length-1] = righe[righe.length-1] + "void keyPressed() { if (showCanvas) { if (key == '+') { scalaCanvas = true; scala = 1.05;} if (key == '-') { scalaCanvas = true; scala = 0.95;}} if (!showCanvas) canvasSwitch = false; else { canvasSwitch = true;} if (key == '0') { deltaX = 0; deltaY = 0;}}";

  finder.findKeyReleased(righe); // gestisce la visualizzazione di keyReleased()

  finder.findMousePressed(righe); // gestisce la visualizzazione di mousePressed()
  finder.findMouseReleased(righe); // gestisce la visualizzazione di mouseReleased()

  finder.findMouseMoved(righe); // gestisce la visualizzazione di mouseMoved()
}

public void changeAppIcon(PImage img) {
  final PGraphics pg = createGraphics(16, 16, JAVA2D);

  pg.beginDraw();
  pg.image(img, 0, 0, 16, 16);
  pg.endDraw();

  frame.setIconImage(pg.image);
}

/* 
Copyright (C) 2014 Carlo Farina 
The program is a semi-free software. 
The license terms are specified in the License file.
*/






class CodeManager {

  String pastedLines; 
  String pastedCode = "";
  String codice [] = new String [1];

  float skipTime; // gestisce la pressione ripetuta del tasto di skip
  float skipDelay = 0.5f;

  public void run() {
    pasteCode();     
    codice[0] = pastedCode;
    saveStrings(codeFileName, codice);
  }

  public void pasteCode() {

    keyPressed(); 

    key = '/'; // risolve bug Ctrl+v

    if (codeIndex > 4) { // se l'ultente ha terminato la compilazione 
      compilationCompleted = true;
      if (pastedCode != "") // se l'utente ha incollato almeno una delle componenti del codice
        codeCreated = true;
    }
  }

  public void keyPressed() {

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

  public String GetTextFromClipboard() {

    String text = (String) GetFromClipboard(DataFlavor.stringFlavor);
    return text;
  }

  public Object GetFromClipboard(DataFlavor flavor) {
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

/* 
Copyright (C) 2014 Carlo Farina 
The program is a semi-free software. 
The license terms are specified in the License file. 
*/

class Finder {

  int rigaInizioSetup = 0;
  int rigaFineSetup = 0;
  int rigaInizioDraw = 0;
  int rigaFineDraw = 0;
  int rigaInizioClassi = 0;

  int sketchWidth = 0;
  int sketchHeight = 0;

  public void findFunctions(String [] righe) {

    for (int i = 0; i < righe.length; i++) {
      if (righe[i].indexOf("void setup()")>= 0) // cerca riga inizio setup()
        rigaInizioSetup = i;
    }
    for (int i = rigaInizioSetup + 1; i < righe.length; i++) {
      if (righe[i].indexOf("}")== 0) { // cerca riga fine setup()
        rigaFineSetup = i;
        break;
      }
    }

    for (int i = rigaFineSetup + 1; i < righe.length; i++) {
      if (righe[i].indexOf("void draw()")>= 0) // cerca riga inizio draw()
        rigaInizioDraw = i;
    }
    for (int i = rigaInizioDraw + 1; i < righe.length; i++) {
      if (righe[i].indexOf("}")== 0) { // cerca riga fine draw()
        rigaFineDraw = i;
        break;
      }
    }
  }

  public void findClasses(String [] righe) {
    for (int i = finder.rigaFineDraw + 1; i < righe.length; i++) { 
      if (righe[i].indexOf("class")>= 0) { 
        rigaInizioClassi = i;
        break;
      } else {
        rigaInizioClassi = righe.length;
      }
    }
  }

  public boolean findKeyPressed(String [] righe) {

    boolean keyPressedFunction = false;

    for (int i = 0; i < righe.length; i++) {
      if (righe[i].indexOf("keyPressed(")>= 0 || righe[i].indexOf("keyPressed (")>= 0) {   
        righe[i] = "void keyPressedFunction() { if (keySwitch) keySwitch = false; if (showCanvas) { if (key == '+') { scalaCanvas = true; scala = 1.05;} if (key == '-') { scalaCanvas = true; scala = 0.95;}} if (!showCanvas) canvasSwitch = false; else { canvasSwitch = true;} if (key == '0') { deltaX = 0; deltaY = 0;}";        
        keyPressedFunction = true; 
        break;
      }
    }

    if (!keyPressedFunction) // se non trova la funzione keyPressed() crea void keyPressedFunction() { if (keySwitch) keySwitch = false;} per la chiamata del draw
      righe[righe.length - 1] = righe[righe.length - 1] + "void keyPressedFunction() { if (keySwitch) keySwitch = false;}";

    return keyPressedFunction;
  }

  public void findKeyReleased(String [] righe) {

    boolean keyReleasedFunction = false;

    for (int i = 0; i < righe.length; i++) {
      if (righe[i].indexOf("keyReleased(")>= 0 || righe[i].indexOf("keyReleased (")>= 0) {   
        righe[i] = "void keyReleasedFunction() { if (!keySwitch) keySwitch = true;";        
        keyReleasedFunction = true; 
        break;
      }
    }

    if (!keyReleasedFunction) // se non trova la funzione keyReleased() crea void keyReleasedFunction() { /* empy function */ } per la chiamata del draw
      righe[righe.length - 1] = righe[righe.length - 1] + "void keyReleasedFunction() {" + '\n' + "/* empy function */ }";
  }

  public boolean findMousePressed(String [] righe) {

    boolean mousePressedFunction = false;

    for (int i = 0; i < righe.length; i++) {
      if (righe[i].indexOf("mousePressed(")>= 0 || righe[i].indexOf("mousePressed (")>= 0) {   
        righe[i] = "void mousePressedFunction() { if (mouseSwitch) mouseSwitch = false; ";        
        mousePressedFunction = true; 
        break;
      }
    }

    if (!mousePressedFunction) // se non trova la funzione mousePressed() crea void mousePressedFunction() { if (mouseSwitch) mouseSwitch = false;} per la chiamata del draw
      righe[righe.length - 1] = righe[righe.length - 1] + "void mousePressedFunction() { if (mouseSwitch) mouseSwitch = false;}";

    return mousePressedFunction;
  }

  public void findMouseReleased(String [] righe) {

    boolean mouseReleasedFunction = false;

    for (int i = 0; i < righe.length; i++) {
      if (righe[i].indexOf("mouseReleased(")>= 0 || righe[i].indexOf("mouseReleased (")>= 0) {   
        righe[i] = "void mouseReleasedFunction() { if (!mouseSwitch) mouseSwitch = true;";        
        mouseReleasedFunction = true; 
        break;
      }
    }

    if (!mouseReleasedFunction) // se non trova la funzione mouseReleased() crea void mouseReleasedFunction() { /* empy function */ } per la chiamata del draw
      righe[righe.length - 1] = righe[righe.length - 1] + "void mouseReleasedFunction() {" + '\n' + "/* empy function */ }";
  }
  
    public void findMouseMoved(String [] righe) {

    boolean mouseMovedFunction = false;

    for (int i = 0; i < righe.length; i++) {
      if (righe[i].indexOf("mouseMoved(")>= 0 || righe[i].indexOf("mouseMoved (")>= 0) {   
        righe[i] = "void mouseMovedFunction() { ";        
        mouseMovedFunction = true; 
        break;
      }
    }

    if (!mouseMovedFunction) // se non trova la funzione mouseMoved() crea void mouseMovedFunction() { /* empy function */ } per la chiamata del draw
      righe[righe.length - 1] = righe[righe.length - 1] + "void mouseMovedFunction() {" + '\n' + "/* empy function */ }";
  }

  public void setSize(String [] righe) {

    String widthString = "";
    String heightString = "";

    int rigaSize = 0;
    int sizeLastNum = 0;

    boolean fillWidth = true; 
    boolean fillHeight = false;

    for (int i = 0; i < righe.length; i++) {
      if (righe[i].indexOf("size")>= 0) { // cerca riga size sketch
        rigaSize = i; 
        break;
      }
    }

    if (righe[rigaSize].indexOf("width")>=0 || righe[rigaSize].indexOf("height")>=0) { // se nel size ci sono width e height
      sketchWidth = width;
      sketchHeight = height;
    } else {
      if (righe[rigaSize].indexOf("displayWidth")>=0 || righe[rigaSize].indexOf("displayHeight")>=0) { // se nel size ci sono displayWidth e displayHeight 
        sketchWidth = displayWidth;
        sketchHeight = displayHeight;
      } else {                                           // se nel size ci sono valori numerici
        if (righe[rigaSize].indexOf ("P")>= 0)           // se trovo un renderer nel size (es. P3D, PDF, ...) interrompo prima la lettura
          sizeLastNum = righe[rigaSize].indexOf ("P");
        else {
          sizeLastNum = righe[rigaSize].length();
        }
        for (int i = righe[rigaSize].indexOf ("(")+1; i < sizeLastNum; i++) { // memorizza i valori del size in due stringhe
          if (righe[rigaSize].charAt(i) == '0' || righe[rigaSize].charAt(i) == '1' || righe[rigaSize].charAt(i) == '2' || righe[rigaSize].charAt(i) == '3' || righe[rigaSize].charAt(i) == '4' || righe[rigaSize].charAt(i) == '5' || righe[rigaSize].charAt(i) == '6' || righe[rigaSize].charAt(i) == '7' || righe[rigaSize].charAt(i) == '8' || righe[rigaSize].charAt(i) == '9') {
            if (fillWidth)
              widthString = widthString + righe[rigaSize].charAt(i);
            if (fillHeight)
              heightString = heightString + righe[rigaSize].charAt(i);
          } else {
            fillWidth = false;
            fillHeight = true;
          }
        }

        sketchWidth = parseInt(widthString);
        sketchHeight = parseInt(heightString);
      }
    }
  }

  public void fixComments(String [] righe) {

    String backupCommento = "";

    int ultimaRigaCommento = -1;

    boolean type = false;

    // cancella commento /* */ sulla stessa riga di un'istruzione

      for (int i = 0; i < righe.length; i++) {
      if (righe[i].indexOf("/*")>= 0) 
        for (int j = 0; j < righe[i].indexOf ("/*"); j++) {
          if (righe[i].charAt(j) != ' ') {
            backupCommento = backupCommento + righe[i].charAt(j);
            type = true;
          }
        }
      if (type) {
        righe[i] = backupCommento;
        type = false;
      }

      backupCommento = "";
    }

    // cancella commento // sulla stessa riga di un'istruzione

    for (int i = 0; i < righe.length; i++) {
      if (righe[i].indexOf("//")>= 0) 
        for (int j = 0; j < righe[i].indexOf ("//"); j++) {
          if (righe[i].charAt(j) != ' ') {
            backupCommento = backupCommento + righe[i].charAt(j);
            type = true;
          }
        }
      if (type) {
        righe[i] = backupCommento;
        type = false;
      }

      backupCommento = "";
    }

    // fix commenti su pi\u00f9 righe

    for (int i = 0; i < righe.length; i++) { 
      if (righe[i].indexOf("/*")>= 0 && i!= ultimaRigaCommento) {
        righe[i] = "//" + righe[i];
        for (int j = i+1; j < righe.length; j++) { 
          if (righe[j].indexOf("*/") == -1) { // se non trovo "*/" nella riga
            righe[j] = "//" + righe[j];
          } else {
            righe[j] = "//" + righe[j] + "/*"; 
            ultimaRigaCommento = j;
            break;
          }
        }
      }
    }

    // fix commento su una riga

    for (int i = 0; i < righe.length; i++) { 
      if (righe[i].indexOf("//")>= 0)
        righe[i] = "/*" + righe[i] + "*/";
    }

    // fix virgolette nei commenti

    for (int i = 0; i < righe.length; i++) { 
      if (righe[i].indexOf("/*")>= 0 || righe[i].indexOf("*/")>= 0) { // se \u00e8 una riga di commento
        backupCommento = righe[i];
        righe[i] = "";
        for (int j = 0; j < backupCommento.length (); j++) {
          if (backupCommento.charAt(j) != '"')
            righe[i] = righe[i] + backupCommento.charAt(j);
        }
      }
    }

    saveStrings(codeFileName, righe);
  }

  public String fixText(String [] righe, int rawIndex) {

    String substring = "";

    if (righe[rawIndex].indexOf("text")>= 0 || righe[rawIndex].indexOf("print")>= 0 || righe[rawIndex].indexOf("load")>= 0 || righe[rawIndex].indexOf("create")>= 0 || righe[rawIndex].indexOf("save")>= 0) {
      for (int i = 0; i < righe[rawIndex].length (); i++) {
        if (righe[rawIndex].charAt(i) != '"')
          substring = substring + righe[rawIndex].charAt(i);
      }
    } else {
      substring = righe[rawIndex];
    }

    return substring;
  }

  public void fixParenthesis(String [] righe) {
    for (int i = 0; i < righe.length; i++) {
      if ((righe[i].indexOf("if(")>= 0 || righe[i].indexOf("if (")>= 0 || righe[i].indexOf("for(")>= 0 || righe[i].indexOf("for (")>= 0) && righe[i].indexOf("{")== -1) { // se c'\u00e8 un if/for senza aperta parentesi
        righe[i] = righe[i] + " {"; 
        righe[i+1] = righe[i+1] + " }"; 
        i++;
      }
    }

    saveStrings(codeFileName, righe);
  }
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "VisualCode" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
