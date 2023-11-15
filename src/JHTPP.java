/**------------------------------------------------------------------------
 * ?                        Java Hypertext Preproccesor
 * ?                               (JHTPP Class)
 * @author         :  anaselmo & YarasAtomic
 * @repo           :  
 * @createdOn      :  15/11/2023
 * @description    :  Really basic Java Hypertext Preproccesor (JHTPP)
 *------------------------------------------------------------------------**/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.Map;

public class JHTPP {
    // Definimos la expresión regular:
    // Grupo 1: {{var}} -> var
    // Grupo 2: {% for x in array %} (...) {% endfor x %} -> primera 'x'
    // Grupo 3: {% for x in array %} (...) {% endfor x %} -> 'array'
    // Grupo 4: {% for x in array %} (...) {% endfor x %} -> '(...)'', es decir, el contenido del bucle
    // Grupo 5: {% for x in array %} (...) {% endfor x %} -> última 'x'
    // PS: Pattern.DOTALL, hace que el '.' pueda ser cualquier cosa (incluyendo \n)
    final private String REG_EXP_VAR =  "\\{\\{\\s*([^\\s]+)\\s*\\}\\}"; // Group 1
    final private String REG_EXP_FOR =  "\\{\\%\\s*for\\s+([[a-z][A-Z0-9]*]+)\\s+in\\s+([a-z][a-zA-Z0-9\\.]*)\\s*\\%\\}"+ // Group 2 y 3
                                        "[[\\n]*[\\t]*[\\s]*]*?(.+)[[\\n]*[\\t]*[\\s]*]*?"+ // Group 4
                                        "\\{\\%\\s*endfor\\s*([[a-z][A-Z0-9]*]+)\\s*\\%\\}[\\n]"; //Group 5
    private String text;
    private VarTree tree;

    /**
     * @brief Constructor de la clase JHTPP
     * @param type 
     * @param s     
     * @param tree  
     * @throws IOException
     */
    public JHTPP(String type, String s, VarTree tree) throws IOException  {
        switch (type){
            case "path": case "Path": case "PATH":
            case "path_file": case "Path_file": case "PATH_FILE":
            case "pathfile":  case "Pathfile":  case "PATHFILE": case "PathFile": 
            case "file":      case "File":      case "FILE":
                this.text = pathToString(s);
                break;
            default:
                this.text = s;
        }
        this.tree = tree;
    }

    /**
     * @brief Crea un string con la información de la ruta pasada como parámetro
     * @param path Ruta del archivo (ej: /path/to/file.html)
     * @return Devuelve el archivo en formato String
     * @throws IOException
     */
    private String pathToString(String path) throws IOException {
        StringBuilder out_s = new StringBuilder();

        try (FileReader file_reader = new FileReader(path);
             BufferedReader buffered_reader = new BufferedReader(file_reader)) {
            String line;
            // Lee cada línea del archivo y agrega al StringBuilder
            while ((line = buffered_reader.readLine()) != null) {
                out_s.append(line).append("\n");
            }
        } catch (IOException e) { e.printStackTrace(); }

        return out_s.toString();
    }

    /**
     * @brief 
     * @param var
     * @return
     */
    private String readVar(String var) {
        String[] parts = var.split("\\.");
        if (parts.length<=1) return tree.getString(var);
        VarTree aux = this.tree;
        
        for (int i=0; i < parts.length-1; ++i) {
            aux = aux.get(parts[i]);
        }
        return aux.getString(parts[parts.length-1]);
    }

    /**
     * 
     * @param var
     * @return
     */
    private VarTree readTree(String var) {
        String[] parts = var.split("\\.");
        if (parts.length==0) return tree.get(var);
        VarTree aux = this.tree;

        for (int i=0; i < parts.length-1; ++i) {
            aux = aux.get(parts[i]);
        }
        return aux.get(parts[parts.length-1]);
    }

    /**
     * @brief Maneja las expresiones regulares de tipo {{var}}
     * @param matcher
     * @return Devuelve la variable procesada (si existe)
     */
    private String handleVar(Matcher matcher) {
        StringBuilder output = new StringBuilder();
        String varName = matcher.group(1);
        String varValue = readVar(varName);

        if (varValue != null) {
            output.append(varValue);
        } else {
            output.append("{{").append(varName).append("}}");
        }
        return output.toString();
    }

    /**
     * @brief Maneja las expresiones regulares de tipo {% for x in array %} (...) {% endfor x %}
     * @param matcher
     * @return Devuelve el contenido del bucle procesado
     * @throws IOException
     */
    private String handleFor(Matcher matcher) throws IOException {
        StringBuilder output = new StringBuilder();
        String loopVar = matcher.group(2);
        String loopArrayName = matcher.group(3);
        VarTree loopValues = readTree(loopArrayName);
        String loopBody = matcher.group(4);
        
        if (matcher.group(2).equals(matcher.group(5))) {
            Iterator<Map.Entry<String, VarTree>> iterator = loopValues.getIterator();
            while(iterator.hasNext()) {
                Map.Entry<String, VarTree> entry = iterator.next();
                String key = entry.getKey();
                VarTree subtree = loopValues.get(key);
                this.tree.put(loopVar, subtree);
                JHTPP tp = new JHTPP("Text", loopBody, this.tree);
                output.append(tp.processText());
            }
        }
        return output.toString();
    }

    
    public String processText() throws IOException  { 
        return processText(this.text); 
    }

    /**
     * @brief Preprocesador de HyperText
     * @param input Texto a procesar
     * @return El texto procesado
     * @throws IOException
     */
    public String processText(String input) throws IOException {
        Pattern pattern = Pattern.compile(REG_EXP_VAR + "|" + REG_EXP_FOR, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);
        StringBuilder output = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            // Añadimos al output desde el inicio del texto hasta que encontremos una exp. reg.
            output.append(input, lastEnd, matcher.start());

            // Si encontramos una del tipo {{var}}
            if (matcher.group(1) != null) {
                output.append(handleVar(matcher));
            }
            // Si encontramos una del tipo{% for d in days %} (...) {% endfor d %}
            if (matcher.group(2) != null && matcher.group(3) != null && matcher.group(4) != null && matcher.group(5) != null) {
                output.append(handleFor(matcher));
            }

            lastEnd = matcher.end();
        }
        output.append(input.substring(lastEnd));
        return output.toString();
    }
}