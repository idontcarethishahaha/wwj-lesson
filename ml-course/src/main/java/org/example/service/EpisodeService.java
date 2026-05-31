package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.EpisodeInsertDTO;
import org.example.dto.EpisodePageDTO;
import org.example.entity.Episode;
import org.example.vo.PageVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 集次表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface EpisodeService extends IService<Episode> {
   boolean insert(EpisodeInsertDTO dto);
   Episode getById(Long id);
   PageVO<Episode> page(EpisodePageDTO dto);
   // 上传视频封面
    String uploadCover(Long episodeId, MultipartFile coverFile);
    // 上传视频
    String uploadVideo(Long episodeId, MultipartFile videoFile) throws Exception;
}
