"""售后相关工具函数"""

from langchain_core.tools import tool

from config.database import get_conn


@tool(description=(
    "根据订单号查询用户的售后进度。传入订单号字符串，"
    "返回该订单的售后申请状态和处理进度。"
))
def get_after_sale_status(order_no: str) -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()
        cursor.execute(
            "SELECT o.order_no, a.id, a.status, a.reason, a.description, "
            "a.refund_amount, a.admin_remark, a.create_time, a.update_time "
            "FROM after_sale a INNER JOIN orders o ON a.order_id = o.id "
            "WHERE o.order_no = %s ORDER BY a.create_time DESC LIMIT 1",
            (order_no,),
        )
        row = cursor.fetchone()
        if not row:
            return f"订单 {order_no} 没有任何售后记录"

        (o_no, as_id, status, reason, desc, refund_amt, admin_remark,
         create_time, update_time) = row

        status_map = {
            0: "⏳ 待处理",
            1: "✅ 已通过",
            2: "❌ 已驳回",
            3: "🏁 已完成",
        }
        status_str = status_map.get(status, f"未知({status})")

        parts = []
        parts.append(f"📦 售后进度（订单号: {o_no}）")
        parts.append(f"━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        parts.append(f"  售后单号: #{as_id}")
        parts.append(f"  当前状态: {status_str}")
        parts.append(f"  申请原因: {reason}")
        if desc:
            parts.append(f"  问题描述: {desc}")
        parts.append(f"  退款金额: ¥{float(refund_amt):.2f}")
        if admin_remark:
            parts.append(f"  商家备注: {admin_remark}")
        parts.append(f"  申请时间: {create_time}")
        parts.append(f"  更新时间: {update_time}")
        parts.append(f"━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

        return "\n".join(parts)

    except Exception as e:
        print(e)
        return "查询售后进度时出错"
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()


@tool(description=(
    "根据订单号为用户发起售后申请。传入订单号、用户ID、售后原因和退款金额。"
    "只有状态为'待收货'或'已完成'且未在售后中的订单可以申请售后。"
    "售后原因包括：仅退款、退货退款、商品质量问题、不想要了等。"
))
def create_after_sale(order_no: str, user_id: str, reason: str, refund_amount: str) -> str:
    conn = get_conn()
    cursor = None
    try:
        cursor = conn.cursor()
        cursor.execute(
            "SELECT id, status, pay_amount, after_sale_status "
            "FROM orders WHERE order_no = %s AND user_id = %s",
            (order_no, user_id),
        )
        row = cursor.fetchone()
        if not row:
            return f"未找到订单 {order_no}"

        oid, status, pay_amount, as_status = row
        status_int = int(status)
        if status_int not in (2, 3):
            return "只有「待收货」或「已完成」的订单可以申请售后"
        if as_status == 1:
            return "该订单已有售后在处理中，请等待处理完成后再申请"

        refund_amt = float(refund_amount)
        if refund_amt <= 0 or refund_amt > float(pay_amount):
            return f"退款金额必须在 0 到实付金额 ¥{float(pay_amount):.2f} 之间"

        cursor.execute(
            "INSERT INTO after_sale (order_id, user_id, reason, refund_amount) "
            "VALUES (%s, %s, %s, %s)",
            (oid, user_id, reason, refund_amt),
        )
        conn.commit()

        return (
            f"✅ 售后申请已提交\n"
            f"  订单号: {order_no}\n"
            f"  原因: {reason}\n"
            f"  退款金额: ¥{refund_amt:.2f}\n"
            f"  请耐心等待商家处理，您可以随时询问售后进度"
        )

    except Exception as e:
        conn.rollback()
        print(e)
        return "申请售后时出错"
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()
