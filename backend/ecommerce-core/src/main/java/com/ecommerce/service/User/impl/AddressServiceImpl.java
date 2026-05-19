package com.ecommerce.service.User.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.dto.AddressDTO;
import com.ecommerce.entity.Address;
import com.ecommerce.common.exception.AddressException;
import com.ecommerce.mapper.AddressMapper;
import com.ecommerce.service.User.AddressService;
import com.ecommerce.vo.AddressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 地址服务实现类
 */
@Slf4j
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {
    
    /**
     * 查询用户的所有地址列表
     * @param userId 用户 ID
     * @return 地址列表
     */
    @Override
    public List<AddressVO> getAddressList(Integer userId) {
        log.debug("查询用户地址列表：userId={}", userId);
        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Address::getUserId, userId)
                    .orderByDesc(Address::getIsDefault)
                    .orderByDesc(Address::getCreateTime);
        
        List<Address> addressList = list(queryWrapper);
        log.debug("用户{}的地址数量：{}", userId, addressList.size());
        
        return addressList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    /**
     * 查询用户的默认地址
     * @param userId 用户 ID
     * @return 默认地址
     */
    @Override
    public AddressVO getDefaultAddress(Integer userId) {
        log.debug("查询用户默认地址：userId={}", userId);
        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Address::getUserId, userId)
                    .eq(Address::getIsDefault, 1)
                    .last("LIMIT 1");
        
        Address address = getOne(queryWrapper);
        if (address == null) {
            log.debug("用户{}没有设置默认地址", userId);
        }
        
        return convertToVO(address);
    }
    
    /**
     * 根据 ID 查询地址详情
     * @param id 地址 ID
     * @return 地址详情
     */
    @Override
    public AddressVO getAddressById(Integer id) {
        log.debug("查询地址详情：addressId={}", id);
        Address address = getById(id);
        if (address == null) {
            log.warn("地址不存在：addressId={}", id);
            throw new AddressException("地址不存在");
        }
        
        return convertToVO(address);
    }
    
    /**
     * 新增地址
     * @param addressDTO 地址请求 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAddress(AddressDTO addressDTO) {
        log.info("新增地址：userId={}, name={}, phone={}, isDefault={}", 
                addressDTO.getUserId(), addressDTO.getName(), 
                addressDTO.getPhone(), addressDTO.getIsDefault());
        
        // 如果是默认地址，先将该用户的其他默认地址改为非默认
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault() == 1) {
            log.debug("将用户{}的其他地址设为非默认", addressDTO.getUserId());
            setDefaultForUser(addressDTO.getUserId(), null);
        }
        
        // 构建 Address 实体
        Address address = Address.builder()
                .userId(addressDTO.getUserId())
                .name(addressDTO.getName())
                .phone(addressDTO.getPhone())
                .province(addressDTO.getProvince())
                .city(addressDTO.getCity())
                .district(addressDTO.getDistrict())
                .detailAddress(addressDTO.getDetailAddress())
                .isDefault(addressDTO.getIsDefault() != null ? addressDTO.getIsDefault() : 0)
                .build();
        
        save(address);
        log.info("地址新增成功：addressId={}", address.getId());
    }
    
    /**
     * 修改地址
     * @param addressDTO 地址请求 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(AddressDTO addressDTO) {
        if (addressDTO.getId() == null) {
            log.warn("修改地址失败：地址 ID 不能为空");
            throw new AddressException("地址 ID 不能为空");
        }
        
        log.info("修改地址：addressId={}, userId={}, isDefault={}", 
                addressDTO.getId(), addressDTO.getUserId(), addressDTO.getIsDefault());
        
        // 如果设置为默认地址，先将该用户的其他默认地址改为非默认
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault() == 1) {
            log.debug("将用户{}的其他地址设为非默认", addressDTO.getUserId());
            setDefaultForUser(addressDTO.getUserId(), addressDTO.getId());
        }
        
        // 构建 Address 实体
        Address address = Address.builder()
                .id(addressDTO.getId())
                .userId(addressDTO.getUserId())
                .name(addressDTO.getName())
                .phone(addressDTO.getPhone())
                .province(addressDTO.getProvince())
                .city(addressDTO.getCity())
                .district(addressDTO.getDistrict())
                .detailAddress(addressDTO.getDetailAddress())
                .isDefault(addressDTO.getIsDefault() != null ? addressDTO.getIsDefault() : 0)
                .build();
        
        updateById(address);
        log.info("地址修改成功：addressId={}", address.getId());
    }
    
    /**
     * 设为默认地址
     * @param userId 用户 ID
     * @param id 地址 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Integer userId, Integer id) {
        // 验证地址是否存在
        Address address = getById(id);
        if (address == null) {
            log.warn("地址不存在：addressId={}", id);
            throw new AddressException("地址不存在");
        }
        
        // 验证地址是否属于该用户
        if (!address.getUserId().equals(userId)) {
            log.warn("无权操作该地址：userId={}, addressId={}", userId, id);
            throw new AddressException("无权操作该地址");
        }
        
        // 将该用户的其他地址设为非默认
        setDefaultForUser(userId, id);
        
        // 将指定地址设为默认
        Address updateAddress = Address.builder()
                .id(id)
                .isDefault(1)
                .build();
        
        updateById(updateAddress);
        log.info("设为默认地址成功：addressId={}", id);
    }
    
    /**
     * 根据 ID 删除地址
     * @param id 地址 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Integer id) {
        Address address = getById(id);
        if (address == null) {
            log.warn("地址不存在：addressId={}", id);
            throw new AddressException("地址不存在");
        }
        
        removeById(id);
        log.info("删除地址成功：addressId={}", id);
    }
    
    /**
     * 将用户的所有地址设为非默认
     * @param userId 用户 ID
     * @param excludeId 排除的地址 ID（不设为非默认）
     */
    private void setDefaultForUser(Integer userId, Integer excludeId) {
        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Address::getUserId, userId)
                    .eq(Address::getIsDefault, 1);
        
        if (excludeId != null) {
            queryWrapper.ne(Address::getId, excludeId);
        }
        
        List<Address> addresses = list(queryWrapper);
        for (Address address : addresses) {
            Address updateAddress = Address.builder()
                    .id(address.getId())
                    .isDefault(0)
                    .build();
            updateById(updateAddress);
        }
    }
    
    /**
     * 转换为 VO
     * @param address 地址实体
     * @return 地址 VO
     */
    private AddressVO convertToVO(Address address) {
        if (address == null) {
            return null;
        }

        AddressVO vo = new AddressVO();
        vo.setId(address.getId());
        vo.setUserId(address.getUserId());
        vo.setName(address.getName());
        vo.setPhone(address.getPhone());
        vo.setProvince(address.getProvince());
        vo.setCity(address.getCity());
        vo.setDistrict(address.getDistrict());
        vo.setDetailAddress(address.getDetailAddress());
        vo.setIsDefault(address.getIsDefault());
        vo.setCreateTime(address.getCreateTime());
        vo.setUpdateTime(address.getUpdateTime());
        return vo;
    }
}
