package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.KillDTO;
import org.example.dto.SeckillInsertDTO;
import org.example.dto.SeckillPageDTO;
import org.example.entity.Seckill;
import org.example.vo.PageVO;

import java.util.List;

/**
 * 秒杀表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface SeckillService extends IService<Seckill> {
    boolean save(SeckillInsertDTO dto);
    PageVO<Seckill> page(SeckillPageDTO dto);
    // 查看今日的秒杀活动
    List<Seckill> queryTodaySeckill();
    // 秒杀课程
    boolean kill(KillDTO dto);
}
