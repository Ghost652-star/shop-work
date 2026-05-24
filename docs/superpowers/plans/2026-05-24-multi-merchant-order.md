# Multi-Merchant Order Split Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Refactor the order system to split orders by merchant at creation time, enabling independent merchant order management while maintaining a single payment flow.

**Architecture:** Orders are created as independent per-merchant records sharing a `batch_no` for payment grouping. No parent order exists. Each order has `merchant_id` and flows through the standard status lifecycle independently. Coupons are distributed proportionally by merchant subtotal ratio.

**Tech Stack:** Java 1.8, Spring Boot 2.7, MyBatis-Plus, MySQL 8, Vue 3, Element Plus, Axios

---

## File Map

### Backend - Create/Modify
- `db_init.sql` — add `merchant_id` to cart, add `merchant_id` + `batch_no` to orders
- `backend/ecommerce-pojo/.../entity/Cart.java` — add `merchantId` field
- `backend/ecommerce-pojo/.../entity/Order.java` — add `merchantId`, `batchNo` fields
- `backend/ecommerce-pojo/.../vo/CartVO.java` — add `merchantId`, `merchantName` fields
- `backend/ecommerce-pojo/.../vo/OrderVO.java` — add `merchantId`, `merchantName` fields
- `backend/ecommerce-core/.../service/User/impl/CartServiceImpl.java` — populate merchantId on add, return merchantName on list
- `backend/ecommerce-core/.../service/User/impl/OrderServiceImpl.java` — refactor createOrder, update payOrder to batch
- `backend/ecommerce-core/.../service/User/OrderService.java` — update payOrder signature
- `backend/ecommerce-core/.../Controller/User/OrderController.java` — update pay endpoint
- `backend/ecommerce-core/.../service/Shop/impl/ShopOrderServiceImpl.java` — add merchantId filter
- `backend/ecommerce-core/.../service/Shop/ShopOrderService.java` — update listOrders signature
- `backend/ecommerce-core/.../Controller/Shop/ShopOrderController.java` — add merchantId param

### Frontend - Modify
- `user-frontend/src/components/CartSidebar.vue` — group by merchant
- `user-frontend/src/views/OrderConfirm.vue` — group by merchant, batch payment
- `user-frontend/src/views/Payment.vue` — batchNo-based payment
- `user-frontend/src/api/order.js` — update payOrder API
- `user-frontend/src/router/index.js` — update payment route
- `shop-frontend/src/api/adminOrder.js` — add merchantId param

---

## Task 1: Database Schema Changes

**Files:**
- Modify: `db_init.sql`
- Execute: SQL on running MySQL instance

- [ ] **Step 1: Add merchant_id to cart table in db_init.sql**

In the `CREATE TABLE cart` block, after `is_checked`, add:
```sql
`merchant_id` BIGINT        NOT NULL DEFAULT 1  COMMENT '商家ID（冗余，从product带入）',
```
Add index after the UNIQUE KEY:
```sql
KEY `idx_merchant_id` (`merchant_id`)
```

- [ ] **Step 2: Add merchant_id and batch_no to orders table in db_init.sql**

In the `CREATE TABLE orders` block, after `user_id`, add:
```sql
`merchant_id` BIGINT        NOT NULL DEFAULT 1  COMMENT '商家ID',
`batch_no`    VARCHAR(50)   NULL                COMMENT '下单批次号，同一次下单的多个商家订单共享',
```
Add indexes:
```sql
KEY `idx_merchant_id` (`merchant_id`),
KEY `idx_batch_no`    (`batch_no`)
```

- [ ] **Step 3: Execute ALTER TABLE on running database**

```sql
ALTER TABLE cart ADD COLUMN `merchant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '商家ID' AFTER `is_checked`;
ALTER TABLE cart ADD KEY `idx_merchant_id` (`merchant_id`);

ALTER TABLE orders ADD COLUMN `merchant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '商家ID' AFTER `user_id`;
ALTER TABLE orders ADD COLUMN `batch_no` VARCHAR(50) NULL COMMENT '下单批次号' AFTER `merchant_id`;
ALTER TABLE orders ADD KEY `idx_merchant_id` (`merchant_id`);
ALTER TABLE orders ADD KEY `idx_batch_no` (`batch_no`);
```

- [ ] **Step 4: Backfill existing data**

```sql
-- Backfill cart merchant_id from product
UPDATE cart c JOIN product p ON c.product_id = p.id SET c.merchant_id = p.merchant_id;

-- Backfill orders merchant_id (all existing orders are merchant 1)
UPDATE orders SET merchant_id = 1 WHERE merchant_id = 0;
```

- [ ] **Step 5: Verify**

```sql
SELECT merchant_id, COUNT(*) FROM cart GROUP BY merchant_id;
SELECT merchant_id, COUNT(*) FROM orders GROUP BY merchant_id;
```

- [ ] **Step 6: Commit**

```bash
git add db_init.sql
git commit -m "feat: add merchant_id to cart and orders, batch_no to orders"
```

---

## Task 2: Entity and VO Updates

**Files:**
- Modify: `backend/ecommerce-pojo/src/main/java/com/ecommerce/entity/Cart.java`
- Modify: `backend/ecommerce-pojo/src/main/java/com/ecommerce/entity/Order.java`
- Modify: `backend/ecommerce-pojo/src/main/java/com/ecommerce/vo/CartVO.java`
- Modify: `backend/ecommerce-pojo/src/main/java/com/ecommerce/vo/OrderVO.java`

