package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.EpisodeInsertDTO;
import org.example.dto.EpisodePageDTO;
import org.example.entity.Episode;
import org.example.result.Result;
import org.example.service.EpisodeService;
import org.example.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 集次表 控制层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@RestController
@Tag(name = "集次表接口")
@RequestMapping("/api/v1/episode")
public class EpisodeController {

    @Autowired
    private EpisodeService episodeService;

    /**
     * 添加集次表。
     *
     * @param episode 集次表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("insert")
    @Operation(description="保存集次表")
    public boolean save(@RequestBody @Parameter(description="集次表") EpisodeInsertDTO episode) {
        return episodeService.insert(episode);
    }

    /**
     * 根据主键删除集次表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("delete/{id}")
    @Operation(description="根据主键集次表")
    public boolean remove(@PathVariable @Parameter(description="集次表主键")Long id) {
        return episodeService.removeById(id);
    }

    /**
     * 根据主键更新集次表。
     *
     * @param episode 集次表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新集次表")
    public boolean update(@RequestBody @Parameter(description="集次表主键")Episode episode) {
        return episodeService.updateById(episode);
    }

    /**
     * 查询所有集次表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有集次表")
    public List<Episode> list() {
        return episodeService.list();
    }

    /**
     * 根据集次表主键获取详细信息。
     *
     * @param id 集次表主键
     * @return 集次表详情
     */
    @GetMapping("select/{id}")
    @Operation(description="根据主键获取集次表")
    public Episode getInfo(@PathVariable Long id) {
        return episodeService.getById(id);
    }

    /**
     * 分页查询集次表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询集次表")
    public PageVO<Episode> page(@Parameter(description="分页信息") EpisodePageDTO page) {
        return episodeService.page(page);
    }

    @PostMapping("uploadVideoCover/{id}")
    @Operation(description="上传集次表封面")
    public Result<String> uploadCover(@PathVariable("id") Long episodeId,@RequestParam("coverFile") MultipartFile coverFile) {
        return new Result<>(episodeService.uploadCover(episodeId,coverFile));
    }


    @PostMapping("uploadVideo/{id}")
    @Operation(description="上传集次表视频")
    public Result<String> uploadVideo(@PathVariable("id") Long episodeId,@RequestParam("videoFile") MultipartFile videoFile) {
        return new Result<>(episodeService.uploadVideo(episodeId,videoFile));
    }


}
