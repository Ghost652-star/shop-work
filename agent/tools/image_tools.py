"""图片识别工具 - 使用多模态 LLM 识别图片中的商品"""

from langchain_openai import ChatOpenAI
from langchain.messages import HumanMessage

from config.settings import settings


def analyze_product_image(image_base64: str) -> str:
    """
    使用多模态 LLM 识别图片中的商品
    返回: 商品名称（用于后续搜索）
    """
    try:
        llm = ChatOpenAI(
            model=settings.llm.model,
            api_key=settings.llm.api_key,
            base_url=settings.llm.base_url,
        )

        # 确保 base64 数据有正确的前缀
        if not image_base64.startswith("data:"):
            image_base64 = f"data:image/jpeg;base64,{image_base64}"

        # 构建多模态消息（使用 OpenAI 兼容格式）
        message = HumanMessage(content=[
            {
                "type": "text",
                "text": "请识别这张图片中的商品是什么。只返回商品名称，不要其他解释。如果是多个商品，返回最主要的一个。例如：汽车防冻玻璃水、蓝牙耳机、洗面奶。"
            },
            {
                "type": "image_url",
                "image_url": {"url": image_base64}
            }
        ])

        response = llm.invoke([message])

        # 处理思维链模型：内容可能在 reasoning_content 中
        result = response.content
        if not result and hasattr(response, 'reasoning_content') and response.reasoning_content:
            result = response.reasoning_content

        return result.strip() if result else ""

    except Exception as e:
        print(f"图片识别失败: {e}")
        return ""
