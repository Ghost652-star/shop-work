"""配置管理"""

import os
from dataclasses import dataclass, field

from dotenv import load_dotenv

load_dotenv(os.path.join(os.path.dirname(os.path.dirname(__file__)), ".env"))


@dataclass
class LLMConfig:
    """LLM 配置"""
    model: str = "mimo-v2.5"
    api_key: str = field(default_factory=lambda: os.getenv("LLM_API_KEY", ""))
    base_url: str = field(default_factory=lambda: os.getenv("LLM_BASE_URL", "https://token-plan-cn.xiaomimimo.com/v1"))


@dataclass
class EmbeddingConfig:
    """Embedding 配置"""
    model: str = "embedding-3"
    collection_name: str = "products"
    persist_dir: str = "chroma_data"
    csv_path: str = "data/product.csv"
    top_k: int = 5
    score_threshold: float = 0.4


@dataclass
class DatabaseConfig:
    """数据库配置"""
    host: str = field(default_factory=lambda: os.getenv("DB_HOST", "localhost"))
    user: str = field(default_factory=lambda: os.getenv("DB_USER", "root"))
    password: str = field(default_factory=lambda: os.getenv("DB_PASSWORD", ""))
    database: str = field(default_factory=lambda: os.getenv("DB_NAME", "db_aps"))
    pool_size: int = 4


@dataclass
class Settings:
    """全局配置"""
    llm: LLMConfig = field(default_factory=LLMConfig)
    embedding: EmbeddingConfig = field(default_factory=EmbeddingConfig)
    database: DatabaseConfig = field(default_factory=DatabaseConfig)

    @classmethod
    def get_instance(cls) -> "Settings":
        if not hasattr(cls, "_instance"):
            cls._instance = cls()
        return cls._instance


settings = Settings.get_instance()
