# Multi-Merchant Order Split Design

## Overview

Refactor the order system to split orders by merchant. When a user checks out with items from multiple merchants, the backend creates independent orders per merchant (no parent order). All orders in the same checkout share a `batch_no` for payment grouping.

## Key Decisions

- **No parent order**: Each merchant order is fully independent after creation
- **batch_no**: Links orders created together for batch payment
- **No merchant_name redundancy**: Only `merchant_id` on orders, JOIN merchant table when needed
- **order_item**: No merchant fields needed (inherits via order_id)
- **Coupon distribution**: Proportional by merchant subtotal / total amount ratio

## Data Model Changes

### cart table
Add field:
```sql
`merchant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '商家ID（冗余，从product带入）'
```

### orders table
Add fields:
```sql
`merchant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '商家ID'
`batch_no`    VARCHAR(50) NULL COMMENT '下单批次号，同一次下单的多个商家订单共享'
```

### order_item table
No changes. Links to orders via order_id.

### Other tables
No changes. merchant, coupon, after_sale, after_sale_item remain as-is.

## Backend Changes

### OrderServiceImpl.createOrder - Refactored Flow

Break the monolithic method into private methods:

```
createOrder(OrderDTO) -> List<OrderVO>
  1. validateAddress(addressId, userId)
  2. resolveOrderItems(cartItemIds | items)
  3. groupByMerchant(items) -> Map<Long, List<OrderItemDTO>>
  4. generateBatchNo()
  5. For each merchant group:
     a. calculateGroupAmount(items) -> subtotal
     b. distributeCoupon(totalAmount, subtotal, coupons) -> couponAmount
     c. calcFreight() -> freightAmount (fixed 5 yuan for now)
     d. buildAndInsertOrder(merchantId, batchNo, amounts, address)
     e. insertOrderItems(orderId, items)
     f. insertOrderCoupons(orderId, distributedCoupons)
     g. updateCouponStatus(coupons, orderId)
  6. deductStock(allItems)
  7. clearCart(cartItemIds)
```

### Coupon Distribution Algorithm

For each merchant group:
```
groupCouponAmount = (groupSubtotal / totalAmount) * couponFaceValue
```

Round to 2 decimal places. Handle rounding remainder by assigning to the last group.

### OrderServiceImpl.payOrder - Batch Payment

```
payOrder(String batchNo, Long userId, String paymentType) -> boolean
  1. Query orders WHERE batch_no = ? AND user_id = ? AND status = 0
  2. Verify all orders are status=0
  3. Batch update all to status=1, set payment_type and payment_time
```

### OrderServiceImpl.cancelOrder - No Change

Still operates on single orderId. Restores stock/coupons for that order only.

### OrderServiceImpl.confirmOrder - No Change

Still operates on single orderId.

### CartServiceImpl Changes

- `addToCart`: Populate `merchant_id` from product's `merchant_id`
- `getCartList`: Return data includes `merchantId`
- `convertToVO`: Add `merchantId` to CartVO

### ShopOrderServiceImpl Changes

- `listOrders`: Add `merchantId` filter parameter
- Query: `WHERE merchant_id = ?`

### Entity Changes

**Cart.java**: Add `merchantId` field
**Order.java**: Add `merchantId`, `batchNo` fields
**OrderItem.java**: No change

### VO Changes

**CartVO**: Add `merchantId`
**OrderVO**: Add `merchantId`, `merchantName` (from JOIN, not stored)

### DTO Changes

**OrderDTO**: No change (cartItemIds already passed, backend resolves merchant)

## Frontend Changes

### CartSidebar.vue

Group cart items by `merchantId`:
```
[Merchant A]
  - item1 (checkbox, image, name, price, qty, remove)
  - item2 ...
[Merchant B]
  - item3 ...
```

Checkout button stores items with `merchantId` to localStorage.

### OrderConfirm.vue

Group selected items by `merchantId`:
```
┌─ Merchant A ──────────────────────┐
│  product1   ¥99   x2   ¥198      │
│  product2   ¥59   x1   ¥59       │
│  Subtotal: ¥257  Shipping: ¥5    │
└──────────────────────────────────┘
┌─ Merchant B ──────────────────────┐
│  product3   ¥120  x1   ¥120      │
│  Subtotal: ¥120  Shipping: ¥5    │
└──────────────────────────────────┘

Coupon: -¥20 (A: ¥13.70, B: ¥6.30)
Total payable: ¥367.00
```

After submit: store `batchNo` from response to localStorage, navigate to `/payment/:batchNo`.

### Payment.vue

- Receive batchNo from route param `this.$route.params.batchNo`
- Display all merchant orders and total amount
- Call `payOrder(batchNo, userId, paymentType)`
- On success, navigate to order list

### Order List (Personal Center)

- Show merchant name on each order (via merchantId JOIN)
- Each order is independent, no grouping

### Shop Frontend OrderManage.vue

- Pass `merchantId` when querying orders
- Each order shows only that merchant's items

## Scope Exclusion

- No changes to coupon table structure
- No changes to after_sale table structure
- No changes to product table structure
- No changes to address flow
- No changes to merchant table structure
