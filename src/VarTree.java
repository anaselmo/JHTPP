/**------------------------------------------------------------------------
 * ?                        Java Hypertext Preproccesor
 * ?                             (VarTree Class)
 * @author         :  YarasAtomic
 * @repo           :  github.com/anaselmo/JHTPP
 * @createdOn      :  before 15/11/2023
 * @description    :  Tree where we store:
 *                      - text
 *                      - numbers 
 *                      - or other trees.
 *------------------------------------------------------------------------**/
import java.util.TreeMap;
import java.util.Map;
import java.util.Iterator;

public class VarTree {
    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//
    //----------------------- CLASS ATTRIBUTES ------------------------//
    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    private String text = null;
    private double num = 0;
    private TreeMap<String,VarTree> subTree;
    private VarType type = VarType.NONE;

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//
    //------------------------ CLASS METHODS --------------------------//
    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    //                                                                 //
    //-----------------------------------------------------------------//
    //-------------------------- CONSTRUCTORS -------------------------//
    //-----------------------------------------------------------------//
    //                                                                 //

    public VarTree(String text) {
        this.text = text;
        type = VarType.TEXT;
    }

    public VarTree(double n) {
        this.num = n;
        type = VarType.NUMBER;
    }

    public VarTree(){
    }

    //                                                                 //
    //-----------------------------------------------------------------//
    //-------------------------- PUT METHODS --------------------------//
    //-----------------------------------------------------------------//
    //                                                                 //

    public void put(String key,String text) {
        if (type==VarType.TREE) {
            subTree.put(key, new VarTree(text));
        } else if (type==VarType.NONE) {
            subTree = new TreeMap<>();
            subTree.put(key, new VarTree(text));
            type = VarType.TREE;
        }
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    public void put(String key, VarTree tree) {
        if (type==VarType.TREE) {
            subTree.put(key, tree);
        } else if(type==VarType.NONE){
            subTree = new TreeMap<>();
            subTree.put(key, tree);
            type = VarType.TREE;
        }
    }

    //                                                                 //
    //-----------------------------------------------------------------//
    //-------------------------- GET METHODS --------------------------//
    //-----------------------------------------------------------------//
    //                                                                 //

    public String getString(String key) {
        if (type==VarType.TREE) {
            VarTree value = subTree.get(key);
            if (value.type==VarType.TEXT) {
                return value.text;
            } else if (value.type==VarType.NUMBER) {
                return Double.toString(value.num);
            }
        }
        return "";
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    public VarTree get(String key) {
        return subTree.get(key);
    }

    //                                                                 //
    //-----------------------------------------------------------------//
    //------------------------- REMOVE METHOD -------------------------//
    //-----------------------------------------------------------------//
    //                                                                 //

    public VarTree remove(String key) {
        return subTree.remove(key);
    }

    //                                                                 //
    //-----------------------------------------------------------------//
    //------------------------- ITERATOR METHOD -----------------------//
    //-----------------------------------------------------------------//
    //                                                                 //

    public Iterator<Map.Entry<String, VarTree>> getIterator() {
        if (type == VarType.TREE) {
            return subTree.entrySet().iterator();
        }
        return null;
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//
    //------------------------- END OF VarTree ------------------------//
    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//
}