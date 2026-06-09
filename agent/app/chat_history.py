"""聊天记录管理器 — 基于 Redis 的持久化存储"""

import json
from datetime import datetime
from typing import List, Optional

import redis

from config.settings import settings

# Redis 连接
redis_client = redis.Redis(
    host=settings.redis.host,
    port=settings.redis.port,
    db=settings.redis.db,
    password=settings.redis.password or None,
    decode_responses=True
)

MAX_MESSAGES = 100  # 每用户最多保留的消息条数
HISTORY_PREFIX = "chat:history:"


def get_history(user_id: str, limit: int = 30) -> List[dict]:
    """获取用户聊天历史，返回最近 limit 条消息"""
    key = f"{HISTORY_PREFIX}{user_id}"
    messages = redis_client.lrange(key, 0, -1)
    if messages:
        messages = [json.loads(m) for m in messages]
        # 返回最近 limit 条
        return messages[-limit:] if len(messages) > limit else messages
    return []


def add_message(user_id: str, role: str, content: str, image: str = None):
    """追加一条消息到历史记录"""
    key = f"{HISTORY_PREFIX}{user_id}"
    message = {
        "role": role,
        "content": content,
        "timestamp": datetime.now().isoformat()
    }
    if image:
        message["image"] = image
    redis_client.rpush(key, json.dumps(message, ensure_ascii=False))
    # 保持消息数量在限制内
    redis_client.ltrim(key, -MAX_MESSAGES, -1)


def clear_history(user_id: str):
    """清空用户聊天历史"""
    key = f"{HISTORY_PREFIX}{user_id}"
    redis_client.delete(key)
