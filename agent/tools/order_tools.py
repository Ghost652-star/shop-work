"""订单相关工具函数 - 支持一句话下单流程"""

import time
import random

from langchain_core.tools import tool

from config.database import get_conn


@tool(description="根据订单号获取订单状态,传入订单号字符串,返回订单状态字符串")
def get_orderStatus(order_id: str) -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()
        cursor.execute("SELECT status FROM orders WHERE order_no = %s", (order_id,))
        result = cursor.fetchone()
        if result:
            status_code = str(result[0])
            status_map = {
                "0": "待付款",
                "1": "待发货",
                "2": "待收货",
                "3": "已完成",
                "4": "已取消",
            }
            return status_map.get(status_code, f"未知状态({status_code})")
        else:
            return "订单不存在"
    except Exception as e:
        print(e)
        return "查询订单状态时出错"
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()


@tool(description=(
    "根据用户ID查询该用户某个订单的详细信息，包括商品明细、金额、收货地址等。"
    "传入订单号字符串（如 'ORDER202601010001'），"
    "返回订单完整详情。**重要：请以清晰的格式展示给用户**"
))
def get_order_detail(order_no: str) -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()
        cursor.execute(
            "SELECT id, order_no, status, total_amount, freight_amount, "
            "coupon_amount, pay_amount, receiver_name, receiver_phone, "
            "receiver_province, receiver_city, receiver_district, "
            "receiver_detail_address, remark, payment_type, payment_time, "
            "create_time "
            "FROM orders WHERE order_no = %s",
            (order_no,),
        )
        row = cursor.fetchone()
        if not row:
            return f"未找到订单 {order_no} 的信息"

        (oid, o_no, status, total, freight, coupon_amt, pay_amt,
         r_name, r_phone, r_prov, r_city, r_dist, r_detail,
         remark, pay_type, pay_time, create_time) = row

        status_map = {
            "0": "待付款", "1": "待发货", "2": "待收货",
            "3": "已完成", "4": "已取消",
        }
        status_str = status_map.get(str(status), f"未知({status})")

        cursor.execute(
            "SELECT product_name, price, quantity, total_price "
            "FROM order_item WHERE order_id = %s",
            (oid,),
        )
        items = cursor.fetchall()

        parts = []
        parts.append(f"📋 订单详情（订单号: {o_no}）")
        parts.append(f"━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        parts.append(f"  状态: {status_str}")
        parts.append(f"  创建时间: {create_time}")
        if pay_time:
            parts.append(f"  支付时间: {pay_time}")
        if pay_type:
            parts.append(f"  支付方式: {pay_type}")
        parts.append("")
        parts.append("  🛒 商品明细：")
        if items:
            parts.append("  | 商品名称 | 单价 | 数量 | 小计 |")
            parts.append("  |---------|------|------|------|")
            for pname, price, qty, sub in items:
                parts.append(f"  | {pname} | ¥{float(price):.2f} | {qty} | ¥{float(sub):.2f} |")
        parts.append("")
        parts.append(f"  💰 金额明细：")
        parts.append(f"    商品总额: ¥{float(total):.2f}")
        parts.append(f"    运费: ¥{float(freight):.2f}")
        if float(coupon_amt) > 0:
            parts.append(f"    优惠券抵扣: -¥{float(coupon_amt):.2f}")
        parts.append(f"    实付金额: ¥{float(pay_amt):.2f}")
        parts.append("")
        full_addr = f"{r_prov}{r_city}{r_dist}{r_detail}"
        parts.append(f"  📍 收货信息：{r_name} {r_phone} {full_addr}")
        if remark:
            parts.append(f"  📝 备注: {remark}")
        parts.append(f"━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

        return "\n".join(parts)

    except Exception as e:
        print(e)
        return "查询订单详情时出错"
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()


@tool(description=(
    "根据订单号取消用户的未付款订单。传入订单号字符串和用户ID字符串。"
    "只有状态为'待付款'的订单可以取消。"
    "返回操作结果。"
))
def cancel_order(order_no: str, user_id: str) -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()
        cursor.execute(
            "SELECT id, status FROM orders WHERE order_no = %s AND user_id = %s",
            (order_no, user_id),
        )
        row = cursor.fetchone()
        if not row:
            return f"未找到订单 {order_no}"

        oid, status = row
        if str(status) != "0":
            status_map = {"0": "待付款", "1": "待发货", "2": "待收货", "3": "已完成", "4": "已取消"}
            return f"订单当前状态为「{status_map.get(str(status), '未知')}」，只有待付款的订单可以取消"

        cursor.execute("UPDATE orders SET status = 4 WHERE id = %s", (oid,))
        conn.commit()
        return f"✅ 订单 {order_no} 已成功取消"

    except Exception as e:
        conn.rollback()
        print(e)
        return "取消订单时出错"
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()


@tool(description=(
    "根据订单号确认收货。传入订单号字符串和用户ID字符串。"
    "只有状态为'待收货'的订单可以确认收货。"
    "返回操作结果。"
))
def confirm_order(order_no: str, user_id: str) -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()
        cursor.execute(
            "SELECT id, status FROM orders WHERE order_no = %s AND user_id = %s",
            (order_no, user_id),
        )
        row = cursor.fetchone()
        if not row:
            return f"未找到订单 {order_no}"

        oid, status = row
        if str(status) != "2":
            status_map = {"0": "待付款", "1": "待发货", "2": "待收货", "3": "已完成", "4": "已取消"}
            return f"订单当前状态为「{status_map.get(str(status), '未知')}」，只有待收货的订单可以确认收货"

        cursor.execute("UPDATE orders SET status = 3 WHERE id = %s", (oid,))
        conn.commit()
        return f"✅ 订单 {order_no} 已确认收货"

    except Exception as e:
        conn.rollback()
        print(e)
        return "确认收货时出错"
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()


@tool(description="根据用户ID查询该用户的所有订单信息。返回包含订单号和状态的列表。**重要：请将结果以表格形式展示给用户，表格包含两列：订单号、订单状态**")
def get_userOrders(user_id: str) -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()
        cursor.execute("SELECT order_no, status FROM orders WHERE user_id = %s", (user_id,))
        results = cursor.fetchall()
        if results:
            status_map = {
                "0": "待付款",
                "1": "待发货",
                "2": "待收货",
                "3": "已完成",
                "4": "已取消",
            }
            orders_info = []
            for order_no, status_code in results:
                status_str = status_map.get(str(status_code), f"未知状态({status_code})")
                orders_info.append(f"订单号: {order_no}, 状态: {status_str}")
            return "\n".join(orders_info)
        else:
            return "没有找到您的订单信息"
    except Exception as e:
        print(e)
        return "查询订单信息时出错"
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()


# ==================== 一句话下单流程工具 ====================


@tool(description=(
    "获取用户的收货地址列表（用于下单选择）。"
    "传入用户ID（user_id），返回该用户的所有收货地址，包括地址ID用于后续下单。"
    "返回格式：每行一个地址，格式为 '序号. 【地址ID: xxx】收货人，电话，完整地址（默认）'。"
    "重要：默认地址会在地址后面标注'（默认）'，记住默认地址的ID用于后续创建订单。"
))
def get_addresses_for_order(user_id: str) -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()
        cursor.execute(
            "SELECT id, name, phone, province, city, district, detail_address, is_default "
            "FROM address WHERE user_id = %s ORDER BY is_default DESC, create_time DESC",
            (user_id,),
        )
        addresses = cursor.fetchall()

        if not addresses:
            return "您还没有收货地址，请先在个人中心添加收货地址"

        result = "您的收货地址列表：\n"
        for i, addr in enumerate(addresses, 1):
            default_mark = "（默认）" if addr[7] == 1 else ""
            full_address = f"{addr[3]}{addr[4]}{addr[5]}{addr[6]}"
            result += f"{i}. 【地址ID: {addr[0]}】{addr[1]}，{addr[2]}，{full_address}{default_mark}\n"

        return result

    except Exception as e:
        print(f"[ERROR] 获取地址列表异常: {e}")
        return f"获取地址列表时出错: {e}"
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()


@tool(description=(
    "为用户创建订单（必须调用此工具来下单）。"
    "参数：user_id（用户ID）、address_id（收货地址ID，从get_addresses_for_order返回的地址ID中获取）。"
    "可选参数：remark（备注）。"
    "返回：订单号、批次号、商品明细、金额等信息。"
    "该工具会自动下单购物车中所有已勾选的商品（is_checked=1）。"
    "使用流程：先调用get_addresses_for_order获取地址，用户确认地址后直接调用此工具。"
    "用户说'使用默认地址'时，使用is_default=1的地址ID。"
))
def create_order(user_id: str, address_id: str, remark: str = "") -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()

        # 1. 验证地址
        cursor.execute(
            "SELECT id, name, phone, province, city, district, detail_address "
            "FROM address WHERE id = %s AND user_id = %s",
            (address_id, user_id),
        )
        address = cursor.fetchone()
        if not address:
            return "收货地址不存在或不属于当前用户"

        # 2. 查询购物车中已勾选的商品
        cursor.execute(
            "SELECT id, product_id, product_name, price, quantity FROM cart WHERE user_id = %s AND is_checked = 1",
            (user_id,),
        )
        cart_items = cursor.fetchall()

        if not cart_items:
            return "购物车中没有已勾选的商品，请先添加商品到购物车"

        # 4. 计算总金额
        total_amount = 0
        item_details = []
        for item in cart_items:
            subtotal = float(item[3]) * item[4]
            total_amount += subtotal
            item_details.append({
                "cart_id": item[0],
                "product_id": item[1],
                "product_name": item[2],
                "price": float(item[3]),
                "quantity": item[4],
                "subtotal": subtotal,
            })

        # 5. 计算优惠券抵扣（简化处理）
        coupon_amount = 0

        # 6. 计算运费和实付金额
        freight_amount = 5.00  # 固定运费
        pay_amount = max(0, total_amount + freight_amount - coupon_amount)

        # 7. 生成批次号和订单号
        timestamp = int(time.time() * 1000)
        batch_no = f"BAT{timestamp}{random.randint(100, 999)}"
        order_no = f"ORD{timestamp}{random.randint(100, 999)}"

        # 8. 创建订单
        cursor.execute(
            "INSERT INTO orders (order_no, batch_no, user_id, merchant_id, total_amount, freight_amount, "
            "coupon_amount, pay_amount, status, receiver_name, receiver_phone, receiver_province, "
            "receiver_city, receiver_district, receiver_detail_address, remark, create_time) "
            "VALUES (%s, %s, %s, %s, %s, %s, %s, %s, 0, %s, %s, %s, %s, %s, %s, %s, NOW())",
            (
                order_no, batch_no, user_id, 1,  # merchant_id 默认为1
                total_amount, freight_amount, coupon_amount, pay_amount,
                address[1], address[2], address[3], address[4], address[5], address[6],
                remark,
            ),
        )
        order_id = cursor.lastrowid

        # 9. 创建订单商品明细
        for item in item_details:
            # 查询商品分类ID
            cursor.execute("SELECT category_id FROM product WHERE id = %s", (item["product_id"],))
            product_info = cursor.fetchone()
            category_id = product_info[0] if product_info else 1

            cursor.execute(
                "INSERT INTO order_item (order_id, product_id, product_name, product_image, category_id, price, quantity, total_price) "
                "VALUES (%s, %s, %s, %s, %s, %s, %s, %s)",
                (order_id, item["product_id"], item["product_name"], "", category_id, item["price"], item["quantity"], item["subtotal"]),
            )

        # 10. 扣减库存
        for item in item_details:
            cursor.execute(
                "UPDATE product SET stock = stock - %s WHERE id = %s AND stock >= %s",
                (item["quantity"], item["product_id"], item["quantity"]),
            )
            if cursor.rowcount == 0:
                conn.rollback()
                return f"商品「{item['product_name']}」库存不足，下单失败"

        # 11. 清空已下单的购物车商品
        cart_ids = [item["cart_id"] for item in item_details]
        placeholders = ",".join(["%s"] * len(cart_ids))
        cursor.execute(
            f"DELETE FROM cart WHERE id IN ({placeholders}) AND user_id = %s",
            cart_ids + [user_id],
        )

        conn.commit()

        # 12. 返回订单信息
        result = f"订单创建成功！\n"
        result += f"订单号：{order_no}\n"
        result += f"批次号：{batch_no}\n"
        result += f"收货地址：{address[1]}，{address[2]}，{address[3]}{address[4]}{address[5]}{address[6]}\n"
        result += f"商品明细：\n"
        for item in item_details:
            result += f"  - {item['product_name']} × {item['quantity']} = ¥{item['subtotal']:.2f}\n"
        result += f"商品总额：¥{total_amount:.2f}\n"
        result += f"运费：¥{freight_amount:.2f}\n"
        if coupon_amount > 0:
            result += f"优惠券抵扣：-¥{coupon_amount:.2f}\n"
        result += f"实付金额：¥{pay_amount:.2f}\n"
        result += f"订单状态：待付款\n"
        result += f"\n请使用 pay_order 工具完成支付"

        return result

    except Exception as e:
        conn.rollback()
        print(f"[ERROR] 创建订单异常: {e}")
        return f"创建订单时出错: {e}"
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()


@tool(description=(
    "为用户完成订单支付（模拟免密支付）。"
    "传入用户ID（user_id）和批次号（batch_no）。"
    "将订单状态从待付款改为待发货，完成支付流程。"
    "用于用户确认订单后快速完成支付。"
))
def pay_order(user_id: str, batch_no: str) -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()

        # 1. 查询该批次号下的所有订单
        cursor.execute(
            "SELECT id, order_no, pay_amount, status FROM orders WHERE batch_no = %s AND user_id = %s",
            (batch_no, user_id),
        )
        orders = cursor.fetchall()

        if not orders:
            return f"未找到批次号 {batch_no} 对应的订单"

        # 2. 验证所有订单状态为待付款
        for order in orders:
            if order[3] != 0:
                return f"订单 {order[1]} 状态不是待付款，无法支付"

        # 3. 更新所有订单状态为待发货
        for order in orders:
            cursor.execute(
                "UPDATE orders SET status = 1, payment_type = '免密支付', payment_time = NOW() "
                "WHERE id = %s",
                (order[0],),
            )

        conn.commit()

        # 4. 返回支付结果
        total_pay = sum(float(order[2]) for order in orders)
        result = f"支付成功！\n"
        result += f"批次号：{batch_no}\n"
        result += f"支付方式：免密支付\n"
        result += f"支付金额：¥{total_pay:.2f}\n"
        result += f"订单数量：{len(orders)} 个\n"
        result += f"\n订单状态已更新为：待发货\n"
        result += f"感谢您的购买！"

        return result

    except Exception as e:
        conn.rollback()
        print(f"[ERROR] 支付订单异常: {e}")
        return f"支付订单时出错: {e}"
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()
