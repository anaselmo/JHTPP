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

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//
    //-------------------- ATRIBUTOS DE LA CLASE ----------------------//
    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    //                                                                 //
    //-----------------------------------------------------------------//
    //----------------- CONSTANTES - EXPRESIÓN REGULAR ----------------//
    //-----------------------------------------------------------------//
    //                                                                 //

    // Definimos la expresión regular:
    // Grupo 1: {{var}} -> var
    final private String REG_EXP_VAR =  "\\{\\{\\s*([^\\s]+)\\s*\\}\\}"; // Group 1

    // Grupo 2: {% for x in array %} (...) {% endfor x %} -> primera 'x'
    // Grupo 3: {% for x in array %} (...) {% endfor x %} -> 'array'
    // Grupo 4: {% for x in array %} (...) {% endfor x %} -> '(...)', es decir, el contenido del bucle
    // Grupo 5: {% for x in array %} (...) {% endfor x %} -> última 'x'
    final private String REG_EXP_FOR = "\\{\\%\\s*for\\s+([[a-z][A-Z0-9]*]+)\\s+in\\s+([a-z][a-zA-Z0-9\\.]*)\\s*\\%\\}"+ // Group 2 y 3
                                        "[\\n\\t\\s]*(.+)[\\n\\t\\s]*"+ // Group 4
                                        "\\{\\%\\s*endfor\\s*([[a-z][A-Z0-9]*]+)\\s*\\%\\}"; //Group 5
    
    // Grupo 6: {% if cond %} (...) {% endif cond %} -> primera 'cond'
    // Grupo 7: {% if cond %} (...) {% endif cond %} -> '(...)', es decir, el contenido del if
    // Grupo 8: {% if cond %} (...) {% endif cond %} -> última 'cond'
    final private String REG_EXP_IF  =  "\\{\\%\\s*if\\s+([[a-z][A-Z0-9]*]+)\\s*\\%\\}"+ // Group 6
                                        "[\\n*\\t*\\s*]*(.+)[\\n*\\t*\\s*]*?"+ // Group 7
                                        "\\{\\%\\s*endif\\s*([[a-z][A-Z0-9]*]+)\\s*\\%\\}[\\n]"; // Group 8 

    //                                                                 //
    //-----------------------------------------------------------------//
    //--------------------------- VARIABLES ---------------------------//
    //-----------------------------------------------------------------//
    //                                                                 //
    private String text;
    private VarTree tree;

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//
    //--------------------- MÉTODOS DE LA CLASE -----------------------//
    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    //                                                                 //
    //-----------------------------------------------------------------//
    //--------------------------- CONSTRUCTOR -------------------------//
    //-----------------------------------------------------------------//
    //                                                                 //

    /**
     * @brief Constructor de la clase JHTPP
     * @param type 
     * @param s     
     * @param tree  
     * @throws IOException
     */
    public JHTPP(InputType type, String str, VarTree tree) throws IOException  {
        switch (type){
            case PATH:
                this.text = pathToString(str);
                break;
            case CONTENT:
                this.text = str;
        }
        this.tree = tree;
    }

    //                                                                 //
    //-----------------------------------------------------------------//
    //------------------------- PATH TO STRING ------------------------//
    //-----------------------------------------------------------------//
    //                                                                 //

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

    //                                                                 //
    //-----------------------------------------------------------------//
    //-------------------------- READ METHODS -------------------------//
    //-----------------------------------------------------------------//
    //                                                                 //

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

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

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

    //                                                                 //
    //-----------------------------------------------------------------//
    //---------------------------- HANDLERS ---------------------------//
    //-----------------------------------------------------------------//
    //                                                                 //

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
            //output.append("b");
            output.append(varValue);
            //output.append("e");
        } else {
            output.append("{{").append(varName).append("}}");
        }
        return output.toString();
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    /**
     * Maneja las expresiones regulares de tipo 
     * {% for x in array %} (...) {% endfor x %}
     * @param matcher
     * @return Devuelve el contenido del bucle procesado
     * @throws IOException
     */
    private String handleFor(Matcher matcher) throws IOException {
        StringBuilder output = new StringBuilder();
        String var = matcher.group(2);
        String arrayName = matcher.group(3);
        VarTree values = readTree(arrayName);
        String body = matcher.group(4); //.trim()
        String id = matcher.group(5);
        
        if (var.equals(id)) {
            Iterator<Map.Entry<String, VarTree>> iterator = values.getIterator();
            while(iterator.hasNext()) {
                Map.Entry<String, VarTree> entry = iterator.next();
                String key = entry.getKey();
                VarTree subtree = values.get(key);
                this.tree.put(var, subtree);
                JHTPP tp = new JHTPP(InputType.CONTENT, body, this.tree);

                output.append(tp.processText());
            }
        }
        return output.toString();
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    private String handleIf(Matcher matcher) throws IOException {
        StringBuilder output = new StringBuilder();
        String condition = matcher.group(6);
        String body = matcher.group(7); //.trim()
        String id = matcher.group(8);

        if (!condition.equals(id))
            return "";

        JHTPP tp = new JHTPP(InputType.CONTENT, body, this.tree);
        output.append(tp.processText());
        
        return output.toString();
    }

    //                                                                 //
    //-----------------------------------------------------------------//
    //------------------------- TEXT PROCESSOR ------------------------//
    //-----------------------------------------------------------------//
    //                                                                 //

    /**
     * @brief Preprocesador de HyperText
     * @param input Texto a procesar
     * @return El texto procesado
     * @throws IOException
     */
    private String processText(String input) throws IOException {
        // PS: Pattern.DOTALL, hace que el '.' pueda ser cualquier cosa (incluyendo \n)
        Pattern pattern = Pattern.compile(REG_EXP_VAR + "|" + REG_EXP_FOR + "|" + REG_EXP_IF, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);
        StringBuilder output = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            // Añadimos al output desde el inicio del texto hasta que encontremos una exp. reg.
            output.append(input, lastEnd, matcher.start());

            boolean varCondition =  matcher.group(1) != null;
            // Si encontramos una del tipo {{var}}
            if (varCondition) {
                output.append(handleVar(matcher));
            }

            boolean forCondition =  matcher.group(2) != null && 
                                    matcher.group(3) != null && 
                                    matcher.group(4) != null && 
                                    matcher.group(5) != null;
            // Si encontramos una del tipo{% for d in days %} (...) {% endfor d %}
            if (forCondition) {
                output.append(handleFor(matcher));
            }

            boolean ifCondition  =  matcher.group(6) != null && 
                                    matcher.group(7) != null && 
                                    matcher.group(8) != null;
            // Si encontramos una del tipo {% if cond %} (...) {% endif cond %}
            if (ifCondition) {
                output.append(handleIf(matcher));
            }
            lastEnd = matcher.end();
        }
        output.append(input.substring(lastEnd));
        return output.toString();
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//
    
    /**
     * 
     * @return Processed text
     * @throws IOException
     */
    
    public String processText() throws IOException  { 
        return processText(this.text); 
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//
    //------------------------ FIN DE LA CLASE ------------------------//
    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//
}