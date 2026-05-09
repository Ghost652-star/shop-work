"""用户相关工具函数（优惠券、地址、收藏、购物车）"""

from datetime import datetime

from langchain_core.tools import tool

from config.database import get_conn


@tool(description="根据用户ID查询该用户所有的优惠券信息。**重要：请将结果以表格形式展示给用户，并按可用、即将过期、已使用、已过期分类展示**")
def get_user_coupons(user_id: str) -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()
        query = """
            SELECT
                c.id as coupon_id,
                c.description,
                c.min_spend,
                c.discount_amount,
                uc.status as use_status,
                uc.get_time,
                uc.expire_time,
                DATEDIFF(uc.expire_time, NOW()) as days_left
            FROM user_coupon uc
            INNER JOIN coupon c ON uc.coupon_id = c.id
            WHERE uc.user_id = %s
            ORDER BY uc.expire_time ASC
        """
        cursor.execute(query, (user_id,))
        results = cursor.fetchall()

        if not results:
            return "您目前没有任何优惠券"

        now = datetime.now()
        soon_expire_threshold = 3

        available = []
        soon_expiring = []
        used = []
        expired = []

        for row in results:
            coupon_id, description, min_spend, discount_amount, use_status, get_time, expire_time, days_left = row

            coupon_info = {
                "description": description or "优惠券",
                "min_spend": float(min_spend),
                "discount_amount": float(discount_amount),
                "expire_time": expire_time,
                "days_left": days_left,
            }

            if use_status == 1:
                used.append(coupon_info)
            elif use_status == 2 or (days_left is not None and days_left < 0):
                expired.append(coupon_info)
            else:
                if days_left is not None and 0 <= days_left <= soon_expire_threshold:
                    soon_expiring.append(coupon_info)
                else:
                    available.append(coupon_info)

        result_parts = []
        result_parts.append("📊 您的优惠券概览：")
        result_parts.append(f"- 可用优惠券：{len(available)} 张")
        result_parts.append(f"- 即将过期：{len(soon_expiring)} 张" + (" ⚠️ 请尽快使用！" if soon_expiring else ""))
        result_parts.append(f"- 已使用：{len(used)} 张")
        result_parts.append(f"- 已过期：{len(expired)} 张")
        result_parts.append("")

        if available:
            result_parts.append("✅ 可用优惠券：")
            result_parts.append("| 优惠券描述 | 折扣金额 | 最低消费 | 过期时间 | 剩余天数 |")
            result_parts.append("|-----------|---------|---------|---------|---------|")
            for coupon in available:
                expire_str = coupon["expire_time"].strftime("%Y-%m-%d") if coupon["expire_time"] else "未知"
                days = coupon["days_left"] if coupon["days_left"] is not None else "未知"
                result_parts.append(
                    f"| {coupon['description']} | ¥{coupon['discount_amount']:.2f} | "
                    f"¥{coupon['min_spend']:.2f} | {expire_str} | {days}天 |"
                )
            result_parts.append("")

        if soon_expiring:
            result_parts.append("⚠️ 即将过期优惠券（3天内）：")
            result_parts.append("| 优惠券描述 | 折扣金额 | 最低消费 | 过期时间 | 剩余天数 |")
            result_parts.append("|-----------|---------|---------|---------|---------|")
            for coupon in soon_expiring:
                expire_str = coupon["expire_time"].strftime("%Y-%m-%d") if coupon["expire_time"] else "未知"
                days = coupon["days_left"] if coupon["days_left"] is not None else 0
                result_parts.append(
                    f"| {coupon['description']} | ¥{coupon['discount_amount']:.2f} | "
                    f"¥{coupon['min_spend']:.2f} | {expire_str} | {days}天 🔥 |"
                )
            result_parts.append("")

        if used:
            result_parts.append("✓ 已使用优惠券：")
            result_parts.append("| 优惠券描述 | 折扣金额 | 最低消费 |")
            result_parts.append("|-----------|---------|---------|")
            for coupon in used:
                result_parts.append(
                    f"| {coupon['description']} | ¥{coupon['discount_amount']:.2f} | ¥{coupon['min_spend']:.2f} |"
                )
            result_parts.append("")

        if expired:
            result_parts.append("❌ 已过期优惠券：")
            result_parts.append("| 优惠券描述 | 折扣金额 | 最低消费 | 过期时间 |")
            result_parts.append("|-----------|---------|---------|---------|")
            for coupon in expired:
                expire_str = coupon["expire_time"].strftime("%Y-%m-%d") if coupon["expire_time"] else "未知"
                result_parts.append(
                    f"| {coupon['description']} | ¥{coupon['discount_amount']:.2f} | "
                    f"¥{coupon['min_spend']:.2f} | {expire_str} |"
                )
            result_parts.append("")

        suggestions = []
        if soon_expiring:
            suggestions.append(f"您有 {len(soon_expiring)} 张优惠券将在3天内过期，建议尽快使用！")
        if available:
            total_discount = sum(c["discount_amount"] for c in available)
            suggestions.append(f"您当前可用的优惠券总共可节省 ¥{total_discount:.2f}")

        if suggestions:
            result_parts.append("💡 建议：")
            for suggestion in suggestions:
                result_parts.append(f"- {suggestion}")

        return "\n".join(result_parts)

    except Exception as e:
        print(e)
        return "查询优惠券信息时出错"
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()


