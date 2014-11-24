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

color coloreCodice;

void setup() {  
  size(150, 150);

  changeAppIcon( loadImage(ICON) );

  textAlign(CENTER, CENTER);
  textSize(14);
}

void draw() {

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
    if (codeCreated)                                            // se il codice incollato dall'utente NON è vuoto
      text("is ready", width / 2, height / 2 + height / 11);
    if (!codeCreated)                                           // se il codice incollato dall'utente è vuoto
      text("is empty", width / 2, height / 2 + height / 11);
  }

  if (codeCreated) { // se il codice incollato dall'utente NON è vuoto ne elabora il contenuto
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

void disegnaSetup(Finder finder, String [] righe) {

  String substring = "";

  for (int i = finder.rigaInizioSetup; i <= finder.rigaFineSetup; i++) { 

    substring = finder.fixText(righe, i);

    coloreCodice = color(#B5FF0D); 

    if (i == finder.rigaInizioSetup)   
      righe[i] = righe[i] + "osCanvas = createGraphics(" + finder.sketchWidth * 7 + ", " + finder.sketchHeight * 7 + "); osCanvas.beginDraw(); osCanvas.noStroke(); osCanvas.fill(0); osCanvas.textSize(" + finder.sketchHeight / righe.length * 0.9 + "); osCanvas.text(" + '"' + substring + '"' + ", 0, " + ((i+1) * lineDistance - lineDistance / 4) + "); osCanvas.fill(" + coloreCodice + ", 180); osCanvas.rect(0, " + i * lineDistance + ", " + finder.sketchWidth + ", " + lineDistance + ");";

    if (i > finder.rigaInizioSetup && i < finder.rigaFineSetup) 
      righe[i] = righe[i] + "osCanvas.fill(0); osCanvas.text(" + '"' + substring + '"' + ", 0, " + ((i+1) * lineDistance - lineDistance / 4) + "); osCanvas.fill(" + coloreCodice + ", 180); osCanvas.rect(0, " + i * lineDistance + ", " + finder.sketchWidth + ", " + lineDistance + ");";

    if (i == finder.rigaFineSetup)
      righe[i] = "osCanvas.fill(0); osCanvas.text(" + '"' + righe[i] + '"' + ", 0, " + ((i+1) * lineDistance - lineDistance / 4) + "); osCanvas.fill(" + coloreCodice + ", 180); osCanvas.rect(0, " + i * lineDistance + ", " + finder.sketchWidth + ", " + lineDistance + "); " + righe[i];
  }
}

void disegnaDraw(Finder finder, String [] righe) { 

  String substring = "";

  for (int i = finder.rigaInizioDraw; i <= finder.rigaFineDraw; i++) {

    substring = finder.fixText(righe, i);

    coloreCodice = color(#B5FF0D);

    if (i == finder.rigaInizioDraw)
      righe[i] = righe[i] + "if (!canvasSwitch && (key == 's' || key == 'S')) showCanvas = true; if (canvasSwitch && (key == 'h' || key == 'H')) showCanvas = false; if (showCanvas && mousePressed) { deltaX = deltaX + mouseX - pmouseX; deltaY = deltaY + mouseY - pmouseY;} translate(deltaX, deltaY); if (!showCanvas) translate(-deltaX, -deltaY); if ((key == '+'  || key == '-') && scalaCanvas) { osCanvas.scale(scala); scalaCanvas = false;} pushMatrix(); osCanvas.background(" + 255 + "); osCanvas.fill(0); osCanvas.text(" + '"' + substring + '"' + ", 0, " + ((i+1) * lineDistance - lineDistance / 4) + "); osCanvas.fill(" + coloreCodice + ", 180); osCanvas.rect(0, " + i * lineDistance + ", " + finder.sketchWidth + ", " + lineDistance + ");";

    if (i > finder.rigaInizioDraw && i < finder.rigaFineDraw) 
      righe[i] = righe[i] + "osCanvas.fill(0); osCanvas.text(" + '"' + substring + '"' + ", 0, " + ((i+1) * lineDistance - lineDistance / 4) + "); osCanvas.fill(" + coloreCodice + ", 180); osCanvas.rect(0, " + i * lineDistance + ", " + finder.sketchWidth + ", " + lineDistance + ");";

    if (i == finder.rigaFineDraw)
      righe[i] = "osCanvas.fill(0); osCanvas.text(" + '"' + righe[i] + '"' + ", 0, " + ((i+1) * lineDistance - lineDistance / 4) + "); osCanvas.fill(" + coloreCodice + ", 180); osCanvas.rect(0, " + i * lineDistance + ", " + finder.sketchWidth + ", " + lineDistance + "); if (keyPressed) keyPressedFunction(); if (!keyPressed && !keySwitch) keyReleasedFunction(); if (mousePressed) mousePressedFunction(); if (!mousePressed && !mouseSwitch) mouseReleasedFunction(); if (mouseX != pmouseX && mouseY != pmouseY) mouseMovedFunction(); osCanvas.endDraw(); popMatrix(); if (showCanvas) { background(#FFFFFF); translate(0, 0, 1); image(osCanvas, 0, 0);} " + righe[i]; // translate sulla Z per posizionare l'osCanvas più avanti rispetto al canvas principale
  }
}

void disegnaFunzioni(Finder finder, String [] righe) {

  String substring = "";

  int endFunctionCharIndex = 0;

  boolean type = false;

  for (int i = finder.rigaFineDraw + 1; i < righe.length; i++) {

    substring = finder.fixText(righe, i);

    coloreCodice = color(#B5FF0D);

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
  if (!finder.findKeyPressed(righe)) // se nel codice non era già presente keyPressed()
    righe[righe.length-1] = righe[righe.length-1] + "void keyPressed() { if (showCanvas) { if (key == '+') { scalaCanvas = true; scala = 1.05;} if (key == '-') { scalaCanvas = true; scala = 0.95;}} if (!showCanvas) canvasSwitch = false; else { canvasSwitch = true;} if (key == '0') { deltaX = 0; deltaY = 0;}}";

  finder.findKeyReleased(righe); // gestisce la visualizzazione di keyReleased()

  finder.findMousePressed(righe); // gestisce la visualizzazione di mousePressed()
  finder.findMouseReleased(righe); // gestisce la visualizzazione di mouseReleased()

  finder.findMouseMoved(righe); // gestisce la visualizzazione di mouseMoved()
}

void changeAppIcon(PImage img) {
  final PGraphics pg = createGraphics(16, 16, JAVA2D);

  pg.beginDraw();
  pg.image(img, 0, 0, 16, 16);
  pg.endDraw();

  frame.setIconImage(pg.image);
}

