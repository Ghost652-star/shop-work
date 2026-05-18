package com.ecommerce.Controller.User;

import com.ecommerce.dto.AfterSaleDTO;
import com.ecommerce.result.Result;
import com.ecommerce.service.User.AfterSaleService;
import com.ecommerce.vo.AfterSaleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/after-sale")
public class AfterSaleController {

    private final AfterSaleService afterSaleService;

    public AfterSaleController(AfterSaleService afterSaleService) {
        this.afterSaleService = afterSaleService;
    }

    @PostMapping
    public Result<Void> createAfterSale(@RequestBody AfterSaleDTO dto) {
        log.info("创建售后申请: userId={}, orderId={}", dto.getUserId(), dto.getOrderId());
        afterSaleService.createAfterSale(dto);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<AfterSaleVO>> getAfterSaleList(@RequestParam Long userId) {
        log.info("查询售后列表: userId={}", userId);
        List<AfterSaleVO> list = afterSaleService.getAfterSaleList(userId);
        return Result.success(list);
    }

    @GetMapping("/detail")
    public Result<AfterSaleVO> getAfterSaleDetail(@RequestParam Long id) {
        log.info("查询售后详情: id={}", id);
        AfterSaleVO vo = afterSaleService.getAfterSaleDetail(id);
        return Result.success(vo);
    }

    @PutMapping("/cancel")
    public Result<Void> cancelAfterSale(@RequestParam Long id, @RequestParam Long userId) {
        log.info("取消售后: id={}, userId={}", id, userId);
        afterSaleService.cancelAfterSale(id, userId);
        return Result.success();
    }
}
