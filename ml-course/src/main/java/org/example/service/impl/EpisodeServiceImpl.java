package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.constant.ML;
import org.example.dto.EpisodeInsertDTO;
import org.example.dto.EpisodePageDTO;
import org.example.entity.Episode;
import org.example.exception.ServiceException;
import org.example.mapper.EpisodeMapper;
import org.example.result.ResultCode;
import org.example.service.EpisodeService;
import org.example.util.MinioUtil;
import org.example.util.VideoDurationUtil;
import org.example.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import static org.example.entity.table.EpisodeTableDef.EPISODE;

/**
 * 集次表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class EpisodeServiceImpl extends ServiceImpl<EpisodeMapper, Episode>  implements EpisodeService{
    @Override
    public boolean insert(EpisodeInsertDTO dto) {
        Episode episode = BeanUtil.copyProperties(dto, Episode.class);
        // 设置默认的封面和视频
        episode.setCover(ML.Episode.DEFAULT_VIDEO_COVER);
        episode.setVideo(ML.Episode.DEFAULT_VIDEO);
        // 设置创建时间和修改时间
        episode.setCreated(LocalDateTime.now());
        episode.setUpdated(LocalDateTime.now());
        // 判断info是否为空
        if (StrUtil.isBlank(episode.getInfo())){
            episode.setInfo("暂无描述");
        }
        if (mapper.insert(episode)==0){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"数据操作失败");
        }
        return true;
    }

    @Override
    public Episode getById(Long id) {
        // 查询关联数据
        RelationManager.addQueryRelations("season");
        Episode episode = mapper.selectOneWithRelationsById(id);
        if (episode==null){
            throw new ServiceException(ResultCode.EPISODE_NOT_FOUND,"数据不存在");
        }
        return episode;
    }

    @Override
    public PageVO<Episode> page(EpisodePageDTO dto) {
        QueryChain<Episode> queryChain = QueryChain.of(mapper)
                .orderBy(EPISODE.IDX.asc(),EPISODE.ID.desc());
        // 判断 title 是否为空
        if (StrUtil.isNotBlank(dto.getTitle())){
            queryChain.where(EPISODE.TITLE.like(dto.getTitle()));
        }
        // 判断 fkSeasonId 是否为空
        if (dto.getFkSeasonId()!=null){
            queryChain.where(EPISODE.FK_SEASON_ID.eq(dto.getFkSeasonId()));
        }
        Page<Episode> result = queryChain.page(new Page<>(dto.getPageNum(),dto.getPageSize()));
        PageVO<Episode> pageVO = new PageVO<>();
        BeanUtil.copyProperties(result,pageVO);
        pageVO.setPageNum(result.getPageNumber());
        return pageVO;
    }

    @Override
    @Transactional
    public String uploadCover(Long episodeId, MultipartFile coverFile) {
        Episode episode = getById(episodeId);
        if (episode == null){
            throw new ServiceException(ResultCode.EPISODE_NOT_FOUND,"数据不存在");
        }
        // 上传文件,得到新的文件名
        String  newFileName
                = MinioUtil.upload(coverFile, ML.MinIO.EPISODE_VIDEO_COVER_DIR, ML.MinIO.BUCKET_NAME);
        episode.setCover(newFileName);
        if (mapper.update(episode)<=0){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"数据操作失败");
        }
        // 返回新的文件名
        return newFileName;
    }

    @Override
    @Transactional
    public String uploadVideo(Long episodeId, MultipartFile videoFile) throws Exception {
        // 获取视频时长
        long durationSeconds= VideoDurationUtil.getVideoDurationSeconds(videoFile);
        Episode episode = getById(episodeId);
        if (episode == null){
            throw new ServiceException(ResultCode.EPISODE_NOT_FOUND,"数据不存在");
        }
        //上传文件
        String newFileName
                = MinioUtil.upload(videoFile, ML.MinIO.EPISODE_VIDEO_DIR, ML.MinIO.BUCKET_NAME);
        episode.setVideo(newFileName);
        episode.setDuration((int) durationSeconds);
        if (mapper.update(episode)<=0){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"数据操作失败");
        }
        return newFileName;
    }
}
