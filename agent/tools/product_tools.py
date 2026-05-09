"""商品相关工具函数"""

from typing import Optional

from langchain_core.tools import tool

from config.database import get_conn
from rag.rag_service import RagService


_rag_service: Optional[RagService] = None


def _get_rag_service() -> RagService:
    global _rag_service
    if _rag_service is None:
        _rag_service = RagService()
    return _rag_service


@tool(description=(
    "根据用户的需求描述，从商品库中语义检索并推荐相关商品。"
    "传入用户描述的商品需求（如'清洁洁面的廉价洗面奶'、'保湿补水的面霜'等），"
    "返回匹配的商品名称和ID列表，再由主Agent调用 get_product_detail 获取详细信息。"
    "用于回答用户关于商品推荐、寻找特定类型商品的问题。"
))
def search_products(product_desc: str) -> str:
    """语义检索商品"""
    try:
        print(f"\n[DEBUG] ===== 向量检索开始 =====")
        print(f"[DEBUG] 用户查询: {product_desc}")

        rag = _get_rag_service()
        results = rag.search_products(query=product_desc)

        print(f"[DEBUG] 检索到 {len(results)} 条结果")
        for i, (doc, score) in enumerate(results):
            print(f"[DEBUG]   结果 {i+1}: [{doc.metadata.get('product_id')}] "
                  f"{doc.metadata.get('product_name')} (相关性: {score:.4f})")

        print(f"[DEBUG] ===== 向量检索结束 =====\n")

        return rag.format_search_results(results, product_desc)

    except Exception as e:
        print(f"[ERROR] 向量检索异常: {e}")
        return f"检索商品时出错: {e}"


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
            result_parts.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
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
