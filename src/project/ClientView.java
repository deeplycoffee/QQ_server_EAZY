package project;

import Tools.KeyListener;
import org.junit.Test;

import java.io.IOException;
import java.util.Scanner;

public class ClientView {
        public static void main(String[] args) throws IOException, ClassNotFoundException {
            OpenView.OpenSystemView();
            Scanner scanner = new Scanner(System.in);
            OpenView.OpenSystemSwitch(scanner);
            System.out.println("客户端退出系统");
    }
}
class OpenView {
    public static void OpenSystemView() {
        System.out.println("==========欢迎登录多人聊天系统==========");
        System.out.println("\t\t1.登录系统");
        System.out.println("\t\t2.注册用户");
        System.out.println("\t\t9.退出系统");
    }

    public static void OpenSystemSwitch(Scanner scanner) throws IOException, ClassNotFoundException {
        boolean judgeOut = true;
        Client client;

        while (judgeOut) {
            System.out.println("输入选择:");
            String strKeyInput = scanner.next();

            switch (strKeyInput) {
                case "1":
                    boolean loginWhileJudge = true;
                    System.out.println("登录系统...\n请输入UserID:");
                    String userName = KeyListener.judgeLength(scanner, 20);

                    System.out.println("请输入UserPassword:");
                    String passWord = KeyListener.judgeLength(scanner, 20);

                    client = new Client(userName,passWord);

                    if (client.judgeUserExist()) {
                        System.out.println("链接成功!");
                        System.out.println("==========欢迎" + userName + "进入系统==========");
                        //client.askForMessage();
                        System.out.println("\n==========二级菜单 (用户:" + userName + ")==========");
                        System.out.println("\t\t1.显式用户列表");
                        System.out.println("\t\t2.群发信息");
                        System.out.println("\t\t3.私聊信息");
                        System.out.println("\t\t4.发送信息");
                        System.out.println("\t\t9.退出系统");
                        System.out.println("输入您要执行的操作:");
                        while (loginWhileJudge) {
                            String KeyInputTodo = scanner.next();
                            switch (KeyInputTodo) {
                                case "1":
                                    System.out.println("\t\t1.用户列表如下...");
                                    client.onlineFriendList();
                                    break;
                                case "2":
                                    System.out.println("\t\t2.群发信息中...");
                                    client.chatWithPeople();
                                    break;
                                case "3":
                                    System.out.println("\t\t3.选择你想私聊的对象..");
                                    //client.onlineFriendList();
                                    client.chatWithOther();
                                    break;
                                case "4":
                                    System.out.println("\t\t4.发送文件中...");
                                    client.sendFile();
                                    break;
                                case "9":
                                    System.out.println("退出系统?是按1,否则返回");
                                    String temp = scanner.next();
                                    if (temp.equals("1")) {
                                        System.out.println("yes,bye");
                                        loginWhileJudge = false;
                                    }
                                    client.exitClose();
                                    break;
                                default:
                                    System.out.println("重新输入:输入有误");
                                    break;
                            }
//                            boolean flag = true;
//                            while(flag){
//                                System.out.println("按任意键继续...");
//                                System.in.read();
//                                flag = false;
//                            }
                        }
                    }
                    break;
                case "2":
                    System.out.println("输入Uid");
                    String name = scanner.next();
                    System.out.println("输入密码");
                    String password = scanner.next();
                    Client clientSet = new Client(name,password);
                    if(clientSet.setUserData())
                        System.out.println("注册成功...");
                    else System.out.println("失败...已有账户");
                    break;
                case "9":
                    System.out.println("退出系统?是按1,否则返回");
                    String temp = scanner.next();
                    if (temp.equals("1")) {
                        System.out.println("yes,bye");
                        judgeOut = false;
                    }
                    break;
                default:
                    System.out.println("重新输入:输入有误");
                    break;
            }
        }
    }
}