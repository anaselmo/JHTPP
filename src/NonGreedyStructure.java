/**------------------------------------------------------------------------
 * ?                        Java Hypertext Preproccesor
 * ?                        (NonGreedyStructure Class)
 * @author         :  anaselmo
 * @repo           :  github.com/anaselmo/JHTPP
 * @createdOn      :  27/11/2023
 * @description    :  Class to store non-greedy 'begin-end' structures  
 *                    and analyze them 
 *------------------------------------------------------------------------**/

import java.io.IOException;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NonGreedyStructure extends BeginEndStructure {

    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//
    //!------------------------ CLASS METHODS ------------------------!//
    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//

    //*---------------------------------------------------------------*//
    //*------------------------- CONSTRUCTOR -------------------------*//
    //*---------------------------------------------------------------*//

    /**
     * 
     * @param structureString
     * @param bodyGroup
     */
    public NonGreedyStructure(String structureString, 
                             Group bodyGroup){
                                setText(structureString);
        Pattern patternGroup = Pattern.compile(bodyGroup.toString(),Pattern.DOTALL);

        setPattern(patternGroup);
        group = new TreeMap<>();
        setGroup(Type.BODY,bodyGroup);
    }

    //*---------------------------------------------------------------*//
    //*--------------------------- GET BODY --------------------------*//
    //*---------------------------------------------------------------*//

    /**
     * 
     * @return
     */
    public String getBody() {
        Matcher matcher = getPattern().matcher(text);
        Group bodyGroup = getGroup(Type.BODY);
        if (matcher.find()) {
            String body = matcher.group(bodyGroup.name());
            return leftTrim(body);
        }
        return text;
    }

    //*---------------------------------------------------------------*//
    //*----------------------- LEFT-TRIM STRING ----------------------*//
    //*---------------------------------------------------------------*//

    /**
     ** Trim the left part of a String (\n,\s,\t unnecesary of the left part)
     * 
     * @param  s string to be left-trimmed (Example: '   \nHello\n   ')
     * @return left-trimmed String ((Example: 'Hello\n   '))
     * @throws IOException
     */
    private String leftTrim(String s) {
        int i = 0;
        for (;i < s.length() && Character.isWhitespace(s.charAt(i));++i){}
        return s.substring(i);
    }

    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//
    //!------------------ END OF NonGreedyStructure ------------------!//
    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//
}
