"""Agent 核心服务"""

from __future__ import annotations

import os
from typing import Any, Dict, Optional

from langchain.agents import create_agent
from langchain_openai import ChatOpenAI

from config.settings import settings
from app.callbacks import AgentDebugCallback
from tools import (
    get_orderStatus,
    get_userOrders,
    get_user_coupons,
    get_user_addresses,
    get_user_favorites,
    get_user_cart,
    get_product_detail,
    search_products,
)


def _load_system_prompt() -> str:
    """加载系统提示词"""
    prompt_path = os.path.join(
        os.path.dirname(os.path.dirname(__file__)),
        "prompts",
        "system_prompt.txt",
    )
    with open(prompt_path, "r", encoding="utf-8") as f:
        return f.read()


system_prompt = _load_system_prompt()


service_agent = create_agent(
    model=ChatOpenAI(
        model=settings.llm.model,
        api_key=settings.llm.api_key,
        base_url=settings.llm.base_url,
    ),
    tools=[
        get_orderStatus,
        get_userOrders,
        get_user_coupons,
        get_user_addresses,
        get_user_favorites,
        get_user_cart,
        search_products,
        get_product_detail,
    ],
    system_prompt=system_prompt,
    middleware=[],
)


def run_service_agent(message: str, user_id: Optional[str] = None, order_no: Optional[str] = None) -> str:
    """同步调用 agent，返回最终回复文本。"""
    content = message
    if user_id:
        content = f"user_id为{user_id},{content}"
    if order_no:
        content = f"order_no为{order_no},{content}"

    result: Dict[str, Any] = service_agent.invoke(
        {"messages": [{"role": "user", "content": content}]},
        config={"callbacks": [AgentDebugCallback()]},
    )
    messages = result.get("messages") or []
    if not messages:
        return ""
    return messages[-1].content


if __name__ == "__main__":
    result = run_service_agent("有没有好用的玻璃水推荐", user_id="1")
    print(result.encode("utf-8", errors="replace").decode("utf-8"))
