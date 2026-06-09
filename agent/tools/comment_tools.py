"""商品评论相关工具函数"""

from langchain_core.tools import tool

from config.database import get_conn


@tool(description=(
    "根据商品ID查询该商品的用户评论，包括评分分布和评论内容。"
    "传入商品ID字符串（如 '1'），"
    "返回评分统计和评论列表。**重要：请以清晰的格式展示给用户**"
))
def get_product_comments(product_id: str) -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()

        cursor.execute(
            "SELECT AVG(rating), COUNT(*) FROM comment WHERE product_id = %s",
            (product_id,),
        )
        avg_row = cursor.fetchone()
        avg_rating = float(avg_row[0]) if avg_row and avg_row[0] else 0
        total_count = avg_row[1] if avg_row else 0

        if total_count == 0:
            return "该商品暂无用户评论"

        cursor.execute(
            "SELECT rating, COUNT(*) FROM comment WHERE product_id = %s GROUP BY rating ORDER BY rating DESC",
            (product_id,),
        )
        raw_dist = cursor.fetchall()
        dist_map = {r: c for r, c in raw_dist}
        dist_rows = [(s, dist_map.get(s, 0)) for s in range(5, 0, -1)]

        cursor.execute(
            "SELECT c.rating, c.content, c.create_time, u.nickname "
            "FROM comment c LEFT JOIN user u ON c.user_id = u.id "
            "WHERE c.product_id = %s ORDER BY c.create_time DESC LIMIT 10",
            (product_id,),
        )
        comments = cursor.fetchall()

        parts = []
        parts.append(f"💬 商品评论概览（共 {total_count} 条）")
        parts.append(f"━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        parts.append(f"  综合评分: {'★' * round(avg_rating)}{'☆' * (5 - round(avg_rating))} {avg_rating:.1f}/5.0")
        parts.append("")
        parts.append("  评分分布：")
        for star_val, cnt in dist_rows:
            if cnt is None:
                cnt = 0
            bar = "█" * min(cnt, 20)
            parts.append(f"    {star_val}星 {bar} {cnt}条")
        parts.append("")
        parts.append("  最新评论：")
        parts.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        for rating, content, ctime, nickname in comments:
            name = nickname or "匿名用户"
            stars = "★" * rating
            parts.append(f"  {name} {stars}")
            if content:
                parts.append(f"  {content[:80]}")
            parts.append(f"  {ctime}")
            parts.append("")

        return "\n".join(parts)

    except Exception as e:
        print(e)
        return "查询商品评论时出错"
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()
