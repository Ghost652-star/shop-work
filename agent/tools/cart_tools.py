"""购物车操作工具函数"""

from langchain_core.tools import tool

from config.database import get_conn


@tool(description=(
    "将商品添加到用户的购物车。"
    "传入用户ID和商品ID（product_id），以及可选的数量（默认1）。"
    "如果商品已存在于购物车，则增加数量。"
    "用于响应用户「加入购物车」、「帮我加购物车」等请求。"
))
def add_to_cart(user_id: str, product_id: str, quantity: int = 1) -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()

        # 检查商品是否存在且上架
        cursor.execute(
            "SELECT id, name, price, stock, status FROM product WHERE id = %s",
            (product_id,),
        )
        product = cursor.fetchone()
        if not product:
            return f"商品ID {product_id} 不存在"
        if product[4] != 1:
            return f"商品「{product[1]}」已下架，无法添加到购物车"
        if product[3] < quantity:
            return f"商品「{product[1]}」库存不足，当前库存：{product[3]}"

        # 检查购物车中是否已有该商品
        cursor.execute(
            "SELECT id, quantity FROM cart WHERE user_id = %s AND product_id = %s",
            (user_id, product_id),
        )
        existing = cursor.fetchone()

        if existing:
            new_qty = existing[1] + quantity
            cursor.execute(
                "UPDATE cart SET quantity = %s WHERE id = %s",
                (new_qty, existing[0]),
            )
            conn.commit()
            return f"已将「{product[1]}」的数量增加到 {new_qty} 件（原 {existing[1]} 件 + 新增 {quantity} 件）"
        else:
            cursor.execute(
                "INSERT INTO cart (user_id, product_id, product_name, price, quantity, is_checked) "
                "VALUES (%s, %s, %s, %s, %s, 1)",
                (user_id, product_id, product[1], product[2], quantity),
            )
            conn.commit()
            return f"已将「{product[1]}」（¥{float(product[2]):.2f} x {quantity}）加入购物车"

    except Exception as e:
        print(f"[ERROR] 添加购物车异常: {e}")
        return f"添加购物车时出错: {e}"
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()
