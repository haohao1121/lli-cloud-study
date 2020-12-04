package com.sky.lli.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.lli.dao.entity.BaseUser;
import com.sky.lli.dao.mapper.BaseUserMapper;
import com.sky.lli.service.BaseUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lli
 * @since 2020-12-04
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseUserServiceImpl extends ServiceImpl<BaseUserMapper, BaseUser> implements BaseUserService {

}
