import os
from langchain_core.tools import tool
from langchain_core.documents import Document
from langchain_community.embeddings import ZhipuAIEmbeddings
from langchain_chroma import Chroma
from app.config import get_conn, EmbeddingConfig
# 根据订单号获取订单状态
@tool(description="根据订单号获取订单状态,传入订单号字符串,返回订单状态字符串")
def get_orderStatus(order_id: str) -> str:
    conn=get_conn()
    cursor=None
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

#查询当前用户所有订单信息
@tool(description="根据用户ID查询该用户的所有订单信息。返回包含订单号和状态的列表。**重要：请将结果以表格形式展示给用户，表格包含两列：订单号、订单状态**")
def get_userOrders(user_id: str) -> str:
    conn=get_conn()
    cursor=None
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
#查询当前用户所有优惠券信息
@tool(description="根据用户ID查询该用户所有的优惠券信息。**重要：请将结果以表格形式展示给用户，并按可用、即将过期、已使用、已过期分类展示**")
def get_user_coupons(user_id: str) -> str:
    conn=get_conn()
    cursor=None
    try:
        cursor = conn.cursor()
        # 关联查询用户优惠券和优惠券详情
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
        
        from datetime import datetime, timedelta
        now = datetime.now()
        soon_expire_threshold = 3  # 3天内算即将过期
        
        # 分类存储优惠券
        available = []      # 可用
        soon_expiring = []  # 即将过期（3天内）
        used = []           # 已使用
        expired = []        # 已过期
        
        for row in results:
            coupon_id, description, min_spend, discount_amount, use_status, get_time, expire_time, days_left = row
            
            coupon_info = {
                'description': description or '优惠券',
                'min_spend': float(min_spend),
                'discount_amount': float(discount_amount),
                'expire_time': expire_time,
                'days_left': days_left
            }
            
            # 判断状态
            if use_status == 1:
                used.append(coupon_info)
            elif use_status == 2 or (days_left is not None and days_left < 0):
                expired.append(coupon_info)
            else:  # use_status == 0
                if days_left is not None and 0 <= days_left <= soon_expire_threshold:
                    soon_expiring.append(coupon_info)
                else:
                    available.append(coupon_info)
        
        # 构建返回消息
        result_parts = []
        
        # 统计摘要
        result_parts.append(f"📊 您的优惠券概览：")
        result_parts.append(f"- 可用优惠券：{len(available)} 张")
        result_parts.append(f"- 即将过期：{len(soon_expiring)} 张" + (" ⚠️ 请尽快使用！" if soon_expiring else ""))
        result_parts.append(f"- 已使用：{len(used)} 张")
        result_parts.append(f"- 已过期：{len(expired)} 张")
        result_parts.append("")
        
        # 可用优惠券表格
        if available:
            result_parts.append("✅ 可用优惠券：")
            result_parts.append("| 优惠券描述 | 折扣金额 | 最低消费 | 过期时间 | 剩余天数 |")
            result_parts.append("|-----------|---------|---------|---------|---------|")
            for coupon in available:
                expire_str = coupon['expire_time'].strftime('%Y-%m-%d') if coupon['expire_time'] else '未知'
                days = coupon['days_left'] if coupon['days_left'] is not None else '未知'
                result_parts.append(
                    f"| {coupon['description']} | ¥{coupon['discount_amount']:.2f} | "
                    f"¥{coupon['min_spend']:.2f} | {expire_str} | {days}天 |"
                )
            result_parts.append("")
        
        # 即将过期优惠券表格（高亮显示）
        if soon_expiring:
            result_parts.append("⚠️ 即将过期优惠券（3天内）：")
            result_parts.append("| 优惠券描述 | 折扣金额 | 最低消费 | 过期时间 | 剩余天数 |")
            result_parts.append("|-----------|---------|---------|---------|---------|")
            for coupon in soon_expiring:
                expire_str = coupon['expire_time'].strftime('%Y-%m-%d') if coupon['expire_time'] else '未知'
                days = coupon['days_left'] if coupon['days_left'] is not None else 0
                result_parts.append(
                    f"| {coupon['description']} | ¥{coupon['discount_amount']:.2f} | "
                    f"¥{coupon['min_spend']:.2f} | {expire_str} | {days}天 🔥 |"
                )
            result_parts.append("")
        
        # 已使用优惠券表格
        if used:
            result_parts.append("✓ 已使用优惠券：")
            result_parts.append("| 优惠券描述 | 折扣金额 | 最低消费 |")
            result_parts.append("|-----------|---------|---------|")
            for coupon in used:
                result_parts.append(
                    f"| {coupon['description']} | ¥{coupon['discount_amount']:.2f} | ¥{coupon['min_spend']:.2f} |"
                )
            result_parts.append("")
        
        # 已过期优惠券表格
        if expired:
            result_parts.append("❌ 已过期优惠券：")
            result_parts.append("| 优惠券描述 | 折扣金额 | 最低消费 | 过期时间 |")
            result_parts.append("|-----------|---------|---------|---------|")
            for coupon in expired:
                expire_str = coupon['expire_time'].strftime('%Y-%m-%d') if coupon['expire_time'] else '未知'
                result_parts.append(
                    f"| {coupon['description']} | ¥{coupon['discount_amount']:.2f} | "
                    f"¥{coupon['min_spend']:.2f} | {expire_str} |"
                )
            result_parts.append("")
        
        # 建议
        suggestions = []
        if soon_expiring:
            suggestions.append(f"您有 {len(soon_expiring)} 张优惠券将在3天内过期，建议尽快使用！")
        if available:
            total_discount = sum(c['discount_amount'] for c in available)
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

