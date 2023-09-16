package project;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientThreadManager {
    private static final ConcurrentHashMap<String,ClientThread> hashMap = new ConcurrentHashMap<>();

    public static void addClientThread(String nameId,ClientThread clientThread){
        hashMap.put(nameId,clientThread);
    }
    public static String getAllKey(){
        StringBuilder ans = new StringBuilder();
        for (String s : hashMap.keySet()) {
            ans.append(s).append(" ");
        }

//        Set set = hashMap.keySet();
//        for(Object o : set){
//            ans.append(o.toString()).append(" ");
//        }
        return ans.toString();
    }
    public static void removeThread(String name){
        hashMap.remove(name);
    }

    public static ClientThread getValue(String nameId){
       return hashMap.get(nameId);
    }

}
