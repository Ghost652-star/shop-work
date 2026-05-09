"""向量存储服务 - 管理 Chroma 连接和文档加载"""

import os
from typing import Optional

from langchain_chroma import Chroma
from langchain_community.embeddings import ZhipuAIEmbeddings
from langchain_core.documents import Document


class VectorStoreService:
    """向量存储服务，单例模式"""

    _instance: Optional["VectorStoreService"] = None
    _store: Optional[Chroma] = None

    def __new__(cls, *args, **kwargs):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
        return cls._instance

    def __init__(
        self,
        embedding_model: str = "embedding-3",
        collection_name: str = "products",
        persist_dir: str = "chroma_data",
        csv_path: str = "data/product.csv",
    ):
        if self._store is not None:
            return

        self.embedding_model = embedding_model
        self.collection_name = collection_name
        self.persist_dir = persist_dir
        self.csv_path = csv_path

        self._init_store()

    def _init_store(self):
        """初始化向量存储"""
        agent_root = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
        persist_path = os.path.join(agent_root, self.persist_dir)

        embeddings = ZhipuAIEmbeddings(model=self.embedding_model)

        if os.path.exists(persist_path) and os.listdir(persist_path):
            print(f"[RAG] 加载已有向量库: {persist_path}")
            self._store = Chroma(
                collection_name=self.collection_name,
                embedding_function=embeddings,
                persist_directory=persist_path,
            )
        else:
            print(f"[RAG] 首次启动，从 CSV 初始化向量库")
            self._store = Chroma(
                collection_name=self.collection_name,
                embedding_function=embeddings,
                persist_directory=persist_path,
            )
            self._load_csv()

        count = self._store._collection.count()
        print(f"[RAG] 向量库文档总数: {count}")

    def _load_csv(self):
        """从 CSV 加载商品数据到向量库"""
        import csv

        agent_root = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
        csv_file = os.path.join(agent_root, self.csv_path)

        if not os.path.exists(csv_file):
            print(f"[RAG] CSV 文件不存在: {csv_file}，跳过初始化")
            return

        documents = []
        ids = []

        with open(csv_file, "r", encoding="utf-8") as f:
            reader = csv.DictReader(f)
            for row in reader:
                doc = Document(
                    page_content=f'{row["name"]}, {row["description"]}',
                    metadata={"product_id": row["id"], "product_name": row["name"]},
                )
                documents.append(doc)
                ids.append(f"product_{row['id']}")

        print(f"[RAG] 从 CSV 读取到 {len(documents)} 条商品")
        if documents:
            self._store.add_documents(documents=documents, ids=ids)
            print(f"[RAG] 已加载 {len(documents)} 条商品到向量库")

    def similarity_search(
        self,
        query: str,
        k: int = 5,
        score_threshold: float = 0.4,
    ) -> list[tuple[Document, float]]:
        """语义检索，返回文档和相关性分数"""
        if self._store is None:
            raise RuntimeError("向量存储未初始化")

        scored = self._store.similarity_search_with_relevance_scores(query, k=k)

        if score_threshold > 0:
            scored = [(doc, score) for doc, score in scored if score >= score_threshold]

        return scored

    def get_retriever(self, k: int = 5):
        """获取 LangChain 检索器"""
        if self._store is None:
            raise RuntimeError("向量存储未初始化")
        return self._store.as_retriever(search_kwargs={"k": k})
