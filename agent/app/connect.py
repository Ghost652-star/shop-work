from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse, Response
from pydantic import BaseModel
from typing import Optional
import traceback
import sys
import json
import io
import locale

from app.serviceClient import run_service_agent
from app.chat_history import get_history, add_message, clear_history

# 修复Windows终端编码问题
if sys.platform == 'win32':
    # 设置默认编码为 UTF-8
    if hasattr(sys.stdout, 'reconfigure'):
        sys.stdout.reconfigure(encoding='utf-8', errors='replace')
    if hasattr(sys.stderr, 'reconfigure'):
        sys.stderr.reconfigure(encoding='utf-8', errors='replace')

# 创建 FastAPI 应用实例
app = FastAPI(title="ShopChat API", description="简单的请求处理API")

# 定义请求数据模型
class RequestData(BaseModel):
    message: str
    user_id: Optional[str] = None
    order_no: Optional[str] = None
    image: Optional[str] = None  # base64 编码的图片数据


# POST 请求处理端点
@app.post("/process")
async def process_request(request: Request):
    try:
        body = await request.json()
        data = RequestData(**body)

        # 保存用户消息（包含图片）
        if data.user_id:
            add_message(data.user_id, "user", data.message, image=data.image)

        # 构建上下文：最近历史 + 当前消息
        history_context = []
        if data.user_id:
            history = get_history(data.user_id, limit=20)
            # 只取历史中的对话（不含最新一条用户消息）
            history_context = history[:-1] if len(history) > 1 else []

        # 组装最终消息
        final_message = data.message
        image_base64 = None
        if data.image:
            # 如果有图片，提取 base64 数据（去掉可能的 data:image/xxx;base64, 前缀）
            image_base64 = data.image
            if "base64," in image_base64:
                image_base64 = image_base64.split("base64,")[1]
            # 在消息中提示用户发送了图片，但不嵌入 base64 数据
            final_message = f"{final_message} [用户发送了一张图片，请调用 analyze_product_image 工具识别图片中的商品。]"

        # 把 FastAPI 收到的输入交给你的 service agent 处理
        processed_msg = run_service_agent(
            message=final_message,
            user_id=data.user_id,
            order_no=data.order_no,
            chat_history=history_context,
            image_base64=image_base64,
        )

        # 保存AI回复
        if data.user_id:
            add_message(data.user_id, "agent", processed_msg)

        # 返回处理结果，确保UTF-8编码
        result = {
            "status": "success",
            "processed_message": processed_msg,
            "original_data": {
                "message": data.message,
                "user_id": data.user_id,
                "order_no": data.order_no,
                "has_image": data.image is not None
            }
        }
        # 使用 ensure_ascii=False 确保中文正确显示
        json_str = json.dumps(result, ensure_ascii=False)
        # 直接返回字节流
        return Response(
            content=json_str.encode('utf-8'),
            media_type="application/json; charset=utf-8"
        )
    except Exception as e:
        import traceback
        traceback.print_exc()
        # 确保错误信息正确编码
        error_msg = str(e)
        # 尝试修复编码问题
        try:
            error_msg = error_msg.encode('utf-8', errors='replace').decode('utf-8')
        except:
            error_msg = repr(error_msg)
        return JSONResponse(
            content={
                "status": "error",
                "processed_message": f"处理出错: {error_msg}",
                "original_data": {}
            },
            media_type="application/json; charset=utf-8"
        )


@app.get("/history/{user_id}")
async def get_chat_history(user_id: str):
    """获取聊天历史记录"""
    try:
        history = get_history(user_id, limit=30)
        return JSONResponse(
            content={"status": "success", "data": history},
            media_type="application/json; charset=utf-8"
        )
    except Exception as e:
        return JSONResponse(
            content={"status": "error", "data": [], "message": str(e)},
            media_type="application/json; charset=utf-8"
        )


@app.delete("/history/{user_id}")
async def delete_chat_history(user_id: str):
    """清空聊天历史记录"""
    try:
        clear_history(user_id)
        return JSONResponse(
            content={"status": "success", "message": "已清空聊天记录"},
            media_type="application/json; charset=utf-8"
        )
    except Exception as e:
        return JSONResponse(
            content={"status": "error", "message": str(e)},
            media_type="application/json; charset=utf-8"
        )


if __name__ == "__main__":
    import uvicorn
    # 启动服务器，端口8000，支持热重载
    uvicorn.run(app, host="127.0.0.1", port=8000)
