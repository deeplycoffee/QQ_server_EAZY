package project;

import netPacket.Message;
import netPacket.MessageType;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class ServerThread extends Thread{
   private Socket socket;

   private String threadName;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public ServerThread(Socket socket, String name) {
        this.socket = socket;
        this.threadName = name;
    }

    @Override
    public void run() {
        System.out.println("服务端线程开启...");

        label:
        while(true){
            try {
                //System.out.println("\n保持通信,等待传入...");
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message)objectInputStream.readObject();
                System.out.println("服务端获取信息成功!");

                switch (message.getMsgType()) {
                    case MessageType.MESSAGE_GET_ONLINE_FRIEND: {
                        System.out.println("\n" + message.getSender() + "请求在线用户列表");
                        String friendList = ServerThreadManager.getAllKey();

                        Message message2 = new Message();
                        message2.setMsgType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                        message2.setContent(friendList);
                        message2.setGetter(message.getSender());//这条信息的接受者是那条请求信息的发送者


                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        objectOutputStream.writeObject(message2);//

                        System.out.println("列表已送回至:" + message.getSender());
                        break;
                    }
                    case MessageType.MESSAGE_CLIENT_EXIT: {
                        System.out.println("\n" + message.getSender() + "请求关闭socket");

                        Message message2 = new Message();
                        message2.setMsgType(MessageType.MESSAGE_CLIENT_EXIT);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        objectOutputStream.writeObject(message2);//

                        ServerThreadManager.removeThread(message.getSender());
                        System.out.println("关闭成功信号已送回至:" + message.getSender());

                        socket.close();
                        break label;
                    }
                    case MessageType.MESSAGE_COMM_MES: {
                        System.out.println("\n收到来自" + message.getSender() + "的发送请求,目标:" + message.getGetter());
                        Message message2 = new Message();

                        message2.setContent(message.getContent());
                        message2.setSender(message.getSender());
                        message2.setGetter(message.getGetter());
                        message2.setSendTime(message.getSendTime());
                        message2.setMsgType(MessageType.MESSAGE_COMM_MES);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream
                                (ServerThreadManager.getValue(message.getGetter()).getSocket().getOutputStream());
                        objectOutputStream.writeObject(message2);//

                        System.out.println("发送成功");
                        break;
                    }
                    case MessageType.MESSAGE_CLIENT_EXIST: {
                        System.out.println("\n收到来自" + message.getSender() + "的查阅请求,目标:" + message.getGetter());


                        Message message2 = new Message();
                        message2.setSender(message.getSender());
                        message2.setGetter(message.getGetter());
                        message2.setContent(message.getContent());
                        message2.setMsgFile(message.getMsgFile());
                        if (ServerThreadManager.getValue(message.getGetter()) != null)
                            message2.setMsgType(MessageType.MESSAGE_CLIENT_EXIST_SUCCEED);
                        else message2.setMsgType(MessageType.MESSAGE_CLIENT_EXIST_FALSE);

                        ObjectOutputStream objectOutputStream = new ObjectOutputStream
                                (ServerThreadManager.getValue(message.getSender()).getSocket().getOutputStream());
                        objectOutputStream.writeObject(message2);//

                        System.out.println("发送查阅结果请求成功");
                        break;
                    }
                    case MessageType.MESSAGE_CHAT_ALL: {
                        System.out.println("\n收到来自" + message.getSender() + "的群聊请求");
                        //试一下getAllKey
                        String friendList = ServerThreadManager.getAllKey();
                        Message message2 = new Message();
                        message2.setMsgType(MessageType.MESSAGE_CHAT_ALL);
                        message2.setSendTime(message.getSendTime());
                        message2.setSender(message.getSender());
                        message2.setContent(message.getContent());
                        message2.setGetter(friendList);

                        ObjectOutputStream objectOutputStream = new ObjectOutputStream
                                (ServerThreadManager.getValue(message.getSender()).getSocket().getOutputStream());
                        objectOutputStream.writeObject(message2);//

                        System.out.println("发送群聊请求结果请求成功");

                        break;
                    }
                    case MessageType.MESSAGE_BE_KILL:
                        System.out.println("\n" + message.getSender() + "将被强制关闭socket");
                        socket.close();
                        System.out.println("关闭成功");
                        break label;
                    case MessageType.MESSAGE_FILE: {
                        System.out.println("\n收到来自" + message.getSender() + "的文件传递请求,目标:" + message.getGetter());
                        Message message2 = new Message();

                        message2.setContent(message.getContent());
                        message2.setSender(message.getSender());
                        message2.setGetter(message.getGetter());
                        message2.setSendTime(message.getSendTime());
                        message2.setMsgType(MessageType.MESSAGE_FILE);
                        message2.setMsgFile(message.getMsgFile());

                        ObjectOutputStream objectOutputStream = new ObjectOutputStream
                                (ServerThreadManager.getValue(message.getGetter()).getSocket().getOutputStream());
                        objectOutputStream.writeObject(message2);//

                        System.out.println("发送成功");
                        break;
                    }
                    case MessageType.MESSAGE_NO_ONLINE:
                        System.out.println("\n收到来自" + message.getSender() + "的离线短信发送请求,目标:" + message.getGetter());

                        if (ServerThreadManager.messages.get(message.getGetter()) == null) {
                            message.setMsgType(MessageType.MESSAGE_COMM_MES);
                            ArrayList<Message> arrayList = new ArrayList<>();
                            arrayList.add(message);
                            ServerThreadManager.messages.put(message.getGetter(), arrayList);
                        } else {
                            message.setMsgType(MessageType.MESSAGE_COMM_MES);
                            ArrayList arrayList = ServerThreadManager.messages.get(message.getGetter());
                            arrayList.add(message);
                            ServerThreadManager.messages.remove(message.getGetter());
                            ServerThreadManager.messages.put(message.getGetter(), arrayList);
                        }
                        System.out.println("发送成功");
                        break;
                    case MessageType.MESSAGE_FILE_NO_ONLINE:
                        System.out.println("\n收到来自" + message.getSender() + "的离线文件发送请求,目标:" + message.getGetter());

                        if (ServerThreadManager.messages.get(message.getGetter()) == null) {
                            message.setMsgType(MessageType.MESSAGE_FILE);
                            ArrayList<Message> arrayList = new ArrayList<>();
                            arrayList.add(message);
                            ServerThreadManager.messages.put(message.getGetter(), arrayList);
                        } else {
                            message.setMsgType(MessageType.MESSAGE_FILE);
                            ArrayList arrayList = ServerThreadManager.messages.get(message.getGetter());
                            arrayList.add(message);
                            ServerThreadManager.messages.remove(message.getGetter());
                            ServerThreadManager.messages.put(message.getGetter(), arrayList);
                        }
                        System.out.println("发送成功");
                        break;
                }

            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
