package buaa.cxtj.job_backend.Service.Impl;

import buaa.cxtj.job_backend.Mapper.DynamicMapper;
import buaa.cxtj.job_backend.POJO.DTO.CommentDTO;
import buaa.cxtj.job_backend.POJO.Entity.Dynamic;
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
        dynamic.setAgree(dynamic.getAgree() + 1);
        int rows = dynamicMapper.updateById(dynamic);
        return new ReturnProtocol(true,"",dynamic);
    }

    @Override
    public ReturnProtocol commentDynamic(String id, String comment) {
        String key = "comment:"+id;
        CommentDTO commentDTO = new CommentDTO(UserHolder.getUser().getId(),id,comment);
        redisUtil.lSet(key, JSONUtil.toJsonStr(commentDTO));
        return new ReturnProtocol(true,"",commentDTO);
    }

    @Override
    public ReturnProtocol transDynamic(String id) {
        Dynamic dynamic = dynamicMapper.selectById(id);
        Dynamic dynamic1 = new Dynamic(UserHolder.getUser().getId(), dynamic.getContent(),dynamic.getUserId());
        dynamicMapper.insert(dynamic1);
        dynamic.setTrans(dynamic.getTrans() + 1);
        dynamicMapper.updateById(dynamic);
        return new ReturnProtocol(true,"",dynamic1);
    }

    @Override
    public ReturnProtocol showDynamic() {
        String id = UserHolder.getUser().getId();
        QueryWrapper<Dynamic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",id);
        List<Dynamic> dynamics = dynamicMapper.selectList(queryWrapper);


        return new  ReturnProtocol(true,"",dynamics);
    }

    @Override
    public ReturnProtocol showComment(String id) {
        String key = "comment:"+id;
        List<Object> comments = redisUtil.lGet(key, 0, redisUtil.lGetListSize(key));
        List<CommentDTO> commentDTOS = new ArrayList<>();
        for(Object o:comments){
            CommentDTO commentDTO = JSONUtil.toBean(o.toString(),CommentDTO.class);
            commentDTOS.add(commentDTO);
        }
        return new ReturnProtocol(true,"",commentDTOS);
    }
}
