package project;

import netPacket.Message;
import netPacket.MessageType;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Scanner;

public class ClientThread extends Thread{
  private   Socket socket;
  private String threadName;

    public ClientThread(Socket socket,String name) {
        this.socket = socket;
        this.threadName = name;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("客户端线程开启...");
        label:
        while(true){
            try {
                System.out.println("\n保持通信,等待传入...");
                ObjectInput objectInput = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) objectInput.readObject();
                //System.out.println("客户端获取信息成功!");

                switch (message.getMsgType()) {
                    case MessageType.MESSAGE_RET_ONLINE_FRIEND: {//如果服务端返回 好友列表类型
                        String[] onlineUser = message.getContent().split(" ");//空格分割

                        System.out.println("==========好友列表==========");
                        for (String s : onlineUser) {
                            System.out.println("用户:" + s);
                        }
                        break;
                    }
                    case MessageType.MESSAGE_CLIENT_EXIT:
                        socket.close();
                        System.out.println("成功关闭socket");
                        break label;
                    case MessageType.MESSAGE_COMM_MES:
                        String news = message.getContent();
                        System.out.println("Time:" + message.getSendTime() + "\n@>>>收到来自:" + message.getSender() + "的短信:" + message.getContent());
                        break;
                    case MessageType.MESSAGE_CLIENT_EXIST_SUCCEED: {
                        System.out.println("查找成功!");

                        Message message2 = new Message();
                        message2.setContent(message.getContent());
                        message2.setSender(message.getSender());
                        message2.setGetter(message.getGetter());
                        message2.setSendTime(new Date().toString());
                        message2.setMsgType(MessageType.MESSAGE_COMM_MES);

                        ObjectOutput objectOutput = new ObjectOutputStream(socket.getOutputStream());
                        objectOutput.writeObject(message2);

                        break;
                    }
                    case MessageType.MESSAGE_CLIENT_EXIST_FALSE: {

                        Message message2 = new Message();
                        message2.setContent(message.getContent());
                        message2.setSender(message.getSender());
                        message2.setGetter(message.getGetter());
                        message2.setSendTime(new Date().toString());
                        message2.setMsgFile(message.getMsgFile());

                        if (message.getMsgFile() != null)
                            message2.setMsgType(MessageType.MESSAGE_FILE_NO_ONLINE);
                        else message2.setMsgType(MessageType.MESSAGE_NO_ONLINE);

                        ObjectOutput objectOutput = new ObjectOutputStream(socket.getOutputStream());
                        objectOutput.writeObject(message2);
                        System.out.println("对方离线,消息发送成功");
                        break;
                    }
                    case MessageType.MESSAGE_CHAT_ALL: {
                        String[] onlineUser = message.getGetter().split(" ");//空格分割

                        for (String s : onlineUser) {
                            if (!s.equals(message.getSender())) {
                                Message message2 = new Message();
                                message2.setContent(message.getContent());
                                message2.setSender(message.getSender());
                                message2.setGetter(s);
                                message2.setSendTime(message2.getSendTime());
                                message2.setMsgType(MessageType.MESSAGE_CLIENT_EXIST);

                                ObjectOutput objectOutput = new ObjectOutputStream(socket.getOutputStream());
                                objectOutput.writeObject(message2);
                            }
                        }
                        break;
                    }
                    case MessageType.MESSAGE_BE_KILL: {
                        Message message2 = new Message();
                        message.setMsgType(MessageType.MESSAGE_BE_KILL);
                        message.setSender(message.getGetter());

                        try {
                            ObjectOutput obj = new ObjectOutputStream(socket.getOutputStream());
                            //一样的,但为了适应未来多个socket这么写
                            obj.writeObject(message);
                            System.out.println("被顶包..\n" + "socket来源:" + message.getSender() + "\n成功关闭socket...");
                            System.out.println(message.getGetter() + ":退出系统");
                            ClientThreadManager.removeThread(message.getGetter());
                            System.exit(0);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    case MessageType.MESSAGE_FILE:
                        String path = "D:\\Java__\\xiangmu\\newQQ\\src\\tempHolder";
                        File defaultFile = new File(path);

                        if (!defaultFile.exists()) {
                            defaultFile.mkdirs();
                            System.out.println("默认目录创建成功!");
                        } else System.out.println("已存在默认目录");

                        byte[] bufLine = message.getMsgFile();
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(path + "\\" + message.getContent()));
                        bufferedOutputStream.write(bufLine);
                        bufferedOutputStream.close();
                        System.out.println("Time:" + message.getSendTime() + "\n@>>>收到来自:" + message.getSender() + "的文件:" + message.getContent());

                        break;
                }
                //Todo
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
