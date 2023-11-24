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
import java.util.Iterator;
import java.util.Map;

public class JHTPP {
    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//
    //----------------------- CLASS ATTRIBUTES ------------------------//
    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    //                                                                 //
    //-----------------------------------------------------------------//
    //---------------- CONSTANTS - REGULAR EXPRESSIONS ----------------//
    //-----------------------------------------------------------------//
    //                                                                 //

    // We define the regular expression: 
    // {{var}}
    //
    // Group 1 -> var
    final private String REG_EXP_VAR =  
        "\\{\\{\\s*([^\\s]+)\\s*\\}\\}";    // Group 1

    // We define the regular expression: 
    // {% for x in array %} (...) {% endfor x %}
    //
    // Group 2 -> first 'x'
    // Group 3 -> 'array'
    // Group 4 -> '(...)', i.e., the loop body
    // Group 5 -> last 'x'  //TODO: We will eventually remove this, unnecessary
    final private String REG_EXP_FOR = 
        "\\{\\%\\s*for\\s+([[a-z][A-Z0-9]*]+)\\s+"+             // Group 2
        "in\\s+([a-z][a-zA-Z0-9\\.]*)\\s*\\%\\}"+               // Group 3
        "[\\n\\t\\s]*(.+)[\\n\\t\\s]*"+                         // Group 4
        "\\{\\%\\s*endfor\\s*([[a-z][A-Z0-9]*]+)\\s*\\%\\}";    // Group 5
    
    // We define the regular expression: 
    // {% if cond %} (...) {% endif cond %}
    //
    // Group 6 -> first 'cond'
    // Group 7 -> '(...)', i.e., the if body
    // Group 8 -> last 'cond' //TODO: We will eventually remove this, unnecessary
    final private String REG_EXP_IF  =  
        "\\{\\%\\s*if\\s+([[a-z][A-Z0-9]*]+)\\s*\\%\\}"+        // Group 6
        "[\\n*\\t*\\s*]*(.+)[\\n*\\t*\\s*]*?"+                  // Group 7
        "\\{\\%\\s*endif\\s*([[a-z][A-Z0-9]*]+)\\s*\\%\\}";     // Group 8 

    //                                                                 //
    //-----------------------------------------------------------------//
    //--------------------------- VARIABLES ---------------------------//
    //-----------------------------------------------------------------//
    //                                                                 //
    
    private String text;
    private VarTree tree;

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//
    //------------------------ CLASS METHODS --------------------------//
    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    //                                                                 //
    //-----------------------------------------------------------------//
    //--------------------------- CONSTRUCTOR -------------------------//
    //-----------------------------------------------------------------//
    //                                                                 //

    /**
     * @brief Constructor of the JHTPP class
     * @param type  'InputType.PATH' or 'InputType.CONTENT' depending of str 
     * @param str   The path of the file or the file content
     * @param tree  The tree were the variables are stored
     * @throws IOException
     */
    public JHTPP(InputType type, String str, VarTree tree) throws IOException  {
        switch (type){
            case PATH:      this.text = pathToString(str);  break;
            case CONTENT:   this.text = str;                break;
        }
        this.tree = tree;
    }

    //                                                                 //
    //-----------------------------------------------------------------//
    //------------------------- PATH TO STRING ------------------------//
    //-----------------------------------------------------------------//
    //                                                                 //

    /**
     * @brief Creates a string with the content of the file in the path 
     * given as a parameter
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

    //                                                                 //
    //-----------------------------------------------------------------//
    //-------------------------- READ METHODS -------------------------//
    //-----------------------------------------------------------------//
    //                                                                 //

    /**
     * @brief Reads a String variable of the Hyper Text stored in the tree.
     * Used when we handle the 'var' structure, it is the value of the var.
     * @example In tree --> [var] = String <---> [name]="Guillermo".
     * or --> [days[0].name] = "Monday"
     * @param var Name of the variable ('key' in the VarTree)
     * @return A String with the corresponding value (stored in the tree)
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
     * @brief Reads a variable of the Hyper Text stored in the tree. Used
     * when we handle the 'for' structure, it is the array of values.
     * @example In tree --> [var] = VarTree <---> [days]=(VarTree)days.
     * @param var Name of the variable ('key' in the VarTree)
     * @return A VarTree with the corresponding value (stored in the tree)
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
     * @brief Handles the regular expressions of the type: {{var}}
     * @param matcher Stores the matches of the regular expressions
     * given in this.text
     * @return Returns the processed variable (if exists)
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

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    /**
     * @brief Handles the regular expressions of the type:
     * {% for x in array %} (...) {% endfor x %}
     * @param matcher Stores the matches of the regular expressions
     * given in this.text
     * @return Returns the processed loop (if exists)
     * @throws IOException
     */
    private String handleFor(Matcher matcher) throws IOException {
        StringBuilder output = new StringBuilder();
        String var = matcher.group(2);
        String arrayName = matcher.group(3);
        VarTree values = readTree(arrayName);
        String body = matcher.group(4); //.trim()
        String id = matcher.group(5);

        if (!var.equals(id))    return body; //TODO: Maybe another return (?)
        
        Iterator<Map.Entry<String, VarTree>> iterator = values.getIterator();
        while(iterator.hasNext()) {
            Map.Entry<String, VarTree> entry = iterator.next();
            String key = entry.getKey();
            VarTree subtree = values.get(key);
            this.tree.put(var, subtree);

            JHTPP tp = new JHTPP(InputType.CONTENT, body, this.tree);
            output.append(tp.processText());
        }

        return output.toString();
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    /**
     * @brief Handles the regular expressions of the type:
     * {% if cond %} (...) {% endif cond %}
     * @param matcher Stores the matches of the regular expressions
     * given in this.text
     * @return Returns the processed if
     * @throws IOException
     */ //TODO: 'cond' needs to be analized & group(8) needs to be deleted
    private String handleIf(Matcher matcher) throws IOException {
        StringBuilder output = new StringBuilder();
        String condition = matcher.group(6);
        String body = matcher.group(7); //.trim(), maybe not necessary (?)
        String id = matcher.group(8);

        if (!condition.equals(id))  return "";

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
     * @brief Preprocesses Hyper Text
     * @param input Text we are going to process
     * @return The processed text
     * @throws IOException
     */
    private String processText(String input) throws IOException {
        // Pattern.DOTALL, means that '.' can be anything (including '\n')
        Pattern pattern = Pattern.compile(REG_EXP_VAR + "|" + REG_EXP_FOR + "|" + REG_EXP_IF, Pattern.DOTALL);
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
     * @brief Method the user will call to process the text given in the 
     * constructor. It triggers the private method String processText(String).
     * @return Processed text
     * @throws IOException
     */
    
    public String processText() throws IOException  { 
        return processText(this.text); 
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//
    //-------------------------- END OF JHTPP -------------------------//
    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//
}