@tool(description="根据用户ID查询该用户的所有收货地址信息。**重要：请将结果以表格形式展示给用户，表格包含：收货人、电话、地址、是否默认**")
def get_user_addresses(user_id: str) -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()
        cursor.execute(
            "SELECT name, phone, province, city, district, detail_address, is_default "
            "FROM address WHERE user_id = %s ORDER BY is_default DESC, create_time DESC",
            (user_id,),
        )
        results = cursor.fetchall()

        if not results:
            return "您还没有添加任何收货地址"

        result_parts = []
        result_parts.append("📍 您的收货地址列表：")
        result_parts.append("")
        result_parts.append("| 收货人 | 电话 | 地址 | 是否默认 |")
        result_parts.append("|-------|------|------|---------|")

        for name, phone, province, city, district, detail_address, is_default in results:
            full_address = f"{province}{city}{district}{detail_address}"
            default_str = "✓ 是" if is_default == 1 else "否"
            result_parts.append(
                f"| {name} | {phone} | {full_address} | {default_str} |"
            )

        return "\n".join(result_parts)

    except Exception as e:
        print(e)
        return "查询地址信息时出错"
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()


@tool(description="根据用户ID查询该用户的收藏商品列表。**重要：请将结果以表格形式展示给用户，表格包含：商品名称、价格**")
def get_user_favorites(user_id: str) -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()
        query = """
            SELECT p.name, p.price, f.create_time
            FROM favorite f
            INNER JOIN product p ON f.product_id = p.id
            WHERE f.user_id = %s
            ORDER BY f.create_time DESC
        """
        cursor.execute(query, (user_id,))
        results = cursor.fetchall()

        if not results:
            return "您还没有收藏任何商品"

        result_parts = []
        result_parts.append(f"⭐ 您的收藏列表（共 {len(results)} 件商品）：")
        result_parts.append("")
        result_parts.append("| 商品名称 | 价格 |")
        result_parts.append("|---------|------|")

        for name, price, create_time in results:
            result_parts.append(f"| {name} | ¥{float(price):.2f} |")

        return "\n".join(result_parts)

    except Exception as e:
        print(e)
        return "查询收藏列表时出错"
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()


@tool(description="根据用户ID查询该用户的购物车商品列表。**重要：请将结果以表格形式展示给用户，表格包含：商品名称、单价、数量、小计**")
def get_user_cart(user_id: str) -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()
        cursor.execute(
            "SELECT product_name, price, quantity, is_checked "
            "FROM cart WHERE user_id = %s ORDER BY create_time DESC",
            (user_id,),
        )
        results = cursor.fetchall()

        if not results:
            return "您的购物车是空的"

        result_parts = []
        result_parts.append(f"🛒 您的购物车（共 {len(results)} 件商品）：")
        result_parts.append("")
        result_parts.append("| 商品名称 | 单价 | 数量 | 小计 |")
        result_parts.append("|---------|------|------|------|")

        total_amount = 0
        checked_count = 0

        for product_name, price, quantity, is_checked in results:
            subtotal = float(price) * quantity
            total_amount += subtotal
            if is_checked == 1:
                checked_count += 1
            result_parts.append(
                f"| {product_name} | ¥{float(price):.2f} | {quantity} | ¥{subtotal:.2f} |"
            )

        result_parts.append("")
        result_parts.append(f"💰 合计：¥{total_amount:.2f}（已选中 {checked_count} 件商品）")

        return "\n".join(result_parts)

    except Exception as e:
        print(e)
        return "查询购物车时出错"
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()
