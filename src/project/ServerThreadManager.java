package project;

import netPacket.Message;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ServerThreadManager {
    private static ConcurrentHashMap<String,ServerThread> hashMap = new ConcurrentHashMap<>();
    //由于只有一个,所以即使用HashMap也不会出现线程问题
    public static ConcurrentHashMap<String, ArrayList<Message>>messages = new ConcurrentHashMap<>();
    public static void addServerThread(String nameId,ServerThread serverThread){
        hashMap.put(nameId,serverThread);
    }

    //为什么这三个public方法都会报错....
//    public static ArrayList<Message> getMessageValue(String name){
//        return messages.get(name);
//    }
//
//    public static void putInMessages(String name,ArrayList<Message>arrayList){
//        messages.put(name,arrayList);
//    }
//    public static void removeMessage(String name){
//        messages.remove(name);
//    }
    public static void removeThread(String name){
        hashMap.remove(name);
    }

    public static ServerThread getValue(String nameId){
        return hashMap.get(nameId);
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
}
