

from __future__ import annotations

from typing import Any, Dict, Optional

from langchain.agents import create_agent
from langchain_community.chat_models.tongyi import ChatTongyi

from app.config import system_prompt
from app.callbacks import AgentDebugCallback
from app.utils import (
    get_orderStatus,
    get_userOrders,
    get_user_coupons,
    get_user_addresses,
    get_user_favorites,
    get_user_cart,
    get_recommend_product,
    get_product_detail,
)


# 创建 agent（建议全局单例：避免每个请求都重新初始化）
service_agent = create_agent(
    model=ChatTongyi(model="MiniMax-M2.5"),
    tools=[
        get_orderStatus,
        get_userOrders,
        get_user_coupons,
        get_user_addresses,
        get_user_favorites,
        get_user_cart,
        get_recommend_product,
        get_product_detail
    ],
    system_prompt=system_prompt,
    middleware=[],
)


def run_service_agent(message: str, user_id: Optional[str] = None) -> str:
    """同步调用 agent，返回最终回复文本。
    """

    content = message
    if user_id:
        content = f"user_id为{user_id},{message}"

    result: Dict[str, Any] = service_agent.invoke(
        {"messages": [{"role": "user", "content": content}]},
        config={"callbacks": [AgentDebugCallback()]},
    )
    messages = result.get("messages") or []
    if not messages:
        return ""
    return messages[-1].content




