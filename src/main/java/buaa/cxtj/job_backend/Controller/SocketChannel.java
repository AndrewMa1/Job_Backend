package buaa.cxtj.job_backend.Controller;


import buaa.cxtj.job_backend.POJO.Entity.Message;
import cn.hutool.json.JSONUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint(value = "/chat/{userId}")
@Component
@Slf4j
public class SocketChannel {

    // 保存链接的session，key为用户名,value为对应的session名
    private static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 创建连接
     * 用于监听建立连接，当有客户端与该服务端点建立连接时，将会自回调该注解标注的方法
     * @param session
     * @param userId
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") String userId) {
        log.info("用户{}已创建连接", userId);
        sessionMap.put(userId,session);
        initMes(userId);
    }

    private void initMes(String userId) {
    }


    /**
     * 用于监听客户端向服务端发送消息，当客户端与服务端发送消息时，将会回调该注解标注的方法
     * @param msg: {"to":{},"msg":{}}
     * @param userId
     */
    @OnMessage
    public void onMessage(String msg,@PathParam(value = "userId") String userId){
        log.info("用户{}发来消息:{}",userId,msg);
        Message message = JSONUtil.toBean(msg, Message.class);
        //根据message中的to属性获取接收消息的用户的session，利用其session将消息转发过去
        message.setTimestamp(LocalDateTime.now().toString());
        message.setFrom(userId);

        Session toSession = sessionMap.get(message.getTo());
        sendMessage(toSession, message);

        try {
            Session fromSession = sessionMap.get(userId);
            fromSession.getBasicRemote().sendText(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 用于监听连接关闭，当客户端与该服务端点断开连接时，将会回调该注解标注的方法
     * @param session
     * @param userId
     */
    @OnClose
    public void onClose(Session session,@PathParam(value = "userId") String userId){
        log.info("用户{}已关闭连接", userId);
        sessionMap.remove(userId);
    }


    /**
     * 用于监听该连接上的任何错误，当客户端与该服务端点的连接发生任何异常，都将回调该注解标注的方法
     * 注意该方法的参数必选Throwable，可选Sessiion以及0-n个String参数，且String参数需要使用@PathParam注解标注
     * @param throwable
     * @param userId
     */
    @OnError
    public void onError(Throwable throwable,@PathParam(value = "userId") String userId){
        log.error("用户{}连接发生异常", userId);
        sessionMap.remove(userId);
    }


    /**
     * 用来发送消息的方法，参数分别为接收消息的用户的session，和对应的消息
     */
    private void sendMessage(Session toSession,Message msg){
        try {
            toSession.getBasicRemote().sendText(msg.getMsg());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}

