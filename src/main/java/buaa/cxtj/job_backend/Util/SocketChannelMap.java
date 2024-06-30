package buaa.cxtj.job_backend.Util;

import jakarta.websocket.Session;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;



@Component
@Data
public class SocketChannelMap {


    public static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();



}
