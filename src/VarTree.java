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
import java.util.Collections;
import java.util.Iterator;

public class VarTree implements Iterable<Map.Entry<String, VarTree>> {
    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//
    //!---------------------- CLASS ATTRIBUTES -----------------------!//
    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//

    private String text = null;
    private double num = 0;
    private TreeMap<String, VarTree> subTree = null;
    private VarType type = VarType.NONE;

    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//
    //!----------------------- CLASS METHODS -------------------------!//
    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//

    //*---------------------------------------------------------------*//
    //*------------------------- CONSTRUCTORS ------------------------*//
    //*---------------------------------------------------------------*//

    /**
     ** Constructor for creating a VarTree with text.
     *
     * @param text The text value to be stored in the VarTree.
     */
    public VarTree(String s) {
        this.text = s;
        type = VarType.TEXT;
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    /**
     ** Constructor for creating a VarTree with a numeric value.
     *
     * @param n The numeric value to be stored in the VarTree.
     */
    public VarTree(double n) {
        this.num = n;
        type = VarType.NUMBER;
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    /**
     ** Default constructor for creating an empty VarTree.
     */
    public VarTree(){
    }

    //*---------------------------------------------------------------*//
    //*------------------------- PUT METHODS -------------------------*//
    //*---------------------------------------------------------------*//

    /**
     ** Put method for adding a new VarTree with text.
     *
     * @param key The key for the new VarTree.
     * @param text The text value to be stored in the new VarTree.
     */
    public void put(String key, String text) {
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

    /**
     **  Put method for adding a new VarTree.
     *
     * @param key The key for the new VarTree.
     * @param tree The VarTree to be stored.
     */
    public void put(String key, VarTree tree) {
        if (type==VarType.TREE) {
           subTree.put(key, tree);
        } else if(type==VarType.NONE){
           subTree = new TreeMap<>();
           subTree.put(key, tree);
           type = VarType.TREE;
        }
    }

    //*---------------------------------------------------------------*//
    //*------------------------- GET METHODS -------------------------*//
    //*---------------------------------------------------------------*//

    /**
     ** Returns the string representation of the value associated with 
     ** the given key.
     *
     * @param key The key to look up.
     * @return The string representation of the value, or an empty string 
     * if not found or not applicable.
     */
    public String getString(String key) {
        VarTree value = subTree.get(key);
        if (type != VarType.TREE || value == null) { 
            return "";
        }
        
        switch (value.type) {
            case TEXT:   return value.text;
            case NUMBER: return String.valueOf(value.num);
            default:     return "";
        }
    }

    //-----------------------------------------------------------------//
    //-----------------------------------------------------------------//

    /**
     ** Gets the VarTree associated with the given key.
     *
     * @param key The key to look up.
     * @return The VarTree associated with the key.
     */
    public VarTree get(String key) {
        return subTree.get(key);
    }

    //*---------------------------------------------------------------*//
    //*------------------------ REMOVE METHOD ------------------------*//
    //*---------------------------------------------------------------*//

    /**
     * Removes the VarTree associated with the given key.
     *
     * @param key The key to look up.
     * @return The removed VarTree.
     */
    public VarTree remove(String key) {
        return subTree.remove(key);
    }

    //*---------------------------------------------------------------*//
    //*------------------------ ITERATOR METHOD ----------------------*//
    //*---------------------------------------------------------------*//

    /**
     ** Provides an iterator over the entries of the VarTree.
     * 
     * @return An iterator over the entries, 
     * or an empty iterator if the VarTree is not of type TREE.
     */
    @Override
    public Iterator<Map.Entry<String, VarTree>> iterator() {
        if (type == VarType.TREE) {
            return subTree.entrySet().iterator();
        } else {
            return Collections.emptyIterator();
        }
    }

    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//
    //!------------------------ END OF VarTree -----------------------!//
    //!---------------------------------------------------------------!//
    //!---------------------------------------------------------------!//
}