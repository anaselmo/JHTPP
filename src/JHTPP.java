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

    private static final String WORD = "\\w+";
    private static final String VAR = WORD+"(\\."+WORD+")*";

    //-----------------------------------------------------------------//

    /** We define the regular expression: 
    *! '{{var}}'
    *
    ** Group <var>          -> '{{var}}'
    **      Group <varName> -> 'var'
    * TODO: Change '.*?' with 'REGEX_DATA', don't know why it doesn't work
    */
    private static final String REGEXP_VAR =  
        "(?<var>\\{\\{(?<varName>"+VAR+")\\}\\})";

    //-----------------------------------------------------------------//

    /** We define the regular expression <for>: 
    *! {% for x in array %} (...) {% endfor %}
    *
    ** Group <for>                  -> {% for x in array %} (...) {% endfor %}
    **      Group <forBegin>        -> '{% for x in array %}'
    **          Group <forElement>  -> 'x'
    **          Group <forArray>    -> 'array'
    **          Group <forBody>     -> '(...)', i.e., the loop body
    **      Group <forEnd>          -> '{% endfor x %}'
    */
    private static final String REGEXP_FORBEGIN = 
        "(?<forBegin>\\{\\%\\s*for\\s+(?<forElement>"+WORD+")\\s+"+
        "in\\s+(?<forArray>"+VAR+")\\s*\\%\\})";

    private static final String REGEXP_FOREND =
        "(?<forEnd>\\{\\%\\s*endfor\\s*\\%\\})";

    private static final String REGEXP_FOR = 
        "(?<for>"+REGEXP_FORBEGIN +
        "(?<forBody>.*)"+
        REGEXP_FOREND+")";

    //-----------------------------------------------------------------//
    
    /** We define the regular expression: 
    *! {% if cond %} (...) {% endif cond %}
    *
    ** Group <if>                   -> '{% if cond %} (...) {% endif %}'
    **      Group <ifBegin>         -> '{% if cond %}'
    **          Group <ifCondition> -> 'cond'
    **          Group <ifBody>      -> '(...)'
    **      Group <ifEnd>           -> '{% endif %}'
    * TODO: Create the REGEXP for IF
    */
    private static final String REGEXP_IF  =  
        "\\{\\%\\s*if\\s+("+VAR+")\\s*\\%\\}"+
        "[\\n*\\t*\\s*]*(.+)[\\n*\\t*\\s*]*?"+
        "\\{\\%\\s*endif\\s*("+VAR+")\\s*\\%\\}";

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
        String varName = matcher.group("varName");
        String varValue = readVar(varName);

        return varValue != null ? varValue : "{{" + varName + "}}";
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    private int forStructureEnd(String forBody) {
        Pattern patternFor = Pattern.compile(REGEXP_FORBEGIN+"|"+REGEXP_FOREND);
        Matcher matcherFor = patternFor.matcher(forBody);
        int forStructureEnd = 0;
        int numberOfOpenFors = 0;

        if (!matcherFor.find()) {
            return forBody.length(); //? Es así?
        }

        do {
            if (matcherFor.group("forBegin") != null) {
                numberOfOpenFors++;
            } 
            if (matcherFor.group("forEnd") != null) {
                numberOfOpenFors--;
            }

            forStructureEnd = matcherFor.end();
        } while(numberOfOpenFors!=0 && matcherFor.find());
        
        return forStructureEnd;
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    private String forBody(String forStructure) throws IOException {
        Pattern patternFor = Pattern.compile(REGEXP_FOR, Pattern.DOTALL);
        Matcher matcherFor = patternFor.matcher(forStructure);

        return matcherFor.find() ? matcherFor.group("forBody") : forStructure;

        /*Pattern patternFor = Pattern.compile(REGEXP_FORBEGIN+"|"+REGEXP_FOREND);
        Matcher matcherFor = patternFor.matcher(forStructure);

        if (!matcherFor.find()) {
            return forStructure;
        }

        int forBodyEnd = 0;
        int numberOfOpenFors = 0;
        int forBodyStart = matcherFor.end();

        do {
            if (matcherFor.group("forBegin") != null) {
                numberOfOpenFors++;
            } 
            if (matcherFor.group("forEnd") != null) {
                numberOfOpenFors--;
            }

            forBodyEnd = matcherFor.start();
        } while(numberOfOpenFors!=0 && matcherFor.find());
        
        return forStructure.substring(forBodyStart, forBodyEnd);*/
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
     */
    private Pair<String,String> handleFor(Matcher matcher) throws IOException {
        String forElement = matcher.group("forElement");
        String forArray = matcher.group("forArray");

        String input = matcher.group("for");
        int endForBody = forStructureEnd(input);
        String forStructure = input.substring(0,endForBody);
        String forBody = forBody(forStructure);
        String restOfInput = input.substring(endForBody);

        StringBuilder output = new StringBuilder();
        VarTree values = readTree(forArray);
        
        for (Map.Entry<String, VarTree> key : values) {
            VarTree subtree = key.getValue();
            this.tree.put(forElement, subtree);
            JHTPP tp = new JHTPP(InputType.CONTENT, forBody, this.tree);
            output.append(tp.processText());
            this.tree.remove(forElement);
        }

        return new Pair<String,String>(output.toString(),restOfInput);
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
        String condition = matcher.group(5);
        String id = matcher.group(6);
        if (!condition.equals(id)) {
            return ""; //TODO
        } 

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
        StringBuilder output = new StringBuilder(input);
        // Pattern.DOTALL, means that '.' can be anything (including '\n')
        Pattern pattern = Pattern.compile(REGEXP_VAR + "|" +
                                          REGEXP_FOR, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(output);

        for (int i=0; matcher.find(i);) {

            if (matcher.group("var") != null) {
                String varReplacement = handleVar(matcher);
                output.replace(matcher.start(), 
                               matcher.end(), 
                               varReplacement);

                i = matcher.start()+varReplacement.length();
                matcher = pattern.matcher(output);
            }
            else if (matcher.group("for") != null) {
                Pair<String,String> forStructure = handleFor(matcher);
                String forReplacement = forStructure.first;
                String restAfterFor = forStructure.second;
                output.replace(matcher.start(), 
                               matcher.end(), 
                               forReplacement);

                int endForReplacement = matcher.start() + forReplacement.length();
                if (!restAfterFor.isEmpty()) { //? Maybe an be changed?
                    output.replace(endForReplacement, 
                                   endForReplacement, 
                                   restAfterFor);
                }

                i = matcher.start()+forReplacement.length();
                matcher = pattern.matcher(output);
            }
        }
        
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