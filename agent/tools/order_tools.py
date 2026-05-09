"""订单相关工具函数"""

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
