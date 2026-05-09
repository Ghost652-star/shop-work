"""RAG 检索服务 - 提供商品语义检索接口"""

from typing import Optional

from langchain_core.documents import Document

from rag.vector_store import VectorStoreService


class RagService:
    """RAG 服务，封装向量检索逻辑"""

    def __init__(
        self,
        top_k: int = 5,
        score_threshold: float = 0.4,
    ):
        self.top_k = top_k
        self.score_threshold = score_threshold
        self._vector_store = VectorStoreService()

    @property
    def vector_store(self) -> VectorStoreService:
        if self._vector_store is None:
            self._vector_store = VectorStoreService()
        return self._vector_store

    def search_products(
        self,
        query: str,
        top_k: Optional[int] = None,
        score_threshold: Optional[float] = None,
    ) -> list[tuple[Document, float]]:
        """语义检索商品

        Args:
            query: 用户查询描述
            top_k: 返回结果数量
            score_threshold: 相关性阈值

        Returns:
            文档和相关性分数列表
        """
        k = top_k or self.top_k
        threshold = score_threshold or self.score_threshold

        return self._vector_store.similarity_search(
            query=query,
            k=k,
            score_threshold=threshold,
        )

    def format_search_results(
        self,
        results: list[tuple[Document, float]],
        query: str,
    ) -> str:
        """格式化检索结果为文本"""
        if not results:
            return "未找到与您需求匹配的商品"

        parts = [f"根据「{query}」为您找到以下商品：", ""]

        for doc, score in results:
            pid = doc.metadata.get("product_id", "未知")
            name = doc.metadata.get("product_name", "未知商品")
            snippet = doc.page_content[:100] if doc.page_content else ""
            parts.append(f"- [ID:{pid}] {name}: {snippet}")

        parts.append("")
        parts.append("如需查看某商品的详细信息（价格、库存等），请调用 get_product_detail 并传入商品ID。")

        return "\n".join(parts)
