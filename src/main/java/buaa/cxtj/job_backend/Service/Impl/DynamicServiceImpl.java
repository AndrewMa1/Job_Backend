package buaa.cxtj.job_backend.Service.Impl;

import buaa.cxtj.job_backend.Mapper.DynamicMapper;
import buaa.cxtj.job_backend.Mapper.UserMapper;
import buaa.cxtj.job_backend.POJO.DTO.CommentDTO;
import buaa.cxtj.job_backend.POJO.DTO.DynamicDTO;
import buaa.cxtj.job_backend.POJO.DTO.TransDTO;
import buaa.cxtj.job_backend.POJO.Entity.Dynamic;
import buaa.cxtj.job_backend.POJO.Entity.User;
import buaa.cxtj.job_backend.POJO.UserHolder;
import buaa.cxtj.job_backend.Service.DynamicService;
import buaa.cxtj.job_backend.Util.RedisUtil;
import buaa.cxtj.job_backend.Util.ReturnProtocol;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicServiceImpl extends ServiceImpl<DynamicMapper, Dynamic> implements DynamicService{

    @Autowired
    private final DynamicMapper dynamicMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private UserMapper userMapper;
    @Override
    public ReturnProtocol postDynamic( String content) {
        String userId = UserHolder.getUser().getId();
        Dynamic dynamic = new Dynamic(userId,content);
        dynamicMapper.insert(dynamic);
        return new ReturnProtocol(true,"",dynamic);
    }

    @Override
    public ReturnProtocol deleteDynamic(String id) {
        dynamicMapper.deleteById(id);
        return new ReturnProtocol(true,"删除成功",null);
    }

    @Override
    public ReturnProtocol agreeDynamic(String id) {
        Dynamic dynamic = dynamicMapper.selectById(id);
        String key = RedisUtil.AGREE + UserHolder.getUser().getId();
        if(redisTemplate.opsForSet().isMember(key,id)){
            dynamic.setAgree(dynamic.getAgree() - 1);
            int rows = dynamicMapper.updateById(dynamic);
            redisTemplate.opsForSet().remove(key,id);
            return new ReturnProtocol(false,"取消点赞",dynamic);
        }else {
            dynamic.setAgree(dynamic.getAgree() + 1);
            int rows = dynamicMapper.updateById(dynamic);
            redisTemplate.opsForSet().add(key,id);
            return new ReturnProtocol(true,"点赞成功",dynamic);
        }
    }

    @Override
    public ReturnProtocol commentDynamic(String id, String comment) {
        String key = "comment:"+id;
        Dynamic dynamic = dynamicMapper.selectById(id);
        dynamic.setComments(dynamic.getComments() + 1);
        dynamicMapper.updateById(dynamic);
        CommentDTO commentDTO = new CommentDTO(UserHolder.getUser().getId(),id,comment);
        redisUtil.lSet(key, JSONUtil.toJsonStr(commentDTO));
        List<Object> comments = redisUtil.lGet(key, 0, redisUtil.lGetListSize(key));
        List<CommentDTO> commentDTOS = new ArrayList<>();
        for(Object o:comments){
            CommentDTO commentDTO1 = JSONUtil.toBean(o.toString(),CommentDTO.class);
            commentDTO1.setNameCom(userMapper.selectById(commentDTO1.getIdCom()).getNickname());
            commentDTOS.add(commentDTO1);
        }
        return new ReturnProtocol(true,"",commentDTOS);
    }

    @Override
    public ReturnProtocol transDynamic(String id) {
        Dynamic dynamic = dynamicMapper.selectById(id);
        Dynamic dynamic1 = new Dynamic(UserHolder.getUser().getId(), dynamic.getContent(),dynamic.getUserId());
        dynamicMapper.insert(dynamic1);
        dynamic.setTrans(dynamic.getTrans() + 1);
        dynamicMapper.updateById(dynamic);
        User user = userMapper.selectById(dynamic.getUserId());
        TransDTO transDTO = new TransDTO(user.getNickname(),dynamic1.getUserId(),dynamic1.getContent(),dynamic.getUserId());
        return new ReturnProtocol(true,"",transDTO);
    }

    @Override
    public ReturnProtocol showDynamic() {
        String id = UserHolder.getUser().getId();
        QueryWrapper<Dynamic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",id).orderByDesc("create_time");;
        List<Dynamic> dynamics = dynamicMapper.selectList(queryWrapper);
        List<String> names = new ArrayList<>();
        for(Dynamic dynamic:dynamics){
            if(dynamic.getTransId()!=null){
                names.add(userMapper.selectById(dynamic.getTransId()).getNickname());
            }
        }
        DynamicDTO dynamicDTO = new DynamicDTO(UserHolder.getUser().getNickname(),names ,dynamics);
        return new  ReturnProtocol(true,"",dynamicDTO);
    }


    @Override
    public ReturnProtocol showComment(String id) {
        String key = "comment:"+id;
        List<Object> comments = redisUtil.lGet(key, 0, redisUtil.lGetListSize(key));
        List<CommentDTO> commentDTOS = new ArrayList<>();
        for(Object o:comments){
            CommentDTO commentDTO = JSONUtil.toBean(o.toString(),CommentDTO.class);
            commentDTO.setNameCom(userMapper.selectById(commentDTO.getIdCom()).getNickname());
            commentDTOS.add(commentDTO);
        }
        return new ReturnProtocol(true,"",commentDTOS);
    }

    @Override
    public ReturnProtocol showOther(String id) {
        QueryWrapper<Dynamic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",id).orderByDesc("create_time");;
        List<Dynamic> dynamics = dynamicMapper.selectList(queryWrapper);
        List<String> names = new ArrayList<>();
        for(Dynamic dynamic:dynamics){
            if(dynamic.getTransId()!=null){
                names.add(userMapper.selectById(dynamic.getTransId()).getNickname());
            }
        }
        DynamicDTO dynamicDTO = new DynamicDTO(UserHolder.getUser().getNickname(),names ,dynamics);
        return new  ReturnProtocol(true,"",dynamicDTO);
    }
}
