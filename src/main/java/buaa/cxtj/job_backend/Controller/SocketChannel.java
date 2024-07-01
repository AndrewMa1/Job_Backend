package buaa.cxtj.job_backend.Controller;


import buaa.cxtj.job_backend.Mapper.ChatMapper;
import buaa.cxtj.job_backend.POJO.Entity.Chat;
import buaa.cxtj.job_backend.POJO.Entity.Message;
import buaa.cxtj.job_backend.POJO.UserHolder;
import buaa.cxtj.job_backend.Service.Impl.KafkaConsumerService;
import buaa.cxtj.job_backend.Service.Impl.KafkaTopicServiceImpl;
import buaa.cxtj.job_backend.Util.SocketChannelMap;
import buaa.cxtj.job_backend.Util.SpringContextUtil;
import cn.hutool.json.JSONUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint(value = "/chat")
@Component
@Slf4j
public class SocketChannel {

    // 保存链接的session，key为用户名,value为对应的session名
    private KafkaTopicServiceImpl kafkaTopicService;
    private KafkaConsumerService kafkaConsumerService;
//    private static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    private ChatMapper chatMapper;

    @Autowired
    SocketChannelMap socketChannelMap;


    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> queryParams = new HashMap<>();
        if (queryString != null && !queryString.isEmpty()) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                queryParams.put(keyValue[0], keyValue[1]);
            }
        }
        return queryParams;
    }


    /**
     * 创建连接
     * 用于监听建立连接，当有客户端与该服务端点建立连接时，将会自回调该注解标注的方法
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        // @PathParam(value = "chatId") String chatId, @PathParam(value = "userName") String userName

        // 获取查询参数
        String queryString = session.getQueryString();
        Map<String, String> queryParams = parseQueryString(queryString);
        String chatId = queryParams.get("chatId");
        String userName = queryParams.get("userName");

        if (kafkaTopicService == null || kafkaConsumerService == null) {
            kafkaTopicService = SpringContextUtil.getBean(KafkaTopicServiceImpl.class);
            kafkaConsumerService = SpringContextUtil.getBean(KafkaConsumerService.class);
        }

//        String userName = UserHolder.getUser().getId();
        log.info("聊天室{}已创建连接，连接人{}", chatId,userName);
        String id = chatId + "&" + userName;  // 该session的Id

        SocketChannelMap.sessionMap.remove(id);

        SocketChannelMap.sessionMap.put(id,session);
        initMes(id);  //初始化聊天记录
    }


    private void initMes(String id) {
        String chatId = id.split("&")[0];
        Session session = SocketChannelMap.sessionMap.get(id);
        List<String> messages = null;
        try {
            messages = kafkaConsumerService.readMessagesFromPartition(chatId, 0);
            System.out.println("消息数量: " + messages.size());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try {
            for(String msg: messages){
                session.getBasicRemote().sendText(msg);
            }
        } catch (IOException e) {
            System.out.println("error");
            throw new RuntimeException(e);
        }
    }


    /**
     * 用于监听客户端向服务端发送消息，当客户端与服务端发送消息时，将会回调该注解标注的方法
     * @param msg: {"to":{},"msg":{}}
     */
    @OnMessage
    public void onMessage(Session session, String msg){

        //,@PathParam(value = "chatId") String chatId, @PathParam(value = "userName") String userName

        // 获取查询参数
        String queryString = session.getQueryString();
        Map<String, String> queryParams = parseQueryString(queryString);
        String chatId = queryParams.get("chatId");
        String userName = queryParams.get("userName");

        log.info("聊天室{}新增消息:{}",chatId,msg);
        Message message = JSONUtil.toBean(msg, Message.class);
        message.setTimestamp(LocalDateTime.now().toString());
        message.setFrom(userName);

        String msgJson = JSONUtil.toJsonStr(message);
        String to_id = chatId + "&" + message.getTo();  // 该session的Id
        System.out.println(to_id);
        String from_id = chatId + "&" + userName;  // 该session的Id
        try{
            Session fromSession = SocketChannelMap.sessionMap.get(from_id);
            sendMessage(chatId,fromSession, msgJson);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        try{
            Session toSession = SocketChannelMap.sessionMap.get(to_id);
            if(chatMapper==null){
                chatMapper = SpringContextUtil.getBean(ChatMapper.class);
            }
            Chat chat = chatMapper.selectById(chatId);
            chat.setTimestamp(LocalDateTime.now().toString());
            if(toSession==null){
                // 对方未登陆聊天室，设置聊天室未读人
                chat.setUnreadUsername(message.getTo());
            }
            chatMapper.updateById(chat);
            sendMessage(chatId,toSession, msgJson);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        kafkaTopicService.sendMessage(chatId,msgJson);

    }


    /**
     * 用于监听连接关闭，当客户端与该服务端点断开连接时，将会回调该注解标注的方法
     * @param session
     *
     */
    @OnClose
    public void onClose(Session session){
        String queryString = session.getQueryString();
        Map<String, String> queryParams = parseQueryString(queryString);
        String chatId = queryParams.get("chatId");
        String userName = queryParams.get("userName");

        String id = chatId + "&" + userName;  // 该session的Id

        log.info("用户{}已关闭连接", id);

        SocketChannelMap.sessionMap.remove(id);
    }


    /**
     * 用于监听该连接上的任何错误，当客户端与该服务端点的连接发生任何异常，都将回调该注解标注的方法
     * 注意该方法的参数必选Throwable，可选Sessiion以及0-n个String参数，且String参数需要使用@PathParam注解标注
     * @param throwable
     */
    @OnError
    public void onError(Throwable throwable,Session session){
        String queryString = session.getQueryString();
        Map<String, String> queryParams = parseQueryString(queryString);
        String chatId = queryParams.get("chatId");
        String userName = queryParams.get("userName");

        String id = chatId + "&" + userName;  // 该session的Id

        log.info("用户{}连接发生异常", id);

        SocketChannelMap.sessionMap.remove(id);
    }


    /**
     * 用来发送消息的方法，参数分别为接收消息的用户的session，和对应的消息
     */
    private void sendMessage(String chatId,Session toSession,String msgJson){
        try {
            toSession.getBasicRemote().sendText(msgJson);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}