- [ ] **Step 1: Add merchantId to Cart entity**

In `Cart.java`, after the `isChecked` field, add:
```java
/**
 * 商家ID
 */
private Long merchantId;
```

- [ ] **Step 2: Add merchantId and batchNo to Order entity**

In `Order.java`, after the `userId` field, add:
```java
/**
 * 商家ID
 */
private Long merchantId;

/**
 * 下单批次号
 */
private String batchNo;
```

- [ ] **Step 3: Add merchantId and merchantName to CartVO**

In `CartVO.java`, after the `subtotal` field, add:
```java
/**
 * 商家ID
 */
private Long merchantId;

/**
 * 商家名称
 */
private String merchantName;
```

- [ ] **Step 4: Add merchantId and merchantName to OrderVO**

In `OrderVO.java`, after the `userId` field, add:
```java
/**
 * 商家ID
 */
private Long merchantId;

/**
 * 商家名称
 */
private String merchantName;
```

- [ ] **Step 5: Build to verify no compilation errors**

Run: `cd backend && mvn clean compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add backend/ecommerce-pojo/src/main/java/com/ecommerce/entity/Cart.java
git add backend/ecommerce-pojo/src/main/java/com/ecommerce/entity/Order.java
git add backend/ecommerce-pojo/src/main/java/com/ecommerce/vo/CartVO.java
git add backend/ecommerce-pojo/src/main/java/com/ecommerce/vo/OrderVO.java
git commit -m "feat: add merchantId/merchantName to Cart, Order, CartVO, OrderVO entities"
```

---

## Task 3: CartServiceImpl — Merchant Info on Add and List

**Files:**
- Create: `backend/ecommerce-core/src/main/java/com/ecommerce/mapper/MerchantMapper.java`
- Modify: `backend/ecommerce-core/src/main/java/com/ecommerce/service/User/impl/CartServiceImpl.java`

- [ ] **Step 1: Create MerchantMapper**

Create `backend/ecommerce-core/src/main/java/com/ecommerce/mapper/MerchantMapper.java`:
```java
package com.ecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ecommerce.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {
}
```

- [ ] **Step 2: Update addToCart to populate merchantId**

In `CartServiceImpl.addToCart`, when building the new Cart object (line ~62-73), add `.merchantId(...)`:
```java
Cart cart = Cart.builder()
        .userId(cartDTO.getUserId())
        .productId(cartDTO.getProductId())
        .productName(product.getName())
        .productImage(product.getMainImage())
        .price(product.getPrice())
        .quantity(cartDTO.getQuantity())
        .isChecked(1)
        .merchantId(product.getMerchantId() != null ? product.getMerchantId().longValue() : 1L)
        .createTime(LocalDateTime.now())
        .updateTime(LocalDateTime.now())
        .build();
```

- [ ] **Step 3: Update getCartList to include merchant name**

Inject `MerchantMapper` at the top of the class:
```java
private final MerchantMapper merchantMapper;

public CartServiceImpl(ProductMapper productMapper, MerchantMapper merchantMapper) {
    this.productMapper = productMapper;
    this.merchantMapper = merchantMapper;
}
```

Update `getCartList` to batch-load merchant names:
```java
@Override
public List<CartVO> getCartList(Long userId) {
    List<Cart> carts = query()
            .eq("user_id", userId)
            .orderByDesc("update_time")
            .list();

    // Batch load merchant names
    Set<Long> merchantIds = carts.stream()
            .map(Cart::getMerchantId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    Map<Long, String> merchantNameMap = Collections.emptyMap();
    if (!merchantIds.isEmpty()) {
        List<Merchant> merchants = merchantMapper.selectBatchIds(merchantIds);
        merchantNameMap = merchants.stream()
                .collect(Collectors.toMap(m -> m.getId().longValue(), Merchant::getName));
    }

    final Map<Long, String> names = merchantNameMap;
    return carts.stream()
            .map(cart -> convertToVO(cart, names.getOrDefault(cart.getMerchantId(), "未知商家")))
            .collect(Collectors.toList());
}
```

- [ ] **Step 4: Update convertToVO to accept merchantName**

Change the `convertToVO` method signature and add merchant fields:
```java
private CartVO convertToVO(Cart cart, String merchantName) {
    BigDecimal subtotal = cart.getPrice().multiply(new BigDecimal(cart.getQuantity()));

    return CartVO.builder()
            .id(cart.getId())
            .userId(cart.getUserId())
            .productId(cart.getProductId())
            .productName(cart.getProductName())
            .productImage(cart.getProductImage())
            .price(cart.getPrice())
            .quantity(cart.getQuantity())
            .isChecked(cart.getIsChecked())
            .merchantId(cart.getMerchantId())
            .merchantName(merchantName)
            .createTime(cart.getCreateTime())
            .updateTime(cart.getUpdateTime())
            .subtotal(subtotal)
            .build();
}
```

- [ ] **Step 5: Add Merchant entity import**

Add to imports:
```java
import com.ecommerce.entity.Merchant;
import com.ecommerce.mapper.MerchantMapper;
import java.util.Set;
import java.util.Objects;
```

- [ ] **Step 6: Build to verify**