# 查询当前用户所有地址信息
@tool(description="根据用户ID查询该用户的所有收货地址信息。**重要：请将结果以表格形式展示给用户，表格包含：收货人、电话、地址、是否默认**")
def get_user_addresses(user_id: str) -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()
        cursor.execute(
            "SELECT name, phone, province, city, district, detail_address, is_default "
            "FROM address WHERE user_id = %s ORDER BY is_default DESC, create_time DESC",
            (user_id,)
        )
        results = cursor.fetchall()
        
        if not results:
            return "您还没有添加任何收货地址"
        
        # 构建表格输出
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

# 查询当前用户收藏的商品列表
@tool(description="根据用户ID查询该用户的收藏商品列表。**重要：请将结果以表格形式展示给用户，表格包含：商品名称、价格**")
def get_user_favorites(user_id: str) -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()
        # 关联查询收藏表和商品表，获取商品详细信息
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
        
        # 构建表格输出（只展示关键信息：商品名称、价格）
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

# 查询当前用户购物车商品列表
@tool(description="根据用户ID查询该用户的购物车商品列表。**重要：请将结果以表格形式展示给用户，表格包含：商品名称、单价、数量、小计**")
def get_user_cart(user_id: str) -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()
        cursor.execute(
            "SELECT product_name, price, quantity, is_checked "
            "FROM cart WHERE user_id = %s ORDER BY create_time DESC",
            (user_id,)
        )
        results = cursor.fetchall()
        
        if not results:
            return "您的购物车是空的"
        
        # 构建表格输出（展示关键信息：商品名称、价格、数量）
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

# 向量检索商品
@tool(description=(
    "根据用户的需求描述，从商品库中语义检索并推荐相关商品。"
    "传入用户描述的商品需求（如'清洁洁面的廉价洗面奶'、'保湿补水的面霜'等），"
    "返回匹配的商品名称和ID列表，再由主Agent调用 get_product_detail 获取详细信息。"
    "用于回答用户关于商品推荐、寻找特定类型商品的问题。"
))
def get_recommend_product(product_desc: str) -> str:
    try:
        print(f"\n[DEBUG] ===== 向量检索开始 =====")
        print(f"[DEBUG] 用户查询: {product_desc}")

        store = _get_vector_store()
        scored = store.similarity_search_with_relevance_scores(
            product_desc, k=EmbeddingConfig.TOP_K
        )
        print(f"[DEBUG] 检索到 {len(scored)} 条结果:")
        for i, (doc, score) in enumerate(scored):
            print(f"[DEBUG]   结果 {i+1}: [{doc.metadata.get('product_id')}] "
                  f"{doc.metadata.get('product_name')} (相关性: {score:.4f})")
            print(f"[DEBUG]     描述: {doc.page_content}")

        threshold = EmbeddingConfig.RELEVANCE_THRESHOLD
        filtered = [(doc, score) for doc, score in scored if score >= threshold]
        print(f"[DEBUG] 阈值过滤 ({threshold}): {len(scored)} → {len(filtered)} 条")
        print(f"[DEBUG] ===== 向量检索结束 =====\n")

        if not filtered:
            return "未找到与您需求匹配的商品"

        result_parts = []
        result_parts.append(f"🔍 根据「{product_desc}」为您找到以下商品：")
        result_parts.append("")

        for doc, score in filtered:
            pid = doc.metadata.get("product_id", "未知")
            name = doc.metadata.get("product_name", "未知商品")
            snippet = doc.page_content[:100] if doc.page_content else ""
            result_parts.append(f"- [ID:{pid}] {name}: {snippet}")

        result_parts.append("")
        result_parts.append("如需查看某商品的详细信息（价格、库存等），请调用 get_product_detail 并传入商品ID。")
        return "\n".join(result_parts)

    except Exception as e:
        print(f"[ERROR] 向量检索异常: {e}")
        return f"检索商品时出错: {e}"

