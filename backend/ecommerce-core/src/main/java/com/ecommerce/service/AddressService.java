package com.ecommerce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecommerce.dto.AddressDTO;
import com.ecommerce.entity.Address;
import com.ecommerce.vo.AddressVO;

import java.util.List;

/**
 * 地址服务接口
 */
public interface AddressService extends IService<Address> {
    /**
     * 查询用户的所有地址列表
     * @param userId 用户 ID
     * @return 地址列表
     */
    List<AddressVO> getAddressList(Integer userId);
    
    /**
     * 查询用户的默认地址
     * @param userId 用户 ID
     * @return 默认地址
     */
    AddressVO getDefaultAddress(Integer userId);
    
    /**
     * 根据 ID 查询地址详情
     * @param id 地址 ID
     * @return 地址详情
     */
    AddressVO getAddressById(Integer id);
    
    /**
     * 新增地址
     * @param addressDTO 地址请求 DTO
     */
    void addAddress(AddressDTO addressDTO);
    
    /**
     * 修改地址
     * @param addressDTO 地址请求 DTO
     */
    void updateAddress(AddressDTO addressDTO);
    
    /**
     * 设为默认地址
     * @param userId 用户 ID
     * @param id 地址 ID
     */
    void setDefaultAddress(Integer userId, Integer id);
    
    /**
     * 根据 ID 删除地址
     * @param id 地址 ID
     */
    void deleteAddress(Integer id);
}
