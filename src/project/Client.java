package project;

import Tools.KeyListener;
import netPacket.Message;
import netPacket.MessageType;
import netPacket.User;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Scanner;

public class Client implements MessageType {
    private   User user;
    private   Socket socket;

    public Client(String name,String password) {
        user = new User(name,password);
    }
    public boolean judgeUserExist() throws IOException, ClassNotFoundException {
        boolean returnBool = false;

        socket = new Socket("127.00.00.01",9999);
        System.out.println("绑定本机成功,端口9999");
        //绑定窗口
        ObjectOutput objectOutput = new ObjectOutputStream(socket.getOutputStream());
        objectOutput.writeObject(user);
        System.out.println("发送user信息成功...请求返回信息....");

        ObjectInput objectInput = new ObjectInputStream(socket.getInputStream());
        Message message = (Message)objectInput.readObject();
        System.out.println("接收登陆反馈许可信息成功....");

        if(message.getMsgType().equals(MESSAGE_LOGIN_SUCCEED)){
            System.out.println("user存在...开启线程...");
            ClientThread clientThread = new ClientThread(socket, user.getUserName());
            ClientThreadManager.addClientThread(user.getUserName(), clientThread);
            System.out.println("客户端线程已加入管理...");

//            //要求另一端退出
//            objectOutput.close();
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            Message message2 = new Message();
//            message2.setMsgType(MessageType.MESSAGE_ASK_SINGLE);
//            objectOutputStream.writeObject(message2);

            clientThread.start();
            returnBool = true;
        }
        else if (message.getMsgType().equals(MESSAGE_LOGIN_FALSE)){
            System.out.println("user不存在,socket已关闭...");//Todo,顶掉账号
            socket.close();
        }

        return returnBool;
    }
    public boolean setUserData() throws IOException, ClassNotFoundException {
        boolean returbool = false;

        socket = new Socket("127.00.00.01",9999);
        System.out.println("绑定本机成功,端口9999");

        //绑定socket后第一次接受都是user,第二次是message

        user.setUserNew(true);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(user);
        System.out.println("发送注册user信息成功...请求返回信息....");

        ObjectInput objectInput = new ObjectInputStream(socket.getInputStream());
        Message message = (Message)objectInput.readObject();
        if(message.getMsgType().equals(MessageType.MESSAGE_RES_SUCCEED)){
            System.out.println("注册成功....");
            returbool = true;
        }
        else if(message.getMsgType().equals(MessageType.MESSAGE_RES_FALSE))
            System.out.println("注册失败....");

        user.setUserNew(false);
        socket.close();

        return returbool;
    }

    //卧槽到底为什么出错啊,妈的改了快三小时了都
    //不要在Client里接受message...
//    public boolean judgeUserOnline(String username,String name) throws IOException, ClassNotFoundException {
//
////        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(socket.getInputStream().toString().getBytes());
////        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
//
//
////        ObjectInput objectInput = new ObjectInputStream(socket.getInputStream());
////        objectInput.readObject();
//        return ((Message)objectInput.readObject()).getMsgType().equals(MessageType.MESSAGE_CLIENT_EXIST_SUCCEED);
//    }

    //不能在Client里向HashMap申请信息!!!!!!!!
    public void chatWithOther() throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        System.out.println("提前写好您想写的话:");
        String news = scanner.nextLine();

        Message message = new Message();
        message.setContent(news);
        message.setMsgType(MessageType.MESSAGE_CLIENT_EXIST);
        message.setGetter(name);
        message.setSender(user.getUserName());
        ObjectOutput objectOutput = new ObjectOutputStream(socket.getOutputStream());
        objectOutput.writeObject(message);
    }


    @Test
    public void sendFile() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("输入传递对象...");
        String getter = scanner.nextLine();
        System.out.println("输入文件路径...");
        String pathStr = scanner.nextLine();
        System.out.println();
        if(Files.exists(Paths.get(pathStr))){
            InputStream inputStream = new FileInputStream(pathStr);
            File file = new File(pathStr);
            byte[] buf = KeyListener.streamTurnToByte(inputStream);
            Message message = new Message();
            message.setMsgType(MessageType.MESSAGE_CLIENT_EXIST);
            message.setSender(user.getUserName());
            message.setSendTime(new Date().toString());
            message.setGetter(getter);
            message.setContent(file.getName());
            message.setMsgFile(buf);
            //文件的输入输出流都记得要关闭!!不然无法传输正确数据
            inputStream.close();

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(message);
        }
        else {
            System.out.println("文件未找到...");
        }

    }
    public void chatWithPeople() throws IOException {
        Scanner scanner = new Scanner(System.in);
        //String name = scanner.nextLine();
        System.out.println("提前写好您想写的话:");
        String news = scanner.nextLine();

        Message message = new Message();
        message.setContent(news);
        message.setSendTime(new Date().toString());
        message.setMsgType(MessageType.MESSAGE_CHAT_ALL);
        message.setSender(user.getUserName());
        ObjectOutput objectOutput = new ObjectOutputStream(socket.getOutputStream());
        objectOutput.writeObject(message);
    }

    public void onlineFriendList(){
        Message message = new Message();
        message.setMsgType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(user.getUserName());
        //包装获取列表信息

        //发送给服务器,要求客户端集合序列返回当前线程:map->ClientThread->socket->outputStream
        try {
            ObjectOutput objectOutput = new ObjectOutputStream
                    (ClientThreadManager.getValue(user.getUserName()).getSocket().getOutputStream());
            objectOutput.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void exitClose(){
        Message message = new Message();
        message.setMsgType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(user.getUserName());

        try {
            //ObjectOutput obj = new ObjectOutputStream(socket.getOutputStream());
            //一样的,但为了适应未来多个socket这么写
            ObjectOutput objectOutput = new ObjectOutputStream
                    (ClientThreadManager.getValue(user.getUserName()).getSocket().getOutputStream());
            objectOutput.writeObject(message);
            System.out.println(user.getUserName()+":退出系统");
            ClientThreadManager.removeThread(user.getUserName());
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
