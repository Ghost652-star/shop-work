package com.ecommerce.Controller.User;

import com.ecommerce.dto.MerchantProductQueryDTO;
import com.ecommerce.result.Result;
import com.ecommerce.service.User.UserMerchantService;
import com.ecommerce.vo.MerchantVO;
import com.ecommerce.vo.PageResultVO;
import com.ecommerce.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端商家控制器
 */
@Slf4j
@RestController
@RequestMapping("/merchant")
@RequiredArgsConstructor
public class UserMerchantController {

    private final UserMerchantService userMerchantService;

    /**
     * 查询商家详情
     */
    @GetMapping("/{id}")
    public Result<MerchantVO> detail(@PathVariable Long id) {
        log.debug("查询商家详情请求: merchantId={}", id);
        MerchantVO merchant = userMerchantService.getMerchantById(id);
        return Result.success(merchant);
    }

    /**
     * 查询商家商品列表
     */
    @GetMapping("/{id}/products")
    public Result<PageResultVO<ProductVO>> listProducts(
            @PathVariable Long id,
            @RequestParam(defaultValue = "composite") String sort,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("查询商家商品列表请求: merchantId={}, sort={}, pageNum={}, pageSize={}",
                id, sort, pageNum, pageSize);

        MerchantProductQueryDTO queryDTO = new MerchantProductQueryDTO();
        queryDTO.setSort(sort);
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);

        PageResultVO<ProductVO> result = userMerchantService.listMerchantProducts(id, queryDTO);
        return Result.success(result);
    }
}