Run: `cd backend && mvn clean compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 7: Commit**

```bash
git add backend/ecommerce-core/src/main/java/com/ecommerce/mapper/MerchantMapper.java
git add backend/ecommerce-core/src/main/java/com/ecommerce/service/User/impl/CartServiceImpl.java
git commit -m "feat: cart add merchantId population and merchantName in list"
```

---

## Task 4: OrderServiceImpl — Refactor createOrder with Merchant Split

**Files:**
- Modify: `backend/ecommerce-core/src/main/java/com/ecommerce/service/User/impl/OrderServiceImpl.java`
- Modify: `backend/ecommerce-core/src/main/java/com/ecommerce/service/User/OrderService.java`

This is the largest task. The existing `createOrder` method (lines 68-266) will be broken into private helper methods and rewritten to support merchant-grouped order creation.

- [ ] **Step 1: Update OrderService interface — change return type**

In `OrderService.java`, change:
```java
OrderVO createOrder(OrderDTO orderDTO);
```
to:
```java
List<OrderVO> createOrder(OrderDTO orderDTO);
```

- [ ] **Step 2: Add MerchantMapper injection to OrderServiceImpl**

Add field:
```java
private final MerchantMapper merchantMapper;
```

Update constructor to include it.

- [ ] **Step 3: Add private helper method — validateAddress**

Extract from the existing createOrder:
```java
private Address validateAddress(Long addressId, Long userId) {
    Address address = addressMapper.selectById(addressId.intValue());
    if (address == null || !address.getUserId().equals(userId.intValue())) {
        throw new AddressException("地址不存在或不属于当前用户");
    }
    return address;
}
```

- [ ] **Step 4: Add private helper method — resolveOrderItems**

Extract cart-to-items resolution:
```java
private List<OrderItemDTO> resolveOrderItems(OrderDTO orderDTO) {
    List<OrderItemDTO> requestItems = orderDTO.getItems();
    if (orderDTO.getCartItemIds() != null && !orderDTO.getCartItemIds().isEmpty()) {
        List<Cart> cartItems = cartMapper.selectList(new QueryWrapper<Cart>()
                .eq("user_id", orderDTO.getUserId())
                .in("id", orderDTO.getCartItemIds()));
        if (cartItems.size() != orderDTO.getCartItemIds().size()) {
            throw new CartException("购物车商品不存在或不属于当前用户");
        }
        boolean hasUnchecked = cartItems.stream()
                .anyMatch(item -> item.getIsChecked() == null || item.getIsChecked() != 1);
        if (hasUnchecked) {
            throw new CartException("存在未勾选的购物车商品");
        }
        requestItems = cartItems.stream()
                .map(item -> OrderItemDTO.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }
    if (requestItems == null || requestItems.isEmpty()) {
        throw new OrderException("订单商品不能为空");
    }
    return requestItems;
}
```

- [ ] **Step 5: Add private helper method — buildOrderItems**

Build OrderItem list from request items and product map:
```java
private List<OrderItem> buildOrderItems(List<OrderItemDTO> requestItems, Map<Long, Product> productMap) {
    List<OrderItem> orderItems = new ArrayList<>();
    for (OrderItemDTO item : requestItems) {
        Product product = productMap.get(item.getProductId());
        if (product == null || product.getStatus() != 1) {
            throw new ProductException("商品不存在或已下架");
        }
        if (product.getStock() < item.getQuantity()) {
            throw new ProductException("商品库存不足");
        }
        BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        orderItems.add(OrderItem.builder()
                .productId(product.getId().longValue())
                .productName(product.getName())
                .productImage(product.getMainImage())
                .categoryId(product.getCategoryId())
                .price(product.getPrice())
                .quantity(item.getQuantity())
                .totalPrice(itemTotal)
                .build());
    }
    return orderItems;
}
```

- [ ] **Step 6: Add private helper method — distributeCoupons**

Implement proportional coupon distribution:
```java
private List<OrderCoupon> distributeCoupons(List<Long> couponIds, Long userId,
        BigDecimal totalAmount, BigDecimal groupAmount,
        Set<Integer> categoryIds, List<UserCoupon> usedUserCoupons,
        Map<Long, Coupon> couponCache) {
    List<OrderCoupon> orderCoupons = new ArrayList<>();
    if (couponIds == null || couponIds.isEmpty()) {
        return orderCoupons;
    }

    List<UserCoupon> userCoupons = userCouponMapper.selectBatchIds(couponIds);
    Map<Long, UserCoupon> userCouponMap = userCoupons.stream()
            .collect(Collectors.toMap(UserCoupon::getId, uc -> uc));

    Set<Integer> couponDefIds = userCoupons.stream()
            .map(uc -> uc.getCouponId().intValue())
            .collect(Collectors.toSet());
    if (!couponDefIds.isEmpty()) {
        List<Coupon> couponDefs = couponMapper.selectBatchIds(couponDefIds);
        couponDefs.forEach(c -> couponCache.putIfAbsent(c.getId().longValue(), c));
    }

    BigDecimal ratio = groupAmount.divide(totalAmount, 10, BigDecimal.ROUND_HALF_UP);

    for (Long couponId : couponIds) {
        UserCoupon userCoupon = userCouponMap.get(couponId);
        if (userCoupon == null || !userCoupon.getUserId().equals(userId)) {
            throw new CouponException("优惠券不存在或不属于当前用户");
        }
        if (userCoupon.getStatus() != 0) {
            throw new CouponException("优惠券已使用或已过期");
        }
        if (LocalDateTime.now().isAfter(userCoupon.getExpireTime())) {
            throw new CouponException("优惠券已过期");
        }

        Coupon coupon = couponCache.get(userCoupon.getCouponId());
        if (coupon == null) {
            throw new CouponException("优惠券不存在");
        }
        if (coupon.getCategoryId() != null && !categoryIds.contains(coupon.getCategoryId())) {
            continue; // Skip coupons not applicable to this merchant group
        }

        // Proportional distribution
        BigDecimal groupDiscount = coupon.getDiscountAmount().multiply(ratio)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        if (groupDiscount.compareTo(BigDecimal.ZERO) > 0) {
            orderCoupons.add(OrderCoupon.builder()
                    .couponId(coupon.getId().longValue())
                    .categoryId(coupon.getCategoryId())
                    .discountAmount(groupDiscount)
                    .build());
            usedUserCoupons.add(userCoupon);
        }
    }
    return orderCoupons;
}
```

- [ ] **Step 7: Add private helper method — generateBatchNo**

```java
private String generateBatchNo() {
    String timestamp = LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    String random = String.format("%03d", new Random().nextInt(1000));
    return "BAT" + timestamp + random;
}
```

- [ ] **Step 8: Rewrite createOrder method**

Replace the entire `createOrder` method with:
```java
@Override
@Transactional
public List<OrderVO> createOrder(OrderDTO orderDTO) {
    log.info("创建订单请求: userId={}, addressId={}, couponIds={}",
            orderDTO.getUserId(), orderDTO.getAddressId(), orderDTO.getCouponIds());

    // 1. Validate address
    Address address = validateAddress(orderDTO.getAddressId(), orderDTO.getUserId());

    // 2. Resolve order items
    List<OrderItemDTO> requestItems = resolveOrderItems(orderDTO);

    // 3. Batch load products
    List<Long> productIds = requestItems.stream()
            .map(OrderItemDTO::getProductId).collect(Collectors.toList());
    List<Product> products = productMapper.selectBatchIds(productIds);
    Map<Long, Product> productMap = products.stream()
            .collect(Collectors.toMap(p -> p.getId().longValue(), p -> p));

    // 4. Build order items
    List<OrderItem> allOrderItems = buildOrderItems(requestItems, productMap);

    // 5. Calculate total amount and group by merchant
    BigDecimal totalAmount = BigDecimal.ZERO;
    Map<Long, List<OrderItem>> itemsByMerchant = new LinkedHashMap<>();
    for (OrderItem item : allOrderItems) {
        Product product = productMap.get(item.getProductId());
        Long merchantId = product.getMerchantId() != null ? product.getMerchantId().longValue() : 1L;
        itemsByMerchant.computeIfAbsent(merchantId, k -> new ArrayList<>()).add(item);
        totalAmount = totalAmount.add(item.getTotalPrice());
    }

    // 6. Generate batch number
    String batchNo = generateBatchNo();

    // 7. Create orders per merchant group
    List<OrderVO> result = new ArrayList<>();
    Map<Long, Coupon> couponCache = new HashMap<>();
    List<UserCoupon> allUsedCoupons = new ArrayList<>();

    for (Map.Entry<Long, List<OrderItem>> entry : itemsByMerchant.entrySet()) {
        Long merchantId = entry.getKey();
        List<OrderItem> groupItems = entry.getValue();

        // Calculate group subtotal
        BigDecimal groupAmount = groupItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Determine category IDs for this group
        Set<Integer> categoryIds = groupItems.stream()
                .map(OrderItem::getCategoryId)
                .collect(Collectors.toSet());

        // Distribute coupons proportionally
        List<UserCoupon> groupUsedCoupons = new ArrayList<>();
        List<OrderCoupon> groupCoupons = distributeCoupons(
                orderDTO.getCouponIds(), orderDTO.getUserId(),
                totalAmount, groupAmount, categoryIds,
                groupUsedCoupons, couponCache);

        BigDecimal couponAmount = groupCoupons.stream()
                .map(OrderCoupon::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal freightAmount = BigDecimal.valueOf(5);
        BigDecimal payAmount = groupAmount.add(freightAmount).subtract(couponAmount);
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
            payAmount = BigDecimal.ZERO;
        }

        // Create order
        String orderNo = generateOrderNo();
        Order order = Order.builder()
                .orderNo(orderNo)
                .userId(orderDTO.getUserId())
                .merchantId(merchantId)
                .batchNo(batchNo)
                .status(0)
                .totalAmount(groupAmount)
                .freightAmount(freightAmount)
                .couponAmount(couponAmount)
                .payAmount(payAmount)
                .receiverName(address.getName())
                .receiverPhone(address.getPhone())
                .receiverProvince(address.getProvince())
                .receiverCity(address.getCity())
                .receiverDistrict(address.getDistrict())
                .receiverDetailAddress(address.getDetailAddress())
                .remark(orderDTO.getRemark())
                .createTime(LocalDateTime.now())
                .build();
        orderMapper.insert(order);

        // Insert order items
        for (OrderItem item : groupItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        // Insert order coupons
        for (OrderCoupon oc : groupCoupons) {
            oc.setOrderId(order.getId());
            orderCouponMapper.insert(oc);
        }

        allUsedCoupons.addAll(groupUsedCoupons);

        // Get merchant name for VO
        Merchant merchant = merchantMapper.selectById(merchantId);
        String merchantName = merchant != null ? merchant.getName() : "未知商家";

        result.add(buildOrderVO(order, groupItems, groupCoupons, couponCache, merchantId, merchantName));
    }

    // 8. Update coupon statuses
    for (UserCoupon userCoupon : allUsedCoupons) {
        userCoupon.setStatus(1);
        userCoupon.setOrderId(result.get(0).getId()); // Reference first order
        userCoupon.setUseTime(LocalDateTime.now());
        userCouponMapper.updateById(userCoupon);
    }

    // 9. Deduct stock
    for (OrderItemDTO item : requestItems) {
        int rows = productMapper.deductStock(item.getProductId(), item.getQuantity());
        if (rows == 0) {
            throw new ProductException("商品库存不足");
        }
    }

    // 10. Clear cart
    if (orderDTO.getCartItemIds() != null && !orderDTO.getCartItemIds().isEmpty()) {
        cartMapper.delete(new QueryWrapper<Cart>()
                .eq("user_id", orderDTO.getUserId())
                .in("id", orderDTO.getCartItemIds()));
    }

    log.info("创建订单成功: batchNo={}, orderCount={}", batchNo, result.size());
    return result;
}
```

- [ ] **Step 9: Update buildOrderVO to accept merchant info**

Add merchantId and merchantName parameters:
```java
private OrderVO buildOrderVO(Order order, List<OrderItem> items, List<OrderCoupon> coupons,
        Map<Long, Coupon> couponMap, Long merchantId, String merchantName) {
    // ... existing item/coupon VO building ...
    return OrderVO.builder()
            // ... existing fields ...
            .merchantId(merchantId)
            .merchantName(merchantName)
            .items(itemVOs)
            .coupons(couponVOs)
            .build();
}
```

Update all existing callers of `buildOrderVO` (getOrderList, getOrderDetail) to pass merchant info. For these methods, load merchant name via JOIN or batch query.

- [ ] **Step 10: Update getOrderList to include merchant info**

After loading orders, batch-load merchant names:
```java
Set<Long> merchantIds = orders.stream()
        .map(Order::getMerchantId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
Map<Long, String> merchantNameMap = Collections.emptyMap();
if (!merchantIds.isEmpty()) {
    List<Merchant> merchants = merchantMapper.selectBatchIds(merchantIds);
    merchantNameMap = merchants.stream()
            .collect(Collectors.toMap(m -> m.getId().longValue(), Merchant::getName));
}
```

Pass merchantId and merchantName to buildOrderVO.

- [ ] **Step 11: Update getOrderDetail to include merchant info**

Same pattern — load merchant name and pass to buildOrderVO.

- [ ] **Step 12: Build to verify**

Run: `cd backend && mvn clean compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 13: Commit**

```bash
git add backend/ecommerce-core/src/main/java/com/ecommerce/service/User/
git add backend/ecommerce-core/src/main/java/com/ecommerce/mapper/
git commit -m "feat: refactor createOrder with merchant split and coupon distribution"
```

---

## Task 5: Batch Payment — payOrder by batchNo

**Files:**
- Modify: `backend/ecommerce-core/src/main/java/com/ecommerce/service/User/OrderService.java`
- Modify: `backend/ecommerce-core/src/main/java/com/ecommerce/service/User/impl/OrderServiceImpl.java`
- Modify: `backend/ecommerce-core/src/main/java/com/ecommerce/Controller/User/OrderController.java`
- Modify: `backend/ecommerce-pojo/src/main/java/com/ecommerce/dto/OrderDTO.java` (ensure batchNo field exists or use a new DTO)

- [ ] **Step 1: Update OrderService interface**

Change payOrder signature:
```java
boolean payOrder(String batchNo, Long userId, String paymentType);
```

- [ ] **Step 2: Rewrite payOrder in OrderServiceImpl**

```java
@Override
@Transactional
public boolean payOrder(String batchNo, Long userId, String paymentType) {
    log.info("支付订单: batchNo={}, userId={}, paymentType={}", batchNo, userId, paymentType);

    List<Order> orders = orderMapper.selectList(new QueryWrapper<Order>()
            .eq("batch_no", batchNo)
            .eq("user_id", userId));

    if (orders.isEmpty()) {
        throw new OrderException("订单不存在");
    }

    for (Order order : orders) {
        if (order.getStatus() != 0) {
            throw new OrderException("订单 " + order.getOrderNo() + " 状态异常，无法支付");
        }
    }

    LocalDateTime now = LocalDateTime.now();
    for (Order order : orders) {
        order.setStatus(1);
        order.setPaymentType(paymentType);
        order.setPaymentTime(now);
        orderMapper.updateById(order);
    }

    log.info("支付订单成功: batchNo={}, orderCount={}", batchNo, orders.size());
    return true;
}
```

- [ ] **Step 3: Update OrderController pay endpoint**

Change the pay endpoint to accept batchNo instead of orderId:
```java
@PostMapping("/pay")
public Result payOrder(@RequestBody Map<String, Object> params) {
    String batchNo = (String) params.get("batchNo");
    Long userId = Long.valueOf(params.get("userId").toString());
    String paymentType = (String) params.get("paymentType");
    boolean success = orderService.payOrder(batchNo, userId, paymentType);
    return success ? Result.success() : Result.error("支付失败");
}
```

Or better — create a PayDTO:
```java
@Data
public class PayDTO {
    private String batchNo;
    private Long userId;
    private String paymentType;
}
```

Then:
```java
@PostMapping("/pay")
public Result payOrder(@RequestBody PayDTO payDTO) {
    boolean success = orderService.payOrder(payDTO.getBatchNo(), payDTO.getUserId(), payDTO.getPaymentType());
    return success ? Result.success() : Result.error("支付失败");
}
```

- [ ] **Step 4: Build to verify**

Run: `cd backend && mvn clean compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add backend/ecommerce-core/src/main/java/com/ecommerce/service/User/
git add backend/ecommerce-core/src/main/java/com/ecommerce/Controller/User/OrderController.java
git add backend/ecommerce-pojo/src/main/java/com/ecommerce/dto/
git commit -m "feat: batch payment by batchNo instead of single orderId"
```

---

## Task 6: ShopOrderService — Merchant Filter

**Files:**
- Modify: `backend/ecommerce-core/src/main/java/com/ecommerce/service/Shop/ShopOrderService.java`
- Modify: `backend/ecommerce-core/src/main/java/com/ecommerce/service/Shop/impl/ShopOrderServiceImpl.java`
- Modify: `backend/ecommerce-core/src/main/java/com/ecommerce/Controller/Shop/ShopOrderController.java`
- Modify: `backend/ecommerce-pojo/src/main/java/com/ecommerce/dto/ShopOrderQueryDTO.java`

- [ ] **Step 1: Add merchantId to ShopOrderQueryDTO**

```java
private Long merchantId;
```

- [ ] **Step 2: Update ShopOrderServiceImpl.listOrders**

Add merchantId filter to the query:
```java
QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
if (queryDTO.getMerchantId() != null) {
    queryWrapper.eq("merchant_id", queryDTO.getMerchantId());
}
if (queryDTO.getStatus() != null) {
    queryWrapper.eq("status", queryDTO.getStatus());
}
queryWrapper.orderByDesc("create_time");
```

- [ ] **Step 3: Update ShopOrderController**

Ensure `merchantId` is passed from request params to the query DTO. The `@ModelAttribute` binding should handle it automatically since it's a field on `ShopOrderQueryDTO`.

- [ ] **Step 4: Build to verify**

Run: `cd backend && mvn clean compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add backend/ecommerce-core/src/main/java/com/ecommerce/service/Shop/
git add backend/ecommerce-core/src/main/java/com/ecommerce/Controller/Shop/
git add backend/ecommerce-pojo/src/main/java/com/ecommerce/dto/ShopOrderQueryDTO.java
git commit -m "feat: shop order list filter by merchantId"
```

---

## Task 7: Frontend — CartSidebar Grouped by Merchant

**Files:**
- Modify: `user-frontend/src/components/CartSidebar.vue`

- [ ] **Step 1: Add computed property for grouped cart items**

In the `computed` section, add:
```javascript
groupedCartItems() {
  const groups = {}
  this.cartItems.forEach(item => {
    const key = item.merchantId || 0
    if (!groups[key]) {
      groups[key] = {
        merchantId: item.merchantId,
        merchantName: item.merchantName || '未知商家',
        items: []
      }
    }
    groups[key].items.push(item)
  })
  return Object.values(groups)
}
```

- [ ] **Step 2: Update cart list template to render grouped items**

Replace the flat `<div v-for="item in cartItems">` with grouped rendering:
```html
<div class="cart-list">
  <div v-for="group in groupedCartItems" :key="group.merchantId" class="merchant-group">
    <div class="merchant-header">
      <span class="merchant-name">{{ group.merchantName }}</span>
    </div>
    <div v-for="item in group.items" :key="item.id" class="cart-item">
      <!-- existing item template unchanged -->
      <div class="item-checkbox">
        <input type="checkbox" :checked="item.isChecked === 1" @change="toggleItemChecked(item)" />
      </div>
      <div class="item-image" @click="goToProduct(item.productId)">
        <img :src="item.productImage" :alt="item.productName" />
      </div>
      <div class="item-info">
        <h4 class="item-name" @click="goToProduct(item.productId)">{{ item.productName }}</h4>
        <div class="item-price">¥{{ item.price }}</div>
        <div class="item-quantity-control">
          <button class="qty-btn" :disabled="item.quantity <= 1" @click="decreaseQuantity(item)">-</button>
          <span class="qty-value">{{ item.quantity }}</span>
          <button class="qty-btn" :disabled="item.quantity >= item.stock" @click="increaseQuantity(item)">+</button>
        </div>
      </div>
      <button class="remove-item-btn" @click="removeFromCart(item)">
        <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="3 6 5 6 21 6"></polyline>
          <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
        </svg>
      </button>
    </div>
  </div>
</div>
```

- [ ] **Step 3: Add merchant group styles**

```css
.merchant-group {
  margin-bottom: 16px;
}

.merchant-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: var(--color-bg);
  border-radius: var(--radius-md);
  margin-bottom: 8px;
}

.merchant-name {
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--color-text-primary);
}
```

- [ ] **Step 4: Update checkout to include merchantId**

In the `checkout()` method, update the mapped items:
```javascript
const selectedItems = this.selectedItems.map(item => ({
  cartItemId: item.id,
  productId: item.productId,
  productName: item.productName,
  productImage: item.productImage,
  price: item.price,
  quantity: item.quantity,
  categoryId: item.categoryId,
  merchantId: item.merchantId,
  merchantName: item.merchantName
}))
```

- [ ] **Step 5: Test with dev server**

Run: `cd user-frontend && npm run dev`
Open browser, add items from different merchants to cart, verify grouping displays correctly.

- [ ] **Step 6: Commit**

```bash
git add user-frontend/src/components/CartSidebar.vue
git commit -m "feat: CartSidebar group items by merchant"
```

---

## Task 8: Frontend — OrderConfirm Grouped by Merchant + Batch Payment

**Files:**
- Modify: `user-frontend/src/views/OrderConfirm.vue`
- Modify: `user-frontend/src/api/order.js`
- Modify: `user-frontend/src/router/index.js`

- [ ] **Step 1: Add computed property for grouped items**

```javascript
groupedItems() {
  const groups = {}
  this.selectedItems.forEach(item => {
    const key = item.merchantId || 0
    if (!groups[key]) {
      groups[key] = {
        merchantId: item.merchantId,
        merchantName: item.merchantName || '未知商家',
        items: [],
        subtotal: 0,
        freight: 5,
        couponAmount: 0,
        payAmount: 0
      }
    }
    groups[key].items.push(item)
    groups[key].subtotal += item.price * item.quantity
  })
  return Object.values(groups)
}
```

- [ ] **Step 2: Update product list template to render grouped**

Replace the flat product list with grouped rendering:
```html
<div class="product-list-section">
  <h2 class="section-title">商品信息</h2>
  <div class="product-list">
    <div v-for="group in groupedItems" :key="group.merchantId" class="merchant-order-group">
      <div class="merchant-group-header">
        <span class="merchant-icon">🏪</span>
        <span class="merchant-label">{{ group.merchantName }}</span>
      </div>
      <div v-for="item in group.items" :key="item.cartItemId || item.productId" class="product-item">
        <!-- existing product item template unchanged -->
      </div>
      <div class="merchant-group-summary">
        <span>小计：¥{{ group.subtotal.toFixed(2) }}</span>
        <span>运费：¥{{ group.freight.toFixed(2) }}</span>
      </div>
    </div>
  </div>
</div>
```

- [ ] **Step 3: Add merchant group styles**

```css
.merchant-order-group {
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  padding: 16px;
  margin-bottom: 16px;
}

.merchant-group-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--color-border-light);
  margin-bottom: 12px;
}

.merchant-label {
  font-size: var(--text-lg);
  font-weight: 600;
  color: var(--color-text-primary);
}

.merchant-group-summary {
  display: flex;
  justify-content: flex-end;
  gap: 24px;
  padding-top: 12px;
  border-top: 1px solid var(--color-border-light);
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
}
```

- [ ] **Step 4: Update freight calculation**

Update `loadData` to calculate freight per merchant group:
```javascript
this.freightAmount = this.groupedItems.length * 5
```

- [ ] **Step 5: Update order API for batch payment**

In `user-frontend/src/api/order.js`, change payOrder:
```javascript
export const payOrder = (payData) => request.post('/order/pay', payData)
```
The `payData` now contains `{ batchNo, userId, paymentType }` instead of `{ orderId, userId, paymentType }`.

- [ ] **Step 6: Update submitOrder to store batchNo**

```javascript
async submitOrder() {
  // ... existing validation ...
  try {
    const orderData = {
      userId: parseInt(userId),
      addressId: this.selectedAddress.id,
      couponIds: this.selectedCoupons,
      remark: this.orderRemark,
      cartItemIds
    }

    const result = await createOrder(orderData)
    if (result.code === 1 && result.data && result.data.length > 0) {
      localStorage.removeItem('selectedCartItems')
      // Store batchNo for payment page
      const batchNo = result.data[0].batchNo
      this.$router.push(`/payment/${batchNo}`)
    } else {
      this.$message.error(result.msg || '创建订单失败')
    }
  } catch (error) {
    console.error('创建订单失败:', error)
    this.$message.error('创建订单失败，请稍后重试')
  }
}
```

- [ ] **Step 7: Update router for batchNo-based payment route**

In `user-frontend/src/router/index.js`, change:
```javascript
{
  path: '/payment/:batchNo',
  name: 'Payment',
  component: Payment
}
```

- [ ] **Step 8: Test with dev server**

Run: `cd user-frontend && npm run dev`
Test full flow: cart → order confirm → submit → payment page.

- [ ] **Step 9: Commit**

```bash
git add user-frontend/src/views/OrderConfirm.vue
git add user-frontend/src/api/order.js
git add user-frontend/src/router/index.js
git commit -m "feat: OrderConfirm grouped by merchant with batch payment"
```

---

## Task 9: Frontend — Payment Page batchNo Support

**Files:**
- Modify: `user-frontend/src/views/Payment.vue`

- [ ] **Step 1: Update loadOrderData to use batchNo**

Replace the `loadOrderData` method:
```javascript
async loadOrderData() {
  const batchNo = this.$route.params.batchNo
  if (!batchNo) {
    this.$message.error('订单批次号不存在')
    this.$router.push('/')
    return
  }

  const userIdRaw = localStorage.getItem('userId')
  const userId = userIdRaw ? parseInt(userIdRaw) : NaN
  if (!userId || Number.isNaN(userId)) {
    this.$message.warning('请先登录')
    this.$router.push('/')
    return
  }

  this.batchNo = batchNo

  // Load all orders in this batch
  try {
    const result = await getOrderList(userId)
    if (result.code === 1 && result.data) {
      this.orders = result.data.filter(o => o.batchNo === batchNo && o.status === 0)
      if (this.orders.length === 0) {
        this.$message.error('未找到待付款订单')
        this.$router.push('/')
        return
      }
      this.totalPayAmount = this.orders.reduce((sum, o) => {
        const amount = typeof o.payAmount === 'number' ? o.payAmount : parseFloat(o.payAmount || 0)
        return sum + (Number.isFinite(amount) ? amount : 0)
      }, 0)
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    this.$message.error('加载订单失败')
  }
}
```

- [ ] **Step 2: Update data properties**

```javascript
data() {
  return {
    // ... existing ...
    batchNo: '',
    orders: [],
    totalPayAmount: 0,
    // Remove single order object
  }
}
```

- [ ] **Step 3: Update template to show multiple orders**

Update the order info section:
```html
<div class="order-info-section">
  <h2 class="section-title">订单信息</h2>
  <div v-for="order in orders" :key="order.id" class="order-brief">
    <div class="info-grid">
      <div class="info-item">
        <span class="info-label">商家：</span>
        <span class="info-value">{{ order.merchantName }}</span>
      </div>
      <div class="info-item">
        <span class="info-label">订单号：</span>
        <span class="info-value">{{ order.orderNo }}</span>
      </div>
      <div class="info-item">
        <span class="info-label">金额：</span>
        <span class="info-value price">¥{{ parseFloat(order.payAmount || 0).toFixed(2) }}</span>
      </div>
    </div>
  </div>
</div>
```

- [ ] **Step 4: Update confirmPayment to use batchNo**

```javascript
async confirmPayment() {
  this.isProcessing = true
  try {
    const userId = parseInt(localStorage.getItem('userId'))
    const result = await payOrder({
      batchNo: this.batchNo,
      userId,
      paymentType: this.getPaymentMethodName(this.selectedPaymentMethod)
    })
    if (result.code === 1) {
      setTimeout(() => {
        this.isProcessing = false
        this.showSuccessDialog = true
      }, 2000)
    } else {
      this.isProcessing = false
      this.errorMessage = result.msg || '支付失败'
      this.showFailedDialog = true
    }
  } catch (error) {
    this.isProcessing = false
    this.errorMessage = '网络错误'
    this.showFailedDialog = true
  }
}
```

- [ ] **Step 5: Update bottom bar to show total**

```html
<div class="total-display">
  <span>应付金额：</span>
  <span class="price">¥{{ totalPayAmount.toFixed(2) }}</span>
</div>
```

- [ ] **Step 6: Update success dialog to go to order list**

```javascript
goToOrderList() {
  this.$router.push('/personal?tab=orders')
}
```

- [ ] **Step 7: Add order brief styles**

```css
.order-brief {
  padding: 12px 0;
  border-bottom: 1px solid var(--color-border-light);
}
.order-brief:last-child {
  border-bottom: none;
}
```

- [ ] **Step 8: Test with dev server**

Test full payment flow end-to-end.

- [ ] **Step 9: Commit**

```bash
git add user-frontend/src/views/Payment.vue
git commit -m "feat: Payment page support batchNo-based multi-order payment"
```

---

## Task 10: Shop Frontend — Merchant ID Filter

**Files:**
- Modify: `shop-frontend/src/api/adminOrder.js`
- Modify: `shop-frontend/src/views/OrderManage.vue`

- [ ] **Step 1: Update adminOrder API**

The current API already uses `params` object, so merchantId will be passed automatically. No change needed to the API file.

- [ ] **Step 2: Add merchantId to OrderManage data**

In the component's `data()`, add:
```javascript
merchantId: null // Set from login info or route
```

In `mounted()` or `loadData()`, set merchantId from localStorage or session:
```javascript
this.merchantId = localStorage.getItem('merchantId')
```

- [ ] **Step 3: Pass merchantId in loadData**

```javascript
async loadData() {
  this.loading = true
  try {
    const params = {
      page: this.page,
      size: this.size,
      merchantId: this.merchantId
    }
    if (this.searchStatus !== '') {
      params.status = this.searchStatus
    }
    const res = await getOrderList(params)
    // ... existing response handling ...
  } finally {
    this.loading = false
  }
}
```

- [ ] **Step 4: Test**

Verify shop frontend only shows orders for the logged-in merchant.

- [ ] **Step 5: Commit**

```bash
git add shop-frontend/src/views/OrderManage.vue
git commit -m "feat: shop order list filter by merchantId"
```

---

## Task 11: End-to-End Verification

- [ ] **Step 1: Start backend**

```bash
cd backend
JAVA_HOME="C:/Program Files/Java/jdk1.8.0_xxx" mvn clean install -DskipTests
cd ecommerce-core && mvn spring-boot:run
```

- [ ] **Step 2: Start frontend dev servers**

```bash
cd user-frontend && npm run dev &
cd shop-frontend && npm run dev &
```

- [ ] **Step 3: Test cart flow with opencli**

- Login as user
- Add products from different merchants to cart
- Verify cart sidebar shows items grouped by merchant
- Verify checkout passes merchantId to order confirm

- [ ] **Step 4: Test order creation flow**

- On order confirm page, verify items grouped by merchant
- Select coupon, verify it applies
- Submit order
- Verify backend creates multiple orders with same batchNo
- Verify stock deducted correctly

- [ ] **Step 5: Test payment flow**

- On payment page, verify all merchant orders shown
- Pay, verify all orders change to status=1
- Check database: verify batchNo, merchantId, status

- [ ] **Step 6: Test cancel and confirm**

- Cancel one merchant order — verify only that order affected
- Confirm receipt on another — verify only that order affected

- [ ] **Step 7: Test shop management**

- Login as merchant
- Verify only see orders for that merchant
- Test ship functionality

- [ ] **Step 8: Run opencli browser tests**

Use opencli-browser to automate visual verification of each page.

- [ ] **Step 9: Final commit**

```bash
git add -A
git commit -m "feat: multi-merchant order split — end-to-end complete"
```
