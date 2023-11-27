/**------------------------------------------------------------------------
 * ?                        Java Hypertext Preproccesor
 * ?                         (GreedyStructure Class)
 * @author         :  anaselmo
 * @repo           :  github.com/anaselmo/JHTPP
 * @createdOn      :  27/11/2023
 * @description    :  Class to store greedy 'begin-end' structures  
 *                    and analyze them 
 *------------------------------------------------------------------------**/

import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GreedyStructure extends BeginEndStructure {

    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//
    //!------------------------ CLASS METHODS ------------------------!//
    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//

    //*---------------------------------------------------------------*//
    //*-------------------------- CONSTRUCTORS -----------------------*//
    //*---------------------------------------------------------------*//

    /**
     * 
     * @param structureString
     * @param beginGroup
     * @param endGroup
     */
    public GreedyStructure(String structureString, 
                             Group beginGroup, 
                             Group endGroup){
        setText(structureString);
        
        setPattern(Pattern.compile(beginGroup+"|"+endGroup));
        group = new TreeMap<>();
        setGroup(Type.BEGIN, beginGroup);
        setGroup(Type.END, endGroup);
    }

    //*---------------------------------------------------------------*//
    //*--------------------------- GET END ---------------------------*//
    //*---------------------------------------------------------------*//

    /**
     ** In a GREEDY Structure, returns the index where the real structure ends
     * @pre    the structure is GREEDY
     * @return the index where the real structure ends
     */
    private int getEnd() {
        Matcher matcher = this.pattern.matcher(text);
        int structureEnd = 0;
        int numberOfOpenStructures = 0;

        if (!matcher.find()) {
            return text.length(); //? Es así?
        }

        do {
            if (matcher.group(getGroup(Type.BEGIN).name()) != null) {
                numberOfOpenStructures++;
            } 
            if (matcher.group(getGroup(Type.END).name()) != null) {
                numberOfOpenStructures--;
            }

            structureEnd = matcher.end();
        } while(numberOfOpenStructures!=0 && matcher.find());

        return structureEnd;
    }

    //*---------------------------------------------------------------*//
    //*------------------------ GET STRUCTURE ------------------------*//
    //*---------------------------------------------------------------*//

    /**
     * 
     * @return
     */
    public String getStructure() {
        return text.substring(0, getEnd());
    }

    //*---------------------------------------------------------------*//
    //*--------------------------- GET REST --------------------------*//
    //*---------------------------------------------------------------*//

    /**
     * 
     * @return
     */
    public String getRest() {
        return text.substring(getEnd());
    }

    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//
    //!-------------------- END OF GreedyStructure -------------------!//
    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//
}
