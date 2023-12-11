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

public class JHTPP {
    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//
    //!---------------------- CLASS ATTRIBUTES -----------------------!//
    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//

    //*---------------------------------------------------------------*//
    //*--------------- CONSTANTS - REGULAR EXPRESSIONS ---------------*//
    //*---------------------------------------------------------------*//

    private static final String REGEX_WORD = "\\w+";
    private static final String REGEX_VAR = REGEX_WORD+"[\\."+REGEX_WORD+"]*";

    //-----------------------------------------------------------------//

    /** We define the regular expression: 
    *! '{{var}}'
    *
    ** Group <var>          -> '{{var}}'
    **      Group <varName> -> 'var'
    * TODO: Change '.*?' with 'REGEX_DATA', don't know why it doesn't work
    */

    private static final Group VARNAME = 
        new Group("varName","(?<varName>"+REGEX_VAR+")");
    private static final Group VAR = 
        new Group("var","(?<var>\\{\\{"+VARNAME+"\\}\\})");

    //-----------------------------------------------------------------//
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
    
    private static final Group FOR_ELEMENT =  
        new Group("forElement", "(?<forElement>"+REGEX_WORD+")");
    private static final Group FOR_ARRAY = 
        new Group("forArray", "(?<forArray>"+REGEX_VAR+")");
    private static final Group FOR_BODY = 
        new Group("forBody","(?<forBody>.*)");

    private static final Group FOR_BEGIN = 
        new Group("forBegin", "(?<forBegin>\\{\\%\\s*for\\s+"+FOR_ELEMENT+"\\s+"+
                                   "in\\s+"+FOR_ARRAY+"\\s*\\%\\})");

    private static final Group FOR_END = 
        new Group("forEnd","(?<forEnd>\\{\\%\\s*endfor\\s*\\%\\})");

    private static final Group FOR = 
        new Group("for", "(?<for>"+FOR_BEGIN+FOR_BODY+FOR_END+")");

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//
    
    /** We define the regular expression: 
    *! {% if a == b %} (...) {% endif cond %}
    *
    ** Group <if>                          -> '{% if a == b %} (...) {% endif %}'
    **      Group <ifBegin>                -> '{% if a == b %}'
    **          Group <ifCondition>        -> 'a == b'
    **              Group <ifElementLeft>  -> 'a'
    **              Group <ifOperator>     -> '=='
    **              Group <ifElementRight> -> 'b'
    **          Group <ifBody>             -> '(...)'
    **      Group <ifEnd>                  -> '{% endif %}'
    * TODO: Create the REGEXP for CONDITION
    */
    private static final Group IF_ELEMENT_LEFT =  
        new Group("ifElementLeft", "(?<ifElementLeft>"+REGEX_VAR+")");

    private static final Group IF_ELEMENT_RIGHT =  
        new Group("ifElementRight", "(?<ifElementRight>"+REGEX_VAR+")");

    private static final Group IF_OPERATOR =  
        new Group("ifOperator", "(?<ifOperator>==|!=|<=|>=|<|>)");

    private static final Group IF_CONDITION =  
        new Group("ifCondition", "(?<ifCondition>"+IF_ELEMENT_LEFT+
                                      "\\s*"+IF_OPERATOR+"\\s*"+IF_ELEMENT_RIGHT+")");
    private static final Group IF_BODY = 
        new Group("ifBody","(?<ifBody>.*)");

    private static final Group IF_BEGIN = 
        new Group("ifBegin", "(?<ifBegin>\\{\\%\\s*if\\s+"+IF_CONDITION+"\\s+"+
                                   "\\s*\\%\\})");

    private static final Group IF_END = 
        new Group("ifEnd","(?<ifEnd>\\{\\%\\s*endif\\s*\\%\\})");

