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

  void findFunctions(String [] righe) {

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

  void findClasses(String [] righe) {
    for (int i = finder.rigaFineDraw + 1; i < righe.length; i++) { 
      if (righe[i].indexOf("class")>= 0) { 
        rigaInizioClassi = i;
        break;
      } else {
        rigaInizioClassi = righe.length;
      }
    }
  }

  boolean findKeyPressed(String [] righe) {

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

  void findKeyReleased(String [] righe) {

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

  boolean findMousePressed(String [] righe) {

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

  void findMouseReleased(String [] righe) {

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
  
    void findMouseMoved(String [] righe) {

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

  void setSize(String [] righe) {

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

  void fixComments(String [] righe) {

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

    // fix commenti su più righe

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
      if (righe[i].indexOf("/*")>= 0 || righe[i].indexOf("*/")>= 0) { // se è una riga di commento
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

  String fixText(String [] righe, int rawIndex) {

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

  void fixParenthesis(String [] righe) {
    for (int i = 0; i < righe.length; i++) {
      if ((righe[i].indexOf("if(")>= 0 || righe[i].indexOf("if (")>= 0 || righe[i].indexOf("for(")>= 0 || righe[i].indexOf("for (")>= 0) && righe[i].indexOf("{")== -1) { // se c'è un if/for senza aperta parentesi
        righe[i] = righe[i] + " {"; 
        righe[i+1] = righe[i+1] + " }"; 
        i++;
      }
    }

    saveStrings(codeFileName, righe);
  }
}

