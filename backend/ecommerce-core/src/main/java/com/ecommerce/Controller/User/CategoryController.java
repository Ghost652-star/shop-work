package com.ecommerce.Controller.User;

import com.ecommerce.result.Result;
import com.ecommerce.service.User.UserCategoryService;
import com.ecommerce.vo.CategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {


    private final UserCategoryService userCategoryService;

    CategoryController(UserCategoryService userCategoryService) {
            this.userCategoryService = userCategoryService;
        }
    @GetMapping("/list")
    public Result<List<CategoryVO>> list() {
        log.debug("查询分类列表请求");
        List<CategoryVO> categories = userCategoryService.listCategories();
        log.debug("查询到分类数量: {}", categories.size());
        return Result.success(categories);
    }
}