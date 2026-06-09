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
    get_order_detail,
    cancel_order,
    confirm_order,
    get_user_coupons,
    get_user_addresses,
    get_user_favorites,
    get_user_cart,
    get_product_detail,
    search_products,
    get_product_comments,
    get_after_sale_status,
    create_after_sale,
    add_to_cart,
    get_addresses_for_order,
    create_order,
    pay_order,
)
from tools.image_tools import analyze_product_image


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
        get_order_detail,
        cancel_order,
        confirm_order,
        get_user_coupons,
        get_user_addresses,
        get_user_favorites,
        get_user_cart,
        search_products,
        get_product_detail,
        get_product_comments,
        get_after_sale_status,
        create_after_sale,
        add_to_cart,
        analyze_product_image,
        get_addresses_for_order,
        create_order,
        pay_order,
    ],
    system_prompt=system_prompt,
    middleware=[],
)


def run_service_agent(
    message: str,
    user_id: Optional[str] = None,
    order_no: Optional[str] = None,
    chat_history: Optional[list] = None,
    image_base64: Optional[str] = None,
) -> str:
    """同步调用 agent，返回最终回复文本。"""
    content = message
    if user_id:
        content = f"user_id为{user_id},{content}"
    if order_no:
        content = f"order_no为{order_no},{content}"

    # 构建消息列表：历史对话 + 当前消息
    # LangChain只接受: human, user, ai, assistant, system, tool, function, developer
    ROLE_MAP = {"user": "human", "agent": "ai"}
    messages_list = []
    if chat_history:
        for m in chat_history:
            role = m.get("role", "user")
            msg_content = m.get("content", "")
            mapped_role = ROLE_MAP.get(role, role)
            if mapped_role in ("human", "ai", "user", "assistant", "system"):
                messages_list.append({"role": mapped_role, "content": msg_content})

    # 如果有图片，使用多模态消息格式
    if image_base64:
        # 确保 base64 数据有正确的前缀
        if not image_base64.startswith("data:"):
            image_base64 = f"data:image/jpeg;base64,{image_base64}"

        # 使用 LangChain 的多模态消息格式
        user_message = {
            "role": "user",
            "content": [
                {"type": "text", "text": content},
                {"type": "image_url", "image_url": {"url": image_base64}}
            ]
        }
        messages_list.append(user_message)
    else:
        messages_list.append({"role": "user", "content": content})

    result: Dict[str, Any] = service_agent.invoke(
        {"messages": messages_list},
        config={"callbacks": [AgentDebugCallback()]},
    )
    result_messages = result.get("messages") or []
    if not result_messages:
        return ""

    # 处理思维链模型（如mimo-v2.5）：内容可能在reasoning_content中
    last_msg = result_messages[-1]
    result_content = ""
    if hasattr(last_msg, 'reasoning_content') and last_msg.reasoning_content and not last_msg.content:
        # 如果content为空但reasoning_content有内容，使用reasoning_content
        result_content = last_msg.reasoning_content
    else:
        result_content = last_msg.content

    # 确保返回的内容是正确的 UTF-8 编码
    if result_content:
        try:
            result_content = result_content.encode('utf-8', errors='replace').decode('utf-8')
        except:
            result_content = str(result_content)
    return result_content


if __name__ == "__main__":
    result = run_service_agent("有没有好用的玻璃水推荐", user_id="1")
    print(result.encode("utf-8", errors="replace").decode("utf-8"))
