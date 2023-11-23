
import java.util.TreeMap;
import java.util.Map;
import java.util.Iterator;


public class VarTree {
    private String text = null;
    private double num = 0;
    private TreeMap<String,VarTree> subTree;
    private VarType type = VarType.NONE;

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

    public void put(String key,String text) {
        if (type==VarType.TREE) {
            subTree.put(key, new VarTree(text));
        } else if (type==VarType.NONE) {
            subTree = new TreeMap<>();
            subTree.put(key, new VarTree(text));
            type = VarType.TREE;
        }
    }

    public void put(String key, VarTree tree) {
        if (type==VarType.TREE) {
            subTree.put(key, tree);
        } else if(type==VarType.NONE){
            subTree = new TreeMap<>();
            subTree.put(key, tree);
            type = VarType.TREE;
        }
    }

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

    public VarTree get(String key) {
        return subTree.get(key);
    }

    public VarTree remove(String key) {
        return subTree.remove(key);
    }

    public Iterator<Map.Entry<String, VarTree>> getIterator() {
        if (type == VarType.TREE) {
            return subTree.entrySet().iterator();
        }
        return null;
    }

}