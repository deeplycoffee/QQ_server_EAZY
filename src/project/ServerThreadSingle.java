package project;

import netPacket.Message;
import netPacket.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Scanner;

public class ServerThreadSingle implements Runnable{
    @Override
    public void run() {
        System.out.println("输入 1.开始推送新闻...");
        while(true){
            Scanner scanner = new Scanner(System.in);
            String key = scanner.nextLine();
            if(key.equals("1")){
                Message message = new Message();
                System.out.println("输入要宣布的事");
                String word = scanner.nextLine();
                message.setContent(word);
                message.setMsgType(MessageType.MESSAGE_COMM_MES);
                message.setSender("Manager");
                message.setSendTime(new Date().toString());
                String[] serverArray = ServerThreadManager.getAllKey().split(" ");
                for(String s : serverArray){
                    message.setGetter(s);
                    try {
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream
                                (ServerThreadManager.getValue(s).getSocket().getOutputStream());
                        objectOutputStream.writeObject(message);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            System.out.println("输入 1.开始推送新闻...");
        }
    }
}

