package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.SeckillDetailInsertDTO;
import org.example.dto.SeckillDetailPageDTO;
import org.example.entity.SeckillDetail;
import org.example.vo.PageVO;

/**
 * 秒杀明细表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface SeckillDetailService extends IService<SeckillDetail> {
    boolean save(SeckillDetailInsertDTO dto);
    PageVO<SeckillDetail> page(SeckillDetailPageDTO dto);
}
