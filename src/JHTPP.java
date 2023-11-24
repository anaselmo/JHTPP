/**------------------------------------------------------------------------
 * ?                        Java Hypertext Preproccesor
 * ?                               (JHTPP Class)
 * @author         :  anaselmo & YarasAtomic
 * @repo           :  github.com/anaselmo/JHTPP
 * @createdOn      :  15/11/2023
 * @description    :  Really basic Java Hypertext Preproccesor (JHTPP)
 *                    Read 'README.md' to further information
 *------------------------------------------------------------------------**/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;

public class JHTPP {
    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//
    //!---------------------- CLASS ATTRIBUTES -----------------------!//
    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//

    //*---------------------------------------------------------------*//
    //*--------------- CONSTANTS - REGULAR EXPRESSIONS ---------------*//
    //*---------------------------------------------------------------*//

    private static final String REGEX_NAME = 
        "[[a-z][A-Z0-9]*]+";
    private static final String REGEX_DATA = 
        REGEX_NAME+"["+"\\."+REGEX_NAME+"]*";

    //-----------------------------------------------------------------//

    /** We define the regular expression: 
    *! {{var}}
    *
    ** Group 1 -> var
    * TODO: Change '.*?' with 'REGEX_DATA', don't know why it doesn't work
    */
    private static final String REGEXP_VAR =  
        "\\{\\{\\s*("+".*?"+")\\s*\\}\\}";                      // Group 1

    //-----------------------------------------------------------------//

    /** We define the regular expression: 
    *! {% for x in array %} (...) {% endfor x %}
    *
    ** Group 2 -> first 'x'
    ** Group 3 -> 'array'
    ** Group 4 -> '(...)', i.e., the loop body
    ** Group 5 -> last 'x'  //TODO: We will eventually remove this, unnecessary
    */
    private static final String REGEXP_FOR = 
        "\\{\\%\\s*for\\s+("+REGEX_DATA+")\\s+"+                // Group 2
        ////"in\\s+([a-z][a-zA-Z0-9\\.]*)\\s*\\%\\}"+               // Group 3
        "in\\s+("+REGEX_DATA+"\\.*"+REGEX_DATA+")\\s*\\%\\}"+   // Group 3
        "[\\n\\t\\s]*(.+)[\\n\\t\\s]*"+                         // Group 4
        "\\{\\%\\s*endfor\\s*([[a-z][A-Z0-9]*]+)\\s*\\%\\}";    // Group 5

    //-----------------------------------------------------------------//
    
    /** We define the regular expression: 
    *! {% if cond %} (...) {% endif cond %}
    *
    ** Group 6 -> first 'cond'
    ** Group 7 -> '(...)', i.e., the if body
    ** Group 8 -> last 'cond'
    * TODO: We'll eventually remove group 8, unnecessary
    */
    private static final String REGEXP_IF  =  
        "\\{\\%\\s*if\\s+("+REGEX_NAME+")\\s*\\%\\}"+           // Group 6
        "[\\n*\\t*\\s*]*(.+)[\\n*\\t*\\s*]*?"+                  // Group 7
        "\\{\\%\\s*endif\\s*("+REGEX_NAME+")\\s*\\%\\}";        // Group 8 

    //*---------------------------------------------------------------*//
    //*-------------------------- VARIABLES --------------------------*//
    //*---------------------------------------------------------------*//
    
    private String text;
    private VarTree tree;

    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//
    //!------------------------ CLASS METHODS ------------------------!//
    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//

    //*---------------------------------------------------------------*//
    //*--------------------------- CONSTRUCTOR -----------------------*//
    //*---------------------------------------------------------------*//

    /**
     ** Constructor of the JHTPP class
     *
     * @param type  'InputType.PATH' or 'InputType.CONTENT' depending of str 
     * @param str   The path of the file or the file content
     * @param tree  The tree where the variables are stored
     * @throws IOException
     */
    public JHTPP(InputType type, String str, VarTree tree) throws IOException  {
        switch (type){
            case PATH:      this.text = pathToString(str);  break;
            case CONTENT:   this.text = str;                break;
        }
        this.tree = tree;
    }

    //*---------------------------------------------------------------*//
    //*------------------------ PATH TO STRING -----------------------*//
    //*---------------------------------------------------------------*//

    /**
     ** Creates a string with the content of the file in the path 
     ** given as a parameter
     *
     * @param path File path (ej: /path/to/file.html)
     * @return The file in String format
     * @throws IOException
     */
    private String pathToString(String path) throws IOException {
        StringBuilder output_string = new StringBuilder();
        String line;

        try (FileReader file_reader = new FileReader(path);
            BufferedReader buffered_reader = new BufferedReader(file_reader)) {
            while ((line = buffered_reader.readLine()) != null) {
                output_string.append(line).append("\n");
            }
        } catch (IOException err) { err.printStackTrace(); }

        return output_string.toString();
    }

    //*---------------------------------------------------------------*//
    //*------------------------- READ METHODS ------------------------*//
    //*---------------------------------------------------------------*//

    // // private VarTree finalTree(String var) {
    // //     String[] parts = var.split("\\.");
    // //     VarTree final_tree = this.tree;
    // //     int depth = 0;
        
