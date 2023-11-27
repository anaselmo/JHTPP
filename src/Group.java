/**------------------------------------------------------------------------
 * ?                        Java Hypertext Preproccesor
 * ?                              (Group Class)
 * @author         :  anaselmo
 * @repo           :  github.com/anaselmo/JHTPP
 * @createdOn      :  27/11/2023
 * @description    :  Class to store regular-expresion groups
 *------------------------------------------------------------------------**/

public class Group {
    private String name;
    private String regex;

    public Group(String name, String regex) {
        this.name = name;
        this.regex = regex;
    }

    public Group(String name, Group group) {
        this.name = name;
        this.regex = group.toString();
    }

    public String name() {
        return name;
    }

    public String toString() {
        return regex;
    }
}
