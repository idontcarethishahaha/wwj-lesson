package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.dto.CouponsInsertDTO;
import org.example.dto.CouponsPageDTO;
import org.example.entity.Coupons;
import org.example.exception.ServiceException;
import org.example.mapper.CouponsMapper;
import org.example.result.ResultCode;
import org.example.service.CouponsService;
import org.example.vo.PageVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.example.entity.table.CouponsTableDef.COUPONS;

/**
 * 优惠卷表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class CouponsServiceImpl extends ServiceImpl<CouponsMapper, Coupons>  implements CouponsService{
    @Override
    public boolean insert(CouponsInsertDTO dto) {
        // 获取title和code
        String title = dto.getTitle();
        String code = dto.getCode();
        // 判断优惠券的开始时间不能晚于结束时间
        if (dto.getStartTime().isAfter(dto.getEndTime())){
            throw new ServiceException(ResultCode.DATETIME_ILLEGAL,"优惠券的开始时间不能晚于结束时间");
        }
        // 判断优惠券的标题和code不能重复
        if(QueryChain.of(mapper).where(COUPONS.TITLE.eq(title)).exists()){
            throw new ServiceException(ResultCode.TITLE_REPEAT,"该标题已存在，请勿重复添加");
        }
        if (QueryChain.of(mapper).where(COUPONS.CODE.eq(code)).exists()){
            throw new ServiceException(ResultCode.CODE_REPEAT,"该code已存在，请勿重复添加");
        }
        // 复制属性
        Coupons coupons = BeanUtil.copyProperties(dto,Coupons.class);
        if (StrUtil.isBlank(dto.getInfo())){
            coupons.setInfo("暂无描述");
        }
        // 设置创建时间和修改时间
        coupons.setCreated(LocalDateTime.now());
        coupons.setUpdated(LocalDateTime.now());
        return mapper.insert(coupons)>0;
    }

    @Override
    public PageVO<Coupons> page(CouponsPageDTO dto) {
        QueryChain<Coupons> queryChain = QueryChain.of(mapper);
        // 获取title
        String title = dto.getTitle();
        if (StrUtil.isNotBlank(title)){
            queryChain.where(COUPONS.TITLE.like(title));
        }
        // 获取code
        String code = dto.getCode();
        if (StrUtil.isNotBlank(code)){
            queryChain.where(COUPONS.CODE.eq(code));
        }
        // 分页查询转VO
        Page<Coupons> result = queryChain.page(new Page<>(dto.getPageNum(),dto.getPageSize()));
        PageVO<Coupons> pageVO = new PageVO<>();
        pageVO.setPageNum(result.getPageNumber());
        BeanUtil.copyProperties(result,pageVO);
        return pageVO;
    }

    @Override
    public Coupons selectByCode(String code) {
        // one()方法获取单条数据，如果结果集中存在多条数据，则抛出异常
        return QueryChain.of(mapper)
                .where(COUPONS.CODE.eq(code))
                .one();
    }
}