    // //     while (depth < parts.length-1) {
    // //         final_tree = final_tree.get(parts[depth++]);
    // //     }
    // //     return final_tree;
    // // }
    /**
     ** Reads a String variable of the Hyper Text stored in the tree.
     ** Used when we handle the 'var' structure, it is the value of the var.
     *
     * @example In tree --> [var] = String <---> [name]="Guillermo".
     * or --> [days.0.name] = "Monday"
     * @param var Name of the variable ('key' in the VarTree)
     * @return A String with the corresponding value (stored in the tree)
     */
    private String readVar(String var) {
        String[] parts = var.split("\\.");
        VarTree final_tree = this.tree;
        int depth = 0;

        while (depth < parts.length-1) {
            final_tree = final_tree.get(parts[depth++]);
        }
        return final_tree.getString(parts[depth]);
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    /**
     ** Reads a variable of the Hyper Text stored in the tree.
     ** Used when we handle the 'for' structure, it is the array of values.
     *
     * @example In tree --> [var] = VarTree <---> [days]=(VarTree) days.
     * or --> [days.0.exercises] = (VarTree) monday_exercises
     * @param var Name of the variable ('key' in the VarTree)
     * @return A VarTree with the corresponding value (stored in the tree)
     */
    private VarTree readTree(String var) {
        String[] parts = var.split("\\.");
        VarTree final_tree = this.tree;
        int depth = 0;

        while (depth < parts.length-1) {
            final_tree = final_tree.get(parts[depth++]);
        }
        return final_tree.get(parts[depth]);
    }

    //*---------------------------------------------------------------*//
    //*--------------------------- HANDLERS --------------------------*//
    //*---------------------------------------------------------------*//

    /**
     ** Handles the regular expressions of the type: {{var}}
     *
     * @param matcher Stores the matches of the regular expressions
     * given in this.text
     * @return Returns the processed variable (if exists)
     */
    private String handleVar(Matcher matcher) {
        String varName = matcher.group(1);
        String varValue = readVar(varName);

        return varValue != null ? varValue : "{{" + varName + "}}";
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    /**
     ** Handles the regular expressions of the type:
     ** {% for x in array %} (...) {% endfor x %}
     *
     * @param matcher Stores the matches of the regular expressions
     * given in this.text
     * @return Returns the processed loop (if exists)
     * @throws IOException
     * ?: Maybe another return when ids are not equal
     * TODO: Delete the 'id' and manage nested loops differently
     */
    private String handleFor(Matcher matcher) throws IOException {
        String var = matcher.group(2);
        String id = matcher.group(5);
        String body = matcher.group(4); //.trim()
        if (!var.equals(id)) return body; //TODO

        StringBuilder output = new StringBuilder();
        String arrayName = matcher.group(3);
        VarTree values = readTree(arrayName);
        
        for (Map.Entry<String, VarTree> key : values) {
            VarTree subtree = key.getValue();
            this.tree.put(var, subtree);
            JHTPP tp = new JHTPP(InputType.CONTENT, body, this.tree);
            output.append(tp.processText());
        }
        return output.toString();
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    /**
     ** Handles the regular expressions of the type:
     ** {% if cond %} (...) {% endif cond %}
     *
     * @param matcher Stores the matches of the regular expressions
     * given in this.text
     * @return Returns the processed if
     * @throws IOException
     * TODO: 'cond' needs to be analized & group(8) needs to be deleted
     * ! NOT FINISHED, NEEDS TO BE DONE
     */
    private String handleIf(Matcher matcher) throws IOException {
        String condition = matcher.group(6);
        String id = matcher.group(8);
        if (!condition.equals(id))  return ""; //TODO

        String body = matcher.group(7); //.trim(), maybe not necessary (?)
        JHTPP tp = new JHTPP(InputType.CONTENT, body, this.tree);
        
        return tp.processText();
    }

    //*---------------------------------------------------------------*//
    //*------------------------ TEXT PROCESSOR -----------------------*//
    //*---------------------------------------------------------------*//

    /**
     ** Preprocesses Hyper Text
     *
     * @param input Text we are going to process
     * @return The processed text
     * @throws IOException
     */
    private String processText(String input) throws IOException {
        // Pattern.DOTALL, means that '.' can be anything (including '\n')
        Pattern pattern = Pattern.compile(REGEXP_VAR + "|" + REGEXP_FOR +
                                          "|" + REGEXP_IF, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);
        StringBuilder output = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            // We add to the output from the beginning of the text  
            // until we find a reg. exp.
            output.append(input, lastEnd, matcher.start());

            boolean varCondition =  matcher.group(1) != null;
            if (varCondition) {
                output.append(handleVar(matcher));
            }
            boolean forCondition =  matcher.group(2) != null && 
                                    matcher.group(3) != null && 
                                    matcher.group(4) != null && 
                                    matcher.group(5) != null;
            if (forCondition) {
                output.append(handleFor(matcher));
            }
            boolean ifCondition  =  matcher.group(6) != null && 
                                    matcher.group(7) != null && 
                                    matcher.group(8) != null;
            if (ifCondition) {
                output.append(handleIf(matcher));
            }

            // We update the end with the end of the found reg. exp. 
            lastEnd = matcher.end();
        }
        // When all matches are handled, we add the last part of the
        // text to the output String (from 'lastEnd' to the 'EOS')
        output.append(input.substring(lastEnd));
        return output.toString();
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//
    
    /**
     ** Method the user will call to process the text given in the 
     ** constructor. It triggers the private method String processText(String).
     * 
     * @return Processed text (original text given in constructor)
     * @throws IOException
     */
    
    public String processText() throws IOException  { 
        return processText(this.text); 
    }

    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//
    //!------------------------- END OF JHTPP ------------------------!//
    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//
}