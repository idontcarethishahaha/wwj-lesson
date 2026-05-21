package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.component.MyRedis;
import org.example.constant.ML;
import org.example.dto.NoticeInsertDTO;
import org.example.dto.NoticePageDTO;
import org.example.entity.Notice;
import org.example.exception.ServiceException;
import org.example.mapper.NoticeMapper;
import org.example.result.ResultCode;
import org.example.service.NoticeService;
import org.example.vo.PageVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.example.entity.table.NoticeTableDef.NOTICE;

/**
 * 通知表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice>  implements NoticeService{
    @Resource
    private MyRedis myRedis;
    @Override
    public boolean insert(NoticeInsertDTO dto) {
        // 拷贝属性
        Notice notice = BeanUtil.copyProperties(dto,Notice.class);
        // 设置创建时间和修改时间
        notice.setCreated(LocalDateTime.now());
        notice.setUpdated(LocalDateTime.now());
        if(mapper.insert(notice)==0){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"添加通知数据失败");
        }
        // 添加新的通知时，将所有通知缓存删除
        // 高频访问但是变动很少的数据，适合缓存
        myRedis.deleteByPrefix(ML.Redis.TOP_NOTICE_KEY_PREFIX);
        return true;
    }

    @Override
    public PageVO<Notice> page(NoticePageDTO dto) {
        // 使用id对通知逆序排序，保证最新的通知在第一页显示
        QueryChain queryChain = QueryChain.of(mapper)
                .orderBy(NOTICE.IDX.asc(),NOTICE.ID.desc());// 新的通知在前，旧的通知在后
        Page<Notice> result = queryChain.page(new Page(dto.getPageNum(),dto.getPageSize()));
        PageVO<Notice> pageVO = new PageVO<>();
        pageVO.setPageNum(result.getPageNumber());
        BeanUtil.copyProperties(result,pageVO);
        return pageVO;
    }

    @Override
    public List<Notice> top(Long n) {
        // 定义redis缓存键：缓存前缀 + n
        String redisKey = ML.Redis.TOP_NOTICE_KEY_PREFIX + n;
        if(myRedis.exists(redisKey)){// 命中缓存
            return JSONUtil.toList(myRedis.get(redisKey),Notice.class);// 转成Notice类型
        }
        // 在数据库查一次
        List<Notice> result = QueryChain.of(mapper)
                .orderBy(NOTICE.IDX.asc(),NOTICE.ID.desc())// 逆序排序
                .limit(n)// 获取前n条数据
                .select()//可以不写
                .listAs(Notice.class);//list();
        // 缓存结果,过期时间3个小时
        myRedis.setEx(redisKey,JSONUtil.toJsonStr(result),3, TimeUnit.HOURS);
        return result;
    }
}
