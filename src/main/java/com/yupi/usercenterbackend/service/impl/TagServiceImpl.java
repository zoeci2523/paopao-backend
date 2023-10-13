package com.yupi.usercenterbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.usercenterbackend.model.Tag;
import com.yupi.usercenterbackend.service.TagService;
import com.yupi.usercenterbackend.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author fengxiaoha
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2023-10-13 15:30:11
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




