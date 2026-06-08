package org.example.controller;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.constant.ML;
import org.example.dto.OrderInsertDTO;
import org.example.dto.OrderMessage;
import org.example.dto.OrderPageDTO;
import org.example.dto.PrePayDTO;
import org.example.dto.QrCodeDTO;
import org.example.entity.Order;
import org.example.result.Result;
import org.example.service.OrderService;
import org.example.util.AlipayUtil;
import org.example.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * 订单表 控制层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@RestController
@Tag(name = "订单表接口")
@RequestMapping("/api/v1/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 添加订单表。
     *
     * @param order 订单表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("insert")
    @Operation(description="保存订单表")
    public boolean save(@RequestBody @Parameter(description="订单表") OrderInsertDTO order) {
        return orderService.save(order);
    }

    /**
     * 根据主键删除订单表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("delete/{id}")
    @Operation(description="根据主键订单表")
    public boolean remove(@PathVariable("id") @Parameter(description="订单表主键")Long id) {
        return orderService.removeById(id);
    }

    /**
     * 根据主键更新订单表。
     *
     * @param order 订单表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新订单表")
    public boolean update(@RequestBody @Parameter(description="订单表主键")Order order) {
        return orderService.updateById(order);
    }

    /**
     * 查询所有订单表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有订单表")
    public List<Order> list() {
        return orderService.list();
    }

    /**
     * 根据订单表主键获取详细信息。
     *
     * @param id 订单表主键
     * @return 订单表详情
     */
    @GetMapping("select/{id}")
    @Operation(description="根据主键获取订单表")
    public Order getInfo(@PathVariable("id") Long id) {
        return orderService.getById(id);
    }

    /**
     * 分页查询订单表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询订单表")
    public PageVO<Order> page(@Parameter(description="分页信息") OrderPageDTO page) {
        return orderService.page(page);
    }

    /**
     * 创建秒杀订单
     */
    @PostMapping("create/seckill")
    @Operation(description="创建秒杀订单")
    public Result<Boolean> createSeckillOrder(@RequestBody @Parameter(description="秒杀订单信息") OrderMessage orderMessage) {
        return new Result<>(orderService.createSeckillOrder(orderMessage));
    }

    /**
     * 预支付
     *
     * @param dto 预支付参数
     * @return 预支付结果
     */
    @PostMapping("prePay")
    @Operation(description="订单预支付")
    public Object prePay(@RequestBody @Parameter(description="订单预支付参数") PrePayDTO dto) {
        return new Result<>(orderService.prePay(dto));
    }

    // 获取支付二维码
    @PostMapping("getQrCode")
    @Operation(description="获取支付二维码")
    public void getQrCode(HttpServletResponse resp,
                            @RequestBody @Parameter(description="订单预支付参数") QrCodeDTO dto) throws Exception {
        Factory.setOptions(AlipayUtil.getInstance());// 初始化支付宝配置（沙箱环境）
        // 发起预支付请求
        AlipayTradePrecreateResponse precreateResponse
                = Factory.Payment.FaceToFace()
                .preCreate("ML订单支付", dto.getSn(), dto.getPayAmount().toString());
        // 解析预支付响应
        JSONObject jsonObject
                = JSONUtil.parseObj(precreateResponse.getHttpBody())
                .getJSONObject("alipay_trade_precreate_response");
        // 设置响应头
        resp.setContentType(MediaType.IMAGE_JPEG_VALUE);// 设置响应类型
        resp.setDateHeader("Expires", 0);// 设置过期时间
        resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
        resp.addHeader("Cache-Control", "post-check=0,pre-check=0");
        // 生成二维码图片
        BufferedImage qrCodeImage
                = QrCodeUtil.generate(jsonObject.getStr("qr_code".toString()), new QrConfig(256, 256));
        try (ServletOutputStream out = resp.getOutputStream()) {
            ImageIO.write(qrCodeImage, "jpg", out);//将二维码图片写入响应流
            out.flush();
        }
    }

    // 订单支付成功回调接口
    @PostMapping("payPayNotify")
    @Operation(description="订单支付成功回调接口")
    public boolean payPayNotify(HttpServletRequest request) {
        String sn = request.getParameter("out_trade_no");// 从请求参数中获取订单编号
        return orderService.updateStatus(sn, ML.Order.PAID);// 更新订单状态为已支付
    }

    // 查询订单状态（是否完成支付）
    @GetMapping("checkPay/{sn}")
    @Operation(description="查询订单状态（是否完成支付）")
    public boolean checkPay(@PathVariable("sn") @Parameter(description="订单编号") String sn) {
        return orderService.checkPay(sn);
    }

    // 获取订单状态
    @GetMapping("/status/{id}")
    @Operation(description = "获取订单状态")
    public Result getStatus(@PathVariable("id") @Parameter(description = "订单编号") Long id) {
        return new Result(orderService.getById(id).getStatus());
    }

    // 取消订单的方法
    @PostMapping("cancel/{id}")
    @Operation(description = "取消订单")
    public boolean cancel(@PathVariable("id") @Parameter(description = "订单编号") Long id) {
        return orderService.cancel(id);
    }

    /**
     * 检查用户是否购买了课程
     *
     * @param userId  用户ID
     * @param courseId 课程ID
     * @return true表示已购买，false表示未购买
     */
    @GetMapping("hasPurchased")
    @Operation(description = "检查用户是否购买了课程")
    public Result<Boolean> hasPurchased(
            @RequestParam("userId") @Parameter(description = "用户ID") Long userId,
            @RequestParam("courseId") @Parameter(description = "课程ID") Long courseId) {
        return new Result<>(orderService.hasPurchased(userId, courseId));
    }
}