    private static final Group IF = 
        new Group("if", "(?<if>"+IF_BEGIN+IF_BODY+IF_END+")");

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
     * @param str   the path of the file or the file content
     * @param tree  the tree where the variables are stored
     * @throws IOException
     */
    public JHTPP(InputType inputType, String str, VarTree tree) throws IOException  {
        switch (inputType){
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
     * @param  path File path (ej: /path/to/file.html)
     * @return the file in String format
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
     *          or --> [days.0.name] = "Monday"
     * @param   var name of the variable ('key' in the VarTree)
     * @return  a String with the corresponding value (stored in the tree)
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
     *          or --> [days.0.exercises] = (VarTree) monday_exercises
     * @param   var name of the variable ('key' in the VarTree)
     * @return  a VarTree with the corresponding value (stored in the tree)
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
    //*------------------- ANALYZE STRUCTURES METHODS ----------------*//
    //*---------------------------------------------------------------*//

    /**
     ** Analyzes the a structure, diving it into the processed structure
     ** and the part of the input that was not part of the real structure 
     * 
     * @param  input           structure to be processed
     * @param  beginGroup      the begin group of the structure
     * @param  endGroup        the end group of the structure
     * @param  bodyGroupToFind the group of the body we are going to change
     * @param  structureGroup  the structure group
     * @return the processed structure and the part of the 
     *         input that was not part of the real structure
     *         (if the structure wasn't greedy, "")
     */
    private Pair<String,String> analyzeStructure(String input, Group beginGroup, 
                                                 Group endGroup, Group bodyGroupToFind, 
                                                 Group structureGroup) {
        // We first analyze the Greedy Structure
        var greedyStructure = new GreedyStructure(input, beginGroup, endGroup);       
        // and we divide it into the real structure and the rest that wasn't
        String structure = greedyStructure.getStructure();
        String restOfInput = greedyStructure.getRest();

        // Then we analyze the body in the Non-greedy Structure
        Group bodyGroup = new Group(bodyGroupToFind.name(), structureGroup);
        var nonGreedyStructure = new NonGreedyStructure(structure, bodyGroup);
        String body = nonGreedyStructure.getBody(); 
        
        return new Pair<String,String>(body,restOfInput);
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    private Pair<String,String> analyzeForStructure(String input) {
        return analyzeStructure(input, FOR_BEGIN, FOR_END, FOR_BODY, FOR);
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    private Pair<String,String> analyzeIfStructure(String input) {
        return analyzeStructure(input, IF_BEGIN, IF_END, IF_BODY, IF);
    }

    //*---------------------------------------------------------------*//
    //*--------------------------- HANDLERS --------------------------*//
    //*---------------------------------------------------------------*//

    /**
     ** Handles the regular expressions of the type: {{var}}
     *
     * @param  matcher stores the matches of the regular expressions
     *                 given in this.text
     * @return the processed variable (if exists)
     */
    private String handleVar(Matcher matcher) {
        String varName = matcher.group(VARNAME.name());
        String varValue = readVar(varName);

        return varValue != null ? varValue : "{{" + varName + "}}";
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    /**
     ** Handles the regular expressions of the type:
     ** {% for x in array %} (...) {% endfor x %}
     *
     * @pre    The 'matcher' contains a group("for")
     * @param  matcher stores the matches of the regular expressions
     *                 given in this.text
     * @return the processed 'for' and the rest of the matcher.group("for")
     *         that did not belong to the real 'for' (if there was not -> "")
     * @throws IOException
     */
    private Pair<String,String> handleFor(Matcher matcher) throws IOException {
        String input = matcher.group(FOR.name());
        String forElement = matcher.group(FOR_ELEMENT.name());
        String forArray = matcher.group(FOR_ARRAY.name());
        VarTree values = readTree(forArray);

        Pair<String,String> analyzedFor = analyzeForStructure(input);
        String forBody = analyzedFor.first;
        String restOfInput = analyzedFor.second;

        StringBuilder output = new StringBuilder();
        for (var key : values) {
            VarTree subtree = key.getValue();
            this.tree.put(forElement, subtree);
            JHTPP tp = new JHTPP(InputType.CONTENT, forBody, this.tree);
            output.append(tp.processText());
            this.tree.remove(forElement);
        }

        return new Pair<String,String>(output.toString(), restOfInput);
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    /**
     ** Handles the regular expressions of the type:
     ** {% if cond %} (...) {% endif %}
     *
     * @param  matcher stores the matches of the regular expressions
     *                 given in this.text
     * @return the processed if
     * @throws IOException
     * TODO: else needs to be analyzed
     * ! NOT FINISHED, NEEDS TO BE DONE
     */
    private Pair<String,String> handleIf(Matcher matcher) throws IOException {
        String input = matcher.group(IF.name());

        String ifElementLeft = matcher.group(IF_ELEMENT_LEFT.name());
        String ifElementRight = matcher.group(IF_ELEMENT_RIGHT.name());
        ifElementLeft = readVar(ifElementLeft);
        ifElementRight = readVar(ifElementRight);

        Pair<String,String> analyzedIf = analyzeIfStructure(input);
        String ifBody = analyzedIf.first;
        String restOfInput = analyzedIf.second;

        String ifOperator = matcher.group(IF_OPERATOR.name());

        StringBuilder output = new StringBuilder();
        JHTPP tp = new JHTPP(InputType.CONTENT, ifBody, tree);
        
        switch (ifOperator) {
            case "<=": break; //TODO 
            case ">=": break;
            case "<": break;
            case ">": break;
            case "==":
                if (ifElementLeft.equals(ifElementRight)) {
                    output.append(tp.processText());
                }
                break;
            case "!=": 
                if (!ifElementLeft.equals(ifElementRight)) {
                    output.append(tp.processText());
                }
                break;
        }

        return new Pair<String,String>(output.toString(), restOfInput);
    }

    //*---------------------------------------------------------------*//
    //*------------------------ TEXT PROCESSOR -----------------------*//
    //*---------------------------------------------------------------*//

    /**
     ** Preprocesses Hyper Text
     *
     * @param  input text we are going to process
     * @return The processed text
     * @throws IOException
     */
    private String processText(String input) throws IOException {
        StringBuilder output = new StringBuilder(input);
        // Pattern.DOTALL, means that '.' can be anything (including '\n')
        Pattern pattern = Pattern.compile(VAR+"|"+FOR+"|"+IF, 
                                          Pattern.DOTALL);
        Matcher matcher = pattern.matcher(output);

        for (int i=0; matcher.find(i);) {
            if (matcher.group(VAR.name()) != null) {
                String varReplacement = handleVar(matcher);
                output.replace(matcher.start(), 
                               matcher.end(), 
                               varReplacement);
                i = varReplacement.length();
            }
            else if (matcher.group(FOR.name()) != null) {
                Pair<String,String> forStructure = handleFor(matcher);
                String forReplacement = forStructure.first;
                String restAfterFor = forStructure.second;
                output.replace(matcher.start(), 
                               matcher.end(), 
                               forReplacement);

                int endForReplacement = matcher.start() + forReplacement.length();
                    output.replace(endForReplacement, 
                                   endForReplacement, 
                                   restAfterFor);
                i = forReplacement.length();
            }
            else if (matcher.group(IF.name()) != null) {
                Pair<String,String> ifStructure = handleIf(matcher);
                String ifReplacement = ifStructure.first;
                String restAfterIf = ifStructure.second;
                output.replace(matcher.start(), 
                               matcher.end(), 
                               ifReplacement);
                 
                int endifReplacement = matcher.start() + ifReplacement.length();
                    output.replace(endifReplacement, 
                                   endifReplacement, 
                                   restAfterIf);
                i = ifReplacement.length();
            }
            i += matcher.start();
            matcher = pattern.matcher(output);
        }
        return output.toString();
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//
    
    /**
     ** Method the user will call to process the text given in the 
     ** constructor. It triggers the private method String processText(String).
     * 
     * @return processed text given in constructor
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