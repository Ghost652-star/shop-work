package com.ecommerce.Controller.User;

import com.ecommerce.dto.AddressDTO;
import com.ecommerce.result.Result;
import com.ecommerce.service.User.AddressService;
import com.ecommerce.vo.AddressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址控制器
 */
@RestController
@Slf4j
@RequestMapping("/address")
public class AddressController {
    
private final AddressService addressService;

 AddressController( AddressService addressService) {
     this.addressService = addressService;
 }

    /**
     * 查询用户的所有地址列表
     * @param userId 用户 ID
     * @return 地址列表
     */
    @GetMapping("/list")
    public Result<List<AddressVO>> getAddressList(@RequestParam Integer userId) {
        log.debug("查询用户地址列表请求: userId={}", userId);
        List<AddressVO> addressList = addressService.getAddressList(userId);
        log.debug("查询到地址数量: {}", addressList.size());
        return Result.success(addressList);
    }
    
    /**
     * 查询用户的默认地址
     * @param userId 用户 ID
     * @return 默认地址
     */
    @GetMapping("/default")
    public Result<AddressVO> getDefaultAddress(@RequestParam Integer userId) {
        log.debug("查询用户默认地址请求: userId={}", userId);
        AddressVO defaultAddress = addressService.getDefaultAddress(userId);
        return Result.success(defaultAddress);
    }
    
    /**
     * 根据 ID 查询地址详情
     * @param id 地址 ID
     * @return 地址详情
     */
    @GetMapping("/{id}")
    public Result<AddressVO> getAddressById(@PathVariable Integer id) {
        log.debug("查询地址详情请求: addressId={}", id);
        AddressVO address = addressService.getAddressById(id);
        return Result.success(address);
    }
    
    /**
     * 新增地址
     * @param addressDTO 地址请求 DTO
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> addAddress(@RequestBody AddressDTO addressDTO) {
        log.info("新增地址请求: userId={}, name={}", addressDTO.getUserId(), addressDTO.getName());
        addressService.addAddress(addressDTO);
        log.info("新增地址成功");
        return Result.success();
    }
    
    /**
     * 修改地址
     * @param addressDTO 地址请求 DTO
     * @return 操作结果
     */
    @PutMapping
    public Result<Void> updateAddress(@RequestBody AddressDTO addressDTO) {
        log.info("修改地址请求: addressId={}, userId={}", addressDTO.getId(), addressDTO.getUserId());
        addressService.updateAddress(addressDTO);
        log.info("修改地址成功: addressId={}", addressDTO.getId());
        return Result.success();
    }
    
    /**
     * 设为默认地址
     * @param userId 用户 ID
     * @param id 地址 ID
     * @return 操作结果
     */
    @PutMapping("/default/{id}")
    public Result<Void> setDefaultAddress(@RequestParam Integer userId, @PathVariable Integer id) {
        log.info("设置默认地址请求: userId={}, addressId={}", userId, id);
        addressService.setDefaultAddress(userId, id);
        log.info("设置默认地址成功: addressId={}", id);
        return Result.success();
    }
    
    /**
     * 根据 ID 删除地址
     * @param id 地址 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@PathVariable Integer id) {
        log.info("删除地址请求: addressId={}", id);
        addressService.deleteAddress(id);
        log.info("删除地址成功: addressId={}", id);
        return Result.success();
    }
}
