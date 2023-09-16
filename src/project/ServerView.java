package project;

import netPacket.Message;
import netPacket.MessageType;
import netPacket.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ServerView {
    private ServerSocket serverSocket = null;

    public static void main(String[] args) {
        new ServerView();
    }

    static private Properties hashMapUser = new Properties();

    static {
        String userFilePath = "D:\\Java__\\xiangmu\\newQQ\\src\\tempUserData";
        if(!Files.exists(Path.of(userFilePath+"\\userData.properties"))){
            File file = new File(userFilePath);
            file.mkdirs();
            File file2 = new File(userFilePath+"\\userData.properties");
            try {
                file2.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("未找到用户注册文件...已创建..");
        }
        try {
            hashMapUser.load(new FileReader(userFilePath+"\\userData.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //以下是测试用例....
        hashMapUser.put("111","222");
        hashMapUser.put("3","3");
        hashMapUser.put("5","6");
        hashMapUser.put("1","1");
        hashMapUser.put("2","2");
    }

    public boolean judgeExistUser(String name,String password){
        if(hashMapUser.get(name)==null){
            System.out.println("无此用户");
            return false;
        }
        if (!hashMapUser.get(name).equals(password)){
            System.out.println("密码错误");
            return false;
        }
        return true;
    }
    public ServerView(){
        try {
            serverSocket = new ServerSocket(9999);
            System.out.println("服务端启动...,端口9999");
            new Thread(new ServerThreadSingle()).start();

            while (true){//当和某个客户端链接后要持续链接,用while
                Socket socket = serverSocket.accept();
                ObjectInput objectInput = new ObjectInputStream(socket.getInputStream());
                User user = (User) objectInput.readObject();
                System.out.println("\n接受用户对象成功....");


                //以下是接到/未接到的一次性返回信息
                Message message = new Message();
                ObjectOutput objectOutput = new ObjectOutputStream(socket.getOutputStream());
                if(judgeExistUser(user.getUserName(), user.getPassword())){

                    if (ServerThreadManager.getValue(user.getUserName())!=null){
                        System.out.println("异地登陆,开始顶号");
                        Message message2 = new Message();
                        message2.setMsgType(MessageType.MESSAGE_BE_KILL);
                        message2.setSender(socket.toString());
                        message2.setGetter(user.getUserName());
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream
                                (ServerThreadManager.getValue(user.getUserName()).getSocket().getOutputStream());
                        objectOutputStream.writeObject(message2);
                    }

                    message.setMsgType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    objectOutput.writeObject(message);
                    System.out.println("成功找到,返回信息成功...");

                    ServerThread serverThread = new ServerThread(socket, user.getUserName());
                    serverThread.start();
                    System.out.println("服务端已连接 userName:"+user.getUserName());
                    ServerThreadManager.addServerThread(user.getUserName(),serverThread);
                    System.out.println("成功放入服务端集合序列...");


                }
                else if(user.isUserNew()){
                    if(hashMapUser.get(user.getUserName())==null) {
                        System.out.println("申请注册...UserName:" + user.getUserName() + " passWord:" + user.getPassword());
                        hashMapUser.put(user.getUserName(), user.getPassword());
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream
                                (new FileOutputStream("D:\\Java__\\xiangmu\\newQQ\\src\\tempUserData\\userData.properties"));
                        hashMapUser.store(bufferedOutputStream, "userDataRegister");
                        message.setMsgType(MessageType.MESSAGE_RES_SUCCEED);
                        bufferedOutputStream.close();
                    }
                    else {
                        System.out.println(user.getUserName() +"已有账户..注册失败");
                        message.setMsgType(MessageType.MESSAGE_RES_FALSE);
                    }
                    objectOutput.writeObject(message);
                }
                else{
                    message.setMsgType(MessageType.MESSAGE_LOGIN_FALSE);
                    objectOutput.writeObject(message);
                    System.out.println("找查失败,UserName:"+user.getUserName()+" password:"+user.getPassword()+"无法查找");
                    socket.close();
                    System.out.println("socket关闭成功");
                    System.out.println();
                }

                if(ServerThreadManager.messages.get(user.getUserName())!=null){
                    ArrayList<Message> arrayList = ServerThreadManager.messages.get(user.getUserName());
                    for(Message m : arrayList){
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        objectOutputStream.writeObject(m);
                    }
                    System.out.println(message.getGetter()+"的离线信息处理完毕...");
                    ServerThreadManager.messages.remove(user.getUserName());

                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                System.out.println("服务端退出....");
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
