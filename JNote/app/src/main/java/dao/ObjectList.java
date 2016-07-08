package dao;

import java.util.ArrayList;

/**
 * Created by Igor on 07/07/2016.
 */
public class ObjectList {

    private final int TYPE_LIST = 3;
    private final int TYPE_OBJECT = 2;
    private final int TYPE_SIMPLE = 1;

    public String name;
    public String value;
    public int type;
    public ArrayList<ObjectList> childList;

    public ObjectList(String name, String value, int type){
        this.name = name;
        this.value = value;
        this.type = type;
        this.childList = new ArrayList<>();
    }

}
