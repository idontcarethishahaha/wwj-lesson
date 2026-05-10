package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.BannerInsertDTO;
import org.example.dto.BannerPageDTO;
import org.example.entity.Banner;
import org.example.vo.PageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 横幅表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface BannerService extends IService<Banner> {
    boolean insert(BannerInsertDTO dto);
    PageVO<Banner> page(BannerPageDTO dto);
    // 获取最新的n个轮播图片
    List<Banner> top(int n);
    // 上传轮播图片
    String uploadBanner(MultipartFile newFile,Long id);
}
