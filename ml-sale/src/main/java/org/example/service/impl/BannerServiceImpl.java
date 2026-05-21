package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.component.MyRedis;
import org.example.constant.ML;
import org.example.dto.BannerInsertDTO;
import org.example.dto.BannerPageDTO;
import org.example.entity.Banner;
import org.example.exception.ServiceException;
import org.example.mapper.BannerMapper;
import org.example.result.ResultCode;
import org.example.service.BannerService;
import org.example.util.MinioUtil;
import org.example.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.example.entity.table.BannerTableDef.BANNER;

/**
 * 横幅表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner>  implements BannerService{
    @Resource
    private MyRedis myRedis;

    @Override
    public boolean insert(BannerInsertDTO dto) {
        // 拷贝属性
        Banner banner = BeanUtil.copyProperties(dto,Banner.class);
        // 设置默认的url
        banner.setUrl(ML.Banner.DEFAULT_BANNER);
        // 判断info是否为空
        if (StrUtil.isBlank(banner.getInfo())){
            banner.setInfo("暂无描述");
        }
        // 设置创建时间和修改时间
        banner.setCreated(LocalDateTime.now());
        banner.setUpdated(LocalDateTime.now());
        // 插入
        if (mapper.insert(banner)==0){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"添加失败");
        }
        // 删除缓存
        myRedis.deleteByPrefix(ML.Redis.TOP_BANNER_KEY_PREFIX);
        // 返回成功
        return true;
    }

    @Override
    public PageVO<Banner> page(BannerPageDTO dto) {
        QueryChain<Banner> queryChain = QueryChain.of(mapper)
                .orderBy(BANNER.IDX.asc(), BANNER.ID.desc());

        Page<Banner> result = queryChain.page(new Page<>(dto.getPageNum(), dto.getPageSize()));
        PageVO<Banner> pageVO = new PageVO<>();
        pageVO.setPageNum(result.getPageNumber());
        BeanUtil.copyProperties(result, pageVO);
        return pageVO;
    }

    @Override
    public List<Banner> top(int n) {
        // 获取redis缓存键
        String redisKey = ML.Redis.TOP_BANNER_KEY_PREFIX + n;
        // 如果Redis中存在，则直接返回
        if (myRedis.exists(redisKey)){
            return JSONUtil.toList(myRedis.get(redisKey),Banner.class);
        }
        // 如果Redis中不存在，则从数据库中查询
        List<Banner> banners = QueryChain.of(mapper)
                .orderBy(BANNER.IDX.asc(),BANNER.ID.desc())
                .limit(n)
                .list();
        // 缓存到Redis中
        myRedis.setEx(redisKey,JSONUtil.toJsonStr(banners),3, TimeUnit.HOURS);
        // 返回结果
        return banners;
    }

    @Override
    public String uploadBanner(MultipartFile newFile, Long id) {
        // 获取banner,判断是否存在
        Banner banner = mapper.selectOneById(id);
        if (banner==null){
            throw new ServiceException(ResultCode.BANNER_NOT_FOUND,"该banner不存在");
        }
        // 上传图片，返回新的文件名
        String newFileName = MinioUtil.upload(newFile,ML.MinIO.BANNER_DIR,ML.MinIO.BUCKET_NAME);
        banner.setUrl(newFileName);
        // 更新数据库，判断是否成功
        if (mapper.update(banner)==0){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"更新失败");
        }
        // 返回新文件名
        return newFileName;
    }
}