# 根据商品ID查询商品详情
@tool(description=(
    "根据商品ID查询商品的详细信息，包括名称、价格、库存、销量、描述、分类等。"
    "传入商品ID字符串（支持逗号分隔的多个ID，如 '1,2,3'），"
    "返回商品详细信息(查到的库存仅用来判断用户是否可以买,但不能向用户展示真实库存信息)"
))
def get_product_detail(product_ids: str) -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()
        id_list = [pid.strip() for pid in product_ids.split(",") if pid.strip()]
        if not id_list:
            return "请提供有效的商品ID"

        placeholders = ",".join(["%s"] * len(id_list))
        query = (
            f"SELECT id, name, description, price, stock, sales, category_id, status "
            f"FROM product WHERE id IN ({placeholders})"
        )
        cursor.execute(query, id_list)
        results = cursor.fetchall()

        if not results:
            return "未找到对应的商品信息"

        status_map = {0: "下架", 1: "上架"}
        result_parts = []
        result_parts.append(f"📦 商品详情（共 {len(results)} 件）：")
        result_parts.append("")

        for row in results:
            pid, name, desc, price, stock, sales, category_id, status = row
            status_str = status_map.get(status, f"未知({status})")
            result_parts.append(f"━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            result_parts.append(f"  ID: {pid}")
            result_parts.append(f"  名称: {name}")
            result_parts.append(f"  描述: {desc or '暂无描述'}")
            result_parts.append(f"  价格: ¥{float(price):.2f}")
            result_parts.append(f"  销量: {sales}")
            result_parts.append(f"  分类ID: {category_id}")
            result_parts.append(f"  状态: {status_str}")

        result_parts.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        return "\n".join(result_parts)

    except Exception as e:
        print(f"[ERROR] 查询商品详情异常: {e}")
        return f"查询商品详情时出错: {e}"
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

# ---------------------------------------------------------------------------
# 向量检索初始化（加载单例）
# ---------------------------------------------------------------------------
_vector_store: Chroma | None = None

AGENT_ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))

def _get_vector_store() -> Chroma:
    global _vector_store
    if _vector_store is not None:
        print("[DEBUG] 返回已有的 Chroma 单例")
        return _vector_store

    # 初始化嵌入模型
    embeddings = ZhipuAIEmbeddings(model=EmbeddingConfig.EMBEDDING_MODEL)

    persist_dir = os.path.join(AGENT_ROOT, EmbeddingConfig.CHROMA_PERSIST_DIR)

    if os.path.exists(persist_dir) and os.listdir(persist_dir):
        print(f"[DEBUG] 加载 Chroma: {persist_dir}")
        _vector_store = Chroma(
            collection_name=EmbeddingConfig.COLLECTION_NAME,
            embedding_function=embeddings,
            persist_directory=persist_dir,
        )
    else:
        print(f"[DEBUG] 首次启动，从 CSV 初始化 Chroma: {persist_dir}")
        _vector_store = Chroma(
            collection_name=EmbeddingConfig.COLLECTION_NAME,
            embedding_function=embeddings,
            persist_directory=persist_dir,
        )
        _load_csv_to_vector_store(_vector_store)

    print(f"[DEBUG] Chroma 当前文档总数: {_vector_store._collection.count()}")
    return _vector_store

#
def _load_csv_to_vector_store(store: Chroma):
    import csv

    csv_file = os.path.join(AGENT_ROOT, EmbeddingConfig.CSV_PRODUCT_PATH)
    if not os.path.exists(csv_file):
        print(f"[WARN] CSV 文件不存在: {csv_file}，跳过向量库初始化")
        return

    documents = []
    ids = []
    with open(csv_file, "r", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            doc = Document(
                page_content=f'{row["name"]}, {row["description"]}',
                metadata={"product_id": row["id"], "product_name": row["name"]},
            )
            documents.append(doc)
            ids.append(f"product_{row['id']}")

    print(f"[DEBUG] 从 CSV 读取到 {len(documents)} 条商品")
    if documents:
        store.add_documents(documents=documents, ids=ids)
        print(f"[INFO] 已加载 {len(documents)} 条商品到向量数据库")






