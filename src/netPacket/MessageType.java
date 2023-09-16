package netPacket;

public interface MessageType {
    //接口定义常量,表示不同的消息类型
    String MESSAGE_LOGIN_SUCCEED = "1";//表示成功
    String MESSAGE_LOGIN_FALSE = "2";//表示失败
    String MESSAGE_COMM_MES = "3";//普通信息包
    String MESSAGE_GET_ONLINE_FRIEND = "4";//要求返回好友列表
    String MESSAGE_RET_ONLINE_FRIEND = "5";//返回在线用户列表
    String MESSAGE_CLIENT_EXIT = "6";//返回在线用户列表
    String MESSAGE_CLIENT_EXIST = "7";//判断是否存在客户好友
    String MESSAGE_CLIENT_EXIST_SUCCEED = "8";//判断是否存在客户好友_SUCCEED
    String MESSAGE_CLIENT_EXIST_FALSE = "9";//判断是否存在客户好友_FALSE
    String MESSAGE_CHAT_ALL = "10";//发送全部聊天
    String MESSAGE_ASK_SINGLE = "11";//判断是否需要顶包
    String MESSAGE_BE_KILL = "12";//被顶包
    String MESSAGE_FILE = "13";//发送文件
    String MESSAGE_NO_ONLINE="14";//发送离线消息
    String MESSAGE_NEW_USER = "15";//检测是否有离线信息
    String MESSAGE_FILE_NO_ONLINE="16";//发送离线消息
    String MESSAGE_RES_SUCCEED = "17";//表示注册成功
    String MESSAGE_RES_FALSE = "18";//表示注册失败
}
