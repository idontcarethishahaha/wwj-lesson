package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.dto.RoleInsertDTO;
import org.example.dto.RolePageDTO;
import org.example.entity.Role;
import org.example.entity.UserRole;
import org.example.exception.ServiceException;
import org.example.mapper.RoleMapper;
import org.example.mapper.RoleMenuMapper;
import org.example.mapper.UserRoleMapper;
import org.example.result.ResultCode;
import org.example.service.RoleService;
import org.example.vo.PageVO;
import org.example.vo.RoleSimpleListVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.example.entity.table.RoleTableDef.ROLE;
import static org.example.entity.table.UserRoleTableDef.USER_ROLE;

/**
 * 角色表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>  implements RoleService{

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Override// 重写save，避免重复插入角色
    public boolean save(RoleInsertDTO dto) {
        //确认角色的名称不重复
        boolean exist = QueryChain.of(mapper)
                .where(ROLE.TITLE.eq(dto.getTitle()))
                .exists();
        if(exist){
            throw new ServiceException(ResultCode.TITLE_REPEAT,"角色标题"+dto.getTitle()+"已存在");
        }
        Role role= BeanUtil.copyProperties(dto,Role.class);
        if(StrUtil.isBlank(dto.getInfo())){
            role.setInfo("暂无描述");
        }
        if(mapper.insert(role)==0){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"添加角色失败");
        }
        return true;
    }

    @Override
    public Role getById(Long id) {
        Role role = mapper.selectOneById(id);
        if(ObjectUtil.isNull(role)){
            throw new ServiceException(ResultCode.ROLE_NOT_FOUND,"该角色不存在");
        }
        return role;
    }

    @Override
    public List<RoleSimpleListVO> simpleList() {
        return QueryChain.of(mapper)
                .orderBy(ROLE.IDX.asc(),ROLE.ID.desc())
                .withRelations()
                .listAs(RoleSimpleListVO.class);
    }

    @Override
    public PageVO<Role> page(RolePageDTO dto) {
        QueryChain<Role> queryChain = QueryChain.of(mapper)
                .orderBy(ROLE.IDX.asc(),ROLE.ID.desc());
        if(StrUtil.isNotBlank(dto.getTitle())){
            queryChain.where(ROLE.TITLE.like(dto.getTitle()));
        }
        Page<Role> result=queryChain
                .withRelations()
                .page(new Page<>(dto.getPageNum(),dto.getPageSize()));
        PageVO<Role> pageVO=new PageVO<>();
        BeanUtil.copyProperties(result,pageVO);
        pageVO.setPageNum(result.getPageNumber());
        return pageVO;
    }

    @Override
    public List<Long> listRoleIdsByUserId(Long userId) {
        return QueryChain.of(UserRole.class)
                .select(USER_ROLE.FK_ROLE_ID)
                .where(USER_ROLE.FK_USER_ID.eq(userId))
                .objListAs(Long.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateRolesByUserId(Long userId, List<Long> roleIds) {
        // 1 先删除已有角色
        UpdateChain.of(userRoleMapper)
                .where(USER_ROLE.FK_USER_ID.eq(userId))
                .remove();
        // 如果未分配新角色，则直接返回true
        if(CollUtil.isEmpty(roleIds)){
            return true;
        }
        List<UserRole> userRoles = new ArrayList<>();
        for(Long roleId : roleIds){
            UserRole userRole = new UserRole();
            userRole.setFkUserId(userId);
            userRole.setFkRoleId(roleId);
            userRoles.add(userRole);
        }
        // 2 批量添加新角色
        if(userRoleMapper.insertBatch(userRoles)<roleIds.size()){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"批量分配用户角色失败");
        }
        return true;
    }
}